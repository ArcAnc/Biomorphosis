/**
 * @author ArcAnc
 * Created at: 21.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags.base;


import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

public class BioDamageTypeTags
{
	private static @NotNull TagKey<DamageType> create(String name)
	{
		return TagKey.create(Registries.DAMAGE_TYPE, Database.rl(name));
	}
}
