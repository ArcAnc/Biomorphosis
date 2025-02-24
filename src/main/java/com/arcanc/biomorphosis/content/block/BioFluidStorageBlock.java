/**
 * @author ArcAnc
 * Created at: 22.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.BioFluidStorage;
import com.mojang.serialization.MapCodec;

public class BioFluidStorageBlock extends BioNorphDependentBlock<BioFluidStorage>
{

    public static final MapCodec<BioFluidStorageBlock> CODEC = simpleCodec(BioFluidStorageBlock :: new);

    public BioFluidStorageBlock(Properties blockProps)
    {
        super(BioFluidStorage :: new, blockProps);
    }
}
