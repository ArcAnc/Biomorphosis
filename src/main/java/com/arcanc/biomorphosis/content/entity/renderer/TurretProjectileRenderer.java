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
import com.arcanc.pulselib.content.animatable.PAnimationController;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.renderer.PEntityRenderLayer;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.data.MolangParser;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TurretProjectileRenderer extends PEntityRenderer<TurretProjectile>
{
	private static final ResourceLocation TEXTURE = Database.rl("entity/projectile_turret/0");
	
	public TurretProjectileRenderer(EntityRendererProvider.Context context)
	{
		super(context, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("projectile_turret")).
						build(),
				PRenderTypes.RenderTypeProvider :: trianglesTranslucent);
	}
	
	@Override
	protected void perBoneSubmit(TurretProjectile animatable,
	                             PoseStack poseStack,
	                             PBakedBone bone,
	                             Collection<PAnimationController<TurretProjectile>> pAnimationControllers,
	                             PModelData data,
	                             Function<ResourceLocation, RenderType> renderType,
	                             int packedColor,
	                             int packedLight,
	                             int packedOverlay,
	                             float partialTick,
	                             @Nullable HeadRotation headRotation,
	                             @Nullable PEntityRenderLayer<TurretProjectile> renderLayer,
	                             @Nullable Map<String, Matrix4f> entityBonePoses,
	                             @Nullable Matrix4f layerTransform,
	                             @Nullable List<DeferredLayerSubmit> deferredLayers,
	                             Map<PAnimationController<TurretProjectile>, MolangParser.Context> molangContexts)
	{
		if (animatable.getEffect() != null)
			packedColor = animatable.getEffect().getColor();
		super.perBoneSubmit(animatable,
				poseStack,
				bone,
				pAnimationControllers,
				data,
				renderType,
				packedColor,
				packedLight,
				packedOverlay,
				partialTick,
				headRotation,
				renderLayer,
				entityBonePoses,
				layerTransform,
				deferredLayers,
				molangContexts);
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}
