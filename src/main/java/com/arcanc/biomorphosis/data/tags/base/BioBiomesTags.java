/**
 * @author ArcAnc
 * Created at: 05.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags.base;


import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class BioBiomesTags
{
	public static final TagKey<Biome> HAS_SWARM_VILLAGE = create("has_structure/swarm_village");
	public static final TagKey<Biome> HAS_SRF_HEADQUARTERS = create("has_structure/srf_headquarters");
	public static final TagKey<Biome> HAS_SRF_TOWER = create("has_structure/srf_tower");
	
	private static @NotNull TagKey<Biome> create(String name)
	{
		return TagKey.create(Registries.BIOME, Database.rl(name));
	}
}
