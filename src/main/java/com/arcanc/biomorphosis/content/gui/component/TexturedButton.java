/**
 * @author ArcAnc
 * Created at: 31.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TexturedButton extends Button
{
	protected final WidgetSprites sprites;
	
	public TexturedButton(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress, Tooltip tooltip)
	{
		super(x, y, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
		this.sprites = sprites;
		this.setTooltip(tooltip);
	}
	
	@Override
	protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
	{
		ResourceLocation resourcelocation = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
		guiGraphics.blit(RenderType :: guiTextured,
				resourcelocation,
				this.getX(), this.getY(),
				0, 0,
				this.width, this.height,
				16, 16,
				16, 16);
	}
}
