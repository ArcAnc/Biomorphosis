/**
 * @author ArcAnc
 * Created at: 07.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class BioCodecs
{
    public static final Codec<BlockPos> BLOCK_POS_JSON_CODEC = Codec.STRING.xmap(
            str ->
            {
                String[] parts = str.split(",");
                return new BlockPos(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim()));
            },
            Vec3i::toShortString);
	
	public static final Codec<DoubleArrayList> DOUBLE_LIST_CODEC = Codec.DOUBLE.listOf().
			xmap(DoubleArrayList :: new, Function.identity());
	
	public static final Codec<VoxelShape> VOXEL_SHAPE_CODEC = DOUBLE_LIST_CODEC.flatXmap(doubles ->
			{
				if (doubles.size() % 6  != 0)
					return DataResult.error(() -> "Wrong data size for VoxelShape. VoxelShape data size must be divisible by 6, got " + doubles.size());
		
				if (doubles.isEmpty())
					return DataResult.success(Shapes.empty());
		
				VoxelShape shape = Shapes.empty();
		
				for (int q = 0; q < doubles.size(); q += 6)
				{
					double minX = doubles.getDouble(q);
					double minY = doubles.getDouble(q + 1);
					double minZ = doubles.getDouble(q + 2);
					double maxX = doubles.getDouble(q + 3);
					double maxY = doubles.getDouble(q + 4);
					double maxZ = doubles.getDouble(q + 5);
			
					shape = Shapes.joinUnoptimized(shape, Shapes.box(minX, minY, minZ, maxX, maxY, maxZ), BooleanOp.OR);
				}
		
				return DataResult.success(shape.optimize());
			},
			voxelShape ->
			{
				DoubleArrayList list = DoubleArrayList.of();
				
				voxelShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
				{
					list.add(minX);
					list.add(minY);
					list.add(minZ);
					list.add(maxX);
					list.add(maxY);
					list.add(maxZ);
				});
				
				return DataResult.success(list);
			});

}
