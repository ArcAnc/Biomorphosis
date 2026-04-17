/**
 * @author ArcAnc
 * Created at: 02.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.loot;

import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;

public class BioBlockLoot extends BlockLootSubProvider
{
    public BioBlockLoot(HolderLookup.Provider registries)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate()
    {
        getKnownBlocks().forEach(this :: dropSelf);
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        List<DeferredBlock<? extends Block>> handMadeBlocks = List.of(
                Registration.BlockReg.MULTIBLOCK_CHAMBER,
                Registration.BlockReg.MULTIBLOCK_CHRYSALIS,
                Registration.BlockReg.MULTIBLOCK_TURRET);

        return Registration.BlockReg.BLOCKS.getEntries().
                stream().
                filter(block -> handMadeBlocks.stream().
                        noneMatch(deferred -> deferred.getId().equals(block.getId()))).
                map(DeferredHolder :: get).
                map(block -> (Block)block).
                filter(block -> block.getLootTable() != BuiltInLootTables.EMPTY).
                toList();
    }
}
