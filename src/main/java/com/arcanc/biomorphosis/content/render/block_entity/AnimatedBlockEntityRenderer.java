/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.render.block_entity;

import com.arcanc.biomorphosis.content.block.block_entity.AnimatedBlockEntity;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.model.BlockEntityModel;
import com.arcanc.biomorphosis.util.model.BlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AnimatedBlockEntityRenderer<BE extends AnimatedBlockEntity, RS extends BlockEntityRenderState, M extends BlockEntityModel<RS>> implements BlockEntityRenderer<BE>
{

    private final RS reusedState = this.createRenderState();
    protected M model;

    protected abstract void renderAdditional(RS renderState, BE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay);

    @Override
    public void render(@NotNull BE blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        BlockState state = blockEntity.getBlockState();
        Direction facing = Direction.NORTH;
        if (state.hasProperty(BlockHelper.BlockProperties.HORIZONTAL_FACING))
            facing = state.getValue(BlockHelper.BlockProperties.HORIZONTAL_FACING);

        RS renderState = createRenderState(blockEntity, partialTick);
        poseStack.pushPose();

        poseStack.translate(0.5f, 0f, 0.5f);

        poseStack.mulPose(Axis.YP.rotationDegrees(getRotationFromDirection(facing)));

        poseStack.scale(-1f, -1f, 1f);
        poseStack.translate(0f, BlockEntityModel.MODEL_Y_OFFSET, 0f);
        this.scale(renderState, poseStack);
        model.setupAnim(renderState);
        renderAdditional(renderState, blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        RenderType renderType = getRenderType(renderState, true, false);
        if (renderType != null)
        {
            VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    protected void scale(RS renderState, PoseStack poseStack)
    {

    }

    public abstract ResourceLocation getTextureLocation(RS renderState);

    public abstract RS createRenderState();

    public void extractRenderState(BE blockEntity, RS state, float partialTick)
    {
        state.partialTicks = partialTick;
        state.ageInTicks = blockEntity.getAge() + partialTick;
    }

    public final RS createRenderState(BE blockEntity, float partialTick)
    {
        RS s = this.reusedState;
        this.extractRenderState(blockEntity, s, partialTick);
        //net.neoforged.neoforge.client.renderstate.RenderStateExtensions.onUpdateEntityRenderState(this, entity, s);
        return s;
    }

    @Nullable
    protected RenderType getRenderType(RS renderState, boolean isVisible, boolean renderTranslucent)
    {
        ResourceLocation resourcelocation = this.getTextureLocation(renderState);
        if (renderTranslucent)
        {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        }
        else if (isVisible)
        {
            return this.model.renderType(resourcelocation);
        }
        else
        {
            return null;
        }
    }

    private float getRotationFromDirection(Direction dir)
    {
        return switch (dir)
        {
            case NORTH, UP, DOWN -> 0.f;
            case SOUTH -> 180.f;
            case WEST -> 90.f;
            case EAST -> 270.0f;
        };
    }
}
