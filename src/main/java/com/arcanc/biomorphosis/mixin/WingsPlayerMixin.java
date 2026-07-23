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
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class WingsPlayerMixin
{
	@Redirect(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isShiftKeyDown()Z"))
	private boolean biomorphosis$keepEyeHeightWhileGliding(Player player)
	{
		return !(WingsEffectType.hasWings(player) && player.getDeltaMovement().y() < -0.5D)
				&& player.isShiftKeyDown();
	}
}
