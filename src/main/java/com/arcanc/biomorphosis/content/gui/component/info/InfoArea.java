/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.info;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public abstract class InfoArea
{
    public static final ResourceLocation TANK = Database.rl("textures/gui/fluid.png");

    protected final Rect2i area;

    protected InfoArea(Rect2i area)
    {
        this.area = area;
    }

    public final void fillTooltip(int mouseX, int mouseY, List<Component> tooltip)
    {
        if(this.area.contains(mouseX, mouseY))
            fillTooltipOverArea(mouseX, mouseY, tooltip);
    }

    protected abstract void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip);

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}
