/**
 * @author ArcAnc
 * Created at: 15.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations;


import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.*;

public record UnlockedGenome(Map<ResourceLocation, Set<GeneRarity>> unlockedGenes)
{
	public static final UnlockedGenome EMPTY = empty();

	public UnlockedGenome
	{
		Map<ResourceLocation, Set<GeneRarity>> copied = new LinkedHashMap<>();
		unlockedGenes.forEach((id, geneRarities) ->
				copied.put(id, copyRarities(geneRarities)));
		unlockedGenes = Collections.unmodifiableMap(copied);
	}

	public static UnlockedGenome empty()
	{
		return new UnlockedGenome(Map.of());
	}

	public static final Codec<UnlockedGenome> CODEC = Codec.unboundedMap(
					ResourceLocation.CODEC,
					GeneRarity.CODEC.listOf()).
			xmap(map ->
			{
				Map<ResourceLocation, Set<GeneRarity>> mutable = new LinkedHashMap<>();

				map.forEach((id, geneRarities) ->
						mutable.put(id, geneRarities.isEmpty() ? Set.of() : EnumSet.copyOf(geneRarities)));

				return new UnlockedGenome(mutable);
			},
			unlockedGenome ->
			{
				Map<ResourceLocation, List<GeneRarity>> encoded = new LinkedHashMap<>();

				unlockedGenome.unlockedGenes().forEach((id, geneRarities) ->
						encoded.put(id, new ArrayList<>(geneRarities)));
				return encoded;
			});

	public static final StreamCodec<FriendlyByteBuf, UnlockedGenome> STREAM_CODEC =
			ByteBufCodecs.<FriendlyByteBuf, ResourceLocation, Set<GeneRarity>, Map<ResourceLocation, Set<GeneRarity>>>map(
					LinkedHashMap :: new,
					ResourceLocation.STREAM_CODEC, ByteBufCodecs.collection(
							i -> EnumSet.noneOf(GeneRarity.class),
							NeoForgeStreamCodecs.enumCodec(GeneRarity.class)
					)).
			map(UnlockedGenome :: new, UnlockedGenome :: unlockedGenes);

	public boolean hasGene(GeneInstance gene)
	{
		return this.unlockedGenes.containsKey(gene.id());
	}

	public boolean hasGeneRarity(GeneInstance gene)
	{
		return this.getRaritiesById(gene.id()).contains(gene.rarity());
	}

	public UnlockedGenome unlock(GeneInstance gene)
	{
		if (hasGeneRarity(gene))
			return this;

		Map<ResourceLocation, Set<GeneRarity>> mutable = mutableCopy();
		Set<GeneRarity> geneRarities = mutable.computeIfAbsent(gene.id(), id -> EnumSet.noneOf(GeneRarity.class));
		geneRarities.add(gene.rarity());
		return new UnlockedGenome(mutable);
	}

	public Set<GeneRarity> getRaritiesById(ResourceLocation id)
	{
		return this.unlockedGenes.getOrDefault(id, Set.of());
	}

	public Set<ResourceLocation> getGeneNames()
	{
		return this.unlockedGenes.keySet();
	}

	private Map<ResourceLocation, Set<GeneRarity>> mutableCopy()
	{
		Map<ResourceLocation, Set<GeneRarity>> mutable = new LinkedHashMap<>();
		this.unlockedGenes.forEach((id, geneRarities) ->
				mutable.put(id, geneRarities.isEmpty() ? EnumSet.noneOf(GeneRarity.class) : EnumSet.copyOf(geneRarities)));
		return mutable;
	}

	private static Set<GeneRarity> copyRarities(Set<GeneRarity> geneRarities)
	{
		if (geneRarities.isEmpty())
			return Set.of();
		return Collections.unmodifiableSet(EnumSet.copyOf(geneRarities));
	}
}
