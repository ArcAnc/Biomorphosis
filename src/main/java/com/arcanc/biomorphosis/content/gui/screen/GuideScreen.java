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
import com.arcanc.biomorphosis.content.book_data.chapter.AbstractBookChapter;
import com.arcanc.biomorphosis.content.book_data.page.AbstractBookPage;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class GuideScreen extends Screen
{
    public static final ResourceLocation TEXT = Database.GUI.getTexturePath("gui/book/book");

    private int xSize = 292;
    private int ySize = 180;
    public int guiLeft;
    public int guiTop;

    private AbstractBookChapter currentChapter;
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
            setCurrentChapter(BookData.getInstance().
                    getContent().
                    keySet().
                    stream().
                    filter(chapter -> chapter.
                            getData().
                            id().
                            equals(Database.GUI.GuideBook.Chapters.TITLE.location())).
                    findFirst().
                    orElse(null));
            setCurrentPage(0);
            setCurrentSubPage(0);
        }
        else
            setCurrentOpenedByHistory(entry);
    }

    public AbstractBookChapter getCurrentChapter()
    {
        return currentChapter;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public int getCurrentSubPage()
    {
        return currentSubPage;
    }

    public void jumpToFirstChapter()
    {
        jumpToChapter(BookData.getInstance().
                getContent().
                keySet().
                stream().
                filter(chapter -> chapter.
                        getData().
                        id().
                        equals(Database.GUI.GuideBook.Chapters.TITLE.location())).
                findFirst().
                orElse(null));
    }

    public void setCurrentOpenedByHistory(BookData.@NotNull BookHistoryEntry entry)
    {
        setCurrentChapter(entry.chapter());
        setCurrentPage(entry.page());
        setCurrentSubPage(entry.subPage());
    }

    private void setCurrentOpenedData(AbstractBookChapter chapter, int page, int subPage)
    {
        setCurrentChapter(chapter);
        setCurrentPage(page);
        setCurrentSubPage(subPage);
    }

    public void jumpToChapter(AbstractBookChapter chapter)
    {
        addNewHistory();
        setCurrentChapter(chapter);
    }

    private void setCurrentChapter(AbstractBookChapter chapter)
    {
        this.currentChapter = chapter;
        setCurrentPage(0);

        BookData.getInstance().getContent().keySet().forEach(chapt -> chapt.setActive(chapt.equals(this.currentChapter)));
    }

    public void setTitlePage()
    {
        addNewHistory();
        setCurrentPage(0);
    }

    public void jumpToPage(int page)
    {
        addNewHistory();
        setCurrentPage(page);
    }

    private void setCurrentPage(int page)
    {
        this.currentPage = page;
        setCurrentSubPage(0);

        List<AbstractBookPage> pages = BookData.getInstance().getContent().get(BookData.getInstance().getCurrentChapter());
        for (int q = 0; q < pages.size(); q++)
        {
            AbstractBookPage absPage = pages.get(q);
            boolean newValue = page == q;
            absPage.visible = newValue;
            absPage.active = newValue;
        }
    }

    public void nextSubPage()
    {
        addNewHistory();
        setCurrentSubPage(this.currentSubPage + 2);
    }

    private void setCurrentSubPage(int subPage)
    {
        this.currentSubPage = subPage;
    }

    private void addNewHistory()
    {
        BookData.getInstance().addNewHistoryEntry(this.currentChapter, this.currentPage, this.currentSubPage);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return super.mouseClicked(mouseX, mouseY, button);
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
