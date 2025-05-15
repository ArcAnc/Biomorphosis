/**
 * @author ArcAnc
 * Created at: 14.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.base.type.DynamicMultiblockPartBlock;
import com.arcanc.biomorphosis.content.registration.Registration;

public class TestDynamicBlock extends DynamicMultiblockPartBlock<TestDynamicBE>
{
    public TestDynamicBlock(Properties props)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_DYNAMIC_TEST, props);
    }
}
