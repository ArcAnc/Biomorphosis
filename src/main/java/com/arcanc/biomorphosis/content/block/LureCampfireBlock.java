/**
 * @author ArcAnc
 * Created at: 31.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.LureCampfireBE;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class LureCampfireBlock extends BioBaseEntityBlock<LureCampfireBE>
{
    public static final BooleanProperty LIT = BlockHelper.BlockProperties.LIT;
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockHelper.BlockProperties.HORIZONTAL_FACING;

    public static final MapCodec<LureCampfireBlock> CODEC = simpleCodec(LureCampfireBlock :: new);

    public LureCampfireBlock(Properties blockProps)
    {
        super(Registration.BETypeReg.BE_LURE_CAMPFIRE, blockProps);
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
        return BlockHelper.castTileEntity(level, pos, LureCampfireBE.class).
                map(lureCampfireBE ->
                {
                    if (!(level instanceof ServerLevel serverLevel))
                        return InteractionResult.SUCCESS;
                    if (stack.is(Registration.BlockReg.FLESH.asItem()))
                    {
                        LureCampfireBE.UsingResult result = lureCampfireBE.addMeat(stack, player);
                        if (result.added())
                            return InteractionResult.SUCCESS_SERVER.heldItemTransformedTo(result.stack());
                    }
                    else if (stack.is(Tags.Items.TOOLS_IGNITER))
                    {
                        boolean isLit = state.getValue(LIT);
                        if (!isLit)
                        {
                            stack.hurtAndBreak(1, serverLevel, player, item -> {});
                            serverLevel.setBlockAndUpdate(pos, state.setValue(LIT, true));
                            return InteractionResult.SUCCESS_SERVER;
                        }
                    }
                    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
                }).
                orElse(super.useItemOn(stack, state, level, pos, player, hand, hitResult));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult)
    {
        return BlockHelper.castTileEntity(level, pos, LureCampfireBE.class).
            map(lureCampfireBE ->
            {
                if (state.getValue(LIT))
                {
                    level.setBlockAndUpdate(pos, state.setValue(LIT, false));
                    return sidedSuccess(level.isClientSide()).withoutItem();
                }
                return InteractionResult.PASS;
            }).
                orElse(InteractionResult.PASS);
    }

    @Override
    protected BlockState getInitDefaultState()
    {
        return super.getInitDefaultState().setValue(LIT, false).setValue(HORIZONTAL_FACING, Direction.NORTH);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()).setValue(LIT, false);
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
        builder.add(LIT, HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull MapCodec<LureCampfireBlock> codec()
    {
        return CODEC;
    }
}
