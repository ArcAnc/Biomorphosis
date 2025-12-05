/**
 * @author ArcAnc
 * Created at: 07.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.multiblock.definition;

import com.arcanc.metamorphosis.util.helper.MetaCodecs;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BlockStateMap
{

    public static final Codec<BlockStateMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(MetaCodecs.BLOCK_POS_JSON_CODEC, BlockState.CODEC).
                fieldOf("states").
                forGetter(BlockStateMap :: getStates),
            BlockState.CODEC.
                fieldOf("placedBlock").
                forGetter(BlockStateMap :: getPlacedBlock)).
            apply(instance, BlockStateMap :: new));
    private final Map<BlockPos, BlockState> stateMap;
    private final BlockState placedBlock;
    private final BlockPos size;
    private final List<MultiblockStructurePart> structure;
    
    public BlockStateMap(Map<BlockPos, BlockState> map, BlockState placedBlock)
    {
        Preconditions.checkNotNull(map);
        this.stateMap = map;
        this.placedBlock = placedBlock;
        this.size = calculateSize();
        this.structure = calculateStructure();
    }

    public Map<BlockPos, BlockState> getStates()
    {
        return this.stateMap;
    }
    
    public BlockState getPlacedBlock()
    {
        return placedBlock;
    }
    
    public List<MultiblockStructurePart> getStructure()
    {
        return this.structure;
    }

    public List<ItemStack> getStackedStructure()
    {
        return getStructure().stream().
                map(part -> new ItemStack(part.getState().getBlock(), part.getAmount())).
                collect(Collectors.toList());
    }

    public BlockPos getSize()
    {
        return this.size;
    }

    private @NotNull List<MultiblockStructurePart> calculateStructure()
    {
        Map<BlockState, MultiblockStructurePart> parts = new HashMap<>();

        this.stateMap.forEach((pos, state) ->
                parts.compute(state, (key, existing) ->
                {
                    if (existing == null)
                        return new MultiblockStructurePart(state);
                    existing.incrAmount();
                    return existing;
                }));

        return new ArrayList<>(parts.values());
    }

    private BlockPos calculateSize()
    {
        if (this.stateMap.isEmpty())
            return BlockPos.ZERO;
        int[] sizes =
                {
                        Integer.MIN_VALUE, //maxX
                        Integer.MIN_VALUE, //maxY
                        Integer.MIN_VALUE, //maxZ
                        Integer.MAX_VALUE, //minX
                        Integer.MAX_VALUE, //minY
                        Integer.MAX_VALUE  //minZ
                };
        this.stateMap.keySet().forEach(pos ->
                Arrays.stream(Direction.Axis.values()).forEach(axis ->
                        checkPos(pos.get(axis), axis.ordinal(), sizes)));
        return new BlockPos(sizes[0] - sizes[3] + 1, sizes[1] - sizes[4] + 1, sizes[2] - sizes[5] + 1);
    }

    private void checkPos(int pos, int index, int @NotNull []sizes)
    {
        int secondaryIndex = index + 3;
        sizes[index] = Math.max(sizes[index], pos);
        sizes[secondaryIndex] = Math.min(sizes[secondaryIndex], pos);
    }

    public static class MultiblockStructurePart
    {
        private int amount;
        private final BlockState state;

        public MultiblockStructurePart(BlockState state, int amount)
        {
            this.state = state;
            this.amount = amount;
        }

        public MultiblockStructurePart(BlockState state)
        {
            this(state, 1);
        }

        public BlockState getState()
        {
            return this.state;
        }

        public int getAmount()
        {
            return this.amount;
        }

        public void setAmount(int newAmount)
        {
            this.amount = newAmount;
        }

        public void incrAmount()
        {
            this.amount++;
        }

        public void decrAmount()
        {
            this.amount--;
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
                return true;
            if (!(object instanceof MultiblockStructurePart that))
                return false;
            return getState().equals(that.getState());
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(getState());
        }
    }
}
