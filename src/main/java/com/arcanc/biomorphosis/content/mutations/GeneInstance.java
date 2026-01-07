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
import net.minecraft.resources.ResourceLocation;

public record GeneInstance(ResourceLocation id, GeneRarity rarity)
{
	public static final Codec<GeneInstance> CODEC = RecordCodecBuilder.create(instance -> instance.
			group(
					ResourceLocation.CODEC.fieldOf("id").forGetter(GeneInstance :: id),
					GeneRarity.CODEC.fieldOf("gene_rarity").forGetter(GeneInstance :: rarity)).
			apply(instance, GeneInstance :: new));
}
