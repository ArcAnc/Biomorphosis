/**
 * @author ArcAnc
 * Created at: 13.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity.ai.goals;


import com.arcanc.metamorphosis.content.entity.Worker;
import net.minecraft.world.entity.ai.goal.Goal;

public class WorkingRandomGoal extends Goal
{
	private final int MAX_WORK_TIME = 50;
	private final int BREAK_BETWEEN_WORK = 200;

	private final Worker mob;
	private int workTime;
	private int breakTime;

	public WorkingRandomGoal(Worker mob)
	{
		this.mob = mob;
	}

	@Override
	public boolean canUse()
	{
		if (this.breakTime > 0)
		{
			this.breakTime--;
			return false;
		}
		return this.mob.isAlive();
	}

	@Override
	public boolean canContinueToUse()
	{
		return this.workTime < MAX_WORK_TIME && this.breakTime <= 0;
	}

	@Override
	public void start()
	{
		this.breakTime = 0;
		this.mob.setWorking(true);
		this.workTime++;
	}

	@Override
	public void stop()
	{
		this.mob.setWorking(false);
		this.workTime = 0;
		this.breakTime = BREAK_BETWEEN_WORK;
	}

	@Override
	public void tick()
	{
		this.workTime ++;
		this.mob.setWorking(true);
		if (this.workTime >= MAX_WORK_TIME)
			this.stop();
	}
}
