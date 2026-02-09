/**
 * @author ArcAnc
 * Created at: 02.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.info;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public class ChamberProgressArea extends ProgressInfoArea
{

    private static final ResourceLocation TEXTURE = Database.rl("textures/gui/elements/chamber/progress.png");

    public ChamberProgressArea(Rect2i area, ProgressInfo info)
    {
        super(area, info, TEXTURE);
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
                40, 12,
                64, 16);

        float percent = (float)current / max;
        int color = 0xFF00FF00;

        if (current <= 0)
        {
            percent = 0f;
            color = 0xFFFF0000;
        }

        Vector2f scale = new Vector2f(this.area.getWidth() / 40f, this.area.getHeight() / 12f);

        float length = (1 - percent) * 30;

        float xPosition = this.area.getX() + (2 + length) * scale.x();
        float yPosition = this.area.getY() + 3 * scale.y();

        RenderHelper.blit(
                guiGraphics,
                this.progressTexture,
                xPosition,
                yPosition,
                41, 3,
                scale.x() * 6, scale.y() * 6,
                0,
                6, 6,
                64, 16,
                color);
    }
}
