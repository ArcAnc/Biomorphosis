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
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MasterRoleBehavior implements IMultiblockRoleBehavior
{
    private final BioMultiblockPart part;

    public MasterRoleBehavior(BioMultiblockPart part)
    {
        this.part = part;
    }

    @Override
    public boolean isMaster()
    {
        return true;
    }

    @Override
    public Optional<BlockPos> getMasterPos()
    {
        return Optional.of(this.part.getBlockPos());
    }

    @Override
    public Optional<BlockPos> getLocalPos()
    {
        return Optional.of(BlockPos.ZERO);
    }

    @Override
    public <T, C> @Nullable T getCapability(BlockCapability<T, C> capability, C context)
    {
        if (this.part.getLevel() == null)
            return null;
        return this.part.getLevel().getCapability(capability, this.part.getBlockPos(), this.part.getBlockState(), this.part, context);
    }
}
