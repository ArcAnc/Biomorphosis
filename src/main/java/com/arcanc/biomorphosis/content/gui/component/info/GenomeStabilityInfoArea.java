/**
 * @author ArcAnc
 * Created at: 12.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.info;


import com.arcanc.biomorphosis.content.gui.component.GeneChooser;
import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GenomeStabilityInfoArea extends InfoArea
{
	private final GeneChooser geneChooser;
	public GenomeStabilityInfoArea(Rect2i area, GeneChooser geneChooser)
	{
		super(area);
		this.geneChooser = geneChooser;
	}
	
	@Override
	protected void fillTooltipOverArea(int mouseX, int mouseY, @NotNull List<Component> tooltip)
	{
		tooltip.add(Component.translatable(Database.GUI.InfoArea.GenomeStabilityInfoArea.Tooltip.STABILITY_TOOLTIP));
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		Minecraft mc = RenderHelper.mc();
		Font font = mc.font;
		GenomeInstance genome = this.geneChooser.getGenome();
		ClientPacketListener listener = mc.getConnection();
		int resultedStability = 0;
		
		if (genome.geneInstances().isEmpty() || listener == null)
		{
			guiGraphics.drawString(
					font,
					Component.translatable(Database.GUI.InfoArea.GenomeStabilityInfoArea.STABILITY, 0),
					this.area.getX(),
					this.area.getY() + this.area.getHeight() / 2 - font.lineHeight /2,
					-1,
					false);
			return;
		}
		
		RegistryAccess registries = listener.registryAccess();
		for (GeneInstance gene : genome.geneInstances())
		{
			GeneDefinition geneDefinition = registries.
					lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
					getOrThrow(ResourceKey.create(Registration.GenomeReg.DEFINITION_KEY, gene.id())).
					value();
			if (geneDefinition == null)
				continue;
			GeneDefinition.RarityData data = geneDefinition.rarityData().get(gene.rarity());
			if (data == null)
				continue;
			resultedStability -= data.destabilizationAmount();
		}
		
		MutableComponent stability = Component.literal(String.valueOf(resultedStability));
		if (resultedStability < 0)
		{
			guiGraphics.drawString(font, Component.translatable(Database.GUI.InfoArea.GenomeStabilityInfoArea.LOW_STABILITY).
							withStyle(ChatFormatting.RED),
					this.area.getX(),
					this.area.getY() - 4 - font.lineHeight / 2,
					-1,
					false);
			stability.withStyle(ChatFormatting.RED);
		}
		
		guiGraphics.drawString(
				font,
				Component.translatable(Database.GUI.InfoArea.GenomeStabilityInfoArea.STABILITY, stability),
				this.area.getX(),
				this.area.getY() + this.area.getHeight() / 2 - font.lineHeight /2,
				-1,
				false);
	}
}
