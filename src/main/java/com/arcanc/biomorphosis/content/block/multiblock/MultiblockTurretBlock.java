/**
 * @author ArcAnc
 * Created at: 17.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;


import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPartBlock;
import com.arcanc.biomorphosis.content.registration.Registration;

public class MultiblockTurretBlock extends StaticMultiblockPartBlock<MultiblockTurret>
{
	public MultiblockTurretBlock(Properties props)
	{
		super(Registration.BETypeReg.BE_MULTIBLOCK_TURRET, props);
	}
}
