/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInterfaces
{
    public interface IWrencheable
    {
        InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx);
    }

    public interface IInteractionObject<T extends BlockEntity & IInteractionObject<T>> extends MenuProvider
    {
        @Nullable
        T getGuiMaster();

        Registration.MenuTypeReg.ArgContainer<? super T, ?> getContainerType();

        boolean canUseGui(Player player);

        default boolean isValid()
        {
            return getGuiMaster()!=null;
        }

        @Override
        default AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity)
        {
            T master = getGuiMaster();
            Preconditions.checkNotNull(master);
            Registration.MenuTypeReg.ArgContainer<? super T, ?> type = getContainerType();
            return type.create(id, playerInventory, master);
        }

        @Override
        default @NotNull Component getDisplayName()
        {
            return Component.empty();
        }
    }
}
