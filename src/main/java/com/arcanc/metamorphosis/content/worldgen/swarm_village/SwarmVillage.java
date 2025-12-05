/**
 * @author ArcAnc
 * Created at: 18.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.worldgen.swarm_village;


import com.arcanc.metamorphosis.util.Database;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class SwarmVillage
{
	public static final KeysData VILLAGE = new KeysData("village");
	
	public static void structures(@NotNull BootstrapContext<Structure> context)
	{
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);

		context.register(VILLAGE.structure(), new SwarmVillageStructure(
				new Structure.StructureSettings.Builder(biomes.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS)).
					terrainAdapation(TerrainAdjustment.BEARD_THIN).build(),
					pools.getOrThrow(VILLAGE.pools().getPoolKey(CellType.CENTER)),
				6,
				ConstantHeight.of(VerticalAnchor.absolute(-1)),
				true,
				Heightmap.Types.WORLD_SURFACE_WG));
	}

	public static void structureSets(@NotNull BootstrapContext<StructureSet> context)
	{
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
		
		context.register (VILLAGE.structureSet (),
				new StructureSet (structures.getOrThrow (VILLAGE.structure()),
						new RandomSpreadStructurePlacement (34, 8, RandomSpreadType.LINEAR, 65295359)));
	}

	public static void templatePools(@NotNull BootstrapContext<StructureTemplatePool> context)
	{
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
		Holder<StructureTemplatePool> empty = pools.getOrThrow(Pools.EMPTY);
		
		context.register (VILLAGE.pools().getPoolKey(CellType.CENTER),
				new StructureTemplatePool (
				empty,
				ImmutableList.of (
					Pair.of(StructurePoolElement.legacy (Database.rlStr ("village/center/start")), 1)),
						StructureTemplatePool.Projection.RIGID));
		
		context.register (VILLAGE.pools().getPoolKey(CellType.ROAD),
				new StructureTemplatePool(
						empty,
						ImmutableList.of(
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/left")), 2),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/right")), 2),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/t")), 2),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/x")), 2),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/fl")), 2),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/fr")), 2),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/straight")), 2),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/0")), 5),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/1")), 5),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/2")), 4),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/3")), 4),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/4")), 3),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/5")), 2),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/6")), 1)
								/*Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/to_spire")), 1)*/)));
		
		context.register(VILLAGE.pools().getPoolKey(CellType.SPIRE),
				new StructureTemplatePool(
						empty,
						ImmutableList.of(
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/0")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/1")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/2")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/3")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/4")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/5")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/6")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/7")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/8")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/9")), 1))));
		
		context.register(VILLAGE.pools().getPoolKey(CellType.WORKER), new StructureTemplatePool(
						empty,
						ImmutableList.of(
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/mobs/worker")), 1))));
		
		context.register(VILLAGE.pools().getPoolKey(CellType.GUARD), new StructureTemplatePool(
				empty,
				ImmutableList.of(
						Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/mobs/guard")), 1))));
		
		/*context.register(VILLAGE.pools().floor(), new StructureTemplatePool(
						empty,
						ImmutableList.of(
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/floor/size_15")), 1)
						)));*/
		/*context.register(VILLAGE.pools().getPoolKey(CellType.HOUSE),
				new StructureTemplatePool(
						empty,
						ImmutableList.of(),
									StructureTemplatePool.Projection.RIGID));*/
	}
	
	public record KeysData(ResourceKey<Structure> structure, ResourceKey<StructureSet> structureSet, SwarmVillagePools pools)
	{
		public KeysData(String name)
		{
			this (
					ResourceKey.create(Registries.STRUCTURE, Database.rl(name)),
					ResourceKey.create(Registries.STRUCTURE_SET, Database.rl(name)),
					new SwarmVillagePools(name));
		}
	}

	public static class SwarmVillagePools
	{
		private final Map<CellType, ResourceKey<StructureTemplatePool>> poolsData = new EnumMap<>(CellType.class);
		private static final ResourceKey<StructureTemplatePool> EMPTY_POOL = ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl("empty"));
		
		public SwarmVillagePools(String name)
		{
			this.poolsData.put(CellType.CENTER, ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/start")));
			this.poolsData.put(CellType.ROAD, ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/road")));
			this.poolsData.put(CellType.SPIRE, ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/spire")));
			this.poolsData.put(CellType.WORKER, ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/worker")));
			this.poolsData.put(CellType.GUARD, ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/guard")));
			this.poolsData.put(CellType.HOUSE, ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/house")));
		}
		
		public ResourceKey<StructureTemplatePool> getPoolKey(CellType type)
		{
			return this.poolsData.getOrDefault(type, EMPTY_POOL);
		}
		
		public StructureTemplatePool getPool(@NotNull Registry<StructureTemplatePool> registry, CellType type)
		{
			return registry.getOptional(this.getPoolKey(type)).orElse(null);
		}
	}
	
	public enum CellType
	{
		EMPTY,
		CENTER,
		HOUSE,
		ROAD,
		SPIRE,
		WORKER,
		GUARD;
	}

}
