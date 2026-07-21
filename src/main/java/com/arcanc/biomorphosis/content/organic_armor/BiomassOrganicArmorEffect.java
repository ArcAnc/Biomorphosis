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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class BiomassOrganicArmorEffect implements IOrganicArmorEffect
{
	@Override
	public void applyTick(LivingEntity wearer, ResourceLocation effectDataId, int count, int duration)
	{
		wearer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, Math.min(count - 1, 3), true, false));
	}

	@Override
	public void applyAttributes(LivingEntity wearer, ResourceLocation effectDataId, int count)
	{
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.KNOCKBACK_RESISTANCE, OrganicArmorEffectHandler.modifierId(effectDataId, "knockback_resistance"), Math.min(0.1d * count, 0.6d), AttributeModifier.Operation.ADD_VALUE);
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.ARMOR_TOUGHNESS, OrganicArmorEffectHandler.modifierId(effectDataId, "armor_toughness"), 0.75d * count, AttributeModifier.Operation.ADD_VALUE);
	}

	@Override
	public void removeAttributes(LivingEntity wearer, ResourceLocation effectDataId)
	{
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.KNOCKBACK_RESISTANCE, OrganicArmorEffectHandler.modifierId(effectDataId, "knockback_resistance"), 0, AttributeModifier.Operation.ADD_VALUE);
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.ARMOR_TOUGHNESS, OrganicArmorEffectHandler.modifierId(effectDataId, "armor_toughness"), 0, AttributeModifier.Operation.ADD_VALUE);
	}

	@Override
	public void onIncomingDamage(LivingEntity wearer, LivingDamageEvent.Pre event, ResourceLocation effectDataId, int count)
	{
		float reduction = Math.min(0.06f * count, 0.3f);
		event.setNewDamage(event.getNewDamage() * (1f - reduction));
	}
}
