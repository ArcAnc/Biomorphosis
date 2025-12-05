/**
 * @author ArcAnc
 * Created at: 09.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data;

import com.arcanc.metamorphosis.content.block.multiblock.definition.DynamicMultiblockDefinition;
import com.arcanc.metamorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.multiblock.DynamicMultiblockBuilder;
import com.arcanc.metamorphosis.data.multiblock.StaticMultiblockBuilder;
import com.arcanc.metamorphosis.data.regSetBuilder.MetaRegistryData;
import com.arcanc.metamorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MetaMultiblockProvider extends MetaRegistryData
{
    private final Map<ResourceLocation, IMultiblockDefinition> multiblockDataMap = new HashMap<>();

    public MetaMultiblockProvider()
    {
        super();
    }

    @Override
    protected void addContent()
    {
        addMultiblock(staticBuilder(Database.rl("chamber")).
                addPart(new BlockPos(0, 0, 0), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(1, 0, -1), Registration.BlockReg.NORPHED_DIRT_STAIR_0.get().defaultBlockState().
                        setValue(StairBlock.FACING, Direction.NORTH).
                        setValue(StairBlock.HALF, Half.TOP).
                        setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT)).
                addPart(new BlockPos(-1, 0, -1), Registration.BlockReg.NORPHED_DIRT_STAIR_0.get().defaultBlockState().
                        setValue(StairBlock.FACING, Direction.NORTH).
                        setValue(StairBlock.HALF, Half.TOP).
                        setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)).
                addPart(new BlockPos(1, 0, 1), Registration.BlockReg.NORPHED_DIRT_STAIR_0.get().defaultBlockState().
                        setValue(StairBlock.FACING, Direction.SOUTH).
                        setValue(StairBlock.HALF, Half.TOP).
                        setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT)).
                addPart(new BlockPos(-1, 0, 1), Registration.BlockReg.NORPHED_DIRT_STAIR_0.get().defaultBlockState().
                        setValue(StairBlock.FACING, Direction.SOUTH).
                        setValue(StairBlock.HALF, Half.TOP).
                        setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT)).
                addPart(new BlockPos(1, 1, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 1, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(1, 1, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 1, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(0, 1, 0), Registration.BlockReg.FLESH.get().defaultBlockState()).
                
                addPart(new BlockPos(1, 2, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 2, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(1, 2, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 2, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(0, 2, 0), Registration.BlockReg.FLESH.get().defaultBlockState()).
                
                addPart(new BlockPos(1, 3, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 3, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(1, 3, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 3, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(0, 3, 0), Registration.BlockReg.FLESH.get().defaultBlockState()).
                
                addPart(new BlockPos(1, 4, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 4, -1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(1, 4, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(-1, 4, 1), Registration.BlockReg.FLESH.get().defaultBlockState()).
                addPart(new BlockPos(0, 4, 0), Registration.BlockReg.FLESH.get().defaultBlockState()).
                
                setPlacedBlock(Registration.BlockReg.MULTIBLOCK_CHAMBER.get().defaultBlockState()).
                end());

        addMultiblock(dynamicBuilder(Database.rl("fluid_storage")).
                setBehavior(DynamicMultiblockDefinition.ScanBehavior.BFS).
                setMaxSize(2, 3, 2).
                setAllowedBlockType(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.get().defaultBlockState()).
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
