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
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class OwnedRarityList extends AbstractSelectionList<OwnedRarityList.OwnedRarityEntry>
{
	public OwnedRarityList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight)
	{
		super(minecraft, width, height, y, itemHeight);
		this.setX(x);
	}
	
	@Override
	public int getRowWidth()
	{
		return (int)(this.getWidth() - this.getWidth() * 0.05f);
	}

	public void updateValues(@NotNull Set<GeneRarity> availableRarities)
	{
		replaceEntries(availableRarities.stream().map(OwnedRarityEntry :: new).toList());
	}
	
	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
	{
	
	}
	
	public static class OwnedRarityEntry extends AbstractSelectionList.Entry<OwnedRarityEntry>
	{
		private final GeneRarity value;
		
		private OwnedRarityEntry(GeneRarity rarity)
		{
			this.value = rarity;
		}
		
		@Override
		public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick)
		{
			Minecraft mc = RenderHelper.mc();
			Font font = mc.font;
			
			guiGraphics.drawString(font,
					Component.literal(getValue().getSerializedName()).
							withColor(getValue().getColor()),
					left + 3,
					top + height / 2 - font.lineHeight / 2,
					-1,
					false);
		}
		
		public GeneRarity getValue()
		{
			return this.value;
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			return true;
		}
	}
}
