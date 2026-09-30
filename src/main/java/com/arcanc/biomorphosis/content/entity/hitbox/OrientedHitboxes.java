/**
 * @author ArcAnc
 * Created at: 29.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.hitbox;

import net.minecraft.world.entity.Entity;

public final class OrientedHitboxes
{
    private OrientedHitboxes() {}

    public static OBB box(Entity entity)
    {
        return entity instanceof IOrientedHitbox oriented ?
		        oriented.getOrientedHitbox() :
		        OBB.fromAABB(entity.getBoundingBox());
    }

    public static boolean intersects(Entity first, Entity second)
    {
        if (!(first instanceof IOrientedHitbox) && !(second instanceof IOrientedHitbox))
			return true;
        return box(first).intersects(box(second));
    }
}
