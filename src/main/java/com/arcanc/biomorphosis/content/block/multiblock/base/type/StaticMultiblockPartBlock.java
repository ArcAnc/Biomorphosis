/**
 * @author ArcAnc
 * Created at: 10.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base.type;

import com.arcanc.biomorphosis.content.block.multiblock.base.BioMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StaticMultiblockPartBlock<T extends BioMultiblockPart> extends MultiblockPartBlock<T>
{
    private static final VoxelShape BASIC_SHAPE = Shapes.or(
            box(0,0,0, 16, 1, 16),
            box(0,0,0, 16, 8, 1),
            box(0,0,0, 1, 8, 16),
            box(15, 0, 0, 16, 8, 16),
            box(0, 0, 15, 16, 8, 16));

    public StaticMultiblockPartBlock(Supplier<BlockEntityType<T>> tileType, Properties props)
    {
        super(MultiblockType.STATIC, tileType, props);
    }

    @Override
    protected void onRemove(@NotNull BlockState state,
                            @NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState newState,
                            boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()))
            BlockHelper.castTileEntity(level, pos, StaticMultiblockPart.class).
                    ifPresent(StaticMultiblockPart :: onDisassemble);

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /*FIXME: add custom logic for shape, which must be received from multiblock definition, which means json*/
    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state,
                                           @NotNull BlockGetter level,
                                           @NotNull BlockPos pos,
                                           @NotNull CollisionContext context)
    {
        return state.getValue(STATE) == MultiblockState.FORMED ? super.getShape(state, level, pos, context) : BASIC_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_FACING);
    }
}
