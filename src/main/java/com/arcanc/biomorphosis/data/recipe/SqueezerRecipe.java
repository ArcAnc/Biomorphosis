/**
 * @author ArcAnc
 * Created at: 02.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.SqueezerRecipeInput;
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

import java.util.ArrayList;
import java.util.List;

public class SqueezerRecipe extends BioBaseRecipe<SqueezerRecipeInput>
{
	public static final List<SqueezerRecipe> RECIPES = new ArrayList<>();
	
	private final IngredientWithSize input;
	private final FluidStack result;
	
	public SqueezerRecipe(String id, IngredientWithSize input, ResourcesInfo resources, FluidStack result)
	{
		super(id, resources);
		this.input = input;
		this.result = result;
	}
	
	@Override
	public boolean matches(SqueezerRecipeInput input, Level level)
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
	public ItemStack getResultItem(HolderLookup.Provider registries)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack assemble(SqueezerRecipeInput input, HolderLookup.Provider registries)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public RecipeSerializer<SqueezerRecipe> getSerializer()
	{
		return Registration.RecipeReg.SQUEEZER_RECIPE.getSerializer().get();
	}
	
	@Override
	public RecipeType<SqueezerRecipe> getType()
	{
		return Registration.RecipeReg.SQUEEZER_RECIPE.getRecipeType().get();
	}
	
	public static class SqueezerRecipeSerializer implements RecipeSerializer<SqueezerRecipe>
	{
		
		public static final MapCodec<SqueezerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
				group(
						Codec.STRING.fieldOf("id").forGetter(SqueezerRecipe :: getGroup),
						IngredientWithSize.CODEC.fieldOf("input").forGetter(SqueezerRecipe :: input),
						ResourcesInfo.CODEC.fieldOf("resources").forGetter(SqueezerRecipe :: getResources),
						FluidStack.OPTIONAL_CODEC.fieldOf("result").forGetter(SqueezerRecipe :: result)).
						apply(instance, SqueezerRecipe :: new));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, SqueezerRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				SqueezerRecipe :: getGroup,
				IngredientWithSize.STREAM_CODEC,
				SqueezerRecipe :: input,
				ResourcesInfo.STREAM_CODEC,
				SqueezerRecipe :: getResources,
				FluidStack.OPTIONAL_STREAM_CODEC,
				SqueezerRecipe :: result,
				SqueezerRecipe :: new);
		
		@Override
		public MapCodec<SqueezerRecipe> codec()
		{
			return CODEC;
		}
		
		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SqueezerRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
