/**
 * @author ArcAnc
 * Created at: 26.07.2026
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
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu>
{
	@Shadow private static CreativeModeTab selectedTab;
	@Unique private OrganicArmorInventoryButton biomorphosis$organicArmorButton;

	public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void biomorphosis$addOrganicArmorButton(CallbackInfo ci)
	{
		this.biomorphosis$organicArmorButton = this.addRenderableWidget(new OrganicArmorInventoryButton(
				this.leftPos + 135,
				this.topPos + 32,
				button -> NetworkEngine.sendToServer(new C2SOpenOrganicArmorInventory()),
				Component.translatable(Database.GUI.OrganicArmorInventory.BUTTON)));
		this.biomorphosis$updateOrganicArmorButtonVisibility();
	}

	@Inject(method = "selectTab", at = @At("TAIL"))
	private void biomorphosis$updateOrganicArmorButtonVisibility(CreativeModeTab tab, CallbackInfo ci)
	{
		this.biomorphosis$updateOrganicArmorButtonVisibility();
	}

	@Unique
	private void biomorphosis$updateOrganicArmorButtonVisibility()
	{
		if (this.biomorphosis$organicArmorButton != null)
			this.biomorphosis$organicArmorButton.visible = selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
	}
}
