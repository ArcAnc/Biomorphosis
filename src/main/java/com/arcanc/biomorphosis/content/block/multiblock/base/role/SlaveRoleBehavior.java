/**
 * @author ArcAnc
 * Created at: 15.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base.role;

import com.arcanc.biomorphosis.content.block.multiblock.base.BioMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SlaveRoleBehavior implements IMultiblockRoleBehavior
{

    private final BioMultiblockPart part;
    private BlockPos masterPos;

    public SlaveRoleBehavior(BioMultiblockPart part)
    {
        this.part = part;
    }

    public SlaveRoleBehavior setMasterPos(@Nullable BlockPos pos)
    {
        this.masterPos = pos;
        return this;
    }

    @Override
    public boolean isMaster()
    {
        return false;
    }

    @Override
    public Optional<BlockPos> getMasterPos()
    {
        return Optional.ofNullable(this.masterPos);
    }

    @Override
    public Optional<BlockPos> getLocalPos()
    {
        return getMasterPos().map(pos ->
        {
            Level level = this.part.getLevel();
            if (level == null)
                return null;
            BlockPos toReturn = this.part.getBlockPos().subtract(pos);
            BlockState state = level.getBlockState(pos);
            if (state.hasProperty(MultiblockPartBlock.HORIZONTAL_FACING))
                if (state.getValue(MultiblockPartBlock.HORIZONTAL_FACING) == Direction.NORTH)
                    return toReturn;
                else
                    return DirectionHelper.rotatePosition(toReturn, state.getValue(MultiblockPartBlock.HORIZONTAL_FACING).getOpposite());

            return toReturn;
        });
    }

    @Override
    public <T, C> @Nullable T getCapability(BlockCapability<T, C> capability, C context)
    {
        Level level = this.part.getLevel();
        if (level == null)
            return null;
        return getMasterPos().map(pos -> level.getCapability(capability, pos, context)).orElse(null);
    }
}
