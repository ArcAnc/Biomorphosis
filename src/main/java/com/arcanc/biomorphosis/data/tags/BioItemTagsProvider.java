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
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BioItemTagsProvider extends ItemTagsProvider
{
    public BioItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @NotNull BlockTagsProvider blockTagsProvider)
    {
        super(output, lookupProvider, blockTagsProvider.contentsGetter(), Database.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        copy(BioBlockTags.NORPH, BioItemTags.NORPH);
        copy(BioBlockTags.NORPH_AVOID, BioItemTags.NORPH_AVOID);
        copy(BioBlockTags.NORPH_SOURCE, BioItemTags.NORPH_SOURCE);
        tag(BioItemTags.WRENCH).add(Registration.ItemReg.WRENCH.get());
    }
}
