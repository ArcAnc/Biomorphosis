/**
 * @author ArcAnc
 * Created at: 11.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.base.static_data.StaticMultiblockPart;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TestStaticBE extends StaticMultiblockPart
{
    public TestStaticBE(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_STATIC_TEST.get(), pos, blockState);
    }

    @Override
    protected void serverTick()
    {

    }

    @Override
    protected void firstTick() {

    }
}
