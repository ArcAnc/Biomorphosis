/**
 * @author ArcAnc
 * Created at: 22.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.BioCatcher;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class BioCatcherBlock extends BioNorphDependentBlock<BioCatcher>
{
    public static final MapCodec<BioCatcherBlock> CODEC = simpleCodec(BioCatcherBlock :: new);
    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.125, 0, 0.125, 0.875, 0.0625, 0.875),
            Shapes.box(0.125, 0, 0, 0.875, 0.0625, 0.125),
            Shapes.box(0.1875, 0.0625, 0.8125, 0.8125, 0.1875, 0.9375),
            Shapes.box(0.3125, 0.0625, 0.9375, 0.375, 0.25, 1),
            Shapes.box(0.125, 0, 0.875, 0.875, 0.0625, 1),
            Shapes.box(0.875, 0, 0.125, 1, 0.0625, 0.875),
            Shapes.box(0, 0, 0.125, 0.125, 0.0625, 0.875),
            Shapes.box(0.1875, 0.0625, 0.0625, 0.8125, 0.1875, 0.1875),
            Shapes.box(0.8125, 0.0625, 0.1875, 0.9375, 0.1875, 0.8125),
            Shapes.box(0.0625, 0.0625, 0.1875, 0.1875, 0.1875, 0.8125),
            Shapes.box(0.625, 0.0625, 0.9375, 0.6875, 0.25, 1),
            Shapes.box(0.9375, 0.0625, 0.625, 1, 0.25, 0.6875),
            Shapes.box(0.9375, 0.0625, 0.3125, 1, 0.25, 0.375),
            Shapes.box(0.3125, 0.0625, 0, 0.375, 0.25, 0.0625),
            Shapes.box(0.625, 0.0625, 0, 0.6875, 0.25, 0.0625),
            Shapes.box(0, 0.0625, 0.625, 0.0625, 0.25, 0.6875),
            Shapes.box(0, 0.0625, 0.3125, 0.0625, 0.25, 0.375));

    public BioCatcherBlock(Properties props)
    {
        super(Registration.BETypeReg.BE_CATCHER, props);
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
                return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection()) ?
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
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull MapCodec<BioCatcherBlock> codec()
    {
        return CODEC;
    }
}
