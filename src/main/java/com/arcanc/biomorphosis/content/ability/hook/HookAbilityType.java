/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.hook;

import com.arcanc.biomorphosis.content.ability.AbilityActivationContext;
import com.arcanc.biomorphosis.content.ability.IMobAbilityType;
import com.arcanc.biomorphosis.content.ability.MobAbilityCastContext;
import net.minecraft.world.entity.Mob;

public final class HookAbilityType implements IMobAbilityType<HookAbility>
{
	@Override
	public Class<HookAbility> getAbilityClass()
	{
		return HookAbility.class;
	}

	@Override
	public boolean canActivate(AbilityActivationContext context, HookAbility ability)
	{
		return !context.player().isSpectator();
	}

	@Override
	public boolean activate(AbilityActivationContext context, HookAbility ability)
	{
		HookHandler.spawn(context.player(), ability);
		return true;
	}

	@Override
	public boolean canCast(MobAbilityCastContext context, HookAbility ability)
	{
		if (context.caster() instanceof Mob mob && mob.isWithinMeleeAttackRange(context.target()))
			return false;

		return context.caster().distanceToSqr(context.target()) <= ability.range() * ability.range() &&
				context.target().getBoundingBox().getCenter().subtract(context.caster().getEyePosition()).lengthSqr() >= 0.0001D;
	}

	@Override
	public boolean cast(MobAbilityCastContext context, HookAbility ability)
	{
		HookHandler.spawn(context.caster(), context.target().getBoundingBox().getCenter().subtract(context.caster().getEyePosition()), ability);
		return true;
	}
}
