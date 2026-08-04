/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.spike;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class SpikeGeometry
{
	private static final Vec3 WORLD_UP = new Vec3(0.0D, 1.0D, 0.0D);
	private static final Vec3 WORLD_RIGHT = new Vec3(1.0D, 0.0D, 0.0D);

	private SpikeGeometry()
	{
	}
	
	public static Vec3 volleyOrigin(LivingEntity entity, Vec3 direction)
	{
		return entity.getEyePosition().add(0.0D, -0.48D, 0.0D).add(direction.scale(0.50D));
	}

	public static Vec3 position(Vec3 origin, Vec3 direction, double lateralCurve, double aimLift, double distance,
			double aimDistance)
	{
		if (distance <= 0.0D)
			return origin;

		double progress = distance / aimDistance;
		Vec3 target = origin.add(direction.scale(aimDistance));
		Vec3 control = origin.lerp(target, 0.5D).add(right(direction).scale(lateralCurve));
		if (distance >= aimDistance)
		{
			Vec3 exitTangent = target.subtract(control).scale(2.0D / aimDistance).
					add(up(direction).scale(-aimLift * (1.0D - Math.exp(-30.0D)) / aimDistance));
			return target.add(exitTangent.normalize().scale(distance - aimDistance));
		}
		Vec3 curvedPosition = origin.scale((1.0D - progress) * (1.0D - progress)).
				add(control.scale(2.0D * (1.0D - progress) * progress)).
				add(target.scale(progress * progress));
		double liftProgress = (1.0D - progress) * (1.0D - Math.exp(-progress * 30.0D));
		return curvedPosition.add(up(direction).scale(aimLift * liftProgress));
	}

	public static Vec3 right(Vec3 direction)
	{
		Vec3 right = direction.cross(WORLD_UP);
		if (right.lengthSqr() < 0.001D)
			right = direction.cross(WORLD_RIGHT);
		return right.normalize();
	}

	public static Vec3 up(Vec3 direction)
	{
		return right(direction).cross(direction).normalize();
	}
}
