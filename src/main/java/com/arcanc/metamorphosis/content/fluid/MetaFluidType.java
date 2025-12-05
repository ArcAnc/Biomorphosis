/**
 * @author ArcAnc
 * Created at: 18.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.fluid;

import com.arcanc.metamorphosis.util.helper.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class MetaFluidType extends FluidType
{
    protected final ResourceLocation stillTexture;
    protected final ResourceLocation flowingTexture;
    protected final ResourceLocation overlayTexture;
    protected final MetaFluidType.FogGetter fogColor;
    protected final FogOptionsGetter fogOptions;
    protected final ColorParams colorParams;

    public MetaFluidType(final ResourceLocation stillTexture,
                         final ResourceLocation flowingTexture,
                         final ResourceLocation overlayTexture,
                         final ColorParams colorGetter,
                         final MetaFluidType.FogGetter fogColor,
                         final FogOptionsGetter fogParameters,
                         final Properties properties)
    {
        super(properties);

        this.stillTexture = stillTexture.withPrefix("block/");
        this.flowingTexture = flowingTexture.withPrefix("block/");
        this.overlayTexture = overlayTexture.withPath("block/");
        this.colorParams = colorGetter;
        this.fogColor = fogColor;
        this.fogOptions = fogParameters;
    }

    public IClientFluidTypeExtensions registerClientExtensions()
    {
        return new IClientFluidTypeExtensions()
        {
            @Override
            public @NotNull ResourceLocation getStillTexture()
            {
                return stillTexture;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture()
            {
                return flowingTexture;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture()
            {
                return overlayTexture;
            }

            @Override
            public int getTintColor()
            {
                return colorParams.getColor();
            }

            @Override
            public @NotNull Vector4f modifyFogColor(@NotNull Camera camera,
                                                    float partialTick,
                                                    @NotNull ClientLevel level,
                                                    int renderDistance,
                                                    float darkenWorldAmount,
                                                    @NotNull Vector4f fluidFogColor)

            {
                return fogColor.getFog(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor, colorParams);
            }

            @Override
            public @NotNull FogParameters modifyFogRender(@NotNull Camera camera,
                                                          FogRenderer.@NotNull FogMode mode,
                                                          float renderDistance,
                                                          float partialTick,
                                                          @NotNull FogParameters fogParameters)
            {
                return fogOptions.getFogParameters(camera, mode, renderDistance, partialTick, fogParameters, colorParams);
            }
        };
    }

    public ResourceLocation getStillTexture()
    {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture()
    {
        return flowingTexture;
    }

    public int getTintColor()
    {
        return colorParams.getColor();
    }

    public ResourceLocation getOverlayTexture()
    {
        return overlayTexture;
    }

    public MetaFluidType.FogGetter getFogColor()
    {
        return fogColor;
    }

    public interface FogGetter
    {
        Vector4f getFog(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor, ColorParams colorParams);

        static Vector4f interColor(Vector4f curColor, @NotNull Vector4f targetColor, float delta)
        {
            Vector4f diff = new Vector4f();

            targetColor.sub(curColor, diff);

            if (diff.equals(0,0,0, 0))
                return targetColor;

            diff.mul(delta, diff);
            return curColor.add(diff, new Vector4f());
        }
    }

    public interface FogOptionsGetter
    {
        FogParameters getFogParameters(@NotNull Camera camera,
                                       FogRenderer.@NotNull FogMode mode,
                                       float renderDistance,
                                       float partialTick,
                                       @NotNull FogParameters fogParameters,
                                       @NotNull ColorParams colorParams);
    }

    public record ColorParams(Vector4f minColor, Vector4f maxColor, int maxTime, TriFunction<Vector4f, Vector4f, Integer, Integer> colorGetter)
    {
        public static @NotNull ColorParams constantColor(Vector4f color)
        {
            return new ColorParams(color, color, 0, (minColor, maxColor, maxTime) -> MathHelper.ColorHelper.color(minColor.div(255f, new Vector4f())));
        }

        public int getColor()
        {
            return colorGetter().apply(minColor(), maxColor(), maxTime());
        }
    }
}
