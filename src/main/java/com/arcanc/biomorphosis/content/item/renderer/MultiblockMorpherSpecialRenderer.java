/**
 * @author ArcAnc
 * Created at: 01.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item.renderer;

import com.arcanc.biomorphosis.content.block.multiblock.MultiblockMorpherRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record MultiblockMorpherSpecialRenderer() implements SpecialModelRenderer<Void>
{

    @Override
    public void render(@Nullable Void patterns,
                       @NotNull ItemDisplayContext displayContext,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay,
                       boolean hasFoilType)
    {
        MultiblockMorpherRenderer.renderItem(displayContext, poseStack, bufferSource, packedLight, packedOverlay, hasFoilType);
    }

    @Override
    public @NotNull Void extractArgument(@NotNull ItemStack stack)
    {
        return null;
    }

    public record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = ResourceLocation.CODEC.fieldOf("texture")
                .xmap(MultiblockMorpherSpecialRenderer.Unbaked::new, MultiblockMorpherSpecialRenderer.Unbaked::texture);

        @Override
        public @NotNull MapCodec<MultiblockMorpherSpecialRenderer.Unbaked> type()
        {
            return MAP_CODEC;
        }

        @Override
        public @NotNull SpecialModelRenderer<?> bake(@NotNull EntityModelSet modelSet)
        {
            return new MultiblockMorpherSpecialRenderer();
        }
    }
}
