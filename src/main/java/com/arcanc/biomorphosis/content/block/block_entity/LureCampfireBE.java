/**
 * @author ArcAnc
 * Created at: 31.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.block_entity.tick.ClientTickableBE;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LureCampfireBE extends AnimatedBlockEntity implements ClientTickableBE
{
    public final AnimationState rotateShaft = new AnimationState();

    public LureCampfireBE(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_LURE_CAMPFIRE.get(), pos, blockState);
    }

    public UsingResult addMeat(ItemStack stack, Player player)
    {
        return new UsingResult(stack, false);
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        if (descrPacket)
        {
            rotateShaft.start(getAge());
        }
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {

    }

    @Override
    public void tickClient()
    {
        age++;
    }

    public record UsingResult(ItemStack stack, boolean added)
    {
    }
}
