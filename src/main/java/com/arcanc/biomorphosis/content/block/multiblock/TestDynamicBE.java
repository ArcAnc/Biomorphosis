/**
 * @author ArcAnc
 * Created at: 14.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.block_entity.BioFluidStorage;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.DynamicMultiblockPart;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestDynamicBE extends DynamicMultiblockPart
{


    public TestDynamicBE(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_DYNAMIC_TEST.get(), pos, blockState);
    }

    @Override
    protected void firstTick() {

    }

    @Override
    protected void transferRequiredData(DynamicMultiblockPart target) {

    }

    @Override
    protected void updateCapabilities()
    {

    }
}
