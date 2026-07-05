/**
 * @author ArcAnc
 * Created at: 27.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import com.arcanc.biomorphosis.util.helper.MathHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OrganicArmorInventoryButton extends Button
{
	private static final int BACKGROUND = MathHelper.ColorHelper.color(230, 20, 26, 23);
	private static final int BACKGROUND_HOVERED = MathHelper.ColorHelper.color(245, 36, 48, 39);
	private static final int BORDER = MathHelper.ColorHelper.color(255, 83, 126, 91);
	private static final int ICON = MathHelper.ColorHelper.color(255, 91, 199, 112);
	private static final int ICON_DARK = MathHelper.ColorHelper.color(255, 35, 104, 54);

	public OrganicArmorInventoryButton(int x, int y, OnPress onPress, Component tooltip)
	{
		super(x, y, 18, 18, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
		this.setTooltip(Tooltip.create(tooltip));
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		int x = this.getX();
		int y = this.getY();
		guiGraphics.fill(x, y, x + this.width, y + this.height, this.isHoveredOrFocused() ? BACKGROUND_HOVERED : BACKGROUND);
		guiGraphics.renderOutline(x, y, this.width, this.height, BORDER);

		guiGraphics.fill(x + 6, y + 4, x + 12, y + 6, ICON);
		guiGraphics.fill(x + 4, y + 6, x + 14, y + 12, ICON);
		guiGraphics.fill(x + 5, y + 12, x + 8, y + 15, ICON_DARK);
		guiGraphics.fill(x + 10, y + 12, x + 13, y + 15, ICON_DARK);
		guiGraphics.fill(x + 7, y + 7, x + 11, y + 13, ICON_DARK);
	}
}
