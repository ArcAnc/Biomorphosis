/**
 * @author ArcAnc
 * Created at: 22.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.fluid.FluidLevelAnimator;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioFluidStorage extends BioSidedAccessBlockEntity
{
    private final FluidSidedStorage handler;

    public BioFluidStorage(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_FLUID_STORAGE.get(), pos, blockState);

        setSideMode(BasicSidedStorage.RelativeFace.UP, BasicSidedStorage.FaceMode.ALL).
                setSideMode(BasicSidedStorage.RelativeFace.DOWN, BasicSidedStorage.FaceMode.ALL);
        this.handler = new FluidSidedStorage().
                addHolder(FluidStackHolder.newBuilder().
                        setCallback(hold -> this.markDirty()).
                        setValidator(stack -> true).
                        setCapacity(10000).
                        build(),
                        BasicSidedStorage.FaceMode.ALL);
    }

    @Override
    protected void firstTick()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.registerBlockEntity(this.level, getBlockPos());
    }

    @Override
    public void setRemoved()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.removeBlockEntity(this.level, getBlockPos());
        super.setRemoved();
    }

    public static @Nullable FluidSidedStorage getHandler(@NotNull BioFluidStorage be, Direction ctx)
    {
        return ctx == null ? be.handler : be.isAccessible(ctx) ? be.handler : null;
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        this.handler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        tag.put(Database.Capabilities.Fluids.HANDLER, this.handler.serializeNBT(registries));
    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, @NotNull UseOnContext ctx)
    {
        Direction dir = ctx.getClickedFace();
        if (!(dir == Direction.UP) && !(dir == Direction.DOWN))
            return InteractionResult.PASS;
        nextMode(dir);
        return InteractionResult.SUCCESS_SERVER;
    }
}
