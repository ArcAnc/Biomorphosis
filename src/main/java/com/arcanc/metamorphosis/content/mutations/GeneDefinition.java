/**
 * @author ArcAnc
 * Created at: 29.09.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.mutations;


import com.arcanc.metamorphosis.content.mutations.types.IGeneEffectType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record GeneDefinition(ResourceLocation id, ResourceLocation image, Map<GeneRarity, RarityData> rarityData)
{
	public static final Codec<GeneDefinition> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					ResourceLocation.CODEC.fieldOf("id").forGetter(GeneDefinition :: id),
					ResourceLocation.CODEC.fieldOf("image").forGetter(GeneDefinition :: image),
					Codec.unboundedMap(GeneRarity.CODEC, RarityData.CODEC).
							fieldOf("rarity_data").forGetter(GeneDefinition :: rarityData)).
					apply(instance, GeneDefinition :: new));

	public record RarityData(List<GeneEffectEntry> effects, List<ResourceLocation> incompatibilities, int destabilizationAmount)
	{
		public static final Codec<RarityData> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						GeneEffectEntry.CODEC.listOf().fieldOf("effects").forGetter(RarityData :: effects),
						ResourceLocation.CODEC.listOf().fieldOf("incompatibilities").forGetter(RarityData :: incompatibilities),
						Codec.INT.fieldOf("destabilization_amount").forGetter(RarityData :: destabilizationAmount)
				).apply(instance, RarityData ::new));
	}
	
	public record GeneEffectEntry(IGeneEffectType<?> type, AttributeParams params)
	{
		public static final Codec<GeneEffectEntry> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						IGeneEffectType.CODEC.fieldOf("type").forGetter(GeneEffectEntry :: type),
						AttributeParams.CODEC.fieldOf("params").forGetter(GeneEffectEntry :: params)
				).apply(instance, GeneEffectEntry::new));
	}
}
