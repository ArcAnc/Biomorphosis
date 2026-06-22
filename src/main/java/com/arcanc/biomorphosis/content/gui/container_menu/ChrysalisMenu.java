/**
 * @author ArcAnc
 * Created at: 16.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.container_menu;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChrysalis;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorType;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChrysalisMenu extends BioContainerMenu
{
	private final BlockPos pos;
	
	public static ChrysalisMenu makeServer(MenuType<?> type, int id, Inventory playerInv, MultiblockChrysalis chrysalis)
	{
		return new ChrysalisMenu(blockCtx(type, id, chrysalis), playerInv, chrysalis.getBlockPos());
	}
	
	public static ChrysalisMenu makeClient(MenuType<?> type, int id, Inventory playerInv, BlockPos chrysalis)
	{
		return new ChrysalisMenu(clientCtx(type, id, ContextType.BLOCK), playerInv, chrysalis);
	}
	
	private ChrysalisMenu(MenuContext ctx, Inventory playerInv, BlockPos chrysalisPos)
	{
		super(ctx);
		this.pos = chrysalisPos;

		Player player = playerInv.player;
		opened(player);
	}
	
	private void opened(Player player)
	{
		if (player.level() instanceof ServerLevel serverLevel)
			BlockHelper.castTileEntity(serverLevel, this.pos, MultiblockChrysalis.class).
					ifPresent(chrysalis -> chrysalis.onInteractionStarted(player));
	}
	
	@Override
	public void removed(Player player)
	{
		super.removed(player);
		if (player.level() instanceof ServerLevel serverLevel)
			BlockHelper.castTileEntity(serverLevel, this.pos, MultiblockChrysalis.class).
					ifPresent(chrysalis -> chrysalis.onInteractionEnd(player));
	}
	
	@Override
	protected void handleMessage(ServerPlayer player, CompoundTag tag)
	{
		ServerLevel level = player.serverLevel();
		BlockPos bePos = TagHelper.readBlockPos(tag, "block_entity_pos");
		if (!bePos.equals(this.pos))
			return;

		if (tag.contains("organic_armor_slot") && tag.contains("organic_armor_action"))
		{
			handleOrganicArmorAction(player, tag.getString("organic_armor_slot"), tag.getString("organic_armor_action"));
			return;
		}

		if (!tag.contains("genome"))
			return;
		GenomeInstance genome = GenomeInstance.CODEC.
				parse(NbtOps.INSTANCE, tag.getCompound("genome")).
				getOrThrow();
		if (!GenomeHelper.validateMutation(player, genome).valid())
			return;
		BlockHelper.castTileEntity(level, bePos, MultiblockChrysalis.class).
				ifPresent(chrysalis -> chrysalis.tryStartMutation(player, genome));
	}

	private void handleOrganicArmorAction(ServerPlayer player, String slotName, String actionName)
	{
		EquipmentSlot slot;
		try
		{
			slot = EquipmentSlot.byName(slotName);
		}
		catch (IllegalArgumentException ex)
		{
			return;
		}
		if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR)
			return;

		MultiblockChrysalis.ArmorAction action;
		try
		{
			action = MultiblockChrysalis.ArmorAction.valueOf(actionName);
		}
		catch (IllegalArgumentException ex)
		{
			return;
		}

		switch (action)
		{
			case EQUIP ->
			{
				ItemStack stack = player.getItemBySlot(slot);
				ResourceKey<OrganicArmorType> typeKey = OrganicArmorHelper.findTypeForSource(player.registryAccess(), stack).orElse(null);
				if (typeKey != null)
					OrganicArmorHelper.install(player, typeKey, stack);
			}
			case UNEQUIP -> OrganicArmorHelper.uninstall(player, slot);
		}
	}
	
	@Override
	public @Nullable BlockPos getBlockPos()
	{
		return this.pos;
	}
}
