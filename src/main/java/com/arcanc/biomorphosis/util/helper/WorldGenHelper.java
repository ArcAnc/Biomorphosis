/**
 * @author ArcAnc
 * Created at: 29.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;


import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

public class WorldGenHelper
{
	public static ResourceKey<Biome> biomeOrFallback(Registry<Biome> biomeRegistry, ResourceKey<Biome>... biomes)
	{
		for (ResourceKey<Biome> key : biomes)
		{
			if (key == null)
				continue;
			
			if (key.location().getNamespace().equals(Database.MOD_ID) || key.location().getNamespace().equals("minecraft"))
				return key;
		}
		
		throw new RuntimeException("Failed to find fallback for biome!");
	}
	
	public static boolean isInsideBuildHeight(ServerLevelAccessor level, int y, int structureHeight)
	{
		LevelHeightAccessor heightAccessor = level.getLevel();
		return y >= heightAccessor.getMinBuildHeight() && y + structureHeight < heightAccessor.getMaxBuildHeight();
	}
}
