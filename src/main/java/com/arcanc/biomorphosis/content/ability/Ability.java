/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class Ability implements IAbility
{
	private final Holder<? extends IAbilityType<?>> type;
	private final int cooldownTicks;
	private final int castTimeTicks;
	private final ResourceLocation castAnimationId;

	public Ability(Holder<? extends IAbilityType<?>> type, int cooldownTicks)
	{
		this(type, cooldownTicks, 0, AbilityCastAnimations.NONE);
	}

	public Ability(Holder<? extends IAbilityType<?>> type, int cooldownTicks, int castTimeTicks, ResourceLocation castAnimationId)
	{
		this.type = Objects.requireNonNull(type, "Ability type cannot be null");

		if (cooldownTicks < 0)
			throw new IllegalArgumentException("Ability cooldown cannot be negative");
		if (castTimeTicks < 0)
			throw new IllegalArgumentException("Ability cast time cannot be negative");

		this.cooldownTicks = cooldownTicks;
		this.castTimeTicks = castTimeTicks;
		this.castAnimationId = Objects.requireNonNull(castAnimationId, "Ability cast animation id cannot be null");
	}

	@Override
	public Holder<? extends IAbilityType<?>> getType()
	{
		return this.type;
	}

	@Override
	public int getCooldownTicks()
	{
		return this.cooldownTicks;
	}

	@Override
	public int getCastTimeTicks()
	{
		return this.castTimeTicks;
	}

	@Override
	public ResourceLocation getCastAnimationId()
	{
		return this.castAnimationId;
	}
}
