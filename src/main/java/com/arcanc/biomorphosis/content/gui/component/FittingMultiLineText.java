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
import org.jetbrains.annotations.NotNull;

public class FittingMultiLineText extends AbstractScrollWidget
{
	private final MultiLineTextWidget multilineWidget;
	
	public FittingMultiLineText(int x, int y, int width, int height, Component message, Font font)
	{
		super(x, y, width, height, message);
		this.multilineWidget = new MultiLineTextWidget(message, font).setMaxWidth(this.getWidth() - this.totalInnerPadding());
	}
	
	public FittingMultiLineText setColor(int color)
	{
		this.multilineWidget.setColor(color);
		return this;
	}
	
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		this.multilineWidget.setMaxWidth(this.getWidth() - this.totalInnerPadding());
	}
	
	@Override
	public void setMessage(@NotNull Component message)
	{
		super.setMessage(message);
		this.multilineWidget.setMessage(message);
	}
	
	@Override
	protected int getInnerHeight()
	{
		return this.multilineWidget.getHeight();
	}
	
	@Override
	protected double scrollRate()
	{
		return 9.0;
	}
	
	@Override
	protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.getX() + this.innerPadding(), this.getY() + this.innerPadding(), 0.0F);
		this.multilineWidget.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.pose().popPose();
	}
	
	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narration)
	{
		narration.add(NarratedElementType.TITLE, this.getMessage());
	}
}
