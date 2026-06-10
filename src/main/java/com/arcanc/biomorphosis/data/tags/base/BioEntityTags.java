/**
 * @author ArcAnc
 * Created at: 09.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags.base;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class BioEntityTags
{
    public static final TagKey<EntityType<?>> SWARM = create("swarm");
    public static final TagKey<EntityType<?>> MELON_MAW = create("melon_maw");

    private static TagKey<EntityType<?>> create(String name)
    {
        return TagKey.create(Registries.ENTITY_TYPE, Database.rl(name));
    }
}
