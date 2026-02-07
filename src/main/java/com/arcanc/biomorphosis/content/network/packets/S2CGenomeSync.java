/**
 * @author ArcAnc
 * Created at: 08.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;


import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record S2CGenomeSync (UUID entityId, GenomeInstance genome) implements IPacket
{
	public static final Type<S2CGenomeSync> TYPE = new Type<>(Database.rl("s2c_genome_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CGenomeSync> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			S2CGenomeSync :: entityId,
			GenomeInstance.STREAM_CODEC,
			S2CGenomeSync :: genome,
			S2CGenomeSync :: new);
	
	@Override
	public void process(@NotNull IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ClientLevel level = RenderHelper.mc().level;
			if (level == null)
				return;
			Entity target = level.getEntities().get(this.entityId);
			if (target == null)
				return;
			target.setData(Registration.DataAttachmentsReg.GENOME, this.genome);
		});
	}
	
	@Override
	public @NotNull Type<S2CGenomeSync> type()
	{
		return TYPE;
	}
}
