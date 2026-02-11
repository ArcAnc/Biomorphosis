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
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.mutations.UnlockedGenome;
import com.arcanc.biomorphosis.content.mutations.templates.GenomeDataDefinition;
import com.arcanc.biomorphosis.content.mutations.templates.GenomeTemplate;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenomeHelper
{
	public static <T extends LivingEntity> @NotNull GenomeTemplate getTemplateByEntity(@NotNull T entity)
	{
		return entity.
				registryAccess().
				lookupOrThrow(Registration.GenomeReg.GENOME_TEMPLATES_KEY).
				getOrThrow(ResourceKey.create(Registration.GenomeReg.GENOME_TEMPLATES_KEY, EntityType.getKey(entity.getType()))).
				value();
	}
	
	public static UnlockedGenome getUnlockedGenome(@NotNull Player player)
	{
		UnlockedGenome instance;
		if (player.hasData(Registration.DataAttachmentsReg.UNLOCKED_GENOME))
			instance = player.getData(Registration.DataAttachmentsReg.UNLOCKED_GENOME);
		else
			instance = UnlockedGenome.EMPTY;
		return instance;
	}
	
	public static int calculateStability(LivingEntity entity)
	{
		return calculateStability(getGenome(entity), entity.level());
	}
	
	public static int calculateStability(GenomeInstance genome, Level level)
	{
		if (genome == null || genome.isEmpty())
			return 0;
		
		HolderLookup.RegistryLookup<GeneDefinition> registry = level.registryAccess().
				lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY);
		int result = 0;
		for (GeneInstance geneInstance : genome.geneInstances())
		{
			GeneDefinition def = registry.getOrThrow(ResourceKey.create(Registration.GenomeReg.DEFINITION_KEY, geneInstance.id())).value();
			if (def == null)
				continue;
			GeneDefinition.RarityData data = def.rarityData().get(geneInstance.rarity());
			if (data == null)
				continue;
			
			result -= data.destabilizationAmount();
		}
		return result;
	}
	
	public static <T extends LivingEntity> @NotNull GenomeInstance getGenome(@NotNull T entity)
	{
		GenomeInstance instance;
		if (entity.hasData(Registration.DataAttachmentsReg.GENOME))
			instance = entity.getData(Registration.DataAttachmentsReg.GENOME);
		else
		{
			Level level = entity.level();
			
			GenomeTemplate template = getTemplateByEntity(entity);
			if (template == null)
				instance = GenomeInstance.EMPTY;
			else
				instance = template.
									genomes().
									getRandom(level.getRandom()).
									map(GenomeDataDefinition :: genomeData).
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
