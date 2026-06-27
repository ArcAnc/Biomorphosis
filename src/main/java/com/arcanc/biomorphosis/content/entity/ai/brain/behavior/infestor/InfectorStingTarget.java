/**
 * @author ArcAnc
 * Created at: 22.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior.infestor;

import com.arcanc.biomorphosis.content.entity.Infestor;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.Map;
import java.util.Optional;

public class InfectorStingTarget extends Behavior<Infestor>
{
	private final float speedModifier;

	public InfectorStingTarget(float speedModifier)
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
				MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
				MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
				Registration.AIReg.INFESTOR_RETURN_POS.get(), MemoryStatus.VALUE_PRESENT
		), 200);
		this.speedModifier = speedModifier;
	}

	@Override
	protected boolean canStillUse(ServerLevel level, Infestor entity, long gameTime)
	{
		return getTarget(entity).filter(target -> Infestor.isValidInfestationTarget(entity, target)).isPresent();
	}

	@Override
	protected void tick(ServerLevel level, Infestor owner, long gameTime)
	{
		Optional<LivingEntity> target = getTarget(owner);
		if (target.isEmpty())
		{
			stopAttack(owner);
			return;
		}

		LivingEntity living = target.get();
		owner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(living, true));
		if (owner.isWithinMeleeAttackRange(living))
		{
			if (owner.doHurtTarget(living))
				owner.infest(living);
			stopAttack(owner);
		}
		else
			owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(living, false), this.speedModifier, 0));
	}

	@Override
	protected void stop(ServerLevel level, Infestor entity, long gameTime)
	{
		if (getTarget(entity).filter(target -> Infestor.isValidInfestationTarget(entity, target)).isEmpty())
			stopAttack(entity);
	}

	private Optional<LivingEntity> getTarget(Infestor infestor)
	{
		return infestor.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
	}

	private void stopAttack(Infestor infestor)
	{
		infestor.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
		infestor.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		infestor.setTarget(null);
		infestor.getNavigation().stop();
	}
}
