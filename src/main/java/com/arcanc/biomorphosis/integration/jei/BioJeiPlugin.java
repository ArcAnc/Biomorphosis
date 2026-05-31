/**
 * @author ArcAnc
 * Created at: 01.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jei;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.*;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.integration.jei.ingredient.IngredientWithSizeHelper;
import com.arcanc.biomorphosis.integration.jei.ingredient.IngredientWithSizeRenderer;
import com.arcanc.biomorphosis.integration.jei.ingredient.StackWithChanceHelper;
import com.arcanc.biomorphosis.integration.jei.ingredient.StackWithChanceRenderer;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Set;

@JeiPlugin
public class BioJeiPlugin implements IModPlugin
{
    @Override
    public void registerIngredients(IModIngredientRegistration registration)
    {
        registration.register(BioIngredientTypes.STACK_WITH_CHANCE_TYPE,
                Set.of(),
                new StackWithChanceHelper(),
                new StackWithChanceRenderer(),
                StackWithChance.CODEC);

       registration.register(BioIngredientTypes.INGREDIENT_WITH_SIZE_TYPE,
               Set.of(),
               new IngredientWithSizeHelper(),
               new IngredientWithSizeRenderer(),
               IngredientWithSize.CODEC.codec());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ChamberRecipeCategory(guiHelper),
                new CrusherRecipeCategory(guiHelper),
                new SqueezerRecipeCategory(guiHelper),
                new StomachRecipeCategory(guiHelper),
                new ForgeRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        ClientLevel level = RenderHelper.mc().level;
        if (level == null)
            return;
        RecipeManager recipeManager = level.getRecipeManager();
        registration.addRecipes(ChamberRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(Registration.RecipeReg.CHAMBER_RECIPE.getRecipeType().get()));
        registration.addRecipes(CrusherRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(Registration.RecipeReg.CRUSHER_RECIPE.getRecipeType().get()));
	    registration.addRecipes(SqueezerRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(Registration.RecipeReg.SQUEEZER_RECIPE.getRecipeType().get()));
        registration.addRecipes(StomachRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(Registration.RecipeReg.STOMACH_RECIPE.getRecipeType().get()));
        registration.addRecipes(ForgeRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(Registration.RecipeReg.FORGE_RECIPE.getRecipeType().get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(Registration.BlockReg.MULTIBLOCK_CHAMBER, ChamberRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(Registration.BlockReg.CRUSHER, CrusherRecipeCategory.RECIPE_TYPE);
	    registration.addRecipeCatalyst(Registration.BlockReg.SQUEEZER, SqueezerRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(Registration.BlockReg.STOMACH, StomachRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(Registration.BlockReg.FORGE, ForgeRecipeCategory.RECIPE_TYPE);
    }
    
    @Override
    public ResourceLocation getPluginUid()
    {
        return Database.Integration.JeiInfo.ID;
    }
}
