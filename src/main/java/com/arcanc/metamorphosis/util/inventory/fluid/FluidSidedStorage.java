/**
 * @author ArcAnc
 * Created at: 27.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.inventory.fluid;

import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.inventory.BasicSidedStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FluidSidedStorage extends BasicSidedStorage<FluidSidedStorage, FluidStackHolder, FluidStack> implements IFluidHandler, INBTSerializable<CompoundTag>
{
    public int getClientFluidAmountInTank(int id)
    {
        validateHolderIndex(id);
        return this.holders.get(id).getClientFluidAmount();
    }

    public void setClientFluidAmountInTank(int id, int amount)
    {
        validateHolderIndex(id);
        this.holders.get(id).setClientFluidAmount(amount);
    }

    @Override
    public FluidSidedStorage getStorage()
    {
        return this;
    }

    @Override
    public boolean isValueValid(FaceMode mode, int id, FluidStack value)
    {
        return getHolderAt(mode, id).map(tank -> tank.isFluidValid(value)).orElse(false);
    }

    @Override
    public int getHolderCapacity(FaceMode mode, int id)
    {
        return getHolderAt(mode, id).map(IFluidTank :: getCapacity).orElse(0);
    }

    @Override
    public FluidStack getValueAtId(FaceMode mode, int id)
    {
        return getHolderAt(mode, id).map(IFluidTank :: getFluid).orElse(FluidStack.EMPTY);
    }

    @Override
    public FluidStack insert(FaceMode mode, @NotNull FluidStack value, boolean isSimulate)
    {
        if (mode == FaceMode.OUTPUT || mode == FaceMode.BLOCKED)
            return FluidStack.EMPTY;
        if (value.isEmpty())
            return FluidStack.EMPTY;

        int remaining = value.getAmount();
        int filledTotal = 0;
        FluidStack filledStack = new FluidStack(value.getFluid(), 0);

        for (IFluidTank tank : getHoldersForAccess(mode))
        {
            filledStack.setAmount(remaining);
            int filled = tank.fill(filledStack, isSimulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
            filledTotal += filled;
            remaining -= filled;
            if (remaining <= 0)
                break;
        }
        return new FluidStack(value.getFluid(), filledTotal);
    }

    @Override
    public FluidStack extract(@Nullable FaceMode mode, @NotNull FluidStack value, boolean isSimulate)
    {
        if (mode == FaceMode.INPUT || mode == FaceMode.BLOCKED)
            return FluidStack.EMPTY;
        if (value.isEmpty())
            return FluidStack.EMPTY;

        FluidStack drainedTotal = FluidStack.EMPTY;
        int remaining = value.getAmount();

        for (IFluidTank tank : getHoldersForAccess(mode))
        {
            FluidStack drained = tank.drain(new FluidStack(value.getFluid(), remaining), isSimulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
            if (!drained.isEmpty())
            {
                if (drainedTotal.isEmpty())
                    drainedTotal = drained.copy();
                else
                    drainedTotal.grow(drained.getAmount());
                remaining -= drained.getAmount();
            }
            if (remaining <= 0)
                break;
        }

        return drainedTotal;
    }

    //--------------------------------------------------
    // I Fluid Handler
    //--------------------------------------------------

    @Override
    public int getTanks()
    {
        return getHoldersCount();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        return getValueAtId(null, tank);
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return getHolderCapacity(null, tank);
    }

    public void setTankCapacity(int tank, int value)
    {
        getHolderAt(null, tank).ifPresent(holder -> holder.setCapacity(value));
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return isValueValid(null, tank, stack);
    }

    @Override
    public int fill(@NotNull FluidStack fluid, @NotNull FluidAction action)
    {
        return insert(null, fluid, action.simulate()).getAmount();
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action)
    {
        return extract(null, resource, action.simulate());
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action)
    {
        if (maxDrain == 0)
            return FluidStack.EMPTY;

        FluidStack drainedTotal = FluidStack.EMPTY;
        int remaining = maxDrain;

        for (IFluidTank tank : getHoldersForAccess(null))
        {
            FluidStack drained = tank.drain(remaining, action);
            if (!drained.isEmpty())
            {
                if (drainedTotal.isEmpty())
                    drainedTotal = drained.copy();
                else
                    drainedTotal.grow(drained.getAmount());
                remaining -= drained.getAmount();
            }

            if (remaining <= 0)
                break;
        }
        return drainedTotal;
    }

    //--------------------------------------------------
    // I NBT SERIALIZING
    //--------------------------------------------------

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        ListTag list = new ListTag();
        for (int q = 0; q < getTanks(); q++)
        {
            FluidStackHolder holder = this.holders.get(q);

            CompoundTag fluidTag = holder.serializeNBT(provider);
            fluidTag.putInt(Database.Capabilities.Fluids.TANK, q);
            list.add(q, fluidTag);
        }
        CompoundTag fullTag = new CompoundTag();
        fullTag.put(Database.Capabilities.Fluids.TANKS, list);
        ListTag modesInfo = new ListTag();
        for (Map.Entry<FaceMode, List<Integer>> entry : this.BY_SIDE.entrySet())
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt(Database.Capabilities.Fluids.MODE, entry.getKey().ordinal());
            tag.putIntArray(Database.Capabilities.Fluids.INTS, entry.getValue());
            modesInfo.add(tag);
        }
        fullTag.put(Database.Capabilities.Fluids.MODES, modesInfo);
        ListTag indexToMode = new ListTag();
        for (Map.Entry<Integer, FaceMode> entry : this.SLOT_TO_MODE.entrySet())
        {
            IntTag tag = IntTag.valueOf(entry.getValue().ordinal());
            indexToMode.add(tag);
        }
        fullTag.put(Database.Capabilities.Fluids.INDEX_TO_MODE, indexToMode);
        return fullTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt)
    {
        ListTag list = nbt.getList(Database.Capabilities.Fluids.TANKS, Tag.TAG_COMPOUND);
        for (int q = 0; q < list.size(); q++)
        {
            CompoundTag tankTag = list.getCompound(q);
            int tank = tankTag.getInt(Database.Capabilities.Fluids.TANK);
            validateHolderIndex(tank);
            this.holders.get(tank).deserializeNBT(provider, tankTag);
        }

        ListTag modesInfo = nbt.getList(Database.Capabilities.Fluids.MODES, Tag.TAG_COMPOUND);
        for (int q = 0; q < modesInfo.size(); q++)
        {
            CompoundTag tag = modesInfo.getCompound(q);
            FaceMode mode = FaceMode.values()[tag.getInt(Database.Capabilities.Fluids.MODE)];
            List<Integer> indexes = Arrays.stream(tag.getIntArray(Database.Capabilities.Fluids.INTS)).
                    boxed().
                    collect(Collectors.toCollection(ArrayList :: new));
            this.BY_SIDE.put(mode, indexes);
        }
        ListTag indexToMode = nbt.getList(Database.Capabilities.Fluids.INDEX_TO_MODE, Tag.TAG_INT);
        for (int q = 0; q < indexToMode.size(); q++)
        {
            FaceMode mode = FaceMode.values()[indexToMode.getInt(q)];
            this.SLOT_TO_MODE.put(q, mode);
        }
    }
}
