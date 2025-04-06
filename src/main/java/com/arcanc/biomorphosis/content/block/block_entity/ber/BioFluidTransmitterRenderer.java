/**
 * @author ArcAnc
 * Created at: 17.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.block_entity.BioFluidTransmitter;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BioFluidTransmitterRenderer implements BlockEntityRenderer<BioFluidTransmitter>
{

    private static final ResourceLocation ESSENTIA = Database.rl("textures/misc/essentia.png");
    private static final int SEGMENTS = 12; // Количество сегментов в окружности
    private static final float RADIUS = 0.06f; // Радиус трубы

    public BioFluidTransmitterRenderer(BlockEntityRendererProvider.Context ctx)
    {

    }

    @Override
    public void render(@NotNull BioFluidTransmitter blockEntity,
                       float partialTicks,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.translate(Vec3.atLowerCornerOf(blockEntity.getBlockPos().multiply(-1)));

        for (BioFluidTransmitter.PathData data : blockEntity.getPathData())
            renderTube(data.edgePath(), poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderTube(@NotNull List<Vec3> points, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        if (points.size() < 2)
            return;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(ESSENTIA));

        List<SegmentData> segments = new ArrayList<>();

        for (int point = 0; point < points.size() - 1; point++)
        {
            Vec3 p0 = points.get(point);
            Vec3 p1 = points.get(point + 1);

            Vec3 direction = p0.subtract(p1).normalize();
            Vec3 tangent = new Vec3(0, 1, 0);
            if (Math.abs(direction.y) > 0.99)
                tangent = new Vec3(1, 0, 0);

            Vec3 normal = direction.cross(tangent).normalize();
            Vec3 binormal = direction.cross(normal).normalize();

            Vector3f[] ring0 = new Vector3f[SEGMENTS];
            Vector3f[] ring1 = new Vector3f[SEGMENTS];
            Vector3f[] lookNormals = new Vector3f[SEGMENTS];

            for (int segment = 0; segment < SEGMENTS; segment++)
            {
                double angle = (segment / (double) SEGMENTS) * Math.PI * 2;
                Vec3 lookNormal = normal.scale(Math.cos(angle) * RADIUS);
                Vec3 offset = lookNormal.add(binormal.scale(Math.sin(angle) * RADIUS));
                ring0[segment] = p0.add(offset).toVector3f();
                ring1[segment] = p1.add(offset).toVector3f();
                lookNormals[segment] = offset.toVector3f().negate();
            }

            segments.add(new SegmentData(ring0, lookNormals));
            segments.add(new SegmentData(ring1, lookNormals));
        }

        for (int point = 0; point < segments.size() - 1; point++)
            for (int segment = 0; segment < SEGMENTS; segment++)
            {
                int next = (segment + 1) % SEGMENTS;
                addQuad(vertexConsumer, poseStack, segment, segments.get(point).mesh()[segment], segments.get(point).mesh()[next], segments.get(point + 1).mesh()[next], segments.get(point + 1).mesh()[segment], segments.get(point).normals()[segment], segments.get(point + 1).normals()[next], packedLight, packedOverlay);
            }
    }

    private void addQuad(@NotNull VertexConsumer vertexConsumer, @NotNull PoseStack matrix, int segment, @NotNull Vector3f v0, @NotNull Vector3f v1, @NotNull Vector3f v2, @NotNull Vector3f v3, Vector3f normalCur, Vector3f normalNext, int packedLight, int packedOverlay)
    {
        vertexConsumer.addVertex(matrix.last().pose(), v0.x(), v0.y(), v0.z()).setColor(33, 12, 12, 175).setUv(0, segment/(float)SEGMENTS).setOverlay(packedOverlay).setLight(packedLight).setNormal(matrix.last(), normalCur);
        vertexConsumer.addVertex(matrix.last().pose(), v1.x(), v1.y(), v1.z()).setColor(33, 12, 12,175).setUv(0, ((segment + 1) / (float)SEGMENTS)).setOverlay(packedOverlay).setLight(packedLight).setNormal(matrix.last(), normalNext);
        vertexConsumer.addVertex(matrix.last().pose(), v2.x(), v2.y(), v2.z()).setColor(33, 12, 12, 175).setUv(1, ((segment + 1) / (float)SEGMENTS)).setOverlay(packedOverlay).setLight(packedLight).setNormal(matrix.last(), normalNext);
        vertexConsumer.addVertex(matrix.last().pose(), v3.x(), v3.y(), v3.z()).setColor(33, 12, 12, 175).setUv(1, segment/(float)SEGMENTS).setOverlay(packedOverlay).setLight(packedLight).setNormal(matrix.last(), normalCur);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BioFluidTransmitter blockEntity)
    {
        return true;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull BioFluidTransmitter blockEntity)
    {
        return new AABB(blockEntity.getBlockPos()).inflate(16);
    }

    private record SegmentData(Vector3f[] mesh, Vector3f[] normals)
    {}

    private record TubeData(List<SegmentData> data)
    {}
}
