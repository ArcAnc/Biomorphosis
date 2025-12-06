/**
 * @author ArcAnc
 * Created at: 04.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.display;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

public class ForgeRecipeDisplay extends BioBaseRecipeDisplay
{
    public static final MapCodec<ForgeRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    SlotDisplay.CODEC.fieldOf("input").forGetter(ForgeRecipeDisplay :: input),
                    ResourcesDisplay.CODEC.fieldOf("resources").forGetter(ForgeRecipeDisplay :: getResourcesDisplay),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(ForgeRecipeDisplay :: result),
                    SlotDisplay.CODEC.fieldOf("craftingStation").forGetter(ForgeRecipeDisplay :: craftingStation)).
            apply(instance, ForgeRecipeDisplay :: new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            ForgeRecipeDisplay :: input,
            ResourcesDisplay.STREAM_CODEC,
            ForgeRecipeDisplay :: getResourcesDisplay,
            SlotDisplay.STREAM_CODEC,
            ForgeRecipeDisplay :: result,
            SlotDisplay.STREAM_CODEC,
            ForgeRecipeDisplay :: craftingStation,
            ForgeRecipeDisplay :: new);

    private final SlotDisplay input;
    private final SlotDisplay result;
    private final SlotDisplay craftingStation;

    public ForgeRecipeDisplay(SlotDisplay input, ResourcesDisplay resources, SlotDisplay result, SlotDisplay craftingStation)
    {
        super(resources);
        this.input = input;
        this.result = result;
        this.craftingStation = craftingStation;
    }

    public SlotDisplay input()
    {
        return this.input;
    }

    @Override
    public @NotNull SlotDisplay result()
    {
        return this.result;
    }

    @Override
    public @NotNull SlotDisplay craftingStation()
    {
        return this.craftingStation;
    }

    @Override
    public @NotNull Type<ForgeRecipeDisplay> type()
    {
        return Registration.RecipeReg.FORGE_RECIPE.getDisplay().get();
    }
}
