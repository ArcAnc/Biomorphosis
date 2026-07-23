/**
 * @author ArcAnc
 * Created at: 14.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base.type;

import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class DynamicMultiblockPartBlock<T extends DynamicMultiblockPart> extends MultiblockPartBlock<T>
{
    public DynamicMultiblockPartBlock(Supplier<BlockEntityType<T>> tileType, Properties props)
    {
        super(MultiblockType.DYNAMIC, tileType, props);
    }

    @Override
    protected void onPlace(BlockState state,
                           Level level,
                           BlockPos pos,
                           BlockState oldState,
                           boolean movedByPiston)
    {
        if (level instanceof ServerLevel serverLevel)
            BlockHelper.castTileEntity(level, pos, DynamicMultiblockPart.class).ifPresent(part -> part.onPlace(serverLevel, pos, state));
    }

    @Override
    protected void onRemove(BlockState state,
                            Level level,
                            BlockPos pos,
                            BlockState newState,
                            boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel)
            BlockHelper.castTileEntity(level, pos, DynamicMultiblockPart.class).ifPresent(part -> part.onRemove(serverLevel, pos, state));
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
