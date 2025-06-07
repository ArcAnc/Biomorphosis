/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.container_menu;

import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.biomorphosis.content.gui.BioSlot;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ChamberMenu extends BioContainerMenu
{
    private final BlockPos pos;

    public static @NotNull ChamberMenu makeServer(MenuType<?> type, int id, @NotNull Inventory playerInv, MultiblockChamber chamber)
    {
        return new ChamberMenu(blockCtx(type, id, chamber), playerInv, chamber.getBlockPos());
    }

    public static @NotNull ChamberMenu makeClient(MenuType<?> type, int id, @NotNull Inventory playerInv, BlockPos chamberPos)
    {
        return new ChamberMenu(clientCtx(type, id, ContextType.BLOCK), playerInv, chamberPos);
    }

    private ChamberMenu(@NotNull MenuContext ctx, @NotNull Inventory playerInventory, BlockPos chamberPos)
    {
        super(ctx);

        this.pos = chamberPos;

        Level level = playerInventory.player.level();
        ItemHelper.getItemHandler(level, chamberPos).ifPresent(handler ->
        {
            this.addSlot(new BioSlot.Output(this, handler, 0, 77, 13));
            for (int q = 0; q < 12; q++)
                this.addSlot(new BioSlot(this, handler, q + 1, 29 + (q % 6) * 19, 38 + (q / 6) * 19));
            this.ownSlotCount = 13;
        });

        this.addStandardInventorySlots(playerInventory, 8, 95);
    }

    @Override
    protected void handleMessage(ServerPlayer player, @NotNull CompoundTag tag)
    {
    }

    @Override
    protected BlockPos getBlockPos()
    {
        return pos;
    }
}
