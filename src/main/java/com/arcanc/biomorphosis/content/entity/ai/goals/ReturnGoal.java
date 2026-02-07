/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.goals;


import com.arcanc.biomorphosis.content.entity.srf.AbstractPalladin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ReturnGoal extends Goal
{
	private final AbstractPalladin palladin;
	private final double speedModifier;
	
	public ReturnGoal(AbstractPalladin palladin, double speedModifier)
	{
		this.palladin = palladin;
		this.speedModifier = speedModifier;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}
	
	
	@Override
	public boolean canUse()
	{
		if (this.palladin.getTarget() == null)
			return this.palladin.isNotOnGuardPos();
		else
			return this.palladin.isOutAggroRadius();
	}
	
	@Override
	public void start()
	{
		BlockPos guardPos = this.palladin.getGuardPos();
		
		this.palladin.setTarget(null);
		this.palladin.getNavigation().moveTo(
				guardPos.getX() + 0.5d,
				guardPos.getY() + 0.5d,
				guardPos.getZ() + 0.5d,
				this.speedModifier);
	}
	
	@Override
	public void stop()
	{
		this.palladin.getNavigation().stop();
	}
}
