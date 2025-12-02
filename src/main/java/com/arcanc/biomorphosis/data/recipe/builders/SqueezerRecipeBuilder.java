/**
 * @author ArcAnc
 * Created at: 02.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.builders;


import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.SqueezerRecipe;
import com.arcanc.biomorphosis.data.recipe.StomachRecipe;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.SqueezerRecipeInput;
import com.arcanc.biomorphosis.data.recipe.input.StomachRecipeInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SqueezerRecipeBuilder extends BioBaseRecipeBuilder<SqueezerRecipeBuilder, SqueezerRecipe, SqueezerRecipeInput>
{
	private IngredientWithSize input;
	private FluidStack result;
	
	public static @NotNull SqueezerRecipeBuilder newBuilder(@NotNull BioBaseRecipe.ResourcesInfo info)
	{
		return new SqueezerRecipeBuilder(info);
	}
	
	private SqueezerRecipeBuilder(BioBaseRecipe.ResourcesInfo info)
	{
		super(info);
	}
	
	@Override
	protected SqueezerRecipe getRecipe()
	{
		return new SqueezerRecipe(this.input, this.info, this.result);
	}
	
	public SqueezerRecipeBuilder setInput(@NotNull IngredientWithSize input)
	{
		this.input = input;
		return this;
	}
	
	public SqueezerRecipeBuilder setResult(@NotNull FluidStack result)
	{
		this.result = result;
		return this;
	}
	
	@Override
	public @NotNull Item getResult()
	{
		return ItemStack.EMPTY.getItem();
	}
}