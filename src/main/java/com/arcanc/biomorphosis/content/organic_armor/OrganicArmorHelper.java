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
		if (!canCreateFrom(player.registryAccess(), typeKey, sourceStack))
			return false;
		if (hasArmor(player, type.slot()))
			return false;

		OrganicArmorState state = getState(player).with(new OrganicArmorState.Piece(type.slot(), typeKey.location(), FluidStack.EMPTY));
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

		OrganicArmorState state = getState(player).without(slot);
		player.setData(Registration.DataAttachmentsReg.ORGANIC_ARMOR, state);
		OrganicArmorEffectHandler.removeEffectData(player, slot);
		rebuildArmor(player);
		returnSourceArmor(player, type, slot);
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
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(sourceStack.getItem());
		return type.sourceArmor().equals(itemId);
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
				map(type -> piece.hasFluid() ? type.armor() : type.drainedArmor()).
				orElse(0);
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

	private static void returnSourceArmor(ServerPlayer player, OrganicArmorType type, EquipmentSlot slot)
	{
		Item item = BuiltInRegistries.ITEM.get(type.sourceArmor());
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
