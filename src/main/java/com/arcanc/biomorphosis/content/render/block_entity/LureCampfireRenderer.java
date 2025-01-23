/**
 * @author ArcAnc
 * Created at: 08.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.render.block_entity;

import com.arcanc.biomorphosis.content.block.LureCampfireBlock;
import com.arcanc.biomorphosis.content.block.block_entity.LureCampfireBE;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LureCampfireRenderer extends GeoBlockRenderer<LureCampfireBE>
{
    public LureCampfireRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultedBlockGeoModel<>(Database.rl("lure_campfire")));
    }

    @Override
    public void postRender(PoseStack poseStack, LureCampfireBE animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
    {
        if (animatable == null)
            return;
        LureCampfireBE.LureCampfireStackHandler inventory = animatable.getInventory();
        if (ItemHelper.isEmpty(inventory))
            return;
        for (int q = 0; q < inventory.getSlots(); q++)
        {
            ItemStack stack = inventory.getStackInSlot(q);
            if (!stack.isEmpty())
            {
                poseStack.pushPose();
                Direction dir = animatable.getBlockState().getValue(LureCampfireBlock.HORIZONTAL_FACING);
                poseStack.mulPose(Axis.YP.rotationDegrees(Direction.getYRot(dir)));
                poseStack.translate(dir.getStepZ() * (-0.3f + (q * 0.15f)), 0.9f, dir.getStepX() * (-0.3f + (q * 0.15f)));
                poseStack.scale(0.4f, 0.4f, 0.4f);

                poseStack.mulPose(Axis.of(dir.getCounterClockWise().getUnitVec3().toVector3f()).rotation(model.getBone("shaft").map(GeoBone :: getRotX).orElse(0f)));
                poseStack.translate(0.0f, -0.179f, 0.0f);

                RenderHelper.renderItem().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
                poseStack.popPose();
            }
        }
    }

    @Override
    public @Nullable RenderType getRenderType(LureCampfireBE animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucent(texture);
    }
}
