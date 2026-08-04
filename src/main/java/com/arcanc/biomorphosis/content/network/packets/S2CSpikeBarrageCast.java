/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.ability.spike.client.SpikeBarrageClientHandler;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record S2CSpikeBarrageCast(UUID ownerId, int durationTicks) implements IPacket
{
	public static final Type<S2CSpikeBarrageCast> TYPE = new Type<>(Database.rl("s2c_spike_barrage_cast"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CSpikeBarrageCast> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, S2CSpikeBarrageCast :: ownerId,
			ByteBufCodecs.VAR_INT, S2CSpikeBarrageCast :: durationTicks,
			S2CSpikeBarrageCast :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() -> SpikeBarrageClientHandler.keepHandsForward(this.ownerId, this.durationTicks));
	}

	@Override
	public Type<S2CSpikeBarrageCast> type()
	{
		return TYPE;
	}
}
