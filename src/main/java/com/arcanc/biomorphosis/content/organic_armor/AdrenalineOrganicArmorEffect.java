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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AdrenalineOrganicArmorEffect implements IOrganicArmorEffect
{
	@Override
	public void applyAttributes(LivingEntity wearer, ResourceLocation effectDataId, int count)
	{
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.MOVEMENT_SPEED, OrganicArmorEffectHandler.modifierId(effectDataId, "movement_speed"), 0.04d * count, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.ATTACK_SPEED, OrganicArmorEffectHandler.modifierId(effectDataId, "attack_speed"), 0.06d * count, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	@Override
	public void removeAttributes(LivingEntity wearer, ResourceLocation effectDataId)
	{
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.MOVEMENT_SPEED, OrganicArmorEffectHandler.modifierId(effectDataId, "movement_speed"), 0, AttributeModifier.Operation.ADD_VALUE);
		OrganicArmorEffectHandler.replaceModifier(wearer, Attributes.ATTACK_SPEED, OrganicArmorEffectHandler.modifierId(effectDataId, "attack_speed"), 0, AttributeModifier.Operation.ADD_VALUE);
	}
}
