/**
 * @author ArcAnc
 * Created at: 31.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.container_menu;

import com.arcanc.biomorphosis.content.block.block_entity.BioBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * This code was taken from <a href="https://github.com/BluSunrize/ImmersiveEngineering/tree/1.21.1">Immersive Engineering</a>
 * <p>BluSunrize is original author. Yes, bro, you are not forgotten</p>
 * <p>Modified by ArcAnc</p>
 */

public abstract class BioContainerMenu extends AbstractContainerMenu //implements IScreenMessageReceive
{

    private final Runnable setChanged;
    private final Predicate<Player> isValid;

    protected BioContainerMenu(@NotNull MenuContext ctx)
    {
        super(ctx.type, ctx.id);
        this.setChanged = ctx.setChanged;
        this.isValid = ctx.isValid;
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return isValid.test(player);
    }

    protected static @NotNull MenuContext blockCtx(MenuType<?> pMenuType, int pContainerId, BlockEntity be)
    {
        return new MenuContext(pMenuType, pContainerId, () ->
        {
            be.setChanged();
            if(be instanceof BioBaseBlockEntity bioBE)
                bioBE.markContainingBlockForUpdate(null);
        }, p ->
        {
            BlockPos pos = be.getBlockPos();
            Level level = be.getLevel();
            if(level==null||level.getBlockEntity(pos)!=be)
                return false;
            else
                return !(p.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64.0D);
        });
    }

    protected static @NotNull MenuContext itemCtx(
            MenuType<?> pMenuType, int pContainerId, Inventory playerInv, EquipmentSlot slot, ItemStack stack
    )
    {
        return new MenuContext(pMenuType, pContainerId, () -> {
        }, p -> {
            if(p != playerInv.player)
                return false;
            return ItemStack.isSameItem(p.getItemBySlot(slot), stack);
        });
    }

    protected static @NotNull MenuContext clientCtx(MenuType<?> pMenuType, int pContainerId)
    {
        return new MenuContext(pMenuType, pContainerId, () -> {
        }, $ -> true);
    }

    protected record MenuContext(
            MenuType<?> type, int id, Runnable setChanged, Predicate<Player> isValid
    )
    {
    }
}
