/**
 * @author ArcAnc
 * Created at: 31.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data;

import com.arcanc.biomorphosis.content.gui.component.icon.Icon;
import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NormalBookChapter extends AbstractBookChapter
{

    private final Icon icon;
    private final String title;

    public NormalBookChapter(@NotNull BookChapterData data)
    {
        super(data);
        List<Object> objects = Icon.IconParser.parse(Component.translatable(data.title()).getString());
        if (objects.getFirst() instanceof Icon ic)
            icon = ic;
        else
            icon = null;

        if (objects.get(1) instanceof String str)
            title = str.trim();
        else
            title = "";
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        guiGraphics.blit(RenderType :: guiTextured,
                GuideScreen.TEXT,
                this.getX(),
                this.getY(),
                236,
                isNative ? 0 : 26,
                this.getWidth(),
                this.getHeight(),
                256,
                256,
                ARGB.color(255, isActive() ? 255 : (int)(255 * 0.75f), isActive() ? 255 : (int)(255 * 0.75f)));

        if (icon != null)
            icon.render(guiGraphics, this.getX() + 1, this.getY() + 1, 18, 18);

        if (!title.isBlank() && isHovered)
            guiGraphics.renderTooltip(RenderHelper.mc().font, Component.literal(title), mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button)
    {
        Screen screen = RenderHelper.mc().screen;
        if (screen == null)
            return;
        if (screen instanceof GuideScreen guideScreen)
            guideScreen.setCurrentChapter(this.getData().id());
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {

    }
}
