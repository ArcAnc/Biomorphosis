/**
 * @author ArcAnc
 * Created at: 11.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags.base;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BioBlockTags
{
    public static final TagKey<Block> NORPH = create("norph");
    public static final TagKey<Block> NORPH_AVOID = create("norph_avoid");
    public static final TagKey<Block> NORPH_SOURCE = create("norph_source");
    public static final TagKey<Block> NORPHED_BLOCKS = create("norphed_blocks");
    public static final TagKey<Block> NORPHED_STAIRS = create("norphed_stairs");
    public static final TagKey<Block> MAINTAINS_SWARM_FARMLAND = create("maintains_swarm_farmland");

    private static TagKey<Block> create(String name)
    {
        return TagKey.create(Registries.BLOCK, Database.rl(name));
    }
}
