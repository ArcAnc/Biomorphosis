/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

public interface IMobAbilityType<A extends IAbility> extends IAbilityType<A>
{
	default boolean canCast(MobAbilityCastContext context, A ability)
	{
		return true;
	}

	boolean cast(MobAbilityCastContext context, A ability);

	default boolean tryCast(MobAbilityCastContext context)
	{
		IAbility untypedAbility = context.ability();
		if (!this.supports(untypedAbility))
			return false;

		A ability = this.getAbilityClass().cast(untypedAbility);
		return this.canCast(context, ability) && this.cast(context, ability);
	}
}
