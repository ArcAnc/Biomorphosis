/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AnimatedBlockEntity extends BioBaseBlockEntity
{
    protected int age = 0;

    public AnimatedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    public int getAge()
    {
        return age;
    }

    @Override
    protected void firstTick()
    {

    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket) {

    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket) {

    }
}
