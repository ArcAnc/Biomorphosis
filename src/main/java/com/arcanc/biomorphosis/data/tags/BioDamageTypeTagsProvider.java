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
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BioDamageTypeTagsProvider extends DamageTypeTagsProvider
{
	public BioDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
	{
		super(output, lookupProvider, Database.MOD_ID, existingFileHelper);
	}
	
	@Override
	protected void addTags(HolderLookup.Provider provider)
	{
		this.tag(Tags.DamageTypes.IS_PHYSICAL).
				add(Registration.DamageTypeReg.TURRET_DAMAGE);
		this.tag(Tags.DamageTypes.IS_ENVIRONMENT).
				add(Registration.DamageTypeReg.TURRET_DAMAGE);
		this.tag(DamageTypeTags.NO_KNOCKBACK).
				add(Registration.DamageTypeReg.TURRET_DAMAGE);
		
		
		this.tag(DamageTypeTags.BYPASSES_ARMOR).
				add(Registration.DamageTypeReg.ACID).
				add(Registration.DamageTypeReg.IMPOSSIBLE_MUTATION).
				add(Registration.DamageTypeReg.INFESTATION);
		this.tag(DamageTypeTags.BYPASSES_COOLDOWN).
				add(Registration.DamageTypeReg.ACID);
		this.tag(DamageTypeTags.BYPASSES_INVULNERABILITY).
				add(Registration.DamageTypeReg.IMPOSSIBLE_MUTATION);
		this.tag(DamageTypeTags.BYPASSES_RESISTANCE).
				add(Registration.DamageTypeReg.IMPOSSIBLE_MUTATION).
				add(Registration.DamageTypeReg.INFESTATION);
		this.tag(DamageTypeTags.NO_KNOCKBACK).
				add(Registration.DamageTypeReg.ACID).
				add(Registration.DamageTypeReg.IMPOSSIBLE_MUTATION).
				add(Registration.DamageTypeReg.INFESTATION);
	}
}
