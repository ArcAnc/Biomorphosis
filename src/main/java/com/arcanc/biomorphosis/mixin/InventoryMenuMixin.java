/**
 * @author ArcAnc
 * Created at: 18.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;


import com.arcanc.biomorphosis.content.gui.slot.OrganicArmorSlotReplacement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin (InventoryMenu.class)
public class InventoryMenuMixin
{
	@WrapOperation(method = "<init>",
			at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;IIILnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/inventory/ArmorSlot;"),
			require = 1)
	private ArmorSlot replaceArmorSlot(Container container,
	                                   LivingEntity owner,
	                                   EquipmentSlot slot,
	                                   int slotIndex,
	                                   int x,
	                                   int y,
	                                   ResourceLocation emptyIcon,
	                                   Operation<ArmorSlot> original)
	{
		return new OrganicArmorSlotReplacement(container, owner, slot, slotIndex, x, y, emptyIcon);
	}
}
