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
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

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
        //threeByThreePacker(RecipeCategory.MISC, Registration.ItemReg.FLESH_PIECE, Registration.BlockReg.FLESH);
        nineBlockStorageRecipes(RecipeCategory.MISC, Registration.ItemReg.FLESH_PIECE, RecipeCategory.MISC, Registration.BlockReg.FLESH);
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
