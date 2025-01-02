/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.render.block_entity;

import com.arcanc.biomorphosis.content.block.block_entity.LureCampfireBE;
import com.arcanc.biomorphosis.content.render.block_entity.model.LureCampfireModel;
import com.arcanc.biomorphosis.content.render.block_entity.model.render_state.LureCampfireRenderState;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LureCampfireRenderer extends AnimatedBlockEntityRenderer<LureCampfireBE, LureCampfireRenderState, LureCampfireModel>
{
    private static final ResourceLocation TEXTURE = Database.rl("textures/block/lure_campfire.png");

    public LureCampfireRenderer(BlockEntityRendererProvider.@NotNull Context ctx)
    {
        model = new LureCampfireModel(ctx.bakeLayer(LureCampfireModel.LAYER_LOCATION));
    }

    @Override
    protected void renderAdditional(LureCampfireRenderState renderState, LureCampfireBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    }

    @Override
    public void extractRenderState(LureCampfireBE blockEntity, LureCampfireRenderState state, float partialTick)
    {
        super.extractRenderState(blockEntity, state, partialTick);
        state.rotateShaftAnimationState.copyFrom(blockEntity.rotateShaft);
    }

    @Override
    public ResourceLocation getTextureLocation(LureCampfireRenderState renderState)
    {
        return TEXTURE;
    }

    @Override
    public LureCampfireRenderState createRenderState()
    {
        return new LureCampfireRenderState();
    }
}
