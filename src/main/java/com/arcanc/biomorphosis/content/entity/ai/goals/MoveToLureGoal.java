/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.goals;

import com.arcanc.biomorphosis.content.entity.Queen;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MoveToLureGoal extends Goal
{
    private final Queen mob;
    private final double speed;

    public MoveToLureGoal(Queen mob, double speedModifier)
    {
        this.mob = mob;
        this.speed = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        return this.mob.getLurePos() != null;
    }

    @Override
    public void start()
    {
        if (this.mob.getLurePos() != null)
            if (!this.mob.isFindLure())
                this.mob.getNavigation().moveTo(this.mob.getLurePos().getX(), this.mob.getLurePos().getY(), this.mob.getLurePos().getZ(), this.speed);
            else
                this.mob.getNavigation().moveTo(this.mob.getSpawnPos().getX(), this.mob.getSpawnPos().getY(), this.mob.getSpawnPos().getZ(), this.speed * 1.2f);
    }

    @Override
    public void tick()
    {
        start();
    }
}
