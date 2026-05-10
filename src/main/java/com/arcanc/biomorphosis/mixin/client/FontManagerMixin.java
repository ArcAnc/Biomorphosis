/**
 * @author ArcAnc
 * Created at: 05.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin.client;


import com.arcanc.biomorphosis.content.gui.font.BioFontSet;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(FontManager.class)
public class FontManagerMixin
{
	@Shadow
	@Final
	private Map<ResourceLocation, FontSet> fontSets;
	
	@Shadow
	@Final
	private TextureManager textureManager;
	
	@Inject(method = "apply", at = @At("TAIL"))
	private void createBioFontSet(FontManager.Preparation preparation, ProfilerFiller profiler, CallbackInfo ci, @Local Set<FontOption> set)
	{
		preparation.fontSets().forEach((resourceLocation, conditionals) ->
		{
			if (resourceLocation.getNamespace().equals(Database.MOD_ID))
			{
				BioFontSet fontSet = new BioFontSet(this.textureManager, resourceLocation);
				fontSet.reload(Lists.reverse(conditionals), set);
				this.fontSets.put(resourceLocation, fontSet);
			}
		});
	}
}
