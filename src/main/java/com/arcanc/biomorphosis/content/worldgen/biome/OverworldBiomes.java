/**
 * @author ArcAnc
 * Created at: 22.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.content.worldgen.BioFeatures;
import com.arcanc.biomorphosis.content.worldgen.biome.wastes.WastesSpireFeature;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import javax.annotation.Nullable;

public class OverworldBiomes
{
	/*FIXME: заменить музыку в биоме пустошей*/
	private static final float WASTES_TEMPERATURE = 1.35f;
	private static final float WASTES_DOWNFALL = 0.0F;

	private static void addFeature(BiomeGenerationSettings.Builder builder, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature)
	{
		builder.addFeature(step, feature);
	}

	public static Biome wastes (HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter)
	{
		MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

		spawnBuilder.addSpawn(MobCategory.MONSTER,
				new MobSpawnSettings.SpawnerData(
						Registration.EntityReg.MOB_INFESTOR.getEntityHolder().get(), 1, 1, 2));
		spawnBuilder.addSpawn(MobCategory.MONSTER,
				new MobSpawnSettings.SpawnerData(
						Registration.EntityReg.MOB_INFESTOR.getEntityHolder().get(), 1, 1, 2));
		spawnBuilder.addSpawn(MobCategory.MONSTER,
				new MobSpawnSettings.SpawnerData(
						Registration.EntityReg.MOB_SWARMLING.getEntityHolder().get(), 1, 4, 8));
		spawnBuilder.addSpawn(MobCategory.MONSTER,
				new MobSpawnSettings.SpawnerData(
						Registration.EntityReg.MOB_LARVA.getEntityHolder().get(), 1, 1, 3));
		spawnBuilder.addSpawn(MobCategory.MONSTER,
				new MobSpawnSettings.SpawnerData(
						Registration.EntityReg.MOB_ZIRIS.getEntityHolder().get(), 1, 2, 4));
		spawnBuilder.addSpawn(MobCategory.MONSTER,
				new MobSpawnSettings.SpawnerData(
						Registration.EntityReg.MOB_KSIGG.getEntityHolder().get(), 1, 2, 4));

		BiomeDefaultFeatures.caveSpawns(spawnBuilder);

		BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);
		BiomeDefaultFeatures.addFossilDecoration(biomeBuilder);
		
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeBuilder);
		BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biomeBuilder);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeBuilder);
		BiomeDefaultFeatures.addDefaultSprings(biomeBuilder);
		BiomeDefaultFeatures.addSurfaceFreezing(biomeBuilder);

		BiomeDefaultFeatures.addDefaultOres(biomeBuilder);
		BiomeDefaultFeatures.addDefaultSoftDisks(biomeBuilder);
		addFeature(biomeBuilder, GenerationStep.Decoration.SURFACE_STRUCTURES, WastesSpireFeature.WASTES_SPIRE_PLACED);
		addFeature(biomeBuilder, GenerationStep.Decoration.VEGETAL_DECORATION, BioFeatures.WASTES_GRASS_PLACED);
		return biome(false, WASTES_TEMPERATURE, WASTES_DOWNFALL, spawnBuilder, biomeBuilder, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT));
	}

	private static Biome biome(
			boolean hasPercipitation,
			float temperature,
			float downfall,
			MobSpawnSettings.Builder mobSpawnSettings,
			BiomeGenerationSettings.Builder generationSettings,
			@Nullable Music backgroundMusic
	) {
		return biome(hasPercipitation, temperature, downfall, 4159204, 329011, null, null, mobSpawnSettings, generationSettings, backgroundMusic);
	}

	private static Biome biome(
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
		BiomeSpecialEffects.Builder biomespecialeffects$builder = new BiomeSpecialEffects.Builder().
						waterColor(waterColor).
						waterFogColor(waterFogColor).
						fogColor(12638463).
						skyColor(calculateSkyColor(temperature)).
						ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).
						backgroundMusic(backgroundMusic);
		if (grassColorOverride != null)
			biomespecialeffects$builder.grassColorOverride(grassColorOverride);

		if (foliageColorOverride != null)
			biomespecialeffects$builder.foliageColorOverride(foliageColorOverride);

		return new Biome.BiomeBuilder().
						hasPrecipitation(hasPrecipitation).
						temperature(temperature).
						downfall(downfall).
						specialEffects(biomespecialeffects$builder.build()).
						mobSpawnSettings(mobSpawnSettings.build()).
						generationSettings(generationSettings.build()).
						build();
	}

	protected static int calculateSkyColor(float temperature)
	{
		float $$1 = temperature / 3.0F;
		$$1 = Mth.clamp($$1, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
	}
}
