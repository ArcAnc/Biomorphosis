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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Map;

public class QueenBurrow extends Behavior<Queen>
{
	public QueenBurrow()
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED,
				MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
		), QueenBrain.BURROW_TICKS + 5);
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, Queen owner)
	{
		return owner.isBurrowing();
	}

	@Override
	protected boolean canStillUse(ServerLevel level, Queen entity, long gameTime)
	{
		return entity.isBurrowing();
	}

	@Override
	protected void tick(ServerLevel level, Queen owner, long gameTime)
	{
		owner.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
		owner.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		owner.tickBurrow();
	}
}
