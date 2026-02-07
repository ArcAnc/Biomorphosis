/**
 * @author ArcAnc
 * Created at: 07.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.icon;

import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class FluidIcon implements Icon
{
    private final FluidStack stack;

    public FluidIcon(FluidStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int xPos, int yPos, int width, int height)
    {
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(this.stack.getFluid());

        ResourceLocation stillTex = renderProps.getStillTexture();
        TextureAtlasSprite still = RenderHelper.getTexture(stillTex);

        graphics.blitSprite(RenderType :: guiTextured, still, xPos, yPos, width, height, renderProps.getTintColor());
    }
}
