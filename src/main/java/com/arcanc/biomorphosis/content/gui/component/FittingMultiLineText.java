/**
 * @author ArcAnc
 * Created at: 31.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class FittingMultiLineText extends AbstractScrollWidget
{
	private static final int DEFAULT_INNER_PADDING = 4;
	private final int innerPaddingX;
	private final int innerPaddingY;
	private final MultiLineTextWidget multilineWidget;
	
	public FittingMultiLineText(int x, int y, int width, int height, Component message, Font font)
	{
		this(x, y, width, height, message, font, DEFAULT_INNER_PADDING, DEFAULT_INNER_PADDING);
	}

	public FittingMultiLineText(int x, int y, int width, int height, Component message, Font font, int innerPaddingX, int innerPaddingY)
	{
		super(x, y, width, height, message);
		if (innerPaddingX < 0 || innerPaddingY < 0)
			throw new IllegalArgumentException("Inner padding cannot be negative");

		this.innerPaddingX = innerPaddingX;
		this.innerPaddingY = innerPaddingY;
		this.multilineWidget = new MultiLineTextWidget(message, font).setMaxWidth(this.getTextWidth());
	}
	
	public FittingMultiLineText setColor(int color)
	{
		this.multilineWidget.setColor(color);
		return this;
	}

	public void renderScaled(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, float scale, int originX, int originY)
	{
		if (!this.visible)
			return;
		if (scale <= 0.0F)
			throw new IllegalArgumentException("Scale must be positive");

		guiGraphics.pose().pushPose();
		applyScale(guiGraphics, scale, originX, originY);
		this.renderBackground(guiGraphics);
		guiGraphics.pose().popPose();
		
		guiGraphics.pose().pushPose();
		guiGraphics.enableScissor(
				scaleCoordinate(this.getX(), scale, originX),
				scaleCoordinate(this.getY(), scale, originY),
				scaleCoordinate(this.getX() + this.getWidth(), scale, originX),
				scaleCoordinate(this.getY() + this.getHeight(), scale, originY));
		applyScale(guiGraphics, scale, originX, originY);
		guiGraphics.pose().translate(0.0F, (float)-this.scrollAmount(), 0.0F);
		this.renderContents(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.disableScissor();
		guiGraphics.pose().popPose();

		guiGraphics.pose().pushPose();
		applyScale(guiGraphics, scale, originX, originY);
		this.renderDecorations(guiGraphics);
		guiGraphics.pose().popPose();
	}
	
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		this.multilineWidget.setMaxWidth(this.getTextWidth());
	}
	
	@Override
	public void setMessage(Component message)
	{
		if (this.getMessage().equals(message))
			return;
		super.setMessage(message);
		this.multilineWidget.setMessage(message);
		this.setScrollAmount(this.scrollAmount());
	}
	
	@Override
	protected int getInnerHeight()
	{
		return this.multilineWidget.getHeight() + this.innerPaddingY * 2 - DEFAULT_INNER_PADDING * 2;
	}
	
	@Override
	protected double scrollRate()
	{
		return 9.0;
	}
	
	@Override
	protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.getX() + this.innerPaddingX, this.getY() + this.innerPaddingY, 0.0F);
		this.multilineWidget.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.pose().popPose();
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narration)
	{
		narration.add(NarratedElementType.TITLE, this.getMessage());
	}

	private int getTextWidth()
	{
		return Math.max(0, this.getWidth() - this.innerPaddingX * 2);
	}

	private static void applyScale(GuiGraphics guiGraphics, float scale, int originX, int originY)
	{
		guiGraphics.pose().translate(originX, originY, 0.0F);
		guiGraphics.pose().scale(scale, scale, 1.0F);
		guiGraphics.pose().translate(-originX, -originY, 0.0F);
	}

	private static int scaleCoordinate(int coordinate, float scale, int origin)
	{
		return Math.round(origin + (coordinate - origin) * scale);
	}
}
