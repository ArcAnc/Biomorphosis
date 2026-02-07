/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class TargetOrRetaliate<E extends Mob> extends Behavior<E>
{

	private final Predicate<LivingEntity> validTarget;
	private final double radius;

	public TargetOrRetaliate(Predicate<LivingEntity> validTarget, double radius)
	{
		super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
					MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.REGISTERED),
				20);

		this.validTarget = validTarget;
		this.radius = radius;
	}

	@Override
	protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull E owner)
	{
		Optional<LivingEntity> hurtBy = owner.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);

		if (hurtBy.isPresent() && this.validTarget.test(hurtBy.get()))
		{
			owner.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, hurtBy.get());
			return true;
		}

		AABB area = owner.getBoundingBox().inflate(this.radius);
		return level.getEntitiesOfClass(LivingEntity.class, area).stream().
				filter(this.validTarget).
				anyMatch(entity ->
					entity.getLastHurtByMob() != null &&
					isAlly(entity.getLastHurtByMob(), owner));
	}

	@Override
	protected void start(@NotNull ServerLevel level, @NotNull E entity, long gameTime)
	{
		Optional<LivingEntity> hurtBy = entity.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
		if (hurtBy.isPresent() && this.validTarget.test(hurtBy.get()))
			entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, hurtBy.get());
		else
		{
			AABB area = entity.getBoundingBox().inflate(this.radius);
			level.getEntitiesOfClass(LivingEntity.class, area).stream()
					.filter(this.validTarget)
					.filter(target ->
							target.getLastHurtByMob() != null &&
									isAlly(target.getLastHurtByMob(), entity)
					)
					.findFirst()
					.ifPresent(target -> entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target));
		}
	}

	private boolean isAlly(@NotNull LivingEntity entity, E mob)
	{
		return false;//entity.getTeam() != null && entity.getTeam() == mob.getTeam();
	}
}
