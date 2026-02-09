/**
 * @author ArcAnc
 * Created at: 13.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.info;


import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class ErrorInfoArea extends InfoArea
{
	private static final int ERROR_SHOW_TIME_LIMIT = 40;
	
	private MutableComponent error;
	private float errorShowTimer = ERROR_SHOW_TIME_LIMIT;
	
	public ErrorInfoArea(Rect2i area)
	{
		super(area);
	}
	
	@Override
	protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
	{
	
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		Minecraft mc = RenderHelper.mc();
		Font font = mc.font;
		
		if (this.error != null && this.errorShowTimer < ERROR_SHOW_TIME_LIMIT)
		{
			int stringsHeight = font.wordWrapHeight(this.error, this.area.getWidth());
			
			this.errorShowTimer += partialTick;
			guiGraphics.drawWordWrap(font,
					this.error,
					(this.area.getX()),
					this.area.getY() + this.area.getHeight() / 2 - stringsHeight / 2,
					this.area.getWidth(),
					-1);
		}
	}
	
	public void updateError(String mainErrorMessage, Object... additionalInfo)
	{
		this.error = Component.translatable(mainErrorMessage, additionalInfo);
		this.errorShowTimer = 0;
	}
}
