/**
 * @author ArcAnc
 * Created at: 29.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.hitbox;

import com.arcanc.biomorphosis.util.helper.OBBHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;

public final class OBB
{
    private final Vec3 center;
    private final double halfWidth, halfHeight, halfLength;
    private final float yaw;
    private final Vec3 right, forward;

    public OBB(Vec3 center, double halfWidth, double halfHeight, double halfLength, float yaw)
    {
        this.center = Objects.requireNonNull(center);
        if (!Double.isFinite(center.x) || !Double.isFinite(center.y) || !Double.isFinite(center.z)
                || !Double.isFinite(halfWidth) || !Double.isFinite(halfHeight) || !Double.isFinite(halfLength)
                || halfWidth < 0 || halfHeight < 0 || halfLength < 0 || !Float.isFinite(yaw))
            throw new IllegalArgumentException("OBB requires finite coordinates and nonnegative extents");
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.halfLength = halfLength;
        this.yaw = yaw;
        double radians = java.lang.Math.toRadians(yaw);
        double sin = java.lang.Math.sin(radians), cos = java.lang.Math.cos(radians);
        this.right = new Vec3(cos, 0, sin);
        this.forward = new Vec3(-sin, 0, cos);
    }

    public static OBB at(Vec3 position, Vec3 localCenterOffset, double width, double height, double length, float yaw)
    {
        OBB origin = new OBB(position, width / 2, height / 2, length / 2, yaw);
        return origin.move(origin.localToWorld(localCenterOffset).subtract(position));
    }

    public static OBB fromAABB(AABB box)
    {
        return new OBB(box.getCenter(), box.getXsize() / 2, box.getYsize() / 2, box.getZsize() / 2, 0);
    }

    public Vec3 center()
    {
		return this.center;
	}
    public double halfWidth()
    {
		return this.halfWidth;
	}
    public double halfHeight()
    {
		return this.halfHeight;
	}
    public double halfLength()
    {
		return this.halfLength;
	}
    public float yaw()
    {
		return this.yaw;
	}
    public Vec3 forward()
    {
		return this.forward;
	}
    public Vec3 right()
    {
		return this.right;
	}

    public Vec3 worldToLocal(Vec3 world)
    {
        Vec3 delta = world.subtract(this.center);
        return new Vec3(delta.dot(this.right), delta.y, delta.dot(forward));
    }

    public Vec3 localToWorld(Vec3 local)
    {
        return this.center.add(this.right.scale(local.x)).add(0, local.y, 0).add(this.forward.scale(local.z));
    }

    public Vec3[] getCorners()
    {
        return new Vec3[] {
                localToWorld(new Vec3(-this.halfWidth, -this.halfHeight, -this.halfLength)),
                localToWorld(new Vec3(this.halfWidth, -this.halfHeight, -this.halfLength)),
                localToWorld(new Vec3(this.halfWidth, -this.halfHeight, this.halfLength)),
                localToWorld(new Vec3(-this.halfWidth, -this.halfHeight, this.halfLength)),
                localToWorld(new Vec3(-this.halfWidth, this.halfHeight, -this.halfLength)),
                localToWorld(new Vec3(this.halfWidth, this.halfHeight, -this.halfLength)),
                localToWorld(new Vec3(this.halfWidth, this.halfHeight, this.halfLength)),
                localToWorld(new Vec3(-this.halfWidth, this.halfHeight, this.halfLength))
        };
    }

    public AABB enclosingAABB()
    {
        double x = java.lang.Math.abs(this.right.x) * this.halfWidth + java.lang.Math.abs(this.forward.x) * this.halfLength;
        double z = java.lang.Math.abs(this.right.z) * this.halfWidth + java.lang.Math.abs(this.forward.z) * this.halfLength;
        return new AABB(this.center.x - x, this.center.y - this.halfHeight, this.center.z - z,
                this.center.x + x, this.center.y + this.halfHeight, this.center.z + z);
    }

    public OBB move(Vec3 offset)
    {
		return move(offset.x, offset.y, offset.z);
	}
    public OBB move(double x, double y, double z)
    {
        return new OBB(this.center.add(x, y, z), this.halfWidth, this.halfHeight, this.halfLength, this.yaw);
    }
    public OBB inflate(double amount)
    {
		return inflate(amount, amount, amount);
	}
    public OBB inflate(double x, double y, double z)
    {
        return new OBB(this.center, this.halfWidth + x, this.halfHeight + y, this.halfLength + z, this.yaw);
    }

    private AABB localBox()
    {
        return new AABB(-this.halfWidth, -this.halfHeight, -this.halfLength, this.halfWidth, this.halfHeight, this.halfLength);
    }

    public boolean contains(Vec3 point)
    {
		return localBox().contains(worldToLocal(point));
	}
    public boolean intersects(AABB box)
    {
		return intersects(fromAABB(box));
	}
    public boolean intersects(OBB box)
    {
		return OBBHelper.intersects(this, box);
	}
	
    public Optional<Vec3> clip(Vec3 start, Vec3 end)
    {
        if (contains(start)) return Optional.of(start);
        return localBox().clip(worldToLocal(start), worldToLocal(end)).map(this::localToWorld);
    }

    public double distanceToSqr(Vec3 point)
    {
		return localBox().distanceToSqr(worldToLocal(point));
	}

    public double radius(Vec3 axis)
    {
        return this.halfWidth * java.lang.Math.abs(this.right.dot(axis)) + this.halfHeight * java.lang.Math.abs(axis.y)
                + this.halfLength * java.lang.Math.abs(this.forward.dot(axis));
    }
}
