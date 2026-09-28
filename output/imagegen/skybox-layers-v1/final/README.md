# Grayscale skybox layers

Two cubemaps, each containing six 512 x 512 RGBA PNG faces:

- `stars/`: star layer with transparent background.
- `nebula/`: foreground gas layer, alpha no greater than 191/255 (74.90%).

All RGB channels are equal, allowing tinting in an editor or renderer. Existing
project textures have NOT been overwritten. `preview.png` shows each layer on
black and the nebula composited over the stars. It is a preview, not a texture.

## Orientation and edges

Files are prepared for the current Biomorphosis `CubemapTexture.orientedPixels`
loader. The four lateral PNG faces are pre-mirrored to cancel that loader's
horizontal reflection. Top and bottom are unchanged. For a standard OpenGL
loader without that reflection, mirror each positive/negative X/Z PNG
horizontally once before use.

The generated artwork was not inherently seamless. Adjacent faces were blended
in direction space near their shared edges; outer RGBA texels were matched
exactly, including all eight three-face corners. Validation reloads the saved
PNGs and checks all twelve edges in GPU orientation, grayscale RGB and alpha.
This checks texture data, not an in-game GPU rendering session.

## Render order

Draw the stars first, then the nebula with normal SRC_ALPHA /
ONE_MINUS_SRC_ALPHA blending. This keeps at least 25% of the background stars
visible even through the densest nebula pixels. Do not draw the nebula twice:
multiple passes accumulate opacity.

The current `WastesSkybox.renderStars` draws NEBULA before STARS using an
additive star pass. To obtain the requested foreground effect, move the NEBULA
draw after the STARS draw and after `RenderSystem.defaultBlendFunc()`.
Use an RGB multiplier of (1, 1, 1) to see the uncolored assets; the current nebula
multiplier (0, 0.879, 0.823) adds a cyan tint. No renderer changes are included.

## Reproduction

Art generated with the built-in imagegen tool. Prompts and untouched drafts
are in the parent directory. User-authorized technical processing is in
`../prepare_cubemaps.py` (Pillow and NumPy). Run it to rebuild these files and
the ZIP. `validation.json` contains measured per-face results.
