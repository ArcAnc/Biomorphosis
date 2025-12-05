/**
 * @author ArcAnc
 * Created at: 06.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.recipe.builders;

import com.arcanc.metamorphosis.data.recipe.MetaBaseRecipe;
import com.arcanc.metamorphosis.data.recipe.input.MetaBaseInput;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class MetaBaseRecipeBuilder<T extends MetaBaseRecipeBuilder<T, R, I>, R extends MetaBaseRecipe<I>, I extends MetaBaseInput> implements RecipeBuilder
{
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    protected final MetaBaseRecipe.ResourcesInfo info;

    protected MetaBaseRecipeBuilder(MetaBaseRecipe.ResourcesInfo info)
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
    public @NotNull T unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion)
    {
        this.criteria.put(name, criterion);
        return getSelf();
    }

    @Override
    public @NotNull T group(@Nullable String groupName)
    {
        return getSelf();
    }

    @Override
    public void save(@NotNull RecipeOutput output, @NotNull ResourceKey<Recipe<?>> key)
    {
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        R recipe = getRecipe();
        output.accept(key, recipe, advancement.build(key.location().withPrefix("recipes/")));
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id)
    {
        ResourceLocation resourcelocation = RecipeBuilder.getDefaultRecipeId(this.getResult());
        ResourceLocation resourcelocation1 = ResourceLocation.parse(id);
        if (resourcelocation1.equals(resourcelocation))
            throw new IllegalStateException("Recipe " + id + " should remove its 'save' argument as it is equal to default one");
        else
        {
            R recipe = getRecipe();
            this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, resourcelocation1.withPrefix(recipe.group() + "/")));
        }
    }
}
