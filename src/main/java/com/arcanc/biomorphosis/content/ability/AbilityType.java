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

public final class AbilityType<A extends IAbility> implements IAbilityType<A>
{
	private final Class<A> abilityClass;

	public AbilityType(Class<A> abilityClass)
	{
		this.abilityClass = Objects.requireNonNull(abilityClass, "Ability class cannot be null");
	}

	@Override
	public Class<A> getAbilityClass()
	{
		return this.abilityClass;
	}
}
