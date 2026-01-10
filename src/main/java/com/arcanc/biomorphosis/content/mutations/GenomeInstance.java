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

import java.util.List;

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
}
