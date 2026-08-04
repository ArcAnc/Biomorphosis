/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.wave;

import net.minecraft.world.phys.Vec3;

public final class WaveGeometry
{
	private static final Vec3 WORLD_UP = new Vec3(0.0, 1.0, 0.0);
	private static final Vec3 WORLD_RIGHT = new Vec3(1.0, 0.0, 0.0);

	private WaveGeometry()
	{
	}

	public static Vec3 right(Vec3 direction)
	{
		Vec3 right = direction.cross(WORLD_UP);
		if (right.lengthSqr() < 0.001)
			right = direction.cross(WORLD_RIGHT);
		return right.normalize();
	}

	public static Vec3 up(Vec3 direction)
	{
		return right(direction).cross(direction).normalize();
	}

	public static double heightFactor(double normalizedLateral)
	{
		return Math.sqrt(Math.max(0.0, 1.0 - normalizedLateral * normalizedLateral));
	}

	public static double forwardArcOffset(double normalizedLateral)
	{
		double heightFactor = heightFactor(normalizedLateral);
		return WaveAbility.ARC_DEPTH * heightFactor * heightFactor;
	}
}
