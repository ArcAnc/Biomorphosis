/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class ItemModelProvider extends ModelProvider
{
    public ItemModelProvider(PackOutput output)
    {
        super(output, Database.MOD_ID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels)
    {
        itemModels.generateFlatItem(Registration.ItemReg.test.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.test_rare.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.test_ultra_rare.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.CREATIVE_TAB_ICON.get(), ModelTemplates.FLAT_ITEM);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks()
    {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems()
    {
        return super.getKnownItems();
    }

    @Override
    public @NotNull String getName()
    {
        return "Item Models - " + Database.MOD_NAME;
    }
}
