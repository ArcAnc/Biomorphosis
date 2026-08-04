/**
 * @author ArcAnc
 * Created at: 31.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Set;

/**
 * The registry entry id identifies the entity type that possesses these abilities.
 */
public record EntityAbilityDefinition(Set<ResourceKey<IAbility>> abilities)
{
	public EntityAbilityDefinition
	{
		abilities = Set.copyOf(abilities);
	}

	public static final Codec<EntityAbilityDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceKey.codec(Registration.AbilityReg.ABILITY_KEY).listOf().xmap(Set :: copyOf, List :: copyOf).
					fieldOf("abilities").forGetter(EntityAbilityDefinition :: abilities)
	).apply(instance, EntityAbilityDefinition :: new));
}
