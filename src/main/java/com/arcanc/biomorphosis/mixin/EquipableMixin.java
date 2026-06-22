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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (Equipable.class)
public interface EquipableMixin
{
	@Inject(method = "swapWithEquipmentSlot", at = @At("HEAD"), cancellable = true)
	private void blockOrganicArmorSlot(Item item,
	                                   Level level,
	                                   Player player,
	                                   InteractionHand hand,
	                                   CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir)
	{
		ItemStack stack = player.getItemInHand(hand);
		EquipmentSlot slot = player.getEquipmentSlotForItem(stack);
		if (GenomeHelper.hasArmorGene(player, slot))
			cir.setReturnValue(InteractionResultHolder.pass(stack));
	}
}
