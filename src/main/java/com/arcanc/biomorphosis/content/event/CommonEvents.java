/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;

import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

public class CommonEvents
{

    public static void registerCommonEvents(@NotNull final IEventBus modEventBus)
    {
/*        modEventBus.addListener(NetworkEngine :: setupMessages);
        registerContainerMenuEvents();
        modEventBus.addListener(CommonEvents :: commonSetup);

        modEventBus.addListener(CommonEvents :: registerCapabilitiesEvent);

        modEventBus.addListener(CommonEvents :: gatherData);

        //fluidTransport
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: levelTickEvent);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: loadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: unloadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: saveLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: playerLoad);
*/    }

/*    private static void registerContainerMenuEvents()
    {
        NeoForge.EVENT_BUS.addListener(NContainerMenu:: onContainerOpened);
        NeoForge.EVENT_BUS.addListener(NContainerMenu :: onContainerClosed);
    }

    private static void commonSetup(final FMLCommonSetupEvent event)
    {
        Nedaire.getLogger().info("{} Started Server Initialization", NDatabase.MOD_ID);

        Nedaire.getLogger().info("{} Finished Server Initialization", NDatabase.MOD_ID);
    }

    private static void registerCapabilitiesEvent(final @NotNull RegisterCapabilitiesEvent event)
    {
        NRegistration.NItems.ITEMS.getEntries().stream().filter(item -> item.get() instanceof NBucketItem).
                map(DeferredHolder:: get).forEach(item -> event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item));

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NRegistration.NBlockEntities.BE_NODE.get(), NodeBlockEntity::getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NRegistration.NBlockEntities.BE_FLUID_STORAGE.get(), FluidStorageBlockEntity::getHandler);
    }

    private static void gatherData(final @NotNull GatherDataEvent.Client event)
    {
        //ExistingFileHelper ext = event.getExistingFileHelper();
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        //CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        gen.addProvider(true, new EnUsProvider(packOutput));
        gen.addProvider(event.includeClient(), new NItemModelProvider(packOutput, ext));
        gen.addProvider(event.includeClient(), new NBlockStateProvider(packOutput, ext));
        gen.addProvider(event.includeClient(), new NSpriteSourceProvider(packOutput, lookupProvider, ext));

        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                packOutput,
                lookupProvider,
                NMultiblockStructureProvider.registerMultiblocksStructure(),
                Set.of(NDatabase.MOD_ID)));
    }*/

}
