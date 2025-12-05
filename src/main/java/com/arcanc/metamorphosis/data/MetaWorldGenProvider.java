/**
 * @author ArcAnc
 * Created at: 22.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data;


import com.arcanc.metamorphosis.content.worldgen.swarm_village.SwarmVillage;
import com.arcanc.metamorphosis.data.regSetBuilder.MetaRegistryData;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.NotNull;

public class MetaWorldGenProvider extends MetaRegistryData
{
	@Override
	protected void addContent()
	{
	}

	@Override
	protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
	{
		//FIXME: придумать адекватный метод впилить биом
		//registrySetBuilder.add(Registries.BIOME, BioBiomes :: bootstrap);
		registrySetBuilder.add (Registries.TEMPLATE_POOL, SwarmVillage :: templatePools);
		registrySetBuilder.add (Registries.STRUCTURE, SwarmVillage :: structures);
		registrySetBuilder.add (Registries.STRUCTURE_SET, SwarmVillage :: structureSets);
	}
}
