/**
 * @author ArcAnc
 * Created at: 07.05.2025
 * Copyright (c) 2025
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class DynamicMultiblockDefinition implements IMultiblockDefinition
{
    public static final MapCodec<DynamicMultiblockDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(DynamicMultiblockDefinition :: getId),
            ScanBehavior.CODEC.fieldOf("behavior").forGetter(DynamicMultiblockDefinition :: getBehavior),
            BlockPos.CODEC.fieldOf("max_size").forGetter(DynamicMultiblockDefinition :: size),
            BlockState.CODEC.fieldOf("allowed_block_type").forGetter(DynamicMultiblockDefinition :: getAllowedBlockType)).
            apply(instance, DynamicMultiblockDefinition::new));

    private final ResourceLocation id;
    private final ScanBehavior behavior;
    private final BlockPos maxSize;
    private final BlockState allowedBlockType;

    public DynamicMultiblockDefinition(ResourceLocation id, ScanBehavior behavior, BlockPos maxSize, BlockState allowedBlockType)
    {
        this.id = id;
        this.behavior = behavior;
        this.maxSize = maxSize;
        this.allowedBlockType = allowedBlockType;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    public ScanBehavior getBehavior()
    {
        return this.behavior;
    }

    public BlockState getAllowedBlockType()
    {
        return this.allowedBlockType;
    }

    @Override
    public BlockStateMap getStructure(BlockGetter level, BlockPos origin)
    {
        return this.behavior.getStructure(level, origin, this);
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

        private static @NotNull Map<BlockPos, BlockState> dfs(BlockGetter level, @NotNull Map<BlockPos, BlockState> visited, BlockPos startPos, DynamicMultiblockDefinition definition)
        {
            if (visited.containsKey(startPos))
                return visited;
            if (!isValidBlock(startPos, level, definition.getAllowedBlockType()))
                return visited;
            if (!withinBounds(startPos, startPos, definition.size()))
                return visited;

            visited.put(startPos, level.getBlockState(startPos));

            for (Direction dir : Direction.values())
                dfs(level, visited, startPos.relative(dir), definition);

            return visited;
        }

        private static @NotNull Map<BlockPos, BlockState> bfs(BlockGetter level, @NotNull Map<BlockPos, BlockState> visited, BlockPos startPos, DynamicMultiblockDefinition definition)
        {
            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(startPos);

            while (!queue.isEmpty())
            {
                BlockPos current = queue.poll();

                if (visited.containsKey(current))
                    continue;
                if (!isValidBlock(current, level, definition.getAllowedBlockType()))
                    continue;
                if (!withinBounds(current, startPos, definition.size()))
                    continue;

                visited.put(current, level.getBlockState(current));

                for (Direction dir : Direction.values())
                {
                    BlockPos neighbor = current.relative(dir);
                    if (!visited.containsKey(neighbor))
                        queue.add(neighbor);
                }
            }

            return visited;
        }

        private static boolean isValidBlock(BlockPos pos, @NotNull BlockGetter level, @NotNull BlockState allowedBlockType)
        {
            BlockState state = level.getBlockState(pos);
            Block block = allowedBlockType.getBlock();
            return state.is(block);
        }

        private static boolean withinBounds(@NotNull BlockPos pos, @NotNull BlockPos origin, @NotNull BlockPos maxSize)
        {
            return Math.abs(pos.getX() - origin.getX()) <= maxSize.getX()
                    && Math.abs(pos.getY() - origin.getY()) <= maxSize.getY()
                    && Math.abs(pos.getZ() - origin.getZ()) <= maxSize.getZ();
        }

        public @NotNull BlockStateMap getStructure(BlockGetter level, BlockPos startPos, DynamicMultiblockDefinition definition)
        {
            Map<BlockPos, BlockState> globalMap = this.executor.findStructure(level, new HashMap<>(), startPos, definition);

            return new BlockStateMap(globalMap.entrySet().stream().
                    collect(Collectors.toMap(
                            entry -> entry.getKey().subtract(startPos),
                            Map.Entry :: getValue)),
                            definition.getAllowedBlockType());
        }

        @Override
        public @NotNull String getSerializedName()
        {
            return this.name;
        }
    }

    @FunctionalInterface
    private interface BehaviorExecutor
    {
        Map<BlockPos, BlockState> findStructure(BlockGetter level, Map<BlockPos, BlockState> visited, BlockPos startPos, DynamicMultiblockDefinition definition);
    }
}
