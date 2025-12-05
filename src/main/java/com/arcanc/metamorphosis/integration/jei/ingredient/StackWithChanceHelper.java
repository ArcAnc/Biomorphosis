/**
 * @author ArcAnc
 * Created at: 05.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.integration.jei.ingredient;

import com.arcanc.metamorphosis.integration.jei.MetaIngredientTypes;
import com.arcanc.metamorphosis.util.helper.ItemHelper;
import com.arcanc.metamorphosis.util.inventory.item.StackWithChance;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StackWithChanceHelper implements IIngredientHelper<StackWithChance>
{
    @Override
    public @NotNull IIngredientType<StackWithChance> getIngredientType()
    {
        return MetaIngredientTypes.STACK_WITH_CHANCE_TYPE;
    }

    @Override
    public @NotNull String getDisplayName(@NotNull StackWithChance ingredient)
    {
        return ingredient.stack().getDisplayName().getString();
    }

    @Override
    public @NotNull String getUid(@NotNull StackWithChance ingredient, @NotNull UidContext context)
    {
        return ItemHelper.getRegistryName(ingredient.stack().getItem()).toString()
                + "#" + ingredient.stack().getDamageValue()
                + "@" + ingredient.chance();
    }

    @Override
    public @NotNull ResourceLocation getResourceLocation(@NotNull StackWithChance ingredient)
    {
        return ItemHelper.getRegistryName(ingredient.stack().getItem());
    }

    @Override
    public @NotNull StackWithChance copyIngredient(@NotNull StackWithChance ingredient)
    {
        return new StackWithChance(ingredient.stack().copy(), ingredient.chance());
    }

    @Override
    public @NotNull String getErrorInfo(@Nullable StackWithChance ingredient)
    {
        return "";
    }
}
