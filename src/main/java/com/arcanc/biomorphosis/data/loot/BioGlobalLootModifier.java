/**
 * @author ArcAnc
 * Created at: 14.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.loot;


import com.arcanc.biomorphosis.data.loot.modifiers.FleshLootModifier;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class BioGlobalLootModifier extends GlobalLootModifierProvider
{
	public BioGlobalLootModifier(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
	{
		super(output, registries, Database.MOD_ID);
	}
	
	@Override
	protected void start()
	{
		this.add("flesh_modifier",
				new FleshLootModifier(new LootItemCondition[]{}));
	}
}
