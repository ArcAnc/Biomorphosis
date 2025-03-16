/**
 * @author ArcAnc
 * Created at: 04.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BioLootTableProvider
{
    public static @NotNull LootTableProvider create(List<LootTableProvider.SubProviderEntry> entries, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        return new LootTableProvider(packOutput, Set.of(), entries, registries);
    }
}
