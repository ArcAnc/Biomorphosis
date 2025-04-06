/**
 * @author ArcAnc
 * Created at: 30.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class BioBaseInput implements RecipeInput
{
    private final FluidStack biomass;
    private final FluidStack lymph;
    private final FluidStack adrenaline;

    public BioBaseInput(FluidStack biomass, FluidStack lymph, FluidStack adrenaline)
    {
        this.biomass = biomass;
        this.lymph = lymph;
        this.adrenaline = adrenaline;
    }

    public FluidStack biomass()
    {
        return biomass;
    }

    public FluidStack lymph()
    {
        return lymph;
    }

    public FluidStack adrenaline()
    {
        return adrenaline;
    }

    @Override
    public @NotNull ItemStack getItem(int index)
    {
        return ItemStack.EMPTY;
    }

    public FluidStack getFluid(int index)
    {
        if (index < 0 || index > 2)
            throw new IllegalArgumentException("No fluid for index " + index);
        return switch (index)
        {
            case 0 -> biomass;
            case 1 -> lymph;
            default -> adrenaline;
        };
    }

    @Override
    public int size()
    {
        return 2;
    }
}