/**
 * @author ArcAnc
 * Created at: 07.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record GenomeInstance(List<GeneInstance> geneInstances)
{
	public static final GenomeInstance EMPTY = new GenomeInstance(List.of());
	
	public static final Codec<GenomeInstance> CODEC = RecordCodecBuilder.create(instance -> instance.
			group(
					GeneInstance.CODEC.listOf().fieldOf("genes").forGetter(GenomeInstance :: geneInstances)).
			apply(instance, GenomeInstance :: new));
	
	public static final StreamCodec<FriendlyByteBuf, GenomeInstance> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.<FriendlyByteBuf, GeneInstance>list().
					apply(GeneInstance.STREAM_CODEC),
			GenomeInstance :: geneInstances,
			GenomeInstance :: new);
	
	public @NotNull GenomeInstance copy()
	{
		return new GenomeInstance(List.copyOf(this.geneInstances));
	}
	
	public int calculateDiff(GenomeInstance other)
	{
		Map<ResourceLocation, GeneRarity> a = new HashMap<>();
		Map<ResourceLocation, GeneRarity> b = new HashMap<>();
		
		for (GeneInstance g : this.geneInstances())
			a.put(g.id(), g.rarity());
		
		for (GeneInstance g : other.geneInstances())
			b.put(g.id(), g.rarity());
		
		int diff = 0;
		
		Set<ResourceLocation> allKeys = new HashSet<>();
		allKeys.addAll(a.keySet());
		allKeys.addAll(b.keySet());
		
		for (ResourceLocation id : allKeys)
		{
			GeneRarity ra = a.get(id);
			GeneRarity rb = b.get(id);
			
			if (ra == null)
				diff += rb.ordinal();
			else if (rb == null)
				diff += ra.ordinal();
			else
				diff += Math.abs(ra.ordinal() - rb.ordinal());
		}
		
		return diff;
	}
	
	public boolean hasGene(@NotNull ResourceLocation id)
	{
		if (this.geneInstances().isEmpty())
			return false;
		for (GeneInstance gene : this.geneInstances())
			if(gene.id().equals(id))
				return true;
		return false;
	}
	
	public Optional<GeneInstance> getGene(@NotNull ResourceLocation id)
	{
		if (this.geneInstances().isEmpty())
			return Optional.empty();
		
		for (GeneInstance gene : this.geneInstances())
			if (gene.id().equals(id))
				return Optional.of(gene);
		return Optional.empty();
	}
	
	public boolean isEmpty()
	{
		return this.geneInstances().isEmpty();
	}
}
