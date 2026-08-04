/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

public interface IAbilityType<A extends IAbility>
{
	Class<A> getAbilityClass();

	default boolean supports(IAbility ability)
	{
		return this.getAbilityClass().isInstance(ability);
	}

	default IAbilityInstance createInstance(IAbility ability)
	{
		if (!this.supports(ability))
			throw new IllegalArgumentException("Ability is not supported by this ability type");

		return new AbilityInstance(ability);
	}

	default boolean canActivate(AbilityActivationContext context, A ability)
	{
		return true;
	}

	default boolean canActivate(AbilityActivationContext context)
	{
		IAbility untypedAbility = context.ability();
		return this.supports(untypedAbility) && this.canActivate(context, this.getAbilityClass().cast(untypedAbility));
	}

	default boolean canContinueCasting(AbilityActivationContext context, A ability)
	{
		return true;
	}

	default boolean canContinueCasting(AbilityActivationContext context)
	{
		IAbility untypedAbility = context.ability();
		return this.supports(untypedAbility) && this.canContinueCasting(context, this.getAbilityClass().cast(untypedAbility));
	}

	default boolean activate(AbilityActivationContext context, A ability)
	{
		return true;
	}

	default boolean tryActivate(AbilityActivationContext context)
	{
		IAbility untypedAbility = context.ability();
		if (!this.supports(untypedAbility))
			return false;

		A ability = this.getAbilityClass().cast(untypedAbility);
		return this.canActivate(context) && this.activate(context, ability);
	}
}
