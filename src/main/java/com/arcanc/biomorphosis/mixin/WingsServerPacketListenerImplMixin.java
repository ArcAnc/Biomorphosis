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
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class WingsServerPacketListenerImplMixin
{
	@WrapOperation (method = "handleMovePlayer", at = @At (value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isFallFlying()Z"))
	public boolean handleMovePlayer(ServerPlayer serverPlayer, Operation<Boolean> operation)
	{
		return operation.call(serverPlayer) || WingsEffectType.isFlying(serverPlayer);
	}
}
