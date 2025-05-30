/**
 * @author ArcAnc
 * Created at: 04.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.inventory.item;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStackSidedStorage extends BasicSidedStorage<ItemStackSidedStorage, ItemStackHolder, ItemStack> implements IItemHandler, INBTSerializable<CompoundTag>
{
    @Override
    public ItemStackSidedStorage getStorage()
    {
        return this;
    }

    @Override
    public boolean isValueValid(FaceMode mode, int id, ItemStack value)
    {
        return getHolderAt(mode, id).map(holder -> holder.isItemValid(value)).orElse(false);
    }

    @Override
    public int getHolderCapacity(FaceMode mode, int id)
    {
        return getHolderAt(mode, id).map(IItemHolder :: getCapacity).orElse(0);
    }

    @Override
    public ItemStack getValueAtId(FaceMode mode, int id)
    {
        return getHolderAt(mode, id).map(IItemHolder :: getStack).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack insert(FaceMode mode, ItemStack value, boolean isSimulate)
    {
        if (mode == FaceMode.OUTPUT || mode == FaceMode.BLOCKED)
            return value;
        if (value.isEmpty())
            return ItemStack.EMPTY;

        ItemStack remaining = value.copy();

        for (IItemHolder holder : getHoldersForAccess(mode))
        {
            remaining = holder.insertItem(remaining, isSimulate);
            if (remaining.getCount() <= 0)
                return ItemStack.EMPTY;
        }
        return remaining;
    }

    @Override
    public ItemStack extract(FaceMode mode, ItemStack value, boolean isSimulate)
    {
        if (mode == FaceMode.INPUT || mode == FaceMode.BLOCKED)
            return ItemStack.EMPTY;
        if (value.isEmpty())
            return ItemStack.EMPTY;

        ItemStack extractedTotal = ItemStack.EMPTY;
        int remaining = value.getCount();

        for (IItemHolder holder : getHoldersForAccess(mode))
        {
            ItemStack extracted = holder.extractItem(remaining, isSimulate);
            if (!extracted.isEmpty())
            {
                if (extractedTotal.isEmpty())
                    extractedTotal = extracted.copy();
                else
                    extractedTotal.grow(extracted.getCount());
                remaining -= extracted.getCount();
            }
            if (remaining <= 0)
                break;
        }

        return extractedTotal;
    }

    //--------------------------------------------------
    // I Item Handler
    //--------------------------------------------------

    @Override
    public int getSlots()
    {
        return getHoldersCount();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot)
    {
        return getValueAtId(null, slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        return getHolderAt(null, slot).map(holder -> holder.insertItem(stack, simulate)).orElse(stack);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount <= 0)
            return ItemStack.EMPTY;

        return getHolderAt(null, slot).map(holder -> holder.extractItem(amount, simulate)).orElse(ItemStack.EMPTY);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return getHolderCapacity(null, slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return isValueValid(null, slot, stack);
    }

    //--------------------------------------------------
    // I NBT SERIALIZING
    //--------------------------------------------------

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        ListTag list = new ListTag();
        for (int q = 0; q < getSlots(); q++)
        {
            ItemStackHolder holder = this.holders.get(q);

            CompoundTag itemTag = holder.serializeNBT(provider);
            itemTag.putInt(Database.Capabilities.Items.HOLDER, q);
            list.add(q, itemTag);
        }
        CompoundTag fullTag = new CompoundTag();
        fullTag.put(Database.Capabilities.Items.HOLDERS, list);
        ListTag modesInfo = new ListTag();
        for (Map.Entry<FaceMode, List<Integer>> entry : BY_SIDE.entrySet())
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt(Database.Capabilities.Items.MODE, entry.getKey().ordinal());
            tag.putIntArray(Database.Capabilities.Items.INTS, entry.getValue());
            modesInfo.add(tag);
        }
        fullTag.put(Database.Capabilities.Items.MODES, modesInfo);
        ListTag indexToMode = new ListTag();
        for (Map.Entry<Integer, FaceMode> entry : this.SLOT_TO_MODE.entrySet())
        {
            IntTag tag = IntTag.valueOf(entry.getValue().ordinal());
            indexToMode.add(tag);
        }
        fullTag.put(Database.Capabilities.Items.INDEX_TO_MODE, indexToMode);
        return fullTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt)
    {
        ListTag list = nbt.getList(Database.Capabilities.Items.HOLDERS, Tag.TAG_COMPOUND);
        for (int q = 0; q < list.size(); q++)
        {
            CompoundTag holderTag = list.getCompound(q);
            int holder = holderTag.getInt(Database.Capabilities.Items.HOLDER);
            validateHolderIndex(holder);
            this.holders.get(holder).deserializeNBT(provider, holderTag);
        }

        ListTag modesInfo = nbt.getList(Database.Capabilities.Items.MODES, Tag.TAG_COMPOUND);
        for (int q = 0; q < modesInfo.size(); q++)
        {
            CompoundTag tag = modesInfo.getCompound(q);
            FaceMode mode = FaceMode.values()[tag.getInt(Database.Capabilities.Items.MODE)];
            List<Integer> indexes = Arrays.stream(tag.getIntArray(Database.Capabilities.Items.INTS)).
                    boxed().
                    collect(Collectors.toCollection(ArrayList:: new));
            this.BY_SIDE.put(mode, indexes);
        }
        ListTag indexToMode = nbt.getList(Database.Capabilities.Items.INDEX_TO_MODE, Tag.TAG_INT);
        for (int q = 0; q < indexToMode.size(); q++)
        {
            FaceMode mode = FaceMode.values()[indexToMode.getInt(q)];
            this.SLOT_TO_MODE.put(q, mode);
        }
    }
}
