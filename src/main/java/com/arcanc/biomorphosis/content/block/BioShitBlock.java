/**
 * @author ArcAnc
 * Created at: 19.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BioShitBlock extends BioBaseBlock
{
	public static final MapCodec<BioShitBlock> CODEC = simpleCodec(BioShitBlock :: new);
	
	public static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);
	
	public BioShitBlock(Properties props)
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
		return !level.isEmptyBlock(pos.below());
	}
	
	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}
	
	@Override
	protected MapCodec<BioShitBlock> codec()
	{
		return CODEC;
	}
}
