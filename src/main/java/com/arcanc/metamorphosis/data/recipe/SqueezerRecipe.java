/**
 * @author ArcAnc
 * Created at: 02.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.recipe;


import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.recipe.display.SqueezerRecipeDisplay;
import com.arcanc.metamorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.metamorphosis.data.recipe.input.SqueezerRecipeInput;
import com.arcanc.metamorphosis.data.recipe.slot_display.ResourcesDisplay;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.display.FluidStackSlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqueezerRecipe extends MetaBaseRecipe<SqueezerRecipeInput>
{
	public static final List<SqueezerRecipe> RECIPES = new ArrayList<>();
	
	private final IngredientWithSize input;
	private final FluidStack result;
	
	private PlacementInfo placementInfo;
	
	public SqueezerRecipe(IngredientWithSize input, @NotNull ResourcesInfo resources, FluidStack result)
	{
		super(resources);
		this.input = input;
		this.result = result;
	}
	
	@Override
	public boolean matches(@NotNull SqueezerRecipeInput input, @NotNull Level level)
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
	public @NotNull ItemStack assemble(@NotNull SqueezerRecipeInput input, HolderLookup.@NotNull Provider registries)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public @NotNull RecipeSerializer<SqueezerRecipe> getSerializer()
	{
		return Registration.RecipeReg.SQUEEZER_RECIPE.getSerializer().get();
	}
	
	@Override
	public @NotNull RecipeType<SqueezerRecipe> getType()
	{
		return Registration.RecipeReg.SQUEEZER_RECIPE.getRecipeType().get();
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
		return Registration.RecipeReg.SQUEEZER_RECIPE.getCategory().get();
	}
	
	@Override
	public @NotNull List<RecipeDisplay> display()
	{
		return List.of(new SqueezerRecipeDisplay(
				this.input.display(),
				new ResourcesDisplay(this.getResources()),
				new FluidStackSlotDisplay(this.result),
				new SlotDisplay.ItemStackSlotDisplay(new ItemStack(Registration.BlockReg.SQUEEZER.get()))));
	}
	
	public static class SqueezerRecipeSerializer implements RecipeSerializer<SqueezerRecipe>
	{
		
		public static final MapCodec<SqueezerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
				group(
				IngredientWithSize.CODEC.fieldOf("input").forGetter(SqueezerRecipe :: input),
				ResourcesInfo.CODEC.fieldOf("resources").forGetter(SqueezerRecipe :: getResources),
				FluidStack.OPTIONAL_CODEC.fieldOf("result").forGetter(SqueezerRecipe :: result)).
				apply(instance, SqueezerRecipe :: new));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, SqueezerRecipe> STREAM_CODEC = StreamCodec.composite(
				IngredientWithSize.STREAM_CODEC,
				SqueezerRecipe :: input,
				ResourcesInfo.STREAM_CODEC,
				SqueezerRecipe :: getResources,
				FluidStack.OPTIONAL_STREAM_CODEC,
				SqueezerRecipe :: result,
				SqueezerRecipe :: new);
		
		@Override
		public @NotNull MapCodec<SqueezerRecipe> codec()
		{
			return CODEC;
		}
		
		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, SqueezerRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
