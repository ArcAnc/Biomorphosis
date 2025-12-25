/**
 * @author ArcAnc
 * Created at: 19.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.container_menu;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockTurret;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class TurretMenu extends BioContainerMenu
{
	private final BlockPos pos;
	
	public static @NotNull TurretMenu makeServer(MenuType<?> type, int id, @NotNull Inventory playerInv, MultiblockTurret turret)
	{
		return new TurretMenu(blockCtx(type, id, turret), playerInv, turret.getBlockPos());
	}
	
	public static @NotNull TurretMenu makeClient(MenuType<?> type, int id, @NotNull Inventory playerInv, BlockPos turretPos)
	{
		return new TurretMenu(clientCtx(type, id, ContextType.BLOCK), playerInv, turretPos);
	}
	
	private TurretMenu(@NotNull MenuContext ctx, @NotNull Inventory playerInventory, BlockPos turretPos)
	{
		super(ctx);
		
		this.pos = turretPos;
		
		/*Level level = playerInventory.player.level();
		ItemHelper.getItemHandler(level, turretPos).ifPresent(handler ->
		{
			this.addSlot(new BioSlot.Output(this, handler, 0, 77, 13));
			for (int q = 0; q < MultiblockChamber.MAX_SLOT_AMOUNT; q++)
				this.addSlot(new BioSlot(this, handler, q + 1, 29 + (q % 6) * 19, 38 + (q / 6) * 19));
			this.ownSlotCount = 13;
		});*/
		
		this.addStandardInventorySlots(playerInventory, 8, 95);
	}
	
	@Override
	protected void handleMessage(@NotNull ServerPlayer player, CompoundTag tag)
	{
		ServerLevel level = player.serverLevel();
		BlockPos bePos = TagHelper.readBlockPos(tag, "block_entity_pos");
		BlockHelper.castTileEntity(level, bePos, MultiblockTurret.class).
				ifPresent(turret ->
				{
					if (tag.contains("new_effect"))
					{
						MultiblockTurret.TurretEffect newEffect = MultiblockTurret.TurretEffect.values()[tag.getInt("new_effect")];
						turret.setShootEffect(newEffect);
					}
					if (tag.contains("new_mode"))
					{
						MultiblockTurret.TargetMode newMode = MultiblockTurret.TargetMode.values()[tag.getInt("new_mode")];
						turret.setTargetMode(newMode);
					}
					if (tag.contains("power"))
						turret.setEnabled(tag.getBoolean("power"));
				});
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return this.pos;
	}
}
