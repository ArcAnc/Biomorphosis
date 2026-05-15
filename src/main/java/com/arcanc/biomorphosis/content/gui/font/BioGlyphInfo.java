/**
 * @author ArcAnc
 * Created at: 25.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.font;


import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;

import java.util.function.Function;

public record BioGlyphInfo(float oversample,
                           NativeImage image,
                           int offsetX,
                           int offsetY,
                           int width,
                           int height,
                           float advance,
                           float bearingLeft,
                           float bearingTop) implements GlyphInfo
{
	public static BioGlyphInfo bitmap(float scale,
	                                  NativeImage image,
	                                  int offsetX,
	                                  int offsetY,
	                                  int width,
	                                  int height,
	                                  float advance,
	                                  float bearingLeft,
	                                  float bearingTop)
	{
		return new BioGlyphInfo(1.0F / scale, image, offsetX, offsetY, width, height, advance, bearingLeft, bearingTop);
	}
	
	public static GlyphInfo empty(float advance)
	{
		return new Empty(advance);
	}
	
	@Override
	public float getAdvance()
	{
		return this.advance;
	}
	
	@Override
	public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> glyphProvider)
	{
		return glyphProvider.apply(new SheetGlyphInfo()
		{
			@Override
			public float getOversample() {
				return BioGlyphInfo.this.oversample;
			}
			
			@Override
			public int getPixelWidth() {
				return BioGlyphInfo.this.width;
			}
			
			@Override
			public int getPixelHeight() {
				return BioGlyphInfo.this.height;
			}
			
			@Override
			public float getBearingTop() {
				return BioGlyphInfo.this.bearingTop;
			}
			
			@Override
			public float getBearingLeft() {
				return BioGlyphInfo.this.bearingLeft;
			}
			
			@Override
			public void upload(int xOffset, int yOffset)
			{
				BioGlyphInfo.this.image.upload(0,
						xOffset,
						yOffset,
						BioGlyphInfo.this.offsetX,
						BioGlyphInfo.this.offsetY,
						BioGlyphInfo.this.width,
						BioGlyphInfo.this.height,
						false,
						false);
			}
			
			@Override
			public boolean isColored() {
				return BioGlyphInfo.this.image.format().components() > 1;
			}
		});
	}
	
	private record Empty(float advance) implements GlyphInfo
	{
		@Override
		public float getAdvance()
		{
			return this.advance;
		}
		
		@Override
		public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> glyphProvider)
		{
			return glyphProvider.apply(new SheetGlyphInfo()
			{
				@Override
				public float getOversample()
				{
					return 1.0F;
				}
				
				@Override
				public int getPixelWidth()
				{
					return 1;
				}
				
				@Override
				public int getPixelHeight()
				{
					return 1;
				}
				
				@Override
				public float getBearingTop()
				{
					return 0.0F;
				}
				
				@Override
				public void upload(int xOffset, int yOffset)
				{
				}
				
				@Override
				public boolean isColored()
				{
					return false;
				}
			});
		}
	}
}
