/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity.ai.brain.behavior.guard;


import com.arcanc.metamorphosis.content.entity.QueenGuard;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Vanilla reworked from {@link net.minecraft.world.entity.ai.behavior.MoveToTargetSink}
 */

public class PatrolIfNoQueen extends Behavior<QueenGuard>
{
	private static final int MAX_COOLDOWN_BEFORE_RETRYING = 40;
	@Nullable
	private Path path;
	@Nullable
	private BlockPos lastTargetPos;

	private int remainingCooldown;

	private final float speedModifier;

	public PatrolIfNoQueen(float speedModifier)
	{
		this(150, 250, speedModifier);
	}

	public PatrolIfNoQueen(int minDuration, int maxDuration, float speedModifier)
	{
		super(
				ImmutableMap.of(
						MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
						MemoryStatus.REGISTERED,
						MemoryModuleType.PATH,
						MemoryStatus.VALUE_ABSENT,
						MemoryModuleType.WALK_TARGET,
						MemoryStatus.VALUE_PRESENT,
						Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(),
						MemoryStatus.VALUE_ABSENT,
						Registration.AIReg.QUEEN_GUARD_PATROL_POS.get(),
						MemoryStatus.VALUE_PRESENT
				),
				minDuration,
				maxDuration
		);
		this.speedModifier = speedModifier;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, QueenGuard owner)
	{
		if (this.remainingCooldown > 0)
		{
			this.remainingCooldown--;
			return false;
		}
		else
		{
			Brain<?> brain = owner.getBrain();
			WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
			boolean flag = this.reachedTarget(owner, walktarget);
			if (!flag && this.tryComputePath(owner, walktarget, level.getGameTime()))
			{
				this.lastTargetPos = walktarget.getTarget().currentBlockPosition();
				return true;
			}
			else
			{
				brain.eraseMemory(MemoryModuleType.WALK_TARGET);
				if (flag)
					brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

				return false;
			}
		}
	}

	@Override
	protected boolean canStillUse(ServerLevel level, QueenGuard entity, long gameTime)
	{
		if (this.path != null && this.lastTargetPos != null)
		{
			Optional<WalkTarget> optional = entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
			boolean flag = optional.map(PatrolIfNoQueen :: isWalkTargetSpectator).orElse(false);
			PathNavigation pathnavigation = entity.getNavigation();
			return !pathnavigation.isDone() && optional.isPresent() && !this.reachedTarget(entity, optional.get()) && !flag;
		}
		else
			return false;
	}

	@Override
	protected void stop(@NotNull ServerLevel level, @NotNull QueenGuard entity, long gameTime)
	{
		if (entity.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)
				&& !this.reachedTarget(entity, entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get())
				&& entity.getNavigation().isStuck())
			this.remainingCooldown = level.getRandom().nextInt(MAX_COOLDOWN_BEFORE_RETRYING);

		entity.getNavigation().stop();
		entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		entity.getBrain().eraseMemory(MemoryModuleType.PATH);
		this.path = null;
	}

	@Override
	protected void start(@NotNull ServerLevel level, @NotNull QueenGuard entity, long gameTime)
	{
		entity.getBrain().setMemory(MemoryModuleType.PATH, this.path);
		entity.getNavigation().moveTo(this.path, this.speedModifier);
	}

	@Override
	protected void tick(@NotNull ServerLevel level, @NotNull QueenGuard owner, long gameTime)
	{
		Path path = owner.getNavigation().getPath();
		Brain<?> brain = owner.getBrain();
		if (this.path != path)
		{
			this.path = path;
			brain.setMemory(MemoryModuleType.PATH, path);
		}

		if (path != null && this.lastTargetPos != null)
		{
			WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
			if (walktarget.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4.0
					&& this.tryComputePath(owner, walktarget, level.getGameTime()))
			{
				this.lastTargetPos = walktarget.getTarget().currentBlockPosition();
				this.start(level, owner, gameTime);
			}
		}
	}

	private boolean tryComputePath(@NotNull Mob mob, @NotNull WalkTarget target, long time)
	{
		BlockPos blockpos = target.getTarget().currentBlockPosition();
		this.path = mob.getNavigation().createPath(blockpos, 0);
		Brain<?> brain = mob.getBrain();
		if (this.reachedTarget(mob, target))
			brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		else
		{
			boolean flag = this.path != null && this.path.canReach();
			if (flag)
				brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
			else if (!brain.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE))
				brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, time);

			if (this.path != null)
				return true;

			Vec3 vec3 = DefaultRandomPos.getPosTowards((PathfinderMob)mob,
					16,
					8,
					Vec3.atBottomCenterOf(brain.getMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get()).get()),
					(float) (Math.PI / 2));
			if (vec3 != null)
			{
				this.path = mob.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
				return this.path != null;
			}
		}
		return false;
	}

	private boolean reachedTarget(@NotNull Mob mob, @NotNull WalkTarget target)
	{
		return target.getTarget().currentBlockPosition().distManhattan(mob.blockPosition()) <= target.getCloseEnoughDist();
	}

	private static boolean isWalkTargetSpectator(@NotNull WalkTarget walkTarget)
	{
		return walkTarget.getTarget() instanceof EntityTracker entitytracker && entitytracker.getEntity().isSpectator();
	}
}
