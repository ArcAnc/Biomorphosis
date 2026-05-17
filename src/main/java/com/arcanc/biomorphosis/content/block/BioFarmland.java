/**
 * @author ArcAnc
 * Created at: 12.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;

public class BioFarmland extends BioBaseBlock
{
	public static final MapCodec<BioFarmland> CODEC = simpleCodec(BioFarmland::new);
	
	public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
	
	protected static final VoxelShape SHAPE = Block.box(0.0, 1.0, 0.0, 16.0, 16.0, 16.0);
	
	public static final int MAX_MOISTURE = 7;
	
	public BioFarmland(Properties props)
	{
		super(props);
	}
	
	@Override
	protected BlockState getInitDefaultState()
	{
		BlockState state = super.getInitDefaultState();
		if (state.hasProperty(MOISTURE))
			state = state.setValue(MOISTURE, 0);
		return state;
	}
	
	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
	{
		if (direction == Direction.DOWN && !state.canSurvive(level, pos))
			level.scheduleTick(pos, this, 1);
		
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}
	
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		BlockState blockstate = level.getBlockState(pos.below());
		return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return !this.getInitDefaultState().canSurvive(context.getLevel(), context.getClickedPos())
				? BlockHelper.getRandomStateFromTag(BioBlockTags.NORPHED_BLOCKS, context.getLevel())
				: super.getStateForPlacement(context);
	}
	
	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	
	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}
	
	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		if (!state.canSurvive(level, pos))
			turnToDirt(null, state, level, pos);
	}
	
	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		int i = state.getValue(MOISTURE);
		if (!isNearBiomass(level, pos))
		{
			if (i > 0)
				level.setBlock(pos, state.setValue(MOISTURE, i - 1), 2);
			else if (!shouldMaintainFarmland(level, pos))
				turnToDirt(null, state, level, pos);
		} else if (i < 7)
			level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
	}
	
	public static void turnToDirt(@Nullable Entity entity, BlockState state, Level level, BlockPos pos)
	{
		BlockState blockstate = pushEntitiesUp(state, BlockHelper.getRandomStateFromTag(BioBlockTags.NORPHED_BLOCKS, level), level, pos);
		level.setBlockAndUpdate(pos, blockstate);
		level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
	}
	
	private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos)
	{
		return level.getBlockState(pos.below()).is(BioBlockTags.MAINTAINS_SWARM_FARMLAND);
	}
	
	private static boolean isNearBiomass(LevelReader level, BlockPos pos)
	{
		for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4)))
		{
			FluidState fluidState = level.getFluidState(blockpos);
			FluidType fluid = fluidState.getFluidType();
			if (fluid == Registration.FluidReg.BIOMASS.type().get())
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos)
	{
		return state.getValue(FarmBlock.MOISTURE) > 0;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(MOISTURE);
	}
	
	@Override
	protected MapCodec<BioFarmland> codec()
	{
		return CODEC;
	}
}
