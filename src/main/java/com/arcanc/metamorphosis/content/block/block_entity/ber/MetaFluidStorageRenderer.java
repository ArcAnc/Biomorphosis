/**
 * @author ArcAnc
 * Created at: 28.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.block_entity.ber;

import com.arcanc.metamorphosis.content.block.block_entity.MetaFluidStorage;
import com.arcanc.metamorphosis.util.helper.MathHelper;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
import com.arcanc.metamorphosis.util.inventory.fluid.FluidSidedStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class MetaFluidStorageRenderer implements BlockEntityRenderer<MetaFluidStorage>
{
    private static final float MIN_X =  3.01F/16F;
    private static final float MAX_X = 12.99F/16F;
    private static final float MIN_Y =  2.01F/16F;
    private static final float MAX_Y = 14.99F/16F;
    private static final float MIN_Z =  3.01F/16F;
    private static final float MAX_Z = 12.99F/16F;

    private static final float MIN_UV_T =  3.01F;
    private static final float MAX_UV_T = 12.99F;
    private static final float MIN_U_S  =  3.01F;
    private static final float MAX_U_S  = 12.99F;
    private static final float MIN_V_S  =  0.01F;
    private static final float MAX_V_S  = 15.99F;

    public MetaFluidStorageRenderer(BlockEntityRendererProvider.Context ctx)
    {

    }

    @Override
    public void render(@NotNull MetaFluidStorage be,
                       float partialTick,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        FluidSidedStorage storage = MetaFluidStorage.getHandler(be, null);
        if (storage == null)
            return;
        FluidStack stack = storage.getFluidInTank(0);
        if (stack.isEmpty())
            return;
        renderContent(stack, (float)storage.getClientFluidAmountInTank(0) / storage.getTankCapacity(0), poseStack);
    }

    private void renderContent(@NotNull FluidStack stack, float height, @NotNull PoseStack pose)
    {
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(stack.getFluid());

        ResourceLocation stillTex = renderProps.getStillTexture();
        TextureAtlasSprite still = RenderHelper.getTexture(stillTex);

        ResourceLocation flowTex = renderProps.getFlowingTexture();
        TextureAtlasSprite flow = RenderHelper.getTexture(flowTex);


        boolean gas = stack.getFluid().getFluidType().isLighterThanAir();
        Vector4f color = MathHelper.ColorHelper.vector4fFromARGB(renderProps.getTintColor());

        pose.pushPose();
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.enableDepthTest();

        PoseStack.Pose matrix = pose.last();

        if (height < 1)
            drawTop(builder, matrix, height, still, color, gas);
        drawSides(builder, matrix, height, flow, color, gas);
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableDepthTest();
        pose.popPose();
    }

    private void drawTop(@NotNull VertexConsumer builder, PoseStack.Pose pose, float height, @NotNull TextureAtlasSprite tex, @NotNull Vector4f color, boolean gas)
    {
        float minX = gas ? MAX_X : MIN_X;
        float maxX = gas ? MIN_X : MAX_X;
        float y = (MIN_Y + (gas ? (1F - height) * (MAX_Y - MIN_Y) : height * (MAX_Y - MIN_Y)));

        float minU = tex.getU(MIN_UV_T / 16f);
        float maxU = tex.getU(MAX_UV_T / 16f);
        float minV = tex.getV(MIN_UV_T / 16f);
        float maxV = tex.getV(MAX_UV_T / 16f);

        builder.addVertex(pose, maxX, y, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, minX, y, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, minX, y, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, maxX, y, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);
    }

    private void drawSides(@NotNull VertexConsumer builder, PoseStack.Pose pose, float height, @NotNull TextureAtlasSprite tex, @NotNull Vector4f color, boolean gas)
    {
        float minY = gas ? MAX_Y - (height * (MAX_Y - MIN_Y)) : MIN_Y;
        float maxY = gas ? MAX_Y : MIN_Y + height * (MAX_Y - MIN_Y);

        float minU = tex.getU(MIN_U_S / 16f);
        float maxU = tex.getU(MAX_U_S / 16f);
        float minV = tex.getV(MIN_V_S / 16f);
        float maxV = tex.getV(height);

        //North
        builder.addVertex(pose, MIN_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MAX_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MAX_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MIN_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);

        //South
        builder.addVertex(pose, MAX_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MIN_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MIN_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MAX_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);

        //East
        builder.addVertex(pose, MAX_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MAX_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MAX_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MAX_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);

        //West
        builder.addVertex(pose, MIN_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MIN_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MIN_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MIN_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);
    }
}
