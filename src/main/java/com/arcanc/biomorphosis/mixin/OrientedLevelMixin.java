/**
 * @author ArcAnc
 * Created at: 29.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;

import com.arcanc.biomorphosis.content.entity.hitbox.IOrientedQueryBounds;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class OrientedLevelMixin implements IOrientedQueryBounds
{
    @Unique private double biomorphosis$orientedReach;

    @Override
    public double biomorphosis$orientedReach()
    {
		return this.biomorphosis$orientedReach;
	}

    @Override
    public void biomorphosis$includeOrientedReach(double reach)
    {
        this.biomorphosis$orientedReach = Math.max(this.biomorphosis$orientedReach, reach);
    }

    @WrapOperation(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/entity/LevelEntityGetter;get(Lnet/minecraft/world/phys/AABB;Ljava/util/function/Consumer;)V"), require = 1)
    private void biomorphosis$sections(LevelEntityGetter<Entity> getter,
                                       AABB area,
                                       Consumer<Entity> consumer,
                                       Operation<Void> original)
    {
        if (this.biomorphosis$orientedReach == 0)
			original.call(getter, area, consumer);
        else
			original.call(getter, area.inflate(this.biomorphosis$orientedReach), (Consumer<Entity>) entity ->
			{
            if (entity.getBoundingBox().intersects(area))
				consumer.accept(entity);
			});
    }

    @WrapOperation(method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;I)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/entity/LevelEntityGetter;get(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V"), require = 1)
    private <T extends Entity> void biomorphosis$typedSections(LevelEntityGetter<Entity> getter,
                                                               EntityTypeTest<Entity, T> type,
                                                               AABB area,
                                                               AbortableIterationConsumer<T> consumer,
                                                               Operation<Void> original)
    {
        if (biomorphosis$orientedReach == 0)
			original.call(getter, type, area, consumer);
        else
			original.call(getter, type, area.inflate(biomorphosis$orientedReach),
					(AbortableIterationConsumer<T>) entity ->
                entity.getBoundingBox().intersects(area) ?
		                consumer.accept(entity) :
		                AbortableIterationConsumer.Continuation.CONTINUE);
    }
}
