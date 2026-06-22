/**
 * @author ArcAnc
 * Created at: 21.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.organic_armor;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public record OrganicArmorType(ResourceLocation sourceArmor, EquipmentSlot slot, int armor, int capacity)
{
	public static final Codec<EquipmentSlot> ARMOR_SLOT_CODEC = Codec.STRING.comapFlatMap(name ->
	{
		try
		{
			EquipmentSlot slot = EquipmentSlot.byName(name);
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR)
				return DataResult.error(() -> "Slot " + name + " is not an armor slot");
			return DataResult.success(slot);
		}
		catch (IllegalArgumentException ex)
		{
			return DataResult.error(() -> "Unknown equipment slot " + name);
		}
	}, EquipmentSlot :: getName);

	public static final Codec<OrganicArmorType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("source_armor").forGetter(OrganicArmorType :: sourceArmor),
			ARMOR_SLOT_CODEC.fieldOf("slot").forGetter(OrganicArmorType :: slot),
			Codec.INT.fieldOf("armor").forGetter(OrganicArmorType :: armor),
			Codec.INT.fieldOf("capacity").forGetter(OrganicArmorType :: capacity)
	).apply(instance, OrganicArmorType :: new));

	public int drainedArmor()
	{
		return this.armor / 2;
	}
}
