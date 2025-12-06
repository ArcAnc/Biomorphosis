/**
 * @author ArcAnc
 * Created at: 19.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.builders;

import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.StomachRecipe;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.StomachRecipeInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class StomachRecipeBuilder extends BioBaseRecipeBuilder<StomachRecipeBuilder, StomachRecipe, StomachRecipeInput>
{
    private IngredientWithSize input;
    private FluidStack result;

    public static @NotNull StomachRecipeBuilder newBuilder(@NotNull BioBaseRecipe.ResourcesInfo info)
    {
        return new StomachRecipeBuilder(info);
    }

    private StomachRecipeBuilder(BioBaseRecipe.ResourcesInfo info)
    {
        super(info);
    }

    @Override
    protected StomachRecipe getRecipe()
    {
        return new StomachRecipe(this.input, this.info, this.result);
    }

    public StomachRecipeBuilder setInput(@NotNull IngredientWithSize input)
    {
        this.input = input;
        return this;
    }

    public StomachRecipeBuilder setResult(@NotNull FluidStack result)
    {
        this.result = result;
        return this;
    }

    @Override
    public @NotNull Item getResult()
    {
        return ItemStack.EMPTY.getItem();
    }
}
