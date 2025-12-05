/**
 * @author ArcAnc
 * Created at: 11.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ZoneHelper
{
    public static Stream<BlockPos> getPoses(BlockPos centerPos, @NotNull ZoneHelper.RadiusOptions radiusOptions)
    {
        return radiusOptions.getZoneType().getCreator().createZone(centerPos, new Vec3i(radiusOptions.getX(), radiusOptions.getY(), radiusOptions.getZ()));
    }

    private static @NotNull Stream<BlockPos> createCircularZone(@NotNull BlockPos centerPos, @NotNull Vec3i radius)
    {
        List<BlockPos> result = new ArrayList<>();

        final int rX = radius.getX();
        final int rY = radius.getY();
        final int rZ = radius.getZ();

        final double invMaxR2X = 1.0 / (rX * rX);
        final double invMaxR2Y = 1.0 / (rY * rY);
        final double invMaxR2Z = 1.0 / (rZ * rZ);

        for (int x = -rX; x <= rX; x++)
            for (int y = -rY; y <= rY; y++)
                for (int z = -rZ; z <= rZ; z++)
                {
                    double value = (x * x) * invMaxR2X + (y * y) * invMaxR2Y + (z * z) * invMaxR2Z;
                    if (value <= 1)
                        result.add(centerPos.offset(x, y, z));
                }
        return result.stream();
    }

    private static @NotNull Stream<BlockPos> createSquareZone(@NotNull BlockPos centerPos, @NotNull Vec3i radius)
    {
        List<BlockPos> result = new ArrayList<>();

        int minX = centerPos.getX() - radius.getX();
        int minY = centerPos.getY() - radius.getY();
        int minZ = centerPos.getZ() - radius.getZ();

        int maxX = centerPos.getX() + radius.getX();
        int maxY = centerPos.getY() + radius.getY();
        int maxZ = centerPos.getZ() + radius.getZ();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    result.add(new BlockPos(x, y, z));
        return result.stream();
    }

    public static class RadiusOptions
    {
        private final ZoneType zoneType;
        private final int x;
        private final int y;
        private final int z;

        private RadiusOptions(ZoneType zoneType, int x, int y, int z)
        {
            this.zoneType = zoneType;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static @NotNull ZoneHelper.RadiusOptions of(ZoneType type, int radius)
        {
            return new RadiusOptions(type, radius, radius, radius);
        }

        public static @NotNull ZoneHelper.RadiusOptions of(ZoneType type, int horizontalRadius, int y)
        {
            return new RadiusOptions(type, horizontalRadius, y, horizontalRadius);
        }

        public static @NotNull ZoneHelper.RadiusOptions of(ZoneType type, int x, int y, int z)
        {
            return new RadiusOptions(type, x, y, z);
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public int getZ()
        {
            return z;
        }

        public ZoneType getZoneType()
        {
            return zoneType;
        }
    }

    @FunctionalInterface
    public interface ZoneCreator
    {
        Stream<BlockPos> createZone(BlockPos center, Vec3i radius);
    }

    public enum ZoneType
    {
        CIRCLE(ZoneHelper :: createCircularZone),
        SQUARE(ZoneHelper :: createSquareZone);

        private final ZoneCreator creator;

        ZoneType(ZoneCreator creator)
        {
            this.creator = creator;
        }

        public ZoneCreator getCreator()
        {
            return creator;
        }
    }
}
