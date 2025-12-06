/**
 * @author ArcAnc
 * Created at: 07.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.display;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChamberRecipeDisplay extends BioBaseRecipeDisplay
{

    public static final MapCodec<ChamberRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    SlotDisplay.Composite.MAP_CODEC.fieldOf("input").forGetter(ChamberRecipeDisplay :: getInput),
                    ResourcesDisplay.CODEC.fieldOf("resources").forGetter(ChamberRecipeDisplay :: getResourcesDisplay),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(ChamberRecipeDisplay :: result),
                    SlotDisplay.CODEC.fieldOf("craftingStation").forGetter(ChamberRecipeDisplay :: craftingStation)).
            apply(instance, ChamberRecipeDisplay :: new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChamberRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.Composite.STREAM_CODEC,
            ChamberRecipeDisplay :: getInput,
            ResourcesDisplay.STREAM_CODEC,
            ChamberRecipeDisplay :: getResourcesDisplay,
            SlotDisplay.STREAM_CODEC,
            ChamberRecipeDisplay :: result,
            SlotDisplay.STREAM_CODEC,
            ChamberRecipeDisplay :: craftingStation,
            ChamberRecipeDisplay :: new);

    private final SlotDisplay.Composite input;
    private final SlotDisplay result;
    private final SlotDisplay craftingStation;

    public ChamberRecipeDisplay(SlotDisplay.Composite input,
                                ResourcesDisplay resources,
                                SlotDisplay result,
                                SlotDisplay craftingStation)
    {
        super(resources);
        this.input = input;
        this.result = result;
        this.craftingStation = craftingStation;
    }


    public ChamberRecipeDisplay(SlotDisplay.Composite input,
                                int time,
                                SlotDisplay result,
                                SlotDisplay craftingStation)
    {
        this(input, new ResourcesDisplay(new BioBaseRecipe.ResourcesInfo(
                new BioBaseRecipe.BiomassInfo(false, 0),
                Optional.empty(),
                Optional.empty(),
                time)),
                result,
                craftingStation);
    }

    public SlotDisplay.Composite getInput()
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
    public @NotNull Type<ChamberRecipeDisplay> type()
    {
        return Registration.RecipeReg.CHAMBER_RECIPE.getDisplay().get();
    }
}
