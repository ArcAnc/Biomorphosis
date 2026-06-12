/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.gui.BioSlot;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BioSpriteSourceProvider extends SpriteSourceProvider
{
    public static final ResourceLocation GUI_ATLAS = ResourceLocation.withDefaultNamespace("gui");
    public static final ResourceLocation PARTICLE_ATLAS = ResourceLocation.withDefaultNamespace("particles");

    public BioSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, Database.MOD_ID, existingFileHelper);
    }

    @Override
    protected void gather()
    {
        //-------------------------------------------------------------------------------
        // PARTICLES
        //-------------------------------------------------------------------------------
        atlas(PARTICLE_ATLAS).addSource(new SingleFile(
                Database.rl("particle/hive_deco_fly"),
                Optional.of(Database.rl("hive_deco"))));

        //-------------------------------------------------------------------------------
        // TOOLTIPS
        //-------------------------------------------------------------------------------
        atlas(GUI_ATLAS).addSource(new SingleFile(
                Database.rl("gui/tooltip/special_background"),
                Optional.of(Database.rl("tooltip/special_background"))));
        atlas(GUI_ATLAS).addSource(new SingleFile(
                Database.rl("gui/tooltip/special_frame"),
                Optional.of(Database.rl("tooltip/special_frame"))));

        //-------------------------------------------------------------------------------
        // SLOTS
        //-------------------------------------------------------------------------------
        atlas(GUI_ATLAS).addSource(new SingleFile(Database.rl("gui/slots/frame"), Optional.of(BioSlot.FRAME)));
        atlas(GUI_ATLAS).addSource(new SingleFile(Database.rl("gui/slots/mask"), Optional.of(BioSlot.MASK)));
    }

    @Override
    public String getName()
    {
        return Database.MOD_NAME + ": Sprite Generator";
    }
}
