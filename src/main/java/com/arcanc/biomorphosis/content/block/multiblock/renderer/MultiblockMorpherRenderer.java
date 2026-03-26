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
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.definition.PartsMap;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.model.obj.MorpherBaseObj;
import com.arcanc.biomorphosis.util.model.obj.ObjRenderTypes;
import com.arcanc.biomorphosis.util.model.obj.SphereGreenObj;
import com.arcanc.biomorphosis.util.model.obj.SphereObj;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.model.baked.PBakedModel;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MultiblockMorpherRenderer extends PBlockRenderer<MultiblockMorpher>
{
	private static final ResourceLocation TEXTURE = Database.rl("block/morpher/0");
	
	private static final MorpherBaseObj BASE_MODEL = new MorpherBaseObj(Database.rl("textures/block/morpher/0.png"));
	private static final SphereObj sphereModel = new SphereObj(Database.rl("textures/block/chamber/sphere.png"));
	private static final SphereGreenObj sphereGreenModel = new SphereGreenObj(Database.rl("textures/block/chamber/sphere.png"));
	
	private static final PModelData MORPHED = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("morpher")).build();
	private static final PModelData MORPHING = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("morpher")).build();
	private static final PModelData DISASSEMBLED = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("morpher")).build();
	
	public MultiblockMorpherRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("morpher")).
				addTexture(Database.rl("0")).
				build(),
				PRenderTypes.RenderTypeProvider :: trianglesTranslucent);
	}
	
	@Override
	public @Nullable PBakedModel getModel(MultiblockMorpher animatable)
	{
		return this.getModelData(animatable).getModel();
	}
	
	@Override
	public PModelData getModelData(MultiblockMorpher animatable)
	{
		MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
		return state == MultiblockState.FORMED ? MORPHED : state == MultiblockState.MORPHING ? MORPHING : DISASSEMBLED;
	}
	
	@Override
	public void preSubmit(PoseStack poseStack, MultiblockMorpher animatable, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick, @Nullable Object... additionalData)
	{
		BlockState state = animatable.getBlockState();
		
		poseStack.pushPose();
		
		//BASE_MODEL.render(poseStack, ObjRenderTypes :: trianglesTranslucent, bufferSource, packedOverlay, packedLight, -1);
		
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
			sphereModel.render(poseStack, ObjRenderTypes :: trianglesSolid, bufferSource, packedOverlay, packedLight, -1);
			poseStack.popPose();
			
			poseStack.pushPose();
			poseStack.translate(0f, -0.05f, 0f);
			poseStack.scale(value, value, value);
			sphereGreenModel.render(poseStack, ObjRenderTypes :: trianglesTranslucent, bufferSource, packedOverlay, packedLight, -1);
		}
		poseStack.popPose();
	}

	public static void renderItem(ItemDisplayContext displayContext,
								  PoseStack poseStack,
								  MultiBufferSource bufferSource,
								  int packedLight,
								  int packedOverlay)
	{
		poseStack.pushPose();
		poseStack.translate(0.5f, 0f, 0.5f);
		poseStack.scale(0.85f, 0.85f, 0.85f);
		BASE_MODEL.render(poseStack, ObjRenderTypes :: trianglesTranslucent, bufferSource, packedOverlay, packedLight, -1);
		poseStack.popPose();
	}
	
	public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}
