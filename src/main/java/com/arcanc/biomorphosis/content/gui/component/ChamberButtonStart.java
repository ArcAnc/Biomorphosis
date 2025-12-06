/**
 * @author ArcAnc
 * Created at: 03.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;

import com.arcanc.biomorphosis.content.gui.component.animation.AnimatedTexture;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.function.Supplier;

public class ChamberButtonStart extends Button
{
    private static final ResourceLocation TEXTURE = Database.rl("textures/gui/elements/chamber/button.png");
    private static final ResourceLocation OVERLAY = Database.rl("textures/gui/elements/chamber/button_overlay.png");

    private final AnimatedTexture texture;

    private final Supplier<Integer> currentTime;
    private final Supplier<Integer> maxTime;

    private boolean clicked = false;

    public ChamberButtonStart(int x, int y, int width, int height, OnPress onPress, @NotNull ClickLimitation limitation)
    {
        super(x, y, width, height, Component.empty(), onPress, messageSupplier -> Component.empty());
        this.texture = new AnimatedTexture(new Vector2i(16, 128), new Vector2i(16, 16), 8, TEXTURE);
        this.currentTime = limitation.currentTime();
        this.maxTime = limitation.maxTime();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.texture.render(guiGraphics, new Rect2i(this.getX(), this.getY(), this.getWidth(), this.getHeight()));

        this.clicked = this.currentTime.get() < this.maxTime.get();

        if (this.clicked)
        {
            setTooltip(Tooltip.create(Component.translatable(Database.GUI.ChamberButton.PROCESS)));

            guiGraphics.blit(RenderType :: guiTextured, OVERLAY,
                    this.getX(), this.getY(),
                    0,0,
                    this.getWidth(), this.getHeight(),
                    16, 16,
                    16, 16);
        }
        else
            setTooltip(Tooltip.create(Component.translatable(Database.GUI.ChamberButton.START)));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (!this.active || !this.visible)
            return false;
        else if (CommonInputs.selected(keyCode) && !this.clicked)
        {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.active && this.visible)
        {
            if (this.isValidClickButton(button))
            {
                boolean flag = this.isMouseOver(mouseX, mouseY);
                if (flag && !this.clicked)
                {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY, button);
                    return true;
                }
            }
        }
        return false;
    }

    public record ClickLimitation(Supplier<Integer> currentTime, Supplier<Integer> maxTime)
    {}
}
