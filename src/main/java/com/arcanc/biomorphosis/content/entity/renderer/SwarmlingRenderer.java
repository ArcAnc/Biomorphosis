/**
 * @author ArcAnc
 * Created at: 16.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;


import com.arcanc.biomorphosis.content.entity.Swarmling;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class SwarmlingRenderer extends PEntityRenderer<Swarmling>
{
	private static final ResourceLocation TEXTURE = Database.rl("entity/swarmling/0");
	
	public SwarmlingRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("swarmling")).
						build(),
				PRenderTypes.RenderTypeProvider :: trianglesCutout);
	}
	
	@Override
	public void trueSubmit(PoseStack poseStack, Swarmling animatable, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick, @Nullable Object... additionalData)
	{
		if (!animatable.isBaby())
		{
			poseStack.pushPose();
			poseStack.scale(1.9f, 1.9f, 1.9f);
			super.trueSubmit(poseStack, animatable, renderType, bufferSource, packedLight, packedOverlay, partialTick, additionalData);
			poseStack.popPose();
		}
		else
			super.trueSubmit(poseStack, animatable, renderType, bufferSource, packedLight, packedOverlay, partialTick, additionalData);
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}
