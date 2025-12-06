/**
 * @author ArcAnc
 * Created at: 31.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page;

import com.arcanc.biomorphosis.content.book_data.BookData;
import com.arcanc.biomorphosis.content.book_data.BookPageData;
import com.arcanc.biomorphosis.content.book_data.chapter.AbstractBookChapter;
import com.arcanc.biomorphosis.content.book_data.page.component.AbstractPageComponent;
import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NormalBookPage extends AbstractBookPage
{
    private Vec3i toTitle, arrowLeft, arrowRight;

    public NormalBookPage(@NotNull BookPageData data)
    {
        super(data);
    }

    @Override
    protected void renderPageContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        int subPage = BookData.getInstance().getCurrentSubpage();
        List<AbstractPageComponent> currentPage = this.dividedComponents.get(subPage);
        if (currentPage != null)
            currentPage.forEach(component -> component.render(guiGraphics, mouseX, mouseY, partialTicks));

        currentPage = this.dividedComponents.get(subPage + 1);
        if (currentPage != null)
            currentPage.forEach(component -> component.render(guiGraphics, mouseX, mouseY, partialTicks));
    }

    @Override
    public void reCalcPositions()
    {
        super.reCalcPositions();

        Rect2i zone = AbstractBookChapter.getPageZones().getFirst();

        this.toTitle = new Vec3i(zone.getX() + 15 + 103, zone.getY() + 170, 0);
        this.arrowLeft = new Vec3i(zone.getX(), zone.getY() + 145, 0);
        this.arrowRight = new Vec3i(zone.getX() + 15 + 225, zone.getY() + 145, 0);
    }

    private boolean isArrowActive(@NotNull Vec3i arrow)
    {
        if (arrow.equals(this.toTitle))
            return true;
        else if (arrow.equals(this.arrowRight) && this.dividedComponents.containsKey(BookData.getInstance().getCurrentSubpage() + 2))
            return true;
        else return arrow.equals(this.arrowLeft) && BookData.getInstance().getHistorySize() > 1;
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
        if (isAboveArrow(mouseX, mouseY, this.toTitle))
        {
            guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.toTitle.getX(), this.toTitle.getY(), 49, 207, 17, 9, 256, 256);
            guiGraphics.renderTooltip(font, Component.translatable(Database.GUI.GuideBook.Pages.Components.ARROW_TO_TITLE), mouseX, mouseY);
        }
        else
            guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, this.toTitle.getX(), this.toTitle.getY(), 49, 194, 17, 9, 256, 256);
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
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return super.isMouseOver(mouseX, mouseY) || anyArrowClicked(mouseX, mouseY);
    }

    public void onArrowClick(double mouseX, double mouseY, int button)
    {
        if (isArrowClicked(mouseX, mouseY, this.arrowLeft))
            BookData.getInstance().getScreen().setCurrentOpenedByHistory(BookData.getInstance().getLastHistoryEntry());
        else if (isArrowClicked(mouseX, mouseY, this.arrowRight))
            BookData.getInstance().getScreen().nextSubPage();
        else if (isArrowClicked(mouseX, mouseY, this.toTitle))
            BookData.getInstance().getScreen().setTitlePage();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {

    }
}
