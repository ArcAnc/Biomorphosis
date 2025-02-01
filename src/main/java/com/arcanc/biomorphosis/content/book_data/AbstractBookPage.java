/**
 * @author ArcAnc
 * Created at: 29.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBookPage extends AbstractWidget
{
    private final BookPageData data;

    public AbstractBookPage(@NotNull BookPageData data)
    {
        super(0, 0, 16, 16, Component.translatable(data.title()));
        this.data = data;
    }

    public BookPageData getData()
    {
        return data;
    }
}
