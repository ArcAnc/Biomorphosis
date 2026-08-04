/**
 * @author ArcAnc
 * Created at: 29.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record AbilityCastingState(Optional<ResourceLocation> abilityId, int slot, long startedAt, long completesAt)
{
	public static final AbilityCastingState EMPTY = empty();

	public static final Codec<AbilityCastingState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.optionalFieldOf("ability").forGetter(AbilityCastingState :: abilityId),
			Codec.INT.fieldOf("slot").forGetter(AbilityCastingState :: slot),
			Codec.LONG.fieldOf("started_at").forGetter(AbilityCastingState :: startedAt),
			Codec.LONG.fieldOf("completes_at").forGetter(AbilityCastingState :: completesAt)
	).apply(instance, AbilityCastingState :: new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AbilityCastingState> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
			AbilityCastingState :: abilityId,
			ByteBufCodecs.VAR_INT,
			AbilityCastingState :: slot,
			ByteBufCodecs.VAR_LONG,
			AbilityCastingState :: startedAt,
			ByteBufCodecs.VAR_LONG,
			AbilityCastingState :: completesAt,
			AbilityCastingState :: new);

	public static AbilityCastingState empty()
	{
		return new AbilityCastingState(Optional.empty(), -1, 0L, 0L);
	}

	public static AbilityCastingState start(ResourceLocation abilityId, int slot, long startedAt, int castTimeTicks)
	{
		return new AbilityCastingState(Optional.of(abilityId), slot, startedAt, startedAt + castTimeTicks);
	}

	public boolean isCasting()
	{
		return this.abilityId.isPresent();
	}
}
