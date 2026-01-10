/**
 * @author ArcAnc
 * Created at: 07.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;


import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.mutations.templates.GenomeDataDefinition;
import com.arcanc.biomorphosis.content.mutations.templates.GenomeTemplate;
import com.arcanc.biomorphosis.content.mutations.types.IGeneEffectType;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CGenomeSync;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenomeHelper
{
	public static <T extends LivingEntity> @Nullable GenomeTemplate getTemplateByEntity(@NotNull T entity)
	{
		return entity.
				registryAccess().
				lookupOrThrow(Registration.GenomeReg.GENOME_TEMPLATES_KEY).
				getValue(EntityType.getKey(entity.getType()));
	}
	
	public static <T extends LivingEntity> @NotNull GenomeInstance getGenome(@NotNull T entity)
	{
		GenomeInstance instance;
		if (entity.hasData(Registration.DataAttachmentsReg.GENOME))
			instance = entity.getData(Registration.DataAttachmentsReg.GENOME);
		else
		{
			Level level = entity.level();
			
			instance = entity.
					registryAccess().
					lookupOrThrow(Registration.GenomeReg.GENOME_TEMPLATES_KEY).
					getOptional(EntityType.getKey(entity.getType())).
					flatMap(genomeTemplate ->
							genomeTemplate.
									genomes().
									getRandom(level.getRandom()).
							map(GenomeDataDefinition :: genomeData)).
					orElse(GenomeInstance.EMPTY);
			
			entity.setData(Registration.DataAttachmentsReg.GENOME, instance);
		}
		return instance;
	}
	
	public static @NotNull List<Object> getAllEffectData(GeneDefinition.@NotNull GeneEffectEntry entry)
	{
		List<Object> list = new ArrayList<>();
		
		Map<String, Dynamic<?>> dataMap = entry.params().rawData().
				asMap(
				dynamic -> dynamic.asString(""),
				dynamic -> dynamic);
		
		for (Map.Entry<String, Dynamic<?>> stringDynamicEntry : dataMap.entrySet())
		{
			Dynamic<?> dynamic = stringDynamicEntry.getValue();
			if (dynamic.asNumber().isSuccess())
				list.add(dynamic.asNumber().getOrThrow());
			else if (dynamic.asBoolean().isSuccess())
				list.add(dynamic.asBoolean().getOrThrow());
			else if (dynamic.asString().isSuccess())
				list.add(dynamic.asString().getOrThrow());
		}
		
		return list;
	}
}
