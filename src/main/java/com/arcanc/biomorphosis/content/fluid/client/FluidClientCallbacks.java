package com.arcanc.biomorphosis.content.fluid.client;

import com.arcanc.biomorphosis.content.fluid.BioFluidType;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.joml.Vector4f;

/** Construct client-signature lambdas only on the physical client. */
public final class FluidClientCallbacks
{
    private FluidClientCallbacks() {}

    public static BioFluidType.ColorParams biomassColor()
    {
        return new BioFluidType.ColorParams(new Vector4f(112, 15, 37, 255), new Vector4f(97, 21, 10, 255), 80, (minColor, maxColor, maxTime) ->
                {
                    Vector3f minimumColor = new Vector3f(minColor.x(), minColor.y(), minColor.z()).div(255f);
                    Vector3f maximumColor = new Vector3f(maxColor.x(), maxColor.y(), maxColor.z()).div(255f);
                    Minecraft mc = RenderHelper.mc();
                    Level level = mc.level;
                    if (level == null)
                        return -1;
                    long levelTime = level.getGameTime();
                    float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
                    float halfTime = maxTime / 2f;

                    float time = (levelTime + partialTicks) % maxTime;
                    if (time < halfTime)
                        return MathHelper.ColorHelper.lerp(time / halfTime, MathHelper.ColorHelper.color(maximumColor), MathHelper.ColorHelper.color(minimumColor));
                    else
                        return MathHelper.ColorHelper.lerp((time - halfTime) / halfTime, MathHelper.ColorHelper.color(minimumColor), MathHelper.ColorHelper.color(maximumColor));
                });
    }

    public static BioFluidType.FogGetter color()
    {
        return (camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor, colorParams) ->
                MathHelper.ColorHelper.vector3fFromRGB24(colorParams.getColor());
    }

    public static BioFluidType.FogOptionsGetter options()
    {
        return (camera, mode, renderDistance, partialTick, nearDistance, farDistance, shape, colorParams) -> {
            RenderSystem.setShaderFogStart(1.0f);
            RenderSystem.setShaderFogEnd(6.0f);
        };
    }
}
