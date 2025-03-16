/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;

import com.arcanc.biomorphosis.content.book_data.page.component.recipes.RecipeRenderHandler;
import com.arcanc.biomorphosis.content.fluid.BioFluidType;
import com.arcanc.biomorphosis.content.fluid.FluidLevelAnimator;
import com.arcanc.biomorphosis.content.gui.component.tooltip.TooltipBorderHandler;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.BioBookProvider;
import com.arcanc.biomorphosis.data.BioRecipeProvider;
import com.arcanc.biomorphosis.data.BioSpriteSourceProvider;
import com.arcanc.biomorphosis.data.SummaryModelProvider;
import com.arcanc.biomorphosis.data.lang.EnUsProvider;
import com.arcanc.biomorphosis.data.loot.BioBlockLoot;
import com.arcanc.biomorphosis.data.loot.BioLootTableProvider;
import com.arcanc.biomorphosis.data.tags.BioBlockTagsProvider;
import com.arcanc.biomorphosis.data.tags.BioItemTagsProvider;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.model.BioFluidStorageBakedModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ClientEvents
{
    public static void registerClientEvents(final @NotNull IEventBus modEventBus)
    {
        modEventBus.addListener(ClientEvents :: clientSetup);
        modEventBus.addListener(ClientEvents :: gatherData);
        modEventBus.addListener(ClientEvents :: registerBlockEntityRenderers);
        modEventBus.addListener(ClientEvents :: registerFluidTypesExtensions);
        modEventBus.addListener(ClientEvents :: setupItemColor);
        modEventBus.addListener(ClientEvents :: setupModels);

        TooltipBorderHandler.registerHandler();
        RecipeRenderHandler.registerRenderers();
        NeoForge.EVENT_BUS.addListener(FluidLevelAnimator :: renderFrame);
    }

    private static void registerFluidTypesExtensions(final RegisterClientExtensionsEvent event)
    {
        Registration.FluidReg.FLUID_TYPES.getEntries().
            stream().
            map(DeferredHolder :: get).
            filter(fluidType -> fluidType instanceof BioFluidType).
            map(fluidType -> (BioFluidType)fluidType).
            forEach(fluidType ->
            event.registerFluidType(fluidType.registerClientExtensions(), fluidType));
    }

    private static void clientSetup (final @NotNull FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            Registration.FluidReg.FLUIDS.getEntries().
                    stream().
                    filter(fluid -> fluid.get().getFluidType() instanceof BioFluidType).
                    map(DeferredHolder:: get).
                    forEach(fluid ->
                            ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent())
                    );
        });
    }

    private static void setupItemColor(final @NotNull RegisterColorHandlersEvent.ItemTintSources event)
    {

    }

    private static void registerBlockEntityRenderers(final EntityRenderersEvent.@NotNull RegisterRenderers event)
    {
        Registration.BETypeReg.BLOCK_ENTITIES.getEntries().stream().
                map(DeferredHolder :: get).
                filter(type -> type instanceof Registration.BETypeReg.BioBlockEntityType).
                map(type -> (Registration.BETypeReg.BioBlockEntityType<? extends BlockEntity>)type).
                filter(type -> type.getRenderer() != null).
                forEach(type -> event.registerBlockEntityRenderer(type, type.getRenderer()));
    }

    private static void gatherData(final @NotNull GatherDataEvent.Client event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(true, new EnUsProvider(packOutput));
        gen.addProvider(true, new SummaryModelProvider(packOutput));
        BlockTagsProvider btp = new BioBlockTagsProvider(packOutput, lookupProvider);
        gen.addProvider(true, btp);
        gen.addProvider(true, new BioItemTagsProvider(packOutput, lookupProvider, btp));
        gen.addProvider(true, new BioRecipeProvider.Runner(packOutput, lookupProvider));
        gen.addProvider(true, new BioSpriteSourceProvider(packOutput, lookupProvider));

        gen.addProvider(true, BioLootTableProvider.create(
                List.of(
                        new LootTableProvider.SubProviderEntry(BioBlockLoot::new, LootContextParamSets.BLOCK)),
                packOutput,
                lookupProvider));

        gen.addProvider(true, new DatapackBuiltinEntriesProvider(
                packOutput,
                lookupProvider,
                BioBookProvider.registerBookContent(),
                Set.of(Database.MOD_ID)));
    }

    private static void setupModels (final ModelEvent.@NotNull ModifyBakingResult event)
    {
        event.getBakingResult().blockStateModels().computeIfPresent(
                BlockModelShaper.stateToModelLocation(Registration.BlockReg.FLUID_STORAGE.get().defaultBlockState()),
                (location, bakedModel) -> new BioFluidStorageBakedModel(bakedModel));
    }
}
