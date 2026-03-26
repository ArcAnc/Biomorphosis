/**
 * @author ArcAnc
 * Created at: 07.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TagHelper
{
    public static Vec2 readVec2(CompoundTag compound)
    {
        return new Vec2(compound.getFloat("x"), compound.getFloat("y"));
    }

    public static Vec2 readVec2(CompoundTag compound, String address)
    {
        Vec2 vec = Vec2.ZERO;
        if (compound.contains(address))
        {
            CompoundTag tag = compound.getCompound(address);
            vec = readVec2(tag);
        }
        return vec;
    }

    public static Vec3 readVec3(CompoundTag compound)
    {
        return new Vec3(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
    }

    public static Vec3 readVec3(CompoundTag compound, String address)
    {
        Vec3 vec = Vec3.ZERO;
        if (compound.contains(address))
        {
            CompoundTag tag = compound.getCompound(address);
            vec = readVec3(tag);
        }
        return vec;
    }

    public static CompoundTag writeVec3(Vec3 vec)
    {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec.x());
        tag.putDouble("y", vec.y());
        tag.putDouble("z", vec.z());

        return tag;
    }

    public static CompoundTag writeVec3(Vec3 vec, CompoundTag dest, String address)
    {
        dest.put(address, writeVec3(vec));
        return dest;
    }

    public static CompoundTag writeVec2(Vec2 vec)
    {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x", vec.x);
        tag.putFloat("y", vec.y);

        return tag;
    }

    public static CompoundTag writeVec2(Vec2 vec, CompoundTag dest, String address)
    {
        dest.put(address, writeVec2(vec));
        return dest;
    }

    public static BlockPos readBlockPos(CompoundTag compound, String address)
    {
        BlockPos pos = BlockPos.ZERO;

        if (compound.contains(address))
        {
            CompoundTag tag = compound.getCompound(address);
            if (!tag.isEmpty())
            {
                pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            }
        }

        return pos;
    }

    public static CompoundTag writeBlockPos (BlockPos pos)
    {
        CompoundTag tag = new CompoundTag();

        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());

        return tag;
    }

    public static CompoundTag writeBlockPos(BlockPos pos, CompoundTag dest, String address)
    {
        dest.put(address, writeBlockPos(pos));
        return dest;
    }
}
