/**
 * @author ArcAnc
 * Created at: 29.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.ability.client.AbilityCastClientHandler;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record S2CAbilityCast(UUID playerId, ResourceLocation abilityId, ResourceLocation animationId, long startedAt, int castTimeTicks, boolean active) implements IPacket
{
	public static final Type<S2CAbilityCast> TYPE = new Type<>(Database.rl("s2c_ability_cast"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CAbilityCast> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			S2CAbilityCast :: playerId,
			ResourceLocation.STREAM_CODEC,
			S2CAbilityCast :: abilityId,
			ResourceLocation.STREAM_CODEC,
			S2CAbilityCast :: animationId,
			ByteBufCodecs.VAR_LONG,
			S2CAbilityCast :: startedAt,
			ByteBufCodecs.VAR_INT,
			S2CAbilityCast :: castTimeTicks,
			ByteBufCodecs.BOOL,
			S2CAbilityCast :: active,
			S2CAbilityCast :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			if (this.active)
				AbilityCastClientHandler.start(this.playerId, this.animationId, this.startedAt, this.castTimeTicks);
			else
				AbilityCastClientHandler.stop(this.playerId);
		});
	}

	@Override
	public Type<S2CAbilityCast> type()
	{
		return TYPE;
	}
}
