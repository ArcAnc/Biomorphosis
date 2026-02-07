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
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChrysalisMenu extends BioContainerMenu
{
	private final BlockPos pos;
	
	public static @NotNull ChrysalisMenu makeServer(MenuType<?> type, int id, @NotNull Inventory playerInv, MultiblockChrysalis chrysalis)
	{
		return new ChrysalisMenu(blockCtx(type, id, chrysalis), playerInv, chrysalis.getBlockPos());
	}
	
	public static @NotNull ChrysalisMenu makeClient(MenuType<?> type, int id, @NotNull Inventory playerInv, BlockPos chrysalis)
	{
		return new ChrysalisMenu(clientCtx(type, id, ContextType.BLOCK), playerInv, chrysalis);
	}
	
	private ChrysalisMenu(@NotNull MenuContext ctx, @NotNull Inventory playerInv, BlockPos chrysalisPos)
	{
		super(ctx);
		this.pos = chrysalisPos;

		Player player = playerInv.player;
		opened(player);
	}
	
	private void opened(@NotNull Player player)
	{
		if (player.level() instanceof ServerLevel serverLevel)
			BlockHelper.castTileEntity(serverLevel, this.pos, MultiblockChrysalis.class).
					ifPresent(chrysalis -> chrysalis.onInteractionStarted(player));
	}
	
	@Override
	public void removed(@NotNull Player player)
	{
		super.removed(player);
		if (player.level() instanceof ServerLevel serverLevel)
			BlockHelper.castTileEntity(serverLevel, this.pos, MultiblockChrysalis.class).
					ifPresent(chrysalis -> chrysalis.onInteractionEnd(player));
	}
	
	@Override
	protected void handleMessage(@NotNull ServerPlayer player, CompoundTag tag)
	{
		ServerLevel level = player.serverLevel();
		BlockPos bePos = TagHelper.readBlockPos(tag, "block_entity_pos");
		if (!tag.contains("genome"))
			return;
		GenomeInstance genome = GenomeInstance.CODEC.
				parse(NbtOps.INSTANCE, tag.getCompound("genome")).
				getOrThrow();
		BlockHelper.castTileEntity(level, bePos, MultiblockChrysalis.class).
				ifPresent(chrysalis -> chrysalis.tryStartMutation(player, genome));
	}
	
	@Override
	public @Nullable BlockPos getBlockPos()
	{
		return this.pos;
	}
}
