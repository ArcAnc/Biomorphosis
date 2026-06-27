/**
 * @author ArcAnc
 * Created at: 22.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.brain.behavior.infestor;

import com.arcanc.biomorphosis.content.entity.Infestor;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;

public class InfectorReturnHome extends Behavior<Infestor>
{
	private final float speedModifier;

	public InfectorReturnHome(float speedModifier)
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				Registration.AIReg.INFESTOR_RETURN_POS.get(), MemoryStatus.VALUE_PRESENT
		), 10);
		this.speedModifier = speedModifier;
	}

	@Override
	protected void start(ServerLevel level, Infestor entity, long gameTime)
	{
		Optional<BlockPos> returnPos = entity.getBrain().getMemory(Registration.AIReg.INFESTOR_RETURN_POS.get());
		if (returnPos.isEmpty())
			return;

		BlockPos pos = returnPos.get();
		if (entity.blockPosition().distManhattan(pos) <= 2)
		{
			entity.getBrain().eraseMemory(Registration.AIReg.INFESTOR_RETURN_POS.get());
			entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
			return;
		}

		entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(Vec3.atBottomCenterOf(pos), this.speedModifier, 1));
	}
}
