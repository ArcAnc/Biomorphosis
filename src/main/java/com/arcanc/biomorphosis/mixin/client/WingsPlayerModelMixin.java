/**
 * @author ArcAnc
 * Created at: 03.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;

import com.arcanc.biomorphosis.content.mutations.wings.client.WingsClient;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class WingsPlayerModelMixin
{
	@Inject(method = "setupAnim", at = @At("TAIL"))
	private void biomorphosis$applyFlightHeadPose(LivingEntity entity, float limbSwing, float limbSwingAmount,
	                                              float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
	{
		if ((Object) this instanceof PlayerModel<?> model && entity instanceof Player player)
			WingsClient.applyFlightHeadPose(player, model, ageInTicks, headPitch);
	}
}
