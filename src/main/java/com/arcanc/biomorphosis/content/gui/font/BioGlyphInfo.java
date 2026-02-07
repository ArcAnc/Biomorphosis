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
import net.minecraft.client.gui.font.glyphs.BakedGlyph;

import java.util.function.Function;

public class BioGlyphInfo implements GlyphInfo
{
	
	@Override
	public float getAdvance()
	{
		return 0;
	}
	
	@Override
	public float getAdvance(boolean bold)
	{
		return GlyphInfo.super.getAdvance(bold);
	}
	
	@Override
	public float getBoldOffset()
	{
		return GlyphInfo.super.getBoldOffset();
	}
	
	@Override
	public float getShadowOffset()
	{
		return GlyphInfo.super.getShadowOffset();
	}
	
	@Override
	public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> glyphProvider)
	{
		return null;
	}
}
