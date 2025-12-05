/**
 * @author ArcAnc
 * Created at: 01.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.integration.jei;

import com.arcanc.metamorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.metamorphosis.content.gui.MetaSlot;
import com.arcanc.metamorphosis.content.gui.component.info.ProgressInfoArea;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.recipe.ChamberRecipe;
import com.arcanc.metamorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.helper.MathHelper;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
import com.mojang.math.Axis;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.List;

public class ChamberRecipeCategory implements IRecipeCategory<ChamberRecipe>
{
    public static final IRecipeType<ChamberRecipe> RECIPE_TYPE = IRecipeType.create(Registration.RecipeReg.CHAMBER_RECIPE.getRecipeType().getId(), ChamberRecipe.class);
    private final IDrawable icon;

    private int progress;
    private int maxTime;

    private final ProgressInfoArea progressArrow = new ProgressInfoArea(new Rect2i(48, -69, 20, 20), new ProgressInfoArea.ProgressInfo(() -> this.progress, () -> this.maxTime));

    private final int maxPerRow = 6;
    private final int startY = 10;

    public ChamberRecipeCategory(@NotNull IGuiHelper guiHelper)
    {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Registration.BlockReg.MULTIBLOCK_CHAMBER));
    }

    @Override
    public @NotNull IRecipeType<ChamberRecipe> getRecipeType()
    {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle()
    {
        return Component.translatable(Database.Integration.JeiInfo.CHAMBER_RECIPE_NAME);
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ChamberRecipe recipe, @NotNull IFocusGroup focuses)
    {
        builder.setShapeless();

        List<IngredientWithSize> inputs = recipe.input();

        int rows = Mth.ceil(inputs.size() / (MultiblockChamber.MAX_SLOT_AMOUNT / 2f));

        int drawn = 0;
        for (int row = 0; row < rows; row++)
        {
            int squaresInRow = Math.min(this.maxPerRow, inputs.size() - drawn);
            int rowWidth = squaresInRow * 19;

            int startX = (this.getWidth() - rowWidth) / 2;
            for (int col = 0; col < squaresInRow; col++)
            {
                int x = startX + col * 19;
                int y = this.startY + row * 19;
                IngredientWithSize ingredient = inputs.get(row * this.maxPerRow + col);
                addInputSlot(builder, x, y, ingredient);
            }
            drawn += squaresInRow;
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 70).add(recipe.result().copy());
    }

    private void addInputSlot (@NotNull IRecipeLayoutBuilder builder, int x, int y, IngredientWithSize ingredient)
    {
        IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
        slotBuilder.add(MetaIngredientTypes.INGREDIENT_WITH_SIZE_TYPE, ingredient);
    }

    @Override
    public void draw(@NotNull ChamberRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        List<IngredientWithSize> inputs = recipe.input();

        int rows = Mth.ceil(inputs.size() / (MultiblockChamber.MAX_SLOT_AMOUNT / 2f));

        int drawn = 0;
        for (int row = 0; row < rows; row++)
        {
            int squaresInRow = Math.min(this.maxPerRow, inputs.size() - drawn);
            int rowWidth = squaresInRow * 19;

            int startX = (this.getWidth() - rowWidth) / 2;
            for (int col = 0; col < squaresInRow; col++)
            {
                int x = startX + col * 19;
                int y = this.startY + row * 19;
                guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, x - 1, y - 1, 18, 18);
                guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, x - 1, y - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));
            }
            drawn += squaresInRow;
        }

        guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 49, 69, 18, 18);
        guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 49, 69, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

        this.maxTime = recipe.getResources().time();
        this.progress = (int)(System.currentTimeMillis() / 10 % maxTime);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(90));
        this.progressArrow.render(guiGraphics, (int)mouseX, (int)mouseY, 0.33f);
        guiGraphics.pose().popPose();

        guiGraphics.blit(RenderType :: guiTextured, Database.GUI.Textures.JEI.TIME, 5, 77, 0, 0, 8, 8, 16, 16,16, 16);
        guiGraphics.drawString(RenderHelper.mc().font, Component.literal(Integer.toString(recipe.getResources().time())), 15, 77, 0, false);
    }

    @Override
    public int getWidth()
    {
        return 120;
    }

    @Override
    public int getHeight()
    {
        return 90;
    }
}
