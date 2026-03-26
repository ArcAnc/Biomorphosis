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
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.ForgeRecipeInput;
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

import java.util.ArrayList;
import java.util.List;

public class ForgeRecipe extends BioBaseRecipe<ForgeRecipeInput>
{
    public static final List<ForgeRecipe> RECIPES = new ArrayList<>();

    private final IngredientWithSize input;
    private final ItemStack result;

    public ForgeRecipe(String id, IngredientWithSize input, ResourcesInfo resources, ItemStack result)
    {
        super(id, resources);
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(ForgeRecipeInput input, Level level)
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
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return this.result();
    }
    
    @Override
    public ItemStack assemble(ForgeRecipeInput input, HolderLookup.Provider registries)
    {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<ForgeRecipe> getSerializer()
    {
        return Registration.RecipeReg.FORGE_RECIPE.getSerializer().get();
    }

    @Override
    public RecipeType<ForgeRecipe> getType()
    {
        return Registration.RecipeReg.FORGE_RECIPE.getRecipeType().get();
    }

    public static class ForgeRecipeSerializer implements RecipeSerializer<ForgeRecipe>
    {

        public static final MapCodec<ForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
                group(
                        Codec.STRING.fieldOf("id").forGetter(ForgeRecipe :: getGroup),
                        IngredientWithSize.CODEC.fieldOf("input").forGetter(ForgeRecipe :: input),
                        ResourcesInfo.CODEC.fieldOf("resources").forGetter(ForgeRecipe :: getResources),
                        ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(ForgeRecipe :: result)).
                apply(instance, ForgeRecipe :: new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ForgeRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                ForgeRecipe :: getGroup,
                IngredientWithSize.STREAM_CODEC,
                ForgeRecipe :: input,
                ResourcesInfo.STREAM_CODEC,
                ForgeRecipe :: getResources,
                ItemStack.OPTIONAL_STREAM_CODEC,
                ForgeRecipe :: result,
                ForgeRecipe :: new);

        @Override
        public MapCodec<ForgeRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ForgeRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
