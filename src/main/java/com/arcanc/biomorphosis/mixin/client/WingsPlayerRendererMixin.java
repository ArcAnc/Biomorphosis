/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;

import com.arcanc.biomorphosis.content.mutations.wings.client.WingsClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class WingsPlayerRendererMixin
{
	@Inject(method = "setupRotations", at = @At("TAIL"))
	private void biomorphosis$applyFlightPitchAndRoll(AbstractClientPlayer player, PoseStack poseStack, float bob,
	                                                 float yBodyRot, float partialTick, float scale, CallbackInfo callback)
	{
		WingsClient.applyFlightModelRotation(player, poseStack, partialTick);
	}
}
