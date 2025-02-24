/**
 * @author ArcAnc
 * Created at: 24.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.inventory.fluid;

import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleFluidHandler implements IFluidHandler, INBTSerializable<CompoundTag>
{
    protected List<FluidStackHolder> tanks;

    public SimpleFluidHandler()
    {
        this(new ArrayList<>());
    }

    protected SimpleFluidHandler(List<FluidStackHolder> tanks)
    {
        this.tanks = Preconditions.checkNotNull(tanks);
    }

    public static @NotNull Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    public int getTanks()
    {
        return tanks.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        validateTankIndex(tank);
        return this.tanks.get(tank).getFluid();
    }

    @Override
    public int getTankCapacity(int tank)
    {
        validateTankIndex(tank);
        return this.tanks.get(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        validateTankIndex(tank);
        return this.tanks.get(tank).isFluidValid(stack);
    }

    @Override
    public int fill(@NotNull FluidStack stack, @NotNull FluidAction action)
    {
        int remaining = stack.getAmount();
        int filledTotal = 0;

        for (FluidStackHolder tank : this.tanks)
        {
            int filled = tank.fill(new FluidStack(stack.getFluid(), remaining), action);
            filledTotal += filled;
            remaining -= filled;

            if (remaining <= 0)
                break;
        }

        return filledTotal;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack stack, @NotNull FluidAction action)
    {
        FluidStack drainedTotal = FluidStack.EMPTY;
        int remaining = stack.getAmount();

        for (FluidStackHolder tank : this.tanks)
        {
            FluidStack drained = tank.drain(new FluidStack(stack.getFluid(), remaining), action);
            if (!drained.isEmpty())
            {
                if (drainedTotal.isEmpty())
                {
                    drainedTotal = drained.copy();
                }
                else
                {
                    drainedTotal.grow(drained.getAmount());
                }
                remaining -= drained.getAmount();
            }

            if (remaining <= 0)
                break;
        }

        return drainedTotal;
    }

    @Override
    public @NotNull FluidStack drain(int amount, @NotNull FluidAction action)
    {
        FluidStack drainedTotal = FluidStack.EMPTY;
        int remaining = amount;

        for (FluidStackHolder tank : tanks)
        {
            FluidStack drained = tank.drain(remaining, action);
            if (!drained.isEmpty())
            {
                if (drainedTotal.isEmpty())
                {
                    drainedTotal = drained.copy();
                }
                else
                {
                    drainedTotal.grow(drained.getAmount());
                }
                remaining -= drained.getAmount();
            }

            if (remaining <= 0)
                break;
        }

        return drainedTotal;
    }

    public FluidStackHolder getTank(int tank)
    {
        validateTankIndex(tank);
        return this.tanks.get(tank);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        ListTag list = new ListTag();
        for (int q = 0; q < getTanks(); q++)
        {
            FluidStackHolder holder = this.tanks.get(q);

            CompoundTag fluidTag = holder.serializeNBT(provider);
            fluidTag.putInt(Database.Capabilities.Fluids.TANK, q);
            list.add(q, fluidTag);
        }
        CompoundTag fullTag = new CompoundTag();
        fullTag.put(Database.Capabilities.Fluids.TANKS, list);
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
            validateTankIndex(tank);
            this.tanks.get(tank).deserializeNBT(provider, tankTag);
        }
    }

    public boolean isEmpty ()
    {
        if(!tanks.isEmpty())
            for (FluidStackHolder tank : this.tanks)
                if (!tank.isEmpty())
                    return false;
        return true;
    }

    public SimpleFluidHandler getHandler()
    {
        return this;
    }

    protected void validateTankIndex(int tank)
    {
        if (tank < 0 || tank >= tanks.size())
            throw new RuntimeException("Tank " + tank + " not in valid range - [0," + tanks.size() + "]");
    }

    public static class Builder
    {
        private final List<FluidStackHolder> tanks = new ArrayList<>();

        public Builder() {}

        public Builder addTank(FluidStackHolder tank)
        {
            this.tanks.add(tank);
            return this;
        }

        public Builder addTanks(FluidStackHolder... tanks)
        {
            Collections.addAll(this.tanks, tanks);
            return this;
        }

        public Builder addTanks(Collection<FluidStackHolder> tanks)
        {
            this.tanks.addAll(tanks);
            return this;
        }

        public SimpleFluidHandler build()
        {
            return new SimpleFluidHandler(tanks);
        }
    }
}
