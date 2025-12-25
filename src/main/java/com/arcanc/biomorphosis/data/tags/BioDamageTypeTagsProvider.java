/**
 * @author ArcAnc
 * Created at: 21.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BioDamageTypeTagsProvider extends DamageTypeTagsProvider
{
	public BioDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
	{
		super(output, lookupProvider, Database.MOD_ID);
	}
	
	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider)
	{
		this.tag(Tags.DamageTypes.IS_PHYSICAL).
				add(Registration.DamageTypeReg.TURRET_DAMAGE);
		this.tag(Tags.DamageTypes.IS_ENVIRONMENT).
				add(Registration.DamageTypeReg.TURRET_DAMAGE);
		this.tag(DamageTypeTags.NO_KNOCKBACK).
				add(Registration.DamageTypeReg.TURRET_DAMAGE);
		
	}
}
