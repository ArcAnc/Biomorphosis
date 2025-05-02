/**
 * @author ArcAnc
 * Created at: 26.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.SidedConfig;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

public abstract class BioSidedAccessBlockEntity extends BioBaseBlockEntity implements BlockInterfaces.IWrencheable
{
    protected SidedConfig config = SidedConfig.zeroAccess();

    public static final ModelProperty<SidedConfig> ACCESS_PROPERTIES = new ModelProperty<>();

    public BioSidedAccessBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    protected void nextMode(Direction dir)
    {
        BasicSidedStorage.RelativeFace face = DirectionHelper.getRelativeDirection(getBlockState(), dir);
        this.config.nextMode(face);
        this.markDirty();
        if (level != null && level.isClientSide())
            requestModelDataUpdate();
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        this.config.deserializeNBT(registries, tag.getList("config", Tag.TAG_COMPOUND));
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        tag.put("config", this.config.serializeNBT(registries));
    }

    public boolean isAccessible(Direction direction)
    {
        return getFaceModeForDirection(direction) != BasicSidedStorage.FaceMode.INTERNAL && getFaceModeForDirection(direction) != BasicSidedStorage.FaceMode.BLOCKED;
    }

    public BasicSidedStorage.FaceMode getFaceModeForDirection(Direction dir)
    {
        return getFaceModeForFace(getRelativeFace(dir));
    }

    protected BasicSidedStorage.RelativeFace getRelativeFace(Direction dir)
    {
        return DirectionHelper.getRelativeDirection(getBlockState(), dir);
    }

    protected BasicSidedStorage.FaceMode getFaceModeForFace(BasicSidedStorage.RelativeFace face)
    {
        return this.config.getMode(face);
    }

    protected BioSidedAccessBlockEntity setSideMode(BasicSidedStorage.RelativeFace face, BasicSidedStorage.FaceMode mode)
    {
        Preconditions.checkNotNull(face);
        Preconditions.checkNotNull(face);
        this.config.setMode(face, mode);
        return this;
    }

    @Override
    public @NotNull ModelData getModelData()
    {
        return super.getModelData().derive().with(ACCESS_PROPERTIES, this.config.copy()).build();
    }

    protected static final class ConsumedFluidsData
    {
        public int biomass = 0;
        public int lymph = 0;
        public int adrenaline = 0;

        public void clearData()
        {
            this.biomass = 0;
            this.lymph = 0;
            this.adrenaline = 0;
        }

        public void writeData(@NotNull CompoundTag tag)
        {
            tag.putInt("biomass", this.biomass);
            tag.putInt("lymph", this.lymph);
            tag.putInt("adrenaline", this.adrenaline);
        }

        public void readData(@NotNull CompoundTag tag)
        {
            this.biomass = tag.getInt("biomass");
            this.lymph = tag.getInt("lymph");
            this.adrenaline = tag.getInt("adrenaline");
        }
    }
}
