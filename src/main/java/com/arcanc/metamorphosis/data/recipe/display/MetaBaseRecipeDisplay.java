/**
 * @author ArcAnc
 * Created at: 02.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.recipe.display;

import com.arcanc.metamorphosis.data.recipe.slot_display.ResourcesDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public abstract class MetaBaseRecipeDisplay implements RecipeDisplay
{
    private final ResourcesDisplay resourcesDisplay;

    public MetaBaseRecipeDisplay(ResourcesDisplay resourcesDisplay)
    {
        this.resourcesDisplay = resourcesDisplay;
    }

    public ResourcesDisplay getResourcesDisplay()
    {
        return resourcesDisplay;
    }
}
