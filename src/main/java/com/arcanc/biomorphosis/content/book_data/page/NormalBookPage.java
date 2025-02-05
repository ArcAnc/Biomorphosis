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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.NotNull;

public class NormalBookPage extends AbstractBookPage
{
    public NormalBookPage(@NotNull BookPageData data)
    {
        super(data);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.components.stream().
                filter(component -> component.getOwningSubpage() == BookData.getInstance().getCurrentSubpage() ||
                        component.getOwningSubpage() == BookData.getInstance().getCurrentSubpage() + 1).
                forEach(component -> component.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {

    }
}
