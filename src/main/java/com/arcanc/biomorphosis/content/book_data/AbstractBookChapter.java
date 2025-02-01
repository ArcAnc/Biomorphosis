/**
 * @author ArcAnc
 * Created at: 31.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBookChapter extends AbstractWidget
{
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
                boolean flag = this.isMouseOver(mouseX, mouseY);
                if (flag)
                {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
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
        return this.visible
                && mouseX >= (double)this.getX()
                && mouseY >= (double)this.getY()
                && mouseX < (double)this.getRight()
                && mouseY < (double)this.getBottom();
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
}
