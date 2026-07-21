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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PatrolIfNoQueen extends Behavior<QueenGuard>
{
	private final float speedModifier;

	public PatrolIfNoQueen(float speedModifier)
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
				Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), MemoryStatus.VALUE_ABSENT,
				Registration.AIReg.QUEEN_GUARD_PATROL_POS.get(), MemoryStatus.VALUE_PRESENT
		), 60);
		this.speedModifier = speedModifier;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, QueenGuard owner)
	{
		return !owner.isBerserk() && owner.getRandom().nextInt(GuardBrain.PATROL_INTERVAL) == 0 && getPosition(owner) != null;
	}

	@Override
	protected void start(ServerLevel level, QueenGuard entity, long gameTime)
	{
		Vec3 position = getPosition(entity);
		if (position != null)
			entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(position, this.speedModifier, 1));
	}

	private @Nullable Vec3 getPosition(QueenGuard guard)
	{
		BlockPos patrolPos = guard.getPatrolPos();
		Vec3 patrolCenter = Vec3.atCenterOf(patrolPos);
		if (!guard.blockPosition().closerToCenterThan(patrolCenter, GuardBrain.PATROL_RADIUS))
			return LandRandomPos.getPosTowards(guard, 16, 8, patrolCenter);

		return LandRandomPos.getPos(guard, 16, 8, pos -> -pos.distSqr(patrolPos));
	}
}
