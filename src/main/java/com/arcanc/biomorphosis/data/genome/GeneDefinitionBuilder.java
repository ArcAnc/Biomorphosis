/**
 * @author ArcAnc
 * Created at: 06.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.genome;


import com.arcanc.biomorphosis.content.mutations.AttributeParams;
import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneRarity;
import com.arcanc.biomorphosis.content.mutations.types.IGeneEffectType;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GeneDefinitionBuilder
{
	private final ResourceLocation id;
	private ResourceLocation image;
	private final Map<GeneRarity, GeneDefinition.RarityData> rarityInfo = new EnumMap<>(GeneRarity.class);
	
	public static @NotNull GeneDefinitionBuilder builder(ResourceLocation id)
	{
		return new GeneDefinitionBuilder(id);
	}
	
	private GeneDefinitionBuilder (ResourceLocation id)
	{
		this.id = id;
	}
	
	public GeneDefinitionBuilder setImage(ResourceLocation image)
	{
		this.image = image;
		return this;
	}
	
	public RarityDataBuilder addRarityInfo(GeneRarity rarity)
	{
		return this.new RarityDataBuilder(rarity);
	}
	
	public GeneDefinitionBuilder addRarityInfo(GeneRarity rarity, GeneDefinition.RarityData data)
	{
		Preconditions.checkNotNull(rarity);
		Preconditions.checkNotNull(data);
		this.rarityInfo.put(rarity, data);
		return this;
	}
	
	public GeneDefinition end()
	{
		return new GeneDefinition(this.id, this.image, this.rarityInfo);
	}
	
	public class RarityDataBuilder
	{
		private final GeneRarity rarity;
		private final List<GeneDefinition.GeneEffectEntry> effects = new ArrayList<>();
		private final List<ResourceLocation> incompatibilities = new ArrayList<>();
		private int destabilizationAmount;
		
		private RarityDataBuilder(GeneRarity rarity)
		{
			this.rarity = rarity;
		}
		
		public RarityDataBuilder setDestabilizationAmount(int destabilizationAmount)
		{
			this.destabilizationAmount = destabilizationAmount;
			return this;
		}
		
		public RarityDataBuilder addIncompatibilities(ResourceLocation geneId)
		{
			Preconditions.checkNotNull(geneId);
			this.incompatibilities.add(geneId);
			return this;
		}
		
		public RarityDataBuilder addEffect(IGeneEffectType<?> type, AttributeParams params)
		{
			Preconditions.checkNotNull(type);
			Preconditions.checkNotNull(params);
			this.effects.add(new GeneDefinition.GeneEffectEntry(type, params));
			return this;
		}
		
		public GeneDefinitionBuilder end()
		{
			return GeneDefinitionBuilder.this.addRarityInfo(this.rarity, new GeneDefinition.RarityData(this.effects, this.incompatibilities, this.destabilizationAmount));
		}
	}
}
