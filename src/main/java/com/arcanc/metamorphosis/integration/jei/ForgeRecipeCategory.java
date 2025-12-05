/**
 * @author ArcAnc
 * Created at: 06.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.integration.jei;

import com.arcanc.metamorphosis.content.gui.MetaSlot;
import com.arcanc.metamorphosis.content.gui.screen.GuideScreen;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.recipe.ForgeRecipe;
import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.helper.MathHelper;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
import com.mojang.math.Axis;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class ForgeRecipeCategory implements IRecipeCategory<ForgeRecipe>
{
    public static final IRecipeType<ForgeRecipe> RECIPE_TYPE = IRecipeType.create(Registration.RecipeReg.FORGE_RECIPE.getRecipeType().getId(), ForgeRecipe.class);
    private final IDrawable icon;
    private final IDrawable arrow;

    public ForgeRecipeCategory(@NotNull IGuiHelper guiHelper)
    {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Registration.BlockReg.FORGE));
        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
    }

    @Override
    public @NotNull IRecipeType<ForgeRecipe> getRecipeType()
    {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle()
    {
        return Component.translatable(Database.Integration.JeiInfo.FORGE_RECIPE_NAME);
    }

    @Override
    public int getWidth()
    {
        return 100;
    }

    @Override
    public int getHeight()
    {
        return 100;
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ForgeRecipe recipe, @NotNull IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 42, 35).add(MetaIngredientTypes.INGREDIENT_WITH_SIZE_TYPE, recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 42, 80).add(recipe.result());

        int biomassAmount = (int) (recipe.getResources().time() * recipe.getResources().biomass().perSecond());
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10).
                setFluidRenderer(biomassAmount,false,20,10).
                add(NeoForgeTypes.FLUID_STACK, new FluidStack(Registration.FluidReg.BIOMASS.still(), biomassAmount));

        recipe.getResources().adrenaline().ifPresent(info ->
        {
            int amount = (int) (info.perSecond() * recipe.getResources().time());

            builder.addSlot(RecipeIngredientRole.INPUT, 40, 10).
                    setFluidRenderer(amount, false, 20, 10).
                    add(NeoForgeTypes.FLUID_STACK, new FluidStack(Registration.FluidReg.ADRENALINE.still(), amount));
        });

        recipe.getResources().acid().ifPresent(info ->
        {
            int amount = (int) (info.perSecond() * recipe.getResources().time());

            builder.addSlot(RecipeIngredientRole.INPUT, 70, 10).
                    setFluidRenderer(amount, false, 20, 10).
                    add(NeoForgeTypes.FLUID_STACK, new FluidStack(Registration.FluidReg.ACID.still(), amount));
        });
    }

    @Override
    public void draw(@NotNull ForgeRecipe recipe,
                     @NotNull IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics,
                     double mouseX,
                     double mouseY)
    {
        Minecraft mc = RenderHelper.mc();
        Font font = mc.font;
        boolean shift = GuideScreen.hasShiftDown();

        guiGraphics.blitSprite(RenderType:: guiTextured, MetaSlot.FRAME, 42 - 1, 35 - 1, 18, 18);
        guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 42 - 1, 35 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

        guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.FRAME, 42 - 1, 80 - 1, 18, 18);
        guiGraphics.blitSprite(RenderType :: guiTextured, MetaSlot.MASK, 42 - 1, 80 - 1, 18, 18, MathHelper.ColorHelper.color(MetaSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

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
            guiGraphics.drawString(font, recipe.getResources().biomass().required() ?
                            Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                            Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                    20, 5, 0, false);
            guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, recipe.getResources().biomass().perSecond()),
                    10, 50, 0, false);

            recipe.getResources().adrenaline().ifPresent(info ->
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

            recipe.getResources().acid().ifPresent(info ->
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

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(58, 55, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(90));
        this.arrow.draw(guiGraphics);
        guiGraphics.pose().popPose();

        guiGraphics.blit(RenderType:: guiTextured, Database.GUI.Textures.JEI.TIME, 5, 55, 0, 0, 8, 8, 16, 16,16, 16);
        guiGraphics.drawString(RenderHelper.mc().font, Component.literal(Integer.toString(recipe.getResources().time())), 15, 55, 0, false);
    }
}
