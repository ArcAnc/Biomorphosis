/**
 * @author ArcAnc
 * Created at: 10.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;

import com.arcanc.biomorphosis.content.sky.BiomeSkyboxes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
	@Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
	private void hideCloudsInCustomSkyboxBiome(PoseStack poseStack,
	                                           Matrix4f frustumMatrix,
	                                           Matrix4f projectionMatrix,
	                                           float partialTick,
	                                           double camX,
	                                           double camY,
	                                           double camZ,
	                                           CallbackInfo ci)
	{
		if (BiomeSkyboxes.shouldHideClouds())
			ci.cancel();
	}
}
