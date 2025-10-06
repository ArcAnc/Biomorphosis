/**
 * @author ArcAnc
 * Created at: 28.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.swarm_village;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SwarmVillageFloorProcessor extends StructureProcessor
{
	public static final MapCodec<SwarmVillageFloorProcessor> CODEC = MapCodec.unit(SwarmVillageFloorProcessor :: new);
	
	@Override
	public @Nullable StructureTemplate.StructureBlockInfo process(@NotNull LevelReader serverLevel,
	                                                              @NotNull BlockPos offset,
	                                                              @NotNull BlockPos pos,
	                                                              StructureTemplate.@NotNull StructureBlockInfo blockInfo,
	                                                              StructureTemplate.@NotNull StructureBlockInfo relativeBlockInfo,
	                                                              @NotNull StructurePlaceSettings settings,
	                                                              @Nullable StructureTemplate template)
	{
		BlockState state = relativeBlockInfo.state();
		if (state.is(Blocks.GRASS_BLOCK))
			return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), Registration.BlockReg.NORPHED_DIRT_0.get().defaultBlockState(), null);
		return relativeBlockInfo;
	}
	
	@Override
	protected @NotNull StructureProcessorType<?> getType()
	{
		return Registration.StructureProcessorTypeReg.VILLAGE_FLOOR_REPLACE.get();
	}
}
