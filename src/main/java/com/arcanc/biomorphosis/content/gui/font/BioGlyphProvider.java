/**
 * @author ArcAnc
 * Created at: 25.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.font;


import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.enumextensions.GlyphProviderTypeExtension;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class BioGlyphProvider implements GlyphProvider
{
	private final NativeImage image;
	private final CodepointMap<GlyphInfo> glyphs;
	
	BioGlyphProvider(NativeImage image, CodepointMap<GlyphInfo> glyphs)
	{
		this.image = image;
		this.glyphs = glyphs;
	}
	
	@Override
	public void close()
	{
		this.image.close();
	}
	
	@Nullable
	@Override
	public GlyphInfo getGlyph(int character)
	{
		return this.glyphs.get(character);
	}
	
	@Override
	public IntSet getSupportedGlyphs()
	{
		return IntSets.unmodifiable(this.glyphs.keySet());
	}
	
	@OnlyIn (Dist.CLIENT)
	public record Definition(ResourceLocation file,
	                         int height,
	                         Optional<Integer> ascent) implements GlyphProviderDefinition
	{
		public static final MapCodec<Definition> CODEC = RecordCodecBuilder.<Definition>mapCodec(
						instance -> instance.group(
										ResourceLocation.CODEC.fieldOf("file").forGetter(Definition :: file),
										Codec.INT.optionalFieldOf("height", 8).forGetter(Definition :: height),
										Codec.INT.optionalFieldOf("ascent").forGetter(Definition :: ascent)
								)
								.apply(instance, Definition::new)
				)
				.validate(Definition :: validate);
		
		private static DataResult<BioGlyphProvider.Definition> validate(BioGlyphProvider.Definition definition)
		{
			if (definition.ascent.isPresent() && definition.ascent.get() > definition.height)
				return DataResult.error(() -> "Ascent " + definition.ascent.get() + " higher than height " + definition.height);
			return DataResult.success(definition);
		}
		
		@Override
		public GlyphProviderType type()
		{
			return GlyphProviderTypeExtension.BIO_BITMAP.getValue();
		}
		
		@Override
		public Either<Loader, Reference> unpack()
		{
			return Either.left(this :: load);
		}
		
		private BioGlyphProvider load(ResourceManager resourceManager) throws IOException
		{
			ResourceLocation resourcelocation = this.file.withPrefix("textures/");
			
			BioGlyphProvider glyphProvider;
			try (InputStream inputstream = resourceManager.open(resourcelocation))
			{
				NativeImage nativeimage = NativeImage.read(NativeImage.Format.RGBA, inputstream);
				CodepointMap<GlyphInfo> codepointmap = new CodepointMap<>(GlyphInfo[] :: new, GlyphInfo[][] :: new);
				BioMsdfAtlas msdfAtlas = BioMsdfAtlas.load(resourceManager, resourcelocation);
				
				this.loadMsdfGlyphs(msdfAtlas, nativeimage, codepointmap, resourcelocation);
				this.loadMissingWhitespace(msdfAtlas, codepointmap);
				
				glyphProvider = new BioGlyphProvider(nativeimage, codepointmap);
			}
			
			return glyphProvider;
		}
		
		private void loadMsdfGlyphs(BioMsdfAtlas atlas,
		                            NativeImage image,
		                            CodepointMap<GlyphInfo> glyphs,
		                            ResourceLocation textureLocation)
		{
			atlas.supportedCodepoints().forEach(codepoint ->
			{
				Optional<BioMsdfAtlas.Glyph> glyph = atlas.glyph(codepoint);
				if (glyph.isEmpty())
				{
					Database.LOGGER.warn("Codepoint '{}' was indexed in {}, but missing in {}", Integer.toHexString(codepoint), textureLocation, atlas.source());
					return;
				}
				
				GlyphInfo glyphInfo = glyphs.put(codepoint, atlas.toGlyphInfo(image, glyph.get(), this.height));
				if (glyphInfo != null)
					Database.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(codepoint), textureLocation);
			});
		}
		
		private void loadMissingWhitespace(BioMsdfAtlas atlas, CodepointMap<GlyphInfo> glyphs)
		{
			if (glyphs.get(atlas.spaceCodepoint()) == null)
				glyphs.put(atlas.spaceCodepoint(), atlas.spaceGlyphInfo(this.height));
		}
	}
}
