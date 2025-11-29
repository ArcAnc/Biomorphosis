/**
 * @author ArcAnc
 * Created at: 02.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.slot_display;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.display.ForFluidStacks;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record ResourcesDisplay(BioBaseRecipe.ResourcesInfo resourcesInfo) implements SlotDisplay
{
    public static final MapCodec<ResourcesDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BioBaseRecipe.ResourcesInfo.CODEC.fieldOf("resources").forGetter(ResourcesDisplay :: resourcesInfo)).
            apply(instance, ResourcesDisplay :: new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourcesDisplay> STREAM_CODEC = StreamCodec.composite(
            BioBaseRecipe.ResourcesInfo.STREAM_CODEC,
            ResourcesDisplay :: resourcesInfo,
            ResourcesDisplay :: new);

    @Override
    public <T> @NotNull Stream<T> resolve(@NotNull ContextMap context, @NotNull DisplayContentsFactory<T> output)
    {
        return output instanceof ForFluidStacks<T> fluids ? Stream.of(
                fluids.forStack(new FluidStack(Registration.FluidReg.BIOMASS.still().get(), (resourcesInfo.biomass().perSecond() < 1) ? 1 : (int)resourcesInfo.biomass().perSecond())),
                fluids.forStack(new FluidStack(Registration.FluidReg.ACID.still().get(), resourcesInfo.acid().map(info -> info.perSecond() < 1 ? 1 : (int)info.perSecond()).orElse(0))),
                fluids.forStack(new FluidStack(Registration.FluidReg.ADRENALINE.still().get(), resourcesInfo.adrenaline().map(info -> info.perSecond() < 1 ? 1 : (int)info.perSecond()).orElse(0)))) :
                Stream.of();
    }

    @Override
    public @NotNull Type<ResourcesDisplay> type()
    {
        return Registration.SlotDisplayReg.RESOURCES.get();
    }
}
