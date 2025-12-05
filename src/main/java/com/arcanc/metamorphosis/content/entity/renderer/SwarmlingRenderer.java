/**
 * @author ArcAnc
 * Created at: 16.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity.renderer;


import com.arcanc.metamorphosis.content.entity.Swarmling;
import com.arcanc.metamorphosis.util.Database;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SwarmlingRenderer extends GeoEntityRenderer<Swarmling>
{
	public SwarmlingRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx, new DefaultedEntityGeoModel<>(Database.rl("swarmling"), true));
	}

	@Override
	public void preRender(PoseStack poseStack, @NotNull Swarmling animatable, @NotNull BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
	{
		if (!animatable.isBaby())
			model.getBone("all_2").ifPresent(geoBone -> geoBone.updateScale(1.9f, 1.9f, 1.9f));
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
	}
}
