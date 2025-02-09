/**
 * @author ArcAnc
 * Created at: 09.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NorphBlock extends BioBaseBlock
{
    private static final int SPREAD_RANGE = 10;
    public static final MapCodec<NorphBlock> CODEC = simpleCodec(NorphBlock :: new);

    public NorphBlock(@NotNull Properties props)
    {
        super(props);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random)
    {
        BlockPos source = getSourcePos(level, pos);
        if (source == null)
        {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            return;
        }

        BlockPos[] poses = {pos.above(), pos.below(), pos.east(), pos.west(), pos.north(), pos.south()};

        BlockPos testPos = poses[level.getRandom().nextInt(poses.length)];
        if (testPos.distSqr(testPos) < SPREAD_RANGE * SPREAD_RANGE)
        {
            BlockState testState = level.getBlockState(testPos);
            if (!testState.isEmpty() &&
                    !testState.is(Registration.BlockReg.NORPH_SOURCE) &&
                    !testState.is(Registration.BlockReg.NORPH))
            {
                level.setBlock(testPos, Registration.BlockReg.NORPH.get().getInitDefaultState(), 3);
                level.blockUpdated(pos, Blocks.AIR);
                state.updateNeighbourShapes(level, pos, 3);
            }
        }
        level.scheduleTick(testPos, Registration.BlockReg.NORPH.get(), 300 + random.nextInt(220) - 100);
        level.scheduleTick(pos, this, 300 + random.nextInt(220) - 100);
    }

    private @Nullable BlockPos getSourcePos(ServerLevel level, @NotNull BlockPos pos)
    {
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-SPREAD_RANGE, -SPREAD_RANGE, -SPREAD_RANGE), pos.offset(SPREAD_RANGE, SPREAD_RANGE, SPREAD_RANGE)))
            if (level.getBlockState(checkPos).getBlock() instanceof NorphSource)
                return checkPos;
        return null;
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec()
    {
        return CODEC;
    }
}
