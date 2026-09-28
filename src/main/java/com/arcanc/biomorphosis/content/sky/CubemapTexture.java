/**
 * @author ArcAnc
 * Created at: 27.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL32C;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.List;

/** A GPU texture whose target is {@link GL13C#GL_TEXTURE_CUBE_MAP}, not Minecraft's 2D texture manager. */
final class CubemapTexture implements AutoCloseable
{
	private static final int[] FACE_TARGETS = {
			GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
			GL13C.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
			GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
			GL13C.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
			GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
			GL13C.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z};

	private final CubemapDefinition definition;
	private int textureId;

	CubemapTexture(CubemapDefinition definition)
	{
		this.definition = definition;
	}

	CubemapDefinition definition()
	{
		return definition;
	}

	int textureId()
	{
		return textureId;
	}

	boolean isLoaded()
	{
		return textureId != 0;
	}

	static LoadedCubemap load(ResourceManager resourceManager, CubemapDefinition definition)
	{
		List<net.minecraft.resources.ResourceLocation> faces = definition.faces();
		int[][] pixels = new int[faces.size()][];
		int size = -1;

		for (int index = 0; index < faces.size(); index++)
		{
			var location = faces.get(index);
			var resource = resourceManager.getResource(location).
					orElseThrow(() -> new IllegalStateException("Missing cubemap face " + location));
			try (var stream = resource.open();
				 NativeImage image = NativeImage.read(NativeImage.Format.RGBA, stream))
			{
				if (image.getWidth() != image.getHeight())
					throw new IllegalStateException("Cubemap face " + location + " must be square, found " + image.getWidth() + "x" + image.getHeight());
				if (size != -1 && size != image.getWidth())
					throw new IllegalStateException("Cubemap face " + location + " is " + image.getWidth() + "x" + image.getHeight() +
							" but the other faces of " + definition + " are " + size + "x" + size);

				size = image.getWidth();
				pixels[index] = image.getPixelsRGBA();
			}
			catch (IOException exception)
			{
				throw new IllegalStateException("Unable to load cubemap face " + location, exception);
			}
		}

		return new LoadedCubemap(size, pixels);
	}

	void upload(LoadedCubemap loaded)
	{
		RenderSystem.assertOnRenderThreadOrInit();
		close();

		int previousActiveTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
		RenderSystem.activeTexture(GL13C.GL_TEXTURE0);
		int previousCubemap = GL11C.glGetInteger(GL13C.GL_TEXTURE_BINDING_CUBE_MAP);

		textureId = GL11C.glGenTextures();
		GL11C.glBindTexture(GL13C.GL_TEXTURE_CUBE_MAP, textureId);
		GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_MIN_FILTER, GL11C.GL_LINEAR);
		GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR);
		GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_WRAP_S, GL12C.GL_CLAMP_TO_EDGE);
		GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL11C.GL_TEXTURE_WRAP_T, GL12C.GL_CLAMP_TO_EDGE);
		GL11C.glTexParameteri(GL13C.GL_TEXTURE_CUBE_MAP, GL12C.GL_TEXTURE_WRAP_R, GL12C.GL_CLAMP_TO_EDGE);

		for (int index = 0; index < FACE_TARGETS.length; index++)
		{
			IntBuffer pixels = BufferUtils.createIntBuffer(loaded.size() * loaded.size());
			pixels.put(orientedPixels(loaded.pixels()[index], loaded.size(), index)).flip();
			GL11C.glTexImage2D(FACE_TARGETS[index], 0, GL11C.GL_RGBA, loaded.size(), loaded.size(), 0,
					GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE, pixels);
		}

		GL32C.glEnable(GL32C.GL_TEXTURE_CUBE_MAP_SEAMLESS);
		GL11C.glBindTexture(GL13C.GL_TEXTURE_CUBE_MAP, previousCubemap);
		RenderSystem.activeTexture(previousActiveTexture);
	}

	/**
	 * Cubemap PNGs are conventionally authored while looking at the outside of a
	 * cube. The four lateral faces are viewed from its inside at runtime, so their
	 * horizontal pixel order must be reversed. The top and bottom faces keep their
	 * original orientation.
	 */
	private static int[] orientedPixels(int[] source, int size, int faceIndex)
	{
		if (faceIndex == 2 || faceIndex == 3)
			return source;

		int[] result = new int[source.length];
		for (int y = 0; y < size; y++)
			for (int x = 0; x < size; x++)
				result[y * size + x] = source[y * size + (size - 1 - x)];
		return result;
	}

	@Override
	public void close()
	{
		if (textureId == 0)
			return;

		RenderSystem.assertOnRenderThreadOrInit();
		GL11C.glDeleteTextures(textureId);
		textureId = 0;
	}

	record LoadedCubemap(int size, int[][] pixels)
	{
	}
}
