/**
 * @author ArcAnc
 * Created at: 03.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.trades;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.content.worldgen.srf.orders.PalladinOrder;
import com.arcanc.biomorphosis.content.worldgen.srf.orders.PalladinOrders;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Trades
{
	//FIXME: whole structure requires total redesign. This shit was created just to fast production and nothing more
	
	private static final Map<ResourceKey<PalladinOrder>, Map<PalladinTraderType, VillagerTrades.ItemListing[]>> PALLADIN_TRADES = new HashMap<>();
	
	private static void registerTrades(final @NotNull ServerAboutToStartEvent event)
	{
		PALLADIN_TRADES.put(PalladinOrders.BASE_ORDER, ImmutableMap.<PalladinTraderType, VillagerTrades.ItemListing[]>builder().
				put(PalladinTraderType.CAPTAIN, new VillagerTrades.ItemListing[]
				{
						new VillagerTrades.EmeraldForItems(Items.WHEAT, 20, 999, 2),
						new VillagerTrades.EmeraldForItems(Items.POTATO, 26, 999, 2),
						new VillagerTrades.EmeraldForItems(Items.CARROT, 22, 999, 2),
						new VillagerTrades.EmeraldForItems(Items.BEETROOT, 15, 999, 2),
						new VillagerTrades.EmeraldForItems(Blocks.PUMPKIN, 6, 999, 10),
						new VillagerTrades.EmeraldForItems(Blocks.MELON, 4, 999, 20),
						new VillagerTrades.EmeraldForItems(Registration.ItemReg.QUEENS_BRAIN, 1, 5, 25, 15),
						new VillagerTrades.EmeraldForItems(Registration.ItemReg.ANTENNAS, 2, 999, 25, 3),
						new VillagerTrades.EmeraldForItems(Registration.ItemReg.GUARD_ARMOR_PIECE, 4, 999, 25, 3),
						new VillagerTrades.EmeraldForItems(Registration.ItemReg.SWARMLING_HEAD, 1, 999, 25, 3),
						new VillagerTrades.EmeraldForItems(Registration.ItemReg.ZIRIS_WING, 2, 999, 25, 3),
						new VillagerTrades.EmeraldForItems(Registration.ItemReg.INFESTOR_STING, 1, 999, 25, 3)
				}).
				put(PalladinTraderType.BLACKSMITH, new VillagerTrades.ItemListing[]
				{
						new VillagerTrades.EmeraldForItems(Items.COAL, 15, 999, 2),
						new VillagerTrades.EmeraldForItems(Items.IRON_INGOT, 5, 100, 2),
						new VillagerTrades.EmeraldForItems(Items.IRON_BOOTS, 1, 100, 5, 4),
						new VillagerTrades.EmeraldForItems(Items.IRON_HELMET, 1, 100, 5, 5),
						new VillagerTrades.EmeraldForItems(Items.IRON_CHESTPLATE, 1, 100, 5, 9),
						new VillagerTrades.EmeraldForItems(Items.IRON_LEGGINGS, 1, 100, 5, 7),
						new VillagerTrades.EmeraldForItems(Items.IRON_AXE, 1, 100, 1, 3),
						new VillagerTrades.EmeraldForItems(Items.IRON_SHOVEL, 1, 100, 1, 3),
						new VillagerTrades.EmeraldForItems(Items.IRON_HOE, 1, 100, 1, 3),
						new VillagerTrades.EmeraldForItems(Items.IRON_PICKAXE, 1, 100, 1, 3),
						new VillagerTrades.EmeraldForItems(Items.LAVA_BUCKET, 1, 100, 20),
						new VillagerTrades.EmeraldForItems(Items.DIAMOND, 1, 100, 30),
						new VillagerTrades.EmeraldForItems(Items.ARROW, 16, 100, 1),
						new VillagerTrades.EmeraldForItems(Items.BOW, 1, 100, 5, 2)
				}).build());
		
	}
	
	public static VillagerTrades.ItemListing[] getOffers(ResourceKey<PalladinOrder> order, PalladinTraderType traderType)
	{
		return PALLADIN_TRADES.get(order).get(traderType);
	}
	
	public static void register(@NotNull IEventBus modEventBus)
	{
		NeoForge.EVENT_BUS.addListener(Trades :: registerTrades);
	}
	
	public enum PalladinTraderType
	{
		CAPTAIN, BLACKSMITH;
	}
}
