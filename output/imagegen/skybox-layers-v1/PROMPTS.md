# Skybox layers — imagegen drafts

Generated using the built-in imagegen tool. These files are drafts, not validated seamless cubemaps. Existing game assets have not been replaced.

Both images are 1536 x 1024 RGBA. Intended atlas order: top +X, -X, +Y; bottom -Y, +Z, -Z. Intended cell size: 512 x 512.

The nebula generator did not honor the maximum alpha requirement. First output max alpha: 253/255. Edited output max alpha: 252/255. Do not treat these drafts as satisfying the 75% opacity limit.

## Stars prompt

Use case: stylized-concept. Asset type: production game skybox cubemap texture atlas, STARS ONLY layer. Generate one landscape 1536x1024 PNG with six exactly square 512x512 cubemap faces packed edge-to-edge in a 3-column by 2-row atlas, no gutters, no frames, no labels. Top row left to right: positive X, negative X, positive Y. Bottom row left to right: negative Y, positive Z, negative Z. OpenGL cubemap face orientation, viewed from cube center, 90-degree square views of a single continuous star sphere; match shared cube edges with appropriate orientation. Content: a beautiful deep starfield with thousands of tiny crisp white and pale gray stars, varied sizes and brightness, a few subtle compact glows, irregular natural distributions and sparse clusters, most of the area empty. Only stars, no diffuse nebula, no cloudy haze, no planets, no sun, no moon, no galaxies, no text. Strict neutral grayscale, RGB channels equal, intended for later color tinting. Genuine transparent background between stars, smooth alpha on star glows, no black backing and absolutely no checkerboard painted in the pixels. All six panels must contain starfield, including top and bottom directions. This is a flat texture atlas, not a picture of a cube, not a perspective mockup.

## Nebula prompt

Use case: stylized-concept. Asset type: production game skybox cubemap texture atlas, separate foreground NEBULA ONLY layer. Generate one landscape 1536x1024 RGBA PNG with six exactly square 512x512 cubemap faces packed edge-to-edge in a 3-column by 2-row atlas, no gutters, no frames, no labels. Top row left to right: positive X, negative X, positive Y. Bottom row left to right: negative Y, positive Z, negative Z. OpenGL cubemap face orientation, viewed from cube center, 90-degree square views of a single continuous environment; match shared cube edges with appropriate orientation. Subject: ethereal interstellar nebula, elongated branching wisps of translucent cosmic gas, intricate delicate billowing filaments and layered soft gray dust clouds, irregular large open voids, compelling asymmetric structure. Strict neutral black-white-gray palette with RGB channels equal for later tinting. NO stars or bright pinpoint particles at all: this is a separate nebula layer that will overlay a different starfield texture. Genuine transparent background. CRITICAL: ALL nebula pixels must be semitransparent, maximum opacity 75 percent (8-bit alpha at most 191), typically 10 to 55 percent; bright filament cores also alpha at most 191. Empty regions alpha 0. Preserve luminous gray-white RGB separately from alpha; do not simulate transparency using a baked black or checkerboard background. No opaque areas, planets, moons, horizon, lettering or watermark. All six panels populated with different coherent nebula views, not six copies. Flat texture atlas only, not a cube mockup.

## Nebula edit

Requested preservation of 1536 x 1024 dimensions and grayscale cloud design, removal of grid lines, and multiplication of all alpha values by 0.70 with maximum alpha 191/255. The result removed visible divider lines but did not satisfy the alpha limit.
