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
import com.arcanc.biomorphosis.content.entity.hitbox.OrientedHitboxes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class OrientedLivingEntityMixin
{
    @Inject(method = {"tick", "setYBodyRot"}, at = @At("TAIL"), require = 2)
    private void biomorphosis$updateBodyBounds(CallbackInfo ci)
    {
        if ((Object)this instanceof IOrientedHitbox oriented)
        {
            Entity self = (Entity)(Object)this;
            AABB bounds = oriented.getOrientedHitbox().enclosingAABB();
            if (!bounds.equals(self.getBoundingBox()))
                self.setBoundingBox(bounds);
        }
    }

    @ModifyExpressionValue(method = "pushEntities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"), require = 1)
    private List<Entity> biomorphosis$contacts(List<Entity> candidates)
    {
        Entity self = (Entity)(Object)this;
        return candidates.stream().filter(other -> OrientedHitboxes.
		        intersects(self, other)).toList();
    }
}
