/**
 * @author ArcAnc
 * Created at: 11.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import com.arcanc.biomorphosis.data.tags.base.BioItemTags;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BioItemTagsProvider extends ItemTagsProvider
{
    public BioItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, blockTagsProvider.contentsGetter(), Database.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(BioItemTags.NORPH).
                add(Registration.BlockReg.NORPH.get().asItem()).
                add(Registration.BlockReg.NORPH_OVERLAY.get().asItem()).
                add(Registration.BlockReg.NORPH_STAIRS.get().asItem()).
                add(Registration.BlockReg.FLUID_STORAGE.get().asItem()).
                add(Registration.BlockReg.FLUID_TRANSMITTER.get().asItem()).
                add(Registration.BlockReg.CRUSHER.get().asItem()).
                add(Registration.BlockReg.SQUEEZER.get().asItem()).
                add(Registration.BlockReg.STOMACH.get().asItem()).
                add(Registration.BlockReg.CATCHER.get().asItem()).
                add(Registration.BlockReg.FORGE.get().asItem()).
                add(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.get().asItem()).
                add(Registration.BlockReg.MULTIBLOCK_CHAMBER.get().asItem()).
                add(Registration.BlockReg.MULTIBLOCK_MORPHER.get().asItem()).
                add(Registration.BlockReg.MULTIBLOCK_TURRET.get().asItem()).
                add(Registration.BlockReg.MULTIBLOCK_CHRYSALIS.get().asItem()).
                add(Registration.BlockReg.PROP_0.get().asItem()).
                add(Registration.BlockReg.PROP_1.get().asItem()).
                add(Registration.BlockReg.PROP_2.get().asItem()).
                add(Registration.BlockReg.HIVE_DECO.get().asItem()).
                add(Registration.BlockReg.CHEST.get().asItem()).
                add(Registration.BlockReg.MEAT_MELON_BLOCK.get().asItem()).
                add(Registration.BlockReg.BIO_SHIT.get().asItem());
        copy(BioBlockTags.NORPHED_STAIRS, BioItemTags.NORPHED_STAIRS);
        //copy(BioBlockTags.NORPH_AVOID, BioItemTags.NORPH_AVOID);
        /*this.tag(BioItemTags.NORPH_AVOID).
                add(Registration.BlockReg.MULTIBLOCK_CHAMBER.asItem()).
                add(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.asItem()).
                add(Registration.BlockReg.MULTIBLOCK_TURRET.asItem()).
                add(Registration.BlockReg.MULTIBLOCK_CHRYSALIS.asItem());*/
        copy(BioBlockTags.NORPH_SOURCE, BioItemTags.NORPH_SOURCE);
        copy(BioBlockTags.NORPHED_BLOCKS, BioItemTags.NORPHED_BLOCKS);
        tag(BioItemTags.WRENCH).add(Registration.ItemReg.WRENCH.get());
        tag(BioItemTags.KSIGG_FOOD).add(Registration.ItemReg.FLESH_PIECE.get());
        
        tag(ItemTags.HOES).add(Registration.ItemReg.FLESH_HOE.get());
    }
}
