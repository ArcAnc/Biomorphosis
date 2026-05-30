/**
 * @author ArcAnc
 * Created at: 29.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.WorldGenHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class WastesSpireFeature extends Feature<NoneFeatureConfiguration>
{
	private static final int SPIRES_COUNT = 10;
	private static final int MAX_TERRAIN_DELTA = 4;
	private static final int MAX_FOUNDATION_DEPTH = 10;
	private static final int MAX_WATER_FOUNDATION_DEPTH = 48;
	private static final int FOUNDATION_SCAN_HEIGHT = 8;
	private static final int VILLAGE_PADDING = 6;

	public WastesSpireFeature(Codec<NoneFeatureConfiguration> codec)
	{
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
	{
		RandomSource random = context.random();
		ResourceLocation spire = Database.rl("spire/" + random.nextInt(SPIRES_COUNT));
		StructureTemplate template = context.level().getLevel().getStructureManager().getOrCreate(spire);

		Rotation rotation = Rotation.getRandom(random);
		BlockPos origin = context.origin();
		BlockPos templateSize = new BlockPos(template.getSize());
		BlockPos rotationPivot = new BlockPos(templateSize.getX() / 2, 0, templateSize.getZ() / 2);
		StructurePlaceSettings settings = new StructurePlaceSettings().
				setRotation(rotation).
				setRotationPivot(rotationPivot).
				setRandom(random).
				setIgnoreEntities(false).
				setKnownShape(true).
				addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
		TransformedBounds bounds = getTransformedBounds(templateSize, settings);
		BlockPos size = bounds.size();
		BlockPos footprintStart = new BlockPos(
				origin.getX() - size.getX() / 2,
				0,
				origin.getZ() - size.getZ() / 2);
		TerrainData terrain = collectTerrainData(context.level(), footprintStart, size);
		int y = terrain.maxY();

		if (!WorldGenHelper.isInsideBuildHeight(context.level(), y, size.getY()))
			return false;

		//if (terrain.maxY() - terrain.minY() > MAX_TERRAIN_DELTA)
		//	return false;

		BlockPos anchorPos = new BlockPos(
				footprintStart.getX() - bounds.minX(),
				y,
				footprintStart.getZ() - bounds.minZ());
		BlockPos supportStart = new BlockPos(
				footprintStart.getX(),
				y,
				footprintStart.getZ());
		BoundingBox footprint = new BoundingBox(
				supportStart.getX(),
				supportStart.getY() - VILLAGE_PADDING,
				supportStart.getZ(),
				supportStart.getX() + size.getX() - 1,
				supportStart.getY() + VILLAGE_PADDING,
				supportStart.getZ() + size.getZ() - 1);

		if (intersectsSwarmVillage(context.level(), footprint))
			return false;

		boolean placed = template.placeInWorld(context.level(), anchorPos, anchorPos, settings, random, 2);
		if (placed)
			fillFoundation(context.level(), supportStart, size, terrain);

		return placed;
	}

	private static TransformedBounds getTransformedBounds(BlockPos templateSize, StructurePlaceSettings settings)
	{
		int maxX = templateSize.getX() - 1;
		int maxZ = templateSize.getZ() - 1;
		BlockPos[] corners = new BlockPos[]
				{
						new BlockPos(0, 0, 0),
						new BlockPos(maxX, 0, 0),
						new BlockPos(0, 0, maxZ),
						new BlockPos(maxX, 0, maxZ)
				};
		int minTransformedX = Integer.MAX_VALUE;
		int minTransformedZ = Integer.MAX_VALUE;
		int maxTransformedX = Integer.MIN_VALUE;
		int maxTransformedZ = Integer.MIN_VALUE;

		for (BlockPos corner : corners)
		{
			BlockPos transformed = StructureTemplate.calculateRelativePosition(settings, corner);
			minTransformedX = Math.min(minTransformedX, transformed.getX());
			minTransformedZ = Math.min(minTransformedZ, transformed.getZ());
			maxTransformedX = Math.max(maxTransformedX, transformed.getX());
			maxTransformedZ = Math.max(maxTransformedZ, transformed.getZ());
		}

		return new TransformedBounds(
				minTransformedX,
				minTransformedZ,
				new BlockPos(maxTransformedX - minTransformedX + 1, templateSize.getY(), maxTransformedZ - minTransformedZ + 1));
	}

	private static TerrainData collectTerrainData(ServerLevelAccessor level, BlockPos footprintStart, BlockPos size)
	{
		int[][] heights = new int[size.getX()][size.getZ()];
		int[][] floorHeights = new int[size.getX()][size.getZ()];
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (int x = 0; x < size.getX(); x++)
		{
			for (int z = 0; z < size.getZ(); z++)
			{
				int height = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, footprintStart.getX() + x, footprintStart.getZ() + z);
				int floorHeight = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, footprintStart.getX() + x, footprintStart.getZ() + z);
				heights[x][z] = height;
				floorHeights[x][z] = floorHeight;
				minY = Math.min(minY, height);
				maxY = Math.max(maxY, height);
			}
		}

		return new TerrainData(heights, floorHeights, minY, maxY);
	}

	private static boolean intersectsSwarmVillage(ServerLevelAccessor level, BoundingBox footprint)
	{
		StructureManager structureManager = level.getLevel().structureManager();
		
		for (ChunkPos chunkPos : footprint.intersectingChunks().toList())
		{
			boolean intersects = structureManager.
					startsForStructure(chunkPos, structure -> structure.type() == Registration.StructureTypeReg.SWARM_VILLAGE_TYPE.get()).
					stream().
					anyMatch(start -> start.isValid() && start.getBoundingBox().intersects(footprint));

			if (intersects)
				return true;
		}

		return false;
	}

	private static void fillFoundation(ServerLevelAccessor level, BlockPos placePos, BlockPos size, TerrainData terrain)
	{
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		for (int x = 0; x < size.getX(); x++)
		{
			for (int z = 0; z < size.getZ(); z++)
			{
				int supportY = findLowestStructureBlock(level, placePos, x, z, size.getY(), mutablePos);
				if (supportY == Integer.MIN_VALUE)
					continue;

				int surfaceY = terrain.heights()[x][z];
				int floorY = terrain.floorHeights()[x][z];
				int minFoundationY = isWaterColumn(level, placePos.getX() + x, surfaceY, placePos.getZ() + z, mutablePos) ? floorY : surfaceY;
				int maxDepth = minFoundationY == floorY ? MAX_WATER_FOUNDATION_DEPTH : MAX_FOUNDATION_DEPTH;
				int minFillY = Math.max(minFoundationY, supportY - maxDepth);
				BlockState foundation = BuiltInRegistries.BLOCK.
						getRandomElementOf(BioBlockTags.NORPHED_BLOCKS, level.getRandom()).
						orElseThrow().
						value().
						defaultBlockState();
				
				for (int y = supportY - 1; y >= minFillY; y--)
				{
					mutablePos.set(placePos.getX() + x, y, placePos.getZ() + z);
					BlockState state = level.getBlockState(mutablePos);
					if (state.isAir() || !state.getFluidState().isEmpty())
						level.setBlock(mutablePos, foundation, 2);
				}
			}
		}
	}

	private static int findLowestStructureBlock(ServerLevelAccessor level, BlockPos placePos, int x, int z, int templateHeight, BlockPos.MutableBlockPos mutablePos)
	{
		int maxScanY = placePos.getY() + Math.min(templateHeight, FOUNDATION_SCAN_HEIGHT);

		for (int y = placePos.getY(); y < maxScanY; y++)
		{
			mutablePos.set(placePos.getX() + x, y, placePos.getZ() + z);
			BlockState state = level.getBlockState(mutablePos);
			if (!state.isAir())
				return y;
		}

		return Integer.MIN_VALUE;
	}

	private static boolean isWaterColumn(ServerLevelAccessor level, int x, int surfaceY, int z, BlockPos.MutableBlockPos mutablePos)
	{
		mutablePos.set(x, surfaceY - 1, z);
		return !level.getFluidState(mutablePos).isEmpty();
	}

	private record TerrainData(int[][] heights, int[][] floorHeights, int minY, int maxY)
	{
	}

	private record TransformedBounds(int minX, int minZ, BlockPos size)
	{
	}


}
