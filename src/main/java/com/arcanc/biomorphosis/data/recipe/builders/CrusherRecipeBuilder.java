/**
 * @author ArcAnc
 * Created at: 06.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.builders;

import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.CrusherRecipe;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.CrusherRecipeInput;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CrusherRecipeBuilder extends BioBaseRecipeBuilder<CrusherRecipeBuilder, CrusherRecipe, CrusherRecipeInput>
{
    private IngredientWithSize input;
    private ItemStack result;
    private final List<StackWithChance> secondaryResults = new ArrayList<>();

    public static CrusherRecipeBuilder newBuilder(BioBaseRecipe.ResourcesInfo info)
    {
        return new CrusherRecipeBuilder(info);
    }

    private CrusherRecipeBuilder(BioBaseRecipe.ResourcesInfo info)
    {
        super(info);
    }

    @Override
    protected CrusherRecipe getRecipe()
    {
        return new CrusherRecipe(this.group, this.input, this.info, this.result, this.secondaryResults);
    }

    public CrusherRecipeBuilder setInput(IngredientWithSize input)
    {
        this.input = input;
        return this;
    }

    public CrusherRecipeBuilder setResult(ItemStack result)
    {
        this.result = result;
        return this;
    }

    public CrusherRecipeBuilder addSecondaryOutput(StackWithChance stack)
    {
        this.secondaryResults.add(stack);
        return this;
    }

    @Override
    public Item getResult()
    {
        return this.result.getItem();
    }
}
