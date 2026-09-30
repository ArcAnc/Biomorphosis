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
import com.arcanc.biomorphosis.content.entity.hitbox.OBB;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class OrientedMobMixin
{
    @Shadow @Final private static double DEFAULT_ATTACK_REACH;

    @WrapOperation(method = "isWithinMeleeAttackRange", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;intersects(Lnet/minecraft/world/phys/AABB;)Z"), require = 1)
    private boolean biomorphosis$melee(AABB attack, AABB targetBox, Operation<Boolean> original, LivingEntity target)
    {
        if (!(this instanceof IOrientedHitbox) && !(target instanceof IOrientedHitbox))
            return original.call(attack, targetBox);
        OBB attacking = (Object)this instanceof IOrientedHitbox oriented ?
		        oriented.getOrientedHitbox().inflate(DEFAULT_ATTACK_REACH, 0, DEFAULT_ATTACK_REACH) :
		        OBB.fromAABB(attack);
        return target instanceof IOrientedHitbox oriented ?
		        attacking.intersects(oriented.getOrientedHitbox()) :
		        attacking.intersects(targetBox);
    }
}
