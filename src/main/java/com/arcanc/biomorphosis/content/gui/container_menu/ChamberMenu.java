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
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public class ChamberMenu extends BioContainerMenu
{
    private final BlockPos pos;

    public static ChamberMenu makeServer(MenuType<?> type, int id, Inventory playerInv, MultiblockChamber chamber)
    {
        return new ChamberMenu(blockCtx(type, id, chamber), playerInv, chamber.getBlockPos());
    }

    public static ChamberMenu makeClient(MenuType<?> type, int id, Inventory playerInv, BlockPos chamberPos)
    {
        return new ChamberMenu(clientCtx(type, id, ContextType.BLOCK), playerInv, chamberPos);
    }

    private ChamberMenu(MenuContext ctx, Inventory playerInventory, BlockPos chamberPos)
    {
        super(ctx);

        this.pos = chamberPos;

        Level level = playerInventory.player.level();
        ItemHelper.getItemHandler(level, chamberPos).ifPresent(handler ->
        {
            this.addSlot(new BioSlot.Output(this, handler, 0, 77, 13));
            for (int q = 0; q < MultiblockChamber.MAX_SLOT_AMOUNT; q++)
                this.addSlot(new BioSlot(this, handler, q + 1, 29 + (q % 6) * 19, 38 + (q / 6) * 19));
            this.ownSlotCount = 13;
        });

        this.addStandardInventorySlots(playerInventory, 8, 95);
    }

    @Override
    protected void handleMessage(ServerPlayer player, CompoundTag tag)
    {
        ServerLevel level = player.serverLevel();
        BlockPos bePos = TagHelper.readBlockPos(tag, "block_entity_pos");
        BlockHelper.castTileEntity(level, bePos, MultiblockChamber.class).
                ifPresent(MultiblockChamber :: tryStart);
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.pos;
    }
}
