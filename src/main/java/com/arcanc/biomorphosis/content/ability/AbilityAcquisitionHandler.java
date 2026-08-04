/**
 * @author ArcAnc
 * Created at: 31.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CUnlockedAbilities;
import com.arcanc.biomorphosis.util.helper.AbilityHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class AbilityAcquisitionHandler
{
	private AbilityAcquisitionHandler()
	{
	}

	public static void register()
	{
		NeoForge.EVENT_BUS.addListener(AbilityAcquisitionHandler :: onLivingDeath);
	}

	private static void onLivingDeath(LivingDeathEvent event)
	{
		if (event.getEntity().level().isClientSide())
			return;

		Entity attacker = event.getSource().getEntity();
		if (!(attacker instanceof ServerPlayer player))
			return;

		AbilityLoadout loadout = AbilityHelper.getLoadout(player);
		AbilityLoadout updatedLoadout = loadout;
		List<ResourceLocation> newlyUnlockedAbilities = new ArrayList<>();
		Set<ResourceLocation> abilities = AbilityHelper.getInnateAbilities(event.getEntity());
		for (ResourceLocation abilityId : abilities)
			if (!updatedLoadout.hasAbility(abilityId))
			{
				updatedLoadout = updatedLoadout.withUnlockedAbility(abilityId);
				newlyUnlockedAbilities.add(abilityId);
			}

		if (!newlyUnlockedAbilities.isEmpty())
		{
			player.setData(Registration.DataAttachmentsReg.ABILITY_LOADOUT, updatedLoadout);
			NetworkEngine.sendToPlayer(player, new S2CUnlockedAbilities(newlyUnlockedAbilities));
		}
	}
}
