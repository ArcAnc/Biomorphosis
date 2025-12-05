/**
 * @author ArcAnc
 * Created at: 11.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.tags.base;

import com.arcanc.metamorphosis.util.Database;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class MetaBlockTags
{
    public static final TagKey<Block> NORPH = create("norph");
    public static final TagKey<Block> NORPH_AVOID = create("norph_avoid");
    public static final TagKey<Block> NORPH_SOURCE = create("norph_source");

    private static @NotNull TagKey<Block> create(String name)
    {
        return TagKey.create(Registries.BLOCK, Database.rl(name));
    }
}
