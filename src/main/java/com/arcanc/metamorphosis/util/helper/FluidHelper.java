/**
 * @author ArcAnc
 * Created at: 24.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class FluidHelper
{
    public static boolean isFluidHandler(@NotNull Level level, @NotNull BlockPos pos)
    {
        return getFluidHandler(level, pos, null).isPresent();
    }

    public static boolean isFluidHandler(@NotNull Level level, @NotNull BlockPos pos, @Nullable Direction dir)
    {
        return getFluidHandler(level, pos, dir).isPresent();
    }

    public static boolean isFluidHandler(@NotNull ItemStack stack)
    {
        return !stack.isEmpty() && getFluidHandler(stack).isPresent();
    }

    public static boolean isFluidHandler(BlockEntity tile)
    {
        return isFluidHandler(tile, null);
    }

    public static boolean isFluidHandler(@NotNull BlockEntity tile, Direction dir)
    {
        return getFluidHandler(tile, dir).isPresent();
    }

    public static Optional<IFluidHandler> getFluidHandler (Level level, BlockPos pos)
    {
        return getFluidHandler(level, pos, null);
    }

    public static Optional<IFluidHandler> getFluidHandler (@NotNull Level level, @NotNull BlockPos pos, @Nullable Direction dir)
    {
        return Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, dir));
    }

    public static Optional<IFluidHandler> getFluidHandler (@NotNull BlockEntity tile)
    {
        return getFluidHandler(tile, null);
    }

    public static Optional<IFluidHandler> getFluidHandler (@NotNull BlockEntity tile, @Nullable Direction dir)
    {
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        BlockState state = tile.getBlockState();

        return getFluidHandler(level, pos, tile, state, dir);
    }

    public static Optional<IFluidHandler> getFluidHandler(@NotNull Level level, @NotNull BlockPos pos, @Nullable BlockEntity tile, @Nullable BlockState state, @Nullable Direction dir)
    {
        return Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, state, tile, dir));
    }

    public static Optional<IFluidHandler> getNearbyFluidHandler (@NotNull BlockEntity tile, Direction dir)
    {
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        return getFluidHandler(level, pos.relative(dir), dir.getOpposite());
    }

    public static Optional<IFluidHandlerItem> getFluidHandler (@NotNull ItemStack stack)
    {
        return Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM));
    }

    public static boolean hasEmptySpace(BlockEntity tile)
    {
        return hasEmptySpace(tile, null);
    }

    public static boolean hasEmptySpace(ItemStack stack)
    {
        Optional<IFluidHandlerItem> handler = getFluidHandler(stack);
        if (handler.isPresent())
        {
            return hasEmptyItemSpace(handler);
        }
        return false;
    }

    public static boolean hasEmptySpace(BlockEntity tile, Direction dir)
    {
        Optional<IFluidHandler> handler = getFluidHandler(tile, dir);
        if (handler.isPresent())
        {
            return hasEmptySpace(handler);
        }
        return false;
    }

    public static boolean hasEmptySpace(@NotNull Optional<IFluidHandler> in)
    {
        return in.map(handler ->
        {
            for (int q = 0; q < handler.getTanks(); q++)
            {
                FluidStack stack = handler.getFluidInTank(q);
                if (stack.isEmpty() || stack.getAmount() < handler.getTankCapacity(q))
                {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean hasEmptyItemSpace(@NotNull Optional<IFluidHandlerItem> in)
    {
        return in.map(handler ->
        {
            for (int q = 0; q < handler.getTanks(); q++)
            {
                FluidStack stack = handler.getFluidInTank(q);
                if (stack.isEmpty() || stack.getAmount() < handler.getTankCapacity(q))
                {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean hasEmptySpace(@NotNull IFluidHandler handler)
    {
        for (int q = 0; q < handler.getTanks(); q++)
        {
            FluidStack stack = handler.getFluidInTank(q);
            if (stack.isEmpty() || stack.getAmount() < handler.getTankCapacity(q))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasEmptySpace(@NotNull IItemHandler in)
    {
        for (int q = 0; q < in.getSlots(); q++)
        {
            ItemStack stack = in.getStackInSlot(q);
            if (hasEmptySpace(stack))
            {
                return true;
            }
        }
        return false;
    }

    public static int getEmptySpace(@NotNull IFluidHandler handler)
    {
        int space = 0;
        for (int q = 0; q < handler.getTanks(); q++)
        {
            FluidStack stack = handler.getFluidInTank(q);
            if (stack.isEmpty())
            {
                space += handler.getTankCapacity(q);
            }
            else if (stack.getAmount() < handler.getTankCapacity(q))
            {
                space += handler.getTankCapacity(q) - stack.getAmount();
            }
        }
        return space;
    }

    public static int getEmptySpace(@NotNull IFluidHandlerItem handler)
    {
        int space = 0;
        for (int q = 0; q < handler.getTanks(); q++)
        {
            FluidStack stack = handler.getFluidInTank(q);
            if (stack.isEmpty())
            {
                space += handler.getTankCapacity(q);
            }
            else if (stack.getAmount() < handler.getTankCapacity(q))
            {
                space += handler.getTankCapacity(q) - stack.getAmount();
            }
        }
        return space;
    }

    public static int getEmptySpace(Optional<IFluidHandler> in)
    {
        if (hasEmptySpace(in))
        {
            return in.map(FluidHelper::getEmptySpace).orElse(0);
        }
        return 0;
    }

    public static int getEmptyItemSpace(Optional<IFluidHandlerItem> in)
    {
        if (hasEmptyItemSpace(in))
        {
            return in.map(FluidHelper::getEmptySpace).orElse(0);
        }
        return 0;
    }


    public static int getEmptySpace(ItemStack in)
    {
        return getEmptyItemSpace(getFluidHandler(in));
    }

    public static int getEmptySpace(IItemHandler in)
    {
        if (hasEmptySpace(in))
        {
            int space = 0;
            for (int q = 0; q < in.getSlots(); q++)
            {
                ItemStack stack = in.getStackInSlot(q);
                space += getEmptySpace(stack);
            }
            return space;
        }
        return 0;
    }

    public static boolean isEmpty(BlockEntity tile)
    {
        Optional<IFluidHandler> hand = getFluidHandler(tile);
        if (hand.isPresent())
        {
            return isEmpty(hand);
        }
        return false;
    }

    public static boolean isEmpty(@NotNull Optional<IFluidHandler> in)
    {
        return in.map(handler ->
        {
            for (int q = 0; q < handler.getTanks(); q++)
            {
                if (!handler.getFluidInTank(q).isEmpty())
                    return false;
            }
            return true;
        }).orElse(true);
    }

    public static boolean isEmpty(@NotNull IFluidHandler handler)
    {
        for (int q = 0; q < handler.getTanks(); q++)
        {
            if (!handler.getFluidInTank(q).isEmpty())
                return false;
        }
        return true;
    }

    public static boolean isFull(@NotNull Optional<IFluidHandler> input)
    {
        return input.map(FluidHelper :: isFull).orElse(false);
    }

    public static boolean isFull(@NotNull IFluidHandler handler)
    {
        for (int q = 0; q < handler.getTanks(); q++)
            if (handler.getFluidInTank(q).getAmount() < handler.getTankCapacity(q))
                return false;
        return true;
    }

    public static boolean contains (@NotNull Optional<IFluidHandler> handler, FluidType fluid)
    {
        return handler.map(iFluidHandler ->
        {
            for (int q = 0; q < iFluidHandler.getTanks(); q++)
                if (iFluidHandler.getFluidInTank(q).getFluidType() == fluid)
                    return true;
            return false;
        }).orElse(false);
    }

    public static boolean contains (@NotNull Optional<IFluidHandler> handler, Set<FluidType> fluid)
    {
        return handler.map(iFluidHandler ->
        {
            for (int q = 0; q < iFluidHandler.getTanks(); q++)
                if (fluid.contains(iFluidHandler.getFluidInTank(q).getFluidType()))
                    return true;
            return false;
        }).orElse(false);
    }
}
