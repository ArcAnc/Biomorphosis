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
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.model.obj.ObjRenderTypes;
import com.arcanc.biomorphosis.util.model.obj.SphereObj;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.client.model.obj.ObjTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;

public class MultiblockChamberRenderer extends GeoBlockRenderer<MultiblockChamber>
{
    private final SphereObj sphereModel = new SphereObj(Database.rl("textures/block/multiblock_chamber/sphere.png"));

    public MultiblockChamberRenderer(BlockEntityRendererProvider.Context ctx)
    {
        super(new ChamberModel(Database.rl("chamber"), Database.rl("chamber"), Database.rl("chamber")));
    }

    @Override
    public void preRender(PoseStack poseStack, MultiblockChamber animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor)
    {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);

        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 0);
        this.sphereModel.render(poseStack, ObjRenderTypes :: trianglesSolid, bufferSource, packedOverlay, packedLight, renderColor);
        poseStack.popPose();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull MultiblockChamber blockEntity)
    {
        BlockPos size = blockEntity.getDefinition().map(IMultiblockDefinition :: size).orElse(BlockPos.ZERO);
        return blockEntity.isMaster() ? new AABB(Vec3.atCenterOf(blockEntity.getBlockPos().subtract(size)), Vec3.atCenterOf(blockEntity.getBlockPos().offset(size))) : super.getRenderBoundingBox(blockEntity);
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
