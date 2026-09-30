/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.arcanc.biomorphosis.content.worldgen.BioBiomes;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class WastesSkybox
{
	private static final float CLOUD_DISTANCE = 86.0F;
	private static final float CLOUD_Y_OFFSET = -8.0F;
	private static final float CLOUD_MIN_PITCH = 0.14F;
	private static final float CLOUD_MAX_PITCH = 0.78F;
	private static final float CLOUD_MIN_YAW_SPEED = 1.75F;
	private static final float CLOUD_MAX_YAW_SPEED = 2.65F;
	private static final float CLOUD_FADE_SUN_HEIGHT = 0.1F;
	private static final float CLOUD_MIN_PITCH_SPEED = 1.85F;
	private static final float CLOUD_MAX_PITCH_SPEED = 2.9F;
	private static final int CLOUD_COUNT = 50;
	private static final int CLOUD_MIN_LOBES = 10;
	private static final int CLOUD_MAX_LOBES = 16;
	private static final int CLOUD_SEGMENTS = 16;
	private static final CubemapDefinition STARS = CubemapDefinition.fromDirectory(Database.rl("environment/skybox/wastes/stars"));
	private static final CubemapDefinition NEBULA = CubemapDefinition.fromDirectory(Database.rl("environment/skybox/wastes/nebula"));
	private static final RenderStateShard.ShaderStateShard POSITION_COLOR_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer :: getPositionColorShader);
	
	private static final RenderType CLOUDS_RENDER_TYPE = RenderType.create(
			Database.rlStr("wastes_clouds"),
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.TRIANGLES,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			true,
			RenderType.CompositeState.builder().
					setShaderState(POSITION_COLOR_SHADER).
					setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).
					setCullState(RenderStateShard.NO_CULL).
					setDepthTestState(RenderStateShard.NO_DEPTH_TEST).
					setWriteMaskState(RenderStateShard.COLOR_WRITE).
					createCompositeState(false));
	private static final BiomeSkyboxEffect STARS_EFFECT = WastesSkybox :: renderStars;
	private static final BiomeSkyboxEffect CLOUDS_EFFECT = new BiomeSkyboxEffect()
	{
		@Override
		public void render(BiomeSkyboxRenderContext context)
		{
			renderClouds(context);
		}
		
		@Override
		public boolean isForeground()
		{
			return true;
		}
	};
	private static final List<WastesCloud> CLOUDS = createClouds();
	
	private WastesSkybox()
	{
	}
	
	static void register()
	{
		BiomeSkyboxes.registerCubemap(NEBULA);
		BiomeSkyboxes.registerCubemap(STARS);
		BiomeSkyboxes.register(BioBiomes.WASTES, BiomeSkybox.builder(Database.rl("environment/skybox/wastes")).
				noonColor(207, 230, 202).
				midnightColor(54, 48, 92).
				noonFogColor(85, 110, 94).
				midnightFogColor(24, 18, 47).
				effect(STARS_EFFECT).
				effect(new WastesHorizon()).
				effect(CLOUDS_EFFECT).
				build());
	}
	
	private static void renderStars(BiomeSkyboxRenderContext context)
	{
		float nightAlpha = Mth.clamp(context.level().getStarBrightness(context.partialTick()) * 1.35F, 0.0F, 1.0F);
		if (nightAlpha <= 0.001F)
			return;
		
		float alpha = context.alpha() * nightAlpha;
		float rotation = BiomeSkyboxes.sphereTravelAngle(context.level(), context.partialTick());
		CubemapSkyboxRenderer.render(NEBULA, alpha, rotation, 0.0F, 0.879F, 0.823F, context.poseStack());
		RenderSystem.blendFunc(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
				com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
		CubemapSkyboxRenderer.render(STARS, alpha, rotation, 1.0F, 1.0F, 1.0F, context.poseStack());
		RenderSystem.defaultBlendFunc();
	}
	
	private static void renderClouds(BiomeSkyboxRenderContext context)
	{
		float sunHeight = Mth.cos(context.level().getSunAngle(context.partialTick()));
		float daylight = Mth.clamp((sunHeight + CLOUD_FADE_SUN_HEIGHT) / (2.0F * CLOUD_FADE_SUN_HEIGHT), 0.0F, 1.0F);
		float alpha = context.alpha() * daylight * daylight * (3.0F - 2.0F * daylight);
		if (alpha <= 0.001F || CLOUDS.isEmpty())
			return;
		
		Matrix4f matrix = context.poseStack().last().pose();
		float gameDays = (context.level().getGameTime() + context.partialTick()) / 24000.0F;
		
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		for (WastesCloud cloud : CLOUDS)
			addCloud(buffer, matrix, cloud, gameDays, alpha);
		CLOUDS_RENDER_TYPE.draw(buffer.buildOrThrow());
	}
	
	private static List<WastesCloud> createClouds()
	{
		Random random = new Random(System.nanoTime());
		List<WastesCloud> clouds = new ArrayList<>();
		for (int cloudIndex = 0; cloudIndex < CLOUD_COUNT; cloudIndex++)
		{
			float width = randomFloat(random, 18.0F, 34.0F);
			float height = randomFloat(random, 5.5F, 10.0F);
			int lobeCount = CLOUD_MIN_LOBES + random.nextInt(CLOUD_MAX_LOBES - CLOUD_MIN_LOBES + 1);
			List<CloudLobe> lobes = new ArrayList<>();
			for (int lobeIndex = 0; lobeIndex < lobeCount; lobeIndex++)
				lobes.add(createCloudLobe(random, width, height));
			
			clouds.add(new WastesCloud(
					randomFloat(random, 0.0F, Mth.TWO_PI),
					randomFloat(random, CLOUD_MIN_PITCH, CLOUD_MAX_PITCH),
					randomSign(random) * randomFloat(random, CLOUD_MIN_YAW_SPEED, CLOUD_MAX_YAW_SPEED),
					randomFloat(random, 0.03F, 0.11F),
					randomFloat(random, CLOUD_MIN_PITCH_SPEED, CLOUD_MAX_PITCH_SPEED),
					randomFloat(random, 0.0F, Mth.TWO_PI),
					randomFloat(random, -24.0F, 24.0F),
					randomFloat(random, -5.0F, 3.0F),
					randomFloat(random, 0.35F, 0.58F),
					List.copyOf(lobes)));
		}
		return List.copyOf(clouds);
	}
	
	private static CloudLobe createCloudLobe(Random random, float cloudWidth, float cloudHeight)
	{
		float[] edgeScale = new float[CLOUD_SEGMENTS];
		for (int index = 0; index < edgeScale.length; index++)
			edgeScale[index] = randomFloat(random, 0.72F, 1.18F);
		
		return new CloudLobe(
				randomFloat(random, -cloudWidth * 0.48F, cloudWidth * 0.48F),
				randomFloat(random, -cloudHeight * 0.42F, cloudHeight * 0.42F),
				randomFloat(random, cloudWidth * 0.16F, cloudWidth * 0.34F),
				randomFloat(random, cloudHeight * 0.28F, cloudHeight * 0.58F),
				randomFloat(random, -0.55F, 0.55F),
				randomFloat(random, 0.6F, 1.0F),
				edgeScale);
	}
	
	private static void addCloud(BufferBuilder buffer,
	                             Matrix4f matrix,
	                             WastesCloud cloud,
	                             float gameDays,
	                             float alpha)
	{
		CloudBasis basis = cloudBasis(cloud.yaw() + gameDays * cloud.yawSpeed(), cloudPitch(cloud, gameDays));
		float centerX = basis.dirX() * CLOUD_DISTANCE + basis.rightX() * cloud.rightOffset() + basis.upX() * cloud.upOffset();
		float centerY = basis.dirY() * CLOUD_DISTANCE + CLOUD_Y_OFFSET + basis.rightY() * cloud.rightOffset() + basis.upY() * cloud.upOffset();
		float centerZ = basis.dirZ() * CLOUD_DISTANCE + basis.rightZ() * cloud.rightOffset() + basis.upZ() * cloud.upOffset();
		int cloudAlpha = BiomeSkyboxes.alphaToInt(alpha * cloud.alpha());
		
		for (CloudLobe lobe : cloud.lobes())
			addCloudLobe(buffer, matrix, basis, centerX, centerY, centerZ, lobe, cloudAlpha);
	}
	
	private static float cloudPitch(WastesCloud cloud, float gameDays)
	{
		return Mth.clamp(
				cloud.pitch() + Mth.sin(cloud.pitchPhase() + gameDays * cloud.pitchSpeed()) * cloud.pitchAmplitude(),
				CLOUD_MIN_PITCH,
				CLOUD_MAX_PITCH);
	}
	
	private static CloudBasis cloudBasis(float yaw, float pitch)
	{
		float horizontal = Mth.cos(pitch);
		float dirX = Mth.sin(yaw) * horizontal;
		float dirY = Mth.sin(pitch);
		float dirZ = Mth.cos(yaw) * horizontal;
		float rightX = Mth.cos(yaw);
		float rightY = 0.0F;
		float rightZ = -Mth.sin(yaw);
		float upX = -Mth.sin(yaw) * Mth.sin(pitch);
		float upY = Mth.cos(pitch);
		float upZ = -Mth.cos(yaw) * Mth.sin(pitch);
		
		return new CloudBasis(dirX, dirY, dirZ, rightX, rightY, rightZ, upX, upY, upZ);
	}
	
	private static void addCloudLobe(BufferBuilder buffer,
	                                 Matrix4f matrix,
	                                 CloudBasis basis,
	                                 float centerX,
	                                 float centerY,
	                                 float centerZ,
	                                 CloudLobe lobe,
	                                 int cloudAlpha)
	{
		float rotationSin = Mth.sin(lobe.rotation());
		float rotationCos = Mth.cos(lobe.rotation());
		int centerAlpha = Mth.clamp((int)(cloudAlpha * lobe.alpha()), 0, 255);
		
		for (int segment = 0; segment < CLOUD_SEGMENTS; segment++)
		{
			addCloudVertex(buffer, matrix, basis, centerX, centerY, centerZ, lobe.right(), lobe.up(), centerAlpha);
			addCloudEdgeVertex(buffer, matrix, basis, centerX, centerY, centerZ, lobe, segment, rotationSin, rotationCos);
			addCloudEdgeVertex(buffer, matrix, basis, centerX, centerY, centerZ, lobe, segment + 1, rotationSin, rotationCos);
		}
	}
	
	private static void addCloudEdgeVertex(BufferBuilder buffer,
	                                       Matrix4f matrix,
	                                       CloudBasis basis,
	                                       float centerX,
	                                       float centerY,
	                                       float centerZ,
	                                       CloudLobe lobe,
	                                       int segment,
	                                       float rotationSin,
	                                       float rotationCos)
	{
		float angle = Mth.TWO_PI * segment / CLOUD_SEGMENTS;
		float scale = lobe.edgeScale()[segment % CLOUD_SEGMENTS];
		float localRight = Mth.cos(angle) * lobe.radiusRight() * scale;
		float localUp = Mth.sin(angle) * lobe.radiusUp() * scale;
		float rotatedRight = localRight * rotationCos - localUp * rotationSin;
		float rotatedUp = localRight * rotationSin + localUp * rotationCos;
		addCloudVertex(buffer, matrix, basis, centerX, centerY, centerZ, lobe.right() + rotatedRight, lobe.up() + rotatedUp, 0);
	}
	
	private static void addCloudVertex(BufferBuilder buffer,
	                                   Matrix4f matrix,
	                                   CloudBasis basis,
	                                   float centerX,
	                                   float centerY,
	                                   float centerZ,
	                                   float right,
	                                   float up,
	                                   int alpha)
	{
		buffer.addVertex(matrix,
						centerX + basis.rightX() * right + basis.upX() * up,
						centerY + basis.rightY() * right + basis.upY() * up,
						centerZ + basis.rightZ() * right + basis.upZ() * up).
				setColor(178, 199, 171, alpha);
	}
	
	private static float randomFloat(Random random, float min, float max)
	{
		return min + random.nextFloat() * (max - min);
	}
	
	private static float randomSign(Random random)
	{
		return random.nextBoolean() ? 1.0F : -1.0F;
	}
	
	private record WastesCloud(float yaw,
	                           float pitch,
	                           float yawSpeed,
	                           float pitchAmplitude,
	                           float pitchSpeed,
	                           float pitchPhase,
	                           float rightOffset,
	                           float upOffset,
	                           float alpha,
	                           List<CloudLobe> lobes)
	{
	}
	
	private record CloudLobe(float right,
	                         float up,
	                         float radiusRight,
	                         float radiusUp,
	                         float rotation,
	                         float alpha,
	                         float[] edgeScale)
	{
	}
	
	private record CloudBasis(float dirX,
	                          float dirY,
	                          float dirZ,
	                          float rightX,
	                          float rightY,
	                          float rightZ,
	                          float upX,
	                          float upY,
	                          float upZ)
	{
	}
}
