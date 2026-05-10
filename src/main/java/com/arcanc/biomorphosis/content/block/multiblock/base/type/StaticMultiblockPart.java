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
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.definition.PartsMap;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.VoxelShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class StaticMultiblockPart extends BioMultiblockPart implements ServerTickableBE
{
    @Nullable private Map<Direction, VoxelShape> shapeCache;
    
    public StaticMultiblockPart(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    public void generateVoxelShapes(VoxelShape north)
    {
        this.shapeCache = VoxelShapeHelper.rotateHorizontal(north);
    }
    
    public VoxelShape getVoxelShape(Direction dir)
    {
        if (this.shapeCache == null)
        {
            BlockPos masterPos = getMasterPos().orElse(null);
            if (masterPos == null)
                return Shapes.block();
            StaticMultiblockPart master = BlockHelper.castTileEntity(this.level, masterPos, this.getClass()).orElse(null);
            if (master == null)
                return Shapes.block();
            IMultiblockDefinition def = master.getDefinition().orElse(null);
            if (def == null)
               return Shapes.block();
            PartsMap.MultiblockPart part = def.getStructure(this.level, masterPos).
                    getParts().
                    get(this.roleBehavior.getLocalPos().orElse(null));
            if (part == null)
                return Shapes.block();
            generateVoxelShapes(part.shape());
        }
        return this.shapeCache.get(dir);
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
    public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
    }

    @Override
    public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
    }
}
