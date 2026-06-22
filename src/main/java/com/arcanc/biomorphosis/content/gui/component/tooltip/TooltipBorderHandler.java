/**
 * @author ArcAnc
 * Created at: 26.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.tooltip;

import com.arcanc.biomorphosis.content.event.CustomEvents;
import com.arcanc.biomorphosis.content.gui.component.animation.AnimationData;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.opengl.GL11;

public class TooltipBorderHandler
{
    private static TooltipData overrideTooltipData;

    public static void registerHandler()
    {
        NeoForge.EVENT_BUS.addListener(TooltipBorderHandler :: tooltipDisplayEvent);
        NeoForge.EVENT_BUS.addListener(TooltipBorderHandler :: tooltipBackgroundEvent);
    }

    public static void pushTooltipStyle(TooltipData data)
    {
        overrideTooltipData = data;
    }

    public static void popTooltipStyle()
    {
        overrideTooltipData = null;
    }

    private static void tooltipDisplayEvent(final CustomEvents.TooltipDisplayEvent event)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return;

        TooltipData data = getTooltipData(player, event.getStack());
        if (data == null)
            return;
        if (!data.isTextured())
            return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();

        int x = event.getX();
        int y = event.getY();
        int width = event.getWidth();
        int height = event.getHeight();

        ResourceLocation texture = data.decorations().withSuffix("_decorations.png").withPrefix("textures/gui/tooltip/");

        RenderSystem.setShaderTexture(0, texture);

        RenderHelper.mc().getTextureManager().getTexture(texture).bind();

        int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        if (texWidth == 0 || texHeight == 0)
            return;

        int patternWidth = 160;
        int patternHeight = 64;

        int cornerWidth = 32;
        int cornerHeight = 32;

        int middleWidth = 96;
        int middleHeight = cornerHeight;

        poseStack.pushPose();

        RenderSystem.enableBlend();

        poseStack.translate(0, 0, 410.0);

        AnimationData animationData = AnimationData.construct(texHeight, patternHeight, 2);
        if (data.isInterpolated())
            renderInterpolatedDecorations(
                    animationData,
                    player,
                    guiGraphics,
                    texture,
                    x,
                    y,
                    width,
                    height,
                    patternWidth,
                    patternHeight,
                    cornerWidth,
                    cornerHeight,
                    middleWidth,
                    middleHeight,
                    texWidth,
                    texHeight);
        else
            renderNormalDecorations(
                    animationData,
                    player,
                    guiGraphics,
                    texture,
                    x,
                    y,
                    width,
                    height,
                    patternWidth,
                    patternHeight,
                    cornerWidth,
                    cornerHeight,
                    middleWidth,
                    middleHeight,
                    texWidth,
                    texHeight);

        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }

    private static void renderInterpolatedDecorations(AnimationData animationData,
                                                      LocalPlayer player,
                                                      GuiGraphics guiGraphics,
                                                      ResourceLocation texture,
                                                      int x,
                                                      int y,
                                                      int width,
                                                      int height,
                                                      int patternWidth,
                                                      int patternHeight,
                                                      int cornerWidth,
                                                      int cornerHeight,
                                                      int middleWidth,
                                                      int middleHeight,
                                                      int texWidth,
                                                      int texHeight)
    {
        int currentFrame = animationData.getFrameByTime(player.tickCount).getFirst();
        int nextFrame = (currentFrame + 1) % animationData.totalLength();

        int offset = patternHeight * currentFrame;

        float progress = (player.tickCount % 2) / 2f;

        int baseColor = MathHelper.ColorHelper.colorFromFloat(1f, 1f, 1f, 1f);
        int intColor = MathHelper.ColorHelper.colorFromFloat(progress, 1f, 1f, 1f);

        RenderHelper.blit(
                guiGraphics,
                texture,
                x - cornerWidth / 2 - 3,
                y - cornerHeight / 2 - 3,
                0, offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                baseColor);
        RenderHelper.blit(
                guiGraphics,
                texture,
                x + width - cornerWidth / 2 + 3,
                y - cornerHeight / 2 - 3,
                patternWidth - cornerWidth, offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                baseColor);

        RenderHelper.blit(
                guiGraphics,
                texture,
                x - cornerWidth / 2 - 3,
                y + height - cornerHeight / 2 + 3,
                0,
                (patternHeight - cornerHeight) + offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                baseColor);
       
        RenderHelper.blit(
                guiGraphics,
                texture,
                x + width - cornerWidth / 2 + 3,
                y + height - cornerHeight / 2 + 3,
                patternWidth - cornerWidth,
                (patternHeight - cornerHeight) + offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                baseColor);

        RenderHelper.blit(
                guiGraphics,
                texture,
                x + (width - middleWidth) / 2,
                y - middleHeight + 1,
                cornerWidth, offset,
                middleWidth, middleHeight,
                0,
                middleWidth, middleHeight,
                texWidth, texHeight,
                baseColor);
        RenderHelper.blit(
                guiGraphics,
                texture,
                x + (width - middleWidth) / 2,
                y + height - 1, cornerWidth,
                middleHeight + offset,
                middleWidth, middleHeight,
                0,
                middleWidth, middleHeight,
                texWidth, texHeight,
                baseColor);

        offset = patternHeight * nextFrame;

        RenderHelper.blit(
                guiGraphics,
                texture,
                x - cornerWidth / 2 - 3,
                y - cornerHeight / 2 - 3,
                0, offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                intColor);
        RenderHelper.blit(
                guiGraphics,
                texture,
                x + width - cornerWidth / 2 + 3,
                y - cornerHeight / 2 - 3,
                patternWidth - cornerWidth, offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                intColor);

        RenderHelper.blit(
                guiGraphics,
                texture,
                x - cornerWidth / 2 - 3,
                y + height - cornerHeight / 2 + 3,
                0, (patternHeight - cornerHeight) + offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                intColor);
        RenderHelper.blit(
                guiGraphics,
                texture,
                x + width - cornerWidth / 2 + 3,
                y + height - cornerHeight / 2 + 3,
                patternWidth - cornerWidth,
                (patternHeight - cornerHeight) + offset,
                cornerWidth, cornerHeight,
                0,
                cornerWidth, cornerHeight,
                texWidth, texHeight,
                intColor);

        RenderHelper.blit(
                guiGraphics,
                texture,
                x + (width - middleWidth) / 2,
                y - middleHeight + 1,
                cornerWidth, offset,
                middleWidth, middleHeight, 0,
                middleWidth, middleHeight,
                texWidth, texHeight,
                intColor);
        RenderHelper.blit(
                guiGraphics,
                texture,
                x + (width - middleWidth) / 2,
                y + height - 1, cornerWidth,
                middleHeight + offset,
                middleWidth, middleHeight,
                0,
                middleWidth, middleHeight,
                texWidth, texHeight,
                intColor);
    }

    private static void renderNormalDecorations(AnimationData animationData,
                                                LocalPlayer player,
                                                GuiGraphics guiGraphics,
                                                ResourceLocation texture,
                                                int x,
                                                int y,
                                                int width,
                                                int height,
                                                int patternWidth,
                                                int patternHeight,
                                                int cornerWidth,
                                                int cornerHeight,
                                                int middleWidth,
                                                int middleHeight,
                                                int texWidth,
                                                int texHeight)
    {
        int frame = animationData.getFrameByTime(player.tickCount).getFirst();
        int offset = patternHeight * frame;

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        
        guiGraphics.blit(texture, x - cornerWidth / 2 - 3, y - cornerHeight / 2 - 3, 0, offset, cornerWidth, cornerHeight, texWidth, texHeight);
        guiGraphics.blit(texture, x + width - cornerWidth / 2 + 3, y - cornerHeight / 2 - 3, patternWidth - cornerWidth, offset, cornerWidth, cornerHeight, texWidth, texHeight);

        guiGraphics.blit(texture, x - cornerWidth / 2 - 3, y + height - cornerHeight / 2 + 3, 0, (patternHeight - cornerHeight) + offset, cornerWidth, cornerHeight, texWidth, texHeight);
        guiGraphics.blit(texture, x + width - cornerWidth / 2 + 3, y + height - cornerHeight / 2 + 3, patternWidth - cornerWidth, (patternHeight - cornerHeight) + offset, cornerWidth, cornerHeight, texWidth, texHeight);

        guiGraphics.blit(texture, x + (width - middleWidth) / 2, y - middleHeight + 1, cornerWidth, offset, middleWidth, middleHeight, texWidth, texHeight);
        guiGraphics.blit(texture, x + (width - middleWidth) / 2, y + height - 1, cornerWidth, middleHeight + offset, middleWidth, middleHeight, texWidth, texHeight);
    }

    private static void tooltipBackgroundEvent(final RenderTooltipEvent.Color event)
    {
        TooltipData data = getTooltipData(RenderHelper.clientPlayer(), event.getItemStack());
        if (data == null)
            return;
        
        event.setBackground(MathHelper.ColorHelper.color(240, 50, 29, 27));
        event.setBorderStart(MathHelper.ColorHelper.color(80, 255, 0, 5));
        event.setBorderEnd(MathHelper.ColorHelper.color(80, 127, 0, 2));
        //if (data.isTextured())
            //event.setTexture(data.background());
    }

    private static TooltipData getTooltipData(LocalPlayer player, ItemStack stack)
    {
        if (overrideTooltipData != null)
            return overrideTooltipData;
        if (!(stack.getItem() instanceof ICustomTooltip tooltip))
            return null;

        StyleData style = tooltip.getStyle();
        if (!style.isCustom())
            return null;
        return style.tooltip().apply(player, stack);
    }
}
