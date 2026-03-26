/**
 * @author ArcAnc
 * Created at: 05.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jei.ingredient;

import com.arcanc.biomorphosis.integration.jei.BioIngredientTypes;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class StackWithChanceHelper implements IIngredientHelper<StackWithChance>
{
    @Override
    public IIngredientType<StackWithChance> getIngredientType()
    {
        return BioIngredientTypes.STACK_WITH_CHANCE_TYPE;
    }

    @Override
    public String getDisplayName(StackWithChance ingredient)
    {
        return ingredient.stack().getDisplayName().getString();
    }
    
    //FIXME: idk is this right way to make string for those method
    @Override
    public String getUniqueId(StackWithChance ingredient, UidContext context)
    {
        return "StackWithChance";
    }
    
    @Override
    public String getUid(StackWithChance ingredient, UidContext context)
    {
        return ItemHelper.getRegistryName(ingredient.stack().getItem()).toString()
                + "#" + ingredient.stack().getDamageValue()
                + "@" + ingredient.chance();
    }

    @Override
    public ResourceLocation getResourceLocation(StackWithChance ingredient)
    {
        return ItemHelper.getRegistryName(ingredient.stack().getItem());
    }

    @Override
    public StackWithChance copyIngredient(StackWithChance ingredient)
    {
        return new StackWithChance(ingredient.stack().copy(), ingredient.chance());
    }

    @Override
    public String getErrorInfo(@Nullable StackWithChance ingredient)
    {
        return "";
    }
}
