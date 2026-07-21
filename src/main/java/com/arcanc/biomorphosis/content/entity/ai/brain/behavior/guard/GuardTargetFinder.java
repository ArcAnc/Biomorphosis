/**
 * @author ArcAnc
 * Created at: 09.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior.guard;

import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.QueenGuard;
import com.arcanc.biomorphosis.content.entity.ai.brain.GuardBrain;
import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class GuardTargetFinder extends Behavior<QueenGuard>
{
	public GuardTargetFinder()
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.REGISTERED
		), 20);
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, QueenGuard owner)
	{
		return findTarget(level, owner).isPresent();
	}

	@Override
	protected void start(ServerLevel level, QueenGuard entity, long gameTime)
	{
		findTarget(level, entity).ifPresent(target ->
		{
			entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
			entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		});
	}

	private Optional<LivingEntity> findTarget(ServerLevel level, QueenGuard guard)
	{
		Optional<LivingEntity> hurtBy = guard.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).
				filter(guard :: canAttackInCurrentMode);
		if (hurtBy.isPresent())
			return hurtBy;

		Optional<LivingEntity> allyAttacker = findSwarmAttacker(level, guard);
		if (allyAttacker.isPresent())
			return allyAttacker;

		if (guard.isBerserk())
			return findNearestEnemy(level, guard, guard.getBoundingBox().inflate(GuardBrain.BERSERK_TARGET_RADIUS));

		Optional<Queen> queen = GuardBrain.getQueen(level, guard);
		if (queen.isPresent())
		{
			LivingEntity queenAttacker = queen.get().getLastHurtByMob();
			if (guard.canAttackInCurrentMode(queenAttacker))
				return Optional.of(queenAttacker);
			return findNearestEnemy(level, guard, queen.get().getBoundingBox().inflate(GuardBrain.QUEEN_DEFENSE_RADIUS));
		}

		if (!guard.hasQueenId())
			return findNearestEnemy(level, guard, new AABB(guard.getPatrolPos()).inflate(GuardBrain.PATROL_RADIUS));

		return Optional.empty();
	}

	private Optional<LivingEntity> findSwarmAttacker(ServerLevel level, QueenGuard guard)
	{
		return level.getEntitiesOfClass(
						LivingEntity.class,
						guard.getBoundingBox().inflate(GuardBrain.RESPONSE_RADIUS),
						SwarmTargeting :: isSwarmMember).
				stream().
				map(LivingEntity :: getLastHurtByMob).
				filter(guard :: canAttackInCurrentMode).
				min(Comparator.comparingDouble(guard :: distanceToSqr));
	}

	private Optional<LivingEntity> findNearestEnemy(ServerLevel level, QueenGuard guard, AABB area)
	{
		return level.getEntitiesOfClass(LivingEntity.class, area, guard :: canAttackInCurrentMode).
				stream().
				min(Comparator.comparingDouble(guard :: distanceToSqr));
	}
}
