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
import java.util.ArrayList;
import java.util.List;

public class BioGlyphProvider implements GlyphProvider
{
	private final NativeImage image;
	private final CodepointMap<BioGlyphInfo> glyphs;
	
	BioGlyphProvider(NativeImage image, CodepointMap<BioGlyphInfo> glyphs)
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
	public record Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid) implements GlyphProviderDefinition
	{
		private static final Codec<int[][]> CODEPOINT_GRID_CODEC = Codec.STRING.listOf().xmap(list ->
		{
			int i = list.size();
			int[][] aint = new int[i][];
			
			for (int j = 0; j < i; j++)
				aint[j] = list.get(j).codePoints().toArray();
			
			return aint;
		}, array ->
		{
			List<String> list = new ArrayList<>(array.length);
			
			for (int[] aint : array)
				list.add(new String(aint, 0, aint.length));
			
			return list;
		}).validate(Definition :: validateDimensions);
		
		public static final MapCodec<Definition> CODEC = RecordCodecBuilder.<Definition>mapCodec(
						instance -> instance.group(
										ResourceLocation.CODEC.fieldOf("file").forGetter(Definition :: file),
										Codec.INT.optionalFieldOf("height", 8).forGetter(Definition :: height),
										Codec.INT.fieldOf("ascent").forGetter(Definition :: ascent),
										CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(Definition :: codepointGrid)
								)
								.apply(instance, Definition::new)
				)
				.validate(Definition :: validate);
		
		private static DataResult<int[][]> validateDimensions(int[][] dimensions)
		{
			int i = dimensions.length;
			if (i == 0) {
				return DataResult.error(() -> "Expected to find data in codepoint grid");
			} else {
				int[] aint = dimensions[0];
				int j = aint.length;
				if (j == 0) {
					return DataResult.error(() -> "Expected to find data in codepoint grid");
				} else {
					for (int k = 1; k < i; k++) {
						int[] aint1 = dimensions[k];
						if (aint1.length != j) {
							return DataResult.error(
									() -> "Lines in codepoint grid have to be the same length (found: "
											+ aint1.length
											+ " codepoints, expected: "
											+ j
											+ "), pad with \\u0000"
							);
						}
					}
					
					return DataResult.success(dimensions);
				}
			}
		}
		
		private static DataResult<BioGlyphProvider.Definition> validate(BioGlyphProvider.Definition definition)
		{
			return definition.ascent > definition.height
					? DataResult.error(() -> "Ascent " + definition.ascent + " higher than height " + definition.height)
					: DataResult.success(definition);
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
				int i = nativeimage.getWidth();
				int j = nativeimage.getHeight();
				int k = i / this.codepointGrid[0].length;
				int l = j / this.codepointGrid.length;
				float f = (float)this.height / (float)l;
				CodepointMap<BioGlyphInfo> codepointmap = new CodepointMap<>(BioGlyphInfo[] :: new, BioGlyphInfo[][] :: new);
				
				for (int i1 = 0; i1 < this.codepointGrid.length; i1++)
				{
					int j1 = 0;
					
					for (int k1 : this.codepointGrid[i1])
					{
						int l1 = j1++;
						if (k1 != 0) {
							int i2 = this.getActualGlyphWidth(nativeimage, k, l, l1, i1);
							BioGlyphInfo glyphInfo = codepointmap.put(
									k1, new BioGlyphInfo(f, nativeimage, l1 * k, i1 * l, k, l, (int)(0.5 + (double)((float)i2 * f)) + 1, this.ascent)
							);
							if (glyphInfo != null)
								Database.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(k1), resourcelocation);
						}
					}
				}
				
				glyphProvider = new BioGlyphProvider(nativeimage, codepointmap);
			}
			
			return glyphProvider;
		}
		
		private int getActualGlyphWidth(NativeImage image, int width, int height, int x, int y)
		{
			int i;
			for (i = width - 1; i >= 0; i--)
			{
				int j = x * width + i;
				
				for (int k = 0; k < height; k++)
				{
					int l = y * height + k;
					if (image.getLuminanceOrAlpha(j, l) != 0)
						return i + 1;
				}
			}
			
			return i + 1;
		}
	}
}
