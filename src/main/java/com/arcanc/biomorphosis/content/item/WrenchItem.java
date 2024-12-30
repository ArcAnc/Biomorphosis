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
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

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
                            //FIXME: добавить работу с жижами
                            /*if (FluidHelper.isFluidHandler(blockEntity))
                                if (!stack.has(NRegistration.NDataComponents.POSITION))
                                {
                                    stack.set(NRegistration.NDataComponents.POSITION, pos);
                                    return InteractionResult.sidedSuccess(level.isClientSide());
                                }
                            */
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
