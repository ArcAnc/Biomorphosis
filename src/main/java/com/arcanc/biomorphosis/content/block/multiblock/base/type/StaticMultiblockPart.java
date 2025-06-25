/**
 * @author ArcAnc
 * Created at: 10.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base.type;

import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.block.multiblock.base.BioMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class StaticMultiblockPart extends BioMultiblockPart implements ServerTickableBE
{

    public StaticMultiblockPart(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    @Override
    public void tickServer()
    {
        if (this.level == null)
            return;

        MultiblockState state = getBlockState().getValue(MultiblockPartBlock.STATE);

        if ((state == MultiblockState.MORPHING || state == MultiblockState.FORMED) &&
            isMultiblockStillValid())
        {
            multiblockServerTick();
            return;
        }

        disassembleMultiblock();
    }

    @Override
    protected void tryFormMultiblock(Level level)
    {
    }

    protected abstract void multiblockServerTick();

    @Override
    protected boolean isMultiblockStillValid()
    {
        if (this.level == null)
            return true;
        if (!isMaster())
            return true;
        List<Pair<BlockPos, BlockState>> structure = this.definition.getStructure(getLevel(), getBlockPos()).getStates().
                entrySet().
                stream().
                filter(entry -> !entry.getKey().equals(BlockPos.ZERO)).
                map(entry ->
                {
                    Direction dir = DirectionHelper.getFace(getBlockState());
                    BlockPos rotatedPos = DirectionHelper.rotatePosition(entry.getKey(), dir);
                    return Pair.of(rotatedPos, entry.getValue().rotate(DirectionHelper.rotationFromNorth(dir)));
                }).
                toList();

        if (structure.isEmpty())
            return false;

        for (Pair<BlockPos, BlockState> pair : structure)
        {
            BlockPos realPos = pair.getFirst().offset(getBlockPos());
            if (!this.level.isLoaded(realPos))
                return false;
            BlockState toCheck = this.level.getBlockState(realPos);
            if (!BlockHelper.statesEquivalent(pair.getSecond(), toCheck))
                return false;
        }
        return true;
    }

    @Override
    public void onDisassemble()
    {
        if (this.level == null)
            return;
        if (!isMaster())
            return;

        List<Pair<BlockPos, BlockState>> structure = this.definition.getStructure(getLevel(), getBlockPos()).getStates().
                entrySet().
                stream().
                filter(entry -> !entry.getKey().equals(BlockPos.ZERO)).
                map(entry ->
                {
                    Direction dir = DirectionHelper.getFace(getBlockState());
                    BlockPos rotatedPos = DirectionHelper.rotatePosition(entry.getKey(), dir);
                    return Pair.of(rotatedPos, entry.getValue().rotate(DirectionHelper.rotationFromNorth(dir)));
                }).
                toList();

        if (structure.isEmpty())
            return;

        for (Pair<BlockPos, BlockState> pair : structure)
        {
            BlockPos realPos = pair.getFirst().offset(getBlockPos());
            if (!this.level.isLoaded(realPos))
                continue;
            BlockState toCheck = this.level.getBlockState(realPos);
            if (!BlockHelper.statesEquivalent(pair.getSecond(), toCheck))
                continue;
            this.level.destroyBlock(realPos, true);
        }

        this.level.destroyBlock(getBlockPos(), true);
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
    }
}
