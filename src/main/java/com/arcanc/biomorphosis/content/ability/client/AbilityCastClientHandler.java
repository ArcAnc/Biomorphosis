/**
 * @author ArcAnc
 * Created at: 29.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;

public final class AbilityCastClientHandler
{
	private static final Map<UUID, ClientCast> ACTIVE_CASTS = new Object2ObjectOpenHashMap<>();

	private AbilityCastClientHandler()
	{
	}

	public static void start(UUID playerId, ResourceLocation animationId, long startedAt, int castTimeTicks)
	{
		ACTIVE_CASTS.put(playerId, new ClientCast(animationId, startedAt, castTimeTicks));
	}

	public static void stop(UUID playerId)
	{
		ACTIVE_CASTS.remove(playerId);
	}

	public static boolean isCasting(Player player, ResourceLocation animationId)
	{
		ClientCast cast = ACTIVE_CASTS.get(player.getUUID());
		if (cast == null || !cast.animationId.equals(animationId))
			return false;

		if (player.level().getGameTime() < cast.startedAt + cast.castTimeTicks)
			return true;

		ACTIVE_CASTS.remove(player.getUUID(), cast);
		return false;
	}

	private record ClientCast(ResourceLocation animationId, long startedAt, int castTimeTicks)
	{
	}
}
