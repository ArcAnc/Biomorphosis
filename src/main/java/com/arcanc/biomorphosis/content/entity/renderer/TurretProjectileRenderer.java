/**
 * @author ArcAnc
 * Created at: 24.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;


import com.arcanc.biomorphosis.content.entity.TurretProjectile;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TurretProjectileRenderer extends GeoEntityRenderer<TurretProjectile>
{
	public TurretProjectileRenderer(EntityRendererProvider.Context context)
	{
		super(context, new DefaultedEntityGeoModel<>(Database.rl("projectile_turret")));
	}
	
	@Override
	public void renderRecursively(PoseStack poseStack, @NotNull TurretProjectile animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
	{
		if (animatable.getEffect() != null)
			renderColor = animatable.getEffect().getColor();
		super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
	}
	
	@Override
	public @Nullable RenderType getRenderType(TurretProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
	{
		return RenderType.entityTranslucent(texture);
	}
}
