/**
 * @author ArcAnc
 * Created at: 21.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BioDamageTypesProvider extends BioRegistryData
{
	private final Map<ResourceLocation, DamageType> damageTypeMap = new HashMap<>();
	
	public BioDamageTypesProvider()
	{
		super();
	}
	
	@Override
	protected void addContent()
	{
		addDamageType(new DamageType(Registration.DamageTypeReg.TURRET_DAMAGE.location().toString(),
				DamageScaling.NEVER,
				0.1f,
				DamageEffects.HURT,
				DeathMessageType.DEFAULT));
	}
	
	private void addDamageType(DamageType type)
	{
		this.damageTypeMap.putIfAbsent(ResourceLocation.parse(type.msgId()), type);
	}
	
	@Override
	protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
	{
		registrySetBuilder.add(Registries.DAMAGE_TYPE, context ->
				this.damageTypeMap.forEach((location, damageType) ->
						context.register(getDefinitionKey(location), damageType)));
	}
	
	private @NotNull ResourceKey<DamageType> getDefinitionKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registries.DAMAGE_TYPE, location);
	}
}
