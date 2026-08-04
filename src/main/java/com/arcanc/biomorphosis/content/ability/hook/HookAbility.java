/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.hook;

import com.arcanc.biomorphosis.content.ability.Ability;
import com.arcanc.biomorphosis.content.ability.AbilityCastAnimations;
import com.arcanc.biomorphosis.content.ability.IAbilityType;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class HookAbility extends Ability
{
	public static final int COOLDOWN_TICKS = 20 * 5;
	public static final int CAST_TIME_TICKS = 6;
	public static final double RANGE = 20.0d;
	public static final double SPEED_PER_TICK = 1.5d;
	public static final int PULL_TICKS = 24;
	public static final double PULL_SPEED = 0.70d;
	private static final ResourceLocation ICON = Database.rl("textures/gui/ability/hook.png");

	public HookAbility(Holder<? extends IAbilityType<?>> type)
	{
		super(type, COOLDOWN_TICKS, CAST_TIME_TICKS, AbilityCastAnimations.HOOK);
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
	public Optional<ResourceLocation> getIcon()
	{
		return Optional.of(ICON);
	}
}
