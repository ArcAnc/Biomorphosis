/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.definition;

import com.arcanc.biomorphosis.util.helper.BioCodecs;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record GrowthPattern(BlockState anchor,
                            Map<BlockPos, Requirement> base,
                            List<Branch> branches,
                            int minRadius,
                            List<PatternRule> rules)
{
    public static final Codec<GrowthPattern> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("anchor").forGetter(GrowthPattern :: anchor),
            Codec.unboundedMap(BioCodecs.BLOCK_POS_JSON_CODEC, Requirement.CODEC).
                    optionalFieldOf("base", Map.of()).forGetter(GrowthPattern :: base),
            Branch.CODEC.listOf().optionalFieldOf("branches", List.of()).forGetter(GrowthPattern :: branches),
            Codec.INT.optionalFieldOf("min_radius", 1).forGetter(GrowthPattern :: minRadius),
            PatternRule.CODEC.listOf().optionalFieldOf("rules", List.of()).forGetter(GrowthPattern :: rules)).
            apply(instance, GrowthPattern :: new));

    public GrowthPattern(BlockState anchor, Map<BlockPos, Requirement> base, List<Branch> branches)
    {
        this(anchor, base, branches, 1, List.of());
    }

    public GrowthPattern
    {
        if (base.containsKey(BlockPos.ZERO))
            throw new IllegalArgumentException("The anchor must not be duplicated in a pattern base");
        if (minRadius < 1)
            throw new IllegalArgumentException("A pattern requires min_radius of at least one");
        base = Map.copyOf(base);
        branches = List.copyOf(branches);
        rules = List.copyOf(rules);
    }

    public boolean accepts(BlockState state)
    {
        if (state.is(this.anchor.getBlock()))
            return true;
        return this.branches.stream().flatMap(branch -> branch.cells().values().stream()).
                anyMatch(requirement -> requirement.accepts(state));
    }

    public boolean baseMatches(BlockGetter level, BlockPos anchorPos)
    {
        return this.base.entrySet().stream().
                allMatch(entry -> entry.getValue().matches(level, anchorPos.offset(entry.getKey())));
    }

    public record Requirement(@Nullable BlockState state, @Nullable Fluid fluid)
    {
        public static final Codec<Requirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.optionalFieldOf("state").forGetter(requirement -> Optional.ofNullable(requirement.state)),
                BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("fluid").forGetter(requirement -> Optional.ofNullable(requirement.fluid))).
                apply(instance, (state, fluid) -> new Requirement(state.orElse(null), fluid.orElse(null))));

        public Requirement
        {
            if ((state == null) == (fluid == null))
                throw new IllegalArgumentException("A pattern requirement must declare exactly one of state or fluid");
        }

        public boolean accepts(BlockState toCheck)
        {
            return this.state != null && BlockHelper.statesEquivalent(this.state, toCheck);
        }

        public boolean matches(BlockGetter level, BlockPos pos)
        {
            return this.state != null ? BlockHelper.statesEquivalent(this.state, level.getBlockState(pos)) :
                    level.getFluidState(pos).getType() == this.fluid;
        }

        public static Requirement state(BlockState state)
        {
            return new Requirement(state, null);
        }

        public static Requirement fluid(Fluid fluid)
        {
            return new Requirement(null, fluid);
        }
    }

    public record Branch(BlockPos start, BlockPos step, Map<BlockPos, Requirement> cells)
    {
        public static final Codec<Branch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("start").forGetter(Branch :: start),
                BlockPos.CODEC.fieldOf("step").forGetter(Branch :: step),
                Codec.unboundedMap(BioCodecs.BLOCK_POS_JSON_CODEC, Requirement.CODEC).
                        fieldOf("cells").forGetter(Branch :: cells)).
                apply(instance, Branch :: new));

        public Branch
        {
            if (step.equals(BlockPos.ZERO))
                throw new IllegalArgumentException("A growth-pattern branch must have a non-zero step");
            if (cells.isEmpty())
                throw new IllegalArgumentException("A growth-pattern branch requires at least one cell");
            cells = Map.copyOf(cells);
        }
    }
}
