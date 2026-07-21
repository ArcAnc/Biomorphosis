/**
 * @author ArcAnc
 * Created at: 09.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain;

import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.queen.QueenAttackTarget;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.queen.QueenBurrow;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.queen.QueenTargetFinder;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.queen.QueenTravel;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.queen.QueenUnburrow;
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

public class QueenBrain
{
	public static final int UNBURROW_TICKS = 30;
	public static final int BURROW_TICKS = 100;
	public static final int TARGET_RADIUS = 10;
	public static final int ARRIVAL_DISTANCE = 2;

	public static Brain<Queen> makeBrain(Dynamic<?> ops)
	{
		Brain.Provider<Queen> provider = Brain.provider(
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
						MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE
				),
				List.of(
						SensorType.NEAREST_LIVING_ENTITIES,
						SensorType.NEAREST_PLAYERS,
						SensorType.HURT_BY
				));
		Brain<Queen> brain = provider.makeBrain(ops);

		brain.addActivity(Activity.CORE, 0,
				ImmutableList.of(
						new Swim(0.8f),
						new LookAtTargetSink(45, 90),
						new MoveToTargetSink()
				));

		brain.addActivity(Activity.WORK, 10,
				ImmutableList.of(
						new QueenUnburrow(),
						new QueenBurrow(),
						new QueenAttackTarget(1.2f, 30),
						new QueenTargetFinder(),
						new QueenTravel(1.0f, 1.2f)
				));

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.WORK);
		brain.useDefaultActivity();
		return brain;
	}
}
