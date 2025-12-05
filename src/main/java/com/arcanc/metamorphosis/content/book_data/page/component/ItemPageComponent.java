/**
 * @author ArcAnc
 * Created at: 02.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.book_data.page.component;

import com.arcanc.metamorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemPageComponent extends AbstractPageComponent
{
    private final ItemStack item;

    public ItemPageComponent(ResourceLocation location)
    {
        super(0, 0, 18, 18, Component.empty());

        this.item = new ItemStack(BuiltInRegistries.ITEM.getValue(location));
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        guiGraphics.renderItem(item, this.getX() + 1, this.getY() + 1);
        if (isHovered())
            guiGraphics.renderTooltip(RenderHelper.mc().font, item, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
    {

    }
}
