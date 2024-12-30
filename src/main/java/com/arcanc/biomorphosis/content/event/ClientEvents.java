/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;

import com.arcanc.biomorphosis.data.BioRecipeProvider;
import com.arcanc.biomorphosis.data.SummaryModelProvider;
import com.arcanc.biomorphosis.data.lang.EnUsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class ClientEvents
{
    public static void registerClientEvents(final @NotNull IEventBus modEventBus)
    {
        modEventBus.addListener(ClientEvents :: gatherData);
/*        modEventBus.addListener(ClientEvents :: registerBlockEntityRenderers);
        modEventBus.addListener(ClientEvents :: registerMenuScreens);
        modEventBus.addListener(ClientEvents :: setupClient);
        modEventBus.addListener(ClientEvents :: setupItemColor);
        modEventBus.addListener(ClientEvents :: setupModels);
        modEventBus.addListener(ClientEvents :: registerFluidTypesExtensions);

        modEventBus.addListener(ClientEvents ::registerReloadListeners);

        modEventBus.addListener(DynamicModel :: registerModels);

        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: essenceRenderer);
*/    }

/*    private static void registerFluidTypesExtensions(final RegisterClientExtensionsEvent event)
    {
        NRegistration.NFluids.FLUID_TYPES.getEntries().
                stream().
                map(DeferredHolder::get).
                filter(fluidType -> fluidType instanceof NFluidType).
                map(fluidType -> (NFluidType)fluidType).
                forEach(fluidType ->
                        event.registerFluidType(fluidType.registerClientExtensions(), fluidType));
    }

    private static void registerBlockEntityRenderers(final EntityRenderersEvent.@NotNull RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(NRegistration.NBlockEntities.BE_NODE.get(), NodeRenderer:: new);
        event.registerBlockEntityRenderer(NRegistration.NBlockEntities.BE_FLUID_STORAGE.get(), FluidStorageRenderer:: new);
        event.registerBlockEntityRenderer(NRegistration.NBlockEntities.BE_FLUID_TRANSMITTER.get(), FluidTransmitterRenderer :: new);
    }

    private static void setupClient(final @NotNull FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            NRegistration.NFluids.FLUIDS.getEntries().stream().filter(fluid -> fluid.get().getFluidType() instanceof NEnergonFluidType).
                    map(DeferredHolder:: get).
                    forEach(fluid ->
                    {
                        ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent());
                        ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent());
                    });
        });
    }

    private static void registerMenuScreens(@NotNull RegisterMenuScreensEvent event)
    {
        event.register(NRegistration.NMenuTypes.FLUID_TRANSMITTER.getType(), FluidTransmitterScreen ::new);
    }

    private static void setupItemColor(final @NotNull RegisterColorHandlersEvent.Item event)
    {
        event.register((stack, tintIndex) ->
                {
                    if (stack.getItem() instanceof NBucketItem item)
                    {
                        if (item.content.getFluidType() instanceof NEnergonFluidType type && tintIndex == 1)
                            return type.getEnergonType().color();
                    }
                    return -1;
                },
                NRegistration.NFluids.ENERGON_DARK.bucket().get(),
                NRegistration.NFluids.ENERGON_BLUE.bucket().get(),
                NRegistration.NFluids.ENERGON_RED.bucket().get(),
                NRegistration.NFluids.ENERGON_GREEN.bucket().get(),
                NRegistration.NFluids.ENERGON_YELLOW.bucket().get());
    }

    private static void setupModels (final ModelEvent.@NotNull RegisterGeometryLoaders event)
    {
        event.register(SimpleModel.GeometryLoader.ID, new SimpleModel.GeometryLoader(FluidStorageBakedModel :: new));
    }

    private static void registerReloadListeners(final @NotNull RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> RenderHelper.clearRenderCaches());
    }
 */

    private static void gatherData(final @NotNull GatherDataEvent.Client event)
    {
        //ExistingFileHelper ext = event.getExistingFileHelper();
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        gen.addProvider(true, new EnUsProvider(packOutput));
        gen.addProvider(true, new SummaryModelProvider(packOutput));
        gen.addProvider(true, new BioRecipeProvider.Runner(packOutput, lookupProvider));
    }
}
