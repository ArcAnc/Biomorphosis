/**
 * @author ArcAnc
 * Created at: 05.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.integration.jei.ingredient;

import com.arcanc.metamorphosis.util.helper.RenderHelper;
import com.arcanc.metamorphosis.util.inventory.item.StackWithChance;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackWithChanceRenderer implements IIngredientRenderer<StackWithChance>
{
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull StackWithChance ingredient)
    {
        ItemStack stack = ingredient.stack();
        if (stack.isEmpty())
            return;
        Minecraft mc = RenderHelper.mc();
        guiGraphics.renderItem(stack, 0,0);
        guiGraphics.renderItemDecorations(mc.font, stack, 0,0);

        guiGraphics.drawString(mc.font, Component.literal(String.format("%.1f", ingredient.chance() * 100) + "%"), 0, 20, -1, false);
    }

    @Override
    public List<Component> getTooltip(@NotNull StackWithChance ingredient, @NotNull TooltipFlag tooltipFlag)
    {
        Minecraft mc = RenderHelper.mc();
        Level level = mc.level;
        if (level == null)
            return List.of();
        Item.TooltipContext context = Item.TooltipContext.of(level);
        return ingredient.stack().getTooltipLines(context, mc.player, tooltipFlag);
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull StackWithChance ingredient, @NotNull TooltipFlag tooltipFlag)
    {
        tooltip.clear();
        tooltip.addAll(getTooltip(ingredient, tooltipFlag));
    }
}
