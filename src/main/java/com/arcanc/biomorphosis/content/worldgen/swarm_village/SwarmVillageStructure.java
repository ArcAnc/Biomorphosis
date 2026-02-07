/**
 * @author ArcAnc
 * Created at: 23.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.swarm_village;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SwarmVillageStructure extends Structure
{
	public static final DimensionPadding DEFAULT_DIMENSION_PADDING = DimensionPadding.ZERO;
	public static final LiquidSettings DEFAULT_LIQUID_SETTINGS = LiquidSettings.APPLY_WATERLOGGING;
	public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
	public static final int MIN_DEPTH = 0;
	public static final int MAX_DEPTH = 20;
	
	public static final MapCodec<SwarmVillageStructure> CODEC = RecordCodecBuilder.<SwarmVillageStructure>mapCodec(
			instance -> instance.group(
					settingsCodec(instance),
					StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
					ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
					Codec.intRange(MIN_DEPTH, MAX_DEPTH).fieldOf("size").forGetter(structure -> structure.maxDepth),
					HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
					Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
					Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
					Codec.intRange(1, MAX_TOTAL_STRUCTURE_RANGE).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
					Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
					DimensionPadding.CODEC
							.optionalFieldOf("dimension_padding", DEFAULT_DIMENSION_PADDING)
							.forGetter(structure -> structure.dimensionPadding),
					LiquidSettings.CODEC.optionalFieldOf("liquid_settings", DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
			).apply(instance, SwarmVillageStructure :: new)).
			validate(SwarmVillageStructure :: verifyRange);
	
	
	private final Holder<StructureTemplatePool> startPool;
	private final Optional<ResourceLocation> startJigsawName;
	private final int maxDepth;
	private final HeightProvider startHeight;
	private final boolean useExpansionHack;
	private final Optional<Heightmap.Types> projectStartToHeightmap;
	private final int maxDistanceFromCenter;
	private final List<PoolAliasBinding> poolAliases;
	private final DimensionPadding dimensionPadding;
	private final LiquidSettings liquidSettings;
	
	private static DataResult<SwarmVillageStructure> verifyRange(@NotNull SwarmVillageStructure structure)
	{
		int i = switch (structure.terrainAdaptation())
		{
			case NONE -> 0;
			case BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE -> 12;
		};
		return structure.maxDistanceFromCenter + i > MAX_TOTAL_STRUCTURE_RANGE
					   ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128")
					   : DataResult.success(structure);
	}
	
	public SwarmVillageStructure(
			Structure.StructureSettings settings,
			Holder<StructureTemplatePool> startPool,
			Optional<ResourceLocation> startJigsawName,
			int maxDepth,
			HeightProvider startHeight,
			boolean useExpansionHack,
			Optional<Heightmap.Types> projectStartToHeightmap,
			int maxDistanceFromCenter,
			List<PoolAliasBinding> poolAliases,
			DimensionPadding dimensionPadding,
			LiquidSettings liquidSettings)
	{
		super(settings);
		this.startPool = startPool;
		this.startJigsawName = startJigsawName;
		this.maxDepth = maxDepth;
		this.startHeight = startHeight;
		this.useExpansionHack = useExpansionHack;
		this.projectStartToHeightmap = projectStartToHeightmap;
		this.maxDistanceFromCenter = maxDistanceFromCenter;
		this.poolAliases = poolAliases;
		this.dimensionPadding = dimensionPadding;
		this.liquidSettings = liquidSettings;
	}
	
	public SwarmVillageStructure(
			Structure.StructureSettings settings,
			Holder<StructureTemplatePool> startPool,
			int maxDepth,
			HeightProvider startHeight,
			boolean useExpansionHack,
			Heightmap.Types projectStartToHeightmap
	) {
		this(
				settings,
				startPool,
				Optional.empty(),
				maxDepth,
				startHeight,
				useExpansionHack,
				Optional.of(projectStartToHeightmap),
				80,
				List.of(),
				DEFAULT_DIMENSION_PADDING,
				DEFAULT_LIQUID_SETTINGS
		);
	}
	
	@Override
	protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context)
	{
		ChunkPos chunkpos = context.chunkPos();
		int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
		BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());
		return JigsawPlacement.addPieces(
				context,
				this.startPool,
				this.startJigsawName,
				this.maxDepth,
				blockpos,
				this.useExpansionHack,
				this.projectStartToHeightmap,
				this.maxDistanceFromCenter,
				PoolAliasLookup.create(this.poolAliases, blockpos, context.seed()),
				this.dimensionPadding,
				this.liquidSettings
		);
	}
	
	@Override
	public void afterPlace(@NotNull WorldGenLevel level,
						   @NotNull StructureManager structureManager,
						   @NotNull ChunkGenerator chunkGenerator,
						   @NotNull RandomSource random,
						   @NotNull BoundingBox boundingBox,
						   @NotNull ChunkPos chunkPos,
						   @NotNull PiecesContainer pieces)
	{
		for (StructurePiece piece : pieces.pieces())
		{
			if (!piece.getBoundingBox().intersects(boundingBox))
				continue;
			
			BoundingBox box = piece.getBoundingBox();
			BoundingBox inflated = new BoundingBox(box.minX() - 15, box.minY() - 5, box.minZ() - 15, box.maxX() + 15, box.maxY() + 5, box.maxZ() + 15);
			
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			
			for (int x = inflated.minX(); x <= inflated.maxX(); x++)
			{
				for (int z = inflated.minZ(); z <= inflated.maxZ(); z++)
				{
					pos.set(x, inflated.minY(), z);
					if (level.isAreaLoaded(pos, 1))
					{
						for (int y = inflated.minY(); y <= inflated.maxY(); y++)
						{
							pos.setY(y);
							if (level.getBlockState(pos).is(BlockTags.DIRT))
								level.setBlock(pos, Registration.BlockReg.NORPHED_DIRT_0.get().defaultBlockState(), 2);
						}
					}
				}
			}
		}
	}
	
	
	
	@Override
	public @NotNull StructureType<?> type()
	{
		return Registration.StructureTypeReg.SWARM_VILLAGE_TYPE.get();
	}
}
