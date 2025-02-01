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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemIcon implements Icon
{
    private final Item item;

    public ItemIcon(Item item)
    {
        this.item = item;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int xPos, int yPos, int width, int height)
    {
        float scaleX = width / 16f;
        float scaleY = height / 16f;

        graphics.pose().pushPose();
        graphics.pose().translate(xPos, yPos, 0.f);
        graphics.pose().scale(scaleX, scaleY, 1.f);
        graphics.renderItem(new ItemStack(item), 0,0);
        graphics.pose().popPose();
    }
}
