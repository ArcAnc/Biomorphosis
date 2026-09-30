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
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ProjectileUtil.class)
public abstract class OrientedProjectileMixin
{
    @WrapOperation(method = {
            "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;"
    }, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;clip(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Ljava/util/Optional;"), require = 2)
    private static Optional<Vec3> biomorphosis$clip(AABB box,
                                                    Vec3 start,
                                                    Vec3 end,
                                                    Operation<Optional<Vec3>> original,
                                                    @Local(ordinal = 2) Entity candidate)
    {
        return candidate instanceof IOrientedHitbox oriented ?
		        oriented.getOrientedHitbox().clip(start, end) :
		        original.call(box, start, end);
    }

    @WrapOperation(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;contains(Lnet/minecraft/world/phys/Vec3;)Z"), require = 1)
    private static boolean biomorphosis$contains(AABB box,
                                                 Vec3 point,
                                                 Operation<Boolean> original,
                                                 @Local(ordinal = 2) Entity candidate)
    {
        return candidate instanceof IOrientedHitbox oriented ?
		        oriented.getOrientedHitbox().contains(point) :
		        original.call(box, point);
    }

    @WrapOperation(method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/EntityHitResult;"), require = 1)
    private static EntityHitResult biomorphosis$contactPoint(Entity target,
                                                             Operation<EntityHitResult> original,
                                                             @Local(argsOnly = true, ordinal = 0) Vec3 start,
                                                             @Local(argsOnly = true, ordinal = 1) Vec3 end)
    {
        return target instanceof IOrientedHitbox oriented ?
		        oriented.getOrientedHitbox().clip(start, end).
				        map(point -> new EntityHitResult(target, point)).
				        orElseGet(() -> original.call(target)) :
		        original.call(target);
    }
}
