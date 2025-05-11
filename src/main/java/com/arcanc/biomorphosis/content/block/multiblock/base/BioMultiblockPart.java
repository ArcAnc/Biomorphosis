/**
 * @author ArcAnc
 * Created at: 10.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base;

import com.arcanc.biomorphosis.content.block.block_entity.BioBaseBlockEntity;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ClientTickableBE;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.LazyDataLoader;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class BioMultiblockPart extends BioBaseBlockEntity implements ServerTickableBE, ClientTickableBE
{
    @Nullable
    protected BlockPos masterPos;
    protected Predicate<BlockPos> isMaster = pos -> getMasterPos().map(mPos -> mPos.equals(pos)).orElse(false);
    protected IMultiblockDefinition definition;

    private LazyDataLoader<BioMultiblockPart> dataLoader;

    public BioMultiblockPart(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    @Override
    public void tickClient()
    {
        loadDefinition();
    }

    @Override
    public void tickServer()
    {
        if (this.level == null)
            return;

        loadDefinition();

        if (!isMultiblockPart())
            tryFormMultiblock();
        else if (!isMultiblockStillValid() && getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED)
            disassembleMultiblock();
    }

    private void loadDefinition()
    {
        if (this.dataLoader != null)
        {
            this.dataLoader.loadData(this);
            this.dataLoader = null;
            if (!this.level.isClientSide())
                markDirty();
        }
    }

    public Optional<IMultiblockDefinition> getDefinition()
    {
        return Optional.ofNullable(this.definition);
    }

    public Optional<BlockPos> getMasterPos()
    {
        return Optional.ofNullable(this.masterPos);
    }

    public boolean isMultiblockPart()
    {
        return this.masterPos != null;
    }

    public boolean isMaster(@NotNull BlockPos pos)
    {
        return this.isMaster.test(pos);
    }

    protected void markAsPartOfMultiblock(BlockPos masterPos)
    {
        this.masterPos = masterPos;
        setMultiblockState(MultiblockState.FORMED);
        markDirty();
    }

    protected void startMorphing(BlockPos masterPos)
    {
        this.masterPos = masterPos;
        setMultiblockState(MultiblockState.MORPHING);
        markDirty();
    }

    protected void resetMultiblockState()
    {
        this.masterPos = null;
        setMultiblockState(MultiblockState.DISASSEMBLED);
        markDirty();
    }

    protected void setMultiblockState(MultiblockState state)
    {
        if (this.level == null)
            return;
        BlockPos pos = getBlockPos();
        BlockState current = this.level.getBlockState(pos);
        if (current.getBlock() instanceof MultiblockPartBlock<?> block)
            this.level.setBlockAndUpdate(pos, current.setValue(MultiblockPartBlock.STATE, state));
    }

    protected void disassembleMultiblock()
    {
        onDisassemble();
        resetMultiblockState();
    }

    protected boolean isConnectedToNorph()
    {
        if (this.level == null)
            return false;
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof MultiblockPartBlock<?> block))
            return false;
        return block.isConnectedToNorph(this.level, getBlockPos(), state);
    }

    protected MultiblockState getMultiblockState()
    {
        return this.level != null ?
                getMasterPos().
                        map(pos -> this.level.getBlockState(pos).getValue(MultiblockPartBlock.STATE)).
                        orElse(MultiblockState.DISASSEMBLED) :
                MultiblockState.DISASSEMBLED;

    }

    protected abstract void tryFormMultiblock();

    protected abstract boolean isMultiblockStillValid();

    protected abstract void onDisassemble();

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        if (tag.contains("master_pos"))
            this.masterPos = TagHelper.readBlockPos(tag, "master_pos");
        if (tag.contains("definition"))
            this.dataLoader = blockEntity ->
            {
                Level level = blockEntity.getLevel();
                this.definition = level.registryAccess().lookup(Registration.MultiblockReg.DEFINITION_KEY).
                        map(registry -> registry.get(ResourceLocation.parse(tag.getString("definition"))).
                                orElseThrow().
                                value()).orElse(null);
            };
        else
            this.definition = null;
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        getMasterPos().ifPresent(pos -> TagHelper.writeBlockPos(pos, tag, "master_pos"));
        getDefinition().ifPresent(definition -> tag.putString("definition", definition.getId().toString()));
    }
}
