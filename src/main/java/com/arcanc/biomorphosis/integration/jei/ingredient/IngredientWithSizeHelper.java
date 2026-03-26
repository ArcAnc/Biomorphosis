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
import org.jetbrains.annotations.Nullable;

public class IngredientWithSizeHelper implements IIngredientHelper<IngredientWithSize>
{
    @Override
    public IIngredientType<IngredientWithSize> getIngredientType()
    {
        return BioIngredientTypes.INGREDIENT_WITH_SIZE_TYPE;
    }

    @Override
    public String getDisplayName(IngredientWithSize ingredient)
    {
        return RenderHelper.getStackAtCurrentTime(ingredient).getDisplayName().getString();
    }
    
    //FIXME: idk is this right way to make string for those method
    @Override
    public String getUniqueId(IngredientWithSize ingredient, UidContext context)
    {
        return "IngredientWithSize";
    }
    
    @Override
    public Object getUid(IngredientWithSize ingredient, UidContext context)
    {
        return RenderHelper.getStackAtCurrentTime(ingredient);
    }

    @Override
    public ResourceLocation getResourceLocation(IngredientWithSize ingredient)
    {
        return Database.rl("ingredient_with_size");
    }

    @Override
    public IngredientWithSize copyIngredient(IngredientWithSize ingredient)
    {
        return new IngredientWithSize(ingredient.ingredient(), ingredient.amount());
    }

    @Override
    public String getErrorInfo(@Nullable IngredientWithSize ingredient) {
        return "";
    }
}
