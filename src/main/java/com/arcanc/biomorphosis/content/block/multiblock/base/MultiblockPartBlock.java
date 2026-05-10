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
import com.arcanc.biomorphosis.content.block.multiblock.base.type.DynamicMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class MultiblockPartBlock<T extends BioMultiblockPart> extends BioNorphDependentBlock<T>
{
    public static final EnumProperty<MultiblockState> STATE = BlockHelper.BlockProperties.MULTIBLOCK_STATE;
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;
    private final MultiblockType type;

    public MultiblockPartBlock(MultiblockType type, Supplier<BlockEntityType<T>> tileType, Properties props)
    {
        super(tileType, props.dynamicShape());
        this.type = type;
    }

    public MultiblockType getMultiblockType()
    {
        return this.type;
    }

    public boolean isFormed(BlockState state)
    {
        return state.hasProperty(STATE) && state.getValue(STATE) == MultiblockState.FORMED;
    }

    public boolean isMorphed(BlockState state)
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
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state.hasProperty(STATE))
            state = state.setValue(STATE, MultiblockState.DISASSEMBLED);
        if (state.hasProperty(HORIZONTAL_FACING))
            state = state.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
        return state;
    }

    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.hasProperty(HORIZONTAL_FACING))
            return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockHelper.nextHorizontalDirection(state);
    }
    
    @Override
    protected VoxelShape getShape(BlockState state,
                                  BlockGetter level,
                                  BlockPos pos,
                                  CollisionContext context)
    {
		BioMultiblockPart part = BlockHelper.castTileEntity(level, pos, BioMultiblockPart.class).orElse(null);
	    return switch (part)
	    {
		    //FIXME: Это временная заплатка, для динамики нужно придумать другую форму. Если она вообще имеет смысл
		    case DynamicMultiblockPart dynamicMultiblockPart -> Shapes.block();
		    case StaticMultiblockPart staticPart ->
				    staticPart.getVoxelShape(state.hasProperty(HORIZONTAL_FACING) ? state.getValue(HORIZONTAL_FACING) : Direction.NORTH);
		    case null, default -> Shapes.block();
	    };
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(STATE);
    }
}
