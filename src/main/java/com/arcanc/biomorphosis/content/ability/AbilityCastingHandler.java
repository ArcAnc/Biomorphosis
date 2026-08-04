/**
 * @author ArcAnc
 * Created at: 29.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.AbilityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class AbilityCastingHandler
{
	private AbilityCastingHandler()
	{
	}

	public static void register()
	{
		NeoForge.EVENT_BUS.addListener(AbilityCastingHandler :: serverTick);
	}

	private static void serverTick(ServerTickEvent.Post event)
	{
		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers())
		{
			AbilityCastingState state = player.getData(Registration.DataAttachmentsReg.ABILITY_CASTING);
			if (!state.isCasting())
				continue;

			IAbility ability = state.abilityId().flatMap(AbilityHelper :: getAbility).orElse(null);
			if (ability == null)
			{
				AbilityHelper.cancelCast(player, state);
				continue;
			}

			AbilityActivationContext context = new AbilityActivationContext(player, state.slot(), ability);
			if (player.isDeadOrDying() || !ability.getType().value().canContinueCasting(context))
			{
				AbilityHelper.cancelCast(player, state);
				continue;
			}

			long gameTime = player.level().getGameTime();
			if (gameTime >= state.completesAt())
			{
				AbilityHelper.completeCast(player, state, ability);
			}
		}
	}
}
