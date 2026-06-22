/**
 * @author ArcAnc
 * Created at: 21.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;


import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorState;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record S2COrganicArmorSync(UUID entityId, OrganicArmorState state) implements IPacket
{
	public static final Type<S2COrganicArmorSync> TYPE = new Type<>(Database.rl("s2c_organic_armor_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2COrganicArmorSync> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			S2COrganicArmorSync :: entityId,
			OrganicArmorState.STREAM_CODEC,
			S2COrganicArmorSync :: state,
			S2COrganicArmorSync :: new);

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ClientLevel level = RenderHelper.mc().level;
			if (level == null)
				return;
			Entity target = level.getEntities().get(this.entityId);
			if (target == null)
				return;
			target.setData(Registration.DataAttachmentsReg.ORGANIC_ARMOR, this.state);
		});
	}

	@Override
	public Type<S2COrganicArmorSync> type()
	{
		return TYPE;
	}
}
