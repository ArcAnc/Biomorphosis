/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain;


import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.QueenGuard;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.TargetOrRetaliate;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.guard.AttackEnemiesNearQueen;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.guard.FollowQueen;
import com.arcanc.biomorphosis.content.entity.ai.brain.behavior.guard.PatrolIfNoQueen;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GuardBrain
{
	public static @NotNull Brain<QueenGuard> makeBrain(Dynamic<?> ops)
	{
		Brain.Provider<QueenGuard> provider = Brain.provider(
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
						MemoryModuleType.ANGRY_AT,
						MemoryModuleType.UNIVERSAL_ANGER,
						Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(),
						Registration.AIReg.QUEEN_GUARD_PATROL_POS.get()
				),
				List.of(
						SensorType.NEAREST_LIVING_ENTITIES,
						SensorType.NEAREST_PLAYERS,
						SensorType.HURT_BY
				));
		Brain<QueenGuard> brain = provider.makeBrain(ops);

		brain.addActivity(Activity.CORE, 0,
				ImmutableList.of(
						new Swim<>(0.8f),
						new LookAtTargetSink(45, 90)
						//new MoveToTargetSink()
				));

		brain.addActivity(Activity.WORK, 10,
				ImmutableList.of(
						new FollowQueen(0.2f),
						new AttackEnemiesNearQueen(),
						new PatrolIfNoQueen(1.1f),
						new TargetOrRetaliate<>(
								target -> !(target.getType() == EntityType.CREEPER),
								32.0
						)
				));

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.WORK);
		brain.useDefaultActivity();
		return brain;
	}

	public static Optional<Queen> getQueen(ServerLevel level, @NotNull QueenGuard guard)
	{
		UUID uuid = guard.getBrain().getMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get()).orElse(null);
		if (uuid == null)
			return Optional.empty();
		Entity entity = level.getEntity(uuid);
		return entity instanceof Queen queen && queen.isAlive() ? Optional.of(queen) : Optional.empty();
	}
}
