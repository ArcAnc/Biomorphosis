/**
 * @author ArcAnc
 * Created at: 16.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL43C;

import java.io.IOException;
import java.util.List;
import java.nio.FloatBuffer;

public class BioFluidTransmitterRenderTypes
{
    private static final int TRANSPORT_SSBO_BINDING = 3;

    public static RenderType fluidTransmitter(ResourceLocation texture, TubeShaderData shaderData)
    {
        return createFluidTransmitter(texture, shaderData);
    }

    private static RenderType createFluidTransmitter(ResourceLocation texture, TubeShaderData shaderData)
    {
        RenderType.CompositeState state = RenderType.CompositeState.builder().
                setShaderState(new RenderStateShard.ShaderStateShard(() -> prepareShader(shaderData))).
                setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).
                setTexturingState(new TransportSsboStateShard(shaderData)).
                setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
                setCullState(RenderType.NO_CULL).
                setLightmapState(RenderType.LIGHTMAP).
                createCompositeState(true);
        return RenderType.create("bio_fluid_transmitter", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 4096, false, true, state);
    }

    @Nullable
    private static ShaderInstance shader;

    private static @Nullable ShaderInstance prepareShader(TubeShaderData shaderData)
    {
        if (shader == null)
            return null;

        Minecraft mc = RenderHelper.mc();
        float gameTime;
        if (mc.level == null)
            gameTime = (Util.getMillis() % 1000) / 1000f;
        else
            gameTime = ((mc.level.getGameTime() % 100) + mc.getTimer().getGameTimeDeltaPartialTick(false)) / 100f;
        shader.safeGetUniform("GameTime").set(gameTime);
        shader.safeGetUniform("TransportCount").set(shaderData.transports().size());
        return shader;
    }

    private static void registerShaders(final RegisterShadersEvent event)
    {
        try
        {
            event.registerShader(new ShaderInstance(
                            event.getResourceProvider(),
                            Database.rl("bio_fluid_transmitter"),
                            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
                    shaderInstance -> shader = shaderInstance);
        }
        catch (IOException e)
        {
            Database.LOGGER.warn("Failed to register fluid transmitter shader: {}", String.valueOf(e));
        }
    }

    public static void register(IEventBus modEventBus)
    {
        modEventBus.addListener(BioFluidTransmitterRenderTypes :: registerShaders);
    }

    public record TubeShaderData(List<TransportShaderData> transports)
    {
        public static final TubeShaderData EMPTY = new TubeShaderData(List.of());
    }

    public record TransportShaderData(float percent, float red, float green, float blue)
    {
        public static final TransportShaderData EMPTY = new TransportShaderData(0, 0, 0, 0);
    }

    private static class TransportSsboStateShard extends RenderStateShard.TexturingStateShard
    {
        private static int bufferId;

        private TransportSsboStateShard(TubeShaderData shaderData)
        {
            super("bio_fluid_transmitter_transport_ssbo", () -> bind(shaderData), TransportSsboStateShard :: unbind);
        }

        private static void bind(TubeShaderData shaderData)
        {
            if (bufferId == 0)
                bufferId = GL43C.glGenBuffers();

            FloatBuffer buffer = BufferUtils.createFloatBuffer(Math.max(1, shaderData.transports().size()) * 4);
            for (TransportShaderData transport : shaderData.transports())
                buffer.put(transport.percent()).put(transport.red()).put(transport.green()).put(transport.blue());
            if (shaderData.transports().isEmpty())
                buffer.put(TransportShaderData.EMPTY.percent()).put(TransportShaderData.EMPTY.red()).put(TransportShaderData.EMPTY.green()).put(TransportShaderData.EMPTY.blue());
            buffer.flip();

            GL43C.glBindBuffer(GL43C.GL_SHADER_STORAGE_BUFFER, bufferId);
            GL43C.glBufferData(GL43C.GL_SHADER_STORAGE_BUFFER, buffer, GL43C.GL_DYNAMIC_DRAW);
            GL43C.glBindBufferBase(GL43C.GL_SHADER_STORAGE_BUFFER, TRANSPORT_SSBO_BINDING, bufferId);
            GL43C.glBindBuffer(GL43C.GL_SHADER_STORAGE_BUFFER, 0);
        }

        private static void unbind()
        {
            GL43C.glBindBufferBase(GL43C.GL_SHADER_STORAGE_BUFFER, TRANSPORT_SSBO_BINDING, 0);
        }
    }
}
