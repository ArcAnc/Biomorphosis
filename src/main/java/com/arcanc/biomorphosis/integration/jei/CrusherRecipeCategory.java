/**
 * @author ArcAnc
 * Created at: 05.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jei;

import com.arcanc.biomorphosis.content.gui.BioSlot;
import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.CrusherRecipe;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
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

public class CrusherRecipeCategory implements IRecipeCategory<CrusherRecipe>
{
    public static final IRecipeType<CrusherRecipe> RECIPE_TYPE = IRecipeType.create(Registration.RecipeReg.CRUSHER_RECIPE.getRecipeType().getId(), CrusherRecipe.class);

    private final IDrawable icon;

    public CrusherRecipeCategory(@NotNull IGuiHelper guiHelper)
    {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Registration.BlockReg.CRUSHER));
    }

    @Override
    public @NotNull IRecipeType<CrusherRecipe> getRecipeType()
    {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle()
    {
        return Component.translatable(Database.Integration.JeiInfo.CRUSHER_RECIPE_NAME);
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CrusherRecipe recipe, @NotNull IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 42, 35).add(BioIngredientTypes.INGREDIENT_WITH_SIZE_TYPE, recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 10, 80).add(recipe.result());


        int biomassAmount = recipe.getResources().time() * recipe.getResources().biomass().perSecond();
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10).
                setFluidRenderer(biomassAmount,false,20,10).
                add(NeoForgeTypes.FLUID_STACK, new FluidStack(Registration.FluidReg.BIOMASS.still(), biomassAmount)).
                addRichTooltipCallback((recipeSlotView, tooltip) ->
                        tooltip.add(Component.literal("Required: " + recipe.getResources().biomass().required())));

        recipe.getResources().adrenaline().ifPresent(info ->
        {
            int amount = info.perSecond() * recipe.getResources().time();

            builder.addSlot(RecipeIngredientRole.INPUT, 40, 10).
                    setFluidRenderer(amount, false, 20, 10).
                    add(NeoForgeTypes.FLUID_STACK, new FluidStack(Registration.FluidReg.ADRENALINE.still(), amount)).
                    addRichTooltipCallback((recipeSlotView, tooltip) ->
                            tooltip.add(Component.literal("Required: " + recipe.getResources().biomass().required())));
        });

        recipe.getResources().lymph().ifPresent(info ->
        {
            int amount = info.perSecond() * recipe.getResources().time();

            builder.addSlot(RecipeIngredientRole.INPUT, 70, 10).
                    setFluidRenderer(amount, false, 20, 10).
                    add(NeoForgeTypes.FLUID_STACK, new FluidStack(Registration.FluidReg.LYMPH.still(), amount)).
                    addRichTooltipCallback((recipeSlotView, tooltip) ->
                            tooltip.add(Component.literal("Required: " + recipe.getResources().biomass().required())));
        });

        if (recipe.secondaryResults().isEmpty())
            return;
        for (int q = 0; q < recipe.secondaryResults().size(); q++)
        {
            StackWithChance stack = recipe.secondaryResults().get(q);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 60 + q % 2 * 19, 80 + q / 2 * 30).add(BioIngredientTypes.STACK_WITH_CHANCE_TYPE, stack);
        }
    }

    @Override
    public void draw(@NotNull CrusherRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        Minecraft mc = RenderHelper.mc();
        Font font = mc.font;
        boolean shift = GuideScreen.hasShiftDown();

        guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.FRAME, 42 - 1, 35 - 1, 18, 18);
        guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.MASK, 42 - 1, 35 - 1, 18, 18, MathHelper.ColorHelper.color(BioSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));

        guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.FRAME, 10 - 1, 80 - 1, 18, 18);
        guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.MASK, 10 - 1, 80 - 1, 18, 18, MathHelper.ColorHelper.color(BioSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));


        if (!recipe.secondaryResults().isEmpty())
            for (int q = 0; q < recipe.secondaryResults().size(); q++)
            {
                guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.FRAME, 60 + q % 2 * 19 - 1, 80 + q / 2 * 30 - 1, 18, 18);
                guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.MASK, 60 + q % 2 * 19 - 1, 80 + q / 2 * 30 - 1, 18, 18, MathHelper.ColorHelper.color(BioSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));
            }

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

            recipe.getResources().lymph().ifPresent(info ->
            {
                guiGraphics.drawString(font, info.required() ?
                                Component.translatable(Database.Integration.JeiInfo.REQUIRED) :
                                Component.translatable(Database.Integration.JeiInfo.OPTIONAL),
                        140, 5, 0, false);
                guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.WITH_LYMPH, info.modifier()),
                        140, 45, 0, false);
                guiGraphics.drawString(font, Component.translatable(Database.Integration.JeiInfo.PER_TICK, info.perSecond()),
                        140, 55, 0, false);
            });
            guiGraphics.pose().popPose();
        }

        guiGraphics.blit(RenderType :: guiTextured, Database.GUI.Textures.JEI.SECONDARY_OUTPUT, 48, 60, 0, 0, 16, 16, 16, 16, 16, 16);

        guiGraphics.blit(RenderType:: guiTextured, Database.GUI.Textures.JEI.TIME, 5, 55, 0, 0, 8, 8, 16, 16,16, 16);
        guiGraphics.drawString(RenderHelper.mc().font, Component.literal(Integer.toString(recipe.getResources().time())), 15, 55, 0, false);
    }

    @Override
    public int getWidth()
    {
        return 100;
    }

    @Override
    public int getHeight()
    {
        return 110;
    }
}
