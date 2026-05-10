/**
 * @author ArcAnc
 * Created at: 15.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import com.arcanc.biomorphosis.mixin.OctahedralGroupAccessor;
import com.arcanc.biomorphosis.mixin.SymmetricGroup3Expander;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.math.OctahedralGroup;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**This code was taken from <a href="https://github.com/mekanism/Mekanism/blob/1.21.x/src/main/java/mekanism/common/util/VoxelShapeUtils.java">Mekanism mod</a>. @Pupnewfster thanks to you*/
public class VoxelShapeHelper
{
    private static final Vec3 BLOCK_CENTER = new Vec3(0.5d, 0.5d, 0.5d);
    public static final OctahedralGroup BLOCK_ROT_X_270 = OctahedralGroup.ROT_90_X_POS;
    public static final OctahedralGroup BLOCK_ROT_X_180 = OctahedralGroup.ROT_180_FACE_YZ;
    public static final OctahedralGroup BLOCK_ROT_X_90 = OctahedralGroup.ROT_90_X_NEG;
    public static final OctahedralGroup BLOCK_ROT_Y_270 = OctahedralGroup.ROT_90_Y_POS;
    public static final OctahedralGroup BLOCK_ROT_Y_180 = OctahedralGroup.ROT_180_FACE_XZ;
    public static final OctahedralGroup BLOCK_ROT_Y_90 = OctahedralGroup.ROT_90_Y_NEG;
    public static final OctahedralGroup BLOCK_ROT_Z_270 = OctahedralGroup.ROT_90_Z_POS;
    public static final OctahedralGroup BLOCK_ROT_Z_180 = OctahedralGroup.ROT_180_FACE_XY;
    public static final OctahedralGroup BLOCK_ROT_Z_90 = OctahedralGroup.ROT_90_Z_NEG;
    
    public static VoxelShape rotate(VoxelShape shape, OctahedralGroup rotation)
    {
        return rotate(shape, rotation, BLOCK_CENTER);
    }
    
    public static VoxelShape rotate(VoxelShape shape, OctahedralGroup rotation, Vec3 rotationPoint)
    {
        if (rotation == OctahedralGroup.IDENTITY)
            return shape;
        else
        {
            DiscreteVoxelShape newDiscreteShape = ((DiscreteVoxelShapeExpanderHelper) shape.shape).biomorphosis$rotate(rotation);
            if (shape instanceof CubeVoxelShape && BLOCK_CENTER.equals(rotationPoint))
                return new CubeVoxelShape(newDiscreteShape);
            else
            {
                OctahedralGroupAccessor perm = (OctahedralGroupAccessor)(Object)rotation;
                Direction.Axis newX = ((SymmetricGroup3ExpanderHelper)(Object)perm.getPermutation()).biomorphosis$permuteAxis(Direction.Axis.X);
                Direction.Axis newY = ((SymmetricGroup3ExpanderHelper)(Object)perm.getPermutation()).biomorphosis$permuteAxis(Direction.Axis.Y);
                Direction.Axis newZ = ((SymmetricGroup3ExpanderHelper)(Object)perm.getPermutation()).biomorphosis$permuteAxis(Direction.Axis.Z);
                DoubleList newXs = shape.getCoords(newX);
                DoubleList newYs = shape.getCoords(newY);
                DoubleList newZs = shape.getCoords(newZ);
                boolean flipX = rotation.inverts(Direction.Axis.X);
                boolean flipY = rotation.inverts(Direction.Axis.Y);
                boolean flipZ = rotation.inverts(Direction.Axis.Z);
                return new ArrayVoxelShape(newDiscreteShape, flipAxisIfNeeded(newXs, flipX, rotationPoint.get(newX), rotationPoint.x), flipAxisIfNeeded(newYs, flipY, rotationPoint.get(newY), rotationPoint.y), flipAxisIfNeeded(newZs, flipZ, rotationPoint.get(newZ), rotationPoint.z));
            }
        }
    }
    
    @VisibleForTesting
    static DoubleList flipAxisIfNeeded(DoubleList newAxis, boolean flip, double newRelative, double oldRelative)
    {
        if (!flip && newRelative == oldRelative)
            return newAxis;
        else
        {
            int size = newAxis.size();
            DoubleList newList = new DoubleArrayList(size);
            if (flip)
                for(int i = size - 1; i >= 0; --i)
                    newList.add(-(newAxis.getDouble(i) - newRelative) + oldRelative);
            else
                for(int i = 0; i >= 0 && i < size; ++i)
                    newList.add(newAxis.getDouble(i) - newRelative + oldRelative);
            
            return newList;
        }
    }
    
    public static boolean equal(VoxelShape first, VoxelShape second)
    {
        return !Shapes.joinIsNotEmpty(first, second, BooleanOp.NOT_SAME);
    }
    
