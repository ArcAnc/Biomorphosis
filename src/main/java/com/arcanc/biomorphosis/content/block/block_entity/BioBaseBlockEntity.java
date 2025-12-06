/**
 * @author ArcAnc
 * Created at: 31.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This code was partially taken from <a href="https://github.com/BluSunrize/ImmersiveEngineering/tree/1.21.1">Immersive Engineering</a>
 * <p>BluSunrize is original author. Yes, bro, you are not forgotten</p>
 * <p>Modified by ArcAnc</p>
 */

public abstract class BioBaseBlockEntity extends BlockEntity
{
    public BioBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        firstTick();
    }

    protected abstract void firstTick();

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider registries)
    {
        super.loadAdditional(pTag, registries);

        this.readCustomTag(pTag, registries, false);
    }

    public abstract void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket);

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
    {
        super.saveAdditional(tag, registries);
        this.writeCustomTag(tag, registries,false);
    }

    public abstract void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket);

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this, (blockEntity, registryAccess) ->
        {
            CompoundTag tag = new CompoundTag();
            this.writeCustomTag(tag, registryAccess,true);
            return tag;
        });
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider registries)
    {
        CompoundTag nonNullTag = pkt.getTag();
        this.readCustomTag(nonNullTag, registries, true);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
    {
        this.readCustomTag(tag, registries, true);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries)
    {
        CompoundTag tag = super.getUpdateTag(registries);
        writeCustomTag(tag, registries, true);
        return tag;
    }

    public void handlePacketFromClient(CompoundTag tag)
    {

    }

    public void handlePacketFromServer(CompoundTag tag)
    {

    }

    public void markContainingBlockForUpdate(@Nullable BlockState newState)
    {
        if(this.level!=null)
            markBlockForUpdate(getBlockPos(), newState);
    }

    public void markBlockForUpdate(BlockPos pos, @Nullable BlockState newState)
    {
        BlockState state = level.getBlockState(pos);
        if(newState == null)
            newState = state;
        level.sendBlockUpdated(pos, state, newState, 3);
        level.updateNeighborsAt(pos, newState.getBlock());
    }

    /**
     * Most calls to {@link BlockEntity#setChanged} should be replaced by this. The vanilla mD also updates comparator
     * states and re-caches the block state, while in most cases we just want to say "this needs to be saved to disk"
     */
    @SuppressWarnings("deprecation")
    protected void markDirty()
    {
        if (this.level != null && this.level.hasChunkAt(getBlockPos()))
        {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            this.level.getChunkAt(getBlockPos()).markUnsaved();
        }
    }

    /**
     *  Based on the super version, but works around a Forge patch to World#markChunkDirty causing duplicate comparator
     * updates and only performs comparator updates if this TE actually has comparator behavior
     */
    @Override
    public void setChanged()
    {
        if(this.level != null)
        {
            markDirty();
            BlockState state = getBlockState();
            if(state.hasAnalogOutputSignal())
                this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
        }
    }
}
