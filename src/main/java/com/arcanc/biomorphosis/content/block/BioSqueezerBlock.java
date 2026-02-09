/**
 * @author ArcAnc
 * Created at: 30.11.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;


import com.arcanc.biomorphosis.content.block.block_entity.BioSqueezer;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.biomorphosis.util.helper.VoxelShapeHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class BioSqueezerBlock extends BioNorphDependentBlock<BioSqueezer>
{
	public static final MapCodec<BioSqueezerBlock> CODEC = simpleCodec(BioSqueezerBlock :: new);
	public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;
	
	private static final VoxelShape SHAPE = Shapes.or(
			Shapes.box(0, 0, 0, 1, 0.0625, 1),
			Shapes.box(0.125, 0.0625, 0.125, 0.875, 0.375, 0.875),
			Shapes.box(0.0625, 0.0625, 0.0625, 0.1875, 0.3125, 0.1875),
			Shapes.box(0.09375, 0.25, 0.09375, 0.15625, 0.6875, 0.15625),
			Shapes.box(0.066011875, 0.633996875, 0.064826875, 0.191011875, 0.758996875, 0.189826875),
			Shapes.box(0.097261875, 0.758996875, 0.096076875, 0.159761875, 1.008996875, 0.158576875),
			Shapes.box(0.79735125, 0.0583575, 0.7975325, 0.92235125, 0.3083575, 0.9225325),
			Shapes.box(0.82860125, 0.2458575, 0.8287825, 0.89110125, 0.6833575, 0.8912825),
			Shapes.box(0.800863125, 0.629854375, 0.799859375, 0.925863125, 0.754854375, 0.924859375),
			Shapes.box(0.832113125, 0.754854375, 0.831109375, 0.894613125, 1.004854375, 0.893609375),
			Shapes.box(0.0625, 0.0625, 0.75, 0.1875, 0.3125, 0.875),
			Shapes.box(0.09375, 0.25, 0.78125, 0.15625, 0.6875, 0.84375),
			Shapes.box(0.066011875, 0.633996875, 0.752326875, 0.191011875, 0.758996875, 0.877326875),
			Shapes.box(0.097261875, 0.758996875, 0.783576875, 0.159761875, 1.008996875, 0.846076875),
			Shapes.box(0.75, 0.0625, 0.0625, 0.875, 0.3125, 0.1875),
			Shapes.box(0.78125, 0.25, 0.09375, 0.84375, 0.6875, 0.15625),
			Shapes.box(0.753511875, 0.633996875, 0.064826875, 0.878511875, 0.758996875, 0.189826875),
			Shapes.box(0.784761875, 0.758996875, 0.096076875, 0.847261875, 1.008996875, 0.158576875),
			Shapes.box(0.1875, 0.4375, 0.1875, 0.8125, 0.5625, 0.3125),
			Shapes.box(0.1875, 0.4375, 0.3125, 0.3125, 0.5625, 0.6875),
			Shapes.box(0.6875, 0.4375, 0.3125, 0.8125, 0.5625, 0.6875),
			Shapes.box(0.1875, 0.4375, 0.6875, 0.8125, 0.5625, 0.8125),
			Shapes.box(0.25, 0.375, 0.625, 0.75, 0.4375, 0.75),
			Shapes.box(0.25, 0.375, 0.25, 0.75, 0.4375, 0.375),
			Shapes.box(0.625, 0.375, 0.375, 0.75, 0.4375, 0.625),
			Shapes.box(0.25, 0.375, 0.375, 0.375, 0.4375, 0.625));
	
	private static final Map<Direction, VoxelShape> BY_DIRECTION = new EnumMap<>(Direction.class);
	
	public BioSqueezerBlock(Properties blockProps)
	{
		super(Registration.BETypeReg.BE_SQUEEZER, blockProps);
		BY_DIRECTION.put(Direction.NORTH, SHAPE);
		BY_DIRECTION.put(Direction.SOUTH, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.SOUTH));
		BY_DIRECTION.put(Direction.WEST, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.WEST));
		BY_DIRECTION.put(Direction.EAST, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.EAST));
	}
	
	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack,
	                                                   @NotNull BlockState state,
	                                                   @NotNull Level level,
	                                                   @NotNull BlockPos pos,
	                                                   @NotNull Player player,
	                                                   @NotNull InteractionHand hand,
	                                                   @NotNull BlockHitResult hitResult)
	{
		if (FluidHelper.isFluidHandler(stack))
		{
			if (level.isClientSide())
				return ItemInteractionResult.SUCCESS;
			else
				return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection()) ?
						ItemInteractionResult.CONSUME :
						ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
	
	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
	                                                    @NotNull Level level,
	                                                    @NotNull BlockPos pos,
	                                                    @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult)
	{
		if (player.isShiftKeyDown())
		{
			if (ItemHelper.isItemHandler(level, pos))
				if (level.isClientSide())
					return InteractionResult.SUCCESS;
				else
				{
					player.addItem(ItemHelper.getItemHandler(level, pos).
							map(handler -> handler.extractItem(0, Integer.MAX_VALUE, false)).
							orElse(ItemStack.EMPTY));
					return InteractionResult.CONSUME;
				}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}
	
	@Override
	protected BlockState getInitDefaultState()
	{
		BlockState state = super.getInitDefaultState();
		if (state.hasProperty(HORIZONTAL_FACING))
			state = state.setValue(HORIZONTAL_FACING, Direction.NORTH);
		return state;
	}
	
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
	{
		Direction dir = context.getHorizontalDirection();
		return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, dir.getOpposite());
	}
	
	@Override
	protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
	}
	
	@Override
	protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation)
	{
		return BlockHelper.nextHorizontalDirection(state);
	}
	
	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state,
	                                       @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos,
	                                       @NotNull CollisionContext context)
	{
		return BY_DIRECTION.get(state.getValue(HORIZONTAL_FACING));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
	{
		builder.add(HORIZONTAL_FACING);
	}
	
	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
	{
		return RenderShape.INVISIBLE;
	}

	@Override
	protected @NotNull MapCodec<BioSqueezerBlock> codec()
	{
		return CODEC;
	}
	
	@Override
	protected void onRemove(@NotNull BlockState state,
	                        @NotNull Level level,
	                        @NotNull BlockPos pos,
	                        @NotNull BlockState newState,
	                        boolean movedByPiston)
	{
		if (!state.is(newState.getBlock()))
		{
			BlockHelper.castTileEntity(level, pos, BioSqueezer.class).
					map(squeezer -> BioSqueezer.getItemHandler(squeezer, null)).
					ifPresentOrElse(storage ->
					{
						if (level instanceof ServerLevel serverLevel)
							ItemHelper.dropContents(serverLevel, pos, storage);
						super.onRemove(state, level, pos, newState, movedByPiston);
						level.updateNeighbourForOutputSignal(pos, this);
					},
					() -> super.onRemove(state, level, pos, newState, movedByPiston));
		}
	}
}
