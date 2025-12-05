/**
 * @author ArcAnc
 * Created at: 11.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data.tags;

import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.tags.base.MetaBlockTags;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MetaBlockTagsProvider extends BlockTagsProvider
{

    public MetaBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, Database.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        this.tag(MetaBlockTags.NORPH).add(Registration.BlockReg.NORPH.get()).
                add(Registration.BlockReg.NORPH_OVERLAY.get()).
                add(Registration.BlockReg.NORPH_STAIRS.get()).
                add(Registration.BlockReg.FLUID_STORAGE.get()).
                add(Registration.BlockReg.FLUID_TRANSMITTER.get()).
                add(Registration.BlockReg.CRUSHER.get()).
		        add(Registration.BlockReg.SQUEEZER.get()).
                add(Registration.BlockReg.STOMACH.get()).
                add(Registration.BlockReg.CATCHER.get()).
                add(Registration.BlockReg.FORGE.get()).
                add(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.get()).
                add(Registration.BlockReg.MULTIBLOCK_CHAMBER.get()).
                add(Registration.BlockReg.MULTIBLOCK_MORPHER.get()).
                add(Registration.BlockReg.PROP_0.get()).
                add(Registration.BlockReg.PROP_1.get()).
                add(Registration.BlockReg.PROP_2.get()).
                add(Registration.BlockReg.HIVE_DECO.get()).
                add(Registration.BlockReg.CHEST.get());
        this.tag(MetaBlockTags.NORPH_SOURCE).add(Registration.BlockReg.NORPH_SOURCE.get());
        this.tag(MetaBlockTags.NORPH_AVOID).
                add(Blocks.LAVA).
                add(Registration.BlockReg.MULTIBLOCK_CHAMBER.get()).
                add(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.get());
        this.tag(BlockTags.CLIMBABLE).
                add(Registration.BlockReg.GLOW_MOSS.get()).
                add(Registration.BlockReg.MOSS.get());
    }
}
