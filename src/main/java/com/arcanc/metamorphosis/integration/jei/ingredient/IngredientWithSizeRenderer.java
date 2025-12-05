/**
 * @author ArcAnc
 * Created at: 02.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.integration.jei.ingredient;

import com.arcanc.metamorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
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

public class IngredientWithSizeRenderer implements IIngredientRenderer<IngredientWithSize>
{
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull IngredientWithSize ingredient)
    {
        ItemStack stack = RenderHelper.getStackAtCurrentTime(ingredient);
        if (stack.isEmpty())
            return;
        Minecraft mc = RenderHelper.mc();
        guiGraphics.renderItem(stack, 0,0);
        guiGraphics.renderItemDecorations(mc.font, stack, 0,0);
    }

    @Override
    public List<Component> getTooltip(@NotNull IngredientWithSize ingredient, @NotNull TooltipFlag tooltipFlag)
    {
        Minecraft mc = RenderHelper.mc();
        Level level = mc.level;
        if (level == null)
            return List.of();
        Item.TooltipContext context = Item.TooltipContext.of(level);
        return RenderHelper.getStackAtCurrentTime(ingredient).getTooltipLines(context, mc.player, tooltipFlag);
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull IngredientWithSize ingredient, @NotNull TooltipFlag tooltipFlag)
    {
        tooltip.clear();
        tooltip.addAll(getTooltip(ingredient, tooltipFlag));
    }
}
