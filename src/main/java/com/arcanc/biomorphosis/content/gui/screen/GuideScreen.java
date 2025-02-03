/**
 * @author ArcAnc
 * Created at: 26.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen;

import com.arcanc.biomorphosis.content.book_data.BookData;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GuideScreen extends Screen
{
    public static final ResourceLocation TEXT = Database.GUI.getTexturePath("gui/book/book");

    private int xSize = 292;
    private int ySize = 180;
    public int guiLeft;
    public int guiTop;

    private ResourceLocation currentChapter;
    private int currentPage;
    private int currentSubPage;

    public GuideScreen()
    {
        super(Component.empty());
    }

    @Override
    protected void init()
    {
        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        BookData.getInstance().setScreen(this);
        BookData.getInstance().reCalcPositions();

        BookData.getInstance().getContent().forEach((chapter, pages) ->
                addRenderableWidget(chapter));

        BookData.BookHistoryEntry entry = BookData.getInstance().getLastHistoryEntry();

        if (entry == null)
        {
            currentChapter = Database.GUI.GuideBook.Chapters.BASIC.location();
            currentPage = 0;
            currentSubPage = 0;
        }
        else
        {
            currentChapter = entry.chapter();
            currentPage = entry.page();
            currentSubPage = entry.subPage();
        }

        BookData.getInstance().addNewHistoryEntry(currentChapter, currentPage, currentSubPage);

        setCurrentChapter(currentChapter);
    }

    public void setCurrentChapter(ResourceLocation chapter)
    {
        BookData.getInstance().getContent().keySet().forEach(chapt -> chapt.setActive(chapt.getData().id().equals(chapter)));
        BookData.getInstance().addNewHistoryEntry(chapter, currentPage, currentSubPage);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.blit(RenderType :: guiTextured, TEXT, guiLeft + xSize / 2, guiTop, 20, 1, 146, 180, 256, 256);

        guiGraphics.pose().scale(-1f, -1f, 1f);

        guiGraphics.blit(RenderType :: guiTextured, TEXT, -guiLeft - xSize / 2, - guiTop - ySize, 20, 1, 146, 180, 256, 256);

        guiGraphics.pose().scale(-1f, -1f, 1f);
        guiGraphics.pose().popPose();
    }

    public int getGuiLeft()
    {
        return guiLeft;
    }

    public int getGuiTop()
    {
        return guiTop;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
