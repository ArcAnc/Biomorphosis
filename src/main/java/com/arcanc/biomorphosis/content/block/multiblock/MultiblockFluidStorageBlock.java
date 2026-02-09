/**
 * @author ArcAnc
 * Created at: 16.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.base.type.DynamicMultiblockPartBlock;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class MultiblockFluidStorageBlock extends DynamicMultiblockPartBlock<MultiblockFluidStorage>
{
    public MultiblockFluidStorageBlock(Properties props)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_FLUID_STORAGE, props);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack,
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
                return ItemInteractionResult.SUCCESS;
            else
                return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection()) ?
                        ItemInteractionResult.CONSUME :
                        ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
