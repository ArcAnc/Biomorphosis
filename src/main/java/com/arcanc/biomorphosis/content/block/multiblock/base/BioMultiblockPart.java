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
import com.arcanc.biomorphosis.content.block.multiblock.base.role.IMultiblockRoleBehavior;
import com.arcanc.biomorphosis.content.block.multiblock.base.role.MasterRoleBehavior;
import com.arcanc.biomorphosis.content.block.multiblock.base.role.SlaveRoleBehavior;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class BioMultiblockPart extends BioBaseBlockEntity
{
    protected IMultiblockDefinition definition;
    protected IMultiblockRoleBehavior roleBehavior;

    public BioMultiblockPart(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    public Optional<IMultiblockDefinition> getDefinition()
    {
        return Optional.ofNullable(this.definition);
    }

    public Optional<BlockPos> getMasterPos()
    {
        return this.roleBehavior.getMasterPos();
    }

    public boolean isMultiblockPart()
    {
        return this.roleBehavior != null;
    }

    public boolean isMaster()
    {
        return this.roleBehavior.isMaster();
    }

    protected IMultiblockRoleBehavior setRoleBehavior(@NotNull BlockPos masterPos)
    {
        return masterPos.equals(getBlockPos()) ? new MasterRoleBehavior(this) : new SlaveRoleBehavior(this).setMasterPos(masterPos);
    }

    protected void markAsPartOfMultiblock(BlockPos masterPos)
    {
        this.roleBehavior = setRoleBehavior(masterPos);
        setMultiblockState(MultiblockState.FORMED);
        markDirty();
    }

    protected void startMorphing(BlockPos masterPos)
    {
        this.roleBehavior = setRoleBehavior(masterPos);
        setMultiblockState(MultiblockState.MORPHING);
        markDirty();
    }

    protected void resetMultiblockState()
    {
        this.roleBehavior = null;
        setMultiblockState(MultiblockState.DISASSEMBLED);
        markDirty();
    }

    protected void setMultiblockState(MultiblockState state)
    {
        if (this.level == null)
            return;
        BlockPos pos = getBlockPos();
        BlockState current = this.level.getBlockState(pos);
        if (current.getBlock() instanceof MultiblockPartBlock<?>)
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
        if (tag.contains("role"))
            this.roleBehavior = IMultiblockRoleBehavior.load(tag.getCompound("role"), this);
        if (tag.contains("definition"))
        {
            ResourceLocation location = ResourceLocation.parse(tag.getString("definition"));
            this.definition = registries.lookup(Registration.MultiblockReg.DEFINITION_KEY).
                    flatMap(registry -> registry.
                            get(ResourceKey.create(Registration.MultiblockReg.DEFINITION_KEY, location)).
                            map(Holder.Reference :: value)).
            orElse(null);
        }
        else
            this.definition = null;
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        if (this.roleBehavior != null)
            tag.put("role", IMultiblockRoleBehavior.save(this.roleBehavior));
        getDefinition().ifPresent(definition -> tag.putString("definition", definition.getId().toString()));
    }
}
