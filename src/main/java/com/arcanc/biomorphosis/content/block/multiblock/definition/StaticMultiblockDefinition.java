/**
 * @author ArcAnc
 * Created at: 07.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class StaticMultiblockDefinition implements IMultiblockDefinition
{
    public static final MapCodec<StaticMultiblockDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(StaticMultiblockDefinition :: getId),
            BlockStateMap.CODEC.fieldOf("structure").forGetter(StaticMultiblockDefinition :: getStructure)).
            apply(instance, StaticMultiblockDefinition::new));

    private final ResourceLocation id;
    private final BlockStateMap structure;

    public StaticMultiblockDefinition(ResourceLocation id, BlockStateMap map)
    {
        this.id = id;
        this.structure = map;

    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    public BlockStateMap getStructure()
    {
        return this.structure;
    }

    @Override
    public BlockStateMap getStructure(Level level, BlockPos origin)
    {
        return getStructure();
    }

    @Override
    public MultiblockType type()
    {
        return MultiblockType.STATIC;
    }

    @Override
    public BlockPos size()
    {
        return this.structure.getSize();
    }
}
