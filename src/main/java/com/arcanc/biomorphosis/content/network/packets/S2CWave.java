/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.ability.wave.client.WaveClientHandler;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BioCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CWave(Vec3 origin, Vec3 direction, double height, double speedPerTick, double range) implements IPacket
{
	public static final Type<S2CWave> TYPE = new Type<>(Database.rl("s2c_wave"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CWave> STREAM_CODEC = StreamCodec.composite(
			BioCodecs.VEC_3_STREAM_CODEC,
			S2CWave :: origin,
			BioCodecs.VEC_3_STREAM_CODEC,
			S2CWave :: direction,
			ByteBufCodecs.DOUBLE,
			S2CWave :: height,
			ByteBufCodecs.DOUBLE,
			S2CWave :: speedPerTick,
			ByteBufCodecs.DOUBLE,
			S2CWave :: range,
			S2CWave :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() ->
				WaveClientHandler.spawn(this.origin, this.direction, this.height, this.speedPerTick, this.range));
	}

	@Override
	public Type<S2CWave> type()
	{
		return TYPE;
	}
}
