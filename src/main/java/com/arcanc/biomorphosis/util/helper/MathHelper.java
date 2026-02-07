/**
 * @author ArcAnc
 * Created at: 16.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MathHelper
{
    public static final double STANDARD_EPSILON = 1e-9;

    public static boolean isApproximatelyEqual(double first, double second)
    {
        return isApproximatelyEqual(first, second, STANDARD_EPSILON);
    }

    public static boolean isApproximatelyEqual(double first, double second, double epsilon)
    {
        return Math.abs(first - second) < epsilon;
    }

    public static class ColorHelper
    {
        public static int alpha(int color)
        {
            return color >>> 24;
        }

        public static int red(int color)
        {
            return (color >> 16) & 0xFF;
        }

        public static int green(int color)
        {
            return (color >> 8) & 0xFF;
        }

        public static int blue(int color)
        {
            return color & 0xFF;
        }

        public static int color(int alpha, int red, int green, int blue)
        {
            return (alpha << 24) | (red << 16) | (green << 8) | blue;
        }

        public static int color(int red, int green, int blue)
        {
            return color(255, red, green, blue);
        }

        public static int color(@NotNull Vector3f vec)
        {
            return color(as8BitChannel(vec.x()), as8BitChannel(vec.y()), as8BitChannel(vec.z()));
        }

        public static int color(@NotNull Vector4f vec)
        {
            return color(as8BitChannel(vec.w()), as8BitChannel(vec.x()), as8BitChannel(vec.y()), as8BitChannel(vec.z()));
        }

        public static int multiply(int color1, int color2)
        {
            if (color1 == -1)
                return color2;
            if (color2 == -1)
                return color1;
            return color(
                    alpha(color1) * alpha(color2) / 255,
                    red(color1) * red(color2) / 255,
                    green(color1) * green(color2) / 255,
                    blue(color1) * blue(color2) / 255
            );
        }

        public static int scaleRGB(int color, float scale)
        {
            return scaleRGB(color, scale, scale, scale);
        }

        public static int scaleRGB(int color, float redScale, float greenScale, float blueScale)
        {
            return scaleARGB(color, 1, redScale, greenScale, blueScale);
        }

        public static int scaleARGB(int color, float alphaScale, float redScale, float greenScale, float blueScale)
        {
            return color(
                    Mth.clamp((int) (alpha(color) * alphaScale), 0, 255),
                    Mth.clamp((int) (red(color) * redScale), 0, 255),
                    Mth.clamp((int) (green(color) * greenScale), 0, 255),
                    Mth.clamp((int) (blue(color) * blueScale), 0, 255)
            );
        }



        public static int greyscale(int color)
        {
            int grey = (int) (red(color) * 0.3F + green(color) * 0.59F + blue(color) * 0.11F);
            return color(alpha(color), grey, grey, grey);
        }

        public static int lerp(float delta, int color1, int color2)
        {
            return color(
                    Mth.lerpInt(delta, alpha(color1), alpha(color2)),
                    Mth.lerpInt(delta, red(color1), red(color2)),
                    Mth.lerpInt(delta, green(color1), green(color2)),
                    Mth.lerpInt(delta, blue(color1), blue(color2))
            );
        }

        public static int opaque(int color)
        {
            return color | 0xFF000000;
        }

        public static int transparent(int color)
        {
            return color & 0xFFFFFF;
        }

        public static int color(int alpha, int color)
        {
            return (alpha << 24) | (color & 0xFFFFFF);
        }

        public static int white(float alpha) {
            return (as8BitChannel(alpha) << 24) | 0xFFFFFF;
        }

        public static int colorFromFloat(float alpha, float red, float green, float blue)
        {
            return color(as8BitChannel(alpha), as8BitChannel(red), as8BitChannel(green), as8BitChannel(blue));
        }

        public static @NotNull Vector3f vector3fFromRGB24(int color)
        {
            return new Vector3f(
                    red(color) / 255.0f,
                    green(color) / 255.0f,
                    blue(color) / 255.0f
            );
        }

        public static @NotNull Vector4f vector4fFromARGB(int color)
        {
            return new Vector4f(
                    red(color) / 255.0f,
                    green(color) / 255.0f,
                    blue(color) / 255.0f,
                    alpha(color) / 255.0f
            );
        }


        public static int average(int color1, int color2) {
            return color(
                    (alpha(color1) + alpha(color2)) / 2,
                    (red(color1) + red(color2)) / 2,
                    (green(color1) + green(color2)) / 2,
                    (blue(color1) + blue(color2)) / 2
            );
        }

        public static int as8BitChannel(float value)
        {
            return Mth.clamp((int) (value * 255.0f), 0, 255);
        }

        public static float alphaFloat(int color)
        {
            return from8BitChannel(alpha(color));
        }

        public static float redFloat(int color)
        {
            return from8BitChannel(red(color));
        }

        public static float greenFloat(int color)
        {
            return from8BitChannel(green(color));
        }

        public static float blueFloat(int color)
        {
            return from8BitChannel(blue(color));
        }

        private static float from8BitChannel(int value)
        {
            return value / 255.0F;
        }

        public static int toABGR(int color)
        {
            return (color & 0xFF00FF00) | ((color & 0xFF0000) >> 16) | ((color & 0xFF) << 16);
        }

        public static int fromABGR(int color)
        {
            return toABGR(color);
        }

        public static int invertRGB(int color)
        {
            return color(
                    alpha(color),
                    255 - red(color),
                    255 - green(color),
                    255 - blue(color)
            );
        }

        public static int invertARGB(int color)
        {
            return color(
                    255 - alpha(color),
                    255 - red(color),
                    255 - green(color),
                    255 - blue(color)
            );
        }

        public static int blend(int color1, int color2, float ratio) {
            ratio = Mth.clamp(ratio, 0.0F, 1.0F);
            return color(
                    (int) (alpha(color1) * (1 - ratio) + alpha(color2) * ratio),
                    (int) (red(color1) * (1 - ratio) + red(color2) * ratio),
                    (int) (green(color1) * (1 - ratio) + green(color2) * ratio),
                    (int) (blue(color1) * (1 - ratio) + blue(color2) * ratio)
            );
        }
    }
}
