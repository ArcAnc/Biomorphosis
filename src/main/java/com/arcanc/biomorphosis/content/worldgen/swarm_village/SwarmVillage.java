/**
 * @author ArcAnc
 * Created at: 18.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.swarm_village;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.BioWorldGenProvider;
import com.arcanc.biomorphosis.data.tags.base.BioBiomesTags;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class SwarmVillage
{
	public static final KeysData VILLAGE = new KeysData("village");
	
	public static void structures(BootstrapContext<Structure> context)
	{
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);

		context.register(VILLAGE.structure(), new SwarmVillageStructure(
				new Structure.StructureSettings.Builder(biomes.getOrThrow(BioBiomesTags.HAS_SWARM_VILLAGE)).
						terrainAdapation(TerrainAdjustment.BEARD_THIN).
						spawnOverrides(Map.of(
								MobCategory.MONSTER,
								new StructureSpawnOverride(
										StructureSpawnOverride.BoundingBoxType.STRUCTURE,
										WeightedRandomList.create(
												new MobSpawnSettings.SpawnerData(
														Registration.EntityReg.MOB_INFESTOR.getEntityHolder().get(), 1, 1, 2),
												new MobSpawnSettings.SpawnerData(
														Registration.EntityReg.MOB_SWARMLING.getEntityHolder().get(), 1, 4, 8),
												new MobSpawnSettings.SpawnerData(
														Registration.EntityReg.MOB_LARVA.getEntityHolder().get(), 1, 1, 3),
												new MobSpawnSettings.SpawnerData(
														Registration.EntityReg.MOB_ZIRIS.getEntityHolder().get(), 1, 2, 4),
												new MobSpawnSettings.SpawnerData(
														Registration.EntityReg.MOB_KSIGG.getEntityHolder().get(), 1, 2, 4),
												new MobSpawnSettings.SpawnerData(
														Registration.EntityReg.MOB_WORKER.getEntityHolder().get(), 1, 4, 6),
												new MobSpawnSettings.SpawnerData(
														Registration.EntityReg.MOB_QUEEN_GUARD.getEntityHolder().get(), 1, 1, 2)
										)
								)
				)).build(),
						pools.getOrThrow(VILLAGE.pools().getPoolKey(CellType.CENTER)),
				6,
				ConstantHeight.of(VerticalAnchor.absolute(-1)),
				true,
				Heightmap.Types.WORLD_SURFACE_WG));
	}

	public static void structureSets(BootstrapContext<StructureSet> context)
	{
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
		
		context.register (VILLAGE.structureSet(),
				new StructureSet (structures.getOrThrow (VILLAGE.structure()),
						new RandomSpreadStructurePlacement (16, 4, RandomSpreadType.LINEAR, 65295359)));
	}

	public static void templatePools(BootstrapContext<StructureTemplatePool> context)
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
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/0")), 10),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/1")), 10),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/2")), 20),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/3")), 40),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/4")), 60),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/5")), 80),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/house/6")), 100)
								/*Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/to_spire")), 1)*/)));
		
		/*context.register(VILLAGE.pools().getPoolKey(CellType.SPIRE),
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
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/spire/9")), 1))));*/
		
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
			return this.poolsData.getOrDefault(type, BioWorldGenProvider.EMPTY_POOL);
		}
		
		public @Nullable StructureTemplatePool getPool(Registry<StructureTemplatePool> registry, CellType type)
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
