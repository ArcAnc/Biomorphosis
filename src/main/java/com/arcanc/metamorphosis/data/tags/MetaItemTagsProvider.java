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
import com.arcanc.metamorphosis.data.tags.base.MetaItemTags;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MetaItemTagsProvider extends ItemTagsProvider
{
    public MetaItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @NotNull BlockTagsProvider blockTagsProvider)
    {
        super(output, lookupProvider, blockTagsProvider.contentsGetter(), Database.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        copy(MetaBlockTags.NORPH, MetaItemTags.NORPH);
        copy(MetaBlockTags.NORPH_AVOID, MetaItemTags.NORPH_AVOID);
        copy(MetaBlockTags.NORPH_SOURCE, MetaItemTags.NORPH_SOURCE);
        tag(MetaItemTags.WRENCH).add(Registration.ItemReg.WRENCH.get());
        tag(MetaItemTags.KSIGG_FOOD).add(Registration.ItemReg.FLESH_PIECE.get());
    }
}
