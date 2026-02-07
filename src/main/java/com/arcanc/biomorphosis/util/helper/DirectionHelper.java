/**
 * @author ArcAnc
 * Created at: 24.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectionHelper
{

    public static @Nullable BasicSidedStorage.RelativeFace getRelativeDirection(BlockState state, @Nullable Direction dir)
    {
        Direction face = DirectionHelper.getFace(state);

        if (dir == null)
            return null;
        else if (dir == Direction.UP)
            return BasicSidedStorage.RelativeFace.UP;
        else if (dir == Direction.DOWN)
            return BasicSidedStorage.RelativeFace.DOWN;
        else if (face == dir)
            return BasicSidedStorage.RelativeFace.FRONT;
        else if (face == dir.getOpposite())
            return BasicSidedStorage.RelativeFace.BACK;
        else if (face == dir.getClockWise())
            return BasicSidedStorage.RelativeFace.RIGHT;
        else if (face == dir.getCounterClockWise())
            return BasicSidedStorage.RelativeFace.LEFT;
        return BasicSidedStorage.RelativeFace.DOWN;
    }

    public static Direction getFace(@NotNull BlockState state)
    {
        return state.getOptionalValue(BlockHelper.BlockProperties.HORIZONTAL_FACING).
                or(() -> state.getOptionalValue(BlockHelper.BlockProperties.FACING)).
                orElse(Direction.NORTH);
    }

    public static Rotation rotationFromNorth(@NotNull Direction direction)
    {
        return switch (direction)
        {
            case NORTH -> Rotation.NONE;
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> throw new IllegalArgumentException("Unsupported direction: " + direction);
        };
    }

    public static BlockPos rotatePosition(BlockPos pos, @NotNull Direction direction)
    {
        return switch (direction)
        {
            case SOUTH -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            case WEST  -> new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
            case EAST  -> new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
            default    -> pos;
        };
    }

}
