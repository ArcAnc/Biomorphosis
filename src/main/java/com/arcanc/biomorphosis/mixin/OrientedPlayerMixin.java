/**
 * @author ArcAnc
 * Created at: 29.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;

import com.arcanc.biomorphosis.content.entity.hitbox.IOrientedHitbox;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)
public abstract class OrientedPlayerMixin
{
    @Inject(method = "canInteractWithEntity(Lnet/minecraft/world/entity/Entity;D)Z", at = @At("HEAD"), cancellable = true, require = 1)
    private void biomorphosis$reach(Entity target, double margin, CallbackInfoReturnable<Boolean> cir)
    {
        if (target instanceof IOrientedHitbox oriented)
        {
            Player player = (Player)(Object)this;
            double reach = player.entityInteractionRange() + margin;
            cir.setReturnValue(!target.isRemoved() &&
		            oriented.getOrientedHitbox().distanceToSqr(player.getEyePosition()) < reach * reach);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true, require = 1)
    private void biomorphosis$attack(Entity target, CallbackInfo ci)
    {
        if (target instanceof IOrientedHitbox oriented)
        {
            Player player = (Player)(Object)this;
            Vec3 start = player.getEyePosition();
            double reach = player.entityInteractionRange() + (player.level().isClientSide ? 0 : 1);
            if (oriented.getOrientedHitbox().
		            clip(start, start.add(player.getViewVector(1).scale(reach))).isEmpty())
                ci.cancel();
        }
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"), require = 1)
    private <T extends Entity> List<T> biomorphosis$sweepCandidates(Level level, Class<T> type, AABB area, Operation<List<T>> original)
    {
        return original.call(level, type, area).stream()
                .filter(e -> !(e instanceof IOrientedHitbox oriented) || oriented.getOrientedHitbox().intersects(area)).toList();
    }
}
