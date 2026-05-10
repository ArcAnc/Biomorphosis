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

public record BioGlyphInfo(float scale,
                           NativeImage image,
                           int offsetX,
                           int offsetY,
                           int width,
                           int height,
                           int advance,
                           int ascent) implements GlyphInfo
{
	@Override
	public float getAdvance()
	{
		return (float)this.advance;
	}
	
	@Override
	public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> glyphProvider)
	{
		return glyphProvider.apply(new SheetGlyphInfo()
		{
			@Override
			public float getOversample() {
				return 1.0F / BioGlyphInfo.this.scale;
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
				return (float) BioGlyphInfo.this.ascent;
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
}