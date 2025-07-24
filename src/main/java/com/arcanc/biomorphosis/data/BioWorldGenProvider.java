/**
 * @author ArcAnc
 * Created at: 22.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.worldgen.biome.BioBiomes;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.NotNull;

public class BioWorldGenProvider extends BioRegistryData
{
	@Override
	protected void addContent()
	{
	}

	@Override
	protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
	{
		registrySetBuilder.add(Registries.BIOME, BioBiomes :: bootstrap);
	}
}
