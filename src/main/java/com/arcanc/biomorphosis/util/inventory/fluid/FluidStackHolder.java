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
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FluidStackHolder implements IFluidTank
{
    protected FluidStack fluid;
    protected int capacity;
    protected Predicate<FluidStack> validator;
    protected HolderCallback callback;
    protected HolderType type;
    protected Set<DirectionHelper.RelativeFace> accessibleFaces;

    @OnlyIn(Dist.CLIENT)
    private int clientFluidAmount = 0;

    public FluidStackHolder(FluidStack stack, int capacity, HolderType type, Set<DirectionHelper.RelativeFace> accessibleFaces, Predicate<FluidStack> validator, HolderCallback callback)
    {
        Preconditions.checkArgument(capacity > 0, "Capacity must be greater than zero");
        this.fluid = Preconditions.checkNotNull(stack, "FluidStack can't be null");
        this.capacity = capacity;
        this.type = Preconditions.checkNotNull(type, "Holder Type can't be null");
        this.accessibleFaces = Preconditions.checkNotNull(accessibleFaces, "Accessible Faces can't be null");
        this.validator = Preconditions.checkNotNull(validator, "Validator can't be null");
        this.callback = Preconditions.checkNotNull(callback, "Callback can't be null");
    }

    @Override
    public @NotNull FluidStack getFluid()
    {
        return this.fluid;
    }

    public void setAmount(int amount)
    {
        this.fluid.setAmount(Mth.clamp(amount, 0, this.capacity));
        update();
    }

    public void setFluid(@NotNull FluidStack fluid)
    {
        this.fluid = new FluidStack(fluid.getFluid(), Mth.clamp(fluid.getAmount(), 0, this.capacity));
        update();
    }

    @Override
    public int getFluidAmount()
    {
        return this.fluid.getAmount();
    }

    @Override
    public int getCapacity()
    {
        return this.capacity;
    }

    @Override
    public boolean isFluidValid(@NotNull FluidStack stack)
    {
        return this.validator.test(stack);
    }

    public void update()
    {
        this.callback.update(this);
    }

    public HolderType getType()
    {
        return type;
    }

    public Set<DirectionHelper.RelativeFace> getAccessibleFaces()
    {
        return accessibleFaces;
    }

    public boolean isAccessible(BlockState state, Direction direction)
    {
        return direction == null || this.accessibleFaces.contains(DirectionHelper.getRelativeDirection(state, direction));
    }

    @Override
    public int fill(@NotNull FluidStack stack, IFluidHandler.@NotNull FluidAction action)
    {
        if (stack.isEmpty() || !isFluidValid(stack))
            return 0;
        if (!this.fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.fluid, stack))
            return 0;

        int fillAmount = Math.min(this.capacity - this.fluid.getAmount(), stack.getAmount());

        if (action.execute())
        {
            if (this.fluid.isEmpty())
                setFluid(new FluidStack(stack.getFluid(), fillAmount));
            else
                setAmount(this.fluid.getAmount() + fillAmount);
        }
        return fillAmount;
    }

    @Override
    public @NotNull FluidStack drain(int amount, IFluidHandler.@NotNull FluidAction action)
    {
        if (amount <= 0 || this.fluid.isEmpty())
            return FluidStack.EMPTY;

        int drained = Math.min(amount, fluid.getAmount());
        FluidStack drainedStack = new FluidStack(fluid.getFluid(), drained);

        if (action.execute())
        {
            fluid.shrink(drained);
            if (fluid.isEmpty())
                this.fluid = FluidStack.EMPTY;
            update();
        }
        return drainedStack;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack stack, IFluidHandler.@NotNull FluidAction action)
    {
        return (stack.isEmpty() || !FluidStack.isSameFluidSameComponents(stack, fluid)) ? FluidStack.EMPTY : drain(stack.getAmount(), action);
    }

    public void clear()
    {
        this.fluid = FluidStack.EMPTY;
        update();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries)
    {
        CompoundTag tag = new CompoundTag();

        tag.put(Database.Capabilities.Fluids.Holder.FLUID, this.fluid.saveOptional(registries));
        tag.putInt(Database.Capabilities.Fluids.Holder.CAPACITY, this.capacity);
        tag.putInt(Database.Capabilities.Fluids.Holder.TYPE, this.type.ordinal());
        tag.putIntArray(Database.Capabilities.Fluids.Holder.FACES, this.accessibleFaces.stream().map(DirectionHelper.RelativeFace :: ordinal).toList());
        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider registries, @NotNull CompoundTag tag)
    {
        this.capacity = tag.getInt(Database.Capabilities.Fluids.Holder.CAPACITY);
        this.fluid = FluidStack.parseOptional(registries, tag.getCompound(Database.Capabilities.Fluids.Holder.FLUID));
        this.type = HolderType.values()[tag.getInt(Database.Capabilities.Fluids.Holder.TYPE)];
        this.accessibleFaces = Arrays.stream(tag.getIntArray(Database.Capabilities.Fluids.Holder.FACES)).
                mapToObj(id -> DirectionHelper.RelativeFace.values()[id]).collect(Collectors.toSet());

    }

    public boolean isEmpty()
    {
        return this.fluid.isEmpty() || this.fluid.getAmount() == 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getClientFluidAmount()
    {
        return clientFluidAmount;
    }

    @OnlyIn(Dist.CLIENT)
    public void setClientFluidAmount(int clientFluidAmount)
    {
        this.clientFluidAmount = clientFluidAmount;
    }

    public static @NotNull Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private FluidStack stack = FluidStack.EMPTY;
        private HolderType tankType = HolderType.ALL;
        private Predicate<FluidStack> validator = FluidStack -> true;
        private int capacity = 1000;
        private HolderCallback callback = holder -> {};
        private Set<DirectionHelper.RelativeFace> accessibleFaces = new HashSet<>();

        private Builder ()
        {}

        public Builder setCapacity(int capacity)
        {
            this.capacity = capacity;
            return this;
        }

        public Builder setHolderType(HolderType tankType)
        {
            this.tankType = tankType;
            return this;
        }

        public Builder setStack(FluidStack stack)
        {
            this.stack = stack;
            return this;
        }

        public Builder setValidator(Predicate<FluidStack> validator)
        {
            this.validator = validator;
            return this;
        }

        public Builder setCallback(HolderCallback callback)
        {
            this.callback = callback;
            return this;
        }

        public Builder setAccessibleFaces(Set<DirectionHelper.RelativeFace> accessibleFaces)
        {
            this.accessibleFaces = accessibleFaces;
            return this;
        }

        public Builder addAccessibleFaces(DirectionHelper.RelativeFace... faces)
        {
            this.accessibleFaces.addAll(List.of(faces));
            return this;
        }

        public @NotNull FluidStackHolder build()
        {
            return new FluidStackHolder(this.stack, this.capacity, this.tankType, this.accessibleFaces, this.validator, this.callback);
        }
    }

    @FunctionalInterface
    public interface HolderCallback
    {
        void update(FluidStackHolder holder);
    }

    public enum HolderType
    {
        ALL,
        INPUT,
        OUTPUT,
        INTERNAL
    }
}