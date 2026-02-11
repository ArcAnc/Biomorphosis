/**
 * @author ArcAnc
 * Created at: 10.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import com.arcanc.biomorphosis.content.mutations.GeneRarity;
import com.arcanc.biomorphosis.content.mutations.UnlockedGenome;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class OwnedGeneList extends AbstractSelectionList<OwnedGeneList.OwnedGeneEntry>
{
	private final OwnedRarityList rarityList;
	
	public OwnedGeneList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight, OwnedRarityList rarityList)
	{
		super(minecraft, width, height, y, itemHeight);
		this.setX(x);
		this.rarityList = rarityList;
		
		LocalPlayer player = minecraft.player;
		if (player == null)
			return;
		
		UnlockedGenome unlockedGenome = GenomeHelper.getUnlockedGenome(player);
		
		if (unlockedGenome.unlockedGenes().isEmpty())
			return;
		
		for (ResourceLocation geneId : unlockedGenome.getGeneNames())
			addEntry(new OwnedGeneEntry(geneId));
		
		if (this.children().isEmpty())
			return;
		this.setSelected(this.getFirstElement());
		updateBoundedRarities(this.getSelected().getValue());
	}
	
	public void updateBoundedRarities(ResourceLocation id)
	{
		LocalPlayer player = this.minecraft.player;
		if (player == null)
		{
			this.rarityList.updateValues(Set.of());
			return;
		}
		
		UnlockedGenome genome = GenomeHelper.getUnlockedGenome(player);
		Set<GeneRarity> rarities = genome.getRaritiesById(id);
		this.rarityList.updateValues(rarities);
		
		if (rarities.isEmpty())
			return;
		this.rarityList.setSelected(this.rarityList.getFirstElement());
	}
	
	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
	{
	
	}
	
	@Override
	public int getRowRight()
	{
		return super.getRowRight();
	}
	
	@Override
	public int getRowLeft()
	{
		return this.getX();
	}
	
	@Override
	protected int getScrollbarPosition()
	{
		return this.getRowLeft() + this.getRowWidth();
	}
	
	@Override
	public int getRowWidth()
	{
		return this.getWidth() - (int)(this.getWidth() * 0.13f);
	}
	
	@Override
	protected @Nullable OwnedGeneEntry getEntryAtPosition(double mouseX, double mouseY)
	{
		int left = this.getRowLeft();
		int right = this.getRowRight();
		int i1 = Mth.floor(mouseY - (double)this.getY()) - this.headerHeight + (int)this.getScrollAmount() - 4;
		int j1 = i1 / this.itemHeight;
		return mouseX >= (double)left && mouseX <= (double)right && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null;
	}
	
	@Override
	protected void renderSelection(@NotNull GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor)
	{
		int left = getRowLeft();
		int right = getRowRight();
		guiGraphics.fill(left + 1, top - 2, right - 1, top + height + 2, outerColor);
		guiGraphics.fill(left + 2, top - 1, right - 2, top + height + 1, innerColor);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double p_388604_, double p_386550_)
	{
		if (this.isMouseOver(mouseX, mouseY))
			return super.mouseScrolled(mouseX, mouseY, p_388604_, p_386550_);
		return false;
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
			
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(left + 5, top + height / 2 - font.lineHeight / 2, 0);
			guiGraphics.pose().scale(0.7f, 0.7f, 1);

			guiGraphics.drawWordWrap(font,
					Component.translatable(Database.GUI.Genome.Translations.GENE_NAME.apply(getValue())),
					0,
					0,
					(int) (width + width * 0.3f),
					-1);
			guiGraphics.pose().popPose();
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			if (isMouseOver(mouseX, mouseY))
			{
				OwnedGeneList.this.updateBoundedRarities(this.getValue());
				return true;
			}
			return false;
		}
	}
}
