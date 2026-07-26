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

import java.util.List;
import java.util.Optional;

public record OrganicArmorType(String type, List<PieceParams> pieces)
{
	public OrganicArmorType
	{
		pieces = List.copyOf(pieces);
	}

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
			Codec.STRING.fieldOf("type").forGetter(OrganicArmorType :: type),
			PieceParams.CODEC.listOf().fieldOf("pieces").forGetter(OrganicArmorType :: pieces)
	).apply(instance, OrganicArmorType :: new));

	public Optional<PieceParams> get(EquipmentSlot slot)
	{
		return this.pieces.stream().
				filter(params -> params.slot() == slot).
				findFirst();
	}

	public Optional<PieceParams> findBySourceArmor(ResourceLocation sourceArmor)
	{
		return this.pieces.stream().
				filter(params -> params.sourceArmor().equals(sourceArmor)).
				findFirst();
	}

	public record PieceParams(ResourceLocation sourceArmor, ResourceLocation icon, EquipmentSlot slot, int armor, int capacity)
	{
		public static final Codec<PieceParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("source_armor").forGetter(PieceParams :: sourceArmor),
				ResourceLocation.CODEC.fieldOf("icon").forGetter(PieceParams :: icon),
				ARMOR_SLOT_CODEC.fieldOf("slot").forGetter(PieceParams :: slot),
				Codec.intRange(0, Integer.MAX_VALUE).fieldOf("armor").forGetter(PieceParams :: armor),
				Codec.intRange(0, Integer.MAX_VALUE).fieldOf("capacity").forGetter(PieceParams :: capacity)
		).apply(instance, PieceParams :: new));

		public int drainedArmor()
		{
			return this.armor / 2;
		}
	}
}
