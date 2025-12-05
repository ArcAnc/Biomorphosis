/**
 * @author ArcAnc
 * Created at: 19.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.recipe.display;

import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

public class StomachRecipeDisplay extends MetaBaseRecipeDisplay
{
    public static final MapCodec<StomachRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    SlotDisplay.CODEC.fieldOf("input").forGetter(StomachRecipeDisplay :: getInput),
                    ResourcesDisplay.CODEC.fieldOf("resources").forGetter(StomachRecipeDisplay :: getResourcesDisplay),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(StomachRecipeDisplay :: result),
                    SlotDisplay.CODEC.fieldOf("craftingStation").forGetter(StomachRecipeDisplay :: craftingStation)).
            apply(instance, StomachRecipeDisplay :: new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StomachRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            StomachRecipeDisplay :: getInput,
            ResourcesDisplay.STREAM_CODEC,
            StomachRecipeDisplay :: getResourcesDisplay,
            SlotDisplay.STREAM_CODEC,
            StomachRecipeDisplay :: result,
            SlotDisplay.STREAM_CODEC,
            StomachRecipeDisplay :: craftingStation,
            StomachRecipeDisplay :: new);

    private final SlotDisplay input;
    private final SlotDisplay result;
    private final SlotDisplay craftingStation;

    public StomachRecipeDisplay(SlotDisplay input, ResourcesDisplay resources, SlotDisplay result, SlotDisplay craftingStation)
    {
        super(resources);
        this.input = input;
        this.result = result; //FluidStackSlotDisplay!!!
        this.craftingStation = craftingStation;
    }

    public SlotDisplay getInput()
    {
        return input;
    }

    @Override
    public @NotNull SlotDisplay result()
    {
        return result;
    }

    @Override
    public @NotNull SlotDisplay craftingStation()
    {
        return craftingStation;
    }

    @Override
    public @NotNull Type<StomachRecipeDisplay> type()
    {
        return Registration.RecipeReg.STOMACH_RECIPE.getDisplay().get();
    }
}
