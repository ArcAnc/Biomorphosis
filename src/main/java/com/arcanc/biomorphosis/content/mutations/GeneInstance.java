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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record GeneInstance(ResourceLocation id, GeneRarity rarity)
{
	public static final Codec<GeneInstance> CODEC = RecordCodecBuilder.create(instance -> instance.
			group(
					ResourceLocation.CODEC.fieldOf("id").forGetter(GeneInstance :: id),
					GeneRarity.CODEC.fieldOf("gene_rarity").forGetter(GeneInstance :: rarity)).
			apply(instance, GeneInstance :: new));
	
	public static final StreamCodec<FriendlyByteBuf, GeneInstance> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC,
			GeneInstance :: id,
			GeneRarity.STREAM_CODEC,
			GeneInstance :: rarity,
			GeneInstance :: new
	);
	
	@Override
	public boolean equals(Object o)
	{
		if (! (o instanceof GeneInstance (ResourceLocation id1, GeneRarity rarity1)))
			return false;
		return rarity() == rarity1 && Objects.equals(id(), id1);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(id(), rarity());
	}
}
