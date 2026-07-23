/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import java.util.EnumSet;

/**
 * A one-pixel-thick textured plane was taken from {@link <a href="https://github.com/Fuzss/fantastic-wings">Fantastic Wings</a>}
 * <p>
 * Permission was obtained directly from @Fuzs
 */
public final class Model3DTexture extends ModelPart.Cube
{
	private Model3DTexture(float x, float y, float z, int width, int height, int u, int v, int textureWidth, int textureHeight)
	{
		super(0, 0, x, y, z, 0, 0, 0, 0.0F, 0.0F, 0.0F, false, textureWidth, textureHeight,
				EnumSet.allOf(Direction.class));
		float x1 = x + width;
		float y1 = y + height;
		float z1 = z + 1.0F;
		ModelPart.Polygon[] polygons = new ModelPart.Polygon[2 + 2 * width + 2 * height];
		int index = 0;
		polygons[index++] = polygon(x, y, z, x1, y1, z, u, v, u + width, v + height, Direction.NORTH);
		polygons[index++] = polygon(x, y1, z1, x1, y, z1, u, v + height, u + width, v, Direction.SOUTH);
		for (int offset = 0; offset < width; offset++)
		{
			float x0 = x + offset;
			float textureU = u + offset + 0.5F;
			polygons[index++] = polygon(x0, y, z, x0, y1, z1, textureU, v, textureU, v + height, Direction.WEST);
			float x2 = x + offset + 1.0F;
			polygons[index++] = polygon(x2, y1, z, x2, y, z1, textureU, v + height, textureU, v, Direction.EAST);
		}
		for (int offset = 0; offset < height; offset++)
		{
			float y0 = y + offset;
			float textureV = v + offset + 0.5F;
			polygons[index++] = polygon(x, y0 + 1.0F, z, x1, y0 + 1.0F, z1, u, textureV, u + width, textureV, Direction.UP);
			polygons[index++] = polygon(x1, y0, z, x, y0, z1, u + width, textureV, u, textureV, Direction.DOWN);
		}
		this.polygons = polygons;
	}

	private static ModelPart.Polygon polygon(float x0, float y0, float z0, float x1, float y1, float z1,
	                                         float u0, float v0, float u1, float v1, Direction normal)
	{
		boolean vertical = normal.getAxis().isVertical();
		ModelPart.Vertex[] vertices = new ModelPart.Vertex[]{
				new ModelPart.Vertex(x1, y0, z0, 0.0F, 0.0F),
				new ModelPart.Vertex(x0, y0, vertical ? z0 : z1, 0.0F, 0.0F),
				new ModelPart.Vertex(x0, y1, z1, 0.0F, 0.0F),
				new ModelPart.Vertex(x1, y1, vertical ? z1 : z0, 0.0F, 0.0F)
		};
		return new ModelPart.Polygon(vertices, u0, v0, u1, v1, 64, 64, false, normal);
	}

	public static Model3DTexture create(float x, float y, float z, int width, int height, int u, int v,
	                                    int textureWidth, int textureHeight)
	{
		return new Model3DTexture(x, y, z, width, height, u, v, textureWidth, textureHeight);
	}

	@Override
	public void compile(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, int color)
	{
		super.compile(pose, consumer, light, overlay, color);
	}
}
