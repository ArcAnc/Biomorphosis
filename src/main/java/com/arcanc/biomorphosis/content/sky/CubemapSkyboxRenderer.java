/**
 * @author ArcAnc
 * Created at: 27.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;

import java.io.IOException;

/** Renders the six faces only as interpolation geometry; colour comes exclusively from samplerCube directions. */
final class CubemapSkyboxRenderer
{
	private static final float SKYBOX_SIZE = 100.0F;
	private static CubemapShader shader;

	private CubemapSkyboxRenderer()
	{
	}

	static void register(IEventBus modEventBus)
	{
		modEventBus.addListener(CubemapSkyboxRenderer :: registerShader);
	}

	private static void registerShader(RegisterShadersEvent event)
	{
		try
		{
			event.registerShader(new CubemapShader(event.getResourceProvider()), loaded -> shader = (CubemapShader)loaded);
		}
		catch (IOException exception)
		{
			Database.LOGGER.error("Failed to register cubemap skybox shader", exception);
		}
	}

	static void render(CubemapDefinition definition, float alpha, float rotation, float red, float green, float blue, PoseStack poseStack)
	{
		CubemapTexture texture = CubemapTextures.get(definition);
		if (shader == null || texture == null || !texture.isLoaded() || alpha <= 0.001F)
			return;

		int previousActiveTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
		RenderSystem.activeTexture(GL13C.GL_TEXTURE0);
		int previousCubemap = GL11C.glGetInteger(GL13C.GL_TEXTURE_BINDING_CUBE_MAP);
		RenderSystem.activeTexture(previousActiveTexture);

		shader.setCubemap(texture.textureId());
		Matrix4f cameraRotation = new Matrix4f(poseStack.last().pose());
		cameraRotation.m30(0.0F).m31(0.0F).m32(0.0F);
		shader.setDirectionMatrix(new Matrix4f(cameraRotation).invert());
		RenderSystem.setShader(() -> shader);
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		addCube(buffer, cameraRotation, rotation, red, green, blue, BiomeSkyboxes.alphaToInt(alpha));
		BufferUploader.drawWithShader(buffer.buildOrThrow());

		RenderSystem.activeTexture(GL13C.GL_TEXTURE0);
		GL11C.glBindTexture(GL13C.GL_TEXTURE_CUBE_MAP, previousCubemap);
		RenderSystem.activeTexture(previousActiveTexture);
	}

	private static void addCube(BufferBuilder buffer, Matrix4f cameraRotation, float rotation, float red, float green, float blue, int alpha)
	{
		float cos = (float)Math.cos(rotation);
		float sin = (float)Math.sin(rotation);
		int redInt = colorToInt(red);
		int greenInt = colorToInt(green);
		int blueInt = colorToInt(blue);

		// +X, -X, +Y, -Y, +Z, -Z.  The winding is irrelevant because sky rendering disables culling.
		addFace(buffer, cameraRotation, cos, sin, 1, -1, -1, 1, -1, 1, 1, 1, 1, 1, 1, -1, redInt, greenInt, blueInt, alpha);
		addFace(buffer, cameraRotation, cos, sin, -1, -1, 1, -1, -1, -1, -1, 1, -1, -1, 1, 1, redInt, greenInt, blueInt, alpha);
		addFace(buffer, cameraRotation, cos, sin, -1, 1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1, redInt, greenInt, blueInt, alpha);
		addFace(buffer, cameraRotation, cos, sin, -1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, redInt, greenInt, blueInt, alpha);
		addFace(buffer, cameraRotation, cos, sin, 1, -1, 1, -1, -1, 1, -1, 1, 1, 1, 1, 1, redInt, greenInt, blueInt, alpha);
		addFace(buffer, cameraRotation, cos, sin, -1, -1, -1, 1, -1, -1, 1, 1, -1, -1, 1, -1, redInt, greenInt, blueInt, alpha);
	}

	private static void addFace(BufferBuilder buffer,
	                            Matrix4f cameraRotation,
	                            float cos,
	                            float sin,
	                            float x0, float y0, float z0,
	                            float x1, float y1, float z1,
	                            float x2, float y2, float z2,
	                            float x3, float y3, float z3,
	                            int red, int green, int blue, int alpha)
	{
		addVertex(buffer, cameraRotation, cos, sin, x0, y0, z0, red, green, blue, alpha);
		addVertex(buffer, cameraRotation, cos, sin, x1, y1, z1, red, green, blue, alpha);
		addVertex(buffer, cameraRotation, cos, sin, x2, y2, z2, red, green, blue, alpha);
		addVertex(buffer, cameraRotation, cos, sin, x3, y3, z3, red, green, blue, alpha);
	}

	private static void addVertex(BufferBuilder buffer, Matrix4f cameraRotation, float cos, float sin, float x, float y, float z, int red, int green, int blue, int alpha)
	{
		float rotatedX = x * cos - z * sin;
		float rotatedZ = x * sin + z * cos;
		buffer.addVertex(cameraRotation, rotatedX * SKYBOX_SIZE, y * SKYBOX_SIZE, rotatedZ * SKYBOX_SIZE).setColor(red, green, blue, alpha);
	}

	private static int colorToInt(float color)
	{
		return Math.clamp((int)(color * 255.0F), 0, 255);
	}

	private static final class CubemapShader extends ShaderInstance
	{
		private int cubemapTexture;

		private CubemapShader(net.minecraft.server.packs.resources.ResourceProvider provider) throws IOException
		{
			super(provider, Database.rl("skybox/cubemap"), DefaultVertexFormat.POSITION_COLOR);
			setSampler("Skybox", 0);
		}

		void setCubemap(int texture)
		{
			cubemapTexture = texture;
		}

		void setDirectionMatrix(Matrix4f directionMatrix)
		{
			safeGetUniform("DirectionMat").set(directionMatrix);
		}

		@Override
		public void apply()
		{
			super.apply();
			int previousActiveTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
			RenderSystem.activeTexture(GL13C.GL_TEXTURE0);
			GL11C.glBindTexture(GL13C.GL_TEXTURE_CUBE_MAP, cubemapTexture);
			RenderSystem.activeTexture(previousActiveTexture);
		}
	}
}
