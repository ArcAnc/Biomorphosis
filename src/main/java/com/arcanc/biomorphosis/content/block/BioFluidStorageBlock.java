/**
 * @author ArcAnc
 * Created at: 22.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.BioFluidStorage;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BioFluidStorageBlock extends BioNorphDependentBlock<BioFluidStorage>
{

    private static final VoxelShape SHAPE = box(3, 0, 3, 13, 16, 16);

    public static final MapCodec<BioFluidStorageBlock> CODEC = simpleCodec(BioFluidStorageBlock :: new);

    public BioFluidStorageBlock(Properties blockProps)
    {
        super(BioFluidStorage :: new, blockProps);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state,
                                           @NotNull BlockGetter level,
                                           @NotNull BlockPos pos,
                                           @NotNull CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected @NotNull MapCodec<BioFluidStorageBlock> codec()
    {
        return CODEC;
    }
}
