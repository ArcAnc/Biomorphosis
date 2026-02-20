/**
 * @author ArcAnc
 * Created at: 19.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.StomachRecipeInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StomachRecipe extends BioBaseRecipe<StomachRecipeInput>
{
    public static final List<StomachRecipe> RECIPES = new ArrayList<>();

    private final IngredientWithSize input;
    private final FluidStack result;

    public StomachRecipe(String id, IngredientWithSize input, @NotNull ResourcesInfo resources, FluidStack result)
    {
        super(id, resources);
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(@NotNull StomachRecipeInput input, @NotNull Level level)
    {
        ItemStack inputItem = input.getItem(0);
        return this.input.test(inputItem) && this.input.amount() <= inputItem.getCount() && super.matches(input, level);
    }

    public IngredientWithSize input()
    {
        return input;
    }

    public FluidStack result()
    {
        return result;
    }
    
    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    public @NotNull ItemStack assemble(@NotNull StomachRecipeInput input, HolderLookup.@NotNull Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<StomachRecipe> getSerializer()
    {
        return Registration.RecipeReg.STOMACH_RECIPE.getSerializer().get();
    }

    @Override
    public @NotNull RecipeType<StomachRecipe> getType()
    {
        return Registration.RecipeReg.STOMACH_RECIPE.getRecipeType().get();
    }

    public static class StomachRecipeSerializer implements RecipeSerializer<StomachRecipe>
    {

        public static final MapCodec<StomachRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
                group(
                        Codec.STRING.fieldOf("id").forGetter(StomachRecipe :: getGroup),
                        IngredientWithSize.CODEC.fieldOf("input").forGetter(StomachRecipe :: input),
                        ResourcesInfo.CODEC.fieldOf("resources").forGetter(StomachRecipe :: getResources),
                        FluidStack.OPTIONAL_CODEC.fieldOf("result").forGetter(StomachRecipe :: result)).
                apply(instance, StomachRecipe :: new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StomachRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                StomachRecipe :: getGroup,
                IngredientWithSize.STREAM_CODEC,
                StomachRecipe :: input,
                ResourcesInfo.STREAM_CODEC,
                StomachRecipe :: getResources,
                FluidStack.OPTIONAL_STREAM_CODEC,
                StomachRecipe :: result,
                StomachRecipe :: new);

        @Override
        public @NotNull MapCodec<StomachRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, StomachRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
