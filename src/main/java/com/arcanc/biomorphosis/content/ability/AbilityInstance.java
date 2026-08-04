/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import java.util.Objects;

public final class AbilityInstance implements IAbilityInstance
{
	private final IAbility ability;
	private int remainingCooldownTicks;

	public AbilityInstance(IAbility ability)
	{
		this.ability = Objects.requireNonNull(ability, "Ability cannot be null");
	}

	@Override
	public IAbility getAbility()
	{
		return this.ability;
	}

	@Override
	public int getRemainingCooldownTicks()
	{
		return this.remainingCooldownTicks;
	}

	@Override
	public void startCooldown()
	{
		this.remainingCooldownTicks = this.ability.getCooldownTicks();
	}

	@Override
	public void tickCooldown()
	{
		if (this.remainingCooldownTicks > 0)
			this.remainingCooldownTicks--;
	}

	@Override
	public void resetCooldown()
	{
		this.remainingCooldownTicks = 0;
	}
}
