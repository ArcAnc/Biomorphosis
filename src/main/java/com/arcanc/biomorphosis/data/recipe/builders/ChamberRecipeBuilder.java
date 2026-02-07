/**
 * @author ArcAnc
 * Created at: 07.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.builders;

import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.ChamberRecipe;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.ChamberRecipeInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChamberRecipeBuilder extends BioBaseRecipeBuilder<ChamberRecipeBuilder, ChamberRecipe, ChamberRecipeInput>
{
    private List<IngredientWithSize> inputs = new ArrayList<>();
    private ItemStack result;

    public static @NotNull ChamberRecipeBuilder newBuilder(int time)
    {
        return new ChamberRecipeBuilder(new BioBaseRecipe.ResourcesInfo(
                new BioBaseRecipe.BiomassInfo(false, 0),
                Optional.empty(),
                Optional.empty(),
                time));
    }

    private ChamberRecipeBuilder(@NotNull BioBaseRecipe.ResourcesInfo info)
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
