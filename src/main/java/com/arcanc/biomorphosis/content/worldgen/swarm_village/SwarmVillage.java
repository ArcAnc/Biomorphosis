/**
 * @author ArcAnc
 * Created at: 18.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.swarm_village;


import com.arcanc.biomorphosis.util.Database;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
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
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.NotNull;

public class SwarmVillage
{
	public static final KeysData VILLAGE = new KeysData("village");

	public static void structures(@NotNull BootstrapContext<Structure> context)
	{
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);

		context.register(VILLAGE.structure(), new JigsawStructure(
				new Structure.StructureSettings.Builder(biomes.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS)).
					terrainAdapation(TerrainAdjustment.BEARD_THIN).build(),
					pools.getOrThrow(VILLAGE.pools().start()),
				6, ConstantHeight.of(VerticalAnchor.absolute(0)),
				false,
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
		
		context.register (VILLAGE.pools().start(),
				new StructureTemplatePool (
				empty,
				ImmutableList.of (
					Pair.of(StructurePoolElement.legacy (Database.rlStr ("village/center/start")), 1)),
						StructureTemplatePool.Projection.RIGID));
		
		context.register (VILLAGE.pools().roads(),
				new StructureTemplatePool(
						empty,
						ImmutableList.of(
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/left")), 1),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/right")), 1),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/t")), 1),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/x")), 1),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/fl")), 1),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/fr")), 1),
								Pair.of(SwarmLegacySinglePoolElement.terrainMatching(Database.rl("village/road/straight")), 1),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/road/house_0")), 5),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/road/house_1")), 6),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/road/house_2")), 7),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/road/house_3")), 8),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/road/house_4")), 9),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/road/house_5")), 8),
								Pair.of(SwarmLegacySinglePoolElement.rigid(Database.rl("village/road/house_6")), 10))));
		
		/*context.register(VILLAGE.pools().houses(),
				new StructureTemplatePool(
						empty,
						ImmutableList.of(
								Pair.of(StructurePoolElement.legacy(Database.rlStr("village/house/0")), 1),
								Pair.of(StructurePoolElement.legacy(Database.rlStr("village/house/1")), 2),
								Pair.of(StructurePoolElement.legacy(Database.rlStr("village/house/2")), 3),
								Pair.of(StructurePoolElement.legacy(Database.rlStr("village/house/3")), 4),
								Pair.of(StructurePoolElement.legacy(Database.rlStr("village/house/4")), 5),
								Pair.of(StructurePoolElement.legacy(Database.rlStr("village/house/5")), 6),
								Pair.of(StructurePoolElement.legacy(Database.rlStr("village/house/6")), 7)),
									StructureTemplatePool.Projection.RIGID));
	*/}

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

	public record SwarmVillagePools(ResourceKey<StructureTemplatePool> start, ResourceKey<StructureTemplatePool> roads, ResourceKey<StructureTemplatePool> houses)
	{
		public SwarmVillagePools(String name)
		{
			this (
					ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/start")),
					ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/road")),
					ResourceKey.create(Registries.TEMPLATE_POOL, Database.rl(name).withSuffix("/house")));
		}
	}

}
