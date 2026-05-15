/**
 * @author ArcAnc
 * Created at: 15.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.font;


import com.arcanc.biomorphosis.util.Database;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

final class BioMsdfAtlas
{
	private static final int SPACE_CODEPOINT = 32;
	private static final float SPACE_ADVANCE_EM = 0.25F;
	
	private static final Codec<Integer> CODEPOINT_CODEC = Codec.either(Codec.INT, Codec.STRING).xmap(
			either -> either.map(codepoint -> codepoint, string -> string.codePointAt(0)),
			Either :: left);
	
	private static final Codec<AtlasData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			AtlasInfo.CODEC.fieldOf("atlas").forGetter(AtlasData :: atlas),
			Metrics.CODEC.fieldOf("metrics").forGetter(AtlasData :: metrics),
			Glyph.CODEC.listOf().fieldOf("glyphs").forGetter(AtlasData :: glyphs)
	).apply(instance, AtlasData :: new));
	
	private final ResourceLocation source;
	private final AtlasInfo atlas;
	private final Metrics metrics;
	private final List<Glyph> glyphs;
	private final Int2ObjectMap<Glyph> glyphsByCodepoint = new Int2ObjectOpenHashMap<>();
	
	private BioMsdfAtlas(ResourceLocation source, AtlasData data)
	{
		this.source = source;
		this.atlas = data.atlas();
		this.metrics = data.metrics();
		this.glyphs = data.glyphs();
		
		for (Glyph glyph : this.glyphs)
		{
			Optional<Integer> codepoint = glyph.resolvedCodepoint();
			if (codepoint.isEmpty())
				continue;
			
			Glyph previous = this.glyphsByCodepoint.put(codepoint.get(), glyph);
			if (previous != null)
				Database.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(codepoint.get()), source);
		}
		
		if (this.glyphsByCodepoint.isEmpty())
		{
			Database.LOGGER.error("{} does not contain unicode/codepoint/character data. Regenerate the MSDF atlas with unicode values; glyph indexes are not enough to map Minecraft text characters.", source);
		}
	}
	
	static BioMsdfAtlas load(ResourceManager resourceManager, ResourceLocation textureLocation) throws IOException
	{
		ResourceLocation jsonLocation = atlasLocation(textureLocation);
		Optional<Resource> resource = resourceManager.getResource(jsonLocation);
		
		if (resource.isEmpty())
			throw new IOException("Missing MSDF atlas metadata: " + jsonLocation);
		
		try (InputStream stream = resource.get().open();
		     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
		{
			JsonElement json = JsonParser.parseReader(reader);
			Optional<AtlasData> parsed = CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(message ->
					Database.LOGGER.error("Failed to parse {}: {}", jsonLocation, message));
			
			if (parsed.isEmpty())
				throw new IOException("Failed to parse MSDF atlas metadata: " + jsonLocation);
			
			return new BioMsdfAtlas(jsonLocation, parsed.get());
		}
	}
	
	private static ResourceLocation atlasLocation(ResourceLocation textureLocation)
	{
		String path = textureLocation.getPath();
		if (path.endsWith(".png"))
			path = path.substring(0, path.length() - ".png".length()) + ".json";
		else
			path += ".json";
		return ResourceLocation.fromNamespaceAndPath(textureLocation.getNamespace(), path);
	}
	
	GlyphInfo toGlyphInfo(NativeImage image, Glyph glyph, int height)
	{
		float fontScale = (float)height / Math.max(this.metrics.emSize(), 0.0001F);
		if (glyph.atlasBounds().isEmpty() || glyph.planeBounds().isEmpty())
			return BioGlyphInfo.empty(glyph.advance() * fontScale);
		
		Bounds atlasBounds = glyph.atlasBounds().get();
		Bounds planeBounds = glyph.planeBounds().get();
		int atlasTop = ceil(Math.max(atlasBounds.top(), atlasBounds.bottom()));
		int atlasBottom = floor(Math.min(atlasBounds.top(), atlasBounds.bottom()));
		int offsetX = floor(atlasBounds.left());
		int offsetY = this.atlas.bottomOrigin() ? this.atlas.height() - atlasTop : atlasBottom;
		int pixelWidth = Math.max(1, ceil(atlasBounds.right()) - offsetX);
		int pixelHeight = Math.max(1, atlasTop - atlasBottom);
		float planeWidth = Math.max(0.0001F, (planeBounds.right() - planeBounds.left()) * fontScale);
		float planeHeight = Math.max(0.0001F, (planeBounds.top() - planeBounds.bottom()) * fontScale);
		float oversample = Math.max(pixelWidth / planeWidth, pixelHeight / planeHeight);
		
		return new BioGlyphInfo(
				oversample,
				image,
				offsetX,
				offsetY,
				pixelWidth,
				pixelHeight,
				glyph.advance() * fontScale,
				planeBounds.left() * fontScale,
				planeBounds.top() * fontScale);
	}
	
	int spaceCodepoint()
	{
		return SPACE_CODEPOINT;
	}
	
	GlyphInfo spaceGlyphInfo(int height)
	{
		float fontScale = (float)height / Math.max(this.metrics.emSize(), 0.0001F);
		return BioGlyphInfo.empty(SPACE_ADVANCE_EM * fontScale);
	}
	
	IntSet supportedCodepoints()
	{
		return this.glyphsByCodepoint.keySet();
	}
	
	Optional<Glyph> glyph(int codepoint)
	{
		return Optional.ofNullable(this.glyphsByCodepoint.get(codepoint));
	}
	
	float distanceRange()
	{
		return this.atlas.distanceRange();
	}
	
	float lineHeight()
	{
		return this.metrics.lineHeight();
	}
	
	ResourceLocation source()
	{
		return this.source;
	}
	
	private static int floor(float value)
	{
		return (int)Math.floor(value);
	}
	
	private static int ceil(float value)
	{
		return (int)Math.ceil(value);
	}
	
	private record AtlasData(AtlasInfo atlas, Metrics metrics, List<Glyph> glyphs)
	{
	}
	
	record AtlasInfo(String type,
	                 float distanceRange,
	                 float distanceRangeMiddle,
	                 float size,
	                 int width,
	                 int height,
	                 String yOrigin)
	{
		private static final Codec<AtlasInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.optionalFieldOf("type", "msdf").forGetter(AtlasInfo :: type),
				Codec.FLOAT.optionalFieldOf("distanceRange", 4.0F).forGetter(AtlasInfo :: distanceRange),
				Codec.FLOAT.optionalFieldOf("distanceRangeMiddle", 0.0F).forGetter(AtlasInfo :: distanceRangeMiddle),
				Codec.FLOAT.optionalFieldOf("size", 1.0F).forGetter(AtlasInfo :: size),
				Codec.INT.fieldOf("width").forGetter(AtlasInfo :: width),
				Codec.INT.fieldOf("height").forGetter(AtlasInfo :: height),
				Codec.STRING.optionalFieldOf("yOrigin", "bottom").forGetter(AtlasInfo :: yOrigin)
		).apply(instance, AtlasInfo :: new));
		
		private boolean bottomOrigin()
		{
			return "bottom".equals(this.yOrigin);
		}
	}
	
	record Metrics(float emSize,
	               float lineHeight,
	               float ascender,
	               float descender,
	               float underlineY,
	               float underlineThickness)
	{
		private static final Codec<Metrics> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.FLOAT.optionalFieldOf("emSize", 1.0F).forGetter(Metrics :: emSize),
				Codec.FLOAT.optionalFieldOf("lineHeight", 1.0F).forGetter(Metrics :: lineHeight),
				Codec.FLOAT.optionalFieldOf("ascender", 0.0F).forGetter(Metrics :: ascender),
				Codec.FLOAT.optionalFieldOf("descender", 0.0F).forGetter(Metrics :: descender),
				Codec.FLOAT.optionalFieldOf("underlineY", 0.0F).forGetter(Metrics :: underlineY),
				Codec.FLOAT.optionalFieldOf("underlineThickness", 0.0F).forGetter(Metrics :: underlineThickness)
		).apply(instance, Metrics :: new));
	}
	
	record Glyph(int index,
	             Optional<Integer> unicode,
	             Optional<Integer> codepoint,
	             Optional<Integer> character,
	             float advance,
	             Optional<Bounds> planeBounds,
	             Optional<Bounds> atlasBounds)
	{
		private static final Codec<Glyph> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.optionalFieldOf("index", -1).forGetter(Glyph :: index),
				CODEPOINT_CODEC.optionalFieldOf("unicode").forGetter(Glyph :: unicode),
				CODEPOINT_CODEC.optionalFieldOf("codepoint").forGetter(Glyph :: codepoint),
				CODEPOINT_CODEC.optionalFieldOf("character").forGetter(Glyph :: character),
				Codec.FLOAT.optionalFieldOf("advance", 0.0F).forGetter(Glyph :: advance),
				Bounds.CODEC.optionalFieldOf("planeBounds").forGetter(Glyph :: planeBounds),
				Bounds.CODEC.optionalFieldOf("atlasBounds").forGetter(Glyph :: atlasBounds)
		).apply(instance, Glyph :: new));
		
		private Optional<Integer> resolvedCodepoint()
		{
			return this.unicode.or(() -> this.codepoint).or(() -> this.character);
		}
	}
	
	record Bounds(float left, float bottom, float right, float top)
	{
		private static final Codec<Bounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.FLOAT.fieldOf("left").forGetter(Bounds :: left),
				Codec.FLOAT.fieldOf("bottom").forGetter(Bounds :: bottom),
				Codec.FLOAT.fieldOf("right").forGetter(Bounds :: right),
				Codec.FLOAT.fieldOf("top").forGetter(Bounds :: top)
		).apply(instance, Bounds :: new));
	}
}
