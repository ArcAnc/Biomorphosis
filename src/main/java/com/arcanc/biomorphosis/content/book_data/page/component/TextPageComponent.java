/**
 * @author ArcAnc
 * Created at: 02.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page.component;

import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextPageComponent extends AbstractPageComponent
{
    public TextPageComponent(Component message)
    {
        super(0,0, 107, 0, message);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        Component comp = getMessage();
        if (!comp.getString().isBlank())
            guiGraphics.drawWordWrap(RenderHelper.mc().font, comp, getX(), getY(), getWidth(), Color.black.getRGB(), false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
