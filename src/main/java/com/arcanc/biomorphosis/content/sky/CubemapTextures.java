/**
 * @author ArcAnc
 * Created at: 27.09.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/** Owns cubemap GPU objects and replaces them atomically on every client resource reload. */
final class CubemapTextures
{
	private static final Map<CubemapDefinition, CubemapTexture> TEXTURES = new LinkedHashMap<>();

	private CubemapTextures()
	{
	}

	static void register(CubemapDefinition definition)
	{
		TEXTURES.computeIfAbsent(definition, CubemapTexture :: new);
	}

	static CubemapTexture get(CubemapDefinition definition)
	{
		return TEXTURES.get(definition);
	}

	static void registerReloadListener(IEventBus modEventBus)
	{
		modEventBus.addListener((RegisterClientReloadListenersEvent event) -> registerReloadListener(event));
	}

	private static void registerReloadListener(RegisterClientReloadListenersEvent event)
	{
		event.registerReloadListener(new SimplePreparableReloadListener<Map<CubemapTexture, CubemapTexture.LoadedCubemap>>()
		{
			@Override
			protected Map<CubemapTexture, CubemapTexture.LoadedCubemap> prepare(ResourceManager resourceManager, ProfilerFiller profiler)
			{
				Map<CubemapTexture, CubemapTexture.LoadedCubemap> loaded = new LinkedHashMap<>();
				for (CubemapTexture texture : TEXTURES.values())
					loaded.put(texture, CubemapTexture.load(resourceManager, texture.definition()));
				return loaded;
			}

			@Override
			protected void apply(Map<CubemapTexture, CubemapTexture.LoadedCubemap> loaded, ResourceManager resourceManager, ProfilerFiller profiler)
			{
				Runnable upload = () ->
				{
					loaded.forEach(CubemapTexture :: upload);
					BiomeSkyboxes.onResourcesReloaded();
				};
				if (RenderSystem.isOnRenderThreadOrInit())
					upload.run();
				else
					RenderSystem.recordRenderCall(upload :: run);
			}
		});
	}
}
