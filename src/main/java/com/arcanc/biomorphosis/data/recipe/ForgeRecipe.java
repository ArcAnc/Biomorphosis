/**
 * @author ArcAnc
 * Created at: 04.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.display.ForgeRecipeDisplay;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.ForgeRecipeInput;
import com.arcanc.biomorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeRecipe extends BioBaseRecipe<ForgeRecipeInput>
{
    public static final List<ForgeRecipe> RECIPES = new ArrayList<>();

    private final IngredientWithSize input;
    private final ItemStack result;

    private PlacementInfo placementInfo;

    public ForgeRecipe(IngredientWithSize input, @NotNull ResourcesInfo resources, ItemStack result)
    {
        super(resources);
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(@NotNull ForgeRecipeInput input, @NotNull Level level)
    {
        ItemStack inputItem = input.getItem(0);
        return this.input.test(inputItem) && this.input.amount() <= inputItem.getCount() && super.matches(input, level);
    }

    public IngredientWithSize input()
    {
        return this.input;
    }

    public ItemStack result()
    {
        return this.result;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull ForgeRecipeInput input, HolderLookup.@NotNull Provider registries)
    {
        return this.result.copy();
    }

    @Override
    public @NotNull RecipeSerializer<ForgeRecipe> getSerializer()
    {
        return Registration.RecipeReg.FORGE_RECIPE.getSerializer().get();
    }

    @Override
    public @NotNull RecipeType<ForgeRecipe> getType()
    {
        return Registration.RecipeReg.FORGE_RECIPE.getRecipeType().get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo()
    {
        if (this.placementInfo == null)
        {
            List<Optional<Ingredient>> list = new ArrayList<>();
            list.add(Optional.of(this.input.toVanilla()));

            this.placementInfo = PlacementInfo.createFromOptionals(list);
        }
        return this.placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory()
    {
        return Registration.RecipeReg.FORGE_RECIPE.getCategory().get();
    }

    @Override
    public @NotNull List<RecipeDisplay> display()
    {
        return List.of(new ForgeRecipeDisplay(
                this.input.display(),
                new ResourcesDisplay(this.getResources()),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemStackSlotDisplay(new ItemStack(Registration.BlockReg.FORGE.get()))));
    }

    public static class ForgeRecipeSerializer implements RecipeSerializer<ForgeRecipe>
    {

        public static final MapCodec<ForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
                group(
                        IngredientWithSize.CODEC.fieldOf("input").forGetter(ForgeRecipe :: input),
                        ResourcesInfo.CODEC.fieldOf("resources").forGetter(ForgeRecipe :: getResources),
                        ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(ForgeRecipe :: result)).
                apply(instance, ForgeRecipe :: new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ForgeRecipe> STREAM_CODEC = StreamCodec.composite(
                IngredientWithSize.STREAM_CODEC,
                ForgeRecipe :: input,
                ResourcesInfo.STREAM_CODEC,
                ForgeRecipe :: getResources,
                ItemStack.OPTIONAL_STREAM_CODEC,
                ForgeRecipe :: result,
                ForgeRecipe :: new);

        @Override
        public @NotNull MapCodec<ForgeRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ForgeRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
