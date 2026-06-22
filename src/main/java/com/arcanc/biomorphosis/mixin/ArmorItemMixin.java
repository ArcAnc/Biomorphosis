/**
 * @author ArcAnc
 * Created at: 18.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;


import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin (ArmorItem.class)
public class ArmorItemMixin
{
	@WrapOperation(method = "dispenseArmor",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;canEquip(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;)Z"),
			require = 1)
	private static boolean blockOrganicArmorSlotDispense(ItemStack stack,
	                                                     EquipmentSlot slot,
	                                                     LivingEntity entity,
	                                                     Operation<Boolean> original)
	{
		return !GenomeHelper.hasArmorGene(entity, slot) && original.call(stack, slot, entity);
	}
}
