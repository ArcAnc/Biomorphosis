/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.mutations.types.WingsEffectType;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SWings(boolean flying) implements IPacket
{
	public static final Type<C2SWings> TYPE = new Type<>(Database.rl("c2s_wings"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SWings> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, C2SWings :: flying, C2SWings :: new);

	@Override
	public void process(IPayloadContext context)
	{
		if (!(context.player() instanceof ServerPlayer player))
			return;
		context.enqueueWork(() ->
		{
			if (WingsEffectType.setFlying(player, this.flying))
				NetworkEngine.sendToAllClients(new S2CWings(player.getUUID(), this.flying));
		});
	}

	@Override
	public Type<C2SWings> type()
	{
		return TYPE;
	}
}
