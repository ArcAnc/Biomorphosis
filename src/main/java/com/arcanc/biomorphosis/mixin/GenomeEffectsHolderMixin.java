/**
 * @author ArcAnc
 * Created at: 23.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;


import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeEffectsHolder;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public class GenomeEffectsHolderMixin implements GenomeEffectsHolder
{
	@Unique
	private static final String BIOMORPHOSIS_GENE_EFFECT_DATA = "biomorphosis_gene_effect_data";

	@Unique
	private List<GenomeEffectsHolder.GeneEffectInstance> biomorphosis$geneEffects = List.of();

	@Unique
	private final Map<String, CompoundTag> biomorphosis$pendingGeneEffectData = new LinkedHashMap<>();

	@Override
	public List<GenomeEffectsHolder.GeneEffectInstance> biomorphosis$getGeneEffects()
	{
		return this.biomorphosis$geneEffects;
	}

	@Override
	public CompoundTag biomorphosis$getGeneEffectData()
	{
		return this.biomorphosis$saveGeneEffectData();
	}

	@Override
	public void biomorphosis$setGeneEffectData(CompoundTag data)
	{
		this.biomorphosis$pendingGeneEffectData.clear();
		for (String key : data.getAllKeys())
			this.biomorphosis$pendingGeneEffectData.put(key, data.getCompound(key).copy());
	}

	@Override
	public void biomorphosis$rebuildEffects()
	{
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!(entity.level() instanceof ServerLevel serverLevel))
			return;

		Map<String, CompoundTag> preservedData = new LinkedHashMap<>(this.biomorphosis$pendingGeneEffectData);
		this.biomorphosis$pendingGeneEffectData.clear();

		if (!biomorphosis$getGeneEffects().isEmpty())
			biomorphosis$getGeneEffects().forEach(effect ->
			{
				effect.entry().type().saveData(entity, effect.entry().params(), effect.data());
				preservedData.put(effect.key(), effect.data().copy());
				effect.entry().type().remove(entity, effect.entry().params(), effect.data());
			});

		GenomeInstance genome = GenomeHelper.getGenome(entity);

		if (genome == null || genome.geneInstances().isEmpty())
		{
			this.biomorphosis$geneEffects = List.of();
			return;
		}

		List<GenomeEffectsHolder.GeneEffectInstance> effects = new ArrayList<>();
		HolderLookup.RegistryLookup<GeneDefinition> registry = serverLevel.registryAccess().lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY);

		for (GeneInstance gene : genome.geneInstances())
		{
			GeneDefinition definition = registry.getOrThrow(ResourceKey.create(Registration.GenomeReg.DEFINITION_KEY, gene.id())).value();
			if (definition == null)
				continue;
			GeneDefinition.RarityData data = definition.rarityData().get(gene.rarity());
			if (data == null)
				continue;

			for (int q = 0; q < data.effects().size(); q++)
			{
				GeneDefinition.GeneEffectEntry entry = data.effects().get(q);
				String key = biomorphosis$createEffectKey(gene, q, entry);
				CompoundTag effectData = preservedData.getOrDefault(key, entry.type().createData(entity, entry.params())).copy();
				entry.type().loadData(entity, entry.params(), effectData);
				effects.add(new GenomeEffectsHolder.GeneEffectInstance(key, entry, effectData));
			}
		}

		this.biomorphosis$geneEffects = List.copyOf(effects);
		this.biomorphosis$geneEffects.forEach(effect -> effect.entry().type().apply(entity, effect.entry().params(), effect.data()));
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void biomorphosis$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci)
	{
		CompoundTag effectsData = biomorphosis$saveGeneEffectData();
		if (!effectsData.isEmpty())
			tag.put(BIOMORPHOSIS_GENE_EFFECT_DATA, effectsData);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void biomorphosis$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci)
	{
		this.biomorphosis$pendingGeneEffectData.clear();
		if (!tag.contains(BIOMORPHOSIS_GENE_EFFECT_DATA))
			return;

		CompoundTag effectsData = tag.getCompound(BIOMORPHOSIS_GENE_EFFECT_DATA);
		this.biomorphosis$setGeneEffectData(effectsData);
	}

	@Unique
	private CompoundTag biomorphosis$saveGeneEffectData()
	{
		LivingEntity entity = (LivingEntity) (Object) this;
		CompoundTag root = new CompoundTag();

		this.biomorphosis$pendingGeneEffectData.forEach((key, data) ->
		{
			if (!data.isEmpty())
				root.put(key, data.copy());
		});

		for (GenomeEffectsHolder.GeneEffectInstance effect : this.biomorphosis$geneEffects)
		{
			CompoundTag data = effect.data().copy();
			effect.entry().type().saveData(entity, effect.entry().params(), data);
			if (!data.isEmpty())
				root.put(effect.key(), data);
		}

		return root;
	}

	@Unique
	private String biomorphosis$createEffectKey(GeneInstance gene, int effectIndex, GeneDefinition.GeneEffectEntry entry)
	{
		return gene.id() + "|" + gene.rarity().getSerializedName() + "|" + effectIndex + "|" + entry.type().getId();
	}
}
