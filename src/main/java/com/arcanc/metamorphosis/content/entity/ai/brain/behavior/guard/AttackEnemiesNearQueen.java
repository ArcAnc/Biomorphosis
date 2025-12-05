/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity.ai.brain.behavior.guard;


import com.arcanc.metamorphosis.content.entity.QueenGuard;
import com.arcanc.metamorphosis.content.entity.ai.brain.GuardBrain;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.tags.base.MetaEntityTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public class AttackEnemiesNearQueen extends Behavior<QueenGuard>
{
	public AttackEnemiesNearQueen()
	{
		super(Map.of(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), MemoryStatus.REGISTERED));
	}

	@Override
	protected void tick(ServerLevel level, QueenGuard owner, long gameTime)
	{
		GuardBrain.getQueen(level, owner).ifPresentOrElse(queen ->
		{
			List<LivingEntity> enemies = level.getEntitiesOfClass(
					LivingEntity.class,
					queen.getBoundingBox().inflate(32),
					target -> isValidTarget(target, owner));
			if (!enemies.isEmpty())
				owner.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, enemies.getFirst());
		}, () ->
		{
			List<LivingEntity> enemies = level.getEntitiesOfClass(
					LivingEntity.class,
					owner.getBoundingBox().inflate(16),
					target -> isValidTarget(target, owner)
			);
			if (!enemies.isEmpty())
				owner.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, enemies.getFirst());
		});
	}

	private boolean isValidTarget(LivingEntity entity, QueenGuard guard)
	{
		if (entity instanceof Creeper)
			return false;
		if (entity instanceof Player player)
			return !player.isCreative();
		return !entity.getType().is(MetaEntityTags.SWARM);
	}
}
