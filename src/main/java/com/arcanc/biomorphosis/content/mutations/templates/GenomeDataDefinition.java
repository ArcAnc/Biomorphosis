/**
 * @author ArcAnc
 * Created at: 07.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.templates;


import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import org.jetbrains.annotations.NotNull;

public record GenomeDataDefinition(Weight weight, GenomeInstance genomeData) implements WeightedEntry
{
	public static final Codec<GenomeDataDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.
			group(
					Weight.CODEC.fieldOf("weight").forGetter(GenomeDataDefinition :: getWeight),
					GenomeInstance.CODEC.fieldOf("genome_data").forGetter(GenomeDataDefinition :: genomeData)).
			apply(instance,GenomeDataDefinition :: new));
	
	public GenomeDataDefinition(int weight, GenomeInstance genomeData)
	{
		this (Weight.of(weight), genomeData);
	}
	
	@Override
	public @NotNull Weight getWeight()
	{
		return this.weight;
	}
}
