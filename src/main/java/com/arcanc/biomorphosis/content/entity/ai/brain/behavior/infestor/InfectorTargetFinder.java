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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class InfectorTargetFinder extends Behavior<Infestor>
{
	private final double radius;

	public InfectorTargetFinder(double radius)
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				Registration.AIReg.INFESTOR_RETURN_POS.get(), MemoryStatus.VALUE_ABSENT
		), 20);
		this.radius = radius;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, Infestor owner)
	{
		return findTarget(level, owner).isPresent();
	}

	@Override
	protected void start(ServerLevel level, Infestor entity, long gameTime)
	{
		findTarget(level, entity).ifPresent(target ->
		{
			entity.getBrain().setMemory(Registration.AIReg.INFESTOR_RETURN_POS.get(), entity.blockPosition());
			entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
			entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		});
	}

	private Optional<LivingEntity> findTarget(ServerLevel level, Infestor owner)
	{
		Optional<LivingEntity> alertTarget = owner.getBrain().getMemory(Registration.AIReg.INFESTOR_ALERT_TARGET.get()).
				filter(target -> Infestor.isValidInfestationTarget(owner, target));
		if (alertTarget.isPresent())
		{
			owner.getBrain().eraseMemory(Registration.AIReg.INFESTOR_ALERT_TARGET.get());
			return alertTarget;
		}

		AABB area = owner.getBoundingBox().inflate(this.radius);
		return level.getEntitiesOfClass(LivingEntity.class, area, target -> Infestor.isValidInfestationTarget(owner, target)).
				stream().
				min(Comparator.comparingDouble(owner :: distanceToSqr));
	}
}
