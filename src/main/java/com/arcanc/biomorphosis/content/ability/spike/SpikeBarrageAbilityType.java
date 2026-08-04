/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.spike;

import com.arcanc.biomorphosis.content.ability.AbilityActivationContext;
import com.arcanc.biomorphosis.content.ability.IMobAbilityType;
import com.arcanc.biomorphosis.content.ability.MobAbilityCastContext;

public final class SpikeBarrageAbilityType implements IMobAbilityType<SpikeBarrageAbility>
{
	@Override
	public Class<SpikeBarrageAbility> getAbilityClass()
	{
		return SpikeBarrageAbility.class;
	}

	@Override
	public boolean canActivate(AbilityActivationContext context, SpikeBarrageAbility ability)
	{
		return !context.player().isSpectator();
	}

	@Override
	public boolean activate(AbilityActivationContext context, SpikeBarrageAbility ability)
	{
		SpikeBarrageHandler.spawn(context.player(), SpikeBarrageHandler.findAimPoint(context.player(), ability.range()), ability);
		return true;
	}

	@Override
	public boolean canCast(MobAbilityCastContext context, SpikeBarrageAbility ability)
	{
		return context.caster().distanceToSqr(context.target()) <= ability.range() * ability.range() &&
				context.target().getBoundingBox().getCenter().subtract(context.caster().getEyePosition()).lengthSqr() >= 4.0D;
	}

	@Override
	public boolean cast(MobAbilityCastContext context, SpikeBarrageAbility ability)
	{
		SpikeBarrageHandler.spawn(context.caster(), context.target().getBoundingBox().getCenter(), ability);
		return true;
	}
}
