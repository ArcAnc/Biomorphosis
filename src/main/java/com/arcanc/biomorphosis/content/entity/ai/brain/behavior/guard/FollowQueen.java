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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FollowQueen extends Behavior<QueenGuard>
{
	private final float speedModifier;

	public FollowQueen(float speedModifier)
	{
		super(Map.of(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), MemoryStatus.VALUE_PRESENT), 40);
		this.speedModifier = speedModifier;
	}

	@Override
	protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull QueenGuard owner)
	{
		return GuardBrain.getQueen(level, owner).isPresent();
	}

	@Override
	protected void start(@NotNull ServerLevel level, @NotNull QueenGuard entity, long gameTime)
	{
		GuardBrain.getQueen(level, entity).ifPresent(queen ->
				entity.getNavigation().moveTo(queen, entity.getAttributeValue(
						Attributes.MOVEMENT_SPEED) * this.speedModifier));

	}
}
