/**
 * @author ArcAnc
 * Created at: 08.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;


import com.mojang.math.OctahedralGroup;
import com.mojang.math.SymmetricGroup3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin (OctahedralGroup.class)
public interface OctahedralGroupAccessor
{
	@Accessor
	SymmetricGroup3 getPermutation();
}
