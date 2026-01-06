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
import com.arcanc.biomorphosis.util.helper.BioCodecs;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartsMap
{
    public static final Codec<PartsMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(BioCodecs.BLOCK_POS_JSON_CODEC, MultiblockPart.CODEC).
                fieldOf("parts").
                forGetter(PartsMap :: getParts),
            BlockState.CODEC.
                fieldOf("placedBlock").
                forGetter(PartsMap :: getPlacedBlock)).
            apply(instance, PartsMap :: new));
    private final Map<BlockPos, MultiblockPart> partsMap;
    private final BlockState placedBlock;
    private final BlockPos size;
    private final List<IngredientWithSize> structure;
    
    public PartsMap(Map<BlockPos, MultiblockPart> map, BlockState placedBlock)
    {
        Preconditions.checkNotNull(map);
        this.partsMap = map;
        this.placedBlock = placedBlock;
        this.size = calculateSize();
        this.structure = calculateStructure();
    }

    public Map<BlockPos, MultiblockPart> getParts()
    {
        return this.partsMap;
    }
    
    public BlockState getPlacedBlock()
    {
        return placedBlock;
    }
    
    public List<IngredientWithSize> getStructure()
    {
        return this.structure;
    }

    public BlockPos getSize()
    {
        return this.size;
    }

    private @NotNull @Unmodifiable List<IngredientWithSize> calculateStructure()
    {
        Map<Ingredient, Integer> parts = new HashMap<>();
        this.partsMap.forEach((pos, part) ->
                part.ingredients().forEach(ingredientWithSize ->
		                parts.compute(ingredientWithSize.ingredient(), (key, amount) ->
                        {
	                            if (amount == null)
		                            return ingredientWithSize.amount();
	                            return amount + ingredientWithSize.amount();
                        })));
        return parts.entrySet().stream().
		        map(entry ->
		                new IngredientWithSize(entry.getKey(), entry.getValue())).toList();
    }

    private BlockPos calculateSize()
    {
        if (this.partsMap.isEmpty())
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
        this.partsMap.keySet().forEach(pos ->
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

	public record MultiblockPart(VoxelShape shape, List<IngredientWithSize> ingredients)
	{
		public static final Codec<MultiblockPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
						BioCodecs.VOXEL_SHAPE_CODEC.fieldOf("shape").forGetter(MultiblockPart :: shape),
						IngredientWithSize.CODEC.codec().listOf().fieldOf("ingredients").forGetter(MultiblockPart :: ingredients)).
				apply(instance, MultiblockPart :: new));
		
		public MultiblockPart(VoxelShape shape, IngredientWithSize ingredient)
		{
			this(shape, List.of(ingredient));
		}
	}
}
