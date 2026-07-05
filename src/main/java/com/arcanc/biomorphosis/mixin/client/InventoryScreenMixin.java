/**
 * @author ArcAnc
 * Created at: 27.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;


import com.arcanc.biomorphosis.content.gui.component.OrganicArmorInventoryButton;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.C2SOpenOrganicArmorInventory;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu>
{
	public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void biomorphosis$addOrganicArmorButton(CallbackInfo ci)
	{
		this.addRenderableWidget(new OrganicArmorInventoryButton(
				this.leftPos + 151,
				this.topPos + 64,
				button -> NetworkEngine.sendToServer(new C2SOpenOrganicArmorInventory()),
				Component.translatable(Database.GUI.OrganicArmorInventory.BUTTON)));
	}
}
