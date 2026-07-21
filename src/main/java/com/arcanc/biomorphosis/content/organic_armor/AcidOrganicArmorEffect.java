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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class AcidOrganicArmorEffect implements IOrganicArmorEffect
{
	@Override
	public void onMeleeAttacked(LivingEntity wearer, LivingEntity attacker, DamageSource source, float damage, ResourceLocation effectDataId, int count)
	{
		int amplifier = Math.min(count - 1, 3);
		int duration = 60 + count * 20;
		attacker.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amplifier));
		attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier));
	}
}
