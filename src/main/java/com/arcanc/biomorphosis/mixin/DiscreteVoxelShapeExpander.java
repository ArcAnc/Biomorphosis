/**
 * @author ArcAnc
 * Created at: 08.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;


import com.arcanc.biomorphosis.util.helper.VoxelShapeHelper;
import com.mojang.math.OctahedralGroup;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin (DiscreteVoxelShape.class)
public abstract class DiscreteVoxelShapeExpander implements VoxelShapeHelper.DiscreteVoxelShapeExpanderHelper
{
	@Shadow
	@Final
	protected int xSize;
	
	@Shadow
	@Final
	protected int ySize;
	
	@Shadow
	@Final
	protected int zSize;
	
	@Shadow
	public abstract boolean isFull(int x, int y, int z);
	
	@Override
	public DiscreteVoxelShape biomorphosis$rotate(OctahedralGroup rotation)
	{
		if (rotation == OctahedralGroup.IDENTITY)
			return (DiscreteVoxelShape) (Object)this;
		else {
			Vector3i v = ((VoxelShapeHelper.OctahedralGroupExpanderHelper)(Object)rotation).biomorphosis$rotate(new Vector3i(this.xSize, this.ySize, this.zSize));
			int shiftX = biomorphosis$fixupCoordinate(v, 0);
			int shiftY = biomorphosis$fixupCoordinate(v, 1);
			int shiftZ = biomorphosis$fixupCoordinate(v, 2);
			DiscreteVoxelShape newShape = new BitSetDiscreteVoxelShape(v.x, v.y, v.z);
			
			for(int x = 0; x < this.xSize; ++x)
			{
				for(int y = 0; y < this.ySize; ++y)
				{
					for(int z = 0; z < this.zSize; ++z)
					{
						if (this.isFull(x, y, z))
						{
							Vector3i newPos = ((VoxelShapeHelper.OctahedralGroupExpanderHelper)(Object)rotation).biomorphosis$rotate(v.set(x, y, z));
							int newX = shiftX + newPos.x;
							int newY = shiftY + newPos.y;
							int newZ = shiftZ + newPos.z;
							newShape.fill(newX, newY, newZ);
						}
					}
				}
			}
			
			return newShape;
		}
	}
	
	@Unique
	private static int biomorphosis$fixupCoordinate(Vector3i v, int index)
	{
		int value = v.get(index);
		if (value < 0)
		{
			v.setComponent(index, -value);
			return -value - 1;
		}
		else
			return 0;
	}
}
