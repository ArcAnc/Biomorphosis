/**
 * @author ArcAnc
 * Created at: 10.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;

import java.util.*;

public final class BiomeSkyboxes
{
	private static final float DOME_DAY_CYCLE_SCROLL = 1.0F;
	// Extra room for the long rays surrounding the sun's smaller texture core.
	private static final float SUN_SIZE = 18.0F;
	private static final float MOON_SIZE = 9.0F;
	private static final float CELESTIAL_DISTANCE = 94.0F;
	private static final int SAMPLE_RADIUS = 32;
	private static final int SAMPLE_STEP = 8;
	private static final float OUTSIDE_NEIGHBOR_WEIGHT = 0.35F;
	private static final float BLEND_SPEED = 0.055F;
	private static final float CLOUD_HIDE_WEIGHT = 0.35F;
	private static final RenderStateShard.ShaderStateShard POSITION_TEX_COLOR_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer :: getPositionTexColorShader);
	private static final Map<ResourceKey<Biome>, BiomeSkybox> SKYBOXES = new LinkedHashMap<>();
	private static final Map<ResourceKey<Biome>, Float> CURRENT_WEIGHTS = new HashMap<>();
	private static final Map<ResourceLocation, Boolean> TEXTURE_EXISTS = new HashMap<>();
	private static final Map<ResourceLocation, RenderType> CELESTIAL_RENDER_TYPES = new HashMap<>();
	
	private BiomeSkyboxes()
	{
	}
	
	public static void init(IEventBus modEventBus)
	{
		registerDefaults();
		CubemapTextures.registerReloadListener(modEventBus);
		CubemapSkyboxRenderer.register(modEventBus);
		
		NeoForge.EVENT_BUS.addListener(BiomeSkyboxes :: renderSkyboxes);
		NeoForge.EVENT_BUS.addListener(BiomeSkyboxes :: modifyFogColor);
	}
	
	private static void registerDefaults()
	{
		WastesSkybox.register();
	}
	
	public static void register(ResourceKey<Biome> biome, BiomeSkybox skybox)
	{
		SKYBOXES.put(biome, skybox);
		CubemapTextures.register(skybox.day());
		CubemapTextures.register(skybox.night());
	}

	static void registerCubemap(CubemapDefinition cubemap)
	{
		CubemapTextures.register(cubemap);
	}
	
	public static void register(ResourceKey<Biome> biome, ResourceLocation skyboxPath)
	{
		register(biome, BiomeSkybox.builder(skyboxPath).build());
	}
	
	public static void addEffect(ResourceKey<Biome> biome, BiomeSkyboxEffect effect)
	{
		BiomeSkybox skybox = SKYBOXES.get(biome);
		if (skybox == null)
			throw new IllegalArgumentException("No skybox registered for biome " + biome.location());
		
		List<BiomeSkyboxEffect> effects = new ArrayList<>(skybox.effects());
		effects.add(effect);
		register(biome, new BiomeSkybox(
				skybox.day(),
				skybox.night(),
				skybox.sun(),
				skybox.moon(),
				skybox.noonColor(),
				skybox.midnightColor(),
				skybox.noonFogColor(),
				skybox.midnightFogColor(),
				List.copyOf(effects)));
	}
	
	public static boolean shouldHideClouds()
	{
		return CURRENT_WEIGHTS.values().stream().anyMatch(weight -> weight > CLOUD_HIDE_WEIGHT);
	}

	public static boolean shouldHideVanillaCelestialBodies()
	{
		return shouldHideClouds();
	}
	
	public static void modifyFogColor(final ViewportEvent.ComputeFogColor event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null || event.getCamera().getFluidInCamera() != FogType.NONE)
			return;
		
		float partialTick = (float)event.getPartialTick();
		float red = event.getRed();
		float green = event.getGreen();
		float blue = event.getBlue();
		
		for (Map.Entry<ResourceKey<Biome>, Float> entry : CURRENT_WEIGHTS.entrySet().stream().
				filter(entry -> entry.getValue() > 0.001F).
				sorted(Map.Entry.comparingByValue()).
				toList())
		{
			BiomeSkybox skybox = SKYBOXES.get(entry.getKey());
			if (skybox == null)
				continue;
			
			int fogColor = timeBlendColor(level, partialTick, skybox.noonFogColor(), skybox.midnightFogColor());
			float weight = Mth.clamp(entry.getValue(), 0.0F, 1.0F);
			red = Mth.lerp(weight, red, MathHelper.ColorHelper.redFloat(fogColor));
			green = Mth.lerp(weight, green, MathHelper.ColorHelper.greenFloat(fogColor));
			blue = Mth.lerp(weight, blue, MathHelper.ColorHelper.blueFloat(fogColor));
		}
		
		event.setRed(red);
		event.setGreen(green);
		event.setBlue(blue);
	}
	
	public static void renderSkyboxes(final RenderLevelStageEvent event)
	{
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY || SKYBOXES.isEmpty())
			return;
		
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null)
			return;
		
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		Map<ResourceKey<Biome>, Float> targets = sampleTargets(level, event.getCamera().getPosition());
		updateWeights(targets);
		
		if (CURRENT_WEIGHTS.values().stream().allMatch(value -> value <= 0.001F))
			return;
		
		PoseStack poseStack = new PoseStack();
		poseStack.mulPose(event.getModelViewMatrix());
		
		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer :: getPositionTexColorShader);
		
		CURRENT_WEIGHTS.entrySet().stream().
				filter(entry -> entry.getValue() > 0.001F).
				sorted(Map.Entry.comparingByValue()).
				forEach(entry -> renderSkybox(
						level,
						entry.getKey(),
						SKYBOXES.get(entry.getKey()),
						Mth.clamp(entry.getValue(), 0.0F, 1.0F),
						poseStack,
						event.getProjectionMatrix(),
						event.getCamera(),
						event.getRenderTick(),
						partialTick));
		
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer :: getPositionTexColorShader);
		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
	}
	
	private static Map<ResourceKey<Biome>, Float> sampleTargets(ClientLevel level, Vec3 cameraPos)
	{
		Map<ResourceKey<Biome>, Float> result = new HashMap<>();
		float totalWeight = 0.0F;
		BlockPos center = BlockPos.containing(cameraPos);
		Optional<ResourceKey<Biome>> centerBiome = level.getBiome(center).unwrapKey().filter(SKYBOXES :: containsKey);
		if (centerBiome.isPresent())
		{
			result.put(centerBiome.get(), 1.0F);
			return result;
		}
		
		for (int x = -SAMPLE_RADIUS; x <= SAMPLE_RADIUS; x += SAMPLE_STEP)
		{
			for (int z = -SAMPLE_RADIUS; z <= SAMPLE_RADIUS; z += SAMPLE_STEP)
			{
				float distance = Mth.sqrt((float)(x * x + z * z));
				if (distance > SAMPLE_RADIUS)
					continue;
				
				float rawSampleWeight = 1.0F - distance / SAMPLE_RADIUS;
				final float sampleWeight = rawSampleWeight * rawSampleWeight;
				totalWeight += sampleWeight;
				
				BlockPos samplePos = center.offset(x, 0, z);
				level.getBiome(samplePos).unwrapKey().
						filter(SKYBOXES :: containsKey).
						ifPresent(key -> result.merge(key, sampleWeight, Float :: sum));
			}
		}
		
		if (totalWeight <= 0.0F)
			return result;
		
		final float divisor = totalWeight;
		result.replaceAll((key, weight) -> weight / divisor);
		result.replaceAll((key, weight) -> weight * OUTSIDE_NEIGHBOR_WEIGHT);
		return result;
	}
	
	private static void updateWeights(Map<ResourceKey<Biome>, Float> targets)
	{
		SKYBOXES.keySet().forEach(key ->
		{
			float current = CURRENT_WEIGHTS.getOrDefault(key, 0.0F);
			float target = targets.getOrDefault(key, 0.0F);
			float next = Mth.lerp(BLEND_SPEED, current, target);
			
			if (next <= 0.001F && target <= 0.001F)
				CURRENT_WEIGHTS.remove(key);
			else
				CURRENT_WEIGHTS.put(key, next);
		});
	}
	
	private static void renderSkybox(ClientLevel level,
	                                 ResourceKey<Biome> biome,
	                                 BiomeSkybox skybox,
	                                 float alpha,
	                                 PoseStack poseStack,
	                                 Matrix4f projectionMatrix,
	                                 Camera camera,
	                                 int renderTick,
	                                 float partialTick)
	{
		renderDome(level, skybox, alpha, poseStack, partialTick);
		
		BiomeSkyboxRenderContext context = new BiomeSkyboxRenderContext(
				level,
				biome,
				skybox,
				poseStack,
				projectionMatrix,
				camera,
				renderTick,
				partialTick,
				alpha);
		
		for (BiomeSkyboxEffect effect : skybox.effects())
			if (!effect.isForeground())
				effect.render(context);
		
		renderCelestialDiscs(level, skybox, alpha, poseStack, projectionMatrix, partialTick);
		
		for (BiomeSkyboxEffect effect : skybox.effects())
			if (effect.isForeground())
				effect.render(context);
	}
	
	static boolean hasTexture(ResourceLocation texture)
	{
		return TEXTURE_EXISTS.computeIfAbsent(texture, location -> Minecraft.getInstance().getResourceManager().getResource(location).isPresent());
	}

	static void onResourcesReloaded()
	{
		TEXTURE_EXISTS.clear();
	}
	
	private static RenderType celestialRenderType(ResourceLocation texture)
	{
		return CELESTIAL_RENDER_TYPES.computeIfAbsent(texture, BiomeSkyboxes :: createCelestialRenderType);
	}
	
	private static RenderType createCelestialRenderType(ResourceLocation texture)
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder().
				setShaderState(POSITION_TEX_COLOR_SHADER).
				setTextureState(new RenderStateShard.TextureStateShard(texture, true, false)).
				setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).
				setCullState(RenderStateShard.NO_CULL).
				setDepthTestState(RenderStateShard.NO_DEPTH_TEST).
				setWriteMaskState(RenderStateShard.COLOR_WRITE).
				createCompositeState(false);
		String suffix = texture.getNamespace() + "_" + texture.getPath().replace('/', '_').replace('.', '_');
		return RenderType.create(
				Database.rlStr("sky_celestial_translucent_" + suffix),
				DefaultVertexFormat.POSITION_TEX_COLOR,
				VertexFormat.Mode.QUADS,
				RenderType.TRANSIENT_BUFFER_SIZE,
				false,
				true,
				state);
	}
	
	private static void renderDome(ClientLevel level,
	                               BiomeSkybox skybox,
	                               float alpha,
	                               PoseStack poseStack,
	                               float partialTick)
	{
		int skyColor = skyColor(level, skybox, partialTick);
		float red = MathHelper.ColorHelper.redFloat(skyColor);
		float green = MathHelper.ColorHelper.greenFloat(skyColor);
		float blue = MathHelper.ColorHelper.blueFloat(skyColor);
		float nightWeight = nightSkyWeight(level, partialTick);
		float rotation = sphereTravelAngle(level, partialTick);
		CubemapSkyboxRenderer.render(skybox.day(), alpha * (1.0F - nightWeight), rotation, red, green, blue, poseStack);
		CubemapSkyboxRenderer.render(skybox.night(), alpha * nightWeight, rotation, red, green, blue, poseStack);
	}
	
	static float sphereTravelAngle(ClientLevel level, float partialTick)
	{
		return level.getSunAngle(partialTick) * DOME_DAY_CYCLE_SCROLL;
	}

	private static float nightSkyWeight(ClientLevel level, float partialTick)
	{
		float time = level.getTimeOfDay(partialTick);
		return (1.0F - Mth.cos(time * Mth.TWO_PI)) * 0.5F;
	}
	
	private static int skyColor(ClientLevel level, BiomeSkybox skybox, float partialTick)
	{
		return timeBlendColor(level, partialTick, skybox.noonColor(), skybox.midnightColor());
	}
	
	private static int timeBlendColor(ClientLevel level, float partialTick, int noon, int midnight)
	{
		float time = level.getTimeOfDay(partialTick);
		float noonWeight = (Mth.cos(time * Mth.TWO_PI) + 1.0F) * 0.5F;
		return MathHelper.ColorHelper.color(
				Mth.lerpInt(noonWeight, MathHelper.ColorHelper.red(midnight), MathHelper.ColorHelper.red(noon)),
				Mth.lerpInt(noonWeight, MathHelper.ColorHelper.green(midnight), MathHelper.ColorHelper.green(noon)),
				Mth.lerpInt(noonWeight, MathHelper.ColorHelper.blue(midnight), MathHelper.ColorHelper.blue(noon)));
	}
	
	private static void renderCelestialDiscs(ClientLevel level,
	                                         BiomeSkybox skybox,
	                                         float alpha,
	                                         PoseStack poseStack,
	                                         Matrix4f projectionMatrix,
	                                         float partialTick)
	{
		float angle = sphereTravelAngle(level, partialTick);
		if (hasTexture(skybox.sun()))
			renderTexturedCelestialDisc(skybox.sun(), poseStack.last().pose(), angle, alpha, SUN_SIZE);
		else
			renderColoredCelestialDisc(poseStack.last().pose(), angle, alpha, SUN_SIZE, 1.0F, 0.36F, 0.22F);
		
		if (hasTexture(skybox.moon()))
			renderTexturedCelestialDisc(skybox.moon(), poseStack.last().pose(), angle + Mth.PI, alpha, MOON_SIZE);
		else
			renderColoredCelestialDisc(poseStack.last().pose(), angle + Mth.PI, alpha, MOON_SIZE, 0.62F, 0.25F, 0.45F);
		
		RenderSystem.setShader(GameRenderer :: getPositionTexColorShader);
	}
	
	private static void renderTexturedCelestialDisc(ResourceLocation texture,
	                                                Matrix4f matrix,
	                                                float angle,
	                                                float alpha,
	                                                float size)
	{
		DiscBasis basis = discBasis(angle);
		if (basis.visibility() <= 0.001F)
			return;
		
		float visibleAlpha = alpha * basis.visibility();
		int alphaInt = alphaToInt(visibleAlpha);
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		addCelestialVertex(buffer, matrix, basis, -size, -size, 0.0F, 1.0F, alphaInt);
		addCelestialVertex(buffer, matrix, basis, size, -size, 1.0F, 1.0F, alphaInt);
		addCelestialVertex(buffer, matrix, basis, size, size, 1.0F, 0.0F, alphaInt);
		addCelestialVertex(buffer, matrix, basis, -size, size, 0.0F, 0.0F, alphaInt);
		celestialRenderType(texture).draw(buffer.buildOrThrow());
	}
	
	private static void renderColoredCelestialDisc(Matrix4f matrix,
	                                               float angle,
	                                               float alpha,
	                                               float size,
	                                               float red,
	                                               float green,
	                                               float blue)
	{
		DiscBasis basis = discBasis(angle);
		if (basis.visibility() <= 0.001F)
			return;
		
		RenderSystem.setShader(GameRenderer :: getPositionColorShader);
		int alphaInt = alphaToInt(alpha * basis.visibility());
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		addColoredCelestialVertex(buffer, matrix, basis, -size, -size, red, green, blue, 0);
		addColoredCelestialVertex(buffer, matrix, basis, size, -size, red, green, blue, 0);
		addColoredCelestialVertex(buffer, matrix, basis, size, size, red, green, blue, alphaInt);
		addColoredCelestialVertex(buffer, matrix, basis, -size, size, red, green, blue, alphaInt);
		BufferUploader.drawWithShader(buffer.buildOrThrow());
	}
	
	private static DiscBasis discBasis(float angle)
	{
		float dirX = -Mth.sin(angle);
		float dirY = Mth.cos(angle);
		float dirZ = 0.0F;
		float visibility = Mth.clamp((dirY + 0.08F) / 0.16F, 0.0F, 1.0F);
		
		float rightX = 0.0F;
		float rightY = 0.0F;
		float rightZ = 1.0F;
		float upX = -Mth.cos(angle);
		float upY = -Mth.sin(angle);
		float upZ = 0.0F;
		
		return new DiscBasis(dirX, dirY, dirZ, rightX, rightY, rightZ, upX, upY, upZ, visibility);
	}
	
	private static void addCelestialVertex(BufferBuilder buffer, Matrix4f matrix, DiscBasis basis, float right, float up, float u, float v, int alpha)
	{
		buffer.addVertex(matrix,
						basis.dirX() * CELESTIAL_DISTANCE + basis.rightX() * right + basis.upX() * up,
						basis.dirY() * CELESTIAL_DISTANCE + basis.rightY() * right + basis.upY() * up,
						basis.dirZ() * CELESTIAL_DISTANCE + basis.rightZ() * right + basis.upZ() * up).
				setUv(u, v).
				setColor(255, 255, 255, alpha);
	}
	
	private static void addColoredCelestialVertex(BufferBuilder buffer, Matrix4f matrix, DiscBasis basis, float right, float up, float red, float green, float blue, int alpha)
	{
		buffer.addVertex(matrix,
						basis.dirX() * CELESTIAL_DISTANCE + basis.rightX() * right + basis.upX() * up,
						basis.dirY() * CELESTIAL_DISTANCE + basis.rightY() * right + basis.upY() * up,
						basis.dirZ() * CELESTIAL_DISTANCE + basis.rightZ() * right + basis.upZ() * up).
				setColor(colorToInt(red), colorToInt(green), colorToInt(blue), alpha);
	}
	
	static int alphaToInt(float alpha)
	{
		if (alpha > 0.98F)
			return 255;
		return Mth.clamp((int)(alpha * 255.0F), 0, 255);
	}

	private static int colorToInt(float color)
	{
		return Mth.clamp((int)(color * 255.0F), 0, 255);
	}
	
	private record DiscBasis(float dirX,
	                         float dirY,
	                         float dirZ,
	                         float rightX,
	                         float rightY,
	                         float rightZ,
	                         float upX,
	                         float upY,
	                         float upZ,
	                         float visibility)
	{
	}
	
}
