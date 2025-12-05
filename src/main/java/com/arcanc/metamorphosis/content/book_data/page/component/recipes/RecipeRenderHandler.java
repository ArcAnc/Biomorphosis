/**
 * @author ArcAnc
 * Created at: 06.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.book_data.page.component.recipes;

import com.arcanc.metamorphosis.api.book.recipe.RecipeRenderer;
import com.arcanc.metamorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.metamorphosis.content.event.CustomEvents;
import com.arcanc.metamorphosis.content.gui.MetaSlot;
import com.arcanc.metamorphosis.content.gui.component.icon.FluidIcon;
import com.arcanc.metamorphosis.content.gui.component.info.ProgressInfoArea;
import com.arcanc.metamorphosis.content.gui.screen.GuideScreen;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.recipe.*;
import com.arcanc.metamorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.helper.MathHelper;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
import com.arcanc.metamorphosis.util.inventory.item.StackWithChance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeRenderHandler
{
    /*FIXME: переписать на кастомные компоненты. Потому что заебало вручную отрисовывать все текстуры предметов*/

    public static void registerRenderers()
    {
        NeoForge.EVENT_BUS.addListener(RecipeRenderHandler :: addRecipeRenderers);
    }

    private static void addRecipeRenderers(@NotNull final CustomEvents.AddRecipeRenderer event)
    {
        event.addRenderer(RecipeType.CRAFTING, new CraftingRecipeRenderer());
        CookingRecipeRenderer cookingRecipeRenderer = new CookingRecipeRenderer();
        event.addRenderer(RecipeType.BLASTING, cookingRecipeRenderer);
        event.addRenderer(RecipeType.CAMPFIRE_COOKING, cookingRecipeRenderer);
        event.addRenderer(RecipeType.SMELTING, cookingRecipeRenderer);
        event.addRenderer(RecipeType.SMOKING, cookingRecipeRenderer);
        event.addRenderer(Registration.RecipeReg.CHAMBER_RECIPE.getRecipeType().get(), new ChamberRecipeRenderer());
        event.addRenderer(Registration.RecipeReg.CRUSHER_RECIPE.getRecipeType().get(), new CrusherRecipeRenderer());
        event.addRenderer(Registration.RecipeReg.FORGE_RECIPE.getRecipeType().get(), new ForgeRecipeRenderer());
        event.addRenderer(Registration.RecipeReg.STOMACH_RECIPE.getRecipeType().get(), new StomachRecipeRenderer());
		event.addRenderer(Registration.RecipeReg.SQUEEZER_RECIPE.getRecipeType().get(), new SqueezerRecipeRenderer());
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
                    highlighted = stack;
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

            List<IngredientWithSize> ingr = recipe.pattern.ingredients().
                    stream().
                    map(ingredient -> ingredient.orElse(null)).
                    map(ingredient -> ingredient != null ? new IngredientWithSize(ingredient, 1) : null).
                    collect(Collectors.toList());
			if (ingr.size() < 8)
				for (int lastPart = ingr.size(); lastPart < 9; lastPart++)
					ingr.add(lastPart, null);
            Minecraft mc = RenderHelper.mc();
            ItemStack result = recipe.assemble(CraftingInput.EMPTY, mc.level.registryAccess());
            ingr.add(9, new IngredientWithSize(Ingredient.of(result.getItem()), result.getCount()));
            for (int q = 0 ; q < ingr.size(); q++)
            {
                IngredientWithSize in = ingr.get(q);
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

    private static class CookingRecipeRenderer implements RecipeRenderer
    {

        @Override
        public void renderRecipe(Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            if (!(recipe instanceof AbstractCookingRecipe cookingRecipe))
                return;

            final List<Vec2> positions = new ArrayList<>();

            Vec2 arrowPos = Vec2.ZERO;
            Vec2 expPos = Vec2.ZERO;

            positions.add(new Vec2(xPos, yPos));
            positions.add(new Vec2(xPos + 22 + 4 + 18, yPos));
            arrowPos = new Vec2(xPos + 18 + 2, yPos );
            expPos = new Vec2(xPos + 18 + 4 + 22 + 4, yPos + 2);

            ItemStack highlighted = ItemStack.EMPTY;
            ItemStack stack = ItemStack.EMPTY;

            NonNullList<IngredientWithSize> ingr = NonNullList.create();
            ingr.add(new IngredientWithSize(cookingRecipe.input(), 1));
            Minecraft mc = RenderHelper.mc();
            ItemStack result = cookingRecipe.assemble(new SingleRecipeInput(ItemStack.EMPTY), mc.level.registryAccess());
            ingr.add(new IngredientWithSize(Ingredient.of(result.getItem()), result.getCount()));
            int time = cookingRecipe.cookingTime();
            float exp = cookingRecipe.experience();


            for (int q = 0; q < ingr.size(); q++)
            {
                guiGraphics.pose().pushPose();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.enableDepthTest();
                stack = RenderHelper.getStackAtCurrentTime(ingr.get(q));

                guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)positions.get(q).x, (int)positions.get(q).y, 217, 0, 18, 18, 256, 256);
                guiGraphics.renderItem(stack, (int)positions.get(q).x, (int)positions.get(q).y);
                if (mouseX >= (int)positions.get(q).x && mouseY >= (int)positions.get(q).y && mouseX <= (int)positions.get(q).x + 16 && mouseY <= positions.get(q).y + 16)
                    highlighted = stack;
                guiGraphics.pose().popPose();
            }
            guiGraphics.pose().pushPose();
            //exp
            guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)expPos.x + 18, (int)expPos.y + 2, 234, 86, 11, 11, 256, 256);
            guiGraphics.pose().popPose();

            guiGraphics.pose().pushPose();
            //palochka
            guiGraphics.blit(RenderType :: guiTextured, GuideScreen.TEXT, (int)arrowPos.x, (int)arrowPos.y, 234, 53, RenderHelper.animateArrow(time), 15, 256, 256);
            guiGraphics.pose().popPose();

            if (!highlighted.isEmpty())
                guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
            else if (mouseX >= arrowPos.x && mouseY >= arrowPos.y && mouseX <= arrowPos.x + 14 && mouseY <= arrowPos.y + 14)
                guiGraphics.renderTooltip(mc.font, List.of(Component.translatable(Database.GUI.GuideBook.Pages.Components.TICKS, time)), Optional.empty(), mouseX, mouseY);
            else if (mouseX >= expPos.x + 18 && mouseY >= expPos.y + 2 && mouseX <= expPos.x + 18 + 11 && mouseY <= expPos.y + 2 +11)
                guiGraphics.renderTooltip(mc.font, List.of(Component.translatable(Database.GUI.GuideBook.Pages.Components.EXP, exp)), Optional.empty(), mouseX, mouseY);
        }

        @Override
        public int getHeight()
        {
            return 17;
        }

        @Override
        public int getWidth()
        {
            return 48;
        }
    }

    private static class ChamberRecipeRenderer implements RecipeRenderer
    {
        private int progress;
        private int maxTime;
        private final ProgressInfoArea progressArrow = new ProgressInfoArea(new Rect2i(48, -68, 20, 20), new ProgressInfoArea.ProgressInfo(() -> this.progress, () -> this.maxTime));

        @Override
        public void renderRecipe(Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            if (!(recipe instanceof ChamberRecipe chamberRecipe))
                return;
            Minecraft mc = RenderHelper.mc();
            ItemStack highlighted = ItemStack.EMPTY;

            List<IngredientWithSize> inputs = chamberRecipe.input();
            ItemStack result = chamberRecipe.result();

            final int maxPerRow = 6;
            final int startY = 10;
            int rows = Mth.ceil(inputs.size() / (MultiblockChamber.MAX_SLOT_AMOUNT / 2f));

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xPos, yPos, 0);

            int drawn = 0;
            for (int row = 0; row < rows; row++)
            {
                int squaresInRow = Math.min(maxPerRow, inputs.size() - drawn);
                int rowWidth = squaresInRow * 19;

                int startX = (this.getWidth() - rowWidth) / 2;
                for (int col = 0; col < squaresInRow; col++)
                {
                    int x = startX + col * 19;
                    int y = startY + row * 19;
                    IngredientWithSize ingredient = inputs.get(row * maxPerRow + col);
                    ItemStack stack = RenderHelper.getStackAtCurrentTime(ingredient);

                    guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, x - 1, y - 1, 18, 18);
                    guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, x - 1, y - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

                    guiGraphics.renderItem(stack, x, y);
                    if (mouseX >= xPos + x && mouseY >= yPos + y && mouseX <= xPos + x + 16 && mouseY <= yPos + y + 16)
                        highlighted = stack;
                }
                drawn += squaresInRow;
            }

            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 50, 74, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 50, 74, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

            guiGraphics.renderItem(result, 51, 75);
            if (mouseX >= xPos + 50 && mouseY >= yPos + 74 && mouseX <= xPos + 50 + 16 && mouseY <= yPos + 74 + 16)
                highlighted = result;

            this.maxTime = chamberRecipe.getResources().time();
            this.progress = (int)(System.currentTimeMillis() / 10 % maxTime);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(90));
            this.progressArrow.render(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.pose().popPose();

            guiGraphics.blit(RenderType :: guiTextured, Database.GUI.Textures.JEI.TIME, 10, 55, 0, 0, 8, 8, 16, 16,16, 16);
            guiGraphics.drawString(mc.font, Component.literal(Integer.toString(chamberRecipe.getResources().time())), 20, 55, 0, false);
            guiGraphics.pose().popPose();

            if (!highlighted.isEmpty())
                guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
        }

        @Override
        public int getHeight()
        {
            return 95;
        }

        @Override
        public int getWidth()
        {
            return 120;
        }
    }

    private static class CrusherRecipeRenderer implements RecipeRenderer
    {

        @Override
        public void renderRecipe(Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            if (!(recipe instanceof CrusherRecipe crusherRecipe))
                return;

            Minecraft mc = RenderHelper.mc();
            Font font = mc.font;
            ItemStack highlighted = ItemStack.EMPTY;
            boolean shift = GuideScreen.hasShiftDown();

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xPos, yPos, 0);

            IngredientWithSize ingredient = crusherRecipe.input();
            ItemStack stack = RenderHelper.getStackAtCurrentTime(ingredient);

            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 42 - 1, 35 - 1, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 42 - 1, 35 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));
            guiGraphics.renderItem(stack, 42, 35);
            if (mouseX >= xPos + 42 && mouseY >= yPos + 35 && mouseX <= xPos + 42 + 16 && mouseY <= yPos + 35 + 16)
                highlighted = stack.copy();

            stack = crusherRecipe.result();
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 10 - 1, 80 - 1, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 10 - 1, 80 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));
            guiGraphics.renderItem(stack, 10, 80);
            if (mouseX >= xPos + 10 && mouseY >= yPos + 80 && mouseX <= xPos + 10 + 16 && mouseY <= yPos + 80 + 16)
                highlighted = stack.copy();

            if (crusherRecipe.secondaryResults().isEmpty())
                return;
            for (int q = 0; q < crusherRecipe.secondaryResults().size(); q++)
            {
                StackWithChance secondaryResult = crusherRecipe.secondaryResults().get(q);
                stack = secondaryResult.stack();
                int x = 60 + q % 2 * 19;
                int y = 80 + q / 2 * 30;
                guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, x - 1, y - 1, 18, 18);
                guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, x - 1, y - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.drawString(mc.font, Component.literal(String.format("%.1f", secondaryResult.chance() * 100) + "%"), x - 2, y + 20, -1, false);
                if (mouseX >= xPos + x && mouseY >= yPos + y && mouseX <= xPos + x + 16 && mouseY <= yPos + y + 16)
                    highlighted = stack.copy();
            }

            FluidIcon biomass = new FluidIcon(new FluidStack(Registration.FluidReg.BIOMASS.still(), (int) (crusherRecipe.getResources().biomass().perSecond() * crusherRecipe.getResources().time())));
            biomass.render(guiGraphics, 7, 10, 20, 10);

            crusherRecipe.getResources().adrenaline().ifPresent(info ->
            {
                FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ADRENALINE.still(),(int) (info.perSecond() * crusherRecipe.getResources().time())));
                icon.render(guiGraphics, 40, 10, 20, 10);
            });

            crusherRecipe.getResources().acid().ifPresent(info ->
            {
                FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ACID.still(), (int) (info.perSecond() * crusherRecipe.getResources().time())));
                icon.render(guiGraphics, 70, 10, 20, 10);
            });

            if (!shift)
            {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
                guiGraphics.drawString(font, Component.translatable(Database.GUI.HOLD_SHIFT), 5, 1, 0, false);
                guiGraphics.pose().popPose();
            }
            else
            {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                guiGraphics.drawString(font, crusherRecipe.getResources().biomass().required() ?
                                Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                        20, 5, 0, false);
                guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, crusherRecipe.getResources().biomass().perSecond()),
                        10, 50, 0, false);

                crusherRecipe.getResources().adrenaline().ifPresent(info ->
                {
                    guiGraphics.drawString(font, info.required() ?
                                    Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                    Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                            80, 5, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ADRENALINE, info.modifier()),
                            80, 45, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
                            80, 55, 0, false);
                });

                crusherRecipe.getResources().acid().ifPresent(info ->
                {
                    guiGraphics.drawString(font, info.required() ?
                                    Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                    Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                            140, 5, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ACID, info.modifier()),
                            140, 45, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
                            140, 55, 0, false);
                });
                guiGraphics.pose().popPose();
            }

            guiGraphics.blit(RenderType :: guiTextured, Database.GUI.Textures.JEI.SECONDARY_OUTPUT, 48, 60, 0, 0, 16, 16, 16, 16, 16, 16);

            guiGraphics.blit(RenderType:: guiTextured, Database.GUI.Textures.JEI.TIME, 5, 62, 0, 0, 8, 8, 16, 16,16, 16);
            guiGraphics.drawString(RenderHelper.mc().font, Component.literal(Integer.toString(crusherRecipe.getResources().time())), 15, 62, 0, false);

            guiGraphics.pose().popPose();

            if (!highlighted.isEmpty())
                guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
        }

        @Override
        public int getHeight()
        {
            return 110;
        }

        @Override
        public int getWidth()
        {
            return 100;
        }
    }

    private static class ForgeRecipeRenderer implements RecipeRenderer
    {
        private int progress;
        private int maxTime;
        private final ProgressInfoArea progressArrow = new ProgressInfoArea(new Rect2i(55, -61, 20, 20), new ProgressInfoArea.ProgressInfo(() -> this.progress, () -> this.maxTime));

        @Override
        public void renderRecipe(Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            if (!(recipe instanceof ForgeRecipe forgeRecipe))
                return;
            Minecraft mc = RenderHelper.mc();
            Font font = mc.font;
            ItemStack highlighted = ItemStack.EMPTY;
            boolean shift = GuideScreen.hasShiftDown();

            IngredientWithSize input = forgeRecipe.input();
            ItemStack result = forgeRecipe.result();

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xPos, yPos, 0);

            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 42 - 1, 35 - 1, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 42 - 1, 35 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

            ItemStack stack = RenderHelper.getStackAtCurrentTime(input);
            guiGraphics.renderItem(stack, 42, 35);
            if (mouseX >= xPos + 42 && mouseY >= yPos + 35 && mouseX <= xPos + 42 + 16 && mouseY <= yPos + 35 + 16)
                highlighted = stack.copy();

            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 42 - 1, 80 - 1, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 42 - 1, 80 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

            stack = result;
            guiGraphics.renderItem(stack, 42, 80);
            if (mouseX >= xPos + 42 && mouseY >= yPos + 80 && mouseX <= xPos + 42 + 16 && mouseY <= yPos + 80 + 16)
                highlighted = stack.copy();

            FluidIcon biomass = new FluidIcon(new FluidStack(Registration.FluidReg.BIOMASS.still(), (int) (forgeRecipe.getResources().biomass().perSecond() * forgeRecipe.getResources().time())));
            biomass.render(guiGraphics, 7, 10, 20, 10);

            forgeRecipe.getResources().adrenaline().ifPresent(info ->
            {
                FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ADRENALINE.still(), (int) (info.perSecond() * forgeRecipe.getResources().time())));
                icon.render(guiGraphics, 40, 10, 20, 10);
            });

            forgeRecipe.getResources().acid().ifPresent(info ->
            {
                FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ACID.still(), (int) (info.perSecond() * forgeRecipe.getResources().time())));
                icon.render(guiGraphics, 70, 10, 20, 10);
            });

            if (!shift)
            {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
                guiGraphics.drawString(font, Component.translatable(Database.GUI.HOLD_SHIFT), 5, 1, 0, false);
                guiGraphics.pose().popPose();
            }
            else
            {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                guiGraphics.drawString(font, forgeRecipe.getResources().biomass().required() ?
                                Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                        20, 5, 0, false);
                guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, forgeRecipe.getResources().biomass().perSecond()),
                        10, 50, 0, false);

                forgeRecipe.getResources().adrenaline().ifPresent(info ->
                {
                    guiGraphics.drawString(font, info.required() ?
                                    Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                    Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                            80, 5, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ADRENALINE, info.modifier()),
                            80, 45, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
                            80, 55, 0, false);
                });

                forgeRecipe.getResources().acid().ifPresent(info ->
                {
                    guiGraphics.drawString(font, info.required() ?
                                    Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                    Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                            140, 5, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ACID, info.modifier()),
                            140, 45, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
                            140, 55, 0, false);
                });
                guiGraphics.pose().popPose();
            }

            this.maxTime = forgeRecipe.getResources().time();
            this.progress = (int)(System.currentTimeMillis() / 10 % maxTime);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(90));
            this.progressArrow.render(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.pose().popPose();

            guiGraphics.blit(RenderType:: guiTextured, Database.GUI.Textures.JEI.TIME, -5, 65, 0, 0, 8, 8, 16, 16,16, 16);
            guiGraphics.drawString(RenderHelper.mc().font, Component.literal(Integer.toString(forgeRecipe.getResources().time())), 5, 65, 0, false);

            guiGraphics.pose().popPose();

            if (!highlighted.isEmpty())
                guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
        }

        @Override
        public int getHeight()
        {
            return 100;
        }

        @Override
        public int getWidth()
        {
            return 100;
        }
    }

    private static class StomachRecipeRenderer implements RecipeRenderer
    {
        private int progress;
        private int maxTime;
        private final ProgressInfoArea progressArrow = new ProgressInfoArea(new Rect2i(55, -61, 20, 20), new ProgressInfoArea.ProgressInfo(() -> this.progress, () -> this.maxTime));

        @Override
        public void renderRecipe(Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            if (!(recipe instanceof StomachRecipe stomachRecipe))
                return;
            Minecraft mc = RenderHelper.mc();
            Font font = mc.font;
            ItemStack highlighted = ItemStack.EMPTY;
            boolean shift = GuideScreen.hasShiftDown();

            IngredientWithSize input = stomachRecipe.input();
            ItemStack stack = RenderHelper.getStackAtCurrentTime(input);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xPos, yPos, 0);
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 42 - 1, 35 - 1, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 42 - 1, 35 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

            guiGraphics.renderItem(stack, 42, 35);
            if (mouseX >= xPos + 42 && mouseY >= yPos + 35 && mouseX <= xPos + 42 + 16 && mouseY <= yPos + 35 + 16)
                highlighted = stack;

            FluidIcon biomass = new FluidIcon(new FluidStack(Registration.FluidReg.BIOMASS.still(), (int) (stomachRecipe.getResources().biomass().perSecond() * stomachRecipe.getResources().time())));
            biomass.render(guiGraphics, 7, 10, 20, 10);

            stomachRecipe.getResources().adrenaline().ifPresent(info ->
            {
                FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ADRENALINE.still(), (int) (info.perSecond() * stomachRecipe.getResources().time())));
                icon.render(guiGraphics, 40, 10, 20, 10);
            });

            stomachRecipe.getResources().acid().ifPresent(info ->
            {
                FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ACID.still(), (int) (info.perSecond() * stomachRecipe.getResources().time())));
                icon.render(guiGraphics, 70, 10, 20, 10);
            });

            if (!shift)
            {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
                guiGraphics.drawString(font, Component.translatable(Database.GUI.HOLD_SHIFT), 5, 1, 0, false);
                guiGraphics.pose().popPose();
            }
            else
            {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                guiGraphics.drawString(font, stomachRecipe.getResources().biomass().required() ?
                                Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                        20, 5, 0, false);
                guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, stomachRecipe.getResources().biomass().perSecond()),
                        10, 50, 0, false);

                stomachRecipe.getResources().adrenaline().ifPresent(info ->
                {
                    guiGraphics.drawString(font, info.required() ?
                                    Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                    Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                            80, 5, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ADRENALINE, info.modifier()),
                            80, 45, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
                            80, 55, 0, false);
                });

                stomachRecipe.getResources().acid().ifPresent(info ->
                {
                    guiGraphics.drawString(font, info.required() ?
                                    Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                    Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                            140, 5, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ACID, info.modifier()),
                            140, 45, 0, false);
                    guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
                            140, 55, 0, false);
                });
                guiGraphics.pose().popPose();
            }

            FluidIcon result = new FluidIcon(stomachRecipe.result());
            result.render(guiGraphics, 42, 80, 16, 16);

            this.maxTime = stomachRecipe.getResources().time();
            this.progress = (int)(System.currentTimeMillis() / 10 % maxTime);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(90));
            this.progressArrow.render(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.pose().popPose();

            guiGraphics.blit(RenderType:: guiTextured, Database.GUI.Textures.JEI.TIME, -5, 65, 0, 0, 8, 8, 16, 16,16, 16);
            guiGraphics.drawString(RenderHelper.mc().font, Component.literal(Integer.toString(stomachRecipe.getResources().time())), 5, 65, 0, false);

            guiGraphics.pose().popPose();

            if (!highlighted.isEmpty())
                guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
        }

        @Override
        public int getHeight()
        {
            return 100;
        }

        @Override
        public int getWidth()
        {
            return 100;
        }
    }
	
	private static class SqueezerRecipeRenderer implements RecipeRenderer
	{
		private int progress;
		private int maxTime;
		private final ProgressInfoArea progressArrow = new ProgressInfoArea(new Rect2i(55, -61, 20, 20), new ProgressInfoArea.ProgressInfo(() -> this.progress, () -> this.maxTime));
		
		@Override
		public void renderRecipe(Recipe<?> recipe, int xPos, int yPos, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
		{
			if (!(recipe instanceof SqueezerRecipe squeezerRecipe))
				return;
			Minecraft mc = RenderHelper.mc();
			Font font = mc.font;
			ItemStack highlighted = ItemStack.EMPTY;
			boolean shift = GuideScreen.hasShiftDown();
			
			IngredientWithSize input = squeezerRecipe.input();
			ItemStack stack = RenderHelper.getStackAtCurrentTime(input);
			
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(xPos, yPos, 0);
			guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 42 - 1, 35 - 1, 18, 18);
			guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 42 - 1, 35 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));
			
			guiGraphics.renderItem(stack, 42, 35);
			if (mouseX >= xPos + 42 && mouseY >= yPos + 35 && mouseX <= xPos + 42 + 16 && mouseY <= yPos + 35 + 16)
				highlighted = stack;
			
			squeezerRecipe.getResources().adrenaline().ifPresent(info ->
			{
				FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ADRENALINE.still(), (int) (info.perSecond() * squeezerRecipe.getResources().time())));
				icon.render(guiGraphics, 40, 10, 20, 10);
			});
			
			squeezerRecipe.getResources().acid().ifPresent(info ->
			{
				FluidIcon icon = new FluidIcon(new FluidStack(Registration.FluidReg.ACID.still(), (int) (info.perSecond() * squeezerRecipe.getResources().time())));
				icon.render(guiGraphics, 70, 10, 20, 10);
			});
			
			if (!shift)
			{
				guiGraphics.pose().pushPose();
				guiGraphics.pose().scale(0.6f, 0.6f, 0.6f);
				guiGraphics.drawString(font, Component.translatable(Database.GUI.HOLD_SHIFT), 5, 1, 0, false);
				guiGraphics.pose().popPose();
			}
			else
			{
				guiGraphics.pose().pushPose();
				guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
				
				squeezerRecipe.getResources().adrenaline().ifPresent(info ->
				{
					guiGraphics.drawString(font, info.required() ?
									Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
									Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
							80, 5, 0, false);
					guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ADRENALINE, info.modifier()),
							80, 45, 0, false);
					guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
							80, 55, 0, false);
				});
				
				squeezerRecipe.getResources().acid().ifPresent(info ->
				{
					guiGraphics.drawString(font, info.required() ?
									Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
									Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
							140, 5, 0, false);
					guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_ACID, info.modifier()),
							140, 45, 0, false);
					guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
							140, 55, 0, false);
				});
				guiGraphics.pose().popPose();
			}
			
			FluidIcon result = new FluidIcon(squeezerRecipe.result());
			result.render(guiGraphics, 42, 80, 16, 16);
			
			this.maxTime = squeezerRecipe.getResources().time();
			this.progress = (int)(System.currentTimeMillis() / 10 % maxTime);
			
			guiGraphics.pose().pushPose();
			guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(90));
			this.progressArrow.render(guiGraphics, mouseX, mouseY, partialTicks);
			guiGraphics.pose().popPose();
			
			guiGraphics.blit(RenderType:: guiTextured, Database.GUI.Textures.JEI.TIME, -5, 65, 0, 0, 8, 8, 16, 16,16, 16);
			guiGraphics.drawString(RenderHelper.mc().font, Component.literal(Integer.toString(squeezerRecipe.getResources().time())), 5, 65, 0, false);
			
			guiGraphics.pose().popPose();
			
			if (!highlighted.isEmpty())
				guiGraphics.renderTooltip(mc.font, Screen.getTooltipFromItem(mc, highlighted), highlighted.getTooltipImage(), mouseX, mouseY);
		}
		
		@Override
		public int getHeight()
		{
			return 100;
		}
		
		@Override
		public int getWidth()
		{
			return 100;
		}
	}
}
