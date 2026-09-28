"""Turn the imagegen drafts into two edge-matched RGBA cubemaps.

Requires Pillow and NumPy. Source art is retained; this script performs only
technical grayscale, opacity, edge blending and packing operations.
"""

from collections import defaultdict
import json
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED

import numpy as np
from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parent
OUT = ROOT / "final"
SIZE = 512
FACES = ("positive_x", "negative_x", "positive_y", "negative_y", "positive_z", "negative_z")
LATERAL = (0, 1, 4, 5)
ALPHA_CAP = 191  # 191/255 = 74.90%; 192/255 would exceed 75%.


def directions(face, u, v):
    one = np.ones_like(u)
    return np.stack((
        (one, -v, -u), (-one, -v, u), (u, one, v),
        (u, -one, -v), (u, -v, one), (-u, -v, -one),
    )[face], axis=-1)


def face_uv(face, direction):
    x, y, z = np.moveaxis(direction, -1, 0)
    major = np.maximum(np.abs(direction[..., face // 2]), 1e-8)
    sc, tc = ((-z, -y), (z, -y), (x, z), (x, -z), (x, -y), (-x, -y))[face]
    return sc / major, tc / major


def bilinear(pixels, u, v):
    # Reflect a small outside-face footprint to avoid stretching the edge texels.
    u = 1.0 - np.abs(u - 1.0)
    u = -1.0 + np.abs(u + 1.0)
    v = 1.0 - np.abs(v - 1.0)
    v = -1.0 + np.abs(v + 1.0)
    x = np.clip((u + 1.0) * 0.5 * SIZE - 0.5, 0, SIZE - 1)
    y = np.clip((v + 1.0) * 0.5 * SIZE - 0.5, 0, SIZE - 1)
    x0, y0 = np.floor(x).astype(int), np.floor(y).astype(int)
    x1, y1 = np.minimum(x0 + 1, SIZE - 1), np.minimum(y0 + 1, SIZE - 1)
    fx, fy = (x - x0)[..., None], (y - y0)[..., None]
    return ((pixels[y0, x0] * (1 - fx) + pixels[y0, x1] * fx) * (1 - fy)
            + (pixels[y1, x0] * (1 - fx) + pixels[y1, x1] * fx) * fy)


def load_draft(layer):
    image = Image.open(ROOT / f"{layer}-atlas-draft.png").convert("RGBA")
    if image.size != (SIZE * 3, SIZE * 2):
        raise ValueError(f"Unexpected atlas dimensions: {image.size}")
    array = np.asarray(image, dtype=np.float64) / 255.0
    gray = array[..., :3].mean(axis=-1)
    alpha = array[..., 3]
    if layer == "nebula":
        alpha = alpha * (ALPHA_CAP / 255.0)
    # Work in premultiplied form so transparent pixels cannot create halos.
    rgba = np.stack((gray * alpha, gray * alpha, gray * alpha, alpha), axis=-1)
    return [rgba[(i // 3) * SIZE:(i // 3 + 1) * SIZE,
                 (i % 3) * SIZE:(i % 3 + 1) * SIZE].copy() for i in range(6)]


def sample_continuous(source, direction, width):
    """Blend adjacent face art as a continuous function of sky direction."""
    major = np.max(np.abs(direction), axis=-1)
    result = np.zeros((*major.shape, 4), dtype=np.float64)
    total = np.zeros_like(major)
    for face in range(6):
        component = direction[..., face // 2] * (1 if face % 2 == 0 else -1)
        t = np.clip((component / major - (1.0 - width)) / width, 0, 1)
        weight = t * t * (3 - 2 * t)
        mask = weight > 0
        if not np.any(mask):
            continue
        u, v = face_uv(face, direction[mask])
        result[mask] += bilinear(source[face], u, v) * weight[mask, None]
        total += weight
    return result / total[..., None]


def edge_groups():
    groups = defaultdict(list)
    grid = np.arange(SIZE) * 2 - (SIZE - 1)
    for face in range(6):
        for y in range(SIZE):
            xs = range(SIZE) if y in (0, SIZE - 1) else (0, SIZE - 1)
            for x in xs:
                vector = directions(face, np.array(grid[x]), np.array(grid[y]))
                vector[face // 2] *= SIZE - 1
                groups[tuple(vector.tolist())].append((face, y, x))
    assert len(groups) == 12 * (SIZE - 2) + 8
    assert all(len(group) in (2, 3) for group in groups.values())
    assert sum(len(group) == 3 for group in groups.values()) == 8
    return list(groups.values())


def unpremultiply(pixels):
    alpha = pixels[..., 3:4]
    rgb = np.divide(pixels[..., :3], alpha, out=np.zeros_like(pixels[..., :3]), where=alpha > 1e-8)
    return np.clip(np.rint(np.concatenate((rgb, alpha), axis=-1) * 255), 0, 255).astype(np.uint8)


def export_layer(layer, groups):
    source = load_draft(layer)
    center = (np.arange(SIZE) + 0.5) * (2.0 / SIZE) - 1.0
    u, v = np.meshgrid(center, center)
    width = 0.10 if layer == "stars" else 0.20
    faces = [sample_continuous(source, directions(face, u, v), width) for face in range(6)]
    # Make corresponding outer texels exactly equal, including three-face corners.
    for group in groups:
        value = np.mean([faces[f][y, x] for f, y, x in group], axis=0)
        for f, y, x in group:
            faces[f][y, x] = value
    pixels = [unpremultiply(face) for face in faces]
    if layer == "nebula":
        for face in pixels:
            face[..., 3] = np.minimum(face[..., 3], ALPHA_CAP)
    folder = OUT / layer
    folder.mkdir(parents=True, exist_ok=True)
    for i, name in enumerate(FACES):
        # CubemapTexture.orientedPixels mirrors these faces at upload time.
        # Compensate here so the GPU receives the canonical OpenGL orientation.
        packed = pixels[i][:, ::-1] if i in LATERAL else pixels[i]
        Image.fromarray(packed).save(folder / f"{name}.png")
    return pixels


def reload_and_validate(layer, groups):
    faces = []
    stats = {}
    for i, name in enumerate(FACES):
        image = Image.open(OUT / layer / f"{name}.png")
        assert image.mode == "RGBA" and image.size == (SIZE, SIZE)
        pixels = np.asarray(image)
        assert np.array_equal(pixels[..., 0], pixels[..., 1])
        assert np.array_equal(pixels[..., 1], pixels[..., 2])
        if layer == "nebula":
            assert int(pixels[..., 3].max()) <= ALPHA_CAP
        faces.append(pixels[:, ::-1] if i in LATERAL else pixels)
        stats[name] = {"size": list(image.size), "alpha_min": int(pixels[..., 3].min()),
                       "alpha_max": int(pixels[..., 3].max()), "grayscale": True}
    for group in groups:
        first = faces[group[0][0]][group[0][1], group[0][2]]
        assert all(np.array_equal(first, faces[f][y, x]) for f, y, x in group[1:])
    return faces, {"faces": stats, "matched_edge_and_corner_groups": len(groups),
                   "edge_max_rgba_difference": 0, "corner_count": 8}


def sample_cube(faces, direction):
    axis = np.argmax(np.abs(direction), axis=-1)
    component = np.take_along_axis(direction, axis[..., None], axis=-1)[..., 0]
    indices = axis * 2 + (component < 0).astype(int)
    result = np.empty((*axis.shape, 4), dtype=float)
    for face in range(6):
        mask = indices == face
        u, v = face_uv(face, direction[mask])
        pixels = faces[face].astype(float) / 255.0
        pixels[..., :3] *= pixels[..., 3:4]
        result[mask] = bilinear(pixels, u, v)
    return result


def make_preview(stars, nebula):
    width, height = 1536, 512
    yaw = (np.arange(width) + 0.5) * (2 * np.pi / width) - np.pi
    pitch = (0.5 - (np.arange(height) + 0.5) / height) * np.pi
    longitude, latitude = np.meshgrid(yaw, pitch)
    d = np.stack((np.sin(longitude) * np.cos(latitude), np.sin(latitude),
                  np.cos(longitude) * np.cos(latitude)), axis=-1)
    s, n = sample_cube(stars, d), sample_cube(nebula, d)
    combined = n[..., :3] + s[..., :3] * (1 - n[..., 3:4])
    preview = Image.new("RGB", (width, 3 * (height + 36)), "black")
    draw = ImageDraw.Draw(preview)
    for i, (label, rgb) in enumerate((("STARS / on black", s[..., :3]),
                                    ("NEBULA / on black / alpha <= 191 of 255", n[..., :3]),
                                    ("COMPOSITE / nebula in front of stars", combined))):
        y = i * (height + 36)
        draw.text((16, y + 10), label, fill="white")
        preview.paste(Image.fromarray(np.clip(np.rint(rgb * 255), 0, 255).astype(np.uint8)), (0, y + 36))
    preview.save(OUT / "preview.png")


def main():
    groups = edge_groups()
    for layer in ("stars", "nebula"):
        export_layer(layer, groups)
    stars, stars_report = reload_and_validate("stars", groups)
    nebula, nebula_report = reload_and_validate("nebula", groups)
    report = {"format": "RGBA PNG", "face_size": SIZE,
              "orientation": "OpenGL after existing CubemapTexture.orientedPixels upload",
              "nebula_alpha_limit": ALPHA_CAP, "stars": stars_report, "nebula": nebula_report}
    (OUT / "validation.json").write_text(json.dumps(report, indent=2) + "\n")
    make_preview(stars, nebula)
    with ZipFile(ROOT / "skybox-layers-v1.zip", "w", ZIP_DEFLATED) as bundle:
        for path in sorted(OUT.rglob("*")):
            if path.is_file():
                bundle.write(path, path.relative_to(OUT))
    print(json.dumps({"output": str(OUT), "faces": 12,
                      "nebula_alpha_max": max(v["alpha_max"] for v in nebula_report["faces"].values()),
                      "matched_edge_and_corner_groups_per_layer": len(groups),
                      "archive": str(ROOT / "skybox-layers-v1.zip")}, indent=2))


if __name__ == "__main__":
    main()
