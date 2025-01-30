/**
 * @author ArcAnc
 * Created at: 26.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen;

import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GuideScreen extends Screen
{
    public static final ResourceLocation TEXT = Database.GUI.getTexturePath("gui/book/book");

    private int xSize = 292;
    private int ySize = 180;
    public int guiLeft;
    public int guiTop;

    public GuideScreen()
    {
        super(Component.empty());
    }

    @Override
    protected void init()
    {
        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.blit(RenderType :: guiTextured, TEXT, guiLeft + xSize / 2, guiTop, 20, 1, 146, 180, 256, 256);

        guiGraphics.pose().scale(-1f, -1f, 1f);

        guiGraphics.blit(RenderType :: guiTextured, TEXT, -guiLeft - xSize / 2, - guiTop - ySize, 20, 1, 146, 180, 256, 256);

        guiGraphics.pose().scale(-1f, -1f, 1f);
        guiGraphics.pose().popPose();
    }
}
