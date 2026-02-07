/**
 * @author ArcAnc
 * Created at: 23.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;

import com.arcanc.biomorphosis.api.book.recipe.RecipeRenderer;
import com.arcanc.biomorphosis.content.book_data.page.component.recipes.AbstractRecipeComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

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
            return this.x;
        }

        public int getY()
        {
            return this.y;
        }

        public int getWidth()
        {
            return this.width;
        }

        public int getHeight()
        {
            return this.height;
        }

        public GuiGraphics getGuiGraphics()
        {
            return this.guiGraphics;
        }

        public ItemStack getStack()
        {
            return this.stack;
        }
    }

    public static class AddRecipeRenderer extends Event
    {
        private final Map<RecipeType<?>, RecipeRenderer> additionalRenderers = new HashMap<>();

        public AddRecipeRenderer()
        {
        }

        public void addRenderer(RecipeType<?> type, RecipeRenderer renderer)
        {
            this.additionalRenderers.put(type, renderer);
        }

        public void addCustomRenderers()
        {
            if (!this.additionalRenderers.isEmpty())
                this.additionalRenderers.forEach(AbstractRecipeComponent.RecipeRenderCache :: addNewRenderer);
        }
    }
}
