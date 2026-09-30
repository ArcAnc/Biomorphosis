/**
 * @author ArcAnc
 * Created at: 29.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.hitbox.client;

import com.arcanc.biomorphosis.content.entity.hitbox.OBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;

public final class OBBDebugRenderer
{
    private static final int GREEN = 0xff00ff00;
    private static final int BLUE = 0xff0080ff;
    private static final int[][] EDGES =
		    {
					{0, 1}, {1, 2}, {2, 3}, {3, 0},
				    {4, 5}, {5, 6}, {6, 7}, {7, 4},
				    {0, 4}, {1, 5}, {2, 6}, {3, 7}
			};

    private OBBDebugRenderer() {}

    public static void render(PoseStack poses, VertexConsumer buffer, OBB box, Vec3 origin)
    {
        Vec3[] corners = box.getCorners();
        for (int[] edge : EDGES)
            line(poses.last(), buffer, corners[edge[0]].subtract(origin), corners[edge[1]].subtract(origin), GREEN);
        Vec3 start = box.center().subtract(origin);
        Vec3 end = start.add(box.forward().scale(box.halfLength() + 0.5));
        line(poses.last(), buffer, start, end, BLUE);
        line(poses.last(), buffer, end, end.subtract(box.forward().scale(0.3)).add(box.right().scale(0.2)), BLUE);
        line(poses.last(), buffer, end, end.subtract(box.forward().scale(0.3)).subtract(box.right().scale(0.2)), BLUE);
    }

    private static void line(PoseStack.Pose pose, VertexConsumer buffer, Vec3 from, Vec3 to, int color)
    {
        Vec3 normal = to.subtract(from).normalize();
        buffer.addVertex(pose, (float)from.x, (float)from.y, (float)from.z).setColor(color).
		        setNormal(pose, (float)normal.x, (float)normal.y, (float)normal.z);
        buffer.addVertex(pose, (float)to.x, (float)to.y, (float)to.z).setColor(color).
		        setNormal(pose, (float)normal.x, (float)normal.y, (float)normal.z);
    }
}
