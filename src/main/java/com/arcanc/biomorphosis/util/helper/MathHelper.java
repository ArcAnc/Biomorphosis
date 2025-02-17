/**
 * @author ArcAnc
 * Created at: 16.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

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
}
