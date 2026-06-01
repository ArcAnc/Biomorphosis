/**
 * @author ArcAnc
 * Created at: 19.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;


import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BioBushBlock extends BioBaseBlock
{
	public static final MapCodec<BioBushBlock> CODEC = simpleCodec(BioBushBlock :: new);
	
	public BioBushBlock(Properties props)
	{
		super(props);
	}
	
	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
	{
		return !state.canSurvive(level, pos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}
	
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		BlockState below = level.getBlockState(pos.below());
		return below.is(BioBlockTags.NORPHED_BLOCKS);
	}
	
	@Override
	protected MapCodec<BioBushBlock> codec()
	{
		return CODEC;
	}
}
