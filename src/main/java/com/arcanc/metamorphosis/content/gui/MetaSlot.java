/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.gui;

import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.helper.FluidHelper;
import com.arcanc.metamorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.metamorphosis.util.inventory.item.ItemStackSidedStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class MetaSlot extends SlotItemHandler
{
    public static final ResourceLocation FRAME = Database.rl("slot/frame");
    public static final ResourceLocation MASK = Database.rl("slot/mask");

    public static final Vector4f NORMAL_SLOT_COLOR = new Vector4f(139f, 139f, 139f, 255f);

    private final AbstractContainerMenu menu;
    private boolean enabled = true;

    public MetaSlot(AbstractContainerMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }

    public @NotNull Vector4f getSlotColor()
    {
        if (this.getItemHandler() instanceof ItemStackSidedStorage storage)
            return storage.getModeForIndex(this.index).map(BasicSidedStorage.FaceMode :: getColor).orElse(NORMAL_SLOT_COLOR);
        return NORMAL_SLOT_COLOR;
    }

    public void setActive(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isActive()
    {
        return this.enabled;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack)
    {
        return super.mayPlace(stack);
    }

    @Override
    public @NotNull Slot setBackground(@NotNull ResourceLocation sprite)
    {
        return super.setBackground(sprite);
    }

    @Override
    public void set(@NotNull ItemStack stack)
    {
        IItemHandler handler = getItemHandler();
        if (!(handler instanceof ItemStackSidedStorage storage))
        {
            super.set(stack);
            return;
        }
        storage.getHolderAt(null, this.index).ifPresent(holder -> holder.setStack(stack));
    }

    @Override
    public void initialize(@NotNull ItemStack stack)
    {
        IItemHandler handler = getItemHandler();
        if (!(handler instanceof ItemStackSidedStorage storage))
        {
            super.initialize(stack);
            return;
        }
        storage.getHolderAt(null, this.index).ifPresent(holder -> holder.setStack(stack));
    }

    public static class Output extends MetaSlot
    {
        public Output(AbstractContainerMenu menu, IItemHandler itemHandler, int slot, int xPosition, int yPosition)
        {
            super(menu, itemHandler, slot, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack)
        {
            return false;
        }
    }

    public static class FluidContainer extends MetaSlot
    {
        public FluidContainer(AbstractContainerMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition)
        {
            super(menu, itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack)
        {
            return FluidHelper.getFluidHandler(stack).
                    map(handler -> handler.getTanks() <= 0).
                    orElse(false);
        }
    }

    public static class Ghost extends MetaSlot
    {
        public Ghost(AbstractContainerMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition)
        {
            super(menu, itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPickup(@NotNull Player player)
        {
             return false;
        }
    }
}
