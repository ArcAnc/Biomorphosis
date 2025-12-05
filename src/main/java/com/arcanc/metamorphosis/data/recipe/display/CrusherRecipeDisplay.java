/**
 * @author ArcAnc
 * Created at: 02.04.2025
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

public class CrusherRecipeDisplay extends MetaBaseRecipeDisplay
{
    public static final MapCodec<CrusherRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SlotDisplay.CODEC.fieldOf("input").forGetter(CrusherRecipeDisplay :: getInput),
                ResourcesDisplay.CODEC.fieldOf("resources").forGetter(CrusherRecipeDisplay :: getResourcesDisplay),
                SlotDisplay.CODEC.fieldOf("result").forGetter(CrusherRecipeDisplay :: result),
                SlotDisplay.CODEC.fieldOf("secondaryResult").forGetter(CrusherRecipeDisplay :: getSecondaryResults),
                SlotDisplay.CODEC.fieldOf("craftingStation").forGetter(CrusherRecipeDisplay :: craftingStation)).
            apply(instance, CrusherRecipeDisplay :: new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            CrusherRecipeDisplay :: getInput,
            ResourcesDisplay.STREAM_CODEC,
            CrusherRecipeDisplay :: getResourcesDisplay,
            SlotDisplay.STREAM_CODEC,
            CrusherRecipeDisplay :: result,
            SlotDisplay.STREAM_CODEC,
            CrusherRecipeDisplay :: getSecondaryResults,
            SlotDisplay.STREAM_CODEC,
            CrusherRecipeDisplay :: craftingStation,
            CrusherRecipeDisplay :: new);

    private final SlotDisplay input;
    private final SlotDisplay result;
    private final SlotDisplay secondaryResults;
    private final SlotDisplay craftingStation;

    public CrusherRecipeDisplay(SlotDisplay input, ResourcesDisplay resources, SlotDisplay result, SlotDisplay secondaryResults, SlotDisplay craftingStation)
    {
        super(resources);
        this.input = input;
        this.result = result;
        this.secondaryResults = secondaryResults;
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

    public SlotDisplay getSecondaryResults()
    {
        return secondaryResults;
    }

    @Override
    public @NotNull Type<CrusherRecipeDisplay> type()
    {
        return Registration.RecipeReg.CRUSHER_RECIPE.getDisplay().get();
    }
}
