/**
 * @author ArcAnc
 * Created at: 22.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;


import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class BioBiomes
{
	public static final ResourceKey<Biome> WASTES = registerBiomeKey("wastes");

	public static void bootstrap(@NotNull BootstrapContext<Biome> context)
	{
		var carver = context.lookup(Registries.CONFIGURED_CARVER);
		var placedFeatures = context.lookup(Registries.PLACED_FEATURE);

		register(context, WASTES, OverworldBiomes.wastes(placedFeatures, carver));
	}

	private static void register(@NotNull BootstrapContext<Biome> context, ResourceKey<Biome> key, Biome biome)
	{
		context.register(key, biome);
	}

	private static @NotNull ResourceKey<Biome> registerBiomeKey(String name)
	{
		return ResourceKey.create(Registries.BIOME, Database.rl(name));
	}
}
