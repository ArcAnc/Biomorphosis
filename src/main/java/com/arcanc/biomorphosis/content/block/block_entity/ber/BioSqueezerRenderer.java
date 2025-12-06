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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BioSqueezerRenderer extends GeoBlockRenderer<BioSqueezer>
{
	public BioSqueezerRenderer(final BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultedBlockGeoModel<>(Database.rl("squeezer")));
	}
	
	@Override
	public void preRender(PoseStack poseStack, BioSqueezer animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
	{
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
		float percent = FluidHelper.getFluidHandler(animatable).
				map(handler -> handler.getFluidInTank(2).getAmount() / (float) handler.getTankCapacity(2)).orElse(0.0f);
		//FIXME: проверить название модели и убедиться что скейл стоит правильный
		model.getBone("main").ifPresent(geoBone -> geoBone.setScaleY(percent));
	}
}
