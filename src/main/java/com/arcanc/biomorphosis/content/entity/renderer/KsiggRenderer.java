/**
 * @author ArcAnc
 * Created at: 10.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;

import com.arcanc.biomorphosis.content.entity.Ksigg;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KsiggRenderer extends GeoEntityRenderer<Ksigg>
{
    public KsiggRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultedEntityGeoModel<>(Database.rl("ksigg"), true));
    }

    @Override
    public @Nullable RenderType getRenderType(Ksigg animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, @NotNull Ksigg animatable, @NotNull BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
    {
        if (animatable.isBaby())
            model.getBone("all").ifPresent(geoBone -> geoBone.updateScale(0.5f, 0.5f, 0.5f));
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
    }
}
