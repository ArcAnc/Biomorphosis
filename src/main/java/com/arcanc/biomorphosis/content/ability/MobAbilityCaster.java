/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import com.arcanc.biomorphosis.util.helper.AbilityHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public final class MobAbilityCaster
{
	private final Map<ResourceLocation, Long> cooldownEnds = new HashMap<>();

	public boolean tryCast(LivingEntity caster, LivingEntity target, ResourceLocation abilityId)
	{
		if (!(caster.level() instanceof ServerLevel level) || !caster.isAlive() || !target.isAlive() || caster == target)
			return false;

		IAbility ability = AbilityHelper.getInnateAbility(caster, abilityId, IAbility.class).orElse(null);
		if (ability == null || level.getGameTime() < this.cooldownEnds.getOrDefault(abilityId, 0L))
			return false;
		if (!(ability.getType().value() instanceof IMobAbilityType<?> abilityType))
			return false;

		if (!abilityType.tryCast(new MobAbilityCastContext(caster, target, ability)))
			return false;

		this.cooldownEnds.put(abilityId, level.getGameTime() + ability.getCooldownTicks());
		return true;
	}
}
