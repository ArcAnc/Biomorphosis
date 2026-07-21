/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior.guard;


import com.arcanc.biomorphosis.content.entity.QueenGuard;
import com.arcanc.biomorphosis.content.entity.ai.brain.GuardBrain;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.Map;

public class FollowQueen extends Behavior<QueenGuard>
{
	private final float speedModifier;

	public FollowQueen(float speedModifier)
	{
		super(Map.of(
				Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), MemoryStatus.VALUE_PRESENT,
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
				MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED
		), 40);
		this.speedModifier = speedModifier;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, QueenGuard owner)
	{
		return !owner.isBerserk() && GuardBrain.getQueen(level, owner).
				filter(queen -> owner.distanceToSqr(queen) > GuardBrain.QUEEN_GUARD_RADIUS * GuardBrain.QUEEN_GUARD_RADIUS).
				isPresent();
	}

	@Override
	protected void start(ServerLevel level, QueenGuard entity, long gameTime)
	{
		GuardBrain.getQueen(level, entity).ifPresent(queen ->
		{
			entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(queen, true));
			entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(queen, false), this.speedModifier, GuardBrain.QUEEN_GUARD_RADIUS));
		});

	}
}
