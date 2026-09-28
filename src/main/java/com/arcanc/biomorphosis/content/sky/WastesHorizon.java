/**
 * @author ArcAnc
 * Created at: 28.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper.ColorHelper;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

final class WastesHorizon implements BiomeSkyboxEffect
{
	private static final float RADIUS = 80.0F;
	private static final int SEGMENTS = 128;
	private static final float TWILIGHT_SUN_HEIGHT = 0.35F;
	private static final int TWILIGHT_COLOR = ColorHelper.color(242, 159, 106);
	private static final float[] ELEVATIONS = {-0.12F, -0.08F, -0.04F, -0.02F, 0.0F, 0.015F, 0.03F, 0.06F, 0.10F, 0.16F, 0.24F};
	private static final float[] OPACITIES = {0.0F, 0.12F, 0.30F, 0.60F, 1.0F, 0.92F, 0.78F, 0.58F, 0.32F, 0.10F, 0.0F};
	private static final RenderType RENDER_TYPE = RenderType.create(
			Database.rlStr("wastes_horizon"),
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			false,
			RenderType.CompositeState.builder().
					setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer :: getPositionColorShader)).
					setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).
					setCullState(RenderStateShard.NO_CULL).
					setDepthTestState(RenderStateShard.NO_DEPTH_TEST).
					setWriteMaskState(RenderStateShard.COLOR_WRITE).
					createCompositeState(false));

	@Override
	public boolean isForeground()
	{
		return true;
	}

	@Override
	public void render(BiomeSkyboxRenderContext context)
	{
		if (context.alpha() <= 0.001F)
			return;

		float angle = BiomeSkyboxes.sphereTravelAngle(context.level(), context.partialTick());
		float sunHeight = Mth.cos(angle);
		float daylight = Mth.clamp((sunHeight + 0.15F) / 0.45F, 0.0F, 1.0F);
		daylight = daylight * daylight * (3.0F - 2.0F * daylight);
		float twilight = Mth.clamp(1.0F - Math.abs(sunHeight) / TWILIGHT_SUN_HEIGHT, 0.0F, 1.0F);
		twilight *= twilight;
		float sunX = -Mth.sin(angle);
		int dayColor = ColorHelper.lerp(0.65F, context.skybox().noonFogColor(), context.skybox().noonColor());
		int nightColor = ColorHelper.lerp(0.45F, context.skybox().midnightFogColor(), context.skybox().midnightColor());
		int baseColor = ColorHelper.lerp(daylight, nightColor, dayColor);
		float baseAlpha = context.alpha() * Mth.lerp(daylight, 0.38F, 0.72F);

		Matrix4f matrix = new Matrix4f(context.poseStack().last().pose());
		matrix.m30(0.0F).m31(0.0F).m32(0.0F);
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		for (int segment = 0; segment < SEGMENTS; segment++)
		{
			float yaw0 = segment * Mth.TWO_PI / SEGMENTS;
			float yaw1 = (segment + 1) % SEGMENTS * Mth.TWO_PI / SEGMENTS;
			int color0 = horizonColor(baseColor, twilight, sunX, yaw0);
			int color1 = horizonColor(baseColor, twilight, sunX, yaw1);
			for (int ring = 0; ring < ELEVATIONS.length - 1; ring++)
			{
				addVertex(buffer, matrix, yaw0, ring, color0, baseAlpha);
				addVertex(buffer, matrix, yaw1, ring, color1, baseAlpha);
				addVertex(buffer, matrix, yaw1, ring + 1, color1, baseAlpha);
				addVertex(buffer, matrix, yaw0, ring + 1, color0, baseAlpha);
			}
		}
		RENDER_TYPE.draw(buffer.buildOrThrow());
	}

	private static int horizonColor(int baseColor, float twilight, float sunX, float yaw)
	{
		float facingSun = Mth.clamp((Mth.cos(yaw) * sunX + 1.0F) * 0.5F, 0.0F, 1.0F);
		float warmWeight = twilight * (0.12F + 0.88F * facingSun * facingSun * facingSun);
		return ColorHelper.lerp(warmWeight, baseColor, TWILIGHT_COLOR);
	}

	private static void addVertex(BufferBuilder buffer, Matrix4f matrix, float yaw, int ring, int color, float alpha)
	{
		float horizontal = Mth.cos(ELEVATIONS[ring]) * RADIUS;
		buffer.addVertex(matrix, Mth.cos(yaw) * horizontal, Mth.sin(ELEVATIONS[ring]) * RADIUS, Mth.sin(yaw) * horizontal).
				setColor(ColorHelper.red(color), ColorHelper.green(color), ColorHelper.blue(color),
						BiomeSkyboxes.alphaToInt(alpha * OPACITIES[ring]));
	}
}
