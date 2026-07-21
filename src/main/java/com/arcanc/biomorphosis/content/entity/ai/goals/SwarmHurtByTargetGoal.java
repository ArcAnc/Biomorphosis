/**
 * @author ArcAnc
 * Created at: 09.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.ai.goals;

import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.Optional;

public class SwarmHurtByTargetGoal extends TargetGoal
{
	private static final int DEFAULT_ALERT_RADIUS = 24;

	private final double alertRadius;
	private int lastOwnHurtTimestamp;
	private int lastAllyHurtTimestamp;
	private int pendingHurtTimestamp;

	public SwarmHurtByTargetGoal(Mob mob)
	{
		this(mob, DEFAULT_ALERT_RADIUS);
	}

	public SwarmHurtByTargetGoal(Mob mob, double alertRadius)
	{
		super(mob, false);
		this.alertRadius = alertRadius;
	}

	@Override
	public boolean canUse()
	{
		LivingEntity ownAttacker = this.mob.getLastHurtByMob();
		int ownTimestamp = this.mob.getLastHurtByMobTimestamp();
		if (ownTimestamp != this.lastOwnHurtTimestamp && canRetaliate(ownAttacker))
		{
			this.targetMob = ownAttacker;
			this.pendingHurtTimestamp = ownTimestamp;
			return true;
		}

		Optional<SwarmAlert> alert = findAllyAlert();
		if (alert.isPresent())
		{
			this.targetMob = alert.get().attacker();
			this.pendingHurtTimestamp = alert.get().timestamp();
			return true;
		}
		return false;
	}

	@Override
	public void start()
	{
		this.mob.setTarget(this.targetMob);

		if (this.targetMob == this.mob.getLastHurtByMob())
			this.lastOwnHurtTimestamp = this.pendingHurtTimestamp;
		else
			this.lastAllyHurtTimestamp = this.pendingHurtTimestamp;

		super.start();
	}

	private Optional<SwarmAlert> findAllyAlert()
	{
		AABB area = this.mob.getBoundingBox().inflate(this.alertRadius);
		return this.mob.level().getEntitiesOfClass(LivingEntity.class, area, SwarmTargeting :: isSwarmMember).
				stream().
				filter(ally -> ally != this.mob).
				map(ally -> new SwarmAlert(ally.getLastHurtByMob(), ally.getLastHurtByMobTimestamp()))
				.filter(alert -> alert.timestamp() != this.lastAllyHurtTimestamp)
				.filter(alert -> canRetaliate(alert.attacker()))
				.max(Comparator.comparingInt(SwarmAlert :: timestamp).
						thenComparingDouble(alert -> -this.mob.distanceToSqr(alert.attacker())));
	}

	private boolean canRetaliate(LivingEntity target)
	{
		return SwarmTargeting.isValidSwarmEnemy(this.mob, target) &&
				this.canAttack(target, TargetingConditions.DEFAULT);
	}

	private record SwarmAlert(LivingEntity attacker, int timestamp)
	{
	}
}
