/**
 * @author ArcAnc
 * Created at: 20.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.block_entity.BioStomach;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BioStomachRenderer extends GeoBlockRenderer<BioStomach>
{
    private static final float MIN_X =  1.01F/16F;
    private static final float MAX_X = 6.99F/16F;
    private static final float MIN_Y =  1.01F/16F;
    private static final float MAX_Y = 6.99F/16F;
    private static final float MIN_Z =  3.01F/16F;
    private static final float MAX_Z = 12.99F/16F;

    private static final float MIN_UV_T =  3.01F;
    private static final float MAX_UV_T = 12.99F;

    public BioStomachRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultedBlockGeoModel<>(Database.rl("stomach")));
    }

    @Override
    public void postRender(PoseStack poseStack, BioStomach animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
    {
        if (animatable == null)
            return;
        FluidSidedStorage storage = BioStomach.getFluidHandler(animatable, null);
        if (storage == null)
            return;
        FluidStack stack = storage.getFluidInTank(3);
        if (stack.isEmpty())
            return;
        renderContent(stack, (float)storage.getClientFluidAmountInTank(3) / storage.getTankCapacity(3), poseStack);
    }

    private void renderContent(@NotNull FluidStack stack, float height, @NotNull PoseStack pose)
    {
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(stack.getFluid());

        ResourceLocation stillTex = renderProps.getStillTexture();
        TextureAtlasSprite still = RenderHelper.getTexture(stillTex);

        Vector4f color = MathHelper.ColorHelper.vector4fFromARGB(renderProps.getTintColor());

        pose.pushPose();
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.enableDepthTest();

        PoseStack.Pose matrix = pose.last();

        if (height <= 1)
            drawFluid(builder, matrix, height, still, color);
        MeshData data = builder.build();
        if (data != null)
            BufferUploader.drawWithShader(data);
        RenderSystem.disableDepthTest();
        pose.popPose();
    }

    private void drawFluid(@NotNull VertexConsumer builder, PoseStack.Pose pose, float height, @NotNull TextureAtlasSprite tex, @NotNull Vector4f color)
    {
        float maxZ = MAX_Z - 0.5f;
        float minZ = MIN_Z - 0.5f;

        float y = MIN_Y + (height * (MAX_Y - MIN_Y));

        float minU = tex.getU(MIN_UV_T / 16f);
        float maxU = tex.getU(MAX_UV_T / 16f);
        float minV = tex.getV(MIN_UV_T / 16f);
        float maxV = tex.getV(MAX_UV_T / 16f);

        builder.addVertex(pose, MAX_X, y, minZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MIN_X, y, minZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MIN_X, y, maxZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MAX_X, y, maxZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);
    }


    @Override
    public @Nullable RenderType getRenderType(BioStomach animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entitySolid(texture);
    }
}
