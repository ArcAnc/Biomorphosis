/**
 * @author ArcAnc
 * Created at: 09.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior.queen;

import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.ai.brain.QueenBrain;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class QueenTargetFinder extends Behavior<Queen>
{
	public QueenTargetFinder()
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.REGISTERED
		), 20);
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, Queen owner)
	{
		return owner.isOnGround() && findTarget(level, owner).isPresent();
	}

	@Override
	protected void start(ServerLevel level, Queen entity, long gameTime)
	{
		findTarget(level, entity).ifPresent(target ->
		{
			entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
			entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		});
	}

	private Optional<LivingEntity> findTarget(ServerLevel level, Queen queen)
	{
		Optional<LivingEntity> hurtBy = queen.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).
				filter(queen :: isValidCombatTarget);
		if (hurtBy.isPresent())
			return hurtBy;

		return level.getEntitiesOfClass(LivingEntity.class, queen.getBoundingBox().inflate(QueenBrain.TARGET_RADIUS), queen :: isValidCombatTarget).
				stream().
				min(Comparator.comparingDouble(queen :: distanceToSqr));
	}
}
