/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.info;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.IFluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FluidInfoArea extends InfoArea
{
    private final IFluidTank tank;
    private final ResourceLocation overlayTexture;

    public FluidInfoArea(IFluidTank tank, Rect2i area)
    {
        this(tank, area, TANK);
    }

    public FluidInfoArea(IFluidTank tank, Rect2i area, ResourceLocation overlayTexture)
    {
        super(area);
        this.tank = tank;
        this.overlayTexture = overlayTexture;
    }

    @Override
    protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
    {
        FluidStack stack = this.tank.getFluid();
        if (stack.isEmpty())
            return;
        Fluid fluid = stack.getFluid();
        FluidType type = fluid.getFluidType();
        Minecraft mc = RenderHelper.mc();
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(fluid);
        Style style = Style.EMPTY.withColor(renderProps.getTintColor());

        tooltip.add(Component.translatable(stack.getDescriptionId()).withStyle(style));
        tooltip.add(Component.empty());
        if (!Screen.hasShiftDown())
            tooltip.add(Component.translatable(Database.GUI.InfoArea.FluidArea.Tooltip.NORMAL_SHORT_TOOLTIP,
                    Component.literal(Integer.toString(stack.getAmount())).
                            withStyle(style)).
                    withStyle(ChatFormatting.GRAY));
        else
            tooltip.add(Component.translatable(Database.GUI.InfoArea.FluidArea.Tooltip.NORMAL_EXTENDED_TOOLTIP,
                    Component.literal(Integer.toString(stack.getAmount())).
                            withStyle(style),
                    Component.literal(Integer.toString(this.tank.getCapacity())).
                            withStyle(style)).
                    withStyle(ChatFormatting.GRAY));


        if (mc.options.advancedItemTooltips)
        {
            tooltip.add(Component.empty());
            if (!Screen.hasShiftDown())
                tooltip.add(Component.translatable(Database.GUI.HOLD_SHIFT).withStyle(ChatFormatting.DARK_GRAY));
            else
            {
                tooltip.add(Component.translatable(Database.GUI.InfoArea.FluidArea.Tooltip.ADVANCED_TOOLTIP_DENSITY,
                                Component.literal(Integer.toString(type.getDensity())).
                                        withStyle(style)).
                        withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable(Database.GUI.InfoArea.FluidArea.Tooltip.ADVANCED_TOOLTIP_TEMPERATURE,
                                Component.literal(Integer.toString(type.getTemperature())).
                                        withStyle(style)).
                        withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable(Database.GUI.InfoArea.FluidArea.Tooltip.ADVANCED_TOOLTIP_VISCOSITY,
                                Component.literal(Integer.toString(type.getViscosity())).
                                        withStyle(style)).
                        withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        FluidStack stack = this.tank.getFluid();
        float capacity = this.tank.getCapacity();
        Minecraft mc = RenderHelper.mc();

        float fluidPercent = (float)stack.getAmount() / capacity;

        float f = this.area.getY() + (this.area.getHeight() * (1 - fluidPercent));
        float f1 = this.area.getHeight() * fluidPercent;

        guiGraphics.pose().pushPose();

        guiGraphics.blit(
                this.overlayTexture,
                this.area.getX(),
                this.area.getY(),
                0, 0,
                this.area.getWidth(),
                this.area.getHeight(),
                18, 42,
                64, 64);

        if (!stack.isEmpty())
        {
            IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(stack.getFluid());
            int color = renderProps.getTintColor();
            ResourceLocation texture = renderProps.getStillTexture();
            TextureAtlasSprite still = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
            
            float red = MathHelper.ColorHelper.redFloat(color);
            float green = MathHelper.ColorHelper.greenFloat(color);
            float blue = MathHelper.ColorHelper.blueFloat(color);
            float alpha = MathHelper.ColorHelper.alphaFloat(color);
            
            guiGraphics.blit(this.area.getX(), (int)f, 0, this.area.getWidth(), (int)f1, still, red, green, blue, alpha);
        }

        guiGraphics.blit(
                this.overlayTexture,
                this.area.getX(), this.area.getY(),
                40, 0,
                this.area.getWidth(), this.area.getHeight(),
                18, 42,
                64, 64);
        guiGraphics.pose().popPose();
    }
}
