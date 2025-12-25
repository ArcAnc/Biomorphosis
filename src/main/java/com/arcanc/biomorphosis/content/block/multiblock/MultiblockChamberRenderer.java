/**
 * @author ArcAnc
 * Created at: 28.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.renderer.MultiblockGeoModel;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackSidedStorage;
import com.arcanc.biomorphosis.util.model.obj.ObjRenderTypes;
import com.arcanc.biomorphosis.util.model.obj.SphereObj;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MultiblockChamberRenderer extends GeoBlockRenderer<MultiblockChamber>
{
    private final SphereObj sphereModel = new SphereObj(Database.rl("textures/block/multiblock_chamber/sphere.png"));

    public MultiblockChamberRenderer(BlockEntityRendererProvider.Context ctx)
    {
        super(new MultiblockGeoModel<>(Database.rl("chamber"), Database.rl("chamber"), Database.rl("chamber")));
    }

    @Override
    public void preRender(PoseStack poseStack, MultiblockChamber animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
    {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);

        int maxTime = animatable.getMaxWorkedTime();
        int currentTime = animatable.getWorkedTime();

        if (maxTime <= 0 || currentTime >= maxTime)
            renderItemInside(poseStack, animatable, bufferSource, partialTick, packedLight, packedOverlay, renderColor);

        Level level = animatable.getLevel();
        if (level == null)
            return;
        float percent = Math.clamp(currentTime / (float)maxTime, 0.1f, 1.0f);
        float angle = (level.getGameTime() % 360 + partialTick);

        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.scale(percent, percent, percent);
        this.sphereModel.render(poseStack, ObjRenderTypes :: trianglesSolid, bufferSource, packedOverlay, packedLight, renderColor);
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
    public @NotNull AABB getRenderBoundingBox(@NotNull MultiblockChamber blockEntity)
    {
        BlockPos size = blockEntity.getDefinition().map(IMultiblockDefinition :: size).orElse(BlockPos.ZERO);
        return blockEntity.isMaster() ? new AABB(Vec3.atCenterOf(blockEntity.getBlockPos().subtract(size)), Vec3.atCenterOf(blockEntity.getBlockPos().offset(size))) : super.getRenderBoundingBox(blockEntity);
    }

    @Override
    public boolean shouldRender(@NotNull MultiblockChamber blockEntity, @NotNull Vec3 cameraPos)
    {
        return blockEntity.isMaster() && blockEntity.getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED && super.shouldRender(blockEntity, cameraPos);
    }
}
