/**
 * @author ArcAnc
 * Created at: 18.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.slot;


import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OrganicArmorSlotReplacement extends ArmorSlot
{
	public static final ResourceLocation BLOCKED_SLOT = Database.rl("slot/blocked");
	
	private boolean isActive = true;
	
	public OrganicArmorSlotReplacement(Container container,
	                                   LivingEntity owner,
	                                   EquipmentSlot slot,
	                                   int slotIndex,
	                                   int x,
	                                   int y,
	                                   @Nullable ResourceLocation emptyIcon)
	{
		super(container, owner, slot, slotIndex, x, y, emptyIcon);
	}
	
	@Override
	public boolean isActive()
	{
		return this.isActive;
	}
	
	public void setActive(boolean active)
	{
		this.isActive = active;
	}
	
	public LivingEntity getOwner()
	{
		return this.owner;
	}

	public EquipmentSlot getEquipmentSlot()
	{
		return this.slot;
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		if (OrganicArmorHelper.hasArmor(this.owner, this.slot))
			return false;
		return super.mayPlace(stack);
	}
	
	@Override
	public boolean mayPickup(Player player)
	{
		if (OrganicArmorHelper.hasArmor(player, this.slot))
			return false;
		return super.mayPickup(player);
	}
	
	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
	{
		if (OrganicArmorHelper.hasArmor(this.owner, this.slot))
			return Pair.of(InventoryMenu.BLOCK_ATLAS, BLOCKED_SLOT);
		return super.getNoItemIcon();
	}
}
