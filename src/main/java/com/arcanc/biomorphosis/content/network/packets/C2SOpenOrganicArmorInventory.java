/**
 * @author ArcAnc
 * Created at: 27.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SOpenOrganicArmorInventory() implements IPacket
{
	public static final Type<C2SOpenOrganicArmorInventory> TYPE = new Type<>(Database.rl("c2s_open_organic_armor_inventory"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SOpenOrganicArmorInventory> STREAM_CODEC = StreamCodec.unit(new C2SOpenOrganicArmorInventory());

	@Override
	public void process(IPayloadContext context)
	{
		if (!(context.player() instanceof ServerPlayer player))
			return;

		context.enqueueWork(() ->
		{
			player.resetLastActionTime();
			player.openMenu(Registration.MenuTypeReg.ORGANIC_ARMOR_INVENTORY.provide(player), byteBuf -> byteBuf.writeBlockPos(BlockPos.ZERO));
		});
	}

	@Override
	public Type<C2SOpenOrganicArmorInventory> type()
	{
		return TYPE;
	}
}
