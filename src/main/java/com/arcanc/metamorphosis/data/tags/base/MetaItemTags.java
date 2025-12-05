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
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class MetaItemTags
{
    public static final TagKey<Item> NORPH = create("norph");
    public static final TagKey<Item> NORPH_AVOID = create("norph_avoid");
    public static final TagKey<Item> NORPH_SOURCE = create("norph_source");
    public static final TagKey<Item> WRENCH = create("wrench");

    public static final TagKey<Item> KSIGG_FOOD = create("ksigg_food");
	public static final TagKey<Item> SWARMLING_FOOD = create("swarmling_food");

    private static @NotNull TagKey<Item> create(String name)
    {
        return TagKey.create(Registries.ITEM, Database.rl(name));
    }
}
