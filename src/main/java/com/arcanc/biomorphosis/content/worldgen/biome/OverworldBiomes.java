/**
 * @author ArcAnc
 * Created at: 22.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;


import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OverworldBiomes
{
	/*FIXME: заменить музыку в биоме пустошей*/

	private static void addFeature(BiomeGenerationSettings.@NotNull Builder builder, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature)
	{
		builder.addFeature(step, feature);
	}

	public static @NotNull Biome wastes (HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter)
	{
		MobSpawnSettings.Builder builder = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.desertSpawns(builder);
		BiomeGenerationSettings.Builder settingsBuilder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);
		BiomeDefaultFeatures.addFossilDecoration(settingsBuilder);

		BiomeDefaultFeatures.addDefaultCarversAndLakes(settingsBuilder);
		BiomeDefaultFeatures.addDefaultCrystalFormations(settingsBuilder);
		BiomeDefaultFeatures.addDefaultMonsterRoom(settingsBuilder);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(settingsBuilder);
		BiomeDefaultFeatures.addDefaultSprings(settingsBuilder);
		BiomeDefaultFeatures.addSurfaceFreezing(settingsBuilder);

		BiomeDefaultFeatures.addDefaultOres(settingsBuilder);
		BiomeDefaultFeatures.addDefaultSoftDisks(settingsBuilder);
		BiomeDefaultFeatures.addDefaultFlowers(settingsBuilder);
		BiomeDefaultFeatures.addDefaultGrass(settingsBuilder);
		BiomeDefaultFeatures.addDesertVegetation(settingsBuilder);
		BiomeDefaultFeatures.addDefaultMushrooms(settingsBuilder);
		BiomeDefaultFeatures.addDesertExtraVegetation(settingsBuilder);
		BiomeDefaultFeatures.addDesertExtraDecoration(settingsBuilder);
		return biome(false, 2.0F, 0.0F, builder, settingsBuilder, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT));
	}

	private static @NotNull Biome biome(
			boolean hasPercipitation,
			float temperature,
			float downfall,
			MobSpawnSettings.Builder mobSpawnSettings,
			BiomeGenerationSettings.Builder generationSettings,
			@Nullable Music backgroundMusic
	) {
		return biome(hasPercipitation, temperature, downfall, 4159204, 329011, null, null, mobSpawnSettings, generationSettings, backgroundMusic);
	}

	private static @NotNull Biome biome(
			boolean hasPrecipitation,
			float temperature,
			float downfall,
			int waterColor,
			int waterFogColor,
			@Nullable Integer grassColorOverride,
			@Nullable Integer foliageColorOverride,
			MobSpawnSettings.Builder mobSpawnSettings,
			BiomeGenerationSettings.Builder generationSettings,
			@Nullable Music backgroundMusic
	) {
		BiomeSpecialEffects.Builder biomespecialeffects$builder = new BiomeSpecialEffects.Builder()
				.waterColor(waterColor)
				.waterFogColor(waterFogColor)
				.fogColor(12638463)
				.skyColor(calculateSkyColor(temperature))
				.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
				.backgroundMusic(backgroundMusic);
		if (grassColorOverride != null)
			biomespecialeffects$builder.grassColorOverride(grassColorOverride);

		if (foliageColorOverride != null)
			biomespecialeffects$builder.foliageColorOverride(foliageColorOverride);

		return new Biome.BiomeBuilder()
				.hasPrecipitation(hasPrecipitation)
				.temperature(temperature)
				.downfall(downfall)
				.specialEffects(biomespecialeffects$builder.build())
				.mobSpawnSettings(mobSpawnSettings.build())
				.generationSettings(generationSettings.build())
				.build();
	}

	protected static int calculateSkyColor(float temperature)
	{
		float $$1 = temperature / 3.0F;
		$$1 = Mth.clamp($$1, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
	}
}
