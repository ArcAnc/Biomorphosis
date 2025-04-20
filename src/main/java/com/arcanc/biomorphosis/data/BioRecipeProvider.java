/**
 * @author ArcAnc
 * Created at: 30.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.builders.CrusherRecipeBuilder;
import com.arcanc.biomorphosis.data.recipe.builders.StomachRecipeBuilder;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BioRecipeProvider extends RecipeProvider
{

    protected BioRecipeProvider(HolderLookup.Provider registries, RecipeOutput output)
    {
        super(registries, output);
    }

    @Override
    protected void buildRecipes()
    {
        nineBlockStorageRecipes(RecipeCategory.MISC, Registration.ItemReg.FLESH_PIECE, RecipeCategory.MISC, Registration.BlockReg.FLESH);
        shapeless(RecipeCategory.MISC, Registration.ItemReg.BOOK).
                unlockedBy(getHasName(Registration.ItemReg.QUEENS_BRAIN), has(Registration.ItemReg.QUEENS_BRAIN)).
                unlockedBy(getHasName(Items.WRITABLE_BOOK), has(Items.WRITABLE_BOOK)).
                requires(Registration.ItemReg.QUEENS_BRAIN).
                requires(Items.WRITABLE_BOOK).
                save(this.output);

        CrusherRecipeBuilder.newBuilder(new BioBaseRecipe.ResourcesInfo(
                new BioBaseRecipe.BiomassInfo(true,10),
                Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 1, 2.0f)),
                Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 1, 0.5f)),
                200)).
                setInput(new IngredientWithSize(Ingredient.of(Blocks.COBBLESTONE))).
                setResult(new ItemStack(Blocks.GRAVEL)).
                addSecondaryOutput(new StackWithChance(new ItemStack(Blocks.SAND), 0.15f)).
        unlockedBy(getHasName(Blocks.COBBLESTONE), has(Blocks.COBBLESTONE)).
        save(this.output, Database.rl("gravel_from_cobblestone").toString());

        StomachRecipeBuilder.newBuilder(
                new BioBaseRecipe.ResourcesInfo(
                    new BioBaseRecipe.BiomassInfo(false, 2),
                    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 1, 1.5f)),
                    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 1, 0.5f)),
                    100)).
                setInput(new IngredientWithSize(tag(ItemTags.MEAT))).
                setResult(new FluidStack(Registration.FluidReg.BIOMASS.still(), 700)).
        unlockedBy("has_meat", has(ItemTags.MEAT)).
        save(this.output, Database.rl("biomass_from_meat").toString());
    }

    public static class Runner extends RecipeProvider.Runner
    {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(output, registries);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries, @NotNull RecipeOutput output)
        {
            return new BioRecipeProvider(registries, output);
        }

        @Override
        public @NotNull String getName()
        {
            return Database.MOD_NAME + ": Recipes";
        }

    }
}
