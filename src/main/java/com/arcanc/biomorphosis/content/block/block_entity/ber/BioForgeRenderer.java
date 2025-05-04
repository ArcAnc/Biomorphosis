/**
 * @author ArcAnc
 * Created at: 03.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.BioForgeBlock;
import com.arcanc.biomorphosis.content.block.block_entity.BioForge;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class BioForgeRenderer extends GeoBlockRenderer<BioForge>
{
    public BioForgeRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new BioForgeBlockModel());
    }

    @Override
    public @Nullable RenderType getRenderType(BioForge animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucent(texture);
    }

    private static class BioForgeBlockModel extends DefaultedBlockGeoModel<BioForge>
    {
        private static final ResourceLocation FORGE_RL = Database.rl("forge");
        private static final ResourceLocation DOUBLE_FORGE_RL = Database.rl("forge_double");

        private final ResourceLocation forgeModelRL;
        private final ResourceLocation forgeAnimationRL;
        private final ResourceLocation doubleForgeModelRL;
        private final ResourceLocation doubleForgeAnimationRl;

        public BioForgeBlockModel()
        {
            super(FORGE_RL);
            this.forgeModelRL = buildFormattedModelPath(FORGE_RL);
            this.doubleForgeModelRL = buildFormattedModelPath(DOUBLE_FORGE_RL);

            this.forgeAnimationRL = buildFormattedAnimationPath(FORGE_RL);
            this.doubleForgeAnimationRl = buildFormattedAnimationPath(DOUBLE_FORGE_RL);
        }

        @Override
        public ResourceLocation getModelResource(@NotNull BioForge animatable, GeoRenderer<BioForge> renderer)
        {
            return BioForgeBlock.isDouble(animatable.getBlockState()) ? doubleForgeModelRL : forgeModelRL;
        }

        @Override
        public ResourceLocation getAnimationResource(@NotNull BioForge animatable)
        {
            return BioForgeBlock.isDouble(animatable.getBlockState()) ? doubleForgeAnimationRl : forgeAnimationRL;
        }
    }
}
