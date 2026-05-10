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
import com.mojang.math.SymmetricGroup3;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin (OctahedralGroup.class)
public class OctahedralGroupExpander implements VoxelShapeHelper.OctahedralGroupExpanderHelper
{
	
	@Shadow
	@Final
	private SymmetricGroup3 permutation;
	
	@Shadow
	@Final
	private boolean invertX;
	
	@Shadow
	@Final
	private boolean invertY;
	
	@Shadow
	@Final
	private boolean invertZ;
	
	@Override
	public Vector3i biomorphosis$rotate(Vector3i vector)
	{
		((VoxelShapeHelper.SymmetricGroup3ExpanderHelper)(Object)(this.permutation)).biomorphosis$permuteVector(vector);
		vector.x *= this.invertX ? -1 : 1;
		vector.y *= this.invertY ? -1 : 1;
		vector.z *= this.invertZ ? -1 : 1;
		return vector;
	}
}
