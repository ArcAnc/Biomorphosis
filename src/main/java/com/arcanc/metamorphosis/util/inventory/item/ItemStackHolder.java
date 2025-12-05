/**
 * @author ArcAnc
 * Created at: 04.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.inventory.item;

import com.arcanc.metamorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemStackHolder implements IItemHolder
{
    protected ItemStack stack;
    protected int capacity;
    protected Predicate<ItemStack> validator;
    protected ItemStackHolder.HolderCallback callback;

    public ItemStackHolder(ItemStack stack, int capacity, Predicate<ItemStack> validator, ItemStackHolder.HolderCallback callback)
    {
        Preconditions.checkArgument(capacity > 0, "Capacity must be greater than zero");
        this.stack = Preconditions.checkNotNull(stack, "ItemStack can't be null");
        this.capacity = capacity;
        this.validator = Preconditions.checkNotNull(validator, "Validator can't be null");
        this.callback = Preconditions.checkNotNull(callback, "Callback can't be null");
    }

    @Override
    public ItemStack getStack()
    {
        return this.stack;
    }

    public void setAmount(int amount)
    {
        this.stack.setCount(Mth.clamp(amount, 0, this.capacity));
        update();
    }

    public void setStack(@NotNull ItemStack stack)
    {
        this.stack = new ItemStack(stack.getItem(), Mth.clamp(stack.getCount(), 0, this.capacity));
        update();
    }

    @Override
    public int getStackSize()
    {
        return this.stack.getCount();
    }

    @Override
    public int getCapacity()
    {
        return this.capacity;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return this.validator.test(stack);
    }

    public void update()
    {
        this.callback.update(this);
    }

    @Override
    public ItemStack insertItem(@NotNull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(stack))
            return stack;

        ItemStack existing = getStack();

        int limit = Math.min(existing.getMaxStackSize(), getCapacity());

        if (!existing.isEmpty())
        {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
                setStack(reachedLimit ? stack.copyWithCount(limit) : stack);
            else
                setAmount(reachedLimit ? getStackSize() + limit : getStackSize() + stack.getCount());
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int amount, boolean simulate)
    {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack existing = getStack();

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                setStack(ItemStack.EMPTY);
                return existing;
            }
            else
                return existing.copy();
        }
        else
        {
            if (!simulate)
                setStack(existing.copyWithCount(existing.getCount() - toExtract));

            return existing.copyWithCount(toExtract);
        }
    }

    public void clear()
    {
        this.stack = ItemStack.EMPTY;
        update();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries)
    {
        CompoundTag tag = new CompoundTag();

        tag.put(Database.Capabilities.Items.Holder.ITEM, this.stack.saveOptional(registries));
        tag.putInt(Database.Capabilities.Items.Holder.CAPACITY, this.capacity);
        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider registries, @NotNull CompoundTag tag)
    {
        this.capacity = tag.getInt(Database.Capabilities.Items.Holder.CAPACITY);
        this.stack = ItemStack.parseOptional(registries, tag.getCompound(Database.Capabilities.Items.Holder.ITEM));
    }

    public boolean isEmpty()
    {
        return this.stack.isEmpty() || this.stack.getCount() == 0;
    }

    public static @NotNull ItemStackHolder.Builder newBuilder()
    {
        return new ItemStackHolder.Builder();
    }

    public static class Builder
    {
        private ItemStack stack = ItemStack.EMPTY;
        private Predicate<ItemStack> validator = FluidStack -> true;
        private int capacity = 64;
        private ItemStackHolder.HolderCallback callback = holder -> {};

        private Builder ()
        {}

        public ItemStackHolder.Builder setCapacity(int capacity)
        {
            this.capacity = capacity;
            return this;
        }

        public ItemStackHolder.Builder setStack(ItemStack stack)
        {
            this.stack = stack;
            return this;
        }

        public ItemStackHolder.Builder setValidator(Predicate<ItemStack> validator)
        {
            this.validator = validator;
            return this;
        }

        public ItemStackHolder.Builder setCallback(ItemStackHolder.HolderCallback callback)
        {
            this.callback = callback;
            return this;
        }

        public @NotNull ItemStackHolder build()
        {
            return new ItemStackHolder(this.stack, this.capacity, this.validator, this.callback);
        }
    }

    @FunctionalInterface
    public interface HolderCallback
    {
        void update(ItemStackHolder holder);
    }
}
