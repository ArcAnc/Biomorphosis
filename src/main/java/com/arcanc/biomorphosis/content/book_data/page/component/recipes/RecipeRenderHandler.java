/**
 * @author ArcAnc
 * Created at: 06.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page.component.recipes;

import com.arcanc.biomorphosis.api.book.recipe.RecipeRenderer;
import com.arcanc.biomorphosis.content.event.CustomEvents;
import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeRenderHandler
{
    public static void registerRenderers()
    {
        NeoForge.EVENT_BUS.addListener(RecipeRenderHandler :: addRecipeRenderers);
    }

    private static void addRecipeRenderers(@NotNull final CustomEvents.AddRecipeRenderer event)
    {
        event.addRenderer(RecipeType.CRAFTING, new CraftingRecipeRenderer());
    }

    private static class CraftingRecipeRenderer implements RecipeRenderer
    {

        private CraftingRecipeRenderer()
        {
        }

        @Override
        public void renderRecipe(@NotNull Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            if (recipe.getType().equals(RecipeType.CRAFTING))
            {
                CraftingRecipe craftingRecipe = (CraftingRecipe)recipe;
                if (craftingRecipe instanceof ShapedRecipe shapedRecipe)
                    renderShapedRecipe(shapedRecipe, xPos, yPos, guiGraphics, mouseX, mouseY, partialTicks);
                else if (craftingRecipe instanceof ShapelessRecipe shapelessRecipe)
                    renderShapelessRecipe(shapelessRecipe, xPos, yPos, guiGraphics, mouseX, mouseY, partialTicks);
            }
        }

        private void renderShapelessRecipe(ShapelessRecipe recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            final List<Vec2> positions = new ArrayList<>();
            Vec2 imagePos = Vec2.ZERO;
            final int partW = getWidth() / 5;
            final int partH = getHeight() / 3;
            for (int yy = 0; yy < 3; yy++)
            {
                for (int xx = 0; xx < 3; xx++)
                {
                    positions.add(xx+ (yy * 3), new Vec2(xPos + (xx *partW), yPos + (yy * partH)));
                }
            }

            imagePos = new Vec2(xPos + 4 * partW - 10, yPos + partH);
            positions.add(new Vec2(xPos + 5 * partW, yPos + partH));

            ItemStack highlighted = ItemStack.EMPTY;
            ItemStack stack = ItemStack.EMPTY;

            List<Ingredient> ingr = recipe.placementInfo().ingredients();
            Minecraft mc = RenderHelper.mc();
            ItemStack result = recipe.assemble(CraftingInput.EMPTY, mc.level.registryAccess());
            for (int q = 0 ; q < ingr.size(); q++)
            {
                Ingredient in = ingr.get(q);
                stack = RenderHelper.getStackAtCurrentTime(in);

                guiGraphics.pose().pushPose();

                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)positions.get(q).x - 1, (int)positions.get(q).y - 1, 217, 0, 18, 18, 256, 256);
                guiGraphics.renderItem(stack, (int)positions.get(q).x, (int)positions.get(q).y);
                guiGraphics.pose().popPose();
                if (mouseX >= (int)positions.get(q).x && mouseY >= (int)positions.get(q).y && mouseX <= (int)positions.get(q).x + 16 && mouseY <= positions.get(q).y + 16)
                {
                    highlighted = stack;
                }
            }

            guiGraphics.renderItem(result, (int)positions.get(9).x, (int)positions.get(9).y);

            guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)imagePos.x, (int)imagePos.y - 18, 234, 69, 22, 15, 256, 256);

            guiGraphics.pose().pushPose();
            guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)imagePos.x, (int)imagePos.y, 234, 53, 22, 15, 256, 256);

            guiGraphics.pose().popPose();

            if (!highlighted.isEmpty())
                guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
            else if (mouseX >= imagePos.x && mouseY >= imagePos.y - 18 && mouseX <= imagePos.x + 22 && mouseY <= imagePos.y - 18 + 15)
                guiGraphics.renderTooltip(mc.font, List.of(Component.translatable(Database.GUI.GuideBook.Pages.Components.SHAPELESS)), Optional.empty(), mouseX, mouseY);

        }

        private void renderShapedRecipe(@NotNull ShapedRecipe recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            final List<Vec2> positions = new ArrayList<>();
            Vec2 imagePos = Vec2.ZERO;
            final int partW = getWidth() / 5;
            final int partH = getHeight() / 3;
            for (int yy = 0; yy < 3; yy++)
            {
                for (int xx = 0; xx < 3; xx++)
                {
                    positions.add(xx+ (yy * 3), new Vec2(xPos + (xx *partW), yPos + (yy * partH)));
                }
            }

            imagePos = new Vec2(xPos + 4 * partW - 10, yPos + partH);
            positions.add(new Vec2(xPos + 5 * partW, yPos + partH));

            ItemStack highlighted = ItemStack.EMPTY;
            ItemStack stack = ItemStack.EMPTY;

            List<SizedIngredient> ingr = recipe.pattern.ingredients().
                    stream().
                    map(ingredient -> ingredient.orElse(null)).
                    map(ingredient -> ingredient != null ? new SizedIngredient(ingredient, 1) : null).
                    collect(Collectors.toList());
            Minecraft mc = RenderHelper.mc();
            ItemStack result = recipe.assemble(CraftingInput.EMPTY, mc.level.registryAccess());
            ingr.add(9, new SizedIngredient(Ingredient.of(result.getItem()), result.getCount()));
            for (int q = 0 ; q < ingr.size(); q++)
            {
                SizedIngredient in = ingr.get(q);
                stack = in != null ? RenderHelper.getStackAtCurrentTime(in) : ItemStack.EMPTY;

                guiGraphics.pose().pushPose();

                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)positions.get(q).x - 1, (int)positions.get(q).y - 1, 217, 0, 18, 18, 256, 256);
                guiGraphics.renderItem(stack, (int)positions.get(q).x, (int)positions.get(q).y);
                guiGraphics.pose().popPose();
                if (mouseX >= (int)positions.get(q).x && mouseY >= (int)positions.get(q).y && mouseX <= (int)positions.get(q).x + 16 && mouseY <= positions.get(q).y + 16)
                    highlighted = stack;
            }


            guiGraphics.pose().pushPose();
            guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)imagePos.x, (int)imagePos.y, 234, 53, 22, 15, 256, 256);

            guiGraphics.pose().popPose();

            if (!highlighted.isEmpty())
                guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
            else if (mouseX >= imagePos.x && mouseY >= imagePos.y && mouseX <= imagePos.x + 22 && mouseY <= imagePos.y + 15)
                guiGraphics.renderTooltip(mc.font, List.of(Component.translatable(Database.GUI.GuideBook.Pages.Components.SHAPED)), Optional.empty(), mouseX, mouseY);
        }

        @Override
        public int getHeight()
        {
            return 55;
        }

        @Override
        public int getWidth()
        {
            return 90;
        }
    }
}
