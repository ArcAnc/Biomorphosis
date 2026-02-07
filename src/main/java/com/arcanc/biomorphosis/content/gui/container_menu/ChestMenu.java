/**
 * @author ArcAnc
 * Created at: 20.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.container_menu;


import com.arcanc.biomorphosis.content.block.block_entity.BioChest;
import com.arcanc.biomorphosis.content.gui.BioSlot;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChestMenu extends BioContainerMenu
{
	private final BlockPos pos;
	public final List<UUID> usingPlayers = new ArrayList<>();

	public static @NotNull ChestMenu makeServer(MenuType<?> type, int id, @NotNull Inventory playerInv, BioChest chest)
	{
		return new ChestMenu(blockCtx(type, id, chest), playerInv, chest.getBlockPos());
	}

	public static @NotNull ChestMenu makeClient(MenuType<?> type, int id, @NotNull Inventory playerInv, BlockPos chestPos)
	{
		return new ChestMenu(clientCtx(type, id, ContextType.BLOCK), playerInv, chestPos);
	}

	private ChestMenu(@NotNull MenuContext ctx, @NotNull Inventory playerInventory, BlockPos chestPos)
	{
		super(ctx);

		this.pos = chestPos;

		Level level = playerInventory.player.level();
		ItemHelper.getItemHandler(level, chestPos).ifPresent(handler ->
		{
			for (int q = 0; q < BioChest.MAX_SLOT_AMOUNT; q++)
				this.addSlot(new BioSlot(this, handler, q, 10 + (q % 9) * 19, 10 + (q / 9) * 19));
			this.ownSlotCount = BioChest.MAX_SLOT_AMOUNT;
		});

		this.addStandardInventorySlots(playerInventory, 13, 95);
	}

	private static void onOpen(final PlayerContainerEvent.@NotNull Open event)
	{
		if (event.getContainer() instanceof ChestMenu container && event.getEntity() instanceof ServerPlayer player)
		{
			container.usingPlayers.add(player.getUUID());
			if (container.usingPlayers.size()  == 1)
				BlockHelper.castTileEntity(player.level(), container.getBlockPos(), BioChest.class).
					ifPresent(BioChest :: open);
		}
	}

	private static void onClose(final PlayerContainerEvent.@NotNull Close event)
	{
		if (event.getContainer() instanceof ChestMenu container && event.getEntity() instanceof ServerPlayer player)
		{
			container.usingPlayers.remove(player.getUUID());
			if (container.usingPlayers.isEmpty())
				BlockHelper.castTileEntity(player.level(), container.getBlockPos(), BioChest.class).
						ifPresent(BioChest :: close);
		}
	}

	public static void registerEvents()
	{
		NeoForge.EVENT_BUS.addListener(ChestMenu :: onOpen);
		NeoForge.EVENT_BUS.addListener(ChestMenu :: onClose);
	}

	@Override
	public @Nullable BlockPos getBlockPos()
	{
		return this.pos;
	}

	@Override
	protected void handleMessage(ServerPlayer player, CompoundTag tag)
	{
	}
}
