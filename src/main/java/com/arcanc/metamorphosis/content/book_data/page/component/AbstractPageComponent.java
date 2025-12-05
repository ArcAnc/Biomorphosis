/**
 * @author ArcAnc
 * Created at: 02.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.book_data.page.component;

import com.arcanc.metamorphosis.content.book_data.chapter.AbstractBookChapter;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public abstract class AbstractPageComponent extends AbstractWidget
{
    private int shiftX = 0;
    private int owningSubpage = 0;
    public AbstractPageComponent(int x, int y, int width, int height, Component message)
    {
        super(x, y, width, height, message);
    }

    public void reCalcShiftX(int subPage)
    {
        Rect2i zone = AbstractBookChapter.getPageZones().get(subPage);
        this.shiftX = zone.getWidth() / 2 - getWidth() / 2;
    }

    public int getShiftX()
    {
        return shiftX;
    }

    public int getOwningSubpage()
    {
        return owningSubpage;
    }

    public void setOwningSubpage(int owningSubpage)
    {
        this.owningSubpage = owningSubpage;
    }
}
