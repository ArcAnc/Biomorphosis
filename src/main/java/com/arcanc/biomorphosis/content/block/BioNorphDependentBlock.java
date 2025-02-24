/**
 * @author ArcAnc
 * Created at: 22.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.BioBaseBlockEntity;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BioNorphDependentBlock<T extends BioBaseBlockEntity> extends BioBaseEntityBlock<T>
{
    public BioNorphDependentBlock(BiFunction<BlockPos, BlockState, T> makeEntity, Properties props)
    {
        super(makeEntity, props);
    }

    public BioNorphDependentBlock(Supplier<BlockEntityType<T>> tileType, Properties blockProps)
    {
        super(tileType, blockProps);
    }

    @Override
    public void setPlacedBy(@NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState state,
                            @Nullable LivingEntity placer,
                            @NotNull ItemStack stack)
    {
        if (!isConnectedToNorph(level, pos, state))
            dropBlock(level, pos);
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state,
                                   @NotNull Level level,
                                   @NotNull BlockPos pos,
                                   @NotNull Block neighborBlock,
                                   @Nullable Orientation orientation,
                                   boolean movedByPiston)
    {
        if (!isConnectedToNorph(level, pos, state))
            dropBlock(level, pos);
    }

    private boolean isConnectedToNorph(@NotNull Level level,
                                       @NotNull BlockPos pos,
                                       @NotNull BlockState state)
    {
        if (state.is(Registration.BlockReg.NORPH_OVERLAY))
            return true;

        for (Direction direction : Direction.values())
        {
            BlockPos checkPos = pos.relative(direction);
            BlockState checkState = level.getBlockState(checkPos);
            if (checkState.is(BioBlockTags.NORPH))
                return true;
        }
        return false;
    }

    protected void dropBlock(@NotNull Level level, BlockPos pos)
    {
        level.destroyBlock(pos, true);
        level.sendBlockUpdated(pos, defaultBlockState(), level.getBlockState(pos), UPDATE_ALL);
    }
}
