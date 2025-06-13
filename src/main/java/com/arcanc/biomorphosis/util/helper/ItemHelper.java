/**
 * @author ArcAnc
 * Created at: 11.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ItemHelper
{

    public static boolean isItemHandler(Level level, BlockPos pos)
    {
        return isItemHandler(level, pos, null);
    }

    public static boolean isItemHandler(Level level, BlockPos pos, @Nullable Direction dir)
    {
        if (level != null)
        {
            return BlockHelper.getTileEntity(level, pos).map(tile ->
                    isItemHandler(tile, dir)).orElse(false);
        }
        return false;
    }

    public static boolean isItemHandler(BlockEntity tile)
    {
        return isItemHandler(tile, null);
    }

    public static boolean isItemHandler(BlockEntity tile, @Nullable Direction dir)
    {
        return getItemHandler(tile, dir).isPresent();
    }

    public static Optional<IItemHandler> getItemHandler (Level level, BlockPos pos)
    {
        return getItemHandler(level, pos, null);
    }

    public static Optional<IItemHandler> getItemHandler (@NotNull Level level, @NotNull BlockPos pos, @Nullable Direction dir)
    {
        return Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, pos, dir));
    }

    public static Optional<IItemHandler> getItemHandler (@NotNull BlockEntity tile, Direction dir)
    {
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        BlockState state = tile.getBlockState();

        return getItemHandler(level, pos, tile, state, dir);
    }

    public static Optional<IItemHandler> getItemHandler(@NotNull Level level, @NotNull BlockPos pos, @Nullable BlockEntity tile, @Nullable BlockState state, @Nullable Direction dir)
    {
        return Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, tile, dir));
    }

    public static boolean hasEmptySpace(BlockEntity tile)
    {
        return hasEmptySpace(tile, null);
    }

    public static boolean hasEmptySpace(BlockEntity tile, Direction dir)
    {
        Optional<IItemHandler> handler = getItemHandler(tile, dir);
        if (handler.isPresent())
        {
            return hasEmptySpace(handler);
        }
        return false;
    }

    public static boolean hasEmptySpace(@NotNull Optional<IItemHandler> in)
    {
        return in.map(handler ->
        {
            for (int q = 0; q < handler.getSlots(); q++)
            {
                ItemStack stack = handler.getStackInSlot(q);
                if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize())
                {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean hasEmptySpace(@NotNull IItemHandler in)
    {
        for (int q = 0; q < in.getSlots(); q++)
        {
            ItemStack stack = in.getStackInSlot(q);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize())
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(@NotNull IItemHandler inv)
    {
        for (int q = 0; q < inv.getSlots(); q++)
        {
            if (!inv.getStackInSlot(q).isEmpty())
                return false;
        }
        return true;
    }

    public static @NotNull ResourceLocation getRegistryName(Item item)
    {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static void dropContents (Level level, BlockPos pos, Optional<IItemHandler> inventory)
    {
        if (level != null && pos != null)
        {
            inventory.ifPresent(handler ->
                    dropContents(level, pos, handler));
        }
    }

    public static void dropContents (Level level, BlockPos pos, IItemHandler inventory)
    {
        if (level != null && pos != null)
        {
            for (int q = 0; q < inventory.getSlots() ; q++)
            {
                Containers.dropItemStack(level, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, inventory.getStackInSlot(q));
            }
        }
    }
}
