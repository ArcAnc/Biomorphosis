/**
 * @author ArcAnc
 * Created at: 08.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.container_menu;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GenomeMenu extends BioContainerMenu
{
	private final LivingEntity entity;
	
	public static @NotNull GenomeMenu makeServer(MenuType<?> menuType, int id, @NotNull Inventory playerInv, LivingEntity entity)
	{
		return new GenomeMenu(entityCtx(menuType, id, entity), playerInv, entity);
	}
	
	public static @NotNull GenomeMenu makeClient(MenuType<?> menuType, int id, @NotNull Inventory playerInv, LivingEntity entity)
	{
		return new GenomeMenu(clientCtx(menuType, id, ContextType.ENTITY), playerInv, entity);
	}
	
	private GenomeMenu(MenuContext ctx, Inventory playerInv, LivingEntity entity)
	{
		super(ctx);
		
		this.entity = entity;
		
		this.addStandardInventorySlots(playerInv, 13, 95);
	}
	
	@Override
	protected void handleMessage(ServerPlayer player, CompoundTag tag)
	{
	}
	
	@Override
	public @Nullable UUID getUuid()
	{
		return this.entity.getUUID();
	}
}
