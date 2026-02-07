/**
 * @author ArcAnc
 * Created at: 31.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.display.CrusherRecipeDisplay;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.CrusherRecipeInput;
import com.arcanc.biomorphosis.data.recipe.slot_display.ItemStackWithChanceDisplay;
import com.arcanc.biomorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
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

public class CrusherRecipe extends BioBaseRecipe<CrusherRecipeInput>
{
    public static final List<CrusherRecipe> RECIPES = new ArrayList<>();

    private final IngredientWithSize input;
    private final ItemStack result;
    private final List<StackWithChance> secondaryResults;

    private PlacementInfo placementInfo;

    public CrusherRecipe(IngredientWithSize input, @NotNull ResourcesInfo resourcesInfo, ItemStack result)
    {
        this(input, resourcesInfo, result, List.of());
    }
    public CrusherRecipe(IngredientWithSize input, @NotNull ResourcesInfo resources, ItemStack result, List<StackWithChance> secondaryResults)
    {
        super(resources);
        this.input = input;
        this.result = result;
        this.secondaryResults = secondaryResults;
    }

    @Override
    public boolean matches(@NotNull CrusherRecipeInput input, @NotNull Level level)
    {
        ItemStack inputItem = input.getItem(0);
        return this.input.test(inputItem) && this.input.amount() <= inputItem.getCount() && super.matches(input, level);
    }

    public IngredientWithSize input()
    {
        return input;
    }

    public ItemStack result()
    {
        return result;
    }

    public List<StackWithChance> secondaryResults()
    {
        return secondaryResults;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CrusherRecipeInput input, HolderLookup.@NotNull Provider registries)
    {
        return this.result.copy();
    }

    @Override
    public @NotNull RecipeSerializer<CrusherRecipe> getSerializer()
    {
        return Registration.RecipeReg.CRUSHER_RECIPE.getSerializer().get();
    }

    @Override
    public @NotNull RecipeType<CrusherRecipe> getType()
    {
        return Registration.RecipeReg.CRUSHER_RECIPE.getRecipeType().get();
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
        return Registration.RecipeReg.CRUSHER_RECIPE.getCategory().get();
    }

    @Override
    public @NotNull List<RecipeDisplay> display()
    {
        return List.of(new CrusherRecipeDisplay(
                this.input.display(),
                new ResourcesDisplay(this.getResources()),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.Composite(this.secondaryResults.stream().
                        map(ItemStackWithChanceDisplay :: new).
                        map(display -> (SlotDisplay)display).
                        toList()),
                new SlotDisplay.ItemStackSlotDisplay(new ItemStack(Registration.BlockReg.CRUSHER.get()))));
    }

    public static class CrusherRecipeSerializer implements RecipeSerializer<CrusherRecipe>
    {
        public static final MapCodec<CrusherRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
                group(
                        IngredientWithSize.CODEC.fieldOf("input").forGetter(CrusherRecipe :: input),
                        ResourcesInfo.CODEC.fieldOf("resources").forGetter(CrusherRecipe :: getResources),
                        ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(CrusherRecipe :: result),
                        StackWithChance.CODEC.listOf().fieldOf("secondary_results").forGetter(CrusherRecipe :: secondaryResults)).
                apply(instance, CrusherRecipe :: new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> STREAM_CODEC = StreamCodec.composite(
                IngredientWithSize.STREAM_CODEC,
                CrusherRecipe :: input,
                ResourcesInfo.STREAM_CODEC,
                CrusherRecipe :: getResources,
                ItemStack.OPTIONAL_STREAM_CODEC,
                CrusherRecipe :: result,
                ByteBufCodecs.<RegistryFriendlyByteBuf, StackWithChance>list().
                        apply(StackWithChance.STREAM_CODEC),
                CrusherRecipe :: secondaryResults,
                CrusherRecipe :: new);

        @Override
        public @NotNull MapCodec<CrusherRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
