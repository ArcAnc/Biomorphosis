/**
 * @author ArcAnc
 * Created at: 04.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.builders;

import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.ForgeRecipe;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.ForgeRecipeInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ForgeRecipeBuilder extends BioBaseRecipeBuilder<ForgeRecipeBuilder, ForgeRecipe, ForgeRecipeInput>
{
    private IngredientWithSize input;
    private ItemStack result;

    public static @NotNull ForgeRecipeBuilder newBuilder(@NotNull BioBaseRecipe.ResourcesInfo info)
    {
        return new ForgeRecipeBuilder(info);
    }

    private ForgeRecipeBuilder(BioBaseRecipe.ResourcesInfo info)
    {
        super(info);
    }

    @Override
    protected ForgeRecipe getRecipe()
    {
        return new ForgeRecipe(this.input, this.info, this.result);
    }

    public ForgeRecipeBuilder setInput(@NotNull IngredientWithSize input)
    {
        this.input = input;
        return this;
    }

    public ForgeRecipeBuilder setResult(@NotNull ItemStack result)
    {
        this.result = result;
        return this;
    }

    @Override
    public @NotNull Item getResult()
    {
        return this.result.getItem();
    }
}
