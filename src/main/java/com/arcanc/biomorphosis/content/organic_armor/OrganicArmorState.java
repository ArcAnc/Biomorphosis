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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record OrganicArmorState(List<Piece> pieces)
{
	public static final OrganicArmorState EMPTY = empty();

	public OrganicArmorState
	{
		pieces = List.copyOf(pieces);
	}

	public static OrganicArmorState empty()
	{
		return new OrganicArmorState(List.of());
	}

	public static final Codec<OrganicArmorState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Piece.CODEC.listOf().fieldOf("pieces").forGetter(OrganicArmorState :: pieces)
	).apply(instance, OrganicArmorState :: new));

	public static final StreamCodec<RegistryFriendlyByteBuf, OrganicArmorState> STREAM_CODEC =
			ByteBufCodecs.<RegistryFriendlyByteBuf, Piece>list().
					apply(Piece.STREAM_CODEC).
					map(OrganicArmorState :: new, OrganicArmorState :: pieces);

	public boolean isEmpty()
	{
		return this.pieces.isEmpty();
	}

	public Optional<Piece> get(EquipmentSlot slot)
	{
		return this.pieces.stream().filter(piece -> piece.slot() == slot).findFirst();
	}

	public boolean has(EquipmentSlot slot)
	{
		return get(slot).isPresent();
	}

	public OrganicArmorState with(Piece piece)
	{
		List<Piece> mutable = new ArrayList<>(this.pieces);
		mutable.removeIf(existing -> existing.slot() == piece.slot());
		mutable.add(piece);
		return new OrganicArmorState(mutable);
	}

	public OrganicArmorState without(EquipmentSlot slot)
	{
		List<Piece> mutable = new ArrayList<>(this.pieces);
		mutable.removeIf(piece -> piece.slot() == slot);
		return new OrganicArmorState(mutable);
	}

	public record Piece(EquipmentSlot slot, ResourceLocation typeId, FluidStack fluid)
	{
		public static final Codec<Piece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				OrganicArmorType.ARMOR_SLOT_CODEC.fieldOf("slot").forGetter(Piece :: slot),
				ResourceLocation.CODEC.fieldOf("type").forGetter(Piece :: typeId),
				FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(Piece :: fluid)
		).apply(instance, Piece :: new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Piece> STREAM_CODEC = StreamCodec.composite(
				NeoForgeStreamCodecs.enumCodec(EquipmentSlot.class),
				Piece :: slot,
				ResourceLocation.STREAM_CODEC,
				Piece :: typeId,
				FluidStack.OPTIONAL_STREAM_CODEC,
				Piece :: fluid,
				Piece :: new);

		public boolean hasFluid()
		{
			return !this.fluid.isEmpty();
		}
	}
}
