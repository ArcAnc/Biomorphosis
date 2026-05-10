/**
 * @author ArcAnc
 * Created at: 05.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.font;


import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class BioGlyphRenderTypes
{
	public static class BioRenderTypesProvider
	{
		private static final Function<ResourceLocation, RenderType> BIO_TEXT = Util.memoize(BioGlyphRenderTypes.BioRenderTypesProvider :: bioText);
		private static final Function<ResourceLocation, RenderType> BIO_TEXT_SEE_THROUGH = Util.memoize(BioGlyphRenderTypes.BioRenderTypesProvider :: bioTextSeeThrough);
		private static final Function<ResourceLocation, RenderType> BIO_TEXT_POLYGON_OFFSET = Util.memoize(BioGlyphRenderTypes.BioRenderTypesProvider :: bioTextPolygonOffset);
		private static final Function<ResourceLocation, RenderType> BIO_TEXT_INTENSITY = Util.memoize(BioGlyphRenderTypes.BioRenderTypesProvider :: bioTextIntensity);
		private static final Function<ResourceLocation, RenderType> BIO_TEXT_INTENSITY_SEE_THROUGH = Util.memoize(BioGlyphRenderTypes.BioRenderTypesProvider :: bioTextIntensitySeeThrough);
		private static final Function<ResourceLocation, RenderType> BIO_TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(BioGlyphRenderTypes.BioRenderTypesProvider :: bioTextIntensityPolygonOffset);
		
		public static GlyphRenderTypes create(ResourceLocation resourceLocation, boolean colored)
		{
			return colored
					? new GlyphRenderTypes(
					BIO_TEXT.apply(resourceLocation),
					BIO_TEXT_SEE_THROUGH.apply(resourceLocation),
					BIO_TEXT_POLYGON_OFFSET.apply(resourceLocation))
					: new GlyphRenderTypes(
					BIO_TEXT_INTENSITY.apply(resourceLocation),
					BIO_TEXT_INTENSITY_SEE_THROUGH.apply(resourceLocation),
					BIO_TEXT_INTENSITY_POLYGON_OFFSET.apply(resourceLocation));
		}
		
		private static RenderType bioText(ResourceLocation resourceLocation)
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder().
					setShaderState(ShadersProvider.StateShard.BIO_TEXT_STATE_SHARD).
					setTextureState(new CustomizableTextureState(resourceLocation, () -> NeoForgeRenderTypes.enableTextTextureLinearFiltering, () -> false)).
					setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
					setLightmapState(RenderType.LIGHTMAP).
					createCompositeState(false);
			return RenderType.create("bio_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, state);
		}
		
		private static RenderType bioTextSeeThrough(ResourceLocation resourceLocation)
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder().
					setShaderState(ShadersProvider.StateShard.BIO_TEXT_SEE_THROUGH_STATE_SHARD).
					setTextureState(new CustomizableTextureState(resourceLocation, () -> NeoForgeRenderTypes.enableTextTextureLinearFiltering, () -> false)).
					setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
					setLightmapState(RenderType.LIGHTMAP).
					setDepthTestState(RenderType.NO_DEPTH_TEST).
					setWriteMaskState(RenderType.COLOR_WRITE).
					createCompositeState(false);
			return RenderType.create("bio_text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, state);
		}
		
		private static RenderType bioTextPolygonOffset(ResourceLocation resourceLocation)
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder().
					setShaderState(ShadersProvider.StateShard.BIO_TEXT_STATE_SHARD).
					setTextureState(new CustomizableTextureState(resourceLocation, () -> NeoForgeRenderTypes.enableTextTextureLinearFiltering, () -> false)).
					setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
					setLightmapState(RenderType.LIGHTMAP).
					setLayeringState(RenderType.POLYGON_OFFSET_LAYERING).
					createCompositeState(false);
			return RenderType.create("bio_text_polygon_offset", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, state);
		}
		
		private static RenderType bioTextIntensity(ResourceLocation resourceLocation)
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder().
					setShaderState(ShadersProvider.StateShard.BIO_TEXT_INTENSITY_STATE_SHARD).
					setTextureState(new CustomizableTextureState(resourceLocation, () -> NeoForgeRenderTypes.enableTextTextureLinearFiltering, () -> false)).
					setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
					setLightmapState(RenderType.LIGHTMAP).
					createCompositeState(false);
			return RenderType.create("bio_text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, state);
		}
		
		private static RenderType bioTextIntensitySeeThrough(ResourceLocation resourceLocation)
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder().
					setShaderState(ShadersProvider.StateShard.BIO_TEXT_INTENSITY_SEE_THROUGH_STATE_SHARD).
					setTextureState(new CustomizableTextureState(resourceLocation, () -> NeoForgeRenderTypes.enableTextTextureLinearFiltering, () -> false)).
					setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
					setLightmapState(RenderType.LIGHTMAP).
					setDepthTestState(RenderType.NO_DEPTH_TEST).
					setWriteMaskState(RenderType.COLOR_WRITE).
					createCompositeState(false);
			return RenderType.create("bio_text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, state);
		}
		
		private static RenderType bioTextIntensityPolygonOffset(ResourceLocation resourceLocation)
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder().
					setShaderState(ShadersProvider.StateShard.BIO_TEXT_INTENSITY_STATE_SHARD).
					setTextureState(new CustomizableTextureState(resourceLocation, () -> NeoForgeRenderTypes.enableTextTextureLinearFiltering, () -> false)).
					setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
					setLightmapState(RenderType.LIGHTMAP).
					setLayeringState(RenderType.POLYGON_OFFSET_LAYERING).
					createCompositeState(false);
			return RenderType.create("bio_text_intensity_polygon_offset", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, state);
		}
	}
	
	private static class CustomizableTextureState extends RenderStateShard.TextureStateShard
	{
		private final BooleanSupplier blurSupplier;
		private final BooleanSupplier mipmapSupplier;
		
		private CustomizableTextureState(ResourceLocation resLoc, BooleanSupplier blur, BooleanSupplier mipmap)
		{
			super(resLoc, blur.getAsBoolean(), mipmap.getAsBoolean());
			this.blurSupplier = blur;
			this.mipmapSupplier = mipmap;
		}
		
		public void setupRenderState()
		{
			this.blur = this.blurSupplier.getAsBoolean();
			this.mipmap = this.mipmapSupplier.getAsBoolean();
			super.setupRenderState();
		}
	}
	
	public static class ShadersProvider
	{
		public static class StateShard
		{
			private static final RenderStateShard.ShaderStateShard BIO_TEXT_STATE_SHARD = new RenderStateShard.ShaderStateShard(() -> BIO_TEXT_SHADER);
			private static final RenderStateShard.ShaderStateShard BIO_TEXT_INTENSITY_STATE_SHARD = new RenderStateShard.ShaderStateShard(() -> BIO_TEXT_INTENSITY_SHADER);
			private static final RenderStateShard.ShaderStateShard BIO_TEXT_SEE_THROUGH_STATE_SHARD = new RenderStateShard.ShaderStateShard(() -> BIO_TEXT_SEE_THROUGH_SHADER);
			private static final RenderStateShard.ShaderStateShard BIO_TEXT_INTENSITY_SEE_THROUGH_STATE_SHARD = new RenderStateShard.ShaderStateShard(() -> BIO_TEXT_INTENSITY_SEE_THROUGH_SHADER);
		}
		
		@Nullable
		public static ShaderInstance BIO_TEXT_SHADER;
		@Nullable
		public static ShaderInstance BIO_TEXT_INTENSITY_SHADER;
		@Nullable
		public static ShaderInstance BIO_TEXT_SEE_THROUGH_SHADER;
		@Nullable
		public static ShaderInstance BIO_TEXT_INTENSITY_SEE_THROUGH_SHADER;
		
		private static void registerShaders(final RegisterShadersEvent event)
		{
			try
			{
				event.registerShader(new ShaderInstance(
								event.getResourceProvider(),
								Database.rl("bio_text"),
								DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
						shaderInstance -> BIO_TEXT_SHADER = shaderInstance);
				event.registerShader(new ShaderInstance(
								event.getResourceProvider(),
								Database.rl("bio_text_intensity"),
								DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
						shaderInstance -> BIO_TEXT_INTENSITY_SHADER = shaderInstance);
				event.registerShader(new ShaderInstance(
								event.getResourceProvider(),
								Database.rl("bio_text_see_through"),
								DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
						shaderInstance -> BIO_TEXT_SEE_THROUGH_SHADER = shaderInstance);
				event.registerShader(new ShaderInstance(
								event.getResourceProvider(),
								Database.rl("bio_text_intensity_see_through"),
								DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
						shaderInstance -> BIO_TEXT_INTENSITY_SEE_THROUGH_SHADER = shaderInstance);
			}
			catch (IOException e)
			{
				Database.LOGGER.warn("Failed to register shaders: {}", String.valueOf(e));
			}
		}
	}
	
	public static void register(IEventBus modEventBus)
	{
		modEventBus.addListener(BioGlyphRenderTypes.ShadersProvider :: registerShaders);
	}
}