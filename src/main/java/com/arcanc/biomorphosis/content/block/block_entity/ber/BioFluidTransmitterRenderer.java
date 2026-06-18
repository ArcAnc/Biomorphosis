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
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;

import java.util.*;

public class BioFluidTransmitterRenderer implements BlockEntityRenderer<BioFluidTransmitter>
{

    private static final ResourceLocation ESSENTIA = Database.rl("textures/misc/essentia.png");
    private static final int SEGMENTS = 12; // Количество сегментов в окружности
    private static final float RADIUS = 0.06f; // Радиус трубы
    private static final float FLUID_RADIUS = 0.035f;
    private static final float TRANSPORT_INTERPOLATION_TICKS = 5;
    private static final int LAVA_COLOR = MathHelper.ColorHelper.color(255, 69, 0);
    private static final int BASE_STYLE = MathHelper.ColorHelper.color(175, 33, 12, 12);
    private static final int OVERLAY_STYLE = MathHelper.ColorHelper.color(230, 255, 255, 255);
    private final Map<UUID, InterpolationState> interpolationByTransportId = new HashMap<>();

    public BioFluidTransmitterRenderer(BlockEntityRendererProvider.Context ctx)
    {

    }

    @Override
    public void render(BioFluidTransmitter blockEntity,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        poseStack.pushPose();
        Vec3 vec = Vec3.atLowerCornerOf(blockEntity.getBlockPos().multiply(-1));
        poseStack.translate(vec.x(), vec.y(), vec.z());

        double renderTime = blockEntity.getLevel() == null ? partialTicks : blockEntity.getLevel().getGameTime() + partialTicks;
        cleanupInterpolationCache(blockEntity);
        for (BioFluidTransmitter.PathData data : blockEntity.getPathData())
            renderTube(blockEntity.getClientPath(data), data, blockEntity.getTransportDataEntries(), renderTime, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private void cleanupInterpolationCache(BioFluidTransmitter blockEntity)
    {
        Set<UUID> activeIds = new HashSet<>();
        for (Map.Entry<UUID, BioFluidTransmitter.TransportData> entry : blockEntity.getTransportDataEntries())
            activeIds.add(entry.getKey());
        this.interpolationByTransportId.keySet().removeIf(id -> !activeIds.contains(id));
    }

    private void renderTube(List<Vec3> points, BioFluidTransmitter.PathData pathData, Collection<Map.Entry<UUID, BioFluidTransmitter.TransportData>> transports, double renderTime, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        if (points.size() < 2)
            return;

        VertexConsumer baseConsumer = bufferSource.getBuffer(BioFluidTransmitterRenderTypes.fluidTransmitter(ESSENTIA, BioFluidTransmitterRenderTypes.TubeShaderData.EMPTY));
        renderTubeLayer(points, RADIUS, BASE_STYLE, baseConsumer, poseStack, packedLight);

        BioFluidTransmitterRenderTypes.TubeShaderData shaderData = getShaderData(pathData, transports, renderTime);
        if (!shaderData.transports().isEmpty())
        {
            VertexConsumer fluidConsumer = bufferSource.getBuffer(BioFluidTransmitterRenderTypes.fluidTransmitter(ESSENTIA, shaderData));
            renderTubeLayer(points, RADIUS + FLUID_RADIUS, OVERLAY_STYLE, fluidConsumer, poseStack, packedLight);
        }
    }

    private void renderTubeLayer(List<Vec3> points, float radius, int style, VertexConsumer vertexConsumer, PoseStack poseStack, int packedLight)
    {
        List<RingData> rings = new ArrayList<>();

        for (int point = 0; point < points.size() - 1; point++)
        {
            Vec3 p0 = points.get(point);
            Vec3 p1 = points.get(point + 1);
            float percent0 = point / (float)(points.size() - 1);
            float percent1 = (point + 1) / (float)(points.size() - 1);

            Vec3 direction = p0.subtract(p1).normalize();
            Vec3 tangent = new Vec3(0, 1, 0);
            if (Math.abs(direction.y) > 0.99)
                tangent = new Vec3(1, 0, 0);

            Vec3 normal = direction.cross(tangent).normalize();
            Vec3 binormal = direction.cross(normal).normalize();

            Vector3f[] ring0 = new Vector3f[SEGMENTS];
            Vector3f[] ring1 = new Vector3f[SEGMENTS];

            for (int segment = 0; segment < SEGMENTS; segment++)
            {
                double angle = (segment / (double) SEGMENTS) * Math.PI * 2;
                Vec3 radial = normal.scale(Math.cos(angle)).add(binormal.scale(Math.sin(angle)));
                ring0[segment] = p0.add(radial.scale(radius)).toVector3f();
                ring1[segment] = p1.add(radial.scale(radius)).toVector3f();
            }

            rings.add(new RingData(ring0, percent0));
            rings.add(new RingData(ring1, percent1));
        }

        for (int point = 0; point < rings.size() - 1; point++)
            for (int segment = 0; segment < SEGMENTS; segment++)
            {
                int next = (segment + 1) % SEGMENTS;
                addQuad(vertexConsumer, poseStack, segment, rings.get(point).mesh()[segment], rings.get(point).mesh()[next], rings.get(point + 1).mesh()[next], rings.get(point + 1).mesh()[segment], rings.get(point).percent(), rings.get(point + 1).percent(), style, packedLight);
            }
    }

    private BioFluidTransmitterRenderTypes.TubeShaderData getShaderData(BioFluidTransmitter.PathData pathData, Collection<Map.Entry<UUID, BioFluidTransmitter.TransportData>> transports, double renderTime)
    {
        List<BioFluidTransmitterRenderTypes.TransportShaderData> shaderTransports = new ArrayList<>();
        for (Map.Entry<UUID, BioFluidTransmitter.TransportData> entry : transports)
        {
            BioFluidTransmitter.TransportData transport = entry.getValue();
            if (transport.getFluid().isEmpty() || !transport.getPathDataId().equals(pathData.pathData()))
                continue;
            float rawTransportPercent = transport.getDirection() == BioFluidTransmitter.PathDirection.POSITIVE ? transport.getPercent() : 1 - transport.getPercent();
            float transportPercent = getInterpolatedPercent(entry.getKey(), rawTransportPercent, renderTime);
            int fluidColor = getFluidColor(transport.getFluid());
            shaderTransports.add(new BioFluidTransmitterRenderTypes.TransportShaderData(
                    transportPercent,
                    ((fluidColor >> 16) & 255) / 255f,
                    ((fluidColor >> 8) & 255) / 255f,
                    (fluidColor & 255) / 255f));
        }
        return new BioFluidTransmitterRenderTypes.TubeShaderData(shaderTransports);
    }

    private int getFluidColor(FluidStack fluid)
    {
        if (fluid.is(Fluids.LAVA))
            return LAVA_COLOR;
        return IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor();
    }

    private float getInterpolatedPercent(UUID transportId, float targetPercent, double renderTime)
    {
        InterpolationState state = this.interpolationByTransportId.get(transportId);
        if (state == null)
        {
            this.interpolationByTransportId.put(transportId, new InterpolationState(targetPercent, targetPercent, renderTime));
            return targetPercent;
        }

        if (Math.abs(state.targetPercent() - targetPercent) > 0.0001f)
        {
            float currentPercent = state.get(renderTime);
            state = new InterpolationState(currentPercent, targetPercent, renderTime);
            this.interpolationByTransportId.put(transportId, state);
        }
        return state.get(renderTime);
    }

    private void addQuad(VertexConsumer vertexConsumer, PoseStack matrix, int segment, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, float curPercent, float nextPercent, int style, int packedLight)
    {
        addTubeVertex(vertexConsumer, matrix, v0, style, curPercent, segment / (float)SEGMENTS, packedLight);
        addTubeVertex(vertexConsumer, matrix, v1, style, curPercent, (segment + 1) / (float)SEGMENTS, packedLight);
        addTubeVertex(vertexConsumer, matrix, v2, style, nextPercent, (segment + 1) / (float)SEGMENTS, packedLight);
        addTubeVertex(vertexConsumer, matrix, v3, style, nextPercent, segment / (float)SEGMENTS, packedLight);
    }

    private void addTubeVertex(VertexConsumer vertexConsumer, PoseStack matrix, Vector3f vertex, int style, float u, float v, int packedLight)
    {
        vertexConsumer.addVertex(matrix.last().pose(), vertex.x(), vertex.y(), vertex.z()).
                setColor(style).
                setUv(u, v).
                setLight(packedLight);
    }

    @Override
    public boolean shouldRenderOffScreen(BioFluidTransmitter blockEntity)
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BioFluidTransmitter blockEntity)
    {
        return new AABB(blockEntity.getBlockPos()).inflate(16);
    }

    private record RingData(Vector3f[] mesh, float percent)
    {}

    private record InterpolationState(float startPercent, float targetPercent, double startTime)
    {
        private float get(double renderTime)
        {
            float progress = (float)((renderTime - this.startTime) / TRANSPORT_INTERPOLATION_TICKS);
            progress = Math.max(0, Math.min(1, progress));
            return this.startPercent + (this.targetPercent - this.startPercent) * progress;
        }
    }
}
