/**
 * @author ArcAnc
 * Created at: 25.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.gui.font;


import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.Nullable;

public class MetaGlyphProvider implements GlyphProvider
{
	
	@Override
	public void close()
	{
		GlyphProvider.super.close();
	}
	
	@Override
	public @Nullable GlyphInfo getGlyph(int character)
	{
		return GlyphProvider.super.getGlyph(character);
	}
	
	@Override
	public IntSet getSupportedGlyphs()
	{
		return null;
	}
}
