/**
 * @author ArcAnc
 * Created at: 31.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.wave.client;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

final class WaveRenderTypes
{
	private static final RenderType WAVE = RenderType.create("biomorphosis_wave",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.TRIANGLES,
			8192,
			false,
			true,
			RenderType.CompositeState.builder().
					setShaderState(new RenderStateShard.ShaderStateShard(WaveRenderTypes :: prepareShader)).
					setTransparencyState(RenderType.ADDITIVE_TRANSPARENCY).
					setDepthTestState(RenderType.LEQUAL_DEPTH_TEST).
					setWriteMaskState(RenderType.COLOR_WRITE).
					setCullState(RenderType.NO_CULL).
					createCompositeState(false));

	private static @Nullable ShaderInstance shader;

	private WaveRenderTypes()
	{
	}

	static RenderType wave()
	{
		return WAVE;
	}
	
	private static @Nullable ShaderInstance prepareShader()
	{
		if (shader == null)
			return null;

		Minecraft minecraft = RenderHelper.mc();
		float gameTime = minecraft.level == null ?
				(Util.getMillis() % 1_200_000L) / 1000.0F :
				(minecraft.level.getGameTime() + minecraft.getTimer().getGameTimeDeltaPartialTick(false)) / 20.0F;
		shader.safeGetUniform("GameTime").set(gameTime);
		return shader;
	}

	private static void registerShader(RegisterShadersEvent event)
	{
		try
		{
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Database.rl("ability/wave"), DefaultVertexFormat.POSITION_TEX_COLOR),
					shaderInstance -> shader = shaderInstance);
		}
		catch (IOException exception)
		{
			Database.LOGGER.warn("Failed to register wave shader: {}", String.valueOf(exception));
		}
	}

	static void register(IEventBus modEventBus)
	{
		modEventBus.addListener(WaveRenderTypes :: registerShader);
	}
}
