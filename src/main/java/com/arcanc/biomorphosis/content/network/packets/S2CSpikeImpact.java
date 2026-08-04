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
import com.arcanc.biomorphosis.util.helper.BioCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CSpikeImpact(Vec3 origin, double lateralCurve, Vec3 impactPosition) implements IPacket
{
	public static final Type<S2CSpikeImpact> TYPE = new Type<>(Database.rl("s2c_spike_impact"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CSpikeImpact> STREAM_CODEC = StreamCodec.composite(
			BioCodecs.VEC_3_STREAM_CODEC, S2CSpikeImpact :: origin,
			ByteBufCodecs.DOUBLE, S2CSpikeImpact :: lateralCurve,
			BioCodecs.VEC_3_STREAM_CODEC, S2CSpikeImpact :: impactPosition,
			S2CSpikeImpact :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() -> SpikeBarrageClientHandler.impact(this.origin, this.lateralCurve, this.impactPosition));
	}

	@Override
	public Type<S2CSpikeImpact> type()
	{
		return TYPE;
	}
}
