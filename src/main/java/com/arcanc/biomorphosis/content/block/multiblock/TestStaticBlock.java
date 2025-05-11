/**
 * @author ArcAnc
 * Created at: 11.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.multiblock.base.static_data.StaticMultiblockPartBlock;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TestStaticBlock extends StaticMultiblockPartBlock<TestStaticBE>
{

    private static final VoxelShape SHAPE = Shapes.or(
            box(0,0,0, 16, 16, 1),
            box(0,0,0, 1, 16, 16),
            box(0,0,0, 16, 1, 16),
            box(15, 1, 1, 16, 16,16),
            box(1, 1, 15, 16, 16, 16));

    public TestStaticBlock(Properties props)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_STATIC_TEST, props);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
