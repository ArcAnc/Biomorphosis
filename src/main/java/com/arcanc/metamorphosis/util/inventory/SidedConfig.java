/**
 * @author ArcAnc
 * Created at: 27.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class SidedConfig implements INBTSerializable<ListTag>
{
    private final Map<BasicSidedStorage.RelativeFace, BasicSidedStorage.FaceMode> config;

    private SidedConfig(Map<BasicSidedStorage.RelativeFace, BasicSidedStorage.FaceMode> faceConfig)
    {
        this.config = Preconditions.checkNotNull(faceConfig);
    }

    public void setMode(BasicSidedStorage.RelativeFace face, BasicSidedStorage.FaceMode mode)
    {
        this.config.put(face, mode);
    }

    public void cycleMode(BasicSidedStorage.RelativeFace face, boolean forward)
    {
        BasicSidedStorage.FaceMode[] values = BasicSidedStorage.FaceMode.values();
        int index = getMode(face).ordinal();
        index = (index + (forward ? 1 : values.length - 1)) % values.length;
        config.put(face, values[index]);
    }

    public void nextMode(BasicSidedStorage.RelativeFace face)
    {
        cycleMode(face, true);
    }

    public void prevMode(BasicSidedStorage.RelativeFace face)
    {
        cycleMode(face, false);
    }

    public BasicSidedStorage.FaceMode getMode(BasicSidedStorage.RelativeFace face)
    {
        return config.get(face);
    }

    public static @NotNull SidedConfig fullAccess()
    {
        return new SidedConfig(defaultConfig(BasicSidedStorage.FaceMode.ALL));
    }

    public static @NotNull SidedConfig zeroAccess()
    {
        return new SidedConfig(defaultConfig(BasicSidedStorage.FaceMode.BLOCKED));
    }

    private static @NotNull EnumMap<BasicSidedStorage.RelativeFace, BasicSidedStorage.FaceMode> defaultConfig(BasicSidedStorage.FaceMode mode)
    {
        EnumMap<BasicSidedStorage.RelativeFace, BasicSidedStorage.FaceMode> map = new EnumMap<>(BasicSidedStorage.RelativeFace.class);
        for (BasicSidedStorage.RelativeFace face : BasicSidedStorage.RelativeFace.values())
            map.put(face, mode);
        return map;
    }

    public SidedConfig copy()
    {
        return new SidedConfig(Map.copyOf(this.config));
    }

    //-------------------------------------------------
    // Serializing
    //-------------------------------------------------
    @Override
    public @NotNull ListTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        ListTag listTag = new ListTag();

        for (Map.Entry<BasicSidedStorage.RelativeFace, BasicSidedStorage.FaceMode> entry : config.entrySet())
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt("face", entry.getKey().ordinal());
            tag.putInt("mode", entry.getValue().ordinal());
            listTag.add(tag);
        }
        return listTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull ListTag nbt)
    {
        this.config.clear();

        for (int q = 0; q < nbt.size(); q++)
        {
            CompoundTag tag = nbt.getCompound(q);
            this.config.put(BasicSidedStorage.RelativeFace.values()[tag.getInt("face")], BasicSidedStorage.FaceMode.values()[tag.getInt("mode")]);
        }
    }
}
