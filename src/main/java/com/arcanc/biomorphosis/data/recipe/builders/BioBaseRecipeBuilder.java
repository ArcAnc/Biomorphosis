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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BioBaseRecipeBuilder<T extends BioBaseRecipeBuilder<T, R, I>, R extends BioBaseRecipe<I>, I extends BioBaseInput> implements RecipeBuilder
{
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

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
}
