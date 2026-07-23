/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.mutations.types.WingsEffectType;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SWingsFlightInput(float strafe, float forward, boolean jumping, boolean descending) implements IPacket
{
	public static final Type<C2SWingsFlightInput> TYPE = new Type<>(Database.rl("c2s_wings_flight_input"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SWingsFlightInput> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT, C2SWingsFlightInput :: strafe,
			ByteBufCodecs.FLOAT, C2SWingsFlightInput :: forward,
			ByteBufCodecs.BOOL, C2SWingsFlightInput :: jumping,
			ByteBufCodecs.BOOL, C2SWingsFlightInput :: descending,
			C2SWingsFlightInput :: new);

	@Override
	public void process(IPayloadContext context)
	{
		if (!(context.player() instanceof ServerPlayer player))
			return;
		context.enqueueWork(() -> WingsEffectType.setFlightInput(player, this.strafe, this.forward, this.jumping, this.descending));
	}

	@Override
	public Type<C2SWingsFlightInput> type()
	{
		return TYPE;
	}
}
