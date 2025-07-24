/**
 * @author ArcAnc
 * Created at: 09.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.tags;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BioEntityTagsProvider extends EntityTypeTagsProvider
{
    public BioEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(output, provider, Database.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        this.tag(BioEntityTags.SWARM).
                add(Registration.EntityReg.MOB_KSIGG.getEntityHolder().get()).
                add(Registration.EntityReg.MOB_INFESTOR.getEntityHolder().get()).
                add(Registration.EntityReg.MOB_LARVA.getEntityHolder().get()).
                add(Registration.EntityReg.MOB_SWARMLING.getEntityHolder().get()).
                add(Registration.EntityReg.MOB_ZIRIS.getEntityHolder().get()).
                add(Registration.EntityReg.MOB_QUEEN.getEntityHolder().get());
    }
}
