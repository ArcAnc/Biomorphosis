/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Function;

public class RenderHelper
{
    public static @NotNull Minecraft mc()
    {
        return Minecraft.getInstance();
    }

    public static LocalPlayer clientPlayer()
    {
        return mc().player;
    }

    public static @NotNull TextureAtlas textureMap()
    {
        return mc().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
    }

    public static @NotNull TextureAtlasSprite getTexture(ResourceLocation location)
    {
        return textureMap().getSprite(location);
    }

    public static @NotNull ItemRenderer renderItem()
    {
        return mc().getItemRenderer();
    }

    public static void openGuideScreen()
    {
        Minecraft.getInstance().setScreen(new GuideScreen());
    }

    public static ItemStack getStackAtCurrentTime(@NotNull SizedIngredient ingredient)
    {
        return getStackAtCurrentTime(ingredient.ingredient().getValues().stream().
                map(itemHolder -> new ItemStack(itemHolder, ingredient.count())).toList());
    }

    public static ItemStack getStackAtCurrentTime(@NotNull Ingredient ingredient)
    {
        return getStackAtCurrentTime(ingredient.getValues().stream().map(ItemStack::new).toList());
    }

    public static ItemStack getStackAtCurrentTime(@NotNull List<ItemStack> items)
    {
        int perm = (int)(System.currentTimeMillis() / 1000 % items.size());
        return items.get(perm);
    }

    public static int animateArrow (int ticks)
    {
        return (int) (System.currentTimeMillis() / ticks % 22);
    }

    public static void blit(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Function<ResourceLocation, RenderType> renderTypeGetter,
            @NotNull ResourceLocation atlasLocation,
            float x,
            float y,
            float uOffset,
            float vOffset,
            float width,
            float height,
            float uWidth,
            float vHeight,
            int textureWidth,
            int textureHeight
    )
    {
        RenderHelper.blit(guiGraphics, renderTypeGetter, atlasLocation, x, y, uOffset, vOffset, width, height, uWidth, vHeight, textureWidth, textureHeight,-1);
    }

    public static void blit(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Function<ResourceLocation, RenderType> renderTypeGetter,
            @NotNull ResourceLocation atlasLocation,
            float x,
            float y,
            float uOffset,
            float vOffset,
            float width,
            float height,
            float uWidth,
            float vHeight,
            int textureWidth,
            int textureHeight,
            int color
    ) {
        RenderHelper.blit(
                guiGraphics,
                renderTypeGetter,
                atlasLocation,
                x,
                x + width,
                y,
                y + height,
                uOffset / (float)textureWidth,
                (uOffset + uWidth) / (float)textureWidth,
                vOffset / (float)textureHeight,
                (vOffset + vHeight) / (float)textureHeight,
                color
        );
    }

    public static void blit (@NotNull GuiGraphics guiGraphics,
                             @NotNull Function<ResourceLocation, RenderType> renderTypeGetter,
                             @NotNull ResourceLocation atlasLocation,
                             float x1,
                             float x2,
                             float y1,
                             float y2,
                             float minU,
                             float maxU,
                             float minV,
                             float maxV,
                             int color)
    {
        RenderType rendertype = renderTypeGetter.apply(atlasLocation);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        VertexConsumer vertexconsumer = guiGraphics.bufferSource.getBuffer(rendertype);
        vertexconsumer.addVertex(matrix4f, x1, y1, 0.0F).setUv(minU, minV).setColor(color);
        vertexconsumer.addVertex(matrix4f, x1, y2, 0.0F).setUv(minU, maxV).setColor(color);
        vertexconsumer.addVertex(matrix4f, x2, y2, 0.0F).setUv(maxU, maxV).setColor(color);
        vertexconsumer.addVertex(matrix4f, x2, y1, 0.0F).setUv(maxU, minV).setColor(color);
    }
}
