/**
 * @author ArcAnc
 * Created at: 02.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.inventory.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public record StackWithChance(ItemStack stack, float chance)
{
    public static final Codec<StackWithChance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(StackWithChance :: stack),
                    ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("chance").forGetter(StackWithChance :: chance)).
            apply(instance, StackWithChance :: new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StackWithChance> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC,
            StackWithChance :: stack,
            ByteBufCodecs.FLOAT,
            StackWithChance :: chance,
            StackWithChance :: new);
}
