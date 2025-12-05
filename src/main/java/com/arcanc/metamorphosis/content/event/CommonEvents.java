/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.event;

import com.arcanc.metamorphosis.content.block.block_entity.*;
import com.arcanc.metamorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.metamorphosis.content.block.multiblock.MultiblockFluidStorage;
import com.arcanc.metamorphosis.content.entity.MetaEntityType;
import com.arcanc.metamorphosis.content.fluid.FluidTransportHandler;
import com.arcanc.metamorphosis.content.gui.container_menu.ChestMenu;
import com.arcanc.metamorphosis.content.item.MetaBucketItem;
import com.arcanc.metamorphosis.content.network.NetworkEngine;
import com.arcanc.metamorphosis.content.registration.Registration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
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
        NeoForge.EVENT_BUS.addListener(CommonEvents :: sendRecipesToClient);
        ChestMenu.registerEvents();
        /*FIXME: придумать адекватный метод добавления биома, без терраблендера, ибо лютая какая-то хуйня получается
        *  Ну или придумать способ, адекватной настройки. Как вариант можно выдрать из <a href="https://github.com/Glitchfiend/BiomesOPlenty/blob/1.21.8/common/src/main/java/biomesoplenty/init/ModBiomes.java">биомес о пленти</a>*/
        //modEventBus.addListener(CommonEvents :: commonSetupEvent);
   }
    
    /*private static void commonSetupEvent(@NotNull FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            Regions.register(new WastesRegion(Database.rl("wastes"), 6));
            
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, Database.MOD_ID, SurfaceRuleData.makeRules());
        });
    }*/
    
    @SuppressWarnings("unchecked")
    private static void registerEntityAttributes(final @NotNull EntityAttributeCreationEvent event)
    {
        Registration.EntityReg.ENTITY_TYPES.getEntries().stream().
                map(DeferredHolder::get).
                filter(entityType -> entityType.getBaseClass().isAssignableFrom(LivingEntity.class)).
                map(entityType -> (MetaEntityType<LivingEntity>)entityType).
                forEach(bioEntityType ->
                        event.put(bioEntityType,
                            bioEntityType.getEntityAttributeProvider().
                                    provide().
                                    build()));
    }

    private static void registerCapabilitiesEvent(final @NotNull RegisterCapabilitiesEvent event)
    {
        Registration.ItemReg.ITEMS.getEntries().stream().filter(item -> item.get() instanceof MetaBucketItem).
                map(DeferredHolder :: get).
                forEach(item -> event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item));

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_FLUID_STORAGE.get(), MetaFluidStorage :: getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_MULTIBLOCK_FLUID_STORAGE.get(), MultiblockFluidStorage :: getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_CRUSHER.get(), MetaCrusher :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_CRUSHER.get(), MetaCrusher :: getItemHandler);
	    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_SQUEEZER.get(), MetaSqueezer :: getFluidHandler);
	    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_SQUEEZER.get(), MetaSqueezer :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_STOMACH.get(), MetaStomach :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_STOMACH.get(), MetaStomach :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_CATCHER.get(), MetaCatcher :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_FORGE.get(), MetaForge :: getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Registration.BETypeReg.BE_FORGE.get(), MetaForge :: getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_MULTIBLOCK_CHAMBER.get(), MultiblockChamber :: getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.BETypeReg.BE_CHEST.get(), MetaChest :: getItemHandler);
		
		
		/*FIXME: нужно найти более правильный способ, чем фильтровать по типу. Мне не нравится,
		   но моджанги сделали хуйню с переписыванием возвращаемого типа у EntityType*/
	    /*BuiltInRegistries.ENTITY_TYPE.stream().
			    filter(type -> type.getCategory() != MobCategory.MISC).
	            forEach(type ->
			    event.registerEntity(BioCapabilities.GENOME, type,
					    (object, context) -> new GenomeHandler((LivingEntity) object)));*/
    }

    private static void sendRecipesToClient(final @NotNull OnDatapackSyncEvent event)
    {
        event.sendRecipes(Registration.RecipeReg.RECIPE_TYPES.getEntries().stream().
                map(DeferredHolder::get).
                toArray(RecipeType[]::new));
    }
}
