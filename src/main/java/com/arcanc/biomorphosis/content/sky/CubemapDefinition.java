/**
 * @author ArcAnc
 * Created at: 27.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * The six OpenGL cubemap faces.  A face is selected by the corresponding local
 * sky direction: {@code +X}, {@code -X}, {@code +Y}, {@code -Y}, {@code +Z},
 * or {@code -Z}.  PNG orientation is the normal OpenGL cubemap orientation for
 * that face; use labelled faces while authoring a new cubemap to verify it.
 */
public record CubemapDefinition(ResourceLocation positiveX,
                                ResourceLocation negativeX,
                                ResourceLocation positiveY,
                                ResourceLocation negativeY,
                                ResourceLocation positiveZ,
                                ResourceLocation negativeZ)
{
	public List<ResourceLocation> faces()
	{
		return List.of(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
	}

	/**
	 * Creates the conventional six-face layout beneath a logical texture directory.
	 * For example, {@code biomorphosis:environment/skybox/wastes/day} resolves to
	 * {@code assets/biomorphosis/textures/environment/skybox/wastes/day/positive_x.png},
	 * and the five equivalent face names.
	 */
	public static CubemapDefinition fromDirectory(ResourceLocation directory)
	{
		return new CubemapDefinition(
				face(directory, "positive_x"),
				face(directory, "negative_x"),
				face(directory, "positive_y"),
				face(directory, "negative_y"),
				face(directory, "positive_z"),
				face(directory, "negative_z"));
	}

	private static ResourceLocation face(ResourceLocation directory, String face)
	{
		return directory.withSuffix("/" + face + ".png").withPrefix("textures/");
	}
}
