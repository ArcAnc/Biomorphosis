/**
 * @author ArcAnc
 * Created at: 22.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.sensor;

import com.arcanc.biomorphosis.content.entity.Infestor;
import com.arcanc.biomorphosis.content.entity.ai.brain.InfestorBrain;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public class SwarmHurtBySensor extends Sensor<Infestor>
{
	public SwarmHurtBySensor()
	{
		super(10);
	}

	@Override
	protected void doTick(ServerLevel level, Infestor entity)
	{
		AABB area = entity.getBoundingBox().inflate(InfestorBrain.RESPONSE_RADIUS);
		Optional<LivingEntity> attacker = level.getEntitiesOfClass(LivingEntity.class, area, target ->
						target.getType().is(BioEntityTags.SWARM) && target.getLastHurtByMob() != null).
				stream().
				map(LivingEntity :: getLastHurtByMob).
				filter(target -> Infestor.isValidInfestationTarget(entity, target)).
				min(Comparator.comparingDouble(entity :: distanceToSqr));

		entity.getBrain().setMemory(Registration.AIReg.INFESTOR_ALERT_TARGET.get(), attacker);
	}

	@Override
	public Set<MemoryModuleType<?>> requires()
	{
		return ImmutableSet.of(Registration.AIReg.INFESTOR_ALERT_TARGET.get());
	}
}
