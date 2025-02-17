/**
 * @author ArcAnc
 * Created at: 16.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.norph;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NorphStairs extends StairBlock
{
    public NorphStairs(BlockState baseState, Properties properties)
    {
        super(baseState, properties);
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston)
    {
        if (level instanceof ServerLevel serverLevel)
            serverLevel.scheduleTick(pos, this, NorphOverlay.NorphSpreadConfig.getTicksDelay(serverLevel));
    }

    @Override
    protected void onRemove(@NotNull BlockState state,
                            @NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState newState,
                            boolean movedByPiston)
    {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!level.isClientSide())
            level.setBlockAndUpdate(pos, Blocks.STONE_STAIRS.withPropertiesOf(state));
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random)
    {
        trySpread(state, level, pos, random);
        level.scheduleTick(pos, this, NorphOverlay.NorphSpreadConfig.getTicksDelay(level));
    }

    private void trySpread(@NotNull BlockState state,
                           @NotNull ServerLevel level,
                           @NotNull BlockPos pos,
                           @NotNull RandomSource random)
    {
        if (!level.isAreaLoaded(pos, 1))
            return;
        NorphOverlay.NorphSpreadConfig config = Registration.BlockReg.NORPH_OVERLAY.get().getSpreaderConfig();

        if (config.getSourcePos(level, pos) == null)
        {
            level.removeBlock(pos, false);
            return;
        }

        BlockPos[] poses =
                {
                        pos.above(),
                        pos.below(),
                        pos.east(),
                        pos.west(),
                        pos.north(),
                        pos.south()
                };

        BlockPos testPos = poses[random.nextInt(poses.length)];
        if (Registration.BlockReg.NORPH_OVERLAY.get().getSpreaderConfig().getSourcePos(level, testPos) == null)
            return;

        BlockState testState = level.getBlockState(testPos);
        if (    !testState.isAir() &&
                !testState.is(BioBlockTags.NORPH_SOURCE) &&
                !testState.is(BioBlockTags.NORPH) &&
                !testState.is(BioBlockTags.NORPH_AVOID))
        {
            level.setBlock(testPos, Registration.BlockReg.NORPH.get().defaultBlockState(), Block.UPDATE_ALL);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
        }
    }
}
