/**
 * @author ArcAnc
 * Created at: 28.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.renderer;

import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackSidedStorage;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PBakedModel;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.model.textures.PAlphaMode;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class MultiblockChamberRenderer extends PBlockRenderer<MultiblockChamber>
{
    private static final ResourceLocation TEXTURE = Database.rl("block/chamber/0");
    private static final ResourceLocation SPHERE_TEXTURE = Database.rl("block/chamber/sphere/0");
    private static final float[][] PULSE_POINTS = {
            {0f, 0f}, {1f, 0.02f}, {2f, 0.04f}, {3f, 0.06f}, {4f, 0.07f}, {5f, 0.08f},
            {6f, 0.09f}, {7f, 0.10f}, {8f, 0.11f}, {9f, 0.12f}, {10f, 0.13f}, {11f, 0.15f},
            {12f, 0.145f}, {13f, 0.13f}, {14f, 0.12f}, {15f, 0.11f}, {16f, 0.10f}, {17f, 0.09f},
            {18f, 0.05f}, {19f, 0.04f}, {20f, 0.02f}, {21f, 0.01f}, {22f, 0.01f}, {23f, 0f},
            {46f, 0f}, {47f, 0.01f}, {48f, 0.03f}, {49f, 0.05f}, {50f, 0.06f}, {51f, 0.05f},
            {52f, 0.04f}, {53f, 0.03f}, {54f, 0.02f}, {55f, 0.01f}, {56f, 0f}, {99f, 0f}
    };
    
    private static final PModelData SPHERE_MODEL = new PModelData.Builder(
            Database.rl("glmodels/block/chamber/sphere.gltf"), "").build();
    private static final PModelData MORPHED = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("chamber")).build();
    private static final PModelData MORPHING = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("chamber")).build();
    private static final PModelData DISASSEMBLED = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("chamber")).build();
    
    public MultiblockChamberRenderer(BlockEntityRendererProvider.Context ctx)
    {
        super(MORPHED, PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    @Override
    public @Nullable PBakedModel getModel(MultiblockChamber animatable)
    {
        return this.getModelData(animatable).getModel();
    }
    
    @Override
    public PModelData getModelData(MultiblockChamber animatable)
    {
        MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
        return state == MultiblockState.FORMED ? MORPHED : state == MultiblockState.MORPHING ? MORPHING : DISASSEMBLED;
    }
    
    @Override
    public void preSubmit(PoseStack poseStack, MultiblockChamber animatable, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick, @Nullable Object... additionalData)
    {
        int maxTime = animatable.getMaxWorkedTime();
        int currentTime = animatable.getWorkedTime();
        
        if (maxTime <= 0 || currentTime >= maxTime)
            renderItemInside(poseStack, animatable, bufferSource, partialTick, packedLight, packedOverlay, -1);
        
        Level level = animatable.getLevel();
        if (level == null)
            return;
        float percent = Math.clamp(currentTime / (float)maxTime, 0.1f, 1.0f);
        float angle = (level.getGameTime() % 360 + partialTick);
        PBakedModel sphereModel = SPHERE_MODEL.getModel();
        if (sphereModel == null)
            return;
        
        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        float scale = percent * (1f + pulse(System.nanoTime() / 1_000_000_000f) * 2f);
        poseStack.scale(scale, scale, scale);
        sphereModel.instantDraw(
                poseStack,
                SPHERE_MODEL,
                List.of(),
                PRenderTypes.RenderTypeProvider :: trianglesImmediate,
                -1,
                packedLight,
                packedOverlay,
                partialTick);
        poseStack.popPose();
    }

    private void renderItemInside(PoseStack poseStack, MultiblockChamber animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, int renderColor)
    {
        ItemStackSidedStorage storage = MultiblockChamber.getItemHandler(animatable, null);
        if (storage == null)
            return;
        ItemStack stack = storage.getStackInSlot(0);
        if (stack.isEmpty())
            return;

        Level level = animatable.getLevel();
        if (level == null)
            return;
        float angle = (animatable.getLevel().getGameTime() % 360 + partialTick);

        poseStack.pushPose();
        poseStack.translate(0, 1.25f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.scale(1.5f, 1.5f, 1.5f);
        RenderHelper.renderItem().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
        poseStack.popPose();
    }
	
	@Override
	protected PMeshRenderContext resolveMeshRender(MultiblockChamber animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		if (!bone.name().equals("plat_top") &&
			!bone.name().equals("sphere"))
			return inherited;
		return new PMeshRenderContext(PRenderTypes.RenderTypeProvider :: trianglesTranslucent,
				inherited.color(),
				inherited.packedLight(),
				inherited.packedOverlay(),
				inherited.deformation(),
				inherited.texture(),
				inherited.emissive(),
				PAlphaMode.TRANSLUCENT);
	}
	
	@Override
    public AABB getRenderBoundingBox(MultiblockChamber blockEntity)
    {
        BlockPos size = blockEntity.getDefinition().map(IMultiblockDefinition :: size).orElse(BlockPos.ZERO);
        return blockEntity.isMaster() ? new AABB(Vec3.atCenterOf(blockEntity.getBlockPos().subtract(size)), Vec3.atCenterOf(blockEntity.getBlockPos().offset(size))) : super.getRenderBoundingBox(blockEntity);
    }

    @Override
    public boolean shouldRender(MultiblockChamber blockEntity, Vec3 cameraPos)
    {
        return blockEntity.isMaster() && blockEntity.getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED && super.shouldRender(blockEntity, cameraPos);
    }
    
    public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
        event.addTextureLocation(SPHERE_TEXTURE);
    }

    private static float pulse(float time)
    {
        float index = (time * (20f / 60f) % 1f) * 99f;
        for (int q = 1; q < PULSE_POINTS.length; q++)
        {
            float[] previous = PULSE_POINTS[q - 1];
            float[] next = PULSE_POINTS[q];
            if (index <= next[0])
                return Mth.lerp((index - previous[0]) / (next[0] - previous[0]), previous[1], next[1]);
        }
        return 0f;
    }
}
