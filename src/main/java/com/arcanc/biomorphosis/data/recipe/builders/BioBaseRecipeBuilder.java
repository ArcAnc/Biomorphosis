/**
 * @author ArcAnc
 * Created at: 06.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.builders;

import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.input.BioBaseInput;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BioBaseRecipeBuilder<T extends BioBaseRecipeBuilder<T, R, I>, R extends BioBaseRecipe<I>, I extends BioBaseInput> implements RecipeBuilder
{
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    protected String group;
    protected final BioBaseRecipe.ResourcesInfo info;

    protected BioBaseRecipeBuilder(BioBaseRecipe.ResourcesInfo info)
    {
        this.info = info;
    }

    protected abstract R getRecipe();

    @SuppressWarnings("unchecked")
    private T getSelf()
    {
        return (T)this;
    }

    @Override
    public T unlockedBy(String name, Criterion<?> criterion)
    {
        this.criteria.put(name, criterion);
        return getSelf();
    }

    @Override
    public T group(@Nullable String groupName)
    {
        this.group = groupName;
        return getSelf();
    }
    
    @Override
    public void save(RecipeOutput output, ResourceLocation loc)
    {
        Advancement.Builder advancement = output.advancement().
                        addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(loc)).
                        rewards(AdvancementRewards.Builder.recipe(loc)).
                requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        R recipe = getRecipe();
        output.accept(loc, recipe, advancement.build(loc.withPrefix("recipes/")));
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id)
    {
        ResourceLocation resourcelocation = RecipeBuilder.getDefaultRecipeId(this.getResult());
        ResourceLocation resourcelocation1 = ResourceLocation.parse(id);
        if (resourcelocation1.equals(resourcelocation))
            throw new IllegalStateException("Recipe " + id + " should remove its 'save' argument as it is equal to default one");
        else
        {
            R recipe = getRecipe();
            this.save(recipeOutput, resourcelocation1.withPrefix(recipe.getGroup() + "/"));
        }
    }
}
