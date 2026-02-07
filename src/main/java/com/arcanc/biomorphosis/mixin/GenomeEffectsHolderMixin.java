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
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public class GenomeEffectsHolderMixin implements GenomeEffectsHolder
{
	@Unique
	private List<GeneDefinition.GeneEffectEntry> biomorphosis$geneEffects = List.of();
	
	@Override
	public List<GeneDefinition.GeneEffectEntry> biomorphosis$getGeneEffects()
	{
		return this.biomorphosis$geneEffects;
	}
	
	@Override
	public void biomorphosis$rebuildEffects()
	{
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!(entity.level() instanceof ServerLevel serverLevel))
			return;
		
		if (!biomorphosis$getGeneEffects().isEmpty())
			biomorphosis$getGeneEffects().forEach(entry -> entry.type().remove(entity, entry.params()));
			
		
		GenomeInstance genome = entity.getData(Registration.DataAttachmentsReg.GENOME);
		
		if (genome == null || genome.geneInstances().isEmpty())
		{
			this.biomorphosis$geneEffects = List.of();
			return;
		}
		
		List<GeneDefinition.GeneEffectEntry> effects = new ArrayList<>();
		Registry<GeneDefinition> registry = serverLevel.registryAccess().lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY);
		
		for (GeneInstance gene : genome.geneInstances())
		{
			GeneDefinition definition = registry.getValue(gene.id());
			if (definition == null)
				continue;
			GeneDefinition.RarityData data = definition.rarityData().get(gene.rarity());
			if (data == null)
				continue;
			
			effects.addAll(data.effects());
		}
		
		this.biomorphosis$geneEffects = List.copyOf(effects);
		this.biomorphosis$geneEffects.forEach(entry -> entry.type().apply(entity, entry.params()));
	}
}
