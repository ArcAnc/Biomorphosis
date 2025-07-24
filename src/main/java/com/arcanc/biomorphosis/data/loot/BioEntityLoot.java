/**
 * @author ArcAnc
 * Created at: 18.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.loot;

import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class BioEntityLoot extends EntityLootSubProvider
{
    public BioEntityLoot(HolderLookup.Provider registries)
    {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate()
    {
        this.add(Registration.EntityReg.MOB_QUEEN.getEntityHolder().get(), LootTable.lootTable().
                withPool(LootPool.lootPool().
                        setRolls(ConstantValue.exactly(1.0f)).
                        add(
                                LootItem.lootTableItem(Registration.ItemReg.QUEENS_BRAIN).
                                        apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f)))).
                        when(LootItemKilledByPlayerCondition.killedByPlayer())));

        this.add(Registration.EntityReg.MOB_KSIGG.getEntityHolder().get(), LootTable.lootTable().
                withPool(LootPool.lootPool().
                        setRolls(ConstantValue.exactly(1.0f)).
                        add(
                                LootItem.lootTableItem(Registration.ItemReg.FLESH_PIECE).
                                        apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))).
                        when(LootItemKilledByPlayerCondition.killedByPlayer())).
                withPool(LootPool.lootPool().
                        setRolls(ConstantValue.exactly(1.0f)).
                        add(LootItem.lootTableItem(Items.LEATHER).
                                apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))).
                        when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(Registration.EntityReg.MOB_LARVA.getEntityHolder().get(), LootTable.lootTable());
        this.add(Registration.EntityReg.MOB_ZIRIS.getEntityHolder().get(), LootTable.lootTable().
                withPool(LootPool.lootPool().
                        setRolls(ConstantValue.exactly(1.0f)).
                        add(
                                LootItem.lootTableItem(Registration.ItemReg.FLESH_PIECE).
                                    apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))).
                        when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(Registration.EntityReg.MOB_INFESTOR.getEntityHolder().get(), LootTable.lootTable().
                withPool(LootPool.lootPool().
                        setRolls(ConstantValue.exactly(1.0f)).
                        add(LootItem.lootTableItem(Registration.ItemReg.FLESH_PIECE).
                            apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))).
                        when(LootItemKilledByPlayerCondition.killedByPlayer())));

        this.add(Registration.EntityReg.MOB_SWARMLING.getEntityHolder().get(), LootTable.lootTable().
                withPool(LootPool.lootPool().
                        setRolls(ConstantValue.exactly(1.0f)).
                        add(LootItem.lootTableItem(Registration.ItemReg.FLESH_PIECE).
                            apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))).
                        when(LootItemKilledByPlayerCondition.killedByPlayer())));
    }

    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes()
    {
        return Registration.EntityReg.ENTITY_TYPES.getEntries().
                stream().
                map(DeferredHolder :: get);
    }
}
