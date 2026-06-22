/**
 * @author ArcAnc
 * Created at: 19.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;


import com.arcanc.biomorphosis.content.gui.slot.OrganicArmorSlotRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (AbstractContainerScreen.class)
public abstract class AbstractContainerScreeMixin
{
	@Shadow
	protected Slot hoveredSlot;

	@Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
	private void biomorphosis$renderOrganicArmorSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci)
	{
		if (!OrganicArmorSlotRenderer.shouldReplace(slot))
			return;

		OrganicArmorSlotRenderer.render(guiGraphics, slot);
		ci.cancel();
	}

	@Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
	private void biomorphosis$renderOrganicArmorTooltip(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci)
	{
		if (!OrganicArmorSlotRenderer.shouldReplace(this.hoveredSlot))
			return;

		OrganicArmorSlotRenderer.renderTooltip(guiGraphics, ((AbstractContainerScreen<?>)(Object)this).getMinecraft().font, this.hoveredSlot, x, y);
		ci.cancel();
	}
}
