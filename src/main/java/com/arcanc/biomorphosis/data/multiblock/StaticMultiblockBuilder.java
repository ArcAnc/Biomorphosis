/**
 * @author ArcAnc
 * Created at: 09.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.definition.BlockStateMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.StaticMultiblockDefinition;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class StaticMultiblockBuilder
{
    private final ResourceLocation location;
    private final Map<BlockPos, BlockState> stateMap = new HashMap<>();

    public StaticMultiblockBuilder(ResourceLocation location)
    {
        Preconditions.checkNotNull(location);
        this.location = location;
    }

    public StaticMultiblockBuilder addPart(BlockPos pos, BlockState state)
    {
        Preconditions.checkNotNull(pos);
        Preconditions.checkNotNull(state);
        this.stateMap.putIfAbsent(pos, state);
        return this;
    }

    public StaticMultiblockBuilder addParts(Map<BlockPos, BlockState> stateMap)
    {
        Preconditions.checkNotNull(stateMap);
        if (stateMap.isEmpty())
            return this;
        stateMap.forEach(this.stateMap :: putIfAbsent);
        return this;
    }

    public StaticMultiblockDefinition end()
    {
        return new StaticMultiblockDefinition(this.location, new BlockStateMap(this.stateMap));
    }
}
