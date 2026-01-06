/**
 * @author ArcAnc
 * Created at: 09.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.definition.PartsMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.StaticMultiblockDefinition;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticMultiblockBuilder
{
    private final ResourceLocation location;
    private final Map<BlockPos, PartsMap.MultiblockPart> partsMap = new HashMap<>();
    private BlockState placedBlock;

    public StaticMultiblockBuilder(ResourceLocation location)
    {
        Preconditions.checkNotNull(location);
        this.location = location;
    }

    public StaticMultiblockBuilder addPart(BlockPos pos, VoxelShape shape, IngredientWithSize... ingredients)
    {
        Preconditions.checkNotNull(pos);
		Preconditions.checkNotNull(shape);
        Preconditions.checkNotNull(ingredients);
        this.addPart(pos, new PartsMap.MultiblockPart(shape, List.of(ingredients)));
        return this;
    }
	
	public StaticMultiblockBuilder addPart(BlockPos pos, PartsMap.MultiblockPart part)
	{
		Preconditions.checkNotNull(pos);
		Preconditions.checkNotNull(part);
		this.partsMap.putIfAbsent(pos, part);
		return this;
	}
	
    public StaticMultiblockBuilder addParts(Map<BlockPos, PartsMap.MultiblockPart> partsMap)
    {
        Preconditions.checkNotNull(partsMap);
        if (partsMap.isEmpty())
            return this;
        partsMap.forEach(this :: addPart);
        return this;
    }
    
    public StaticMultiblockBuilder setPlacedBlock(BlockState state)
    {
        Preconditions.checkNotNull(state);
        this.placedBlock = state;
        return this;
    }

    public StaticMultiblockDefinition end()
    {
        return new StaticMultiblockDefinition(this.location, new PartsMap(this.partsMap, this.placedBlock));
    }
}
