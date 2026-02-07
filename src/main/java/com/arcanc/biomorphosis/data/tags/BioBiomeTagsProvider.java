/**
 * @author ArcAnc
 * Created at: 05.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags;


import com.arcanc.biomorphosis.data.tags.base.BioBiomesTags;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BioBiomeTagsProvider extends BiomeTagsProvider
{
	public BioBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider)
	{
		super(output, provider, Database.MOD_ID);
	}
	
	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider)
	{
		this.tag(BioBiomesTags.HAS_SWARM_VILLAGE).
				addTag(BiomeTags.IS_FOREST).
				addTag(BiomeTags.IS_JUNGLE).
				addTag(BiomeTags.IS_SAVANNA).
				addTag(BiomeTags.HAS_VILLAGE_PLAINS);
		
		this.tag(BioBiomesTags.HAS_SRF_HEADQUARTERS).
				addTag(BiomeTags.IS_FOREST).
				addTag(BiomeTags.IS_JUNGLE).
				addTag(BiomeTags.IS_SAVANNA).
				addTag(BiomeTags.HAS_VILLAGE_PLAINS);
		
		this.tag(BioBiomesTags.HAS_SRF_TOWER).
				addTag(BiomeTags.IS_FOREST).
				addTag(BiomeTags.IS_JUNGLE).
				addTag(BiomeTags.IS_SAVANNA).
				addTag(BiomeTags.HAS_VILLAGE_PLAINS);
	}
}
