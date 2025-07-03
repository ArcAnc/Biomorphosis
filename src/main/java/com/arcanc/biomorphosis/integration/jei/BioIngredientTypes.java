/**
 * @author ArcAnc
 * Created at: 02.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jei;

import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.NotNull;

public class BioIngredientTypes
{
    public static final IIngredientType<IngredientWithSize> SIZED_INGREDIENT_TYPE = new IIngredientType<>()
    {
        @Override
        public @NotNull Class<IngredientWithSize> getIngredientClass()
        {
            return IngredientWithSize.class;
        }
    };
}
