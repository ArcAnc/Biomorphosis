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
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class BioFluidStorageBlock extends BioNorphDependentBlock<BioFluidStorage>
{

    private static final VoxelShape SHAPE = Shapes.or(
            box(3, 2, 3, 13, 14, 13),
            box(1, 14, 1, 15, 16, 15),
            box(1, 0, 1, 15, 2, 15),
            box(2,2,2, 4, 5, 4),
            box(2, 11, 2, 4, 14, 4),
            box(2, 11, 12, 4, 14, 14),
            box(2, 2, 12, 4, 5, 14),
            box(12, 11, 12, 14, 14, 14),
            box(12, 2, 12, 14, 5, 14),
            box(12, 11, 2, 14, 14, 4),
            box(12, 2, 2, 14,5, 4));

    public static final MapCodec<BioFluidStorageBlock> CODEC = simpleCodec(BioFluidStorageBlock :: new);

    public BioFluidStorageBlock(Properties blockProps)
    {
        super(BioFluidStorage :: new, blockProps);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack,
                                                   @NotNull BlockState state,
                                                   @NotNull Level level,
                                                   @NotNull BlockPos pos,
                                                   @NotNull Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult)
    {
        if (FluidHelper.isFluidHandler(stack))
        {
            if (level.isClientSide())
                return InteractionResult.SUCCESS;
            else
                return FluidUtil.interactWithFluidHandler(player, hand, level, pos, null) ?
                        InteractionResult.SUCCESS_SERVER :
                        InteractionResult.TRY_WITH_EMPTY_HAND;

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
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
