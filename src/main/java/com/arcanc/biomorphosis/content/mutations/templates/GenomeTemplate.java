/**
 * @author ArcAnc
 * Created at: 07.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.templates;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;

public record GenomeTemplate (EntityType<?> entityType, WeightedRandomList<GenomeDataDefinition> genomes)
{
	public static final Codec<GenomeTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.
			group(
					BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(GenomeTemplate :: entityType),
					WeightedRandomList.codec(GenomeDataDefinition.CODEC).fieldOf("genomes").forGetter( GenomeTemplate :: genomes)).
			apply(instance, GenomeTemplate :: new));
}
