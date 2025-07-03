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
import com.arcanc.biomorphosis.data.recipe.builders.ChamberRecipeBuilder;
import com.arcanc.biomorphosis.data.recipe.builders.CrusherRecipeBuilder;
import com.arcanc.biomorphosis.data.recipe.builders.ForgeRecipeBuilder;
import com.arcanc.biomorphosis.data.recipe.builders.StomachRecipeBuilder;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BioRecipeProvider extends RecipeProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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

        shaped(RecipeCategory.MISC, Registration.BlockReg.NORPH_SOURCE).
                pattern(" R ").
                pattern("FHF").
                pattern("FSF").
                define('F', Registration.BlockReg.FLESH).
                define('H', Items.CREAKING_HEART).
                define('R', Tags.Items.BRICKS_RESIN).
                define('S', Tags.Items.STONES).
                unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
                unlockedBy(getHasName(Items.CREAKING_HEART), has(Items.CREAKING_HEART)).
                unlockedBy(getHasName(Items.RESIN_BRICK), has(Tags.Items.BRICKS_RESIN)).
                unlockedBy(getHasName(Items.STONE), has(Tags.Items.STONES)).
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

        ChamberRecipeBuilder.newBuilder(400).
                addInput(new IngredientWithSize(tag(Tags.Items.BARRELS_WOODEN))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.NORPH))).
                setResult(new ItemStack(Registration.BlockReg.FLUID_STORAGE)).
        unlockedBy(getHasName(Items.BARREL), has(Tags.Items.BARRELS_WOODEN)).
        unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
        unlockedBy(getHasName(Registration.BlockReg.NORPH), has(Registration.BlockReg.NORPH)).
        save(this.output, Database.rl("fluid_storage_from_chamber").toString());

        ChamberRecipeBuilder.newBuilder(400).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.NORPH))).
                addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
                addInput(new IngredientWithSize(tag(Tags.Items.ENDER_PEARLS))).
                setResult(new ItemStack(Registration.BlockReg.FLUID_TRANSMITTER)).
        unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
        unlockedBy(getHasName(Registration.BlockReg.NORPH), has(Registration.BlockReg.NORPH)).
        unlockedBy(getHasName(Items.SLIME_BALL), has(Tags.Items.SLIME_BALLS)).
        unlockedBy(getHasName(Items.ENDER_PEARL), has(Tags.Items.ENDER_PEARLS)).
        save(this.output, Database.rl("fluid_transmitter_from_chamber").toString());

        ChamberRecipeBuilder.newBuilder(400).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.NORPH))).
                addInput(new IngredientWithSize(Ingredient.of(Items.STONE))).
                addInput(new IngredientWithSize(Ingredient.of(Items.CHEST))).
                setResult(new ItemStack(Registration.BlockReg.STOMACH)).
        unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
        unlockedBy(getHasName(Registration.BlockReg.NORPH), has(Registration.BlockReg.NORPH)).
        unlockedBy(getHasName(Items.STONE), has(Items.STONE)).
        unlockedBy(getHasName(Items.CHEST), has(Items.CHEST)).
        save(this.output, Database.rl("stomach_from_chamber").toString());

        ChamberRecipeBuilder.newBuilder(400).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.NORPH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Items.SHEARS))).
                addInput(new IngredientWithSize(Ingredient.of(Items.STONE))).
                addInput(new IngredientWithSize(Ingredient.of(Items.STONE))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                setResult(new ItemStack(Registration.BlockReg.CRUSHER)).
        unlockedBy(getHasName(Items.SHEARS), has(Items.SHEARS)).
        unlockedBy(getHasName(Registration.BlockReg.NORPH), has(Registration.BlockReg.NORPH)).
        unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
        save(this.output, Database.rl("crusher_from_chamber").toString());

        ChamberRecipeBuilder.newBuilder(400).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.NORPH))).
                addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
                addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
                addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
                addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
                addInput(new IngredientWithSize(Ingredient.of(Items.COMPARATOR))).
                setResult(new ItemStack(Registration.BlockReg.CATCHER)).
        unlockedBy(getHasName(Registration.BlockReg.NORPH), has(Registration.BlockReg.NORPH)).
        unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
        unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS)).
        unlockedBy(getHasName(Items.COMPARATOR), has(Items.COMPARATOR)).
        save(this.output, Database.rl("catcher_from_chamber").toString());

        ChamberRecipeBuilder.newBuilder(400).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.NORPH))).
                addInput(new IngredientWithSize(Ingredient.of(Items.BLAST_FURNACE))).
                addInput(new IngredientWithSize(Ingredient.of(Items.BLAST_FURNACE))).
                setResult(new ItemStack(Registration.BlockReg.FORGE)).
        unlockedBy(getHasName(Registration.BlockReg.NORPH), has(Registration.BlockReg.NORPH)).
        unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
        unlockedBy(getHasName(Items.BLAST_FURNACE), has(Items.BLAST_FURNACE)).
        save(this.output, Database.rl("forge_from_chamber").toString());

        shaped(RecipeCategory.MISC, Registration.BlockReg.MULTIBLOCK_MORPHER).
                pattern("SWS").
                pattern("NSN").
                define('W', tag(Tags.Items.BUCKETS_WATER)).
                define('S', tag(Tags.Items.STONES)).
                define('N', Registration.BlockReg.NORPH).
                unlockedBy(getHasName(Items.WATER_BUCKET), has(Tags.Items.BUCKETS_WATER)).
                unlockedBy(getHasName(Items.STONE), has(Tags.Items.STONES)).
                unlockedBy(getHasName(Registration.BlockReg.NORPH), has(Registration.BlockReg.NORPH)).
                save(this.output);

        ChamberRecipeBuilder.newBuilder(150).
                addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
                addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
                addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
                addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
                addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLUID_STORAGE))).
                setResult(new ItemStack(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE)).
                unlockedBy(getHasName(Items.SLIME_BALL), has(Tags.Items.SLIME_BALLS)).
                unlockedBy(getHasName(Registration.BlockReg.FLUID_STORAGE), has(Registration.BlockReg.FLUID_STORAGE)).
                save(this.output, Database.rl("multiblock_fluid_storage_from_chamber").toString());

        generateBioForgeVanillaEnhancedRecipes();
    }

    private void generateBioForgeVanillaEnhancedRecipes()
    {
        Path path = FMLPaths.GAMEDIR.get().resolve("vanilla_recipes");

        try(Stream<Path> paths = Files.walk(path))
        {
            paths.filter(possiblePath -> possiblePath.toString().endsWith(".json")).
                    forEach(recipePath ->
                    {
                        try (Reader recipeReader = Files.newBufferedReader(recipePath))
                        {
                            JsonObject jsonObject = GSON.fromJson(recipeReader, JsonObject.class);
                            if (jsonObject.has("type") && !jsonObject.get("type").getAsString().equals("minecraft:smelting"))
                                return;
                            String recipeName = recipePath.getFileName().toString();
                            String resultedRecipeName = recipeName.substring(0, recipeName.length() - 5);
                            Ingredient input = Ingredient.of(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(jsonObject.get("ingredient").getAsString())));
                            ItemStack result = ItemStack.OPTIONAL_CODEC.parse(JsonOps.INSTANCE, jsonObject.getAsJsonObject("result")).getOrThrow();
                            int cookingTime = jsonObject.has("cookingtime") ? jsonObject.get("cookingtime").getAsInt() : 200;

                            ForgeRecipeBuilder.newBuilder(
                                            new BioBaseRecipe.ResourcesInfo(
                                                    new BioBaseRecipe.BiomassInfo(true, 2),
                                                    Optional.empty(),
                                                    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 1, 0.5f)),
                                                    cookingTime)).
                                    setInput(new IngredientWithSize(input)).
                                    setResult(result).
                                    unlockedBy(getHasName(input.getValues().get(0).value()), has(input.getValues().get(0).value())).
                                    save(this.output, Database.rl(resultedRecipeName).toString());
                        }
                        catch (Exception e)
                        {
                            Database.LOGGER.warn("Failed to parse vanilla recipe: {} - {}", recipePath, e);
                        }
                    });
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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
