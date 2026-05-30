/**
 * @author ArcAnc
 * Created at: 20.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;


import com.arcanc.biomorphosis.util.Database;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class OverworldRegion extends Region
{
	public static final ResourceLocation ID = Database.rl("overworld");
	
	public OverworldRegion(int weight)
	{
		super(ID, RegionType.OVERWORLD, weight);
	}
	
	@Override
	public void addBiomes(Registry<Biome> registry,
						  Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
	{
		new OverworldRegionBuilder().addBiomes(registry, mapper);
	}
}
