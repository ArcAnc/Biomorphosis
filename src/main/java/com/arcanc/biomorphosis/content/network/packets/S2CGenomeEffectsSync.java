/**
 * @author ArcAnc
 * Created at: 19.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.mutations.GenomeEffectsHolder;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record S2CGenomeEffectsSync(UUID entityId, CompoundTag effectData) implements IPacket
{
	public static final Type<S2CGenomeEffectsSync> TYPE = new Type<>(Database.rl("s2c_genome_effects_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CGenomeEffectsSync> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			S2CGenomeEffectsSync :: entityId,
			ByteBufCodecs.COMPOUND_TAG,
			S2CGenomeEffectsSync :: effectData,
			S2CGenomeEffectsSync :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ClientLevel level = RenderHelper.mc().level;
			if (level == null)
				return;
			Entity target = level.getEntities().get(this.entityId);
			if (target instanceof GenomeEffectsHolder holder)
			{
				holder.biomorphosis$setGeneEffectData(this.effectData);
				holder.biomorphosis$rebuildEffects();
			}
		});
	}

	@Override
	public Type<S2CGenomeEffectsSync> type()
	{
		return TYPE;
	}
}
