/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WrenchItem extends BioBaseItem
{
    public WrenchItem(@NotNull Properties properties)
    {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack,
                                                     @NotNull UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return BlockHelper.castTileEntity(level, pos, BlockInterfaces.IWrencheable.class).
                map(tile -> tile.onUsed(stack, context)).
                or(() -> BlockHelper.getTileEntity(level, pos).map(blockEntity ->
                        {
                            if (FluidHelper.isFluidHandler(blockEntity))
                            {
                                if (level.isClientSide())
                                    return InteractionResult.SUCCESS;

                                List<Vec3> positions = stack.has(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA)
                                        ? stack.get(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA)
                                        : new ArrayList<>();
                                if (positions == null)
                                    positions = new ArrayList<>();
                                while (positions.size() <= 1)
                                    positions.add(Vec3.ZERO);

                                if (positions.getFirst() == Vec3.ZERO)
                                    positions.set(0, pos.getBottomCenter());
                                else
                                    positions.set(1, pos.getBottomCenter());
                                stack.set(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA, positions);
                                return InteractionResult.SUCCESS;
                            }
                            return super.onItemUseFirst(stack, context);
                        })
                ).
                or(()->
                {
                    BlockState state = level.getBlockState(context.getClickedPos());
                    if (state.getBlock() instanceof BlockInterfaces.IWrencheable wrencheable)
                        return Optional.of(wrencheable.onUsed(stack, context));
                    return Optional.of(super.onItemUseFirst(stack, context));
                }).
                orElse(super.onItemUseFirst(stack, context));
    }
}
