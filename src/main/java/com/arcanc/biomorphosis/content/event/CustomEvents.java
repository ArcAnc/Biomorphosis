/**
 * @author ArcAnc
 * Created at: 23.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class CustomEvents
{
    public static class TooltipDisplayEvent extends Event
    {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final GuiGraphics guiGraphics;
        private final ItemStack stack;

        public TooltipDisplayEvent(int x,
                                   int y,
                                   int width,
                                   int height,
                                   GuiGraphics guiGraphics,
                                   ItemStack stack)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.guiGraphics = guiGraphics;
            this.stack = stack;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }

        public GuiGraphics getGuiGraphics()
        {
            return guiGraphics;
        }

        public ItemStack getStack()
        {
            return stack;
        }
    }
}
