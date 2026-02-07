/**
 * @author ArcAnc
 * Created at: 07.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.input;

import com.google.common.base.Preconditions;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChamberRecipeInput extends BioBaseInput
{
    public final ItemStack[] stacks;

    public ChamberRecipeInput(@NotNull List<ItemStack> items)
    {
        this (items.toArray(new ItemStack[]{}));
    }

    public ChamberRecipeInput(ItemStack stack0,
                              ItemStack stack1,
                              ItemStack stack2,
                              ItemStack stack3,
                              ItemStack stack4,
                              ItemStack stack5,
                              ItemStack stack6,
                              ItemStack stack7,
                              ItemStack stack8,
                              ItemStack stack9,
                              ItemStack stack10,
                              ItemStack stack11)
    {
        this(new ItemStack[]{stack0, stack1, stack2, stack3, stack4, stack5,
                             stack6, stack7, stack8, stack9, stack10, stack11});

    }

    public ChamberRecipeInput(ItemStack... itemStacks)
    {
        super(FluidStack.EMPTY, FluidStack.EMPTY, FluidStack.EMPTY);
        Preconditions.checkNotNull(itemStacks);
        Preconditions.checkState(itemStacks.length == 12);

        this.stacks = itemStacks;
    }

    @Override
    public @NotNull ItemStack getItem(int index)
    {
        if (index < 0 || index > 11)
            throw new IllegalArgumentException("No item for index " + index);
        return this.stacks[index];
    }

    @Override
    public int size()
    {
        return 12;
    }
}
