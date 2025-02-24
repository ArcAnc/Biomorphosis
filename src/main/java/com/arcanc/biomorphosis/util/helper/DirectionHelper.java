/**
 * @author ArcAnc
 * Created at: 24.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectionHelper
{

    public static @Nullable RelativeFace getRelativeDirection(BlockState state, @Nullable Direction dir)
    {
        Direction face = DirectionHelper.getFace(state);

        if (dir == null)
            return null;
        else if (dir == Direction.UP)
            return RelativeFace.UP;
        else if (dir == Direction.DOWN)
            return RelativeFace.DOWN;
        else if (face == dir)
            return RelativeFace.FRONT;
        else if (face == dir.getOpposite())
            return RelativeFace.BACK;
        else if (face == dir.getClockWise())
            return RelativeFace.RIGHT;
        else if (face == dir.getCounterClockWise())
            return RelativeFace.LEFT;
        return RelativeFace.DOWN;
    }

    public static Direction getFace(@NotNull BlockState state)
    {
        return state.getOptionalValue(BlockHelper.BlockProperties.HORIZONTAL_FACING).
                or(() -> state.getOptionalValue(BlockHelper.BlockProperties.FACING)).
                orElse(Direction.NORTH);
    }

    public enum RelativeFace
    {
        FRONT, RIGHT, LEFT, BACK, UP, DOWN;
    }
}
