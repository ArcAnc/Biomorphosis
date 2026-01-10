/**
 * @author ArcAnc
 * Created at: 10.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OwnedGeneList extends AbstractSelectionList<OwnedGeneList.OwnedGeneEntry>
{
	private final OwnedRarityList rarityList;
	
	public OwnedGeneList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight, OwnedRarityList rarityList)
	{
		super(minecraft, width, height, y, itemHeight);
		this.setX(x);
		this.rarityList = rarityList;
		
		ClientPacketListener listener = minecraft.getConnection();
		if (listener == null)
			return;
		Registry<GeneDefinition> definitions = listener.registryAccess().lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY);
		
		definitions.forEach(geneDefinition ->
						this.addEntry(new OwnedGeneEntry(geneDefinition.id())));
		
		if (this.children().isEmpty())
			return;
		this.setSelectedIndex(0);
		updateBoundedRarities(this.getSelected().getValue());
	}
	
	public void updateBoundedRarities(ResourceLocation id)
	{
		ClientPacketListener listener = minecraft.getConnection();
		if (listener == null)
			return;
		Registry<GeneDefinition> definitions = listener.registryAccess().lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY);
		
		GeneDefinition definition = definitions.
				getValue(id);
		
		if (definition == null)
		{
			this.rarityList.replaceEntries(List.of());
			return;
		}
		
		this.rarityList.updateValues(definition.
				rarityData().
				keySet());
		this.rarityList.setSelectedIndex(0);
	}
	
	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
	{
	
	}
	
	@Override
	public int getRowWidth()
	{
		return (int)(this.getWidth() - this.getWidth() * 0.05f);
	}
	
	public class OwnedGeneEntry extends AbstractSelectionList.Entry<OwnedGeneEntry>
	{
		private final ResourceLocation value;
		
		private OwnedGeneEntry (ResourceLocation value)
		{
			this.value = value;
		}
		
		public ResourceLocation getValue()
		{
			return this.value;
		}
		
		@Override
		public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick)
		{
			Minecraft mc = RenderHelper.mc();
			Font font = mc.font;
			
			guiGraphics.drawWordWrap(font,
					Component.translatable(Database.GUI.Genome.Translations.GENE_NAME.apply(getValue())),
					left + 5,
					top + height /2 - font.lineHeight / 2,
					width,
					-1,
					false);
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			OwnedGeneList.this.updateBoundedRarities(this.getValue());
			return true;
		}
	}
}
