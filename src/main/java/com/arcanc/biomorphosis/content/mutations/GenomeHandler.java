/**
 * @author ArcAnc
 * Created at: 08.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations;


import com.arcanc.biomorphosis.content.item.GeneInjector;
import com.arcanc.biomorphosis.content.mutations.types.VampirismEffectType;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CGenomeSync;
import com.arcanc.biomorphosis.content.network.packets.S2CUnlockedGenomeSync;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GenomeHandler
{
	public static void register(@NotNull IEventBus modEventBus)
	{
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onEntityJoin);
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onStartTracking);
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: screenOpener);
		
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onPlayerLogin);
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onPlayerRespawn);
		
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onHurtEvent);
		NeoForge.EVENT_BUS.addListener(GenomeHandler :: onDieEvent);
	}
	
	private static void onPlayerLogin(final @NotNull PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!(event.getEntity() instanceof ServerPlayer serverPlayer))
			return;
		
		if (serverPlayer instanceof GenomeEffectsHolder holder)
			holder.biomorphosis$rebuildEffects();
		UnlockedGenome instance = GenomeHelper.getUnlockedGenome(serverPlayer);
		NetworkEngine.sendToPlayer(serverPlayer, new S2CUnlockedGenomeSync(instance, List.of()));
		GenomeInstance genome = GenomeHelper.getGenome(serverPlayer);
		NetworkEngine.sendToPlayer(serverPlayer, new S2CGenomeSync(serverPlayer.getUUID(), genome));
	}
	
	private static void onPlayerRespawn(final @NotNull PlayerEvent.PlayerRespawnEvent event)
	{
		if (!(event.getEntity() instanceof ServerPlayer serverPlayer))
			return;
		
		if (serverPlayer instanceof GenomeEffectsHolder holder)
			holder.biomorphosis$rebuildEffects();
		UnlockedGenome instance = GenomeHelper.getUnlockedGenome(serverPlayer);
		NetworkEngine.sendToPlayer(serverPlayer, new S2CUnlockedGenomeSync(instance, List.of()));
		GenomeInstance genome = GenomeHelper.getGenome(serverPlayer);
		NetworkEngine.sendToPlayer(serverPlayer, new S2CGenomeSync(serverPlayer.getUUID(), genome));
	}

	private static void onEntityJoin(final @NotNull EntityJoinLevelEvent event)
	{
		Entity ent = event.getEntity();
		Level level = ent.level();
		if (level.isClientSide())
			return;
			
		if (event.loadedFromDisk())
		{
			if (!(ent instanceof LivingEntity livingEntity))
				return;
			if (ent instanceof GenomeEffectsHolder holder)
				holder.biomorphosis$rebuildEffects();
		}
		else
		{
			if (!(ent instanceof LivingEntity livingEntity))
				return;
			
			GenomeHelper.getGenome(livingEntity);
			
			if (ent instanceof GenomeEffectsHolder holder)
				holder.biomorphosis$rebuildEffects();
		}
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
			RenderHelper.openGenomeScreen(event.getEntity(), livingEntity);
	}
	
	private static void onHurtEvent(final @NotNull LivingDamageEvent.Post event)
	{
		DamageSource source = event.getSource();
		Entity sourceEntity = source.getEntity();
		if (!(sourceEntity instanceof LivingEntity damager))
			return;
		
		if (!(damager.level() instanceof ServerLevel serverLevel))
			return;
		
		if (!source.isDirect())
			return;
		
		GenomeHelper.getGenome(damager).getGene(Database.GUI.Genome.VAMPIRISM.id()).
				ifPresent(geneInstance ->
				{
					GeneDefinition definition = serverLevel.registryAccess().
							lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
							getValue(geneInstance.id());
					
					if (definition == null)
						return;
					
					GeneDefinition.RarityData data = definition.rarityData().get(geneInstance.rarity());
					
					if (data == null)
						return;
					
					data.effects().forEach(entry ->
					{
						if (entry.type() instanceof VampirismEffectType effectType)
							effectType.handle(damager, event.getNewDamage(), entry.params());
					});
				});
	}
	
	private static void onDieEvent(final @NotNull LivingDeathEvent event)
	{
		LivingEntity diedEntity = event.getEntity();
		DamageSource source = event.getSource();
		Entity sourceEntity = source.getEntity();
		if (!(sourceEntity instanceof ServerPlayer killer))
			return;
		if (!source.isDirect())
			return;
		if (!killer.getItemInHand(InteractionHand.MAIN_HAND).canPerformAction(GeneInjector.INJECTOR_INJECT))
			return;
		
		UnlockedGenome unlockedGenome = GenomeHelper.getUnlockedGenome(killer);
		
		GenomeInstance entityGenome = GenomeHelper.getGenome(diedEntity);
		if (entityGenome.isEmpty())
			return;
		
		List<GeneInstance> newGenes = new ArrayList<>();
		
		for (GeneInstance gene : entityGenome.geneInstances())
			if (unlockedGenome.hasGene(gene))
			{
				if (unlockedGenome.unlockedGenes().get(gene.id()).add(gene.rarity()))
					newGenes.add(gene);
			}
			else
			{
				unlockedGenome.unlockedGenes().putIfAbsent(gene.id(), EnumSet.of(gene.rarity()));
				newGenes.add(gene);
			}
		killer.setData(Registration.DataAttachmentsReg.UNLOCKED_GENOME, unlockedGenome);
		NetworkEngine.sendToPlayer(killer, new S2CUnlockedGenomeSync(unlockedGenome, newGenes));
	}
}
