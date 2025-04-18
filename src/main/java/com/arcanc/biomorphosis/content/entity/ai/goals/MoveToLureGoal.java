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
        return mob.getLurePos() != null;
    }

    @Override
    public void start()
    {
        if (mob.getLurePos() != null)
            if (!mob.isFindLure())
                mob.getNavigation().moveTo(mob.getLurePos().getX(), mob.getLurePos().getY(), mob.getLurePos().getZ(), speed);
            else
                mob.getNavigation().moveTo(mob.getSpawnPos().getX(), mob.getSpawnPos().getY(), mob.getSpawnPos().getZ(), speed * 1.2f);
    }

    @Override
    public void tick()
    {
        start();
    }
}
