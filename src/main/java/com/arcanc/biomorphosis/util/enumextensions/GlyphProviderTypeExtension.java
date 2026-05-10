/**
 * @author ArcAnc
 * Created at: 04.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.enumextensions;


import com.arcanc.biomorphosis.content.gui.font.BioGlyphProvider;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class GlyphProviderTypeExtension
{
	public static final EnumProxy<GlyphProviderType> BIO_BITMAP = new EnumProxy<>(GlyphProviderType.class,
			Database.rlStr("bio_bitmap"),
			BioGlyphProvider.Definition.CODEC);
}
