/**
 * @author ArcAnc
 * Created at: 07.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.display.ChamberRecipeDisplay;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.ChamberRecipeInput;
import com.arcanc.biomorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ChamberRecipe extends BioBaseRecipe<ChamberRecipeInput>
{
    private final List<IngredientWithSize> input = new ArrayList<>();
    private final ItemStack result;

    private PlacementInfo placementInfo;

    public ChamberRecipe(List<IngredientWithSize> inputs, int time, ItemStack result)
    {
        this(   inputs,
                new ResourcesInfo(
                    new BiomassInfo(false, 0),
                        Optional.empty(),
                        Optional.empty(),
                    time),
                result);
    }

    public ChamberRecipe(List<IngredientWithSize> inputs, @NotNull ResourcesInfo resources, ItemStack result)
    {
        super(resources);
        Preconditions.checkNotNull(inputs);
        Preconditions.checkState(inputs.size() < 12);
        this.input.addAll(inputs);
        this.result = result;
    }

    public List<IngredientWithSize> input()
    {
        return this.input;
    }

    public ItemStack result()
    {
        return this.result;
    }

    @Override
    public boolean matches(@NotNull ChamberRecipeInput input, @NotNull Level level)
    {
        List<ItemStack> available = Arrays.stream(input.stacks).
                filter(stack -> !stack.isEmpty()).
                map(ItemStack :: copy).
                toList();

        for (IngredientWithSize needed : this.input)
        {
            int remaining = needed.amount();
            
            for (ItemStack stack : available)
            {
                if (needed.ingredient().test(stack) && stack.getCount() > 0)
                {
                    int used = Math.min(remaining, stack.getCount());
                    stack.shrink(used);
                    remaining -= used;

                    if (remaining <= 0)
                        break;
                }
            }

            if (remaining > 0)
                return false;
        }

        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull ChamberRecipeInput input, HolderLookup.@NotNull Provider registries)
    {
        return this.result.copy();
    }

    @Override
    public @NotNull RecipeSerializer<ChamberRecipe> getSerializer()
    {
        return Registration.RecipeReg.CHAMBER_RECIPE.getSerializer().get();
    }

    @Override
    public @NotNull RecipeType<ChamberRecipe> getType()
    {
        return Registration.RecipeReg.CHAMBER_RECIPE.getRecipeType().get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo()
    {
        if (this.placementInfo == null)
        {
            List<Optional<Ingredient>> list = new ArrayList<>();
            this.input.forEach(ingredientWithSize -> list.add(Optional.of(ingredientWithSize.toVanilla())));

            this.placementInfo = PlacementInfo.createFromOptionals(list);
        }
        return this.placementInfo;
    }

    @Override
    public @NotNull List<RecipeDisplay> display()
    {
        return List.of(new ChamberRecipeDisplay(
            new SlotDisplay.Composite(this.input.stream().map(IngredientWithSize :: display).toList()),
            new ResourcesDisplay(this.getResources()),
            new SlotDisplay.ItemStackSlotDisplay(this.result),
            new SlotDisplay.ItemStackSlotDisplay(new ItemStack(Registration.BlockReg.MULTIBLOCK_CHAMBER.get()))));
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory()
    {
        return Registration.RecipeReg.CHAMBER_RECIPE.getCategory().get();
    }

    public static class ChamberRecipeSerializer implements RecipeSerializer<ChamberRecipe>
    {
        public static final MapCodec<ChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
                group(
                        Codec.list(IngredientWithSize.CODEC.codec()).fieldOf("input").forGetter(ChamberRecipe :: input),
                        ResourcesInfo.CODEC.fieldOf("resources").forGetter(ChamberRecipe :: getResources),
                        ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(ChamberRecipe :: result)).
                apply(instance, ChamberRecipe :: new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChamberRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.<RegistryFriendlyByteBuf, IngredientWithSize>list().
                        apply(IngredientWithSize.STREAM_CODEC),
                ChamberRecipe :: input,
                ResourcesInfo.STREAM_CODEC,
                ChamberRecipe :: getResources,
                ItemStack.OPTIONAL_STREAM_CODEC,
                ChamberRecipe :: result,
                ChamberRecipe :: new);

        @Override
        public @NotNull MapCodec<ChamberRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ChamberRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
