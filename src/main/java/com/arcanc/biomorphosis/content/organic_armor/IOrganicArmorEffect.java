/**
 * @author ArcAnc
 * Created at: 16.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.organic_armor;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public interface IOrganicArmorEffect
{
	default void applyTick(LivingEntity wearer, ResourceLocation effectDataId, int count, int duration)
	{
	}

	default void applyAttributes(LivingEntity wearer, ResourceLocation effectDataId, int count)
	{
	}

	default void removeAttributes(LivingEntity wearer, ResourceLocation effectDataId)
	{
	}

	default void onIncomingDamage(LivingEntity wearer, LivingDamageEvent.Pre event, ResourceLocation effectDataId, int count)
	{
	}

	default void onMeleeAttacked(LivingEntity wearer, LivingEntity attacker, DamageSource source, float damage, ResourceLocation effectDataId, int count)
	{
	}
}
