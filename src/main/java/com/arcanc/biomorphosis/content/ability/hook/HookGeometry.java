/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.hook;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class HookGeometry
{
	private static final Vec3 WORLD_UP = new Vec3(0.0D, 1.0D, 0.0D);
	private static final Vec3 WORLD_RIGHT = new Vec3(1.0D, 0.0D, 0.0D);
	private static final double SPIRAL_RADIUS = 0.30D;
	private static final double SPIRAL_TURNS_PER_BLOCK = 0.55D;

	private HookGeometry()
	{
	}

	public static Vec3 handOrigin(LivingEntity entity, Vec3 direction)
	{
		Vec3 side = right(direction).scale(0.34D);
		return entity.getEyePosition().add(0.0D, -0.48D, 0.0D).
				add(side).
				add(direction.scale(0.34D));
	}

	public static Vec3 spiralPosition(Vec3 origin, Vec3 direction, double distance)
	{
		if (distance <= 0.0D)
			return origin;

		Vec3 right = right(direction);
		Vec3 up = right.cross(direction).normalize();
		double angle = distance * SPIRAL_TURNS_PER_BLOCK * Math.PI * 2.0D;
		double radius = SPIRAL_RADIUS * Math.min(1.0D, distance / 0.70D);
		return origin.add(direction.scale(distance)).
				add(right.scale(Math.cos(angle) * radius)).
				add(up.scale(Math.sin(angle) * radius));
	}

	private static Vec3 right(Vec3 direction)
	{
		Vec3 right = direction.cross(WORLD_UP);
		if (right.lengthSqr() < 0.001D)
			right = direction.cross(WORLD_RIGHT);
		return right.normalize();
	}
}
