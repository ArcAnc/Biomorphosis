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
import com.arcanc.biomorphosis.content.block.multiblock.definition.PartsMap;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        PartsMap map = this.definition.getStructure(getLevel(), getBlockPos());
        
        BlockState placedState = map.getPlacedBlock();
        
        Set<BlockPos> structure = map.getParts().keySet();

        if (structure.isEmpty())
            return false;

        for (BlockPos pos : structure)
        {
            BlockPos realPos = pos.offset(getBlockPos());
            if (!this.level.isLoaded(realPos))
                return false;
            BlockState toCheck = this.level.getBlockState(realPos);
            if (!toCheck.is(placedState.getBlock()))
            //if (!BlockHelper.statesEquivalent(placedState, toCheck))
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

		PartsMap map = this.definition.getStructure(getLevel(), getBlockPos());
	    BlockState placedState = map.getPlacedBlock();
		
	    Set<BlockPos> structure = map.getParts().
		        keySet().
                stream().
                filter(blockPos -> ! blockPos.equals(BlockPos.ZERO)).
                collect(Collectors.toUnmodifiableSet());

        if (structure.isEmpty())
            return;
	    
        
		for (BlockPos pos : structure)
        {
            BlockPos realPos = pos.offset(getBlockPos());
            if (!this.level.isLoaded(realPos))
                continue;
            BlockState toCheck = this.level.getBlockState(realPos);
            if (!toCheck.is(placedState.getBlock()))
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
