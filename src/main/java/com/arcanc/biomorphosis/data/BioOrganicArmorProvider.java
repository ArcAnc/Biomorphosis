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
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class BioOrganicArmorProvider extends BioRegistryData
{
	private final Map<ResourceLocation, OrganicArmorType> armorDataMap = new HashMap<>();

	@Override
	protected void addContent()
	{
		addType("lifeless_head", Registration.ItemReg.LIFELESS_HELMET.getId(), EquipmentSlot.HEAD, 2, 200);
		addType("lifeless_chest", Registration.ItemReg.LIFELESS_CHESTPLATE.getId(), EquipmentSlot.CHEST, 6, 200);
		addType("lifeless_legs", Registration.ItemReg.LIFELESS_LEGGINGS.getId(), EquipmentSlot.LEGS, 4, 200);
		addType("lifeless_feet", Registration.ItemReg.LIFELESS_BOOTS.getId(), EquipmentSlot.FEET, 2, 200);
	}

	private void addType(String id, ResourceLocation sourceArmorId, EquipmentSlot slot, int armor, int capacity)
	{
		addType(Database.rl(id), new OrganicArmorType(sourceArmorId, slot, armor, capacity));
	}

	private void addType(ResourceLocation id, OrganicArmorType type)
	{
		this.armorDataMap.putIfAbsent(id, type);
	}

	@Override
	protected void registerContent(RegistrySetBuilder registrySetBuilder)
	{
		registrySetBuilder.add(Registration.OrganicArmorReg.TYPE_KEY, context ->
				this.armorDataMap.forEach((location, type) ->
						context.register(getTypeKey(location), type)));
	}

	private ResourceKey<OrganicArmorType> getTypeKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registration.OrganicArmorReg.TYPE_KEY, location);
	}
}
