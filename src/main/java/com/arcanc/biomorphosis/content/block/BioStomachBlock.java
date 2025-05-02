/**
 * @author ArcAnc
 * Created at: 19.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.BioStomach;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class BioStomachBlock extends BioNorphDependentBlock<BioStomach>
{
    public static final MapCodec<BioStomachBlock> CODEC = simpleCodec(BioStomachBlock :: new);
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.5, 0.625, 0.265625, 0.875, 1, 0.640625),
            Shapes.box(0, 0.265625, 0.390625, 0.4375, 0.703125, 0.828125),
            Shapes.box(0.125, 0.1875, 0.015625, 0.5, 0.5625, 0.390625),
            Shapes.box(0.9375, 0.0625, 0.203125, 1, 0.4375, 0.828125),
            Shapes.box(0, 0, 0.015625, 1, 0.0625, 1.015625),
            Shapes.box(0.4375, 0.0625, 0.796875, 0.5625, 0.5625, 0.921875),
            Shapes.box(0.84375, 0.59375, 0.203125, 0.90625, 1.03125, 0.265625),
            Shapes.box(0.78125, 0.71875, 0.203125, 0.84375, 0.78125, 0.265625),
            Shapes.box(0.78125, 0.84375, 0.203125, 0.84375, 0.90625, 0.265625),
            Shapes.box(0.59375, 0.90625, 0.203125, 0.65625, 0.96875, 0.265625),
            Shapes.box(0.71875, 0.90625, 0.203125, 0.78125, 0.96875, 0.265625),
            Shapes.box(0.46875, 0.96875, 0.1875, 0.90625, 1.03125, 0.25),
            Shapes.box(0.53125, 0.71875, 0.203125, 0.59375, 0.78125, 0.265625),
            Shapes.box(0.53125, 0.84375, 0.203125, 0.59375, 0.90625, 0.265625),
            Shapes.box(0.46875, 0.59375, 0.203125, 0.53125, 1.03125, 0.265625),
            Shapes.box(0.71875, 0.65625, 0.203125, 0.78125, 0.71875, 0.265625),
            Shapes.box(0.59375, 0.65625, 0.203125, 0.65625, 0.71875, 0.265625),
            Shapes.box(0.46875, 0.59375, 0.1875, 0.90625, 0.65625, 0.25),
            Shapes.box(0.0625, 0.1875, 0.203125, 0.4375, 0.25, 0.828125),
            Shapes.box(0.5, 0.0625, 0.140625, 1, 0.4375, 0.203125),
            Shapes.box(0.5, 0.0625, 0.828125, 1, 0.4375, 0.890625),
            Shapes.box(0.5, 0.0625, 0.203125, 0.5625, 0.4375, 0.828125),
            Shapes.box(0.21875, 0.0625, 0.140625, 0.34375, 0.4375, 0.265625),
            Shapes.box(0, 0.0625, 0.203125, 0.0625, 0.25, 0.828125),
            Shapes.box(0.0625, 0.0625, 0.203125, 0.5, 0.1875, 0.265625),
            Shapes.box(0.0625, 0.0625, 0.765625, 0.5, 0.1875, 0.828125),
            Shapes.box(0.3125, 0.4375, 0.578125, 0.75, 0.875, 1.015625),
            Shapes.box(0.78125, 0.84375, 0.140625, 0.84375, 0.90625, 0.203125),
            Shapes.box(0.78125, 0.71875, 0.140625, 0.84375, 0.78125, 0.203125),
            Shapes.box(0.84375, 0.59375, 0.140625, 0.90625, 1.03125, 0.203125),
            Shapes.box(0.53125, 0.71875, 0.140625, 0.59375, 0.78125, 0.203125),
            Shapes.box(0.53125, 0.84375, 0.140625, 0.59375, 0.90625, 0.203125),
            Shapes.box(0.46875, 0.59375, 0.140625, 0.53125, 1.03125, 0.203125),
            Shapes.box(0.59375, 0.90625, 0.140625, 0.65625, 0.96875, 0.203125),
            Shapes.box(0.71875, 0.90625, 0.140625, 0.78125, 0.96875, 0.203125),
            Shapes.box(0.46875, 0.96875, 0.125, 0.90625, 1.03125, 0.1875),
            Shapes.box(0.71875, 0.65625, 0.140625, 0.78125, 0.71875, 0.203125),
            Shapes.box(0.59375, 0.65625, 0.140625, 0.65625, 0.71875, 0.203125),
            Shapes.box(0.46875, 0.59375, 0.125, 0.90625, 0.65625, 0.1875));

    private static final Map<Direction, VoxelShape> BY_DIRECTION = new EnumMap<>(Direction.class);

    public BioStomachBlock(Properties blockProps)
    {
        super(Registration.BETypeReg.BE_STOMACH, blockProps);
        BY_DIRECTION.put(Direction.NORTH, SHAPE);
        BY_DIRECTION.put(Direction.SOUTH, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.SOUTH));
        BY_DIRECTION.put(Direction.WEST, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.WEST));
        BY_DIRECTION.put(Direction.EAST, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.EAST));
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack,
                                                   @NotNull BlockState state,
                                                   @NotNull Level level,
                                                   @NotNull BlockPos pos,
                                                   @NotNull Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult)
    {
        if (stack.isEmpty())
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (!FluidHelper.isFluidHandler(stack))
        {
            if (level.isClientSide())
                return InteractionResult.SUCCESS;
            else
                ItemHelper.getItemHandler(level, pos).map(handler ->
                {
                    ItemStack returned = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                    player.setItemInHand(hand, returned);
                    return InteractionResult.SUCCESS_SERVER;
                });
        }
        else
        {
            if (level.isClientSide())
                return InteractionResult.SUCCESS;
            else
                return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection()) ?
                        InteractionResult.SUCCESS_SERVER :
                        InteractionResult.TRY_WITH_EMPTY_HAND;
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
                    return InteractionResult.SUCCESS_SERVER;
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
    protected @NotNull MapCodec<BioStomachBlock> codec()
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
            BlockHelper.castTileEntity(level, pos, BioStomach.class).
                    map(stomach -> BioStomach.getItemHandler(stomach, null)).
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
