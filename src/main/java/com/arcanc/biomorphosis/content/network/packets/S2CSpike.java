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

public record S2CSpike(Vec3 origin, Vec3 targetOffset, double lateralCurve, double aimLift, double speedPerTick,
		double range) implements IPacket
{
	public static final Type<S2CSpike> TYPE = new Type<>(Database.rl("s2c_spike"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CSpike> STREAM_CODEC = StreamCodec.composite(
			BioCodecs.VEC_3_STREAM_CODEC, S2CSpike :: origin,
			BioCodecs.VEC_3_STREAM_CODEC, S2CSpike :: targetOffset,
			ByteBufCodecs.DOUBLE, S2CSpike :: lateralCurve,
			ByteBufCodecs.DOUBLE, S2CSpike :: aimLift,
			ByteBufCodecs.DOUBLE, S2CSpike :: speedPerTick,
			ByteBufCodecs.DOUBLE, S2CSpike :: range,
			S2CSpike :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() -> SpikeBarrageClientHandler.spawn(this.origin, this.targetOffset,
				this.lateralCurve, this.aimLift, this.speedPerTick, this.range));
	}

	@Override
	public Type<S2CSpike> type()
	{
		return TYPE;
	}
}
