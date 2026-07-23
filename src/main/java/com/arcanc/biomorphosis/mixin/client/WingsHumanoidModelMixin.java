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
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidModel.class, PlayerModel.class})
public class WingsHumanoidModelMixin
{
	@Inject(method = "setupAnim", at = @At("TAIL"))
	private void biomorphosis$animateFlyingModel(LivingEntity entity, float limbSwing, float limbSwingAmount,
	                                              float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
	{
		HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
		WingsClient.applyFlightModelPose(entity, model, ageInTicks, headPitch);
	}
}
