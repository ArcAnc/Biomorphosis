/**
 * @author ArcAnc
 * Created at: 14.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.renderer;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockMorpher;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.definition.PartsMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.model.obj.MorpherBaseObj;
import com.arcanc.biomorphosis.util.model.obj.ObjRenderTypes;
import com.arcanc.biomorphosis.util.model.obj.SphereGreenObj;
import com.arcanc.biomorphosis.util.model.obj.SphereObj;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MultiblockMorpherRenderer extends GeoBlockRenderer<MultiblockMorpher>
{

	private static final MorpherBaseObj baseModel = new MorpherBaseObj(Database.rl("textures/block/morpher.png"));
	private static final SphereObj sphereModel = new SphereObj(Database.rl("textures/block/multiblock_chamber/sphere.png"));
	private static final SphereGreenObj sphereGreenModel = new SphereGreenObj(Database.rl("textures/block/multiblock_chamber/sphere.png"));

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

		baseModel.render(poseStack, ObjRenderTypes :: trianglesTranslucent, bufferSource, packedOverlay, packedLight, renderColor);

		if ((state.getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING &&
			animatable.isPreparationPhase()) ||
			state.getValue(MultiblockPartBlock.STATE) != MultiblockState.MORPHING)
			{
				poseStack.popPose();
				return;
			}
		else
		{
			IMultiblockDefinition definition = animatable.getDefinition().get();
			PartsMap map = definition.getStructure(animatable.getLevel(), animatable.getBlockPos());

			float progressPartial = animatable.getMorphProgress() + (animatable.getAccumulatedTicks() + partialTick) / animatable.getMorphDelay();

			float value = progressPartial / map.getParts().size();
			BlockPos maxSize = map.getSize();
			int maxScale = Math.max(maxSize.getX(), Math.max(maxSize.getY(), maxSize.getZ()));

			value = Mth.lerp(value, 0.2f, maxScale);
			poseStack.translate(0, -(0.5f * value) + value , 0f);
			poseStack.scale(value, value, value);
			sphereModel.render(poseStack, ObjRenderTypes :: trianglesSolid, bufferSource, packedOverlay, packedLight, renderColor);
			poseStack.popPose();

			poseStack.pushPose();
			poseStack.translate(0f, -0.05f, 0f);
			poseStack.scale(value, value, value);
			sphereGreenModel.render(poseStack, ObjRenderTypes :: trianglesTranslucent, bufferSource, packedOverlay, packedLight, renderColor);
		}
		poseStack.popPose();
	}

	@Override
	public @Nullable RenderType getRenderType(MultiblockMorpher animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
	{
		return RenderType.entityTranslucent(texture);
	}

	public static void renderItem(@NotNull ItemDisplayContext displayContext,
								  @NotNull PoseStack poseStack,
								  @NotNull MultiBufferSource bufferSource,
								  int packedLight,
								  int packedOverlay,
								  boolean hasFoilType)
	{
		poseStack.pushPose();
		poseStack.translate(0.5f, 0f, 0.5f);
		poseStack.scale(0.85f, 0.85f, 0.85f);
		baseModel.render(poseStack, ObjRenderTypes :: trianglesTranslucent, bufferSource, packedOverlay, packedLight, -1);
		poseStack.popPose();
	}
}
