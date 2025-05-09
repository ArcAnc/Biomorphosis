/**
 * @author ArcAnc
 * Created at: 07.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.util.helper.BioCodecs;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class BlockStateMap
{

    public static final Codec<BlockStateMap> CODEC = Codec.unboundedMap(BioCodecs.BLOCK_POS_JSON_CODEC, BlockState.CODEC).
            xmap(BlockStateMap::new, BlockStateMap::getStates);
    private final Map<BlockPos, BlockState> stateMap;

    public BlockStateMap(Map<BlockPos, BlockState> map)
    {
        Preconditions.checkNotNull(map);
        this.stateMap = map;
    }

    public Map<BlockPos, BlockState> getStates()
    {
        return this.stateMap;
    }

    public BlockPos getSize()
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
}
