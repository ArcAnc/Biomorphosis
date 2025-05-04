/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockHelper
{
    public static final class BlockProperties
    {
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
        public static final BooleanProperty LIT = BlockStateProperties.LIT;

        public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
        public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

        public static final BooleanProperty IS_DOUBLE = BooleanProperty.create("is_double");
    }

    public static Optional<BlockEntity> getTileEntity(BlockGetter world, Vec3 pos)
    {
        if (pos != null)
        {
            return getTileEntity(world, BlockPos.containing(pos));
        }
        return Optional.empty();
    }

    public static Optional<BlockEntity> getTileEntity(BlockGetter world, BlockPos pos)
    {
        if (world != null)
        {
            return Optional.ofNullable(world.getBlockEntity(pos));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> castTileEntity(BlockEntity tile, Class<T> to)
    {
        if (tile != null && to.isAssignableFrom(tile.getClass()))
        {
            T entity = (T) tile;
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public static <T> Optional<T> castTileEntity(BlockGetter world, BlockPos pos, Class<T> to)
    {
        if (world != null && pos != null)
        {
            return getTileEntity(world, pos).flatMap(tile -> castTileEntity(tile, to));
        }
        return Optional.empty();
    }

    public static <T> Optional<T> castTileEntity(BlockGetter world, Vec3 pos, Class<T> to)
    {
        if (world != null && pos != null)
        {
            return castTileEntity(world, BlockPos.containing(pos.x(), pos.y(), pos.z()), to);
        }
        return Optional.empty();
    }

    public static @NotNull BlockState nextDirection(@NotNull BlockState state)
    {
        if (state.hasProperty(BlockProperties.FACING))
        {
            int dirIndex = state.getValue(BlockProperties.FACING).get3DDataValue();
            return state.setValue(BlockProperties.FACING, Direction.from3DDataValue((dirIndex + 1) % Direction.values().length));
        }
        return state;
    }

    public static @NotNull BlockState nextHorizontalDirection(@NotNull BlockState state)
    {
        if (state.hasProperty(BlockProperties.HORIZONTAL_FACING))
        {
            int dirIndex = state.getValue(BlockProperties.HORIZONTAL_FACING).get2DDataValue();
            return state.setValue(BlockProperties.HORIZONTAL_FACING, Direction.from2DDataValue((dirIndex + 1) % 4));
        }
        return state;
    }

    public static @NotNull ResourceLocation getRegistryName (Block block)
    {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
