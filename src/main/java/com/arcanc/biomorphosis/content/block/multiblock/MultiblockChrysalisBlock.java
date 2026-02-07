/**
 * @author ArcAnc
 * Created at: 15.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;


import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPartBlock;
import com.arcanc.biomorphosis.content.registration.Registration;

public class MultiblockChrysalisBlock extends StaticMultiblockPartBlock<MultiblockChrysalis>
{
	public MultiblockChrysalisBlock(Properties props)
	{
		super(Registration.BETypeReg.BE_MULTIBLOCK_CHRYSALIS, props);
	}
}
