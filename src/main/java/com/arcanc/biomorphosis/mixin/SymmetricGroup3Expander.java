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
import com.mojang.math.SymmetricGroup3;
import net.minecraft.core.Direction;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin (SymmetricGroup3.class)
public abstract class SymmetricGroup3Expander implements VoxelShapeHelper.SymmetricGroup3ExpanderHelper
{
	@Shadow
	public abstract int permutation(int element);
	
	@Override
	public int biomorphosis$permute(int i)
	{
		return switch (i)
		{
			case 0 -> this.permutation(0);
			case 1 -> this.permutation(1);
			case 2 -> this.permutation(2);
			default -> throw new IllegalArgumentException("Must be 0, 1 or 2, but got " + i);
		};
	}
	
	@Override
	public Vector3i biomorphosis$permuteVector(Vector3i vector)
	{
		int v0 = vector.get(this.permutation(0));
		int v1 = vector.get(this.permutation(1));
		int v2 = vector.get(this.permutation(2));
		return vector.set(v0, v1, v2);
	}
	
	@Override
	public Direction.Axis biomorphosis$permuteAxis(Direction.Axis axis)
	{
		return Direction.Axis.VALUES[this.biomorphosis$permute(axis.ordinal())];
	}
}
