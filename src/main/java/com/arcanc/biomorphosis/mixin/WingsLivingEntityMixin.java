/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;

import com.arcanc.biomorphosis.content.mutations.types.WingsEffectType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class WingsLivingEntityMixin
{
	@Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
	private void biomorphosis$reduceFlightHitbox(Pose pose, CallbackInfoReturnable<EntityDimensions> callback)
	{
		if ((Object) this instanceof Player player && WingsEffectType.isFlying(player) &&
				!WingsEffectType.isHovering(player) && !player.isInWaterOrBubble())
			callback.setReturnValue(callback.getReturnValue().scale(1.0F, 1.0F / 3.0F));
	}

	@Inject(method = "tickHeadTurn", at = @At("HEAD"), cancellable = true)
	protected void tickHeadTurn(float yRot, float animStep, CallbackInfoReturnable<Float> callback)
	{
		if (((Object) this) instanceof Player player && WingsEffectType.isFlying(player))
		{
			player.yBodyRot += Mth.wrapDegrees(yRot - player.yBodyRot) * 0.3F;
			float theta = Mth.clamp(Mth.wrapDegrees(player.getYRot() - player.yBodyRot), -50.0F, 50.0F);
			player.yBodyRot = player.getYRot() - theta;
			callback.setReturnValue(0.0F);
		}
	}
}
