/**
 * @author ArcAnc
 * Created at: 27.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.container_menu;


import com.arcanc.biomorphosis.content.gui.slot.OrganicArmorSlotReplacement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OrganicArmorInventoryMenu extends BioContainerMenu
{
	private static final int ARMOR_SLOT_COUNT = 4;
	private static final int PLAYER_INV_START = ARMOR_SLOT_COUNT;
	private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
	private static final int HOTBAR_START = PLAYER_INV_END;
	private static final int HOTBAR_END = HOTBAR_START + 9;

	public static OrganicArmorInventoryMenu makeServer(MenuType<?> type, int id, Inventory playerInv, Player player)
	{
		return new OrganicArmorInventoryMenu(genericCtx(type, id), playerInv);
	}

	public static OrganicArmorInventoryMenu makeClient(MenuType<?> type, int id, Inventory playerInv, BlockPos ignored)
	{
		return new OrganicArmorInventoryMenu(clientCtx(type, id, ContextType.GENERIC), playerInv);
	}

	private OrganicArmorInventoryMenu(MenuContext ctx, Inventory playerInventory)
	{
		super(ctx);

		Player player = playerInventory.player;
		addArmorSlot(playerInventory, player, EquipmentSlot.HEAD, 39, 45, 16, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
		addArmorSlot(playerInventory, player, EquipmentSlot.CHEST, 38, 45, 48, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
		addArmorSlot(playerInventory, player, EquipmentSlot.LEGS, 37, 134, 16, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
		addArmorSlot(playerInventory, player, EquipmentSlot.FEET, 36, 134, 48, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
		this.ownSlotCount = ARMOR_SLOT_COUNT;

		this.addStandardInventorySlots(playerInventory, 17, 79);
	}

	private void addArmorSlot(Inventory inventory, Player player, EquipmentSlot slot, int slotIndex, int x, int y, ResourceLocation emptyIcon)
	{
		this.addSlot(new OrganicArmorSlotReplacement(inventory, player, slot, slotIndex, x, y, emptyIcon));
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		Slot slot = this.slots.get(index);
		if (!slot.hasItem())
			return ItemStack.EMPTY;

		ItemStack original = slot.getItem();
		ItemStack result = original.copy();

		if (index < ARMOR_SLOT_COUNT)
		{
			if (!this.moveItemStackTo(original, PLAYER_INV_START, HOTBAR_END, true))
				return ItemStack.EMPTY;
		}
		else
		{
			EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(original);
			int armorSlotIndex = getArmorMenuIndex(equipmentSlot);
			if (armorSlotIndex >= 0 && this.slots.get(armorSlotIndex).mayPlace(original) && !this.slots.get(armorSlotIndex).hasItem())
			{
				if (!this.moveItemStackTo(original, armorSlotIndex, armorSlotIndex + 1, false))
					return ItemStack.EMPTY;
			}
			else if (index >= PLAYER_INV_START && index < PLAYER_INV_END)
			{
				if (!this.moveItemStackTo(original, HOTBAR_START, HOTBAR_END, false))
					return ItemStack.EMPTY;
			}
			else if (index >= HOTBAR_START && index < HOTBAR_END)
			{
				if (!this.moveItemStackTo(original, PLAYER_INV_START, PLAYER_INV_END, false))
					return ItemStack.EMPTY;
			}
		}

		if (original.isEmpty())
			slot.set(ItemStack.EMPTY);
		else
			slot.setChanged();

		return result;
	}

	private static int getArmorMenuIndex(EquipmentSlot slot)
	{
		return switch (slot)
		{
			case HEAD -> 0;
			case CHEST -> 1;
			case LEGS -> 2;
			case FEET -> 3;
			default -> -1;
		};
	}

	@Override
	protected void handleMessage(ServerPlayer player, CompoundTag tag)
	{
	}
}
