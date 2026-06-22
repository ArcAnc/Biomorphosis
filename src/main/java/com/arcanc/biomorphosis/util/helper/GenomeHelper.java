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
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GenomeHelper
{
	public static <T extends LivingEntity> @Nullable GenomeTemplate getTemplateByEntity(T entity)
	{
		return entity.
				registryAccess().
				lookupOrThrow(Registration.GenomeReg.GENOME_TEMPLATES_KEY).
				get(ResourceKey.create(Registration.GenomeReg.GENOME_TEMPLATES_KEY, EntityType.getKey(entity.getType()))).
				map(Holder.Reference :: value).orElse(null);
	}

	public static UnlockedGenome getUnlockedGenome(Player player)
	{
		UnlockedGenome instance;
		if (player.hasData(Registration.DataAttachmentsReg.UNLOCKED_GENOME))
			instance = player.getData(Registration.DataAttachmentsReg.UNLOCKED_GENOME);
		else
			instance = UnlockedGenome.empty();
		return instance;
	}

	public static int calculateStability(LivingEntity entity)
	{
		return calculateStability(getGenome(entity), entity.level());
	}

	public static int calculateStability(GenomeInstance genome, Level level)
	{
		return validateGenome(genome, level, null, false).stability();
	}

	public static <T extends LivingEntity> GenomeInstance getGenome(T entity)
	{
		GenomeInstance instance;
		if (entity.hasData(Registration.DataAttachmentsReg.GENOME))
			instance = entity.getData(Registration.DataAttachmentsReg.GENOME);
		else
		{
			Level level = entity.level();

			GenomeTemplate template = getTemplateByEntity(entity);
			if (template == null)
				instance = GenomeInstance.empty();
			else
				instance = template.
									genomes().
									getRandom(level.getRandom()).
									map(GenomeDataDefinition :: genomeData).
									orElseGet(GenomeInstance :: empty);

			entity.setData(Registration.DataAttachmentsReg.GENOME, instance);
		}
		return instance;
	}

	public static <T extends LivingEntity> boolean hasArmorGene(T entity, EquipmentSlot slot)
	{
		return OrganicArmorHelper.hasArmor(entity, slot);
	}

	public static boolean hasArmorGene(GenomeInstance instance, EquipmentSlot slot)
	{
		return false;
	}

	public static List<Object> getAllEffectData(GeneDefinition.GeneEffectEntry entry)
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

	public static GenomeValidationResult validateGenome(GenomeInstance genome, Level level)
	{
		return validateGenome(genome, level, null, true);
	}

	public static GenomeValidationResult validateGenome(GenomeInstance genome, Level level, @Nullable UnlockedGenome unlockedGenome)
	{
		return validateGenome(genome, level, unlockedGenome, true);
	}

	public static GenomeValidationResult validateGenome(GenomeInstance genome, Level level, @Nullable UnlockedGenome unlockedGenome, boolean checkStability)
	{
		List<GenomeValidationError> errors = new ArrayList<>();
		int stability = 0;

		if (genome == null || genome.isEmpty())
			return new GenomeValidationResult(List.of(), 0);

		HolderLookup.RegistryLookup<GeneDefinition> registry = level.registryAccess().
				lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY);
		Map<GeneInstance, GeneDefinition.RarityData> rarityData = new HashMap<>();
		Map<ResourceLocation, GeneInstance> genesById = new HashMap<>();

		for (GeneInstance gene : genome.geneInstances())
		{
			GeneInstance existing = genesById.putIfAbsent(gene.id(), gene);
			if (existing != null)
				errors.add(new GenomeValidationError(GenomeValidationErrorType.DUPLICATE_GENE, gene, existing));

			Optional<Holder.Reference<GeneDefinition>> optionalDefinition = registry.
					get(ResourceKey.create(Registration.GenomeReg.DEFINITION_KEY, gene.id()));
			if (optionalDefinition.isEmpty())
			{
				errors.add(new GenomeValidationError(GenomeValidationErrorType.UNKNOWN_DEFINITION, gene, null));
				continue;
			}

			GeneDefinition definition = optionalDefinition.get().value();
			GeneDefinition.RarityData data = definition.rarityData().get(gene.rarity());
			if (data == null)
			{
				errors.add(new GenomeValidationError(GenomeValidationErrorType.UNKNOWN_RARITY, gene, null));
				continue;
			}

			if (unlockedGenome != null && !unlockedGenome.getRaritiesById(gene.id()).contains(gene.rarity()))
				errors.add(new GenomeValidationError(GenomeValidationErrorType.LOCKED_GENE, gene, null));

			rarityData.put(gene, data);
			stability -= data.destabilizationAmount();
		}

		List<GeneInstance> genes = genome.geneInstances();
		for (int q = 0; q < genes.size(); q++)
			for (int w = q + 1; w < genes.size(); w++)
			{
				GeneInstance first = genes.get(q);
				GeneInstance second = genes.get(w);
				GeneDefinition.RarityData firstData = rarityData.get(first);
				GeneDefinition.RarityData secondData = rarityData.get(second);

				if (firstData != null && firstData.incompatibilities().contains(second.id()))
					errors.add(new GenomeValidationError(GenomeValidationErrorType.INCOMPATIBLE_GENES, first, second));
				if (secondData != null && secondData.incompatibilities().contains(first.id()))
					errors.add(new GenomeValidationError(GenomeValidationErrorType.INCOMPATIBLE_GENES, second, first));
			}

		if (checkStability && stability < 0)
			errors.add(new GenomeValidationError(GenomeValidationErrorType.LOW_STABILITY, null, null));

		return new GenomeValidationResult(List.copyOf(errors), stability);
	}

	public static GenomeValidationResult validateMutation(Player player, GenomeInstance toGenome)
	{
		return validateMutation(player, getGenome(player), toGenome);
	}

	public static GenomeValidationResult validateMutation(Player player, GenomeInstance fromGenome, GenomeInstance toGenome)
	{
		List<GenomeValidationError> errors = new ArrayList<>();
		UnlockedGenome unlockedGenome = getUnlockedGenome(player);
		GenomeValidationResult result = validateGenome(toGenome, player.level(), null, true);
		errors.addAll(result.errors());

		for (GeneInstance gene : toGenome.geneInstances())
		{
			boolean alreadyOwned = fromGenome != null && fromGenome.geneInstances().contains(gene);
			boolean unlocked = unlockedGenome.getRaritiesById(gene.id()).contains(gene.rarity());
			if (!alreadyOwned && !unlocked)
				errors.add(new GenomeValidationError(GenomeValidationErrorType.LOCKED_GENE, gene, null));
		}

		return new GenomeValidationResult(List.copyOf(errors), result.stability());
	}

	public static GenomeValidationResult validateGeneCanBeAdded(GenomeInstance genome, GeneInstance gene, Level level, @Nullable UnlockedGenome unlockedGenome)
	{
		List<GenomeValidationError> errors = new ArrayList<>();
		GenomeValidationResult geneResult = validateGenome(new GenomeInstance(List.of(gene)), level, unlockedGenome, false);
		errors.addAll(geneResult.errors());

		for (GeneInstance checkGene : genome.geneInstances())
			if (gene.id().equals(checkGene.id()) && gene.rarity().ordinal() <= checkGene.rarity().ordinal())
				errors.add(new GenomeValidationError(GenomeValidationErrorType.MORE_POWERFUL_GENE, checkGene, gene));

		if (!errors.isEmpty())
			return new GenomeValidationResult(List.copyOf(errors), validateGenome(genome, level, null, false).stability());

		GenomeInstance withGene = addOrReplaceGene(genome, gene);
		return validateGenome(withGene, level, null, false);
	}

	public static GenomeInstance addOrReplaceGene(GenomeInstance genome, GeneInstance gene)
	{
		List<GeneInstance> genes = new ArrayList<>(genome.geneInstances());
		genes.removeIf(checkGene -> checkGene.id().equals(gene.id()));
		genes.add(gene);
		return new GenomeInstance(genes);
	}

	public static GenomeInstance removeGene(GenomeInstance genome, GeneInstance gene)
	{
		List<GeneInstance> genes = new ArrayList<>(genome.geneInstances());
		genes.remove(gene);
		return new GenomeInstance(genes);
	}

	public record GenomeValidationResult(List<GenomeValidationError> errors, int stability)
	{
		public boolean valid()
		{
			return this.errors.isEmpty();
		}

		public Optional<GenomeValidationError> firstError()
		{
			return this.errors.stream().findFirst();
		}
	}

	public record GenomeValidationError(GenomeValidationErrorType type, @Nullable GeneInstance gene, @Nullable GeneInstance otherGene)
	{
	}

	public enum GenomeValidationErrorType
	{
		UNKNOWN_DEFINITION,
		UNKNOWN_RARITY,
		LOCKED_GENE,
		INCOMPATIBLE_GENES,
		DUPLICATE_GENE,
		MORE_POWERFUL_GENE,
		LOW_STABILITY
	}
}
