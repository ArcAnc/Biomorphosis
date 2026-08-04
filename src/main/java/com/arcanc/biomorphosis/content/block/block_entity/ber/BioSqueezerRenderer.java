/**
 * @author ArcAnc
 * Created at: 30.11.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;


import com.arcanc.biomorphosis.content.block.block_entity.BioSqueezer;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.arcanc.pulselib.content.animatable.PAnimationController;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.data.MolangParser;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class BioSqueezerRenderer extends PBlockRenderer<BioSqueezer>
{
	private static final ResourceLocation TEXTURE = Database.rl("block/squeezer/0");
	
	public BioSqueezerRenderer(final BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("squeezer")).
					build(),
				PRenderTypes.RenderTypeProvider :: trianglesSolid);
	}
	
	@Override
	protected void perBoneSubmit(BioSqueezer animatable, PoseStack poseStack, PBakedBone bone, Collection<PAnimationController<BioSqueezer>> animationControllers, Map<PAnimationController<BioSqueezer>, MolangParser.Context> molangContexts, Function<ResourceLocation, RenderType> renderType, int packedColor, int packedLight, int packedOverlay, float partialTick)
	{
		if (!bone.name().equals("main"))
		{
			super.perBoneSubmit(animatable, poseStack, bone, animationControllers, molangContexts, renderType, packedColor, packedLight, packedOverlay, partialTick);
			return;
		}
		poseStack.pushPose();
		float percent = FluidHelper.getFluidHandler(animatable).
				map(handler -> handler.getFluidInTank(2).getAmount() / (float) handler.getTankCapacity(2)).orElse(0.0f);
		//FIXME: проверить название модели и убедиться что скейл стоит правильный
		poseStack.scale(1, percent, 1);
		super.perBoneSubmit(animatable, poseStack, bone, animationControllers, molangContexts, renderType, packedColor, packedLight, packedOverlay, partialTick);
		poseStack.popPose();
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}
