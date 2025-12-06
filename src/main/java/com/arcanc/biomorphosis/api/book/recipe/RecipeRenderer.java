/**
 * @author ArcAnc
 * Created at: 06.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.api.book.recipe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Recipe;

public interface RecipeRenderer
{
    void renderRecipe(Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    /**
     * This MUST be in pixels!!!
     */
    int getHeight();

    /**
     * This MUST be in pixels!!!
     */
    int getWidth();
}
