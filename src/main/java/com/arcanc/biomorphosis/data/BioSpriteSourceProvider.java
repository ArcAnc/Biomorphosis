/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BioSpriteSourceProvider extends SpriteSourceProvider
{
    private static final ResourceLocation GUI_ATLAS = ResourceLocation.withDefaultNamespace("gui");

    public BioSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, Database.MOD_ID);
    }

    @Override
    protected void gather()
    {
        //-------------------------------------------------------------------------------
        //TOOLTIPS
        //-------------------------------------------------------------------------------
        atlas(GUI_ATLAS).addSource(new SingleFile(Database.rl("gui/tooltip/special_background"), Optional.of(Database.rl("tooltip/special_background"))));
        atlas(GUI_ATLAS).addSource(new SingleFile(Database.rl("gui/tooltip/special_frame"), Optional.of(Database.rl("tooltip/special_frame"))));
    }

    @Override
    public @NotNull String getName()
    {
        return Database.MOD_NAME + ": Sprite Generator";
    }
}
