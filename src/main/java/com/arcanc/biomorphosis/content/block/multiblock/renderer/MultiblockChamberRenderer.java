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
import com.arcanc.biomorphosis.util.model.obj.ObjRenderTypes;
import com.arcanc.biomorphosis.util.model.obj.SphereObj;
import com.arcanc.pulselib.content.animatable.PAnimationController;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedModel;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class MultiblockChamberRenderer extends PBlockRenderer<MultiblockChamber>
{
    private static final ResourceLocation TEXTURE = Database.rl("block/chamber/0");
    
    private static final SphereObj SPHERE_MODEL = new SphereObj(Database.rl("textures/block/chamber/sphere.png"));
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
        
        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.scale(percent, percent, percent);
        SPHERE_MODEL.render(poseStack, ObjRenderTypes :: trianglesSolid, bufferSource, packedOverlay, packedLight, -1);
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
    protected void perBoneSubmit(MultiblockChamber animatable, PoseStack poseStack, PBakedBone bone, Collection<PAnimationController<MultiblockChamber>> pAnimationControllers, Function<ResourceLocation, RenderType> renderType, int packedColor, int packedLight, int packedOverlay, float partialTick)
    {
        if (bone.name().equals("plat_top") || bone.name().equals("sphere"))
            renderType = PRenderTypes.RenderTypeProvider :: trianglesTranslucent;
        super.perBoneSubmit(animatable, poseStack, bone, pAnimationControllers, renderType, packedColor, packedLight, packedOverlay, partialTick);
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
    
    public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
    }
}
