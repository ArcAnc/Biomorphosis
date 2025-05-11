/**
 * @author ArcAnc
 * Created at: 10.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MultiblockState implements StringRepresentable
{
    DISASSEMBLED("disassembled"),
    MORPHING("morphing"),
    FORMED("formed");

    private final String name;

    MultiblockState(String name)
    {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName()
    {
        return name;
    }
}
