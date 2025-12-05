/**
 * @author ArcAnc
 * Created at: 07.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.multiblock.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MultiblockType implements StringRepresentable
{
    STATIC("static"),
    DYNAMIC("dynamic");

    public static final Codec<MultiblockType> CODEC = StringRepresentable.fromEnum(MultiblockType :: values);

    private final String id;

    MultiblockType(String id)
    {
        this.id = id;
    }

    @Override
    public @NotNull String getSerializedName()
    {
        return this.id;
    }

    public MapCodec<? extends IMultiblockDefinition> codecForType()
    {
        return switch (this)
        {
            case DYNAMIC -> DynamicMultiblockDefinition.CODEC;
            case STATIC -> StaticMultiblockDefinition.CODEC;
        };
    }
}
