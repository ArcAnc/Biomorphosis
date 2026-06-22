/**
 * @author ArcAnc
 * Created at: 23.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations;


import net.minecraft.nbt.CompoundTag;

import java.util.List;

public interface GenomeEffectsHolder
{
	List<GeneEffectInstance> biomorphosis$getGeneEffects();
	CompoundTag biomorphosis$getGeneEffectData();
	void biomorphosis$setGeneEffectData(CompoundTag data);
	void biomorphosis$rebuildEffects();

	record GeneEffectInstance(String key, GeneDefinition.GeneEffectEntry entry, CompoundTag data)
	{
	}
}
