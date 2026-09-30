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
import com.arcanc.biomorphosis.content.entity.hitbox.IOrientedQueryBounds;
import com.arcanc.biomorphosis.content.entity.hitbox.OrientedHitboxes;
import com.arcanc.biomorphosis.util.helper.OBBHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class OrientedEntityMixin
{
    @ModifyVariable(method = "setBoundingBox", at = @At("HEAD"), argsOnly = true, require = 1)
    private AABB biomorphosis$enclosingBox(AABB vanilla)
    {
        if (!((Object)this instanceof IOrientedHitbox oriented))
			return vanilla;
        Entity self = (Entity)(Object)this;
        AABB box = oriented.getOrientedHitbox().enclosingAABB();
        if (self.level() instanceof IOrientedQueryBounds bounds)
        {
            double reach = Math.max(Math.max(Math.abs(box.minX - self.getX()), Math.abs(box.maxX - self.getX())),
                    Math.max(Math.max(Math.abs(box.minY - self.getY()), Math.abs(box.maxY - self.getY())),
                            Math.max(Math.abs(box.minZ - self.getZ()), Math.abs(box.maxZ - self.getZ()))));
            bounds.biomorphosis$includeOrientedReach(reach);
        }
        return box;
    }

    @Inject(method = {"setYRot", "setPosRaw"}, at = @At("TAIL"), require = 2)
    private void biomorphosis$updateBroadPhase(CallbackInfo ci)
    {
        if ((Object)this instanceof IOrientedHitbox oriented)
            ((Entity)(Object)this).setBoundingBox(oriented.getOrientedHitbox().enclosingAABB());
    }

    @Inject(method = "collideBoundingBox", at = @At("HEAD"), cancellable = true, require = 1)
    private static void biomorphosis$resolve(Entity entity, Vec3 movement, AABB box, Level level,
                                             List<VoxelShape> hits, CallbackInfoReturnable<Vec3> cir)
    {
        if (entity instanceof IOrientedHitbox || (!hits.isEmpty() &&
		        OBBHelper.hasOrientedCollider(entity, level, box.expandTowards(movement))))
            cir.setReturnValue(OBBHelper.collide(entity, movement, box, level, hits));
    }

    @WrapOperation(method = "collide", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"), require = 1)
    private Vec3 biomorphosis$step(Vec3 movement, AABB box, List<VoxelShape> shapes, Operation<Vec3> original)
    {
        Entity entity = (Entity)(Object)this;
        return entity instanceof IOrientedHitbox || OBBHelper.hasOrientedCollider(entity, entity.level(), box.expandTowards(movement)) ?
		        OBBHelper.collide(entity, movement, box, entity.level(), List.of()) :
		        original.call(movement, box, shapes);
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true, require = 1)
    private void biomorphosis$pushOnlyOnContact(Entity other, CallbackInfo ci)
    {
        if (!OrientedHitboxes.intersects((Entity)(Object)this, other))
			ci.cancel();
    }
}
