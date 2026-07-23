/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WingsFlightData(boolean flying, boolean hasPreviousNoGravity, boolean previousNoGravity)
{
	public static final WingsFlightData EMPTY = empty();

	public static final Codec<WingsFlightData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("flying").forGetter(WingsFlightData :: flying),
			Codec.BOOL.fieldOf("has_previous_no_gravity").forGetter(WingsFlightData :: hasPreviousNoGravity),
			Codec.BOOL.fieldOf("previous_no_gravity").forGetter(WingsFlightData :: previousNoGravity)
	).apply(instance, WingsFlightData :: new));

	public static final StreamCodec<RegistryFriendlyByteBuf, WingsFlightData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, WingsFlightData :: flying,
			ByteBufCodecs.BOOL, WingsFlightData :: hasPreviousNoGravity,
			ByteBufCodecs.BOOL, WingsFlightData :: previousNoGravity,
			WingsFlightData :: new);

	public static WingsFlightData empty()
	{
		return new WingsFlightData(false, false, false);
	}

	public WingsFlightData withFlying(boolean flying)
	{
		return new WingsFlightData(flying, this.hasPreviousNoGravity, this.previousNoGravity);
	}

	public WingsFlightData withPreviousNoGravity(boolean previousNoGravity)
	{
		return new WingsFlightData(this.flying, true, previousNoGravity);
	}

	public WingsFlightData withoutPreviousNoGravity()
	{
		return new WingsFlightData(this.flying, false, false);
	}
}
