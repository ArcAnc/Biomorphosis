/**
 * @author ArcAnc
 * Created at: 15.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.multiblock.base.role;

import com.arcanc.metamorphosis.content.block.multiblock.base.MetaMultiblockPart;
import com.arcanc.metamorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IMultiblockRoleBehavior
{
    boolean isMaster();

    Optional<BlockPos> getMasterPos();

    Optional<BlockPos> getLocalPos();

    <T, C extends @Nullable Object> @Nullable T getCapability(BlockCapability<T, C> capability, C context);

    static @NotNull CompoundTag save(@NotNull IMultiblockRoleBehavior behavior)
    {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("is_master", behavior.isMaster());
        behavior.getMasterPos().ifPresent(pos -> TagHelper.writeBlockPos(pos, tag, "master_pos"));
        return tag;
    }

    static IMultiblockRoleBehavior load(@NotNull CompoundTag tag, MetaMultiblockPart part)
    {
        boolean isMaster = tag.getBoolean("is_master");
        if (isMaster)
            return new MasterRoleBehavior(part);
        else
            return new SlaveRoleBehavior(part).setMasterPos(TagHelper.readBlockPos(tag, "master_pos"));
    }

}
