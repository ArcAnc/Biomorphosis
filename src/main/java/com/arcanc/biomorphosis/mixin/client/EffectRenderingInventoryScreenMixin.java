/**
 * @author ArcAnc
 * Created at: 26.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin
{
	@Inject(method = "render", at = @At("TAIL"))
	private void biomorphosis$renderAcidStackCount(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
	{
		EffectRenderingInventoryScreen<?> screen = (EffectRenderingInventoryScreen<?>)(Object)this;
		AbstractContainerScreenAccessor containerScreen = (AbstractContainerScreenAccessor)this;
		int stackCount = RenderHelper.clientPlayer().getData(Registration.DataAttachmentsReg.ACID_STACKS).stacks().size();
		if (stackCount == 0 || !screen.canSeeEffects())
			return;

		int effectPanelX = containerScreen.biomorphosis$getLeftPos() + containerScreen.biomorphosis$getImageWidth() + 2;
		int availableWidth = guiGraphics.guiWidth() - effectPanelX;
		if (availableWidth < 32)
			return;

		boolean expanded = availableWidth >= 120;
		var panelEvent = ClientHooks.onScreenPotionSize(screen, availableWidth, !expanded, effectPanelX);
		if (panelEvent.isCanceled())
			return;

		expanded = !panelEvent.isCompact();
		effectPanelX = panelEvent.getHorizontalOffset();
		List<MobEffectInstance> effects = RenderHelper.clientPlayer().getActiveEffects().stream().
				filter(ClientHooks :: shouldRenderEffect).
				sorted().toList();
		int yOffset = effects.size() > 5 ? 132 / (effects.size() - 1) : 33;
		int effectY = containerScreen.biomorphosis$getTopPos();

		for (MobEffectInstance effect : effects)
		{
			if (effect.getEffect().value() == Registration.EffectReg.ACID.get())
			{
				int iconX = effectPanelX + (expanded ? 6 : 7);
				String stacks = Integer.toString(stackCount);
				Font font = RenderHelper.mc().font;
				guiGraphics.drawString(font, stacks, iconX + 17 - font.width(stacks), effectY + 18, 0xFFFFFF, true);
				return;
			}
			effectY += yOffset;
		}
	}
}
