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
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
}
