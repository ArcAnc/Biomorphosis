/**
 * @author ArcAnc
 * Created at: 22.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.block_entity.BioCatcher;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.function.Function;

public class BioCatcherRenderer extends PBlockRenderer<BioCatcher>
{
    private static final ResourceLocation TEXTURE = Database.rl("block/catcher/0");
    
    private static final float MIN_X =  3.01F/16F;
    private static final float MAX_X = 12.99F/16F;
    private static final float MIN_Y =  1.01F/16F;
    private static final float MAX_Y = 2.99F/16F;
    private static final float MIN_Z =  3.01F/16F;
    private static final float MAX_Z = 12.99F/16F;

    private static final float MIN_UV_T =  3.01F;
    private static final float MAX_UV_T = 12.99F;

    public BioCatcherRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("catcher")).
                        build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    @Override
    public void postSubmit(PoseStack poseStack, BioCatcher animatable, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick, @Nullable Object... additionalData)
    {
        FluidSidedStorage storage = BioCatcher.getFluidHandler(animatable, null);
        if (storage == null)
            return;
        FluidStack stack = storage.getFluidInTank(0);
        if (stack.isEmpty())
            return;
        renderContent(stack, (float)storage.getClientFluidAmountInTank(0) / storage.getTankCapacity(0), poseStack);
    }
    
    private void renderContent(FluidStack stack, float height, PoseStack pose)
    {
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(stack.getFluid());

        ResourceLocation stillTex = renderProps.getStillTexture();
        TextureAtlasSprite still = RenderHelper.getTexture(stillTex);

        Vector4f color = MathHelper.ColorHelper.vector4fFromARGB(renderProps.getTintColor());

        pose.pushPose();
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShader(GameRenderer :: getPositionTexColorShader);
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

    private void drawFluid(VertexConsumer builder, PoseStack.Pose pose, float height, TextureAtlasSprite tex, Vector4f color)
    {
        float maxZ = MAX_Z - 0.5f;
        float minZ = MIN_Z - 0.5f;

        float maxX = MAX_X - 0.5f;
        float minX = MIN_X - 0.5f;

        float y = MIN_Y + (height * (MAX_Y - MIN_Y));

        float minU = tex.getU(MIN_UV_T / 16f);
        float maxU = tex.getU(MAX_UV_T / 16f);
        float minV = tex.getV(MIN_UV_T / 16f);
        float maxV = tex.getV(MAX_UV_T / 16f);

        builder.addVertex(pose, maxX, y, minZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, minX, y, minZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, minX, y, maxZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, maxX, y, maxZ).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);
    }
    
    public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
    }
}
