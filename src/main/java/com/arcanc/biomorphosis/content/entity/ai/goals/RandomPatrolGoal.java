/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.goals;


import com.arcanc.biomorphosis.content.entity.QueenGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RandomPatrolGoal extends RandomStrollGoal
{
	public RandomPatrolGoal(PathfinderMob mob, double speedModifier)
	{
		super(mob, speedModifier, 240, false);
	}

	@Override
	public boolean canUse()
	{
		QueenGuard guard = (QueenGuard) this.mob;
		return !guard.hasQueenId() && !guard.isBerserk() && super.canUse();
	}

	@Override
	public boolean canContinueToUse()
	{
		QueenGuard guard = (QueenGuard) this.mob;
		return !guard.hasQueenId() && !guard.isBerserk() && super.canContinueToUse();
	}

	@Override
	protected @Nullable Vec3 getPosition()
	{
		BlockPos patrolPos = ((QueenGuard) this.mob).getPatrolPos();
		Vec3 patrolCenter = Vec3.atCenterOf(patrolPos);
		if (!this.mob.blockPosition().closerToCenterThan(patrolCenter, 32))
			return LandRandomPos.getPosTowards(this.mob, 16, 8, patrolCenter);

		return LandRandomPos.getPos(this.mob, 16, 8, pos -> -pos.distSqr(patrolPos));
	}
}
