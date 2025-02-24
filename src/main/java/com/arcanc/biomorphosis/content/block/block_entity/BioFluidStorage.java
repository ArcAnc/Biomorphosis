/**
 * @author ArcAnc
 * Created at: 22.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import com.arcanc.biomorphosis.util.inventory.fluid.SimpleFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BioFluidStorage extends BioBaseBlockEntity
{
    private final SimpleFluidHandler handler;

    public BioFluidStorage(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_FLUID_STORAGE.get(), pos, blockState);

        this.handler = SimpleFluidHandler.newBuilder().
                addTank(FluidStackHolder.newBuilder().
                        setHolderType(FluidStackHolder.HolderType.ALL).
                        setAccessibleFaces(EnumSet.of(DirectionHelper.RelativeFace.DOWN, DirectionHelper.RelativeFace.UP)).
                        setCallback(hold -> this.markDirty()).
                        setValidator(stack -> stack.is(Registration.FluidReg.BIOMASS.type().get())).
                        setCapacity(10000).
                        build()).
                build();
    }

    @Override
    protected void firstTick()
    {

    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        this.handler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        tag.put(Database.Capabilities.Fluids.HANDLER, this.handler.serializeNBT(registries));
    }
}
