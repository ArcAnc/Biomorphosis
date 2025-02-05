/**
 * @author ArcAnc
 * Created at: 04.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page;

import com.arcanc.biomorphosis.content.book_data.BookData;
import com.arcanc.biomorphosis.content.book_data.BookPageData;
import com.arcanc.biomorphosis.content.book_data.chapter.AbstractBookChapter;
import com.arcanc.biomorphosis.content.gui.component.icon.Icon;
import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleBookPage extends AbstractBookPage
{
    private static final int ENTRY_OFFSET = 2;

    private final Map<Integer, List<PageEntry>> pages = new HashMap<>();
    private final AbstractBookChapter chapter;

    public TitleBookPage(AbstractBookChapter chapter)
    {
        super(BookPageData.getTitleData(chapter));
        this.chapter = chapter;
    }

    @Override
    public void reCalcPositions()
    {
        pages.clear();
        int subPage = 0;
        List<AbstractBookPage> filteredPages = BookData.getInstance().getContent().get(chapter);
        Rect2i zone = AbstractBookChapter.getPageZones().getFirst();
        int yPos = zone.getY();

        for (int q= 0; q < filteredPages.size(); q++)
        {
            AbstractBookPage page = filteredPages.get(q);
            if (page == this)
                continue;

            this.pages.computeIfAbsent(subPage, key -> new ArrayList<>());

            List<Object> objects = Icon.IconParser.parse(Component.translatable(page.getData().title()).getString());
            Icon icon = null;
            String title = "";
            if (!objects.isEmpty())
            {
                if (objects.getFirst() instanceof Icon ic)
                    icon = ic;
                if (icon == null)
                {
                    if (objects.getFirst() instanceof String str)
                        title = str.trim();
                    else if (objects.size() > 1 && objects.get(1) instanceof String str)
                        title = str.trim();
                }
            }

            PageEntry entry = new PageEntry(new Vector2i(zone.getX(), yPos), q, icon, title, zone.getWidth());

            if (yPos + entry.getHeight() > zone.getY() + zone.getHeight())
            {
                subPage++;
                zone = AbstractBookChapter.getPageZones().get(subPage % 2);
                yPos = zone.getY();
                this.pages.computeIfAbsent(subPage, value -> new ArrayList<>());
                entry = new PageEntry(new Vector2i(zone.getX(), yPos), q, icon, title, subPage);
            }

            this.pages.get(subPage).add(entry);
            yPos += entry.getHeight() + ENTRY_OFFSET;
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        pages.forEach((integer, pageEntries) ->
                pageEntries.forEach(entry ->
                {
                    boolean bool = integer == BookData.getInstance().getCurrentSubpage() ||
                            integer == BookData.getInstance().getCurrentSubpage() + 1;
                    entry.visible = bool;
                    entry.active = bool;
                    if (bool)
                        entry.render(guiGraphics, mouseX, mouseY, partialTick);
                }));
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button)
    {
        pages.forEach((integer, pageEntries) -> pageEntries.forEach(page -> page.mouseClicked(mouseX, mouseY, button)));
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {

    }

    private static class PageEntry extends AbstractWidget
    {
        private final int page;
        private final Icon icon;
        private final String title;

        private final List<FormattedCharSequence> lines = new ArrayList<>();

        private static final int LINE_HEIGHT = 11;

        private PageEntry(@NotNull Vector2i pos, int page, Icon icon, String title, int subPage)
        {
            super(pos.x(), pos.y(), 0,0, Component.empty());
            this.page = page;
            this.icon = icon;
            this.title = title;

            Rect2i zone = AbstractBookChapter.getPageZones().get(subPage % 2);
            lines.addAll(RenderHelper.mc().font.split(Component.literal(title), zone.getWidth()));
            this.setSize(zone.getWidth(), lines.size() * LINE_HEIGHT);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
        {
            int yOffset = 0;

            if (icon != null)
                icon.render(guiGraphics, getX(), getY(), 9, 9);

            guiGraphics.drawString(RenderHelper.mc().font, this.lines.getFirst(), getX() + (icon != null ? 11 : 0), getY() + yOffset, Color.black.getRGB(), false);
            yOffset += LINE_HEIGHT;

            if (lines.size() > 1)
                for (int q = 1; q < lines.size(); q++)
                {
                    FormattedCharSequence line = lines.get(q);
                    guiGraphics.drawString(RenderHelper.mc().font, line, getX(), getY() + yOffset, Color.black.getRGB(), false);
                    yOffset += LINE_HEIGHT;
                }

            if (isMouseOver(mouseX, mouseY))
                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, getX() - 3, getY() - 3, 209, 113, getWidth() + 3, getHeight() + 3, 47, 11, 256, 256);
        }

        @Override
        public void onClick(double mouseX, double mouseY, int button)
        {
            BookData.getInstance().getScreen().setCurrentPage(this.page);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
        {

        }

        public String getTitle()
        {
            return title;
        }

        public int getPage()
        {
            return page;
        }
    }
}
