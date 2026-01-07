/**
 * @author ArcAnc
 * Created at: 27.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.srf;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.content.worldgen.srf.orders.PalladinOrders;
import com.arcanc.biomorphosis.data.tags.base.BioBiomesTags;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SwarmResistanceForces
{
	private static final Set<OrderData> ORDER_DATA = new HashSet<>();
	
	public static void structures(@NotNull BootstrapContext<Structure> context)
	{
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
		
		ORDER_DATA.forEach(orderData ->
		{
			context.register(orderData.getStructureInfo(StructureType.HEADQUARTERS).
					structure(),
					new JigsawStructure(
							new Structure.StructureSettings.Builder(biomes.getOrThrow(BioBiomesTags.HAS_SRF_HEADQUARTERS)).
									terrainAdapation(TerrainAdjustment.BEARD_THIN).
									spawnOverrides(Map.of(
											MobCategory.CREATURE,
											new StructureSpawnOverride(
													StructureSpawnOverride.BoundingBoxType.STRUCTURE,
													WeightedRandomList.create(
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_SOLDIER.getEntityHolder().get(), 90, 6, 8),
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_BLACKSMITH.getEntityHolder().get(), 4, 1, 1),
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_SERGEANT.getEntityHolder().get(), 4, 2, 2),
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_CAPTAIN.getEntityHolder().get(), 2, 1, 1))))).
									build(),
							pools.getOrThrow(orderData.pools.getStartPool(StructureType.HEADQUARTERS)),
							3,
							ConstantHeight.of(VerticalAnchor.absolute(-1)),
							true,
							Heightmap.Types.WORLD_SURFACE_WG));
			
			context.register(orderData.getStructureInfo(StructureType.TOWER).
							structure(),
					new JigsawStructure(
							new Structure.StructureSettings.Builder(biomes.getOrThrow(BioBiomesTags.HAS_SRF_TOWER)).
									terrainAdapation(TerrainAdjustment.BEARD_THIN).
									spawnOverrides(Map.of(
											MobCategory.CREATURE,
											new StructureSpawnOverride(
													StructureSpawnOverride.BoundingBoxType.STRUCTURE,
													WeightedRandomList.create(
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_SOLDIER.getEntityHolder().get(), 90, 4, 4),
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_BLACKSMITH.getEntityHolder().get(), 4, 1, 1),
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_SERGEANT.getEntityHolder().get(), 4, 2, 2),
															new MobSpawnSettings.SpawnerData(
																	Registration.EntityReg.MOB_BASE_CAPTAIN.getEntityHolder().get(), 2, 1, 1))))).
									build(),
							pools.getOrThrow(orderData.pools.getStartPool(StructureType.TOWER)),
							3,
							ConstantHeight.of(VerticalAnchor.absolute(0)),
							true,
							Heightmap.Types.WORLD_SURFACE_WG));
		});
		
	}
	
	public static void structureSets(@NotNull BootstrapContext<StructureSet> context)
	{
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
		
		ORDER_DATA.forEach(orderData ->
		{
			context.register(orderData.getStructureInfo(StructureType.HEADQUARTERS).structureSet(),
					new StructureSet(structures.getOrThrow(orderData.getStructureInfo(StructureType.HEADQUARTERS).structure()),
							new RandomSpreadStructurePlacement(
									Vec3i.ZERO,
									StructurePlacement.FrequencyReductionMethod.DEFAULT,
									0.25f,
									583947216,
									Optional.empty(),
									32,
									8,
									RandomSpreadType.LINEAR)));
			
			context.register(orderData.getStructureInfo(StructureType.TOWER).structureSet(),
					new StructureSet(structures.getOrThrow(orderData.getStructureInfo(StructureType.TOWER).structure()),
							new RandomSpreadStructurePlacement(
									Vec3i.ZERO,
									StructurePlacement.FrequencyReductionMethod.DEFAULT,
									0.75f,
									914362805,
									Optional.empty(),
									32,
									8,
									RandomSpreadType.LINEAR)));
		});
	}
	
	public static void templatePools(@NotNull BootstrapContext<StructureTemplatePool> context)
	{
		HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
		Holder<StructureTemplatePool> empty = pools.getOrThrow(Pools.EMPTY);
		
		ORDER_DATA.forEach(orderData ->
		{
			for (OrderPoolType type : OrderPoolType.values())
			{
				Pair<ResourceKey<StructureTemplatePool>, ResourceLocation> poolLocPair = orderData.getPools().pools.get(type);
				context.register(poolLocPair.getFirst(),
						new StructureTemplatePool(
								empty,
								ImmutableList.of(
										Pair.of(StructurePoolElement.legacy(poolLocPair.getSecond().toString()), 1)),
								StructureTemplatePool.Projection.RIGID));
			}
			
			for (StructureType type : StructureType.values())
			{
				Pair<ResourceKey<StructureTemplatePool>, ResourceLocation> poolLocPair = orderData.getPools().startPools.get(type);
				context.register(poolLocPair.getFirst(),
						new StructureTemplatePool(
								empty,
								ImmutableList.of(
										Pair.of(StructurePoolElement.legacy(poolLocPair.getSecond().toString()), 1)),
								StructureTemplatePool.Projection.RIGID));
			}
		});
	}
	
	public static void init()
	{
		PalladinOrders.FULL_ORDERS.forEach(palladinOrderKey ->
				ORDER_DATA.add(new OrderData(palladinOrderKey.location())));
	}
	
	
	private static class OrderData
	{
		private final Map<StructureType, StructureInfo> orderData = new EnumMap<>(StructureType.class);
		private final OrderPools pools;
		
		OrderData(ResourceLocation location)
		{
			for (StructureType type : StructureType.values())
			{
				this.orderData.put(type, new StructureInfo(type, location));
			}
			this.pools = new OrderPools(location);
		}
		
		private StructureInfo getStructureInfo(StructureType type)
		{
			return this.orderData.get(type);
		}
		
		public OrderPools getPools()
		{
			return this.pools;
		}
	}
	
	private record StructureInfo(ResourceKey<Structure> structure, ResourceKey<StructureSet> structureSet)
	{
		StructureInfo(@NotNull StructureType structureType, @NotNull ResourceLocation location)
		{
			this(ResourceKey.create(Registries.STRUCTURE, location.withPrefix("srf/orders/").withSuffix("/" + structureType.name().toLowerCase())),
					ResourceKey.create(Registries.STRUCTURE_SET, location.withPrefix("srf/orders/").withSuffix("/" + structureType.name().toLowerCase())));
		}
	}
	
	private static class OrderPools
	{
		private final Map<OrderPoolType, Pair<ResourceKey<StructureTemplatePool>, ResourceLocation>> pools = new EnumMap<>(OrderPoolType.class);
		private final Map<StructureType, Pair<ResourceKey<StructureTemplatePool>, ResourceLocation>> startPools = new EnumMap<>(StructureType.class);
		
		OrderPools(ResourceLocation orderLocation)
		{
			for (OrderPoolType poolType : OrderPoolType.values())
				this.pools.put(poolType, Pair.of(ResourceKey.create(Registries.TEMPLATE_POOL,
								orderLocation.withPrefix("srf/orders/").
										withSuffix("/" + poolType.name().toLowerCase())),
						orderLocation.withPrefix("srf/orders/").
								withSuffix("/mobs/" + poolType.name().toLowerCase())));
			
			for (StructureType type : StructureType.values())
				this.startPools.put(type, Pair.of(ResourceKey.create(Registries.TEMPLATE_POOL,
						orderLocation.withPrefix("srf/orders/").
								withSuffix("/start/" + type.name().toLowerCase())),
						orderLocation.withPrefix("srf/orders/").
								withSuffix("/start/" + type.name().toLowerCase())));
		}
		
		private ResourceKey<StructureTemplatePool> getStartPool(StructureType structureType)
		{
			return this.startPools.get(structureType).getFirst();
		}
	}
	
	private enum StructureType
	{
		HEADQUARTERS,
		//CASTLE,
		TOWER;
	}
	
	private enum OrderPoolType
	{
		SOLDIER,
		SERGEANT,
		CAPTAIN,
		BLACKSMITH;
	}
}
