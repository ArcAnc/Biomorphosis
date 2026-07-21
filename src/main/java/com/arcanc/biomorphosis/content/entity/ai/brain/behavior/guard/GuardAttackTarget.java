/**
 * @author ArcAnc
 * Created at: 09.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior.guard;

import com.arcanc.biomorphosis.content.entity.QueenGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.Map;
import java.util.Optional;

public class GuardAttackTarget extends Behavior<QueenGuard>
{
	private final float speedModifier;
	private final int attackInterval;
	private long nextAttackTick;

	public GuardAttackTarget(float speedModifier, int attackInterval)
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
				MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
				MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
		), 200);
		this.speedModifier = speedModifier;
		this.attackInterval = attackInterval;
	}

	@Override
	protected boolean canStillUse(ServerLevel level, QueenGuard entity, long gameTime)
	{
		return getTarget(entity).filter(entity :: canAttackInCurrentMode).isPresent();
	}

	@Override
	protected void tick(ServerLevel level, QueenGuard owner, long gameTime)
	{
		Optional<LivingEntity> optional = getTarget(owner);
		if (optional.isEmpty() || !owner.canAttackInCurrentMode(optional.get()))
		{
			stopAttack(owner);
			return;
		}

		LivingEntity target = optional.get();
		owner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
		if (owner.isWithinMeleeAttackRange(target))
		{
			owner.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
			if (gameTime >= this.nextAttackTick)
			{
				owner.swing(owner.getUsedItemHand());
				owner.doHurtTarget(target);
				this.nextAttackTick = gameTime + this.attackInterval;
			}
		}
		else
			owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(target, false), this.speedModifier, 0));
	}

	@Override
	protected void stop(ServerLevel level, QueenGuard entity, long gameTime)
	{
		if (getTarget(entity).filter(entity :: canAttackInCurrentMode).isEmpty())
			stopAttack(entity);
	}

	private Optional<LivingEntity> getTarget(QueenGuard guard)
	{
		return guard.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
	}

	private void stopAttack(QueenGuard guard)
	{
		guard.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
		guard.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		guard.setTarget(null);
		guard.getNavigation().stop();
	}
}
