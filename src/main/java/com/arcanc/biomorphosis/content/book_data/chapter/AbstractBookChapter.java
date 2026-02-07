/**
 * @author ArcAnc
 * Created at: 31.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.chapter;

import com.arcanc.biomorphosis.content.book_data.BookChapterData;
import com.arcanc.biomorphosis.content.book_data.BookData;
import com.arcanc.biomorphosis.content.book_data.page.AbstractBookPage;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBookChapter extends AbstractWidget
{
    protected static final List<Rect2i> PAGE_ZONES = new ArrayList<>();

    private final BookChapterData data;

    protected final boolean isNative;

    public AbstractBookChapter(@NotNull BookChapterData data)
    {
        super(0, 0, 20, 26, Component.translatable(data.title()));
        this.data = data;
        isNative = data.id().getNamespace().equals(Database.MOD_ID);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.visible)
        {
            if (this.isValidClickButton(button))
            {
                if (this.isMouseOverChapter(mouseX, mouseY))
                {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY, button);
                    return true;
                }
                else if(isMouseOverPage(mouseX, mouseY))
                {
                    return onPageClick(mouseX, mouseY, button);
                }
            }
        }
        return false;
    }

    private boolean onPageClick(double mouseX, double mouseY, int button)
    {
        AbstractBookPage page = BookData.getInstance().getCurrentPage();
        if (page != null)
            return BookData.getInstance().getCurrentPage().mouseClicked(mouseX, mouseY, button);
        return false;
    }

    public boolean isMouseOverPage(double mouseX, double mouseY)
    {
        if (!isActive())
            return false;
        AbstractBookPage page = BookData.getInstance().getCurrentPage();
        if (page == null)
            return false;
        return page.isMouseOver(mouseX, mouseY);
    }

    public boolean isMouseOverChapter(double mouseX, double mouseY)
    {
        return this.visible &&
                mouseX >= this.getX() &&
                mouseY >= this.getY() &&
                mouseX < this.getRight() &&
                mouseY < this.getBottom();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return this.visible &&
                (isMouseOverChapter(mouseX, mouseY) ||
                 isMouseOverPage(mouseX, mouseY));
    }

    public static List<Rect2i> getPageZones()
    {
        return PAGE_ZONES;
    }

    public static void setPageZones(Rect2i... pageZones)
    {
        PAGE_ZONES.clear();
        PAGE_ZONES.addAll(List.of(pageZones));
    }

    public BookChapterData getData()
    {
        return data;
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean value)
    {
        this.active = value;
    }

    public boolean isNative()
    {
        return isNative;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AbstractBookChapter that))
            return false;
        return getData().id().equals(that.getData().id());
    }
}
