/**
 * @author ArcAnc
 * Created at: 02.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.integration.jei;

import com.arcanc.metamorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.metamorphosis.util.inventory.item.StackWithChance;
import mezz.jei.api.ingredients.IIngredientType;

public class MetaIngredientTypes
{
    public static final IIngredientType<StackWithChance> STACK_WITH_CHANCE_TYPE = () -> StackWithChance.class;

    public static final IIngredientType<IngredientWithSize> INGREDIENT_WITH_SIZE_TYPE = () -> IngredientWithSize.class;
}
