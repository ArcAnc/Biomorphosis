/**
 * @author ArcAnc
 * Created at: 14.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;


import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.definition.BlockStateMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.model.obj.MorpherBaseObj;
import com.arcanc.biomorphosis.util.model.obj.MorpherEggObj;
import com.arcanc.biomorphosis.util.model.obj.ObjRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MultiblockMorpherRenderer extends GeoBlockRenderer<MultiblockMorpher>
{

	private final MorpherBaseObj baseModel = new MorpherBaseObj(Database.rl("textures/block/morpher.png"));
	private final MorpherEggObj eggModel = new MorpherEggObj(Database.rl("textures/block/morpher.png"));

	public MultiblockMorpherRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultedBlockGeoModel<>(Database.rl("morpher")));
	}

	@Override
	public void preRender(PoseStack poseStack, MultiblockMorpher animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
	{
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);

		BlockState state = animatable.getBlockState();

		poseStack.pushPose();

		if ((state.getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING &&
			animatable.isPreparationPhase()) ||
			state.getValue(MultiblockPartBlock.STATE) != MultiblockState.MORPHING)
			this.baseModel.render(poseStack, ObjRenderTypes :: trianglesTranslucent, bufferSource, packedOverlay, packedLight, renderColor);
		else
		{
			IMultiblockDefinition definition = animatable.getDefinition().get();
			BlockStateMap map = definition.getStructure(animatable.getLevel(), animatable.getBlockPos());

			/*FIXME: добавить плавное изменение размера. Сейчас оно слегка дёрганное*/

			float value = ((float) animatable.getMorphProgress() / map.getStates().size());
			BlockPos maxSize = map.getSize();
			int maxScale = Math.max(maxSize.getX(), Math.max(maxSize.getY(), maxSize.getZ()));
			value = Math.clamp(value * maxScale, 1.0f, maxScale);
			poseStack.scale(value, value, value);
			this.eggModel.render(poseStack, ObjRenderTypes :: trianglesSolid, bufferSource, packedOverlay, packedLight, renderColor);
		}
		poseStack.popPose();
	}

	@Override
	public @Nullable RenderType getRenderType(MultiblockMorpher animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
	{
		return RenderType.entityTranslucent(texture);
	}
}
