/**
 * @author ArcAnc
 * Created at: 01.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.spawner;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.List;
import java.util.Optional;

public class SpawnerStructure extends Structure
{
	private static final int MAX_TERRAIN_DELTA = 4;
	
	public static final MapCodec<SpawnerStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					settingsCodec(instance),
					ExtraCodecs.nonEmptyList(SpawnerSetup.CODEC.listOf()).
							fieldOf("setups").forGetter(spawner -> spawner.setups)
			).apply(instance, SpawnerStructure :: new));
	
	private final List<SpawnerSetup> setups;
	
	public SpawnerStructure(StructureSettings settings, List<SpawnerSetup> setups)
	{
		super(settings);
		this.setups = setups;
	}
	
	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context)
	{
		SpawnerSetup setup = selectSetup(context.random());
		ResourceLocation location = setup.getRandomVariantLocation(context.random());
		StructureTemplate template = context.structureTemplateManager().getOrCreate(location);
		Rotation rotation = Rotation.getRandom(context.random());
		Mirror mirror = context.random().nextBoolean() ? Mirror.NONE : Mirror.FRONT_BACK;
		Vec3i size = template.getSize();
		BlockPos pivot = new BlockPos(size.getX() / 2, 0, size.getZ() / 2);
		BlockPos chunkOrigin = context.chunkPos().getWorldPosition();
		BoundingBox box = template.getBoundingBox(chunkOrigin, rotation, pivot, mirror);
		Optional<Integer> surface = findSurfaceY(context, box);
		
		if (surface.isEmpty())
			return Optional.empty();
		
		int y = surface.get() - setup.sink();
		if (y < context.heightAccessor().getMinBuildHeight())
			return Optional.empty();
		
		BlockPos templatePos = new BlockPos(chunkOrigin.getX(), y, chunkOrigin.getZ());
		return Optional.of(new GenerationStub(templatePos, builder ->
				builder.addPiece(new Piece(
						context.structureTemplateManager(),
						templatePos,
						location,
						template,
						rotation,
						mirror,
						pivot))));
	}
	
	private SpawnerSetup selectSetup(RandomSource random)
	{
		float totalWeight = 0;
		for (SpawnerSetup setup : this.setups)
			totalWeight += setup.weight();
		
		float value = random.nextFloat() * totalWeight;
		for (SpawnerSetup setup : this.setups)
		{
			value -= setup.weight();
			if (value <= 0)
				return setup;
		}
		
		return this.setups.getLast();
	}
	
	private static Optional<Integer> findSurfaceY(GenerationContext context, BoundingBox box)
	{
		ChunkGenerator generator = context.chunkGenerator();
		RandomState randomState = context.randomState();
		
		int[][] points = {
				{box.minX(), box.minZ()},
				{box.maxX(), box.minZ()},
				{box.minX(), box.maxZ()},
				{box.maxX(), box.maxZ()},
				{box.getCenter().getX(), box.getCenter().getZ()}
		};
		
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		for (int[] point : points)
		{
			int x = point[0];
			int z = point[1];
			int y = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), randomState);
			int surfaceY = y - 1;
			if (surfaceY < context.heightAccessor().getMinBuildHeight())
				return Optional.empty();

			NoiseColumn column = generator.getBaseColumn(x, z, context.heightAccessor(), randomState);
			BlockState surface = column.getBlock(surfaceY);
			
			if (surface.isAir() || !surface.getFluidState().isEmpty())
				return Optional.empty();
			
			min = Math.min(min, y);
			max = Math.max(max, y);
		}
		
		if (max - min > MAX_TERRAIN_DELTA)
			return Optional.empty();
		
		return Optional.of(min);
	}
	
	@Override
	public StructureType<?> type()
	{
		return Registration.StructureTypeReg.SPAWNER_STRUCTURE.get();
	}
	
	public record SpawnerSetup(
			ResourceLocation template,
			int variants,
			int sink,
			float weight
	)
	{
		public static final Codec<SpawnerSetup> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						ResourceLocation.CODEC.fieldOf("template").forGetter(SpawnerSetup :: template),
						Codec.intRange(1, 1024).optionalFieldOf("variants", 1).forGetter(SpawnerSetup :: variants),
						Codec.intRange(-50, 50).fieldOf("sink").forGetter(SpawnerSetup :: sink),
						ExtraCodecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(SpawnerSetup :: weight)
				).apply(instance, SpawnerSetup :: new));
		
		private ResourceLocation getRandomVariantLocation(RandomSource random)
		{
			int variant = this.variants == 1 ? 0 : random.nextInt(this.variants);
			return this.template.withPath(path -> "spawner/" + path + "/" + variant);
		}
	}
	
	public static class Piece extends TemplateStructurePiece
	{
		public Piece(
				StructureTemplateManager structureTemplateManager,
				BlockPos templatePosition,
				ResourceLocation location,
				StructureTemplate template,
				Rotation rotation,
				Mirror mirror,
				BlockPos pivot)
		{
			super(
					Registration.StructurePieceTypeReg.SPAWNER_STRUCTURE.get(),
					0,
					structureTemplateManager,
					location,
					location.toString(),
					makeSettings(rotation, mirror, pivot),
					templatePosition);
			this.template = template;
		}
		
		public Piece(StructureTemplateManager structureTemplateManager, CompoundTag tag)
		{
			super(Registration.StructurePieceTypeReg.SPAWNER_STRUCTURE.get(), tag, structureTemplateManager, location -> makeSettings(tag, structureTemplateManager, location));
		}
		
		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag)
		{
			super.addAdditionalSaveData(context, tag);
			tag.putString("Rotation", this.placeSettings.getRotation().name());
			tag.putString("Mirror", this.placeSettings.getMirror().name());
		}
		
		private static StructurePlaceSettings makeSettings(CompoundTag tag, StructureTemplateManager structureTemplateManager, ResourceLocation location)
		{
			StructureTemplate template = structureTemplateManager.getOrCreate(location);
			BlockPos pivot = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
			return makeSettings(Rotation.valueOf(tag.getString("Rotation")), Mirror.valueOf(tag.getString("Mirror")), pivot);
		}
		
		private static StructurePlaceSettings makeSettings(Rotation rotation, Mirror mirror, BlockPos pivot)
		{
			return new StructurePlaceSettings().
					setRotation(rotation).
					setMirror(mirror).
					setRotationPivot(pivot).
					addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK).
					addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE));
		}
		
		@Override
		protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box)
		{
		
		}
	}
}