    public static Map<Direction.Axis, VoxelShape> rotateHorizontalAxis(VoxelShape zAxis)
    {
        return rotateHorizontalAxis(zAxis, BLOCK_CENTER);
    }
    
    public static Map<Direction.Axis, VoxelShape> rotateHorizontalAxis(VoxelShape zAxis, Vec3 rotationCenter)
    {
        return Maps.newEnumMap(Map.of(Direction.Axis.Z, zAxis, Direction.Axis.X, rotate(zAxis, BLOCK_ROT_Y_90, rotationCenter)));
    }
    
    public static Map<Direction.Axis, VoxelShape> rotateAllAxis(VoxelShape north)
    {
        return rotateAllAxis(north, BLOCK_CENTER);
    }
    
    public static Map<Direction.Axis, VoxelShape> rotateAllAxis(VoxelShape north, Vec3 rotationCenter)
    {
        return Maps.newEnumMap(Map.of(Direction.Axis.Z, north, Direction.Axis.X, rotate(north, BLOCK_ROT_Y_90, rotationCenter), Direction.Axis.Y, rotate(north, BLOCK_ROT_X_90, rotationCenter)));
    }
    
    public static Map<Direction, VoxelShape> rotateHorizontal(VoxelShape north)
    {
        return rotateHorizontal(north, OctahedralGroup.IDENTITY, BLOCK_CENTER);
    }
    
    public static Map<Direction, VoxelShape> rotateHorizontal(VoxelShape north, OctahedralGroup initial)
    {
        return rotateHorizontal(north, initial, BLOCK_CENTER);
    }
    
    public static Map<Direction, VoxelShape> rotateHorizontal(VoxelShape north, OctahedralGroup initial, Vec3 rotationCenter)
    {
        return Maps.newEnumMap(Map.of(Direction.NORTH, rotate(north, initial), Direction.EAST, rotate(north, BLOCK_ROT_Y_90.compose(initial), rotationCenter), Direction.SOUTH, rotate(north, BLOCK_ROT_Y_180.compose(initial), rotationCenter), Direction.WEST, rotate(north, BLOCK_ROT_Y_270.compose(initial), rotationCenter)));
    }
    
    public static Map<Direction, VoxelShape> rotateAll(VoxelShape north)
    {
        return rotateAll(north, OctahedralGroup.IDENTITY, BLOCK_CENTER);
    }
    
    public static Map<Direction, VoxelShape> rotateAll(VoxelShape north, Vec3 rotationCenter)
    {
        return rotateAll(north, OctahedralGroup.IDENTITY, rotationCenter);
    }
    
    public static Map<Direction, VoxelShape> rotateAll(VoxelShape north, OctahedralGroup initial, Vec3 rotationCenter)
    {
        return Maps.newEnumMap(Map.of(Direction.NORTH, rotate(north, initial), Direction.EAST, rotate(north, BLOCK_ROT_Y_90.compose(initial), rotationCenter), Direction.SOUTH, rotate(north, BLOCK_ROT_Y_180.compose(initial), rotationCenter), Direction.WEST, rotate(north, BLOCK_ROT_Y_270.compose(initial), rotationCenter), Direction.UP, rotate(north, BLOCK_ROT_X_270.compose(initial), rotationCenter), Direction.DOWN, rotate(north, BLOCK_ROT_X_90.compose(initial), rotationCenter)));
    }
    
    public static @Unmodifiable Map<AttachFace, Map<Direction, VoxelShape>> rotateAttachFace(VoxelShape north)
    {
        return rotateAttachFace(north, OctahedralGroup.IDENTITY);
    }
    
    public static @Unmodifiable Map<AttachFace, Map<Direction, VoxelShape>> rotateAttachFace(VoxelShape north, OctahedralGroup initial)
    {
        return Map.of(AttachFace.WALL, rotateHorizontal(north, initial), AttachFace.FLOOR, rotateHorizontal(north, BLOCK_ROT_X_270.compose(initial)), AttachFace.CEILING, rotateHorizontal(north, BLOCK_ROT_Y_180.compose(BLOCK_ROT_X_90).compose(initial)));
    }
    
    public interface SymmetricGroup3ExpanderHelper
    {
        int biomorphosis$permute(int i);
        
        Direction.Axis biomorphosis$permuteAxis(Direction.Axis axis);
        
        Vector3i biomorphosis$permuteVector(Vector3i vector);
    }
    
    public interface DiscreteVoxelShapeExpanderHelper
    {
        DiscreteVoxelShape biomorphosis$rotate(OctahedralGroup rotation);
    }
    
    public interface OctahedralGroupExpanderHelper
    {
        Vector3i biomorphosis$rotate(Vector3i vector);
    }
}
