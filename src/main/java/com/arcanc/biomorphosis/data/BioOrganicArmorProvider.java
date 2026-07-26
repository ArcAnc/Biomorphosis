/**
 * @author ArcAnc
 * Created at: 21.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorType;
import com.arcanc.biomorphosis.content.organic_armor.IOrganicArmorEffect;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorFluidEffect;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioOrganicArmorProvider extends BioRegistryData
{
	private final Map<ResourceLocation, OrganicArmorType> armorDataMap = new HashMap<>();
	private final Map<ResourceLocation, OrganicArmorFluidEffect> armorEffectDataMap = new HashMap<>();

	@Override
	protected void addContent()
	{
		addType("organic", "organic", List.of(
				new OrganicArmorType.PieceParams(Registration.ItemReg.LIFELESS_HELMET.getId(), Database.rl("organic_armor/helmet"), EquipmentSlot.HEAD, 2, 200),
				new OrganicArmorType.PieceParams(Registration.ItemReg.LIFELESS_CHESTPLATE.getId(), Database.rl("organic_armor/chest"), EquipmentSlot.CHEST, 6, 200),
				new OrganicArmorType.PieceParams(Registration.ItemReg.LIFELESS_LEGGINGS.getId(), Database.rl("organic_armor/pants"), EquipmentSlot.LEGS, 4, 200),
				new OrganicArmorType.PieceParams(Registration.ItemReg.LIFELESS_BOOTS.getId(), Database.rl("organic_armor/boots"), EquipmentSlot.FEET, 2, 200)));

		addDefaultEffects("organic");
	}

	private void addType(String id, String type, List<OrganicArmorType.PieceParams> params)
	{
		addType(Database.rl(id), new OrganicArmorType(type, params));
	}

	private void addType(ResourceLocation id, OrganicArmorType type)
	{
		this.armorDataMap.putIfAbsent(id, type);
	}

	private void addDefaultEffects(String typeId)
	{
		ResourceLocation organicArmor = Database.rl(typeId);
		addEffect(typeId + "_acid", Database.rl("acid"), Registration.FluidReg.ACID.type().getId(), organicArmor);
		addEffect(typeId + "_adrenaline", Database.rl("adrenaline"), Registration.FluidReg.ADRENALINE.type().getId(), organicArmor);
		addEffect(typeId + "_biomass", Database.rl("biomass"), Registration.FluidReg.BIOMASS.type().getId(), organicArmor);
	}

	private void addEffect(String id, ResourceLocation effect, ResourceLocation fluidType, ResourceLocation organicArmor)
	{
		addEffect(Database.rl(id), new OrganicArmorFluidEffect(
				getEffectKey(effect),
				getFluidTypeKey(fluidType),
				getTypeKey(organicArmor)));
	}

	private void addEffect(ResourceLocation id, OrganicArmorFluidEffect effect)
	{
		this.armorEffectDataMap.putIfAbsent(id, effect);
	}

	@Override
	protected void registerContent(RegistrySetBuilder registrySetBuilder)
	{
		registrySetBuilder.add(Registration.OrganicArmorReg.TYPE_KEY, context ->
				this.armorDataMap.forEach((location, type) ->
						context.register(getTypeKey(location), type)));
		registrySetBuilder.add(Registration.OrganicArmorReg.EFFECT_DATA_KEY, context ->
				this.armorEffectDataMap.forEach((location, effect) ->
						context.register(getEffectDataKey(location), effect)));
	}

	private ResourceKey<OrganicArmorType> getTypeKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registration.OrganicArmorReg.TYPE_KEY, location);
	}

	private ResourceKey<IOrganicArmorEffect> getEffectKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registration.OrganicArmorReg.EFFECT_KEY, location);
	}

	private ResourceKey<FluidType> getFluidTypeKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(NeoForgeRegistries.Keys.FLUID_TYPES, location);
	}

	private ResourceKey<OrganicArmorFluidEffect> getEffectDataKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registration.OrganicArmorReg.EFFECT_DATA_KEY, location);
	}
}
