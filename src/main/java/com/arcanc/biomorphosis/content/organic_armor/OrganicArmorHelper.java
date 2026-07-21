/**
 * @author ArcAnc
 * Created at: 21.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.organic_armor;


import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2COrganicArmorSync;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganicArmorHelper
{
	private static final ResourceLocation ARMOR_MODIFIER_BASE = Database.rl("organic_armor");

	public static OrganicArmorState getState(LivingEntity entity)
	{
		return entity.hasData(Registration.DataAttachmentsReg.ORGANIC_ARMOR) ?
				entity.getData(Registration.DataAttachmentsReg.ORGANIC_ARMOR) :
				OrganicArmorState.empty();
	}

	public static boolean hasArmor(LivingEntity entity, EquipmentSlot slot)
	{
		return getState(entity).has(slot);
	}

	public static Optional<OrganicArmorState.Piece> getPiece(LivingEntity entity, EquipmentSlot slot)
	{
		return getState(entity).get(slot);
	}

	public static boolean install(ServerPlayer player, ResourceKey<OrganicArmorType> typeKey, ItemStack sourceStack)
	{
		Optional<Holder.Reference<OrganicArmorType>> optionalType = player.registryAccess().
				lookup(Registration.OrganicArmorReg.TYPE_KEY).
				flatMap(registry -> registry.get(typeKey));
		if (optionalType.isEmpty())
			return false;

		OrganicArmorType type = optionalType.get().value();
		OrganicArmorType.PieceParams params = findParamsForSource(type, sourceStack).orElse(null);
		if (params == null)
			return false;
		if (hasArmor(player, params.slot()))
			return false;

		OrganicArmorState state = getState(player).with(new OrganicArmorState.Piece(params.slot(), typeKey.location(), FluidStack.EMPTY));
		player.setData(Registration.DataAttachmentsReg.ORGANIC_ARMOR, state);
		sourceStack.shrink(1);
		rebuildArmor(player);
		sync(player, player);
		return true;
	}

	public static boolean uninstall(ServerPlayer player, EquipmentSlot slot)
	{
		OrganicArmorState.Piece piece = getPiece(player, slot).orElse(null);
		if (piece == null)
			return false;

		OrganicArmorType type = getType(player.level(), piece).orElse(null);
		if (type == null)
			return false;
		OrganicArmorType.PieceParams params = type.get(slot).orElse(null);
		if (params == null)
			return false;

		OrganicArmorState state = getState(player).without(slot);
		player.setData(Registration.DataAttachmentsReg.ORGANIC_ARMOR, state);
		OrganicArmorEffectHandler.removeEffectData(player, slot);
		rebuildArmor(player);
		returnSourceArmor(player, params, slot);
		sync(player, player);
		return true;
	}

	public static Optional<ResourceKey<OrganicArmorType>> findTypeForSource(HolderLookup.Provider registries, ItemStack sourceStack)
	{
		if (sourceStack.isEmpty())
			return Optional.empty();

		return registries.lookup(Registration.OrganicArmorReg.TYPE_KEY).
				flatMap(registry -> registry.
						listElements().
						filter(holder -> canCreateFrom(holder.value(), sourceStack)).
						map(Holder.Reference :: key).
						findFirst());
	}

	public static boolean canCreateFrom(HolderLookup.Provider registries, ResourceKey<OrganicArmorType> typeKey, ItemStack sourceStack)
	{
		return registries.lookup(Registration.OrganicArmorReg.TYPE_KEY).
				flatMap(registry -> registry.get(typeKey)).
				map(holder -> canCreateFrom(holder.value(), sourceStack)).
				orElse(false);
	}

	public static boolean canCreateFrom(OrganicArmorType type, ItemStack sourceStack)
	{
		return findParamsForSource(type, sourceStack).isPresent();
	}

	public static Optional<OrganicArmorType> getType(Level level, OrganicArmorState.Piece piece)
	{
		return level.registryAccess().
				lookup(Registration.OrganicArmorReg.TYPE_KEY).
				flatMap(registry -> registry.get(ResourceKey.create(Registration.OrganicArmorReg.TYPE_KEY, piece.typeId()))).
				map(Holder.Reference :: value);
	}

	public static int getEffectiveArmor(Level level, OrganicArmorState.Piece piece)
	{
		return getType(level, piece).
				flatMap(type -> type.get(piece.slot())).
				map(params -> piece.hasFluid() ? params.armor() : params.drainedArmor()).
				orElse(0);
	}

	public static boolean canFillFluid(LivingEntity entity, FluidStack fluid)
	{
		return getFillablePieces(entity, fluid).stream().anyMatch(entry -> entry.space() > 0);
	}

	public static int fillFluid(ServerPlayer player, FluidStack fluid)
	{
		List<FillablePiece> fillable = getFillablePieces(player, fluid);
		int remaining = fluid.getAmount();
		List<OrganicArmorState.Piece> updated = new ArrayList<>(getState(player).pieces());

		while (remaining > 0)
		{
			List<FillablePiece> active = fillable.stream().filter(entry -> entry.space() > 0).toList();
			if (active.isEmpty())
				break;

			int share = Math.max(1, remaining / active.size());
			boolean changed = false;
			for (FillablePiece entry : active)
			{
				if (remaining <= 0)
					break;

				int filled = Math.min(entry.space(), share);
				entry.fill(filled);
				remaining -= filled;
				changed = true;
			}
			if (!changed)
				break;
		}

		int filledTotal = fluid.getAmount() - remaining;
		if (filledTotal <= 0)
			return 0;

		for (FillablePiece entry : fillable)
		{
			updated.removeIf(piece -> piece.slot() == entry.piece().slot());
			updated.add(entry.toPiece(fluid));
		}

		player.setData(Registration.DataAttachmentsReg.ORGANIC_ARMOR, new OrganicArmorState(updated));
		rebuildArmor(player);
		sync(player, player);
		return filledTotal;
	}

	public static void rebuildArmor(LivingEntity entity)
	{
		AttributeInstance instance = entity.getAttribute(Attributes.ARMOR);
		if (instance == null)
			return;

		for (EquipmentSlot slot : EquipmentSlot.values())
			if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR)
				instance.removeModifier(getArmorModifierId(slot));

		OrganicArmorState state = getState(entity);
		for (OrganicArmorState.Piece piece : state.pieces())
		{
			int armor = getEffectiveArmor(entity.level(), piece);
			if (armor > 0)
				instance.addTransientModifier(new AttributeModifier(getArmorModifierId(piece.slot()), armor, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	public static void sync(ServerPlayer receiver, LivingEntity target)
	{
		NetworkEngine.sendToPlayer(receiver, new S2COrganicArmorSync(target.getUUID(), getState(target)));
	}

	private static ResourceLocation getArmorModifierId(EquipmentSlot slot)
	{
		return ARMOR_MODIFIER_BASE.withSuffix("_" + slot.getName() + "_armor");
	}

	private static List<FillablePiece> getFillablePieces(LivingEntity entity, FluidStack fluid)
	{
		List<FillablePiece> fillable = new ArrayList<>();
		for (OrganicArmorState.Piece piece : getState(entity).pieces())
		{
			OrganicArmorType type = getType(entity.level(), piece).orElse(null);
			if (type == null)
				continue;
			OrganicArmorType.PieceParams params = type.get(piece.slot()).orElse(null);
			if (params == null)
				continue;
			if (!OrganicArmorEffectHandler.hasFluidEffect(entity.level().registryAccess(), piece, fluid))
				continue;

			FluidStack current = piece.fluid();
			if (!current.isEmpty() && !FluidStack.isSameFluidSameComponents(current, fluid))
				continue;
			if (current.getAmount() >= params.capacity())
				continue;

			fillable.add(new FillablePiece(piece, params.capacity(), current.getAmount()));
		}
		return fillable;
	}

	private static class FillablePiece
	{
		private final OrganicArmorState.Piece piece;
		private final int capacity;
		private int amount;

		private FillablePiece(OrganicArmorState.Piece piece, int capacity, int amount)
		{
			this.piece = piece;
			this.capacity = capacity;
			this.amount = amount;
		}

		private OrganicArmorState.Piece piece()
		{
			return this.piece;
		}

		private int space()
		{
			return this.capacity - this.amount;
		}

		private void fill(int amount)
		{
			this.amount += amount;
		}

		private OrganicArmorState.Piece toPiece(FluidStack source)
		{
			if (this.amount <= 0)
				return new OrganicArmorState.Piece(this.piece.slot(), this.piece.typeId(), FluidStack.EMPTY);

			FluidStack filled = source.copy();
			filled.setAmount(this.amount);
			return new OrganicArmorState.Piece(this.piece.slot(), this.piece.typeId(), filled);
		}
	}

	private static Optional<OrganicArmorType.PieceParams> findParamsForSource(OrganicArmorType type, ItemStack sourceStack)
	{
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(sourceStack.getItem());
		return type.findBySourceArmor(itemId);
	}

	private static void returnSourceArmor(ServerPlayer player, OrganicArmorType.PieceParams params, EquipmentSlot slot)
	{
		Item item = BuiltInRegistries.ITEM.get(params.sourceArmor());
		ItemStack returned = new ItemStack(item);
		ItemStack equipped = player.getItemBySlot(slot);
		if (equipped.isEmpty())
		{
			player.setItemSlot(slot, returned);
			return;
		}

		if (!player.getInventory().add(returned))
			player.drop(returned, false);
	}
}
