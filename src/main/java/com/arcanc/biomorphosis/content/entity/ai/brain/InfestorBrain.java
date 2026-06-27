/**
 * @author ArcAnc
 * Created at: 22.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain;

import com.arcanc.biomorphosis.content.entity.Infestor;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.infestor.InfectorPatrol;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.infestor.InfectorReturnHome;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.infestor.InfectorStingTarget;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.infestor.InfectorTargetFinder;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;

public class InfestorBrain
{
	public static final int PATROL_RADIUS = 18;
	public static final int RESPONSE_RADIUS = 24;

	public static Brain<Infestor> makeBrain(Dynamic<?> ops)
	{
		Brain.Provider<Infestor> provider = Brain.provider(
				List.of(
						MemoryModuleType.LOOK_TARGET,
						MemoryModuleType.NEAREST_LIVING_ENTITIES,
						MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
						MemoryModuleType.NEAREST_VISIBLE_PLAYER,
						MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
						MemoryModuleType.HURT_BY,
						MemoryModuleType.HURT_BY_ENTITY,
						MemoryModuleType.ATTACK_TARGET,
						MemoryModuleType.ATTACK_COOLING_DOWN,
						MemoryModuleType.PATH,
						MemoryModuleType.WALK_TARGET,
						MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
						Registration.AIReg.INFESTOR_HOME_POS.get(),
						Registration.AIReg.INFESTOR_RETURN_POS.get(),
						Registration.AIReg.INFESTOR_ALERT_TARGET.get()
				),
				List.of(
						SensorType.NEAREST_LIVING_ENTITIES,
						SensorType.NEAREST_PLAYERS,
						SensorType.HURT_BY,
						Registration.AIReg.INFESTOR_SWARM_HURT_BY.get()
				));
		Brain<Infestor> brain = provider.makeBrain(ops);

		brain.addActivity(Activity.CORE, 0,
				ImmutableList.of(
						new Swim(0.8f),
						new LookAtTargetSink(45, 90),
						new MoveToTargetSink()
				));

		brain.addActivity(Activity.WORK, 10,
				ImmutableList.of(
						new InfectorStingTarget(1.25f),
						new InfectorReturnHome(1.1f),
						new InfectorTargetFinder(RESPONSE_RADIUS),
						new InfectorPatrol(PATROL_RADIUS, 0.8f)
				));

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.WORK);
		brain.useDefaultActivity();
		return brain;
	}
}
