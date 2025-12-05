/**
 * @author ArcAnc
 * Created at: 04.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.book_data.page;

import com.arcanc.metamorphosis.content.book_data.BookData;
import com.arcanc.metamorphosis.content.book_data.BookPageData;
import com.arcanc.metamorphosis.content.book_data.chapter.AbstractBookChapter;
import com.arcanc.metamorphosis.content.gui.component.icon.Icon;
import com.arcanc.metamorphosis.content.gui.screen.GuideScreen;
import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Vec3i;
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

    private Vec3i toTitle, arrowLeft, arrowRight;

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

        for (int q = 0; q < filteredPages.size(); q++)
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
                }
                else if (objects.size() > 1 && objects.get(1) instanceof String str)
                    title = str.trim();
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

        zone = AbstractBookChapter.getPageZones().getFirst();

        this.toTitle = new Vec3i(zone.getX() + 15 + 103, zone.getY() + 170, 0);
        this.arrowLeft = new Vec3i(zone.getX(), zone.getY() + 145, 0);
        this.arrowRight = new Vec3i(zone.getX() + 15 + 225, zone.getY() + 145, 0);
    }

    @Override
    protected void renderPageContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        pages.forEach((integer, pageEntries) ->
                pageEntries.forEach(entry ->
                {
                    boolean bool = integer == BookData.getInstance().getCurrentSubpage() ||
                            integer == BookData.getInstance().getCurrentSubpage() + 1;
                    entry.visible = bool;
                    entry.active = bool;
                    if (bool)
                        entry.render(guiGraphics, mouseX, mouseY, partialTicks);
                }));
    }

    @Override
    protected void renderNavigationButtons(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        Font font = RenderHelper.mc().font;
        if (isArrowActive(this.arrowLeft))
        {
            if (isAboveArrow(mouseX, mouseY, this.arrowLeft))
            {
                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.arrowLeft.getX(), this.arrowLeft.getY(), 26, 207, 18, 10, 256, 256);
                guiGraphics.renderTooltip(font, Component.translatable(Database.GUI.GuideBook.Pages.Components.ARROW_LEFT), mouseX, mouseY);
            }
            else
                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.arrowLeft.getX(), this.arrowLeft.getY(), 3, 207, 18, 10, 256, 256);
        }
        if (isArrowActive(this.arrowRight))
        {
            if (isAboveArrow(mouseX, mouseY, this.arrowRight))
            {
                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.arrowRight.getX(), this.arrowRight.getY(), 26, 194, 18, 10, 256, 256);
                guiGraphics.renderTooltip(font, Component.translatable(Database.GUI.GuideBook.Pages.Components.ARROW_RIGHT), mouseX, mouseY);
            }
            else
                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.arrowRight.getX(), this.arrowRight.getY(), 3, 194, 18, 10, 256, 256);
        }
        if (isArrowActive(this.toTitle))
        {
            if (isAboveArrow(mouseX, mouseY, this.toTitle))
            {
                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.toTitle.getX(), this.toTitle.getY(), 49, 207, 17, 9, 256, 256);
                guiGraphics.renderTooltip(font, Component.translatable(Database.GUI.GuideBook.Pages.Components.ARROW_TO_TITLE), mouseX, mouseY);
            }
            else
                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.toTitle.getX(), this.toTitle.getY(), 49, 194, 17, 9, 256, 256);
        }
    }

    private boolean isAboveArrow(double mouseX, double mouseY, @NotNull Vec3i arrow)
    {
        return mouseX >= arrow.getX() && mouseY >= arrow.getY() && mouseX <= arrow.getX() + (arrow.equals(this.toTitle) ? 17 : 18) && mouseY <= arrow.getY() + (arrow.equals(this.toTitle) ? 9 : 10);
    }

    private boolean anyArrowClicked (double mouseX, double mouseY)
    {
        return isArrowClicked(mouseX, mouseY, this.arrowLeft) || isArrowClicked(mouseX, mouseY, this.arrowRight) || isArrowClicked(mouseX, mouseY, this.toTitle);
    }

    private boolean isArrowClicked(double mouseX, double mouseY, Vec3i arrow)
    {
        return isArrowActive(arrow) && isAboveArrow(mouseX, mouseY, arrow);
    }

    private boolean isArrowActive(@NotNull Vec3i arrow)
    {
        if (arrow.equals(this.toTitle) && !BookData.getInstance().getCurrentChapter().getData().id().equals(Database.GUI.GuideBook.Chapters.TITLE.location()))
            return true;
        else if (arrow.equals(this.arrowRight) && this.pages.containsKey(BookData.getInstance().getCurrentSubpage() + 2))
            return true;
        else return arrow.equals(this.arrowLeft) && BookData.getInstance().getHistorySize() > 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.active && this.visible)
        {
            if (this.isValidClickButton(button))
            {
                if (anyArrowClicked(mouseX, mouseY))
                {
                    this.onArrowClick(mouseX, mouseY, button);
                    return true;
                }
                else if (super.isMouseOver(mouseX, mouseY))
                {
                    this.onClick(mouseX, mouseY, button);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button)
    {
        pages.forEach((integer, pageEntries) -> pageEntries.forEach(page -> page.mouseClicked(mouseX, mouseY, button)));
    }

    public void onArrowClick(double mouseX, double mouseY, int button)
    {
        if (isArrowClicked(mouseX, mouseY, this.arrowLeft))
            BookData.getInstance().getScreen().setCurrentOpenedByHistory(BookData.getInstance().getLastHistoryEntry());
        else if (isArrowClicked(mouseX, mouseY, this.arrowRight))
            BookData.getInstance().getScreen().nextSubPage();
        else if (isArrowClicked(mouseX, mouseY, this.toTitle))
            BookData.getInstance().getScreen().jumpToFirstChapter();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return super.isMouseOver(mouseX, mouseY) || anyArrowClicked(mouseX, mouseY);
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
            BookData.getInstance().getScreen().jumpToPage(this.page);
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
