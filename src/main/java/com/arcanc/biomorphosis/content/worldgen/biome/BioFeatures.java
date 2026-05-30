/**
 * @author ArcAnc
 * Created at: 29.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

public class BioFeatures
{
	public static final ResourceKey<ConfiguredFeature<?, ?>> WASTES_SPIRE_CONFIGURED = configured("spire");
	public static final ResourceKey<PlacedFeature> WASTES_SPIRE_PLACED = placed("spire");

	public static void configuredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context)
	{
		context.register(WASTES_SPIRE_CONFIGURED, new ConfiguredFeature<>(
				Registration.FeatureReg.SPIRE.get(),
				NoneFeatureConfiguration.INSTANCE));
	}

	public static void placedFeatures(BootstrapContext<PlacedFeature> context)
	{
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
		Holder<ConfiguredFeature<?, ?>> wastesSpire = configuredFeatures.getOrThrow(WASTES_SPIRE_CONFIGURED);

		context.register(WASTES_SPIRE_PLACED, new PlacedFeature(wastesSpire, List.of(
				RarityFilter.onAverageOnceEvery(10),
				InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
				BiomeFilter.biome())));
	}

	private static ResourceKey<ConfiguredFeature<?, ?>> configured(String name)
	{
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, Database.rl(name));
	}

	private static ResourceKey<PlacedFeature> placed(String name)
	{
		return ResourceKey.create(Registries.PLACED_FEATURE, Database.rl(name));
	}
}
