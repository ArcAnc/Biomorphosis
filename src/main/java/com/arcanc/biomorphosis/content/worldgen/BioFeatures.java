/**
 * @author ArcAnc
 * Created at: 29.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.content.worldgen.biome.wastes.WastesSpireFeature;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class BioFeatures
{
	public static final ResourceKey<ConfiguredFeature<?, ?>> WASTES_GRASS_CONFIGURED = configured("wastes_grass");
	public static final ResourceKey<PlacedFeature> WASTES_GRASS_PLACED = placed("wastes_grass");
	
	public static void configuredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context)
	{
		context.register(WastesSpireFeature.WASTES_SPIRE_CONFIGURED, new ConfiguredFeature<>(
				Registration.FeatureReg.SPIRE.get(),
				NoneFeatureConfiguration.INSTANCE));
		context.register(WASTES_GRASS_CONFIGURED, new ConfiguredFeature<>(
				Feature.RANDOM_PATCH,
				FeatureUtils.simpleRandomPatchConfiguration(
						2,
						PlacementUtils.onlyWhenEmpty(
								Feature.SIMPLE_BLOCK,
								new SimpleBlockConfiguration(
										BlockStateProvider.simple(Registration.BlockReg.BIO_BUSH.get())
								)
						)
				)
		));
	}

	public static void placedFeatures(BootstrapContext<PlacedFeature> context)
	{
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
		Holder<ConfiguredFeature<?, ?>> wastesSpire = configuredFeatures.getOrThrow(WastesSpireFeature.WASTES_SPIRE_CONFIGURED);

		context.register(WastesSpireFeature.WASTES_SPIRE_PLACED, new PlacedFeature(wastesSpire, List.of(
				RarityFilter.onAverageOnceEvery(40),
				InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
				BiomeFilter.biome())));
		
		Holder<ConfiguredFeature<?, ?>> wastesGrass = configuredFeatures.getOrThrow(WASTES_GRASS_CONFIGURED);
		
		context.register(WASTES_GRASS_PLACED, new PlacedFeature(wastesGrass, List.of(
				NoiseThresholdCountPlacement.of(-0.8, 5, 10),
				InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
				BiomeFilter.biome()
		)));
	}

	public static ResourceKey<ConfiguredFeature<?, ?>> configured(String name)
	{
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, Database.rl(name));
	}

	public static ResourceKey<PlacedFeature> placed(String name)
	{
		return ResourceKey.create(Registries.PLACED_FEATURE, Database.rl(name));
	}
}
