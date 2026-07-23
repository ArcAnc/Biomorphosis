/**
 * @author ArcAnc
 * Created at: 14.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base.type;

import com.arcanc.biomorphosis.content.block.multiblock.base.BioMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.block.multiblock.definition.DynamicMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.content.block.multiblock.definition.PartsMap;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class DynamicMultiblockPart extends BioMultiblockPart implements ServerTickableBE
{
    private final Set<BlockPos> formedParts = new HashSet<>();
    private int patternRefreshDelay;

    public DynamicMultiblockPart(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    public void onPlace(ServerLevel level, BlockPos pos, BlockState state)
    {
        if (state.hasProperty(MultiblockPartBlock.STATE) && state.getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED)
            return;

        this.definition = level.registryAccess().lookup(Registration.MultiblockReg.DEFINITION_KEY).
                flatMap(registry -> registry.filterElements(definition ->
                {
                    if (definition.type() == MultiblockType.STATIC)
                        return false;
                    DynamicMultiblockDefinition dynDef = (DynamicMultiblockDefinition) definition;
                    return dynDef.accepts(this.getBlockState());
                }).
                listElements().
                findFirst().
                map(Holder.Reference::value)).
                orElse(null);
        
        markDirty();

		if (!(this.definition instanceof DynamicMultiblockDefinition dynDefinition))
            return;

        BlockPos masterPos = dynDefinition.getMode() == DynamicMultiblockDefinition.Mode.PATTERN ?
                dynDefinition.findPatternOrigin(level, pos).orElse(null) : pos;
        if (masterPos == null)
            return;

        PartsMap map = dynDefinition.getStructure(level, masterPos);

        if (map.getParts().isEmpty())
            return;

        formMultiblock(level, dynDefinition, map, masterPos);
    }

    private void formMultiblock(ServerLevel level,
                                DynamicMultiblockDefinition definition,
                                PartsMap map,
                                BlockPos defaultMasterPos)
    {
        Set<BlockPos> formerMasters = new HashSet<>();
        for (BlockPos localPos : map.getParts().keySet())
        {
            BlockPos realPos = defaultMasterPos.offset(localPos);
            BlockHelper.castTileEntity(level, realPos, DynamicMultiblockPart.class).ifPresent(part ->
            {
                if (part.isMaster())
                    formerMasters.add(realPos);
            });
        }

        BlockPos masterPos = defaultMasterPos;
        DynamicMultiblockPart master = BlockHelper.castTileEntity(level, masterPos, DynamicMultiblockPart.class).orElse(null);
        if (master == null)
            return;

        for (BlockPos localPos : map.getParts().keySet())
        {
            BlockPos realPos = defaultMasterPos.offset(localPos);
            BlockHelper.castTileEntity(level, realPos, DynamicMultiblockPart.class).ifPresent(part ->
            {
                part.setDefinition(definition);
                part.markAsPartOfMultiblock(masterPos);
            });
        }
        if (definition.getMode() == DynamicMultiblockDefinition.Mode.PATTERN)
            master.setFormedParts(map.getParts().keySet().stream().map(defaultMasterPos :: offset).toList());
        master.updateCapabilities();

        for (BlockPos formerMasterPos : formerMasters)
            if (definition.getMode() == DynamicMultiblockDefinition.Mode.CONNECTED && !formerMasterPos.equals(masterPos))
                BlockHelper.castTileEntity(level, formerMasterPos, DynamicMultiblockPart.class).
                        ifPresent(formerMaster -> formerMaster.transferRequiredData(master));
    }

    private void setFormedParts(Iterable<BlockPos> parts)
    {
        this.formedParts.clear();
        parts.forEach(this.formedParts :: add);
        markDirty();
    }

    @Override
    public void tickServer()
    {
        if (this.level == null || this.level.isClientSide() || ++this.patternRefreshDelay < 10)
            return;
        this.patternRefreshDelay = 0;

        if (!(this.definition instanceof DynamicMultiblockDefinition definition) ||
            definition.getMode() != DynamicMultiblockDefinition.Mode.PATTERN ||
            !getBlockState().is(definition.getPattern().orElseThrow().anchor().getBlock()))
            return;

        PartsMap structure = definition.getStructure(this.level, getBlockPos());
        Set<BlockPos> currentParts = structure.getParts().keySet().stream().
                map(getBlockPos() :: offset).
                collect(java.util.stream.Collectors.toSet());
        if (currentParts.equals(this.formedParts))
            return;

        if (structure.getParts().isEmpty())
        {
            this.formedParts.stream().
                    map(partPos -> BlockHelper.castTileEntity(this.level, partPos, DynamicMultiblockPart.class)).
                    flatMap(Optional :: stream).
                    forEach(BioMultiblockPart :: resetMultiblockState);
            this.formedParts.clear();
            markDirty();
            return;
        }
        formMultiblock((ServerLevel)this.level, definition, structure, getBlockPos());
    }

    public void onRemove(ServerLevel level, BlockPos pos, BlockState state)
    {
        if (this.definition instanceof DynamicMultiblockDefinition dynamicDefinition &&
            dynamicDefinition.getMode() == DynamicMultiblockDefinition.Mode.PATTERN)
        {
            DynamicMultiblockPart master = getMasterPos().
                    flatMap(masterPos -> BlockHelper.castTileEntity(level, masterPos, DynamicMultiblockPart.class)).
                    orElse(this);
            master.formedParts.stream().
                    filter(partPos -> !partPos.equals(pos)).
                    forEach(partPos -> BlockHelper.castTileEntity(level, partPos, DynamicMultiblockPart.class).
                            ifPresent(BioMultiblockPart :: resetMultiblockState));
            master.formedParts.clear();
            master.markDirty();
            return;
        }

        if (!(this.definition instanceof DynamicMultiblockDefinition dynamicDefinition))
            return;

        Set<BlockPos> rebuiltParts = new HashSet<>();
        List<DynamicMultiblockPart> newMasters = new ArrayList<>();
        for (Direction dir : Direction.values())
        {
            BlockPos startPos = pos.relative(dir);
            if (!state.is(level.getBlockState(startPos).getBlock()))
                continue;

            PartsMap map = dynamicDefinition.getStructure(level, startPos);
            if (map.getParts().isEmpty())
                continue;

            Set<BlockPos> component = map.getParts().keySet().stream().
                    map(startPos :: offset).
                    collect(java.util.stream.Collectors.toSet());
            if (!java.util.Collections.disjoint(rebuiltParts, component))
                continue;

            rebuiltParts.addAll(component);
            formMultiblock(level, dynamicDefinition, map, startPos);
            BlockHelper.castTileEntity(level, startPos, DynamicMultiblockPart.class).ifPresent(newMasters :: add);
        }

        if (isMaster() && !newMasters.isEmpty())
        {
            distributeRequiredData(newMasters);
            newMasters.forEach(DynamicMultiblockPart :: updateCapabilities);
        }
    }

    protected abstract void transferRequiredData(DynamicMultiblockPart target);

    protected void distributeRequiredData(List<DynamicMultiblockPart> targets)
    {
        targets.stream().findFirst().ifPresent(this :: transferRequiredData);
    }

    @Override
    protected void tryFormMultiblock(Level level)
    {
    }

    @Override
    protected boolean isMultiblockStillValid()
    {
        return true;
    }

    @Override
    public void onDisassemble()
    {

    }

    protected abstract void updateCapabilities();

    @Override
    public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        this.formedParts.clear();
        if (tag.contains("dynamic_parts"))
            for (long packedPos : tag.getLongArray("dynamic_parts"))
                this.formedParts.add(BlockPos.of(packedPos));
    }

    @Override
    public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        if (isMaster() && !this.formedParts.isEmpty())
            tag.putLongArray("dynamic_parts", this.formedParts.stream().mapToLong(BlockPos :: asLong).toArray());
    }

}
