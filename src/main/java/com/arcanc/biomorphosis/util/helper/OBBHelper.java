/**
 * @author ArcAnc
 * Created at: 29.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;


import com.arcanc.biomorphosis.content.entity.hitbox.IOrientedHitbox;
import com.arcanc.biomorphosis.content.entity.hitbox.IOrientedQueryBounds;
import com.arcanc.biomorphosis.content.entity.hitbox.OBB;
import com.arcanc.biomorphosis.content.entity.hitbox.OrientedHitboxes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class OBBHelper
{
	public static final double CONTACT_EPSILON = 1.0e-7;
	public static final double VELOCITY_EPSILON = 1.0e-12;
	private static final Vec3 UP = new Vec3(0, 1, 0);
	
	private static Vec3[] axes(OBB a, OBB b)
	{
	    return new Vec3[]
			    {
							UP,
					    a.right(),
					    a.forward(),
					    b.right(),
					    b.forward()
					};
	}
	
	public static boolean intersects(OBB a, OBB b)
	{
	    Vec3 delta = b.center().subtract(a.center());
	    for (Vec3 axis : axes(a, b))
	        if (Math.abs(delta.dot(axis)) >= a.radius(axis) + b.radius(axis) - CONTACT_EPSILON)
	            return false;
	    return true;
	}
	
	public static double sweep(
			OBB moving,
			OBB obstacle,
			Vec3 movement)
	{
		Vec3 delta = obstacle.center().
				subtract(moving.center());
		
		double enter = Double.NEGATIVE_INFINITY;
		double exit = Double.POSITIVE_INFINITY;
		boolean initiallyOverlapping = true;
		
		for (Vec3 axis : axes(moving, obstacle))
		{
			double separation = delta.dot(axis);
			
			double radius =
					moving.radius(axis) +
							obstacle.radius(axis);
			double gap = Math.abs(separation) - radius;
			
			double speed = movement.dot(axis);
			
			if (Math.abs(speed) < VELOCITY_EPSILON)
			{
				if (gap >= -CONTACT_EPSILON)
					return 1.0;
				
				continue;
			}

			if (gap >= -CONTACT_EPSILON)
			{
				initiallyOverlapping = false;
				if (separation * speed <= 0.0)
					return 1.0;
			}
			
			double t0 = (separation - radius) / speed;
			double t1 = (separation + radius) / speed;
			
			double axisEnter = Math.min(t0, t1);
			double axisExit = Math.max(t0, t1);
			
			enter = Math.max(enter, axisEnter);
			exit = Math.min(exit, axisExit);
			
			if (enter >= exit)
				return 1.0;
		}
		
		if (exit <= 0.0)
			return 1.0;
		
		if (enter >= 1.0)
			return 1.0;
		
		if (enter < 0.0 && initiallyOverlapping)
			return 1.0;
		
		return Math.max(0.0, enter);
	}
	
	public static boolean hasOrientedCollider(Entity entity, Level level, AABB swept)
	{
	    return entity != null && level instanceof IOrientedQueryBounds bounds &&
			    bounds.biomorphosis$orientedReach() > 0 &&
			    !level.getEntities(entity, swept.inflate(CONTACT_EPSILON),
					    other -> other instanceof IOrientedHitbox &&
					EntitySelector.NO_SPECTATORS.test(other) && entity.canCollideWith(other)).isEmpty();
	}
	
	public static Vec3 collide(Entity entity,
	                       Vec3 movement,
	                       AABB collisionBox,
	                       Level level,
	                       List<VoxelShape> potentialEntityHits)
	{
	    OBB box = OrientedHitboxes.box(entity);
			
	    box = box.move(collisionBox.getCenter().subtract(box.enclosingAABB().getCenter()));
	    AABB swept = box.enclosingAABB().expandTowards(movement).inflate(CONTACT_EPSILON);
	    List<OBB> obstacles = new ArrayList<>();
	    List<Entity> candidates = level.getEntities(entity, swept,
	            EntitySelector.NO_SPECTATORS.and(entity::canCollideWith));
	    for (Entity candidate : candidates)
	        obstacles.add(OrientedHitboxes.box(candidate));
			
	    for (VoxelShape shape : potentialEntityHits)
	    {
	        if (!shape.isEmpty() && candidates.stream().noneMatch(e -> e.getBoundingBox().equals(shape.bounds())))
	            addShape(obstacles, shape, swept);
	    }
	    for (VoxelShape shape : level.getBlockCollisions(entity, swept))
	        addShape(obstacles, shape, swept);
	    if (level.getWorldBorder().isInsideCloseToBorder(entity, swept))
	        addShape(obstacles, level.getWorldBorder().getCollisionShape(), swept);
	    return resolve(box, movement, obstacles);
	}
	
	private static void addShape(List<OBB> obstacles, VoxelShape shape, AABB swept)
	{
	    for (AABB part : shape.toAabbs())
	    {
	        if (part.intersects(swept))
					obstacles.add(OBB.fromAABB(part.intersect(swept)));
	    }
	}
	
	public static Vec3 resolve(OBB box, Vec3 requested, List<OBB> obstacles)
	{
	    double y = limit(box, new Vec3(0, requested.y, 0), obstacles).y;
	    box = box.move(0, y, 0);
	    double x = requested.x, z = requested.z;
	    if (Math.abs(x) < Math.abs(z))
	    {
	        z = limit(box, new Vec3(0, 0, z), obstacles).z;
	        box = box.move(0, 0, z);
	        x = limit(box, new Vec3(x, 0, 0), obstacles).x;
	    }
	    else
	    {
	        x = limit(box, new Vec3(x, 0, 0), obstacles).x;
	        box = box.move(x, 0, 0);
	        z = limit(box, new Vec3(0, 0, z), obstacles).z;
	    }
	    return new Vec3(x, y, z);
	}
	
	private static Vec3 limit(OBB box, Vec3 movement, List<OBB> obstacles)
	{
	    if (movement.lengthSqr() == 0)
				return movement;
	    double fraction = 1;
	    for (OBB obstacle : obstacles)
	        fraction = Math.min(fraction, sweep(box, obstacle, movement));
	    return fraction == 1 ? movement : movement.scale(fraction);
	}
}
