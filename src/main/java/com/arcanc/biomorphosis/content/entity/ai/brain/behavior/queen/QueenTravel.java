/**
 * @author ArcAnc
 * Created at: 09.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior.queen;

import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.ai.brain.QueenBrain;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class QueenTravel extends Behavior<Queen>
{
	private final float lureSpeed;
	private final float returnSpeed;

	public QueenTravel(float lureSpeed, float returnSpeed)
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
		), 40);
		this.lureSpeed = lureSpeed;
		this.returnSpeed = returnSpeed;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, Queen owner)
	{
		return owner.isOnGround() && getTargetPos(owner) != null;
	}

	@Override
	protected boolean canStillUse(ServerLevel level, Queen entity, long gameTime)
	{
		return entity.isOnGround() && entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty();
	}

	@Override
	protected void tick(ServerLevel level, Queen owner, long gameTime)
	{
		BlockPos target = getTargetPos(owner);
		if (target == null)
			return;

		if (owner.blockPosition().closerToCenterThan(Vec3.atCenterOf(target), QueenBrain.ARRIVAL_DISTANCE))
		{
			if (!owner.isFindLure())
				owner.markLureFound();
			else
			{
				owner.startBurrowing();
				owner.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
			}
			return;
		}

		owner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(target, owner.isFindLure() ? this.returnSpeed : this.lureSpeed, QueenBrain.ARRIVAL_DISTANCE));
	}

	private @Nullable BlockPos getTargetPos(Queen queen)
	{
		return queen.isFindLure() ? queen.getSpawnPos() : queen.getLurePos();
	}
}
