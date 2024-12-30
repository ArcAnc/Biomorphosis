/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioBaseBlock extends Block implements SimpleWaterloggedBlock, BlockInterfaces.IWrencheable
{
    public BioBaseBlock(Properties props)
    {
        super(props);

        this.registerDefaultState(getInitDefaultState());
    }

    protected BlockState getInitDefaultState ()
    {
        BlockState state = this.stateDefinition.any();
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED))
        {
            state = state.setValue(BlockHelper.BlockProperties.WATERLOGGED, Boolean.FALSE);
        }
        return state;
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, int eventId, int eventParam)
    {
        if (world.isClientSide() && eventId == 255)
        {
            world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return true;
        }
        return super.triggerEvent(state, world, pos, eventId, eventParam);
    }

    /**
     * WATERLOGGING
     */

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        BlockState state = this.defaultBlockState();
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED))
        {
            return state.setValue(BlockHelper.BlockProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        }
        return state;
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state,
                                              @NotNull LevelReader level,
                                              @NotNull ScheduledTickAccess scheduledTickAccess,
                                              @NotNull BlockPos pos,
                                              @NotNull Direction direction,
                                              @NotNull BlockPos neighborPos,
                                              @NotNull BlockState neighborState,
                                              @NotNull RandomSource random)
    {
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && state.getValue(BlockHelper.BlockProperties.WATERLOGGED))
        {
            scheduledTickAccess.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, pos, Fluids.WATER.getTickDelay(level), 0));
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state)
    {
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && state.getValue(BlockHelper.BlockProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid liquid)
    {
        return state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && SimpleWaterloggedBlock.super.canPlaceLiquid(player, world, pos, state, liquid);
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState liquid)
    {
        return state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, liquid);
    }

    @Override
    public @NotNull ItemStack pickupBlock(@Nullable Player player, @NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockState state)
    {
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED))
        {
            return SimpleWaterloggedBlock.super.pickupBlock(player, world, pos, state);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull InteractionResult onUsed(@NotNull ItemStack stack, @NotNull UseOnContext ctx)
    {
        return InteractionResult.PASS;
    }
}
