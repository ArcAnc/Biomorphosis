/**
 * @author ArcAnc
 * Created at: 26.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.inventory;

import com.google.common.base.Preconditions;
import org.joml.Vector4f;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MUST implement a storage type as an interface. For example, ItemSidedStorage{IItemHandler, ItemStackHolder, ItemStack} MUST implement IItemHandler
 */
public abstract class BasicSidedStorage<STORAGE, HOLDER, UNIT>
{
    protected final List<HOLDER> holders = new ArrayList<>();
    protected final Map<FaceMode, List<Integer>> BY_SIDE = new EnumMap<>(Arrays.stream(FaceMode.values()).collect(Collectors.toMap(Function.identity(), key -> new ArrayList<>())));
    protected final Map<Integer, FaceMode> SLOT_TO_MODE = new HashMap<>();

    public STORAGE addHolder(HOLDER holder, FaceMode mode)
    {
        int id = this.holders.size();
        this.holders.add(Preconditions.checkNotNull(holder));
        this.BY_SIDE.computeIfAbsent(Preconditions.checkNotNull(mode), key -> new ArrayList<>()).add(id);
        this.SLOT_TO_MODE.put(id, mode);
        return getStorage();
    }

    public Optional<FaceMode> getModeForIndex(int index)
    {
        validateHolderIndex(index);
        return Optional.ofNullable(SLOT_TO_MODE.get(index));
    }

    public abstract STORAGE getStorage();

    public List<HOLDER> getHoldersForAccess(FaceMode mode)
    {
        if (mode == FaceMode.BLOCKED)
            return List.of();
        if (mode == null || mode == FaceMode.INTERNAL)
            return this.holders;
        return BY_SIDE.get(mode).stream().map(this.holders :: get).toList();
    }

    public Optional<HOLDER> getHolderAt(FaceMode mode, int id)
    {
        if (mode == FaceMode.BLOCKED)
            return Optional.empty();
        validateHolderIndex(id);
        if (mode == null || mode == FaceMode.INTERNAL)
            return Optional.ofNullable(this.holders.get(id));

        List<Integer> indexes = BY_SIDE.get(mode);
            if (indexes == null || !indexes.contains(id))
                return Optional.empty();
        return Optional.ofNullable(this.holders.get(id));
    }

    public int getHoldersCount()
    {
        return this.holders.size();
    }

    public abstract boolean isValueValid(FaceMode mode, int id, UNIT value);
    public abstract int getHolderCapacity(FaceMode mode, int id);
    public abstract UNIT getValueAtId(FaceMode mode, int id);
    public abstract UNIT insert(FaceMode mode, UNIT value, boolean isSimulate);
    public abstract UNIT extract(FaceMode mode, UNIT value, boolean isSimulate);

    protected void validateHolderIndex(int id)
    {
        if (id < 0 || id >= this.holders.size())
            throw new IndexOutOfBoundsException("Holder " + id + " not in valid range - [0," + this.holders.size() + "]");
    }

    public enum FaceMode
    {
        ALL(new Vector4f(79, 8, 164, 255)),
        INPUT(new Vector4f(235, 107, 0, 255)),
        OUTPUT(new Vector4f(10, 118, 207, 255)),
        INTERNAL(new Vector4f(182, 31, 9, 255)),
        BLOCKED(new Vector4f(0,0,0,0));

        private final Vector4f color;

        FaceMode(Vector4f color)
        {
            this.color = color;
        }

        public Vector4f getColor()
        {
            return color;
        }
    }

    public enum RelativeFace
    {
        FRONT,
        RIGHT,
        LEFT,
        BACK,
        UP,
        DOWN;
    }

}
