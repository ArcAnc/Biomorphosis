/**
 * @author ArcAnc
 * Created at: 07.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.recipe.builders;

import com.arcanc.metamorphosis.data.recipe.MetaBaseRecipe;
import com.arcanc.metamorphosis.data.recipe.ChamberRecipe;
import com.arcanc.metamorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.metamorphosis.data.recipe.input.ChamberRecipeInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChamberRecipeBuilder extends MetaBaseRecipeBuilder<ChamberRecipeBuilder, ChamberRecipe, ChamberRecipeInput>
{
    private List<IngredientWithSize> inputs = new ArrayList<>();
    private ItemStack result;

    public static @NotNull ChamberRecipeBuilder newBuilder(int time)
    {
        return new ChamberRecipeBuilder(new MetaBaseRecipe.ResourcesInfo(
                new MetaBaseRecipe.BiomassInfo(false, 0),
                Optional.empty(),
                Optional.empty(),
                time));
    }

    private ChamberRecipeBuilder(@NotNull MetaBaseRecipe.ResourcesInfo info)
    {
        super(info);
    }

    public ChamberRecipeBuilder addInput(IngredientWithSize ingredient)
    {
        if (this.inputs.size() < 12)
            this.inputs.add(ingredient);
        return this;
    }

    public ChamberRecipeBuilder setResult(@NotNull ItemStack result)
    {
        this.result = result;
        return this;
    }


    @Override
    protected ChamberRecipe getRecipe()
    {
        return new ChamberRecipe(this.inputs, this.info.time(), this.result);
    }

    @Override
    public @NotNull Item getResult()
    {
        return this.result.getItem();
    }
}
