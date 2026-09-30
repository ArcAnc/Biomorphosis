# Softer sun, version 2

Edited from the current project sun using the built-in imagegen tool. ImageMagick resampled the generated RGBA sprite to the existing 128 x 128 texture dimensions. The previous texture is retained in `sun-before.png`, and the full-size result in `sun-generated.png`.

Installed asset: `src/main/resources/assets/biomorphosis/textures/environment/skybox/wastes/sun.png`.

## Prompt

Edit this existing sun sprite for a Minecraft skybox. Keep it a centered circular sun with an almost-white ivory center and a smooth radial gradient. Requested changes: substantially soften the currently hard outer edge; remove the obvious concentric orange ring and crisp outlines; lower yellow-orange saturation moderately toward pale warm cream, champagne and muted golden yellow, keeping the core almost white. Add a small number of short irregular subtle rays on the perimeter, translucent, diffuse and softly tapered rather than sharp spikes; a slight pixelated breakup is acceptable only at the very outer edge to fit a 128x128 game texture. Maintain a clean simple readable sun disk, no gritty interior detail. Preserve the apparent main disk diameter (about 70% of canvas width); let a broad delicate feathered halo and short rays extend to about 92% of canvas width, fading fully to alpha zero before ALL canvas edges. There must be a transparent margin on all four sides, no cropped rays or clipped halo, no rectangular backing. True RGBA transparency outside and through the glow; near-opaque white center. IMPORTANT do not simulate transparency with a black, white, gray or checkerboard background. Output square PNG, preferably 128x128. No lens flares, no giant starburst, no long spikes, no planets, text or other objects. This is a subtle texture refinement, not a new scene.
