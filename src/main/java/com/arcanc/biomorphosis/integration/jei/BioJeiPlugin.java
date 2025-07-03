/**
 * @author ArcAnc
 * Created at: 01.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jei;

import com.arcanc.biomorphosis.data.recipe.ChamberRecipe;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.integration.jei.ingredient.IngredientWithSizeHelper;
import com.arcanc.biomorphosis.integration.jei.ingredient.IngredientWithSizeRenderer;
import com.arcanc.biomorphosis.util.Database;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@JeiPlugin
public class BioJeiPlugin implements IModPlugin
{
    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registration)
    {
       registration.register(BioIngredientTypes.SIZED_INGREDIENT_TYPE,
               Set.of(),
               new IngredientWithSizeHelper(),
               new IngredientWithSizeRenderer(),
               IngredientWithSize.CODEC.codec());
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new ChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration)
    {
        registration.addRecipes(ChamberRecipeCategory.RECIPE_TYPE, ChamberRecipe.CHAMBER_RECIPES);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid()
    {
        return Database.Integration.JeiInfo.ID;
    }
}
