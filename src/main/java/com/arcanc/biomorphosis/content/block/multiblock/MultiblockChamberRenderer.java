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
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MultiblockChamberRenderer extends GeoBlockRenderer<MultiblockChamber>
{
    public MultiblockChamberRenderer(BlockEntityRendererProvider.Context ctx)
    {
        super(new ChamberModel(Database.rl("chamber"), Database.rl("chamber"), Database.rl("chamber")));
    }

    @Override
    public boolean shouldRender(@NotNull MultiblockChamber blockEntity, @NotNull Vec3 cameraPos)
    {
        return blockEntity.isMaster() && blockEntity.getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED && super.shouldRender(blockEntity, cameraPos);
    }

    private static class ChamberModel extends DefaultedBlockGeoModel<MultiblockChamber>
    {
        private final Info morphed;
        private final Info morphing;
        private final Info disassembled;

        public ChamberModel(ResourceLocation morphed, ResourceLocation morphing, ResourceLocation disassembled)
        {
            super(morphed);
            this.morphed = new Info(buildFormattedModelPath(morphed), buildFormattedTexturePath(morphed), buildFormattedAnimationPath(morphed));
            this.morphing = new Info(buildFormattedModelPath(morphing), buildFormattedTexturePath(morphing), buildFormattedAnimationPath(morphing));
            this.disassembled = new Info(buildFormattedModelPath(disassembled), buildFormattedTexturePath(disassembled), buildFormattedAnimationPath(disassembled));
        }

        @Override
        public ResourceLocation getModelResource(@NotNull MultiblockChamber animatable, GeoRenderer<MultiblockChamber> renderer)
        {
            MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
            return state == MultiblockState.FORMED ? morphed.model() : state == MultiblockState.MORPHING ? morphing.model() : disassembled.model();
        }

        @Override
        public ResourceLocation getTextureResource(@NotNull MultiblockChamber animatable, GeoRenderer<MultiblockChamber> renderer)
        {
            MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
            return state == MultiblockState.FORMED ? morphed.texture() : state == MultiblockState.MORPHING ? morphing.texture() : disassembled.texture();
        }

        @Override
        public ResourceLocation getAnimationResource(@NotNull MultiblockChamber animatable)
        {
            MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
            return state == MultiblockState.FORMED ? morphed.animation() : state == MultiblockState.MORPHING ? morphing.animation() : disassembled.animation();
        }

        @Override
        public ResourceLocation[] getAnimationResourceFallbacks(MultiblockChamber animatable, GeoRenderer<MultiblockChamber> renderer) {
            return super.getAnimationResourceFallbacks(animatable, renderer);
        }

        @Override
        public @Nullable RenderType getRenderType(MultiblockChamber animatable, ResourceLocation texture)
        {
            return RenderType.entityTranslucent(texture, false);
        }
    }

    private record Info(ResourceLocation model, ResourceLocation texture, ResourceLocation animation)
    {}
}
