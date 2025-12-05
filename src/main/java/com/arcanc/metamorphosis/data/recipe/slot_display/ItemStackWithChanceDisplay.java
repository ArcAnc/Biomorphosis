/**
 * @author ArcAnc
 * Created at: 02.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.recipe.slot_display;

import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.util.helper.MathHelper;
import com.arcanc.metamorphosis.util.inventory.item.StackWithChance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record ItemStackWithChanceDisplay(ItemStack stack, float chance) implements SlotDisplay
{
    public static final MapCodec<ItemStackWithChanceDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(ItemStackWithChanceDisplay:: stack),
                ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("chance").forGetter(ItemStackWithChanceDisplay:: chance)).
            apply(instance, ItemStackWithChanceDisplay:: new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackWithChanceDisplay> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC,
            ItemStackWithChanceDisplay:: stack,
            ByteBufCodecs.FLOAT,
            ItemStackWithChanceDisplay:: chance,
            ItemStackWithChanceDisplay:: new);

    public ItemStackWithChanceDisplay(@NotNull StackWithChance stackWithChance)
    {
        this(stackWithChance.stack(), stackWithChance.chance());
    }

    @Override
    public <T> @NotNull Stream<T> resolve(@NotNull ContextMap context, @NotNull DisplayContentsFactory<T> output)
    {
        return output instanceof DisplayContentsFactory.ForStacks<T> forStacks ? Stream.of(forStacks.forStack(this.stack)) : Stream.empty();
    }

    @Override
    public @NotNull Type<ItemStackWithChanceDisplay> type()
    {
        return Registration.SlotDisplayReg.ITEM_STACK_WITH_CHANCE.get();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;
        else
        {
            return other instanceof ItemStackWithChanceDisplay stackWithChance &&
                    ItemStack.matches(this.stack, stackWithChance.stack) &&
                    MathHelper.isApproximatelyEqual(this.chance(), stackWithChance.chance());
        }
    }

    @Override
    public boolean isEnabled(@NotNull FeatureFlagSet enabledFeatures)
    {
        return this.stack.getItem().isEnabled(enabledFeatures);
    }
}
