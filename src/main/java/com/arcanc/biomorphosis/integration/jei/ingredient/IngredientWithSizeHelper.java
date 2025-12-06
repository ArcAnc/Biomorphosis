/**
 * @author ArcAnc
 * Created at: 02.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jei.ingredient;

import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.integration.jei.BioIngredientTypes;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IngredientWithSizeHelper implements IIngredientHelper<IngredientWithSize>
{
    @Override
    public @NotNull IIngredientType<IngredientWithSize> getIngredientType()
    {
        return BioIngredientTypes.INGREDIENT_WITH_SIZE_TYPE;
    }

    @Override
    public @NotNull String getDisplayName(@NotNull IngredientWithSize ingredient)
    {
        return RenderHelper.getStackAtCurrentTime(ingredient).getDisplayName().getString();
    }

    @Override
    public @NotNull Object getUid(@NotNull IngredientWithSize ingredient, @NotNull UidContext context)
    {
        return RenderHelper.getStackAtCurrentTime(ingredient);
    }

    @Override
    public @NotNull ResourceLocation getResourceLocation(@NotNull IngredientWithSize ingredient)
    {
        return Database.rl("ingredient_with_size");
    }

    @Override
    public @NotNull IngredientWithSize copyIngredient(@NotNull IngredientWithSize ingredient)
    {
        return new IngredientWithSize(Ingredient.of(ingredient.ingredient().getValues()), ingredient.amount());
    }

    @Override
    public @NotNull String getErrorInfo(@Nullable IngredientWithSize ingredient) {
        return "";
    }
}
