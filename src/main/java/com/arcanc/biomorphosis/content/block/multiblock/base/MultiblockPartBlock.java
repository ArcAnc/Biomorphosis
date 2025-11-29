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
import com.arcanc.biomorphosis.content.block.multiblock.base.role.IMultiblockRoleBehavior;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.DynamicMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.definition.BlockStateMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.arcanc.biomorphosis.util.helper.VoxelShapeHelper;
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

import java.util.Optional;
import java.util.function.Supplier;

public abstract class MultiblockPartBlock<T extends BioMultiblockPart> extends BioNorphDependentBlock<T>
{
    public static final EnumProperty<MultiblockState> STATE = BlockHelper.BlockProperties.MULTIBLOCK_STATE;
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;
    private final MultiblockType type;

    public MultiblockPartBlock(MultiblockType type, Supplier<BlockEntityType<T>> tileType, @NotNull Properties props)
    {
        super(tileType, props.dynamicShape());
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
    protected @NotNull VoxelShape getShape(@NotNull BlockState state,
                                           @NotNull BlockGetter level,
                                           @NotNull BlockPos pos,
                                           @NotNull CollisionContext context)
    {
		BioMultiblockPart part = BlockHelper.castTileEntity(level, pos, BioMultiblockPart.class).orElse(null);
        if (part == null)
            return Shapes.block();
        //FIXME: Это временная заплатка, для динамики нужно придумать другую форму. Если она вообще имеет смысл
		if (part instanceof DynamicMultiblockPart)
			return Shapes.block();
		
        IMultiblockRoleBehavior role = part.getRoleBehavior().orElse(null);
        if (role == null)
            return Shapes.block();
        
        BlockPos masterPos = role.getMasterPos().orElse(null);
        if (masterPos == null)
            return Shapes.block();
        
        BlockPos localPos = role.getLocalPos().orElse(null);
        if (localPos == null)
            return Shapes.block();
        
        BioMultiblockPart master = BlockHelper.castTileEntity(level, masterPos, BioMultiblockPart.class).orElse(null);
        if (master == null || master.definition == null)
            return Shapes.block();
        
        BlockState stateAtLocal = master.definition
                .getStructure(level, masterPos)
                .getStates()
                .get(localPos);
        
        return stateAtLocal != null ? stateAtLocal.getShape(level, localPos) : Shapes.block();
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(STATE);
    }
}
