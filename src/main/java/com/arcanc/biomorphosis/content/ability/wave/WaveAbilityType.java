/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.wave;

import com.arcanc.biomorphosis.content.ability.AbilityActivationContext;
import com.arcanc.biomorphosis.content.ability.IMobAbilityType;
import com.arcanc.biomorphosis.content.ability.MobAbilityCastContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

public final class WaveAbilityType implements IMobAbilityType<WaveAbility>
{
	@Override
	public Class<WaveAbility> getAbilityClass()
	{
		return WaveAbility.class;
	}

	@Override
	public boolean canActivate(AbilityActivationContext context, WaveAbility ability)
	{
		return !context.player().isSpectator();
	}

	@Override
	public boolean activate(AbilityActivationContext context, WaveAbility ability)
	{
		WaveHandler.spawn(context.player(), ability);
		return true;
	}

	@Override
	public boolean canCast(MobAbilityCastContext context, WaveAbility ability)
	{
		return context.caster().distanceToSqr(context.target()) <= ability.range() * ability.range() &&
				context.target().getBoundingBox().getCenter().subtract(context.caster().getBoundingBox().getCenter()).lengthSqr() >= 0.0001D;
	}

	@Override
	public boolean cast(MobAbilityCastContext context, WaveAbility ability)
	{
		var caster = context.caster();
		WaveHandler.spawn(caster, context.target().getBoundingBox().getCenter().subtract(caster.getBoundingBox().getCenter()), ability);
		if (caster.level() instanceof ServerLevel level)
			ability.getCastSound().ifPresent(sound -> level.playSound(null, caster, sound, SoundSource.HOSTILE, 1.0F, 1.0F));
		return true;
	}
}
