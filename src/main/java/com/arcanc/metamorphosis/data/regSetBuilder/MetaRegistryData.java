/**
 * @author ArcAnc
 * Created at: 09.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.regSetBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MetaRegistryData
{
    private static final List<MetaRegistryData> DATA = new ArrayList<>();

    protected MetaRegistryData()
    {
    }

    public static void register(@NotNull MetaRegistryData data)
    {
        data.addContent();
        DATA.add(data);
    }

    protected abstract void addContent();
    protected abstract void registerContent(RegistrySetBuilder registrySetBuilder);

    protected  <T> @NotNull ResourceKey<T> getResourceKey(ResourceKey<Registry<T>> registryKey, ResourceLocation location)
    {
        return ResourceKey.create(registryKey, location);
    }

    public static @NotNull RegistrySetBuilder getBuilder()
    {
        RegistrySetBuilder builder = new RegistrySetBuilder();
        DATA.forEach(bioRegistryData ->
                bioRegistryData.registerContent(builder));
        return builder;
    }

    public static void clear()
    {
        DATA.clear();
    }
}
