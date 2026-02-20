/**
 * @author ArcAnc
 * Created at: 01.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item.renderer;

import com.arcanc.biomorphosis.content.block.multiblock.renderer.MultiblockMorpherRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MultiblockMorpherSpecialRenderer extends BlockEntityWithoutLevelRenderer
{
    
    public MultiblockMorpherSpecialRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet)
    {
        super(blockEntityRenderDispatcher, entityModelSet);
    }
    
    @Override
    public void renderByItem(@NotNull ItemStack stack,
                             @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack,
                             @NotNull MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay)
    {
        MultiblockMorpherRenderer.renderItem(displayContext, poseStack, buffer, packedLight, packedOverlay);
    }
}
