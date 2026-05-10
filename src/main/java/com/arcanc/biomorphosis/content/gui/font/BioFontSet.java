/**
 * @author ArcAnc
 * Created at: 05.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.font;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.client.gui.font.*;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class BioFontSet extends FontSet
{
	private static final RandomSource RANDOM = RandomSource.create();
	private static final float LARGE_FORWARD_ADVANCE = 32.0F;
	
	private final TextureManager textureManager;
	private final ResourceLocation name;
	private BakedGlyph missingGlyph;
	private BakedGlyph whiteGlyph;
	private List<GlyphProvider.Conditional> allProviders = List.of();
	private List<GlyphProvider> activeProviders = List.of();
	private final CodepointMap<BakedGlyph> glyphs = new CodepointMap<>(BakedGlyph[] :: new, BakedGlyph[][] :: new);
	private final CodepointMap<GlyphInfoFilter> glyphInfos = new CodepointMap<>(GlyphInfoFilter[] :: new, GlyphInfoFilter[][] :: new);
	private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap<>();
	private final List<FontTexture> textures = Lists.newArrayList();
	
	public BioFontSet(TextureManager textureManager, ResourceLocation name)
	{
		super(textureManager, name);
		this.textureManager = textureManager;
		this.name = name;
	}
	
	@Override
	public void reload(List<GlyphProvider.Conditional> allProviders, Set<FontOption> options)
	{
		this.allProviders = allProviders;
		this.reload(options);
	}
	
	@Override
	public void reload(Set<FontOption> options)
	{
		this.activeProviders = List.of();
		this.resetTextures();
		this.activeProviders = this.selectProviders(this.allProviders, options);
	}
	
	private void resetTextures()
	{
		this.closeTextures();
		this.glyphs.clear();
		this.glyphInfos.clear();
		this.glyphsByWidth.clear();
		this.missingGlyph = SpecialGlyphs.MISSING.bake(this :: stitch);
		this.whiteGlyph = SpecialGlyphs.WHITE.bake(this :: stitch);
	}
	
	private List<GlyphProvider> selectProviders(List<GlyphProvider.Conditional> providers, Set<FontOption> options)
	{
		IntSet intSet = new IntOpenHashSet();
		List<GlyphProvider> selectedProviders = new ArrayList<>();
		
		for (GlyphProvider.Conditional conditional : providers)
		{
			if (conditional.filter().apply(options))
			{
				selectedProviders.add(conditional.provider());
				intSet.addAll(conditional.provider().getSupportedGlyphs());
			}
		}
		
		Set<GlyphProvider> usedProviders = Sets.newHashSet();
		intSet.forEach(codePoint ->
		{
			for (GlyphProvider glyphProvider : selectedProviders)
			{
				GlyphInfo glyphInfo = glyphProvider.getGlyph(codePoint);
				if (glyphInfo != null)
				{
					usedProviders.add(glyphProvider);
					if (glyphInfo != SpecialGlyphs.MISSING)
						this.glyphsByWidth.computeIfAbsent(Mth.ceil(glyphInfo.getAdvance(false)), ignored -> new IntArrayList()).add(codePoint);
					break;
				}
			}
		});
		return selectedProviders.stream().filter(usedProviders :: contains).toList();
	}
	
	@Override
	public void close()
	{
		this.closeTextures();
	}
	
	private void closeTextures()
	{
		for (FontTexture fontTexture : this.textures)
			fontTexture.close();
		this.textures.clear();
	}
	
	private static boolean hasFishyAdvance(GlyphInfo glyph)
	{
		float advance = glyph.getAdvance(false);
		if (advance < 0.0F || advance > LARGE_FORWARD_ADVANCE)
			return true;
		
		float boldAdvance = glyph.getAdvance(true);
		return boldAdvance < 0.0F || boldAdvance > LARGE_FORWARD_ADVANCE;
	}
	
	private GlyphInfoFilter computeGlyphInfo(int character)
	{
		GlyphInfo glyphInfo = null;
		
		for (GlyphProvider glyphProvider : this.activeProviders)
		{
			GlyphInfo providerGlyphInfo = glyphProvider.getGlyph(character);
			if (providerGlyphInfo != null)
			{
				if (glyphInfo == null)
					glyphInfo = providerGlyphInfo;
				
				if (!hasFishyAdvance(providerGlyphInfo))
					return new GlyphInfoFilter(glyphInfo, providerGlyphInfo);
			}
		}
		
		return glyphInfo != null ? new GlyphInfoFilter(glyphInfo, SpecialGlyphs.MISSING) : GlyphInfoFilter.MISSING;
	}
	
	@Override
	public GlyphInfo getGlyphInfo(int character, boolean filterFishyGlyphs)
	{
		return this.glyphInfos.computeIfAbsent(character, this :: computeGlyphInfo).select(filterFishyGlyphs);
	}
	
	private BakedGlyph computeBakedGlyph(int character)
	{
		for (GlyphProvider glyphProvider : this.activeProviders)
		{
			GlyphInfo glyphInfo = glyphProvider.getGlyph(character);
			if (glyphInfo instanceof BioGlyphInfo)
				return glyphInfo.bake(this :: stitch);
		}
		
		return this.missingGlyph;
	}
	
	@Override
	public BakedGlyph getGlyph(int character)
	{
		return this.glyphs.computeIfAbsent(character, this :: computeBakedGlyph);
	}
	
	private BakedGlyph stitch(SheetGlyphInfo glyphInfo)
	{
		return this.stitch(this.textures, glyphInfo, BioGlyphRenderTypes.BioRenderTypesProvider :: create);
	}
	
	private BakedGlyph stitch(List<FontTexture> textures,
	                          SheetGlyphInfo glyphInfo,
	                          BiFunction<ResourceLocation, Boolean, GlyphRenderTypes> renderTypesFactory)
	{
		for (FontTexture fontTexture : textures)
		{
			BakedGlyph bakedGlyph = fontTexture.add(glyphInfo);
			if (bakedGlyph != null)
				return new BioBakedGlyph(bakedGlyph);
		}
		
		ResourceLocation resourceLocation = this.name.withSuffix("/" + textures.size());
		boolean colored = glyphInfo.isColored();
		FontTexture fontTexture = new FontTexture(renderTypesFactory.apply(resourceLocation, colored), colored);
		textures.add(fontTexture);
		this.textureManager.register(resourceLocation, fontTexture);
		BakedGlyph bakedGlyph = fontTexture.add(glyphInfo);
		if (bakedGlyph == null)
			return this.missingGlyph;
		return new BioBakedGlyph(bakedGlyph);
	}
	
	@Override
	public BakedGlyph getRandomGlyph(GlyphInfo glyph)
	{
		IntList glyphs = this.glyphsByWidth.get(Mth.ceil(glyph.getAdvance(false)));
		return glyphs != null && !glyphs.isEmpty() ? this.getGlyph(glyphs.getInt(RANDOM.nextInt(glyphs.size()))) : this.missingGlyph;
	}
	
	@Override
	public ResourceLocation name()
	{
		return this.name;
	}
	
	@Override
	public BakedGlyph whiteGlyph()
	{
		return this.whiteGlyph;
	}
	
	@OnlyIn(Dist.CLIENT)
	private record GlyphInfoFilter(GlyphInfo glyphInfo, GlyphInfo glyphInfoNotFishy)
	{
		private static final GlyphInfoFilter MISSING = new GlyphInfoFilter(SpecialGlyphs.MISSING, SpecialGlyphs.MISSING);
		
		private GlyphInfo select(boolean filterFishyGlyphs)
		{
			return filterFishyGlyphs ? this.glyphInfoNotFishy : this.glyphInfo;
		}
	}
}
