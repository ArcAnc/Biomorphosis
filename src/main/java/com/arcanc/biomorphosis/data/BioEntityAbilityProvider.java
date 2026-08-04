/**
 * @author ArcAnc
 * Created at: 31.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.ability.EntityAbilityDefinition;
import com.arcanc.biomorphosis.content.ability.IAbility;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class BioEntityAbilityProvider extends BioRegistryData
{
	private final Map<ResourceLocation, EntityAbilityDefinition> definitions = new LinkedHashMap<>();

	@Override
	protected void addContent()
	{
		addDefinition(Registration.EntityReg.MOB_QUEEN.getEntityHolder().get(),
				Registration.AbilityReg.WAVE.getId());
		addDefinition(Registration.EntityReg.MOB_QUEEN_GUARD.getEntityHolder().get(),
				Registration.AbilityReg.HOOK.getId());
		addDefinition(Registration.EntityReg.MOB_INFESTOR.getEntityHolder().get(),
				Registration.AbilityReg.SPIKE_BARRAGE.getId());
	}

	private void addDefinition(EntityType<?> entityType, ResourceLocation... abilities)
	{
		Set<ResourceKey<IAbility>> abilityKeys = Arrays.stream(abilities).
				map(abilityId -> ResourceKey.create(Registration.AbilityReg.ABILITY_KEY, abilityId)).
				collect(Collectors.toUnmodifiableSet());
		this.definitions.put(EntityType.getKey(entityType), new EntityAbilityDefinition(abilityKeys));
	}

	@Override
	protected void registerContent(RegistrySetBuilder registrySetBuilder)
	{
		registrySetBuilder.add(Registration.AbilityReg.ENTITY_ABILITY_KEY, context ->
				this.definitions.forEach((id, definition) ->
						context.register(getResourceKey(Registration.AbilityReg.ENTITY_ABILITY_KEY, id), definition)));
	}
}
