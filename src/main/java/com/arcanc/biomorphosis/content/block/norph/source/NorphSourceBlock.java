/**
 * @author ArcAnc
 * Created at: 09.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.norph.source;

import com.arcanc.biomorphosis.content.block.BioBaseEntityBlock;
import com.arcanc.biomorphosis.content.block.norph.NorphBlock;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.VoxelShapeHelper;
import com.arcanc.biomorphosis.util.helper.ZoneHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class NorphSourceBlock extends BioBaseEntityBlock<NorphSource>
{
    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.11875, -0.25, 0, 0.11875, 0.5625, 0.1875),
            Shapes.box(0.25, 0.4378125, 0.3125, 0.6875, 0.8753125, 0.6875),
            Shapes.box(0.125, -0.1246875, 0.375, 0.5625, 0.5003125, 0.625),
            Shapes.box(0.375, -0.1246875, 0.4375, 0.8125, 0.1253125, 0.9375),
            Shapes.box(0.375, -0.18339625, 0.15245875, 0.8125, 0.31660375, 0.40245875),
            Shapes.box(0.477903125, -0.1246875, 0.281028125, 0.915403125, 0.6878125, 0.718528125),
            Shapes.box(0.203125, 0.0003125, 0.53125, 0.390625, 0.6878125, 0.890625),
            Shapes.box(0.109375, 0.0003125, 0.21875, 0.46875, 0.6878125, 0.40625),
            Shapes.box(0.0625, 0.5, 0.0625, 0.1875, 0.625, 0.25),
            Shapes.box(0.18125, -0.25, 0.8125, 0.18125, 0.5625, 1),
            Shapes.box(0.125, 0.5, 0.75, 0.25, 0.625, 0.9375));

    private static final Map<Direction, VoxelShape> BY_DIRECTION = new EnumMap<>(Direction.class);

    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;

    public static final MapCodec<NorphSourceBlock> CODEC = simpleCodec(NorphSourceBlock :: new);

    public NorphSourceBlock(Properties props)
    {
        super(Registration.BETypeReg.BE_NORPH_SOURCE, props);

        BY_DIRECTION.put(Direction.NORTH, SHAPE);
        BY_DIRECTION.put(Direction.SOUTH, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.SOUTH));
        BY_DIRECTION.put(Direction.WEST, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.WEST));
        BY_DIRECTION.put(Direction.EAST, VoxelShapeHelper.rotateHorizontal(SHAPE, Direction.EAST));
    }

    @Override
    public void setPlacedBy(@NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState state,
                            @Nullable LivingEntity placer,
                            @NotNull ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!(level instanceof ServerLevel serverLevel))
            return;
        if (placer == null)
            return;
        for (Direction dir : Direction.values())
        {
            if (dir.getAxis().isVertical())
                continue;

            BlockPos relative = pos.relative(dir);
            BlockState placedFace = Registration.BlockReg.NORPH_OVERLAY.get().defaultBlockState().setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
            boolean canSurvive = placedFace.canSurvive(level, relative);
            if (serverLevel.getBlockState(relative).canBeReplaced() && canSurvive)
            {
                serverLevel.setBlock(relative, placedFace, Block.UPDATE_ALL);
                serverLevel.blockUpdated(pos, Blocks.AIR);
                state.updateNeighbourShapes(level, pos, NorphBlock.UPDATE_ALL);
            }
        }
    }

    @Override
    protected void randomTick(@NotNull BlockState state,
                              @NotNull ServerLevel level,
                              @NotNull BlockPos pos,
                              @NotNull RandomSource random)
    {
        boolean flag = false;
        for(Direction dir : Direction.values())
            if (dir.getAxis().isHorizontal() &&
                level.getBlockState(pos.relative(dir)).
                        is(BioBlockTags.NORPH))
                flag = true;
        if (!flag)
            ZoneHelper.getPoses(pos, ZoneHelper.RadiusOptions.of(ZoneHelper.ZoneType.SQUARE, 2, 2, 2)).findAny().
                ifPresent(checkPos ->
                {
                    BlockState checkState = level.getBlockState(checkPos);
                    BlockState toTestState = Registration.BlockReg.NORPH_OVERLAY.get().defaultBlockState().setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
                    if (!checkState.is(BioBlockTags.NORPH_SOURCE) &&
                        !checkState.is(BioBlockTags.NORPH_AVOID) &&
                        !checkState.is(BioBlockTags.NORPH) &&
                        toTestState.canSurvive(level, checkPos))
                    {
                        level.setBlockAndUpdate(checkPos, toTestState);
                        level.blockUpdated(pos, Blocks.AIR);
                        state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
                    }
                });
    }

    @Override
    protected BlockState getInitDefaultState()
    {
        return super.getInitDefaultState().setValue(HORIZONTAL_FACING, Direction.NORTH);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot)
    {
        return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
    {
        return BY_DIRECTION.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull MapCodec<NorphSourceBlock> codec()
    {
        return CODEC;
    }
}
