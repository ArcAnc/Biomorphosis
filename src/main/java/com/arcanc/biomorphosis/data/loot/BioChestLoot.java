/**
 * @author ArcAnc
 * Created at: 04.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.loot;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class BioChestLoot implements LootTableSubProvider
{
    private final HolderLookup.Provider registries;
    
    public static final ResourceKey<LootTable> SWARM_VILLAGE = ResourceKey.create(
            Registries.LOOT_TABLE,
            Database.rl("chests/swarm_village"));
    public static final ResourceKey<LootTable> SRF = ResourceKey.create(
            Registries.LOOT_TABLE,
            Database.rl("chests/srf"));

    public BioChestLoot(HolderLookup.Provider registries)
    {
        this.registries = registries;
    }
    
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output)
    {
        output.accept(SWARM_VILLAGE, LootTable.lootTable().
                withPool(LootPool.lootPool().
                        setRolls(UniformGenerator.between(1, 2)).
                                add(LootItem.lootTableItem(Registration.ItemReg.FLESH_PIECE).
                                        setWeight(4).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                                add(LootItem.lootTableItem(Registration.BlockReg.FLESH).
                                        setWeight(1).
                                        apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))).
                withPool(LootPool.lootPool().
                        setRolls(UniformGenerator.between(0, 1)).
                                add(LootItem.lootTableItem(Registration.ItemReg.ANTENNAS).
                                        setWeight(4).
                                        apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))).
                                add(LootItem.lootTableItem(Registration.ItemReg.GUARD_ARMOR_PIECE).
                                        setWeight(1).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))).
                                add(LootItem.lootTableItem(Registration.ItemReg.INFESTOR_STING).
                                        setWeight(4).
                                        apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))).
                                add(LootItem.lootTableItem(Registration.ItemReg.SWARMLING_HEAD).
                                        setWeight(4).
                                        apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))).
                                add(LootItem.lootTableItem(Registration.ItemReg.ZIRIS_WING).
                                        setWeight(4).
                                        apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))).
                withPool(LootPool.lootPool().
                        setRolls(UniformGenerator.between(0, 1)).
                                add(LootItem.lootTableItem(Items.SUGAR).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                                add(LootItem.lootTableItem(Items.SPIDER_EYE).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                                add(LootItem.lootTableItem(Items.STRING).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))).
                                add(LootItem.lootTableItem(Items.BONE).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))).
                                add(LootItem.lootTableItem(Items.GUNPOWDER).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                                add(LootItem.lootTableItem(Items.FEATHER).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                                add(LootItem.lootTableItem(Items.RABBIT_HIDE).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                                add(LootItem.lootTableItem(Items.SLIME_BALL).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))).
                                add(LootItem.lootTableItem(Items.INK_SAC).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))).
                withPool(LootPool.lootPool().
                        setRolls(ConstantValue.exactly(1)).
                        add(EmptyLootItem.emptyItem().setWeight(396)).
                        add(LootItem.lootTableItem(Registration.ItemReg.LIFELESS_HELMET.get())).
                        add(LootItem.lootTableItem(Registration.ItemReg.LIFELESS_CHESTPLATE.get())).
                        add(LootItem.lootTableItem(Registration.ItemReg.LIFELESS_LEGGINGS.get())).
                        add(LootItem.lootTableItem(Registration.ItemReg.LIFELESS_BOOTS.get()))));

        output.accept(SRF, LootTable.lootTable().
                withPool(LootPool.lootPool().
                        setRolls(UniformGenerator.between(1, 2)).
                                add(LootItem.lootTableItem(Items.ARROW).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8)))).
                                add(LootItem.lootTableItem(Items.CARROT).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))).
                                add(LootItem.lootTableItem(Items.POTATO).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))).
                                add(LootItem.lootTableItem(Items.BREAD).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                                add(LootItem.lootTableItem(Items.COOKED_COD).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                                add(LootItem.lootTableItem(Items.COOKED_SALMON).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                                add(LootItem.lootTableItem(Items.COAL).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))).
                                add(LootItem.lootTableItem(Items.TORCH).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)))).
                                add(LootItem.lootTableItem(Items.STICK).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))));
    }
}
