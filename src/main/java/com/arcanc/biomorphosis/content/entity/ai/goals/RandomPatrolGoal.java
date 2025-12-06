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
		Queen queen = ((QueenGuard) this.mob).getQueen();
		return super.canUse() && (queen == null || !queen.isAlive());
	}

	@Override
	public boolean canContinueToUse()
	{
		Queen queen = ((QueenGuard) this.mob).getQueen();
		return super.canContinueToUse() && (queen == null || !queen.isAlive());
	}

	@Override
	protected @Nullable Vec3 getPosition()
	{
		return LandRandomPos.getPos(this.mob, 32, 8);
	}
}
