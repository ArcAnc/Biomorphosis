/**
 * @author ArcAnc
 * Created at: 03.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.animation;

import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class AnimatedTexture
{
    private final AnimationData data;
    private final ResourceLocation texture;
    private final int textureWidth;
    private final int textureHeight;
    private final int patternWidth;
    private final int patternHeight;
    private final int frameTime;

    public AnimatedTexture (@NotNull Vector2i textureSize, @NotNull Vector2i patternSize, int frameTime, ResourceLocation texture)
    {
        this.data = AnimationData.construct(textureSize.y(), patternSize.y(), frameTime);
        this.texture = texture;
        this.textureWidth = textureSize.x();
        this.textureHeight = textureSize.y();
        this.patternWidth = textureSize.x();
        this.patternHeight = patternSize.y();
        this.frameTime = frameTime;
    }

    public void render (@NotNull GuiGraphics guiGraphics, @NotNull Rect2i area)
    {
        Minecraft mc = RenderHelper.mc();

        int currentFrame = this.data.getFrameByTime(mc.level.getGameTime()).getFirst();
        int nextFrame = (currentFrame + 1) % this.data.totalLength();
        int offset = currentFrame * this.patternHeight;

        float progress = (mc.level.getGameTime() % this.frameTime) / (float)this.frameTime;

        int baseColor = ARGB.colorFromFloat(1f, 1f, 1f, 1f);
        int intColor = ARGB.colorFromFloat(progress, 1f, 1f, 1f);

        guiGraphics.blit(RenderType:: guiTextured, this.texture,
                area.getX(), area.getY(),
                0, offset,
                area.getWidth(), area.getHeight(),
                this.patternWidth, this.patternHeight,
                this.textureWidth, this.textureHeight,
                baseColor);

        offset = nextFrame * 16;

        guiGraphics.blit(RenderType :: guiTextured, this.texture,
                area.getX(), area.getY(),
                0, offset,
                area.getWidth(), area.getHeight(),
                this.patternWidth, this.patternHeight,
                this.textureWidth, this.textureHeight,
                intColor);
    }
}
