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
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NorphSource extends BioBaseBlock
{
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
        BlockPos relative = pos.relative(Direction.DOWN);
        serverLevel.setBlock(relative, Registration.BlockReg.NORPH.get().getInitDefaultState(), 3);
        serverLevel.blockUpdated(pos, Blocks.AIR);
        state.updateNeighbourShapes(level, pos, 3);
        serverLevel.scheduleTick(relative, Registration.BlockReg.NORPH.get(), 200 + serverLevel.getRandom().nextInt(50) - 25);
    }

    @Override
    protected @NotNull MapCodec<NorphSource> codec()
    {
        return CODEC;
    }
}
