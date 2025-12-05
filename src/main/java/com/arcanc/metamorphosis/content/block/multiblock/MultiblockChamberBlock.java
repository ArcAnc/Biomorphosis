/**
 * @author ArcAnc
 * Created at: 28.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.multiblock;

import com.arcanc.metamorphosis.content.block.multiblock.base.type.StaticMultiblockPartBlock;
import com.arcanc.metamorphosis.content.registration.Registration;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MultiblockChamberBlock extends StaticMultiblockPartBlock<MultiblockChamber>
{

    public MultiblockChamberBlock(Properties props)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_CHAMBER, props);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }
}
