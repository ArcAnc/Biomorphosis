/**
 * @author ArcAnc
 * Created at: 02.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.recipe.display;


import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.recipe.MetaBaseRecipe;
import com.arcanc.metamorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SqueezerRecipeDisplay extends MetaBaseRecipeDisplay
{
	public static final MapCodec<SqueezerRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SlotDisplay.CODEC.fieldOf("input").forGetter(SqueezerRecipeDisplay :: getInput),
			ResourcesDisplay.CODEC.fieldOf("resources").forGetter(SqueezerRecipeDisplay :: getResourcesDisplay),
			SlotDisplay.CODEC.fieldOf("result").forGetter(SqueezerRecipeDisplay :: result),
			SlotDisplay.CODEC.fieldOf("craftingStation").forGetter(SqueezerRecipeDisplay :: craftingStation)).
			apply(instance, SqueezerRecipeDisplay :: new));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, SqueezerRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
			SlotDisplay.STREAM_CODEC,
			SqueezerRecipeDisplay :: getInput,
			ResourcesDisplay.STREAM_CODEC,
			SqueezerRecipeDisplay :: getResourcesDisplay,
			SlotDisplay.STREAM_CODEC,
			SqueezerRecipeDisplay :: result,
			SlotDisplay.STREAM_CODEC,
			SqueezerRecipeDisplay :: craftingStation,
			SqueezerRecipeDisplay :: new);
	
	private final SlotDisplay input;
	private final SlotDisplay result;
	private final SlotDisplay craftingStation;
	
	public SqueezerRecipeDisplay(SlotDisplay input,
	                            ResourcesDisplay resources,
	                            SlotDisplay result,
	                            SlotDisplay craftingStation)
	{
		super(resources);
		this.input = input;
		this.result = result;
		this.craftingStation = craftingStation;
	}
	
	
	public SqueezerRecipeDisplay(SlotDisplay.Composite input,
	                            int time,
	                            SlotDisplay result,
	                            SlotDisplay craftingStation)
	{
		this(input, new ResourcesDisplay(new MetaBaseRecipe.ResourcesInfo(
						new MetaBaseRecipe.BiomassInfo(false, 0),
						Optional.empty(),
						Optional.empty(),
						time)),
				result,
				craftingStation);
	}
	
	public SlotDisplay getInput()
	{
		return input;
	}
	
	@Override
	public @NotNull SlotDisplay result()
	{
		return this.result;
	}
	
	@Override
	public @NotNull SlotDisplay craftingStation() {
		return this.craftingStation;
	}
	
	@Override
	public @NotNull Type<SqueezerRecipeDisplay> type()
	{
		return Registration.RecipeReg.SQUEEZER_RECIPE.getDisplay().get();
	}
}
