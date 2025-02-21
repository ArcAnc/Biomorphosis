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
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BioBlockTagsProvider extends BlockTagsProvider
{

    public BioBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, Database.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        this.tag(BioBlockTags.NORPH).add(Registration.BlockReg.NORPH.get()).add(Registration.BlockReg.NORPH_OVERLAY.get()).add(Registration.BlockReg.NORPH_STAIRS.get());
        this.tag(BioBlockTags.NORPH_SOURCE).add(Registration.BlockReg.NORPH_SOURCE.get());
        this.tag(BioBlockTags.NORPH_AVOID).add(Blocks.LAVA);
    }
}
