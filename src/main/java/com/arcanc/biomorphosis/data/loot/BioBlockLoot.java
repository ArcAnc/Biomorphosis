/**
 * @author ArcAnc
 * Created at: 02.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.loot;

import com.arcanc.biomorphosis.content.block.BioStemBlock;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        
        getKnownBlocks().forEach(this :: dropSelf);
        
        this.add(
                Registration.BlockReg.MEAT_MELON_STEM.get(),
                block -> this.createStemDrops(block, Registration.ItemReg.MEAT_MELON_SEEDS.get())
        );
        
        this.add(
                Registration.BlockReg.MEAT_MELON_BLOCK.get(),
                block -> this.createSilkTouchDispatchTable(
                        block, this.applyExplosionDecay(
                                block,
                                LootItem.lootTableItem(Registration.ItemReg.FLESH_PIECE).
                                                apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 7.0F))).
                                                apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))).
                                                apply(LimitCount.limitCount(IntRange.upperBound(9)))
                        )
                )
        );
        
        this.add(Registration.BlockReg.BIO_BUSH.get(), this :: createGrassDrops);
    }
    
    public LootTable.Builder createStemDrops(Block block, Item item)
    {
        return LootTable.lootTable()
                .withPool(
                        this.applyExplosionDecay(
                                block,
                                LootPool.lootPool().
                                                setRolls(ConstantValue.exactly(1)).
                                                add(
                                                        LootItem.lootTableItem(item).
                                                                apply(
                                                                BioStemBlock.AGE.getPossibleValues(),
                                                                age -> SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, (age + 1) / 15.0F)).
                                                                                when(
                                                                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).
                                                                                                setProperties(
                                                                                                        StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, age)))))));
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
