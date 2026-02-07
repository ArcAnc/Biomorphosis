/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.goals;


import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.QueenGuard;
import net.minecraft.world.entity.ai.goal.Goal;

public class FollowQueenGoal extends Goal
{
	private final QueenGuard guard;
	private final double speedModifier;
	private int timeToRecalcPath;

	public FollowQueenGoal(QueenGuard guard, double speedModifier)
	{
		this.guard = guard;
		this.speedModifier = speedModifier;
	}

	@Override
	public boolean canUse()
	{
		Queen queen = this.guard.getQueen();
		if (queen == null)
			return false;

		double distance = this.guard.distanceToSqr(queen);

		return queen.isAlive() && distance > 64;

	}

	@Override
	public boolean canContinueToUse()
	{
		Queen queen = this.guard.getQueen();
		if (queen == null || !queen.isAlive())
			return false;
		else
		{
			double d0 = this.guard.distanceToSqr(queen);
			return !(d0 < 9.0) && !(d0 > 256.0);
		}
	}

	@Override
	public void start()
	{
		this.timeToRecalcPath = 0;
	}

	@Override
	public void tick()
	{
		Queen queen = this.guard.getQueen();
		if (queen == null || !queen.isAlive())
			return;

		if (--this.timeToRecalcPath <= 0)
		{
			this.timeToRecalcPath = this.adjustedTickDelay(10);
			this.guard.getNavigation().moveTo(queen, this.speedModifier);
		}
	}
}
