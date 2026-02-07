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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record UnlockedGenome(Map<ResourceLocation, Set<GeneRarity>> unlockedGenes)
{
	public static final UnlockedGenome EMPTY = new UnlockedGenome(new LinkedHashMap<>());

	public static final Codec<UnlockedGenome> CODEC = Codec.unboundedMap(
					ResourceLocation.CODEC,
					GeneRarity.CODEC.listOf()).
			xmap(map ->
			{
				Map<ResourceLocation, Set<GeneRarity>> mutable = new LinkedHashMap<>();
				
				map.forEach((id, geneRarities) ->
						mutable.put(id, EnumSet.copyOf(geneRarities)));
				
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
	
	public boolean hasGene(@NotNull GeneInstance gene)
	{
		return this.unlockedGenes.containsKey(gene.id());
	}
	
	public @NotNull Set<GeneRarity> getRaritiesById(@NotNull ResourceLocation id)
	{
		return this.unlockedGenes.getOrDefault(id, Set.of());
	}
	
	public @NotNull Set<ResourceLocation> getGeneNames()
	{
		return this.unlockedGenes.keySet();
	}
}
