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
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record S2CWings(UUID playerId, boolean flying) implements IPacket
{
	public static final Type<S2CWings> TYPE = new Type<>(Database.rl("s2c_wings"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CWings> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, S2CWings :: playerId,
			ByteBufCodecs.BOOL, S2CWings :: flying,
			S2CWings :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			if (RenderHelper.mc().level == null)
				return;
			Entity entity = RenderHelper.mc().level.getEntities().get(this.playerId);
			if (entity instanceof Player player)
				WingsEffectType.syncFlying(player, this.flying);
		});
	}

	@Override
	public Type<S2CWings> type()
	{
		return TYPE;
	}
}
