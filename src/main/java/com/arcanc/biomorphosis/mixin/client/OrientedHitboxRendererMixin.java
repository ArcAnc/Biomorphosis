package com.arcanc.biomorphosis.mixin.client;

import com.arcanc.biomorphosis.content.entity.hitbox.IOrientedHitbox;
import com.arcanc.biomorphosis.content.entity.hitbox.client.OBBDebugRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class OrientedHitboxRendererMixin
{
    @Inject(method = "renderHitbox", at = @At("HEAD"), require = 1)
    private static void biomorphosis$tickOrigin(PoseStack poses, VertexConsumer buffer, Entity entity,
                                                float partialTick, float red, float green, float blue, CallbackInfo ci)
    {
        if (entity instanceof IOrientedHitbox)
        {
            // Dispatcher translated to interpolated position. Undo interpolation for BOTH boxes.
            poses.pushPose();
            Vec3 offset = entity.position().subtract(new Vec3(
                    Mth.lerp(partialTick, entity.xOld, entity.getX()),
                    Mth.lerp(partialTick, entity.yOld, entity.getY()),
                    Mth.lerp(partialTick, entity.zOld, entity.getZ())));
            poses.translate(offset.x, offset.y, offset.z);
        }
    }

    @Inject(method = "renderHitbox", at = @At("RETURN"), require = 1)
    private static void biomorphosis$render(PoseStack poses, VertexConsumer buffer, Entity entity,
                                            float partialTick, float red, float green, float blue, CallbackInfo ci)
    {
        if (entity instanceof IOrientedHitbox oriented)
        {
            OBBDebugRenderer.render(poses, buffer, oriented.getOrientedHitbox(), entity.position());
            poses.popPose();
        }
    }
}
