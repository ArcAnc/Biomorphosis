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
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChrysalis;
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockFluidStorage;
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockTurret;
import com.arcanc.biomorphosis.content.entity.BioEntityType;
import com.arcanc.biomorphosis.content.entity.trades.Trades;
import com.arcanc.biomorphosis.content.gui.container_menu.ChestMenu;
import com.arcanc.biomorphosis.content.item.BioBucketItem;
import com.arcanc.biomorphosis.content.mutations.GenomeHandler;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.content.worldgen.biome.OverworldRegion;
import com.arcanc.biomorphosis.content.worldgen.biome.wastes.WastesSurfaceRuleData;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class CommonEvents
{

    public static void registerCommonEvents(final IEventBus modEventBus)
    {
        modEventBus.addListener(NetworkEngine:: setupMessages);
        modEventBus.addListener(CommonEvents :: registerCapabilitiesEvent);
        modEventBus.addListener(CommonEvents :: registerEntityAttributes);
	    GenomeHandler.register(modEventBus);
		ChestMenu.registerEvents();
	    Trades.register(modEventBus);
		
        modEventBus.addListener(CommonEvents :: commonSetupEvent);
   }
    
    private static void commonSetupEvent(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            Regions.register(new OverworldRegion(6));
            
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, Database.MOD_ID, WastesSurfaceRuleData.makeRules());
        });
    }
    
    @SuppressWarnings("unchecked")
    private static void registerEntityAttributes(final EntityAttributeCreationEvent event)
    {
        Registration.EntityReg.ENTITY_TYPES.getEntries().stream().
                map(DeferredHolder::get).
                filter(entityType -> LivingEntity.class.isAssignableFrom(entityType.getBaseClass())).
                map(entityType -> (BioEntityType<LivingEntity>)entityType).
                forEach(bioEntityType ->
                        event.put(bioEntityType,
                            bioEntityType.getEntityAttributeProvider().
                                    provide().
                                    build()));
    }

    private static void registerCapabilitiesEvent(final RegisterCapabilitiesEvent event)
    {
        Registration.ItemReg.ITEMS.getEntries().stream().filter(item -> item.get() instanceof BioBucketItem).
                map(DeferredHolder :: get).
                forEach(item -> event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item));

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_FLUID_STORAGE.get(), BioFluidStorage :: getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_MULTIBLOCK_FLUID_STORAGE.get(), MultiblockFluidStorage :: getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_CRUSHER.get(), BioCrusher :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_CRUSHER.get(), BioCrusher :: getItemHandler);
	    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_SQUEEZER.get(), BioSqueezer :: getFluidHandler);
	    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_SQUEEZER.get(), BioSqueezer :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_STOMACH.get(), BioStomach :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_STOMACH.get(), BioStomach :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_CATCHER.get(), BioCatcher :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_FORGE.get(), BioForge :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_FORGE.get(), BioForge :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_MULTIBLOCK_CHAMBER.get(), MultiblockChamber :: getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_CHEST.get(), BioChest :: getItemHandler);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_MULTIBLOCK_TURRET.get(), MultiblockTurret :: getFluidHandler);
	    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_MULTIBLOCK_CHRYSALIS.get(), MultiblockChrysalis :: getFluidHandler);
    }
}
