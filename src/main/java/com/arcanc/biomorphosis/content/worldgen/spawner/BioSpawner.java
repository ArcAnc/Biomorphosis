/**
 * @author ArcAnc
 * Created at: 01.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.spawner;


import com.arcanc.biomorphosis.data.tags.base.BioBiomesTags;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Arrays;
import java.util.Optional;

public class BioSpawner
{
	private static final KeysData SPAWNER_KEYS = new KeysData("spawner");
	private static final int SPACING = 12;
	private static final int SEPARATION = 2;
	private static final int SALT = 120643834;
	private static final float FREQUENCY = 1f;
	
	public static void structures(BootstrapContext<Structure> context)
	{
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		
		context.register(SPAWNER_KEYS.structure(), new SpawnerStructure(
				new Structure.StructureSettings.Builder(biomes.getOrThrow(BioBiomesTags.HAS_SPAWNER)).
						generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES).
						terrainAdapation(TerrainAdjustment.NONE).
						build(),
				Arrays.stream(SpawnerType.values()).
						map(SpawnerType :: setup).
						toList()));
	}
	
	public static void structureSets(BootstrapContext<StructureSet> context)
	{
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
		
		context.register(SPAWNER_KEYS.structureSet(), new StructureSet(
				structures.getOrThrow(SPAWNER_KEYS.structure()),
				new RandomSpreadStructurePlacement(
						Vec3i.ZERO,
						StructurePlacement.FrequencyReductionMethod.DEFAULT,
						FREQUENCY,
						SALT,
						Optional.empty(),
						SPACING,
						SEPARATION,
						RandomSpreadType.LINEAR)));
	}
	
	public static void templatePools(BootstrapContext<StructureTemplatePool> context)
	{
		// SpawnerStructure places structure NBTs directly, so no template pools are required here.
	}
	
	private record KeysData(ResourceKey<Structure> structure, ResourceKey<StructureSet> structureSet)
	{
		private KeysData(String name)
		{
			this (
					ResourceKey.create(Registries.STRUCTURE, Database.rl(name)),
					ResourceKey.create(Registries.STRUCTURE_SET, Database.rl(name)));
		}
	}
	
	private enum SpawnerType
	{
		INFESTOR("infestor", 1, 2, 1),
		KSIGG("ksigg", 1, 3, 1),
		LARVA("larva", 1, 2, 1),
		SWARMLING("swarmling", 1, 3, 1),
		ZIRIS("ziris", 1, 0, 1),
		MELONMAW("melonmaw", 1, 4, 1);
		
		private final String name;
		private final int variants;
		private final int sink;
		private final float weight;
		
		SpawnerType(String name, int variants, int sink, float weight)
		{
			this.name = name;
			this.variants = variants;
			this.sink = sink;
			this.weight = weight;
		}
		
		private SpawnerStructure.SpawnerSetup setup()
		{
			return new SpawnerStructure.SpawnerSetup(Database.rl(this.name), this.variants, this.sink, this.weight);
		}
	}
}
