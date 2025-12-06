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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BooleanSupplier;

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
        int time = this.resources.time();

        boolean biomassCheck = ((BooleanSupplier)() ->
        {
            if (!this.resources.biomass.required())
                return true;
            if (this.resources.biomass().perSecond() > 0 && time > 0)
                return this.resources.biomass().perSecond() <= input.biomass().getAmount();
            return true;
        }).getAsBoolean();

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

        boolean acidCheck = this.resources.acid().map(acid ->
        {
            if (!acid.required())
                return true;
            if (acid.perSecond() == 0)
                return false;
            if (acid.perSecond() > 0 && time > 0)
                return acid.perSecond() <= input.acid().getAmount();
            return true;
        }).orElse(true);

        return biomassCheck && adrenalineCheck && acidCheck;
    }

    @Override
    public boolean isSpecial() 
    {
        return true;
    }

    @Override
    public @NotNull String group()
    {
        ResourceLocation id = BuiltInRegistries.RECIPE_BOOK_CATEGORY.getKey(recipeBookCategory());
        return id != null ? id.getPath() : "";
    }

    public record BiomassInfo(boolean required, float perSecond)
    {
        public static final Codec<BiomassInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.BOOL.fieldOf("required").forGetter(BiomassInfo :: required),
                        Codec.floatRange(0, Float.MAX_VALUE).fieldOf("per_second").forGetter(BiomassInfo :: perSecond)).
                apply(instance, BiomassInfo :: new));

        public static final StreamCodec<ByteBuf, BiomassInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                BiomassInfo::required,
                ByteBufCodecs.FLOAT,
                BiomassInfo :: perSecond,
                BiomassInfo :: new);
    }

    public record AdditionalResourceInfo(boolean required, float perSecond, float modifier)
    {
        public static final Codec<AdditionalResourceInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.BOOL.fieldOf("required").forGetter(AdditionalResourceInfo :: required),
                        Codec.floatRange(0, Float.MAX_VALUE).fieldOf("per_second").forGetter(AdditionalResourceInfo ::perSecond),
                        Codec.floatRange(0f, 1000f).fieldOf("modifier").forGetter(AdditionalResourceInfo :: modifier)).
                apply(instance, AdditionalResourceInfo :: new));

        public static final StreamCodec<ByteBuf, AdditionalResourceInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                AdditionalResourceInfo :: required,
                ByteBufCodecs.FLOAT,
                AdditionalResourceInfo ::perSecond,
                ByteBufCodecs.FLOAT,
                AdditionalResourceInfo :: modifier,
                AdditionalResourceInfo :: new);
    }

    public record ResourcesInfo(BiomassInfo biomass, Optional<AdditionalResourceInfo> acid, Optional<AdditionalResourceInfo> adrenaline, int time)
    {
        public static final Codec<ResourcesInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BiomassInfo.CODEC.fieldOf("biomass").forGetter(ResourcesInfo :: biomass),
                        AdditionalResourceInfo.CODEC.optionalFieldOf("acid").forGetter(ResourcesInfo :: acid),
                        AdditionalResourceInfo.CODEC.optionalFieldOf("adrenaline").forGetter(ResourcesInfo :: adrenaline),
                        Codec.intRange(0, Integer.MAX_VALUE).fieldOf("time").forGetter(ResourcesInfo :: time)).
                apply(instance, ResourcesInfo :: new));

        public static final StreamCodec<ByteBuf, ResourcesInfo> STREAM_CODEC = StreamCodec.composite(
                BiomassInfo.STREAM_CODEC,
                ResourcesInfo :: biomass,
                ByteBufCodecs.optional(AdditionalResourceInfo.STREAM_CODEC),
                ResourcesInfo :: acid,
                ByteBufCodecs.optional(AdditionalResourceInfo.STREAM_CODEC),
                ResourcesInfo :: adrenaline,
                ByteBufCodecs.INT,
                ResourcesInfo :: time,
                ResourcesInfo :: new);
    }
}
