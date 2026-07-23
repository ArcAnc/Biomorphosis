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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

public interface PatternPrecondition
{
    Codec<PatternPrecondition> CODEC = Codec.recursive("pattern_precondition", codec ->
            Type.CODEC.dispatch(PatternPrecondition :: type, type -> type.codec(codec)));

    boolean test(PatternContext context, BlockPos localPos);

    Type type();

    enum Type implements StringRepresentable
    {
        ALWAYS("always"),
        COMPARE("compare"),
        ALL("all"),
        ANY("any"),
        NOT("not");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type :: values);
        private final String name;

        Type(String name)
        {
            this.name = name;
        }

        public MapCodec<? extends PatternPrecondition> codec(Codec<PatternPrecondition> recursiveCodec)
        {
            return switch (this)
            {
                case ALWAYS -> Always.CODEC;
                case COMPARE -> Compare.CODEC;
                case ALL -> Group.codec(recursiveCodec, true);
                case ANY -> Group.codec(recursiveCodec, false);
                case NOT -> Not.codec(recursiveCodec);
            };
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }

    record Always() implements PatternPrecondition
    {
        static final MapCodec<Always> CODEC = MapCodec.unit(new Always());

        @Override
        public boolean test(PatternContext context, BlockPos localPos)
        {
            return true;
        }

        @Override
        public Type type()
        {
            return Type.ALWAYS;
        }
    }

    record Compare(Axis axis, boolean absolute, Operator operator, Value value) implements PatternPrecondition
    {
        static final MapCodec<Compare> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Axis.CODEC.fieldOf("axis").forGetter(Compare :: axis),
                Codec.BOOL.optionalFieldOf("absolute", false).forGetter(Compare :: absolute),
                Operator.CODEC.fieldOf("operator").forGetter(Compare :: operator),
                Value.CODEC.fieldOf("value").forGetter(Compare :: value)).
                apply(instance, Compare :: new));

        @Override
        public boolean test(PatternContext context, BlockPos localPos)
        {
            int actual = localPos.get(this.axis.directionAxis);
            if (this.absolute)
                actual = Math.abs(actual);
            return this.operator.test(actual, this.value.resolve(context));
        }

        @Override
        public Type type()
        {
            return Type.COMPARE;
        }
    }

    record Group(List<PatternPrecondition> conditions, boolean all) implements PatternPrecondition
    {
        static MapCodec<Group> codec(Codec<PatternPrecondition> recursiveCodec, boolean all)
        {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    recursiveCodec.listOf().fieldOf("conditions").forGetter(Group :: conditions)).
                    apply(instance, conditions -> new Group(conditions, all)));
        }

        public Group
        {
            if (conditions.isEmpty())
                throw new IllegalArgumentException("A condition group must not be empty");
            conditions = List.copyOf(conditions);
        }

        @Override
        public boolean test(PatternContext context, BlockPos localPos)
        {
            return this.all ? this.conditions.stream().allMatch(condition -> condition.test(context, localPos)) :
                    this.conditions.stream().anyMatch(condition -> condition.test(context, localPos));
        }

        @Override
        public Type type()
        {
            return this.all ? Type.ALL : Type.ANY;
        }
    }

    record Not(PatternPrecondition condition) implements PatternPrecondition
    {
        static MapCodec<Not> codec(Codec<PatternPrecondition> recursiveCodec)
        {
            return recursiveCodec.fieldOf("condition").xmap(Not :: new, Not :: condition);
        }

        @Override
        public boolean test(PatternContext context, BlockPos localPos)
        {
            return !this.condition.test(context, localPos);
        }

        @Override
        public Type type()
        {
            return Type.NOT;
        }
    }

    enum Axis implements StringRepresentable
    {
        X("x", Direction.Axis.X),
        Y("y", Direction.Axis.Y),
        Z("z", Direction.Axis.Z);

        static final Codec<Axis> CODEC = StringRepresentable.fromEnum(Axis :: values);
        private final String name;
        private final Direction.Axis directionAxis;

        Axis(String name, Direction.Axis directionAxis)
        {
            this.name = name;
            this.directionAxis = directionAxis;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }

    enum Operator implements StringRepresentable
    {
        EQUAL("equal", Objects :: equals),
        LESS_THAN("less_than", (left, right) -> left < right),
        LESS_OR_EQUAL("less_or_equal", (left, right) -> left <= right),
        GREATER_THAN("greater_than", (left, right) -> left > right),
        GREATER_OR_EQUAL("greater_or_equal", (left, right) -> left >= right);

        static final Codec<Operator> CODEC = StringRepresentable.fromEnum(Operator :: values);
        private final String name;
        private final BiPredicate<Integer, Integer> predicate;

        Operator(String name, BiPredicate<Integer, Integer> predicate)
        {
            this.name = name;
            this.predicate = predicate;
        }

        boolean test(int left, int right)
        {
            return this.predicate.test(left, right);
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }

    record Value(Kind kind, int constant)
    {
        static final Codec<Value> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Kind.CODEC.fieldOf("kind").forGetter(Value :: kind),
                Codec.INT.optionalFieldOf("constant", 0).forGetter(Value :: constant)).
                apply(instance, Value :: new));

        int resolve(PatternContext context)
        {
            return switch (this.kind)
            {
                case CONSTANT -> this.constant;
                case RADIUS -> context.radius();
                case NEGATIVE_RADIUS -> -context.radius();
            };
        }

        enum Kind implements StringRepresentable
        {
            CONSTANT("constant"),
            RADIUS("radius"),
            NEGATIVE_RADIUS("negative_radius");

            static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind :: values);
            private final String name;

            Kind(String name)
            {
                this.name = name;
            }

            @Override
            public String getSerializedName()
            {
                return this.name;
            }
        }
    }
}
