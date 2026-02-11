/**
 * @author ArcAnc
 * Created at: 15.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;


import com.arcanc.biomorphosis.content.event.OverlayRenderHandler;
import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GeneRarity;
import com.arcanc.biomorphosis.content.mutations.UnlockedGenome;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record S2CUnlockedGenomeSync(UnlockedGenome unlockedGenome, List<GeneInstance> newGenes) implements IPacket
{
	public static final Type<S2CUnlockedGenomeSync> TYPE = new Type<>(Database.rl("s2c_unlocked_genome_sync"));
	public static final StreamCodec<FriendlyByteBuf, S2CUnlockedGenomeSync> STREAM_CODEC = StreamCodec.composite(
			UnlockedGenome.STREAM_CODEC,
			S2CUnlockedGenomeSync :: unlockedGenome,
			ByteBufCodecs.<FriendlyByteBuf, GeneInstance>list().
					apply(GeneInstance.STREAM_CODEC),
			S2CUnlockedGenomeSync::newGenes,
			S2CUnlockedGenomeSync :: new);
	
	@Override
	public void process(@NotNull IPayloadContext context)
	{
		context.enqueueWork(() ->
		{
			ClientLevel level = RenderHelper.mc().level;
			if (level == null)
				return;
			
			Player player = context.player();
			if (!(player instanceof LocalPlayer localPlayer))
				return;
			localPlayer.setData(Registration.DataAttachmentsReg.UNLOCKED_GENOME, unlockedGenome());
			
			
			if (!this.newGenes.isEmpty())
				this.newGenes.forEach(geneInstance ->
					{
						GeneDefinition definition = level.registryAccess().
								lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
								getOrThrow(ResourceKey.create(Registration.GenomeReg.DEFINITION_KEY, geneInstance.id())).
								value();
						if(definition == null)
							return;
						
						GeneDefinition.RarityData data = definition.rarityData().get(geneInstance.rarity());
						if (data == null)
							return;
						
						ResourceLocation image = definition.image();
						
						OverlayRenderHandler.AdvancementOverlays.addAdvancement(new OverlayRenderHandler.AdvancementOverlays.AdvancementRenderable()
						{
							@Override
							public ResourceLocation getImageLocation()
							{
								return image;
							}
							
							@Override
							public Component getName()
							{
								return Component.translatable(Database.GUI.Genome.Translations.GENE_NAME.apply(geneInstance.id())).
										withColor(geneInstance.rarity().getColor());
							}
							
							@Override
							public Component getAdditionalInfo()
							{
								return Component.translatable(Database.GUI.Overlays.Advancements.RECEIVE_GENE).append(getName());
							}
						});
					});
		});
	}
	
	@Override
	public @NotNull Type<S2CUnlockedGenomeSync> type()
	{
		return TYPE;
	}
}
