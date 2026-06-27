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
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;

public class InfectorPatrol extends Behavior<Infestor>
{
	private final int radius;
	private final float speedModifier;

	public InfectorPatrol(int radius, float speedModifier)
	{
		super(Map.of(
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
				Registration.AIReg.INFESTOR_RETURN_POS.get(), MemoryStatus.VALUE_ABSENT,
				Registration.AIReg.INFESTOR_HOME_POS.get(), MemoryStatus.VALUE_PRESENT
		), 40);
		this.radius = radius;
		this.speedModifier = speedModifier;
	}

	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, Infestor owner)
	{
		return owner.getRandom().nextInt(20) == 0;
	}

	@Override
	protected void start(ServerLevel level, Infestor entity, long gameTime)
	{
		Optional<BlockPos> home = entity.getBrain().getMemory(Registration.AIReg.INFESTOR_HOME_POS.get());
		if (home.isEmpty())
			return;

		Vec3 pos = findPatrolPos(entity, home.get());
		if (pos != null)
			entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.speedModifier, 1));
	}

	private Vec3 findPatrolPos(Infestor entity, BlockPos home)
	{
		double maxDistance = this.radius * this.radius;
		for (int q = 0; q < 10; q++)
		{
			Vec3 pos = LandRandomPos.getPos(entity, this.radius, 8);
			if (pos != null && BlockPos.containing(pos).distSqr(home) <= maxDistance)
				return pos;
		}

		return DefaultRandomPos.getPosTowards(entity, this.radius, 8, Vec3.atBottomCenterOf(home), (float)(Math.PI / 2));
	}
}
