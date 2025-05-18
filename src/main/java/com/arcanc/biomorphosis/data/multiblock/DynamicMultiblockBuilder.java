/**
 * @author ArcAnc
 * Created at: 09.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.definition.DynamicMultiblockDefinition;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class DynamicMultiblockBuilder
{
    private final ResourceLocation location;
    private DynamicMultiblockDefinition.ScanBehavior behavior;
    private BlockPos maxSize;
    private Block allowedBlockType;

    public DynamicMultiblockBuilder(ResourceLocation location)
    {
        Preconditions.checkNotNull(location);
        this.location = location;
    }

    public DynamicMultiblockBuilder setBehavior(DynamicMultiblockDefinition.ScanBehavior behavior)
    {
        Preconditions.checkNotNull(behavior);
        this.behavior = behavior;
        return this;
    }

    public DynamicMultiblockBuilder setMaxSize(BlockPos localSize)
    {
        Preconditions.checkNotNull(localSize);
        this.maxSize = localSize;
        return this;
    }

    public DynamicMultiblockBuilder setMaxSize(int x, int y, int z)
    {
        return setMaxSize(new BlockPos(x, y, z));
    }

    public DynamicMultiblockBuilder setAllowedBlockType(Block block)
    {
        Preconditions.checkNotNull(block);
        this.allowedBlockType = block;
        return this;
    }

    public DynamicMultiblockDefinition end()
    {
        return new DynamicMultiblockDefinition(this.location, this.behavior, this.maxSize, BuiltInRegistries.BLOCK.getKey(this.allowedBlockType));
    }
}
