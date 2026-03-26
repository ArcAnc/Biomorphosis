/**
 * @author ArcAnc
 * Created at: 02.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.recipe.ingredient;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Arrays;
import java.util.stream.Stream;

public record IngredientWithSize(Ingredient ingredient, int amount) implements ICustomIngredient
{
    public static final MapCodec<IngredientWithSize> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.
            group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientWithSize :: ingredient),
                    ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(IngredientWithSize :: amount)).
            apply(instance, IngredientWithSize::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithSize> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            IngredientWithSize :: ingredient,
            ByteBufCodecs.VAR_INT,
            IngredientWithSize :: amount,
            IngredientWithSize :: new);

    public IngredientWithSize(Ingredient ingredient)
    {
        this(ingredient, 1);
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return this.ingredient.test(stack) && stack.getCount() >= this.amount;
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(stack -> stack.copyWithCount(this.amount));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public IngredientType<?> getType()
    {
        return Registration.IngredientReg.SIZED_INGREDIENT.get();
    }
	
	public static IngredientWithSize of (ItemStack stack)
	{
		return new IngredientWithSize(Ingredient.of(stack.getItem()), stack.getCount());
	}
	
	public static IngredientWithSize of (ItemLike item)
	{
		return new IngredientWithSize(Ingredient.of(item));
	}
	
	public static IngredientWithSize of (Ingredient ingredient)
	{
		return new IngredientWithSize(ingredient);
	}
}
