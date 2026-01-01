/**
 * @author ArcAnc
 * Created at: 22.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.worldgen.srf.SwarmResistanceForces;
import com.arcanc.biomorphosis.content.worldgen.swarm_village.SwarmVillage;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

public class BioWorldGenProvider extends BioRegistryData
{
	public static final ResourceKey<StructureTemplatePool> EMPTY_POOL = ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl("empty"));
	
	@Override
	protected void addContent()
	{
	
	}

	@Override
	protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
	{
		//FIXME: придумать адекватный метод впилить биом
		//registrySetBuilder.add(Registries.BIOME, BioBiomes :: bootstrap);
		
		SwarmResistanceForces.init();
		
		registrySetBuilder.add(Registries.STRUCTURE, BioWorldGenProvider :: registerStructures);
		registrySetBuilder.add(Registries.STRUCTURE_SET, BioWorldGenProvider :: registerStructureSets);
		registrySetBuilder.add(Registries.TEMPLATE_POOL, BioWorldGenProvider :: registerTemplatePools);
	}
	
	private static void registerStructures(@NotNull BootstrapContext<Structure> context)
	{
		SwarmVillage.structures(context);
		SwarmResistanceForces.structures(context);
	}
	
	private static void registerStructureSets(BootstrapContext<StructureSet> context)
	{
		SwarmVillage.structureSets(context);
		SwarmResistanceForces.structureSets(context);
	}
	
	private static void registerTemplatePools(BootstrapContext<StructureTemplatePool> context)
	{
		SwarmVillage.templatePools(context);
		SwarmResistanceForces.templatePools(context);
	}
}
