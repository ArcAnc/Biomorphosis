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
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public interface IAbility
{
	Holder<? extends IAbilityType<?>> getType();

	int getCooldownTicks();

	default int getCastTimeTicks()
	{
		return 0;
	}

	default ResourceLocation getCastAnimationId()
	{
		return AbilityCastAnimations.NONE;
	}

	/**
	 * Sound emitted once when this ability begins casting.
	 */
	default Optional<SoundEvent> getCastSound()
	{
		return Optional.empty();
	}

	/**
	 * Texture displayed for this ability in client-side ability selection UI.
	 * Implementations without a dedicated texture can omit an icon.
	 */
	default Optional<ResourceLocation> getIcon()
	{
		return Optional.empty();
	}

	default IAbilityInstance createInstance()
	{
		return this.getType().value().createInstance(this);
	}
}
