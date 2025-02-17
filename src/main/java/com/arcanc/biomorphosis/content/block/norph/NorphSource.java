/**
 * @author ArcAnc
 * Created at: 09.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.norph;

import com.arcanc.biomorphosis.content.block.BioBaseBlock;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.ZoneHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NorphSource extends BioBaseBlock
{
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;

    public static final MapCodec<NorphSource> CODEC = simpleCodec(NorphSource :: new);

    public NorphSource(Properties props)
    {
        super(props);
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
    public @NotNull BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull MapCodec<NorphSource> codec()
    {
        return CODEC;
    }
}
