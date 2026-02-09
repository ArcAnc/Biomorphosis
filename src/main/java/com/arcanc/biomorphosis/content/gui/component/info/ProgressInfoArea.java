/**
 * @author ArcAnc
 * Created at: 01.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.info;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;

public class ProgressInfoArea extends InfoArea
{
    protected final ProgressInfo info;
    protected final ResourceLocation progressTexture;

    public ProgressInfoArea(Rect2i area, ProgressInfo info)
    {
        this(area, info, PROGRESS);
    }

    public ProgressInfoArea(Rect2i area, ProgressInfo info, ResourceLocation texture)
    {
        super(area);
        this.info = info;
        this.progressTexture = texture;
    }

    @Override
    protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
    {
        int current = this.info.current().get();
        int max = this.info.max().get();

        Minecraft mc = RenderHelper.mc();

        float percent = (float)current/max;

        if (!Screen.hasShiftDown())
        {
            if (current <= 0)
                tooltip.add(Component.translatable(Database.GUI.InfoArea.ProgressBar.Tooltip.PERCENT, 0, 100).withStyle(ChatFormatting.GRAY));
            else
                tooltip.add(Component.translatable(Database.GUI.InfoArea.ProgressBar.Tooltip.PERCENT, BigDecimal.valueOf(percent * 100).setScale(2, RoundingMode.HALF_UP), 100).withStyle(ChatFormatting.GRAY));
        }
        else
        {
            if (current <= 0)
                tooltip.add(Component.translatable(Database.GUI.InfoArea.ProgressBar.Tooltip.DIRECT, 0, "???").withStyle(ChatFormatting.GRAY));
            else
                tooltip.add(Component.translatable(Database.GUI.InfoArea.ProgressBar.Tooltip.DIRECT, current, max).withStyle(ChatFormatting.GRAY));
        }

        if (mc.options.advancedItemTooltips)
            if (!Screen.hasShiftDown())
            {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable(Database.GUI.HOLD_SHIFT).withStyle(ChatFormatting.DARK_GRAY));
            }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        int current = this.info.current().get();
        int max = this.info.max().get();

        guiGraphics.blit(this.progressTexture,
                this.area.getX(), this.area.getY(),
                0, 0,
                this.area.getWidth(), this.area.getHeight(),
                22, 15,
                32, 32);

        if (current <= 0)
            return;

        float percent = (float)current / max;

        float width = this.area.getWidth() * percent;
        float textureWidth = 22 * percent;

        RenderHelper.blit(
                guiGraphics,
                this.progressTexture,
                this.area.getX(),
                this.area.getY(),
                0, 16,
                width, this.area.getHeight(),
                0,
                textureWidth, 15,
                32, 32);
    }

    public record ProgressInfo(Supplier<Integer> current, Supplier<Integer> max){}
}
