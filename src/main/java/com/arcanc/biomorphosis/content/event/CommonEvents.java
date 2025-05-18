/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;

import com.arcanc.biomorphosis.content.block.block_entity.*;
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockFluidStorage;
import com.arcanc.biomorphosis.content.entity.BioEntityType;
import com.arcanc.biomorphosis.content.fluid.FluidTransportHandler;
import com.arcanc.biomorphosis.content.item.BioBucketItem;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class CommonEvents
{

    public static void registerCommonEvents(@NotNull final IEventBus modEventBus)
    {

        modEventBus.addListener(NetworkEngine:: setupMessages);
        modEventBus.addListener(CommonEvents :: registerCapabilitiesEvent);
        FluidTransportHandler.registerHandler();
        modEventBus.addListener(CommonEvents :: registerEntityAttributes);
/*        registerContainerMenuEvents();
        modEventBus.addListener(CommonEvents :: commonSetup);



        modEventBus.addListener(CommonEvents :: gatherData);

        //fluidTransport
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: levelTickEvent);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: loadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: unloadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: saveLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: playerLoad);
*/    }

    @SuppressWarnings("unchecked")
    private static void registerEntityAttributes(final @NotNull EntityAttributeCreationEvent event)
    {
        Registration.EntityReg.ENTITY_TYPES.getEntries().stream().
                map(DeferredHolder::get).
                filter(entityType -> entityType.getBaseClass().isAssignableFrom(LivingEntity.class)).
                map(entityType -> (BioEntityType<LivingEntity>)entityType).
                forEach(bioEntityType ->
                        event.put(bioEntityType,
                            bioEntityType.getEntityAttributeProvider().
                                    provide().
                                    build()));
    }

    private static void registerCapabilitiesEvent(final @NotNull RegisterCapabilitiesEvent event)
    {
        Registration.ItemReg.ITEMS.getEntries().stream().filter(item -> item.get() instanceof BioBucketItem).
                map(DeferredHolder :: get).
                forEach(item -> event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item));

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_FLUID_STORAGE.get(), BioFluidStorage :: getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_MULTIBLOCK_FLUID_STORAGE.get(), MultiblockFluidStorage :: getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_CRUSHER.get(), BioCrusher :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_CRUSHER.get(), BioCrusher :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_STOMACH.get(), BioStomach:: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_STOMACH.get(), BioStomach :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_CATCHER.get(), BioCatcher:: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_FORGE.get(), BioForge:: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_FORGE.get(), BioForge:: getFluidHandler);
    }

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
