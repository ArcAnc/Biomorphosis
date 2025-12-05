/**
 * @author ArcAnc
 * Created at: 01.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.gui.component.icon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockIcon implements Icon
{
    private final Block block;

    public BlockIcon(Block block)
    {
        this.block = block;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int xPos, int yPos, int width, int height)
    {
        graphics.pose().pushPose();
        float scaleX = width / 16.0f;
        float scaleY = height / 16.0f;
        graphics.pose().translate(xPos, yPos, 0.f);
        graphics.pose().scale(scaleX, scaleY, 1.f);
        graphics.renderItem(new ItemStack(block), 0, 0);
        graphics.pose().popPose();
    }
}
