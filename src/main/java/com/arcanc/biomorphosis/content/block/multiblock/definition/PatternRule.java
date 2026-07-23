/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PatternRule(PatternPrecondition condition, GrowthPattern.Requirement requirement)
{
    public static final Codec<PatternRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PatternPrecondition.CODEC.fieldOf("condition").forGetter(PatternRule :: condition),
            GrowthPattern.Requirement.CODEC.fieldOf("requirement").forGetter(PatternRule :: requirement)).
            apply(instance, PatternRule :: new));
}
