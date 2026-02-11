/**
 * @author ArcAnc
 * Created at: 22.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;


import com.arcanc.biomorphosis.content.gui.sync.IScreenMessageReceiver;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.mutations.UnlockedGenome;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public record C2SAddUnlockedGenes(UUID playerUUID, GenomeInstance genome) implements IPacket
{
	public static final Type<C2SAddUnlockedGenes> TYPE = new Type<>(Database.rl("c2s_add_unlocked_genes"));
	public static final StreamCodec<FriendlyByteBuf, C2SAddUnlockedGenes> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			C2SAddUnlockedGenes :: playerUUID,
			GenomeInstance.STREAM_CODEC,
			C2SAddUnlockedGenes :: genome,
			C2SAddUnlockedGenes :: new);
	
	@Override
	public void process(@NotNull IPayloadContext context)
	{
		Player player = context.player();
		if (!(player instanceof ServerPlayer serverPlayer))
			return;
		context.enqueueWork(() ->
		{
			MinecraftServer server = serverPlayer.getServer();
			if (server == null)
				return;
			ServerPlayer targetPlayer  = server.getPlayerList().getPlayer(this.playerUUID());
			if (targetPlayer == null)
				return;
			
			UnlockedGenome unlockedGenome = GenomeHelper.getUnlockedGenome(targetPlayer);
			
			for (GeneInstance gene : genome().geneInstances())
				if (unlockedGenome.hasGene(gene))
					unlockedGenome.unlockedGenes().get(gene.id()).add(gene.rarity());
				else
					unlockedGenome.unlockedGenes().putIfAbsent(gene.id(), EnumSet.of(gene.rarity()));
			targetPlayer.setData(Registration.DataAttachmentsReg.UNLOCKED_GENOME, unlockedGenome);
			NetworkEngine.sendToPlayer(targetPlayer, new S2CUnlockedGenomeSync(unlockedGenome, List.of()));
		});
	}
	
	@Override
	public @NotNull Type<C2SAddUnlockedGenes> type()
	{
		return TYPE;
	}
}
