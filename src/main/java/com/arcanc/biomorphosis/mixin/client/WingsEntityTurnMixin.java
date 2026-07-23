/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;

import com.arcanc.biomorphosis.content.mutations.types.WingsEffectType;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class WingsEntityTurnMixin
{
	@Inject(method = "turn", at = @At("TAIL"))
	private void biomorphosis$keepFlyingBodyAligned(double deltaYaw, double deltaPitch, CallbackInfo callback)
	{
		if (!((Object) this instanceof Player player) || !WingsEffectType.isFlying(player) ||
				(WingsEffectType.isHovering(player) && player.getSwimAmount(1.0F) <= 0.0F))
			return;
		
		
		float theta = Mth.wrapDegrees(player.getYRot() - player.yBodyRot);
		if (theta < -50.0F || theta > 50.0F)
		{
			float yawDelta = (float) deltaYaw * 0.15F;

			player.yBodyRot += yawDelta;
			player.yBodyRotO += yawDelta;
		}
	}
}
