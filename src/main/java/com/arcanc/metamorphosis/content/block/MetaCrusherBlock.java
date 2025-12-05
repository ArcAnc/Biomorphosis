/**
 * @author ArcAnc
 * Created at: 28.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block;

import com.arcanc.metamorphosis.content.block.block_entity.MetaCrusher;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.util.helper.BlockHelper;
import com.arcanc.metamorphosis.util.helper.FluidHelper;
import com.arcanc.metamorphosis.util.helper.ItemHelper;
import com.arcanc.metamorphosis.util.helper.VoxelShapeHelper;
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
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class MetaCrusherBlock extends MetaNorphDependentBlock<MetaCrusher>
{
    public static final MapCodec<MetaCrusherBlock> CODEC = simpleCodec(MetaCrusherBlock :: new);
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.1875, 0.125, 0.1875, 0.8125, 0.5625, 0.8125),
            Shapes.box(0, 0.5625, 0, 1, 0.6875, 1),
            Shapes.box(0.5625, 0.75, 0.125, 0.8125, 1, 0.875),
            Shapes.box(0.875, 0.6875, 0.0625, 1, 1.0625, 0.9375),
            Shapes.box(0, 0.6875, 0.9375, 1, 0.875, 1),
            Shapes.box(0.3125, 0.179126875, 0.03125, 0.6875, 0.366626875, 0.34375),
            Shapes.box(0.5625, 0.75, 0.0625, 0.8125, 1, 0.9375),
            Shapes.box(0.1875, 0.75, 0.0625, 0.4375, 1, 0.9375),
            Shapes.box(0.1875, 0.75, 0.125, 0.4375, 1, 0.875),
            Shapes.box(0, 0.6875, 0.0625, 0.125, 1.0625, 0.9375),
            Shapes.box(0, 0.6875, 0, 1, 0.875, 0.0625),
            Shapes.box(0, 0, 0, 1, 0.125, 1),
            Shapes.box(0.0625, 1, 0.5625, 0.125, 1.25, 0.625),
            Shapes.box(0.0625, 1, 0.1875, 0.125, 1.25, 0.25),
            Shapes.box(0.0625, 1.0625, 0.1875, 0.125, 1.25, 0.25),
            Shapes.box(0.0625, 1.0625, 0.5625, 0.125, 1.25, 0.625),
            Shapes.box(0.0625, 1.0625, 0.75, 0.125, 1.25, 0.8125),
            Shapes.box(0.0625, 1.0625, 0.375, 0.125, 1.25, 0.4375),
            Shapes.box(0.875, 1.0625, 0.75, 0.9375, 1.25, 0.8125),
            Shapes.box(0.875, 1.0625, 0.375, 0.9375, 1.25, 0.4375),
            Shapes.box(0.875, 1.0625, 0.1875, 0.9375, 1.25, 0.25),
            Shapes.box(0.875, 1.0625, 0.5625, 0.9375, 1.25, 0.625));

    private static final Map<Direction, VoxelShape> BY_DIRECTION = new EnumMap<>(Direction.class);

    public MetaCrusherBlock(Properties blockProps)
    {
        super(Registration.BETypeReg.BE_CRUSHER, blockProps);
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
        if (FluidHelper.isFluidHandler(stack))
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
    protected @NotNull MapCodec<MetaCrusherBlock> codec()
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
            BlockHelper.castTileEntity(level, pos, MetaCrusher.class).
                    map(crusher -> MetaCrusher.getItemHandler(crusher, null)).
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
