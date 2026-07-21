/**
 * @author ArcAnc
 * Created at: 09.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.targeting;

import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class SwarmTargeting
{
	public static boolean isSwarmMember(@Nullable LivingEntity entity)
	{
		return entity != null && entity.getType().is(BioEntityTags.SWARM);
	}

	public static boolean isValidSwarmEnemy(@Nullable LivingEntity owner, @Nullable LivingEntity target)
	{
		if (target == null || target == owner || !target.isAlive())
			return false;
		if (target.getType().is(BioEntityTags.SWARM))
			return false;
		if (target instanceof Creeper)
			return false;
		return !(target instanceof Player player) || (!player.isCreative() && !player.isSpectator());
	}
}
