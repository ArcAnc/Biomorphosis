/**
 * @author ArcAnc
 * Created at: 30.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe;

import com.arcanc.biomorphosis.data.recipe.input.BioBaseInput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class BioBaseRecipe<T extends BioBaseInput> implements Recipe<T>
{
    private final ResourcesInfo resources;

    public BioBaseRecipe(@NotNull ResourcesInfo resources)
    {
        this.resources = resources;
    }

    public ResourcesInfo getResources()
    {
        return resources;
    }

    @Override
    public boolean matches(@NotNull T input, @NotNull Level level)
    {
        if (this.resources.biomass().perSecond() <= 0)
            return false;

        int time = this.resources.time();

        if (this.resources.biomass().perSecond() > input.biomass().getAmount())
            return false;

        boolean adrenalineCheck = this.resources.adrenaline().map(adrenaline ->
        {
            if (!adrenaline.required())
                return true;
            if (adrenaline.perSecond() == 0)
                return false;
            if (adrenaline.perSecond() > 0 && time > 0)
                return adrenaline.perSecond() <= input.adrenaline().getAmount();
            return true;
        }).orElse(true);

        boolean lymphCheck = this.resources.lymph().map(lymph ->
        {
            if (!lymph.required())
                return true;
            if (lymph.perSecond() == 0)
                return false;
            if (lymph.perSecond() > 0 && time > 0)
                return lymph.perSecond() <= input.lymph().getAmount();
            return true;
        }).orElse(true);

        return adrenalineCheck && lymphCheck;
    }

    @Override
    public boolean isSpecial() 
    {
        return true;
    }

    public record BiomassInfo(int perSecond)
    {
        public static final Codec<BiomassInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("per_second").forGetter(BiomassInfo :: perSecond)).
                apply(instance, BiomassInfo :: new));

        public static final StreamCodec<ByteBuf, BiomassInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                BiomassInfo :: perSecond,
                BiomassInfo :: new);
    }

    public record AdditionalResourceInfo(boolean required, int perSecond, float modifier)
    {
        public static final Codec<AdditionalResourceInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.BOOL.fieldOf("required").forGetter(AdditionalResourceInfo :: required),
                        Codec.INT.fieldOf("per_second").forGetter(AdditionalResourceInfo ::perSecond),
                        Codec.floatRange(0f, 1000f).fieldOf("modifier").forGetter(AdditionalResourceInfo :: modifier)).
                apply(instance, AdditionalResourceInfo :: new));

        public static final StreamCodec<ByteBuf, AdditionalResourceInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                AdditionalResourceInfo :: required,
                ByteBufCodecs.INT,
                AdditionalResourceInfo ::perSecond,
                ByteBufCodecs.FLOAT,
                AdditionalResourceInfo :: modifier,
                AdditionalResourceInfo :: new);
    }

    public record ResourcesInfo(BiomassInfo biomass, Optional<AdditionalResourceInfo> lymph, Optional<AdditionalResourceInfo> adrenaline, int time)
    {
        public static final Codec<ResourcesInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BiomassInfo.CODEC.fieldOf("biomass").forGetter(ResourcesInfo :: biomass),
                        AdditionalResourceInfo.CODEC.optionalFieldOf("lymph").forGetter(ResourcesInfo :: lymph),
                        AdditionalResourceInfo.CODEC.optionalFieldOf("adrenaline").forGetter(ResourcesInfo :: adrenaline),
                        Codec.intRange(0, Integer.MAX_VALUE).fieldOf("time").forGetter(ResourcesInfo :: time)).
                apply(instance, ResourcesInfo :: new));

        public static final StreamCodec<ByteBuf, ResourcesInfo> STREAM_CODEC = StreamCodec.composite(
                BiomassInfo.STREAM_CODEC,
                ResourcesInfo :: biomass,
                ByteBufCodecs.optional(AdditionalResourceInfo.STREAM_CODEC),
                ResourcesInfo :: lymph,
                ByteBufCodecs.optional(AdditionalResourceInfo.STREAM_CODEC),
                ResourcesInfo :: adrenaline,
                ByteBufCodecs.INT,
                ResourcesInfo :: time,
                ResourcesInfo :: new);
    }
}
