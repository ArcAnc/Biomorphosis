/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.util.helper.AbilityHelper;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SActivateAbility(int slot) implements IPacket
{
	public static final Type<C2SActivateAbility> TYPE = new Type<>(Database.rl("c2s_activate_ability"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SActivateAbility> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			C2SActivateAbility :: slot,
			C2SActivateAbility :: new);

	@Override
	public void process(IPayloadContext context)
	{
		if (!(context.player() instanceof ServerPlayer player))
			return;

		context.enqueueWork(() -> AbilityHelper.tryActivate(player, this.slot));
	}

	@Override
	public Type<C2SActivateAbility> type()
	{
		return TYPE;
	}
}
