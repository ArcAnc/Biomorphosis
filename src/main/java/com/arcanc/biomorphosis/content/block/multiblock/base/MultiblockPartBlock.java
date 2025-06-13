/**
 * @author ArcAnc
 * Created at: 10.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base;

import com.arcanc.biomorphosis.content.block.BioNorphDependentBlock;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class MultiblockPartBlock<T extends BioMultiblockPart> extends BioNorphDependentBlock<T>
{
    public static final EnumProperty<MultiblockState> STATE = BlockHelper.BlockProperties.MULTIBLOCK_STATE;
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;
    private final MultiblockType type;

    public MultiblockPartBlock(MultiblockType type, Supplier<BlockEntityType<T>> tileType, Properties props)
    {
        super(tileType, props);
        this.type = type;
    }

    public MultiblockType getMultiblockType()
    {
        return this.type;
    }

    public boolean isFormed(@NotNull BlockState state)
    {
        return state.hasProperty(STATE) && state.getValue(STATE) == MultiblockState.FORMED;
    }

    public boolean isMorphed(@NotNull BlockState state)
    {
        return state.hasProperty(STATE) && state.getValue(STATE) == MultiblockState.MORPHING;
    }

    @Override
    protected BlockState getInitDefaultState()
    {
        BlockState state = super.getInitDefaultState();

        if (state.hasProperty(STATE))
            state = state.setValue(STATE, MultiblockState.DISASSEMBLED);
        if (state.hasProperty(HORIZONTAL_FACING))
            state = state.setValue(HORIZONTAL_FACING, Direction.NORTH);
        return state;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state.hasProperty(STATE))
            state = state.setValue(STATE, MultiblockState.DISASSEMBLED);
        if (state.hasProperty(HORIZONTAL_FACING))
            state = state.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
        return state;
    }

    protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror)
    {
        if (state.hasProperty(HORIZONTAL_FACING))
            return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
        return state;
    }

    @Override
    protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation)
    {
        return BlockHelper.nextHorizontalDirection(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(STATE);
    }
}
