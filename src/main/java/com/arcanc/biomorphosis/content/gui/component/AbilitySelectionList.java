/**
 * @author ArcAnc
 * Created at: 29.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;

import com.arcanc.biomorphosis.content.ability.IAbility;
import com.arcanc.biomorphosis.util.helper.AbilityHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class AbilitySelectionList extends AbstractSelectionList<AbilitySelectionList.AbilityEntry>
{
	private final Consumer<ResourceLocation> selectionHandler;

	public AbilitySelectionList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight,
	                            List<ResourceLocation> abilities, Consumer<ResourceLocation> selectionHandler)
	{
		super(minecraft, width, height, y, itemHeight);
		this.setX(x);
		this.selectionHandler = selectionHandler;
		replaceEntries(abilities.stream().map(AbilityEntry :: new).toList());
	}

	@Override
	public int getRowLeft()
	{
		return getX();
	}

	@Override
	public int getRowWidth()
	{
		return getWidth() - 6;
	}

	@Override
	protected int getScrollbarPosition()
	{
		return getRowLeft() + getRowWidth();
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narration)
	{
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
	{
		return isMouseOver(mouseX, mouseY) && super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	public final class AbilityEntry extends AbstractSelectionList.Entry<AbilityEntry>
	{
		private final ResourceLocation abilityId;

		private AbilityEntry(ResourceLocation abilityId)
		{
			this.abilityId = abilityId;
		}

		@Override
		public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
		                   int mouseX, int mouseY, boolean hovering, float partialTick)
		{
			Font font = minecraft.font;
			AbilityHelper.getAbility(this.abilityId).flatMap(IAbility :: getIcon).ifPresent(icon ->
					RenderHelper.blit(graphics, icon, left + 1, top + 1, 0, 0, 16, 16, 0, 16, 16, 16, 16));
			graphics.drawString(font, Component.literal(formatAbilityName(this.abilityId)), left + 20,
					top + (height - font.lineHeight) / 2, 0xFFFFFFFF, false);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			if (!AbilitySelectionList.this.isMouseOver(mouseX, mouseY))
				return false;

			AbilitySelectionList.this.setSelected(this);
			selectionHandler.accept(this.abilityId);
			return true;
		}
	}

	public static String formatAbilityName(ResourceLocation abilityId)
	{
		String path = abilityId.getPath().replace('_', ' ');
		return Character.toUpperCase(path.charAt(0)) + path.substring(1);
	}
}
