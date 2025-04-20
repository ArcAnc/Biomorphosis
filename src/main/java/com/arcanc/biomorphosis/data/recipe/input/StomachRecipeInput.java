/**
 * @author ArcAnc
 * Created at: 19.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class StomachRecipeInput extends BioBaseInput
{
    private final ItemStack stack;

    public StomachRecipeInput(ItemStack stack, FluidStack biomass, FluidStack lymph, FluidStack adrenaline)
    {
        super(biomass, lymph, adrenaline);
        this.stack = stack;
    }

    @Override
    public @NotNull ItemStack getItem(int index)
    {
        if (index != 0)
            throw new IllegalArgumentException("No item for index " + index);
        return this.stack;
    }

    @Override
    public int size()
    {
        return 1;
    }
}
