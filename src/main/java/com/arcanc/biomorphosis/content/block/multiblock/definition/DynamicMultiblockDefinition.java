/**
 * @author ArcAnc
 * Created at: 07.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.definition;

import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

public class DynamicMultiblockDefinition implements IMultiblockDefinition
{
    public static final MapCodec<DynamicMultiblockDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(DynamicMultiblockDefinition :: getId),
            Mode.CODEC.optionalFieldOf("mode", Mode.CONNECTED).forGetter(DynamicMultiblockDefinition :: getMode),
            ScanBehavior.CODEC.optionalFieldOf("behavior", ScanBehavior.BFS).forGetter(DynamicMultiblockDefinition :: getBehavior),
            BlockPos.CODEC.optionalFieldOf("max_size").forGetter(definition -> Optional.ofNullable(definition.maxSize)),
            BlockState.CODEC.optionalFieldOf("allowed_block_type").forGetter(definition -> Optional.ofNullable(definition.allowedBlockType)),
            GrowthPattern.CODEC.optionalFieldOf("pattern").forGetter(definition -> Optional.ofNullable(definition.pattern))).
            apply(instance, DynamicMultiblockDefinition :: new));

    private final ResourceLocation id;
    private final Mode mode;
    private final ScanBehavior behavior;
    private final @Nullable BlockPos maxSize;
    private final @Nullable BlockState allowedBlockType;
    private final @Nullable GrowthPattern pattern;

    public DynamicMultiblockDefinition(ResourceLocation id, ScanBehavior behavior, BlockPos maxSize, BlockState allowedBlockType)
    {
        this(id, Mode.CONNECTED, behavior, Optional.ofNullable(maxSize), Optional.ofNullable(allowedBlockType), Optional.empty());
    }

    public DynamicMultiblockDefinition(ResourceLocation id, BlockPos maxSize, GrowthPattern pattern)
    {
        this(id, Mode.PATTERN, ScanBehavior.BFS, Optional.ofNullable(maxSize), Optional.empty(), Optional.ofNullable(pattern));
    }

    private DynamicMultiblockDefinition(ResourceLocation id,
                                        Mode mode,
                                        ScanBehavior behavior,
                                        Optional<BlockPos> maxSize,
                                        Optional<BlockState> allowedBlockType,
                                        Optional<GrowthPattern> pattern)
    {
        this.id = id;
        this.mode = mode;
        this.behavior = behavior;
        this.maxSize = maxSize.orElse(null);
        this.allowedBlockType = allowedBlockType.orElse(null);
        this.pattern = pattern.orElse(null);

        if (this.maxSize == null)
            throw new IllegalArgumentException("Dynamic multiblocks require max_size");
        if (mode == Mode.CONNECTED && this.allowedBlockType == null)
            throw new IllegalArgumentException("Connected dynamic multiblocks require allowed_block_type");
        if (mode == Mode.PATTERN && this.pattern == null)
            throw new IllegalArgumentException("Pattern dynamic multiblocks require a pattern");
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    public Mode getMode()
    {
        return this.mode;
    }

    public ScanBehavior getBehavior()
    {
        return this.behavior;
    }

    public @Nullable BlockState getAllowedBlockType()
    {
        return this.allowedBlockType;
    }

    public Optional<GrowthPattern> getPattern()
    {
        return Optional.ofNullable(this.pattern);
    }

    /** Returns whether this definition can be selected for a placed block. */
    public boolean accepts(BlockState state)
    {
        return switch (this.mode)
        {
            case CONNECTED -> state.is(this.allowedBlockType.getBlock());
            case PATTERN -> this.pattern.accepts(state);
        };
    }

    @Override
    public PartsMap getStructure(BlockGetter level, BlockPos origin)
    {
        return switch (this.mode)
        {
            case CONNECTED -> this.behavior.getStructure(level, origin, this);
            case PATTERN -> getGrowingStructure(level, origin);
        };
    }

    public Optional<BlockPos> findPatternOrigin(BlockGetter level, BlockPos placedPos)
    {
        if (this.mode != Mode.PATTERN)
            return Optional.empty();

        if (!this.pattern.rules().isEmpty())
            return level.getBlockState(placedPos).is(this.pattern.anchor().getBlock()) ? Optional.of(placedPos) : Optional.empty();

        return possiblePatternOrigins(level, placedPos).stream().
                max(Comparator.<BlockPos>comparingInt(origin -> getGrowingStructure(level, origin).getParts().size()).
                        thenComparingLong(BlockPos :: asLong));
    }

    private List<BlockPos> possiblePatternOrigins(BlockGetter level, BlockPos placedPos)
    {
        List<BlockPos> origins = new java.util.ArrayList<>();
        if (level.getBlockState(placedPos).is(this.pattern.anchor().getBlock()))
            origins.add(placedPos);

        int maxTiles = maxPatternTiles();
        for (GrowthPattern.Branch branch : this.pattern.branches())
            for (Map.Entry<BlockPos, GrowthPattern.Requirement> cell : branch.cells().entrySet())
                if (cell.getValue().matches(level, placedPos))
                    for (int index = 0; index < maxTiles; index++)
                    {
                        BlockPos offset = branch.start().offset(cell.getKey()).offset(
                                branch.step().getX() * index,
                                branch.step().getY() * index,
                                branch.step().getZ() * index);
                        BlockPos origin = placedPos.subtract(offset);
                        if (level.getBlockState(origin).is(this.pattern.anchor().getBlock()))
                            origins.add(origin);
                    }
        return origins.stream().distinct().toList();
    }

    private PartsMap getGrowingStructure(BlockGetter level, BlockPos origin)
    {
        if (!level.getBlockState(origin).is(this.pattern.anchor().getBlock()))
            return emptyStructure(this.pattern.anchor());
        if (!this.pattern.rules().isEmpty())
            return getRuleStructure(level, origin);
        if (!this.pattern.baseMatches(level, origin) || !this.pattern.base().keySet().stream().
                allMatch(localPos -> withinBounds(localPos, BlockPos.ZERO, this.maxSize)))
            return emptyStructure(this.pattern.anchor());

        Map<BlockPos, PartsMap.MultiblockPart> parts = new HashMap<>();
        addPart(level, origin, BlockPos.ZERO, parts);
        for (BlockPos localPos : this.pattern.base().keySet())
            addPart(level, origin.offset(localPos), localPos, parts);
        for (GrowthPattern.Branch branch : this.pattern.branches())
            for (int index = 0; index < maxPatternTiles(); index++)
            {
                BlockPos tileOrigin = branch.start().offset(
                        branch.step().getX() * index,
                        branch.step().getY() * index,
                        branch.step().getZ() * index);
                if (!tileMatches(level, origin, tileOrigin, branch) || !tileWithinBounds(tileOrigin, branch))
                    break;
                for (BlockPos cellPos : branch.cells().keySet())
                    addPart(level, origin.offset(tileOrigin).offset(cellPos), tileOrigin.offset(cellPos), parts);
            }
        return new PartsMap(parts, this.pattern.anchor());
    }

    private PartsMap getRuleStructure(BlockGetter level, BlockPos origin)
    {
        int maxRadius = Math.min(this.maxSize.getX(), this.maxSize.getZ());
        for (int radius = maxRadius; radius >= this.pattern.minRadius(); radius--)
        {
            Map<BlockPos, PartsMap.MultiblockPart> parts = new HashMap<>();
            addPart(level, origin, BlockPos.ZERO, parts);
            PatternContext context = new PatternContext(origin, radius);
            boolean matches = true;
            for (int x = -radius; x <= radius && matches; x++)
                for (int y = -this.maxSize.getY(); y <= this.maxSize.getY() && matches; y++)
                    for (int z = -radius; z <= radius; z++)
                    {
                        BlockPos localPos = new BlockPos(x, y, z);
                        GrowthPattern.Requirement requirement = this.pattern.rules().stream().
                                filter(rule -> rule.condition().test(context, localPos)).
                                map(PatternRule :: requirement).
                                findFirst().orElse(null);
                        if (requirement == null || localPos.equals(BlockPos.ZERO))
                            continue;
                        if (!requirement.matches(level, origin.offset(localPos)))
                        {
                            matches = false;
                            break;
                        }
                        addPart(level, origin.offset(localPos), localPos, parts);
                    }
            if (matches)
                return new PartsMap(parts, this.pattern.anchor());
        }
        return emptyStructure(this.pattern.anchor());
    }

    private boolean tileMatches(BlockGetter level, BlockPos origin, BlockPos tileOrigin, GrowthPattern.Branch branch)
    {
        return branch.cells().entrySet().stream().allMatch(cell ->
                cell.getValue().matches(level, origin.offset(tileOrigin).offset(cell.getKey())));
    }

    private boolean tileWithinBounds(BlockPos tileOrigin, GrowthPattern.Branch branch)
    {
        return branch.cells().keySet().stream().map(tileOrigin :: offset).
                allMatch(localPos -> withinBounds(localPos, BlockPos.ZERO, this.maxSize));
    }

    private int maxPatternTiles()
    {
        return Math.max(this.maxSize.getX(), Math.max(this.maxSize.getY(), this.maxSize.getZ())) + 1;
    }

    private static PartsMap emptyStructure(BlockState placedBlock)
    {
        return new PartsMap(Map.of(), placedBlock);
    }

    private static void addPart(BlockGetter level,
                                BlockPos realPos,
                                BlockPos localPos,
                                Map<BlockPos, PartsMap.MultiblockPart> parts)
    {
        BlockState state = level.getBlockState(realPos);
        VoxelShape shape = state.getShape(level, realPos);
        parts.put(localPos, new PartsMap.MultiblockPart(shape, IngredientWithSize.of(state.getBlock())));
    }

    @Override
    public MultiblockType type()
    {
        return MultiblockType.DYNAMIC;
    }

    @Override
    public BlockPos size()
    {
        return this.maxSize;
    }

    public enum Mode implements StringRepresentable
    {
        CONNECTED("connected"),
        PATTERN("pattern");

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode :: values);
        private final String name;

        Mode(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }

    public enum ScanBehavior implements StringRepresentable
    {
        DFS("dfs", ScanBehavior :: dfs),
        BFS("bfs", ScanBehavior :: bfs);

        public static final Codec<ScanBehavior> CODEC = StringRepresentable.fromEnum(ScanBehavior :: values);
        private final String name;
        private final BehaviorExecutor executor;

        ScanBehavior(String name, BehaviorExecutor executor)
        {
            this.name = name;
            this.executor = executor;
        }

        private static Map<BlockPos, PartsMap.MultiblockPart> dfs(BlockGetter level,
                                                                   Map<BlockPos, PartsMap.MultiblockPart> visited,
                                                                   BlockPos currentPos,
                                                                   BlockPos origin,
                                                                   DynamicMultiblockDefinition definition)
        {
            if (visited.containsKey(currentPos) || !isValidBlock(currentPos, level, definition.allowedBlockType) ||
                !withinBounds(currentPos, origin, definition.maxSize))
                return visited;
            addPart(level, currentPos, currentPos, visited);
            for (Direction dir : Direction.values())
                dfs(level, visited, currentPos.relative(dir), origin, definition);
            return visited;
        }

        private static Map<BlockPos, PartsMap.MultiblockPart> bfs(BlockGetter level,
                                                                   Map<BlockPos, PartsMap.MultiblockPart> visited,
                                                                   BlockPos startPos,
                                                                   BlockPos origin,
                                                                   DynamicMultiblockDefinition definition)
        {
            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(startPos);
            while (!queue.isEmpty())
            {
                BlockPos current = queue.poll();
                if (visited.containsKey(current) || !isValidBlock(current, level, definition.allowedBlockType) ||
                    !withinBounds(current, origin, definition.maxSize))
                    continue;
                addPart(level, current, current, visited);
                for (Direction dir : Direction.values())
                    if (!visited.containsKey(current.relative(dir)))
                        queue.add(current.relative(dir));
            }
            return visited;
        }

        private static boolean isValidBlock(BlockPos pos, BlockGetter level, BlockState allowedBlockType)
        {
            Block block = allowedBlockType.getBlock();
            return level.getBlockState(pos).is(block);
        }

        public PartsMap getStructure(BlockGetter level, BlockPos origin, DynamicMultiblockDefinition definition)
        {
            Map<BlockPos, PartsMap.MultiblockPart> globalMap = this.executor.findStructure(level, new HashMap<>(), origin, origin, definition);
            return new PartsMap(globalMap.entrySet().stream().collect(Collectors.toMap(
                    entry -> entry.getKey().subtract(origin), Map.Entry :: getValue)), definition.allowedBlockType);
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }

    private static boolean withinBounds(BlockPos pos, BlockPos origin, BlockPos maxSize)
    {
        return Math.abs(pos.getX() - origin.getX()) <= maxSize.getX() &&
                Math.abs(pos.getY() - origin.getY()) <= maxSize.getY() &&
                Math.abs(pos.getZ() - origin.getZ()) <= maxSize.getZ();
    }

    @FunctionalInterface
    private interface BehaviorExecutor
    {
        Map<BlockPos, PartsMap.MultiblockPart> findStructure(BlockGetter level,
                                                              Map<BlockPos, PartsMap.MultiblockPart> visited,
                                                              BlockPos currentPos,
                                                              BlockPos origin,
                                                              DynamicMultiblockDefinition definition);
    }
}
