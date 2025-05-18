/**
 * @author ArcAnc
 * Created at: 09.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.block.multiblock.definition.DynamicMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.multiblock.DynamicMultiblockBuilder;
import com.arcanc.biomorphosis.data.multiblock.StaticMultiblockBuilder;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BioMultiblockProvider extends BioRegistryData
{
    private final Map<ResourceLocation, IMultiblockDefinition> multiblockDataMap = new HashMap<>();

    public BioMultiblockProvider()
    {
        super();
    }

    @Override
    protected void addContent()
    {
        addMultiblock(staticBuilder(Database.rl("static_test")).
                addPart(new BlockPos(0, 0, 1), Blocks.BIRCH_STAIRS.defaultBlockState()).
                addPart(new BlockPos(0, 1, 0), Blocks.DIAMOND_BLOCK.defaultBlockState()).
                addPart(new BlockPos(1, 0, 0), Blocks.BIRCH_STAIRS.defaultBlockState().
                        setValue(StairBlock.FACING, Direction.SOUTH)).
                end());

        addMultiblock(dynamicBuilder(Database.rl("dynamic_test")).
                setBehavior(DynamicMultiblockDefinition.ScanBehavior.DFS).
                setMaxSize(new BlockPos(3, 6, 3)).
                setAllowedBlockType(Registration.BlockReg.TEST_DYNAMIC_MULTIBLOCK.get()).
                end());

        addMultiblock(dynamicBuilder(Database.rl("fluid_storage")).
                setBehavior(DynamicMultiblockDefinition.ScanBehavior.BFS).
                setMaxSize(2, 3, 2).
                setAllowedBlockType(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.get()).
                end());
    }

    private @NotNull StaticMultiblockBuilder staticBuilder(ResourceLocation location)
    {
        return new StaticMultiblockBuilder(location);
    }

    private @NotNull DynamicMultiblockBuilder dynamicBuilder(ResourceLocation location)
    {
        return new DynamicMultiblockBuilder(location);
    }

    private <T extends IMultiblockDefinition> void addMultiblock(T data)
    {
        this.multiblockDataMap.putIfAbsent(data.getId(), data);
    }

    @Override
    protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
    {
        registrySetBuilder.add(Registration.MultiblockReg.DEFINITION_KEY, context ->
                this.multiblockDataMap.forEach((location, bookChapterData) ->
                        context.register(getMultiblockKey(location), bookChapterData)));
    }

    private @NotNull ResourceKey<IMultiblockDefinition> getMultiblockKey(ResourceLocation location)
    {
        Preconditions.checkNotNull(location);
        return getResourceKey(Registration.MultiblockReg.DEFINITION_KEY, location);
    }
}
