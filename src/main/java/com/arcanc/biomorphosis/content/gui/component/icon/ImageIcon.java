/**
 * @author ArcAnc
 * Created at: 01.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.icon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ImageIcon implements Icon
{
    private final ResourceLocation image;
    private final int imgWidth;
    private final int imgHeight;

    public ImageIcon(ResourceLocation image, int imgWidth, int imgHeight)
    {
        this.image = image;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int xPos, int yPos, int width, int height)
    {
        graphics.blit(this.image, xPos, yPos, 0, 0, this.imgWidth, this.imgHeight, width, height, this.imgWidth, this.imgHeight);
    }
}
