/**
 * @author ArcAnc
 * Created at: 04.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block;

import com.arcanc.metamorphosis.content.block.block_entity.MetaFluidTransmitter;
import com.arcanc.metamorphosis.util.helper.BlockHelper;
import com.arcanc.metamorphosis.util.helper.VoxelShapeHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class MetaFluidTransmitterBlock extends MetaNorphDependentBlock<MetaFluidTransmitter>
{
    public static final MapCodec<MetaFluidTransmitterBlock> CODEC = simpleCodec(MetaFluidTransmitterBlock :: new);
    public static final EnumProperty<Direction> FACING = BlockHelper.BlockProperties.FACING;

    private static final Map<Direction, VoxelShape> BY_DIRECTION = new EnumMap<>(Direction.class);
    private static final VoxelShape SHAPE = Shapes.or(
            box(4, 0, 4, 12, 2, 12),
            box(12, 0, 4, 14, 1, 12),
            box(2, 0, 4, 4, 1, 12),
            box(4, 0, 2, 12, 1, 4),
            box(4, 0, 12, 12, 1, 14),
            box(5, 2, 10, 6, 3, 11),
            box(11, 2, 8, 12, 3, 9),
            box(6, 2, 4, 7, 3, 5),
            box(4, 2, 6, 6, 4, 8),
            box(8, 2, 10, 10, 4, 12),
            box(8, 2, 4, 11, 5, 7),
            box(3, 1, 9, 4, 4, 10),
            box(9, 1, 3, 10, 4, 4),
            box(5, 1, 3, 6, 4, 4),
            box(12, 1, 5, 13, 4, 6),
            box(12, 1, 10, 13, 4, 11),
            box(6, 1, 12, 7, 4, 13));

    public MetaFluidTransmitterBlock(Properties blockProps)
    {
        super(MetaFluidTransmitter :: new, blockProps);

        BY_DIRECTION.put(Direction.DOWN, SHAPE);
        BY_DIRECTION.put(Direction.UP, VoxelShapeHelper.rotate(SHAPE, Direction.UP));
        BY_DIRECTION.put(Direction.NORTH, VoxelShapeHelper.rotate(SHAPE, Direction.NORTH));
        BY_DIRECTION.put(Direction.SOUTH, VoxelShapeHelper.rotate(SHAPE, Direction.SOUTH));
        BY_DIRECTION.put(Direction.EAST, VoxelShapeHelper.rotate(SHAPE, Direction.EAST));
        BY_DIRECTION.put(Direction.WEST, VoxelShapeHelper.rotate(SHAPE, Direction.WEST));
    }

    @Override
    protected BlockState getInitDefaultState()
    {
        BlockState state = super.getInitDefaultState();
        if (state.hasProperty(FACING))
            state = state.setValue(FACING, Direction.NORTH);
        return state;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        Direction dir = context.getClickedFace();
        return super.getStateForPlacement(context).setValue(FACING, dir.getOpposite());
    }

    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation)
    {
        return BlockHelper.nextDirection(state);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state,
                                           @NotNull BlockGetter level,
                                           @NotNull BlockPos pos,
                                           @NotNull CollisionContext context)
    {
        return BY_DIRECTION.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    protected @NotNull MapCodec<MetaFluidTransmitterBlock> codec()
    {
        return CODEC;
    }
}
