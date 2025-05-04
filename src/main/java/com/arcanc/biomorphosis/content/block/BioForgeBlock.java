/**
 * @author ArcAnc
 * Created at: 03.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.BioForge;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.biomorphosis.util.helper.VoxelShapeHelper;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BioForgeBlock extends BioNorphDependentBlock<BioForge>
{
    public static final MapCodec<BioForgeBlock> CODEC = simpleCodec(BioForgeBlock :: new);
    public static final BooleanProperty DOUBLE = BlockHelper.BlockProperties.IS_DOUBLE;
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.125, 0.0625, 0, 0.875, 0.125, 1),
            Shapes.box(0.4375, 0.109375, 0.25, 0.5625, 0.609375, 0.375),
            Shapes.box(0.0625, 0.625, 0.0625, 0.9375, 0.625, 0.8125),
            Shapes.box(0.875, 0.125, 0.0625, 0.9375, 0.75, 0.875),
            Shapes.box(0.0625, 0.125, 0.0625, 0.125, 0.75, 0.875),
            Shapes.box(-0.03125, 0.703125, 0.05625000000000002, 0.03125, 0.953125, 0.86875),
            Shapes.box(0.96875, 0.703125, 0.05625000000000002, 1.03125, 0.953125, 0.86875),
            Shapes.box(0.375, 0.375, 0.1875, 0.625, 0.5625, 0.4375),
            Shapes.box(0, 0, 0, 1, 0.0625, 1),
            Shapes.box(0.0625, 0.5625, 0, 0.9375, 0.65625, 0.125),
            Shapes.box(0.0625, 0.5625, 0.75, 0.9375, 0.65625, 0.8125),
            Shapes.box(0, 0.09375, 0.4375, 0.1875, 0.15625, 0.5),
            Shapes.box(0, 0.09375, 0.6875, 0.1875, 0.15625, 0.75),
            Shapes.box(0, 0.09375, 0.1875, 0.1875, 0.15625, 0.25),
            Shapes.box(0.875, 0.03125, 0.4375, 0.9375, 0.21875, 0.5),
            Shapes.box(0.875, 0.03125, 0.1875, 0.9375, 0.21875, 0.25),
            Shapes.box(0.875, 0.03125, 0.6875, 0.9375, 0.21875, 0.75));
    private static final VoxelShape SHAPE_DOUBLE = Shapes.or(
            Shapes.box(0.125, 0.0625, 0, 0.875, 0.125, 1),
            Shapes.box(0.59375, 0.109375, 0.25, 0.71875, 0.609375, 0.375),
            Shapes.box(0.28125, 0.109375, 0.25, 0.40625, 0.609375, 0.375),
            Shapes.box(0.0625, 0.625, 0.0625, 0.9375, 0.625, 0.8125),
            Shapes.box(0.875, 0.125, 0.0625, 0.9375, 0.75, 0.875),
            Shapes.box(0.0625, 0.125, 0.0625, 0.125, 0.75, 0.875),
            Shapes.box(-0.03125, 0.703125, 0.05625000000000002, 0.03125, 0.953125, 0.86875),
            Shapes.box(0.96875, 0.703125, 0.05625000000000002, 1.03125, 0.953125, 0.86875),
            Shapes.box(0.53125, 0.125, 0.1875, 0.78125, 0.3125, 0.4375),
            Shapes.box(0.21875, 0.375, 0.1875, 0.46875, 0.5625, 0.4375),
            Shapes.box(0, 0, 0, 1, 0.0625, 1),
            Shapes.box(0.0625, 0.5625, 0, 0.9375, 0.65625, 0.125),
            Shapes.box(0.0625, 0.5625, 0.75, 0.9375, 0.65625, 0.8125),
            Shapes.box(0, 0.09375, 0.4375, 0.1875, 0.15625, 0.5),
            Shapes.box(0, 0.09375, 0.6875, 0.1875, 0.15625, 0.75),
            Shapes.box(0, 0.09375, 0.1875, 0.1875, 0.15625, 0.25),
            Shapes.box(0.8125, 0.09375, 0.4375, 1, 0.15625, 0.5),
            Shapes.box(0.8125, 0.09375, 0.1875, 1, 0.15625, 0.25),
            Shapes.box(0.8125, 0.09375, 0.6875, 1, 0.15625, 0.75),
            Shapes.box(0.09375, 0.03125, 0.84375, 0.90625, 0.65625, 0.90625));

    private static final List<Boolean> FLAGS = List.of(true, false);
    private static final List<Direction> DIRECTIONS = List.of(Direction.values());

    private static final Table<Direction, Boolean, VoxelShape> SHAPES = ArrayTable.create(DIRECTIONS, FLAGS);

    public BioForgeBlock(Properties props)
    {
        super(Registration.BETypeReg.BE_FORGE, props);

        for (Direction dir : DIRECTIONS)
            for (Boolean flag : FLAGS)
                SHAPES.put(dir, flag, getRotatedShape(dir, flag));
    }

    @Override
    protected BlockState getInitDefaultState()
    {
        BlockState state = super.getInitDefaultState();
        if (state.hasProperty(HORIZONTAL_FACING))
            state = state.setValue(HORIZONTAL_FACING, Direction.NORTH);
        if (state.hasProperty(DOUBLE))
            state = state.setValue(DOUBLE, false);
        return state;
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
        if (FluidHelper.isFluidHandler(stack))
        {
            if (level.isClientSide())
                return InteractionResult.SUCCESS;
            else
                return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection()) ?
                        InteractionResult.SUCCESS_SERVER :
                        super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        else if (stack.is(Registration.ItemReg.FORGE_UPGRADE))
        {
            if (!state.hasProperty(DOUBLE))
                return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
            boolean isDouble = state.getValue(DOUBLE);
            if (isDouble)
                return super.useItemOn(stack, state, level, pos, player, hand, hitResult);

            level.setBlockAndUpdate(pos, state.setValue(DOUBLE, true));
            player.getItemInHand(hand).shrink(1);
            
            return InteractionResult.SUCCESS;
        }
        else
        {
            if (level.isClientSide())
                return InteractionResult.SUCCESS;
            else
            {
                ItemStack returnedStack = ItemHelper.getItemHandler(level, pos).map(handler ->
                        ItemHandlerHelper.insertItemStacked(handler, stack, false)).
                        orElse(stack);
                if (ItemStack.matches(stack, returnedStack))
                    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
                else
                {
                    player.setItemInHand(hand, returnedStack);
                    return InteractionResult.SUCCESS_SERVER;
                }

            }
        }
    }


    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult)
    {
        if (player.isShiftKeyDown())
        {
            if (ItemHelper.isItemHandler(level, pos))
                if (level.isClientSide())
                    return InteractionResult.SUCCESS;
                else
                {
                    player.addItem(ItemHelper.getItemHandler(level, pos).
                            map(handler ->
                            {
                                ItemStack extracted = handler.extractItem(0, Integer.MAX_VALUE, false);
                                if (extracted.isEmpty())
                                    extracted = handler.extractItem(1, Integer.MAX_VALUE, false);
                                return extracted;
                            }).
                            orElse(ItemStack.EMPTY));
                    return InteractionResult.SUCCESS_SERVER;
                }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        Direction dir = context.getHorizontalDirection();
        return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, dir.getOpposite()).setValue(DOUBLE, false);
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

    public static boolean isDouble (@NotNull BlockState state)
    {
        return state.hasProperty(DOUBLE) ? state.getValue(DOUBLE) : false;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state,
                                           @NotNull BlockGetter level,
                                           @NotNull BlockPos pos,
                                           @NotNull CollisionContext context)
    {
        Direction dir = state.getValue(HORIZONTAL_FACING);
        boolean flag = state.getValue(DOUBLE);
        return Objects.requireNonNull(SHAPES.get(dir, flag));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, DOUBLE);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    private @NotNull VoxelShape getRotatedShape (Direction direction, boolean flag)
    {
        VoxelShape shape = flag ? SHAPE_DOUBLE : SHAPE;
        return direction == Direction.NORTH ? shape : VoxelShapeHelper.rotateHorizontal(shape, direction);
    }

    @Override
    protected @NotNull MapCodec<BioForgeBlock> codec()
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
            BlockHelper.castTileEntity(level, pos, BioForge.class).
                    map(forge -> BioForge.getItemHandler(forge, null)).
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
