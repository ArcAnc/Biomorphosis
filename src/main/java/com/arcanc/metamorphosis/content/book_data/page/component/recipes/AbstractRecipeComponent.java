/**
 * @author ArcAnc
 * Created at: 02.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.book_data.page.component.recipes;

import com.arcanc.metamorphosis.api.book.recipe.RecipeRenderer;
import com.arcanc.metamorphosis.content.book_data.page.component.AbstractPageComponent;
import com.arcanc.metamorphosis.content.network.NetworkEngine;
import com.arcanc.metamorphosis.content.network.packets.C2SRecipeRequest;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AbstractRecipeComponent extends AbstractPageComponent
{
    private final ResourceLocation location;
    private Recipe<?> recipe;

    private AbstractRecipeComponent(ResourceLocation recipeType, ResourceLocation location)
    {
        super(0, 0, 0, 0, Component.empty());
        this.location = location;
        if (recipeType.equals(ResourceLocation.withDefaultNamespace("crafting_shaped")) || recipeType.equals(ResourceLocation.withDefaultNamespace("crafting_shapeless")))
            recipeType = ResourceLocation.withDefaultNamespace("crafting");
        ResourceLocation finalResourceLocation = recipeType;
        ClientPacketListener connection = RenderHelper.mc().getConnection();
        if (connection != null)
        {
            RecipeType<?> type = connection.registryAccess().lookup(Registries.RECIPE_TYPE).
                    map(registry -> registry.get(finalResourceLocation).orElseThrow()).
                    orElseThrow().
                    value();
            RecipeRenderCache.getRenderer(type).ifPresentOrElse(renderer ->
                            this.setSize(renderer.getWidth(), renderer.getHeight()),
                    () -> this.setSize(35, 11));
        }
        this.recipe = RecipeCache.getRecipe(location).orElse(null);
        if (recipe == null)
            fetchNewRecipe(location);
    }

    private void fetchNewRecipe(ResourceLocation location)
    {
        NetworkEngine.sendToServer(new C2SRecipeRequest(location));
    }

    public static @NotNull AbstractRecipeComponent createRecipeComponent(ResourceLocation recipeType, ResourceLocation location)
    {
       return new AbstractRecipeComponent(recipeType, location);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        if (this.recipe == null)
        {
            this.recipe = RecipeCache.getRecipe(this.location).orElse(null);
            if (this.recipe == null)
            {
                renderLoading(guiGraphics);
                return;
            }
        }
        renderRecipe(guiGraphics, mouseX, mouseY, partialTicks);
    }

    protected void renderRecipe(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        RecipeRenderCache.getRenderer(this.recipe.getType()).ifPresent(renderer ->
                renderer.renderRecipe(this.recipe, getX(), getY(), guiGraphics, mouseX, mouseY, partialTicks));
    }

    private void renderLoading(@NotNull GuiGraphics guiGraphics)
    {
        guiGraphics.drawString(RenderHelper.mc().font, Component.literal("Loading Recipe..."), this.getX(), this.getY(), Color.black.getRGB(), false);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {

    }

    public static final class RecipeCache
    {
        private final static Map<ResourceLocation, Recipe<?>> CACHED_RECIPES = new HashMap<>();

        public static void storeRecipe(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe)
        {
            CACHED_RECIPES.putIfAbsent(id, recipe);
        }

        public static @NotNull Optional<Recipe<?>> getRecipe(ResourceLocation id)
        {
            return Optional.ofNullable(CACHED_RECIPES.get(id));
        }
    }

    public static final class RecipeRenderCache
    {
        private final static Map<RecipeType<?>, RecipeRenderer> CACHED_RENDERERS = new HashMap<>();

        public static void addNewRenderer(RecipeType<?> type, RecipeRenderer renderer)
        {
            CACHED_RENDERERS.putIfAbsent(type, renderer);
        }

        public static @NotNull Optional<RecipeRenderer> getRenderer(RecipeType<?> type)
        {
            return Optional.ofNullable(CACHED_RENDERERS.get(type));
        }
    }
}
