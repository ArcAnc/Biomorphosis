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
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@JeiPlugin
public class BioJeiPlugin implements IModPlugin
{
    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registration)
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
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new ChamberRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CrusherRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	    registration.addRecipeCategories(new SqueezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new StomachRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration)
    {
        registration.addRecipes(ChamberRecipeCategory.RECIPE_TYPE, ChamberRecipe.RECIPES);
        registration.addRecipes(CrusherRecipeCategory.RECIPE_TYPE, CrusherRecipe.RECIPES);
	    registration.addRecipes(SqueezerRecipeCategory.RECIPE_TYPE, SqueezerRecipe.RECIPES);
        registration.addRecipes(StomachRecipeCategory.RECIPE_TYPE, StomachRecipe.RECIPES);
        registration.addRecipes(ForgeRecipeCategory.RECIPE_TYPE, ForgeRecipe.RECIPES);
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration)
    {
        registration.addCraftingStation(ChamberRecipeCategory.RECIPE_TYPE, new ItemStack(Registration.BlockReg.MULTIBLOCK_CHAMBER));
        registration.addCraftingStation(CrusherRecipeCategory.RECIPE_TYPE, new ItemStack(Registration.BlockReg.CRUSHER));
	    registration.addCraftingStation(SqueezerRecipeCategory.RECIPE_TYPE, new ItemStack(Registration.BlockReg.SQUEEZER));
        registration.addCraftingStation(StomachRecipeCategory.RECIPE_TYPE, new ItemStack(Registration.BlockReg.STOMACH));
        registration.addCraftingStation(ForgeRecipeCategory.RECIPE_TYPE, new ItemStack(Registration.BlockReg.FORGE));
    }

    @Override
    public @NotNull ResourceLocation getPluginUid()
    {
        return Database.Integration.JeiInfo.ID;
    }
}
