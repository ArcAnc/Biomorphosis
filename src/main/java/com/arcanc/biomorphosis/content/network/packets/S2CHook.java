/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.ability.hook.client.HookClientHandler;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BioCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record S2CHook(UUID ownerId, Vec3 origin, Vec3 direction, double speedPerTick, double range) implements IPacket
{
	public static final Type<S2CHook> TYPE = new Type<>(Database.rl("s2c_hook"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CHook> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			S2CHook :: ownerId,
			BioCodecs.VEC_3_STREAM_CODEC,
			S2CHook :: origin,
			BioCodecs.VEC_3_STREAM_CODEC,
			S2CHook :: direction,
			ByteBufCodecs.DOUBLE,
			S2CHook :: speedPerTick,
			ByteBufCodecs.DOUBLE,
			S2CHook :: range,
			S2CHook :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() -> HookClientHandler.spawn(this.ownerId, this.origin, this.direction,
				this.speedPerTick, this.range));
	}

	@Override
	public Type<S2CHook> type()
	{
		return TYPE;
	}
}
