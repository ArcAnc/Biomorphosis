/**
 * @author ArcAnc
 * Created at: 08.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations;


import com.arcanc.biomorphosis.content.gui.screen.container.GenomeScreen;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CGenomeSync;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class GenomeHandler
{
	public static void register(@NotNull IEventBus modEventBus)
	{
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onEntityJoin);
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onStartTracking);
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: screenOpener);
	}
	
	private static void onEntityJoin(final @NotNull EntityJoinLevelEvent event)
	{
		Entity ent = event.getEntity();
		Level level = ent.level();
		if (level.isClientSide())
			return;
		
		if (event.loadedFromDisk())
			return;
		
		if (!(ent instanceof LivingEntity livingEntity))
			return;
		
		GenomeHelper.getGenome(livingEntity);
	}
	
	//FIXME: по идеи будет автосинк. Вероятно это будет лишним
	private static void onStartTracking(final @NotNull PlayerEvent.StartTracking event)
	{
		if(!(event.getTarget() instanceof LivingEntity livingEntity))
			return;
		if (!(event.getEntity() instanceof ServerPlayer serverPlayer))
			return;
		
		GenomeInstance instance = GenomeHelper.getGenome(livingEntity);
		NetworkEngine.sendToPlayer(serverPlayer, new S2CGenomeSync(livingEntity.getUUID(), instance));
	}
	
	private static void screenOpener(final @NotNull PlayerInteractEvent.EntityInteract event)
	{
		if (!(event.getTarget() instanceof LivingEntity livingEntity))
			return;
		if (!event.getEntity().level().isClientSide())
			return;
		if (event.getItemStack().is(Items.DEBUG_STICK))
			RenderHelper.openGenomeScreen(livingEntity);
	}
}
