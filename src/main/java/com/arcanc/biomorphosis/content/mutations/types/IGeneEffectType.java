/**
 * @author ArcAnc
 * Created at: 29.09.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.types;

import com.arcanc.biomorphosis.content.mutations.AttributeParams;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface IGeneEffectType<T extends IGeneEffectType<?>>
{
	Codec<IGeneEffectType<?>> CODEC = Registration.GenomeReg.EFFECT_TYPE_REGISTRY.byNameCodec();

	MapCodec<T> mapCodec();

	ResourceLocation getId();

	default CompoundTag createData(LivingEntity entity, AttributeParams params)
	{
		return new CompoundTag();
	}

	default void loadData(LivingEntity entity, AttributeParams params, CompoundTag data)
	{
	}

	default void saveData(LivingEntity entity, AttributeParams params, CompoundTag data)
	{
	}

	default void apply(LivingEntity entity, AttributeParams params, CompoundTag data)
	{
		apply(entity, params);
	}

	default void remove(LivingEntity entity, AttributeParams params, CompoundTag data)
	{
		remove(entity, params);
	}

	default void tick(LivingEntity entity, AttributeParams params, CompoundTag data)
	{
		tick(entity, params);
	}

	default void apply (LivingEntity entity, AttributeParams params)
	{
	}

	default void remove (LivingEntity entity, AttributeParams params)
	{
	}

	default void tick (LivingEntity entity, AttributeParams params)
	{
	}
}
