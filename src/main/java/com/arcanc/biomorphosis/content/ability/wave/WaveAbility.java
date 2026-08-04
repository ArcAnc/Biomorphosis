/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.wave;

import com.arcanc.biomorphosis.content.ability.Ability;
import com.arcanc.biomorphosis.content.ability.AbilityCastAnimations;
import com.arcanc.biomorphosis.content.ability.IAbilityType;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public final class WaveAbility extends Ability
{
	public static final int COOLDOWN_TICKS = 20 * 10;
	public static final int CAST_TIME_TICKS = 20;
	public static final double RANGE = 20.0d;
	public static final double SPEED_PER_TICK = 12.0d / 20.0d;
	public static final double HALF_WIDTH = 3.0d;
	public static final double HEIGHT = 1.2d;
	public static final double ARC_DEPTH = 1.15d;
	public static final double CENTER_OFFSET_Y = 0.0d;
	private static final ResourceLocation ICON = Database.rl("textures/gui/ability/wave.png");

	public WaveAbility(Holder<? extends IAbilityType<?>> type)
	{
		super(type, COOLDOWN_TICKS, CAST_TIME_TICKS, AbilityCastAnimations.WAVE);
	}

	public double range()
	{
		return RANGE;
	}

	public double speedPerTick()
	{
		return SPEED_PER_TICK;
	}

	@Override
	public Optional<SoundEvent> getCastSound()
	{
		return Optional.of(Registration.SoundReg.WAVE_CAST.get());
	}

	@Override
	public Optional<ResourceLocation> getIcon()
	{
		return Optional.of(ICON);
	}
}
