/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;

import com.arcanc.biomorphosis.content.block.BioStemBlock;
import com.arcanc.biomorphosis.content.block.block_entity.ber.*;
import com.arcanc.biomorphosis.content.block.multiblock.renderer.MultiblockChamberRenderer;
import com.arcanc.biomorphosis.content.block.multiblock.renderer.MultiblockChrysalisRenderer;
import com.arcanc.biomorphosis.content.block.multiblock.renderer.MultiblockMorpherRenderer;
import com.arcanc.biomorphosis.content.block.multiblock.renderer.MultiblockTurretRenderer;
import com.arcanc.biomorphosis.content.book_data.page.component.recipes.RecipeRenderHandler;
import com.arcanc.biomorphosis.content.entity.BioEntityType;
import com.arcanc.biomorphosis.content.entity.renderer.*;
import com.arcanc.biomorphosis.content.entity.renderer.srf.model.BlacksmithModel;
import com.arcanc.biomorphosis.content.entity.renderer.srf.model.CaptainModel;
import com.arcanc.biomorphosis.content.entity.renderer.srf.model.SergeantModel;
import com.arcanc.biomorphosis.content.entity.renderer.srf.model.SoldierModel;
import com.arcanc.biomorphosis.content.fluid.BioFluidType;
import com.arcanc.biomorphosis.content.fluid.FluidLevelAnimator;
import com.arcanc.biomorphosis.content.gui.component.tooltip.TooltipBorderHandler;
import com.arcanc.biomorphosis.content.gui.font.BioGlyphRenderTypes;
import com.arcanc.biomorphosis.content.item.BioBucketItem;
import com.arcanc.biomorphosis.content.particle.HiveDecoParticle;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.*;
import com.arcanc.biomorphosis.data.lang.EnUsProvider;
import com.arcanc.biomorphosis.data.loot.BioBlockLoot;
import com.arcanc.biomorphosis.data.loot.BioEntityLoot;
import com.arcanc.biomorphosis.data.loot.BioGlobalLootModifier;
import com.arcanc.biomorphosis.data.loot.BioLootTableProvider;
import com.arcanc.biomorphosis.data.model.BioBlockStateProvider;
import com.arcanc.biomorphosis.data.model.BioItemModelProvider;
import com.arcanc.biomorphosis.data.recipe.*;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.data.tags.*;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.model.BioFluidStorageBakedModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ClientEvents
{
    public static void registerClientEvents(final IEventBus modEventBus)
    {
		modEventBus.addListener(ClientEvents :: clientSetup);
        modEventBus.addListener(ClientEvents :: gatherData);
        modEventBus.addListener(ClientEvents :: registerRenderers);
		modEventBus.addListener(ClientEvents :: registerItemColors);
	    modEventBus.addListener(ClientEvents :: registerBlockColors);
        modEventBus.addListener(ClientEvents :: registerLayerDefinitions);
        modEventBus.addListener(ClientEvents :: registerClientExtensions);
        modEventBus.addListener(ClientEvents :: setupModels);
        modEventBus.addListener(ClientEvents :: registerMenuScreens);
		modEventBus.addListener(OverlayRenderHandler :: registerGuiLayers);
		modEventBus.addListener(ClientEvents :: registerParticleProviders);

	    BioGlyphRenderTypes.register(modEventBus);
        BioFluidTransmitterRenderTypes.register(modEventBus);

        TooltipBorderHandler.registerHandler();
        RecipeRenderHandler.registerRenderers();
        NeoForge.EVENT_BUS.addListener(FluidLevelAnimator :: renderFrame);
        NeoForge.EVENT_BUS.addListener(ClientEvents :: receiveRecipesEvent);
		registerCustomTextures(modEventBus);
    }
	
	private static void receiveRecipesEvent(final RecipesUpdatedEvent event)
    {
		ChamberRecipe.RECIPES.clear();
        ChamberRecipe.RECIPES.addAll(event.getRecipeManager().getAllRecipesFor(Registration.RecipeReg.CHAMBER_RECIPE.getRecipeType().get()).
                stream().
                map(RecipeHolder :: value).
                toList());

        CrusherRecipe.RECIPES.clear();
        CrusherRecipe.RECIPES.addAll(event.getRecipeManager().getAllRecipesFor(Registration.RecipeReg.CRUSHER_RECIPE.getRecipeType().get()).
                stream().
                map(RecipeHolder :: value).
                toList());
	    
	    SqueezerRecipe.RECIPES.clear();
	    SqueezerRecipe.RECIPES.addAll(event.getRecipeManager().getAllRecipesFor(Registration.RecipeReg.SQUEEZER_RECIPE.getRecipeType().get()).
			    stream().
			    map(RecipeHolder :: value).
			    toList());
		
        StomachRecipe.RECIPES.clear();
        StomachRecipe.RECIPES.addAll(event.getRecipeManager().getAllRecipesFor(Registration.RecipeReg.STOMACH_RECIPE.getRecipeType().get()).
                stream().
                map(RecipeHolder :: value).
                toList());

        ForgeRecipe.RECIPES.clear();
        ForgeRecipe.RECIPES.addAll(event.getRecipeManager().getAllRecipesFor(Registration.RecipeReg.FORGE_RECIPE.getRecipeType().get()).
                stream().
                map(RecipeHolder :: value).
                toList());
    }
	
	private static void registerBlockColors(final RegisterColorHandlersEvent.Block event)
	{
		event.register((state, level, pos, tintIndex) ->
		{
			int age = state.getValue(BioStemBlock.AGE);
			return MathHelper.ColorHelper.color(255 - age * 8, age * 32, age * 4);
		}, Registration.BlockReg.MEAT_MELON_STEM.get());
		
		event.getBlockColors().addColoringState(BioStemBlock.AGE, Registration.BlockReg.MEAT_MELON_STEM.get());
	}
	
	private static void registerItemColors(final RegisterColorHandlersEvent.Item event)
	{
		Registration.ItemReg.ITEMS.getEntries().
				stream().
				map(DeferredHolder :: get).
				filter(item -> item instanceof BioBucketItem).
				forEach(item ->
				event.register(new DynamicFluidContainerModel.Colors(), item));
	}
	
    private static void registerClientExtensions(final RegisterClientExtensionsEvent event)
    {
        Registration.FluidReg.FLUID_TYPES.getEntries().
            stream().
            map(DeferredHolder :: get).
            filter(fluidType -> fluidType instanceof BioFluidType).
            map(fluidType -> (BioFluidType)fluidType).
            forEach(fluidType ->
            event.registerFluidType(fluidType.registerClientExtensions(), fluidType));
    }

    private static void clientSetup (final FMLClientSetupEvent event)
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

    private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        Registration.BETypeReg.BLOCK_ENTITIES.getEntries().stream().
                map(DeferredHolder :: get).
                filter(type -> type instanceof Registration.BETypeReg.BioBlockEntityType).
                map(type -> (Registration.BETypeReg.BioBlockEntityType<? extends BlockEntity, ?, ?>)type).
                filter(type -> type.getRenderer() != null).
                forEach(type -> event.registerBlockEntityRenderer(type, type.getRenderer()));

        Registration.EntityReg.ENTITY_TYPES.getEntries().stream().
                map(DeferredHolder :: get).
                filter(entityType -> entityType instanceof BioEntityType<? extends Entity>).
                map(entityType -> (BioEntityType<? extends Entity>)entityType).
                forEach(entityType -> event.registerEntityRenderer(entityType, entityType.getRendererProvider()));
    }

    private static void registerMenuScreens(final RegisterMenuScreensEvent event)
    {
        Registration.BETypeReg.BLOCK_ENTITIES.getEntries().stream().map(DeferredHolder :: get).
                filter(type -> type instanceof Registration.BETypeReg.BioBlockEntityType<? extends BlockEntity, ?, ?>).
                map(type -> (Registration.BETypeReg.BioBlockEntityType<? extends BlockEntity, ?, ?>)type).
                filter(type -> type.getScreenConstructor() != null).
                forEach(type -> event.register(type.getMenuProvider().getType(), type.getScreenConstructor()));
    }

    private static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event)
    {
		event.registerLayerDefinition(SoldierModel.LAYER_LOCATION, SoldierModel :: createMesh);
		event.registerLayerDefinition(SergeantModel.LAYER_LOCATION, SergeantModel :: createMesh);
		event.registerLayerDefinition(CaptainModel.LAYER_LOCATION, CaptainModel :: createMesh);
		event.registerLayerDefinition(BlacksmithModel.LAYER_LOCATION, BlacksmithModel :: createMesh);
    }

	private static void registerParticleProviders(final RegisterParticleProvidersEvent event)
	{
		event.registerSprite(Registration.ParticleReg.HIVE_DECO.get(),
				new HiveDecoParticle.Provider());
	}
	
    private static void gatherData(final GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
	    ExistingFileHelper ext = event.getExistingFileHelper();
        gen.addProvider(true, new BioItemModelProvider(packOutput, ext));
		gen.addProvider(true, new BioBlockStateProvider(packOutput, ext));
        BlockTagsProvider btp = new BioBlockTagsProvider(packOutput, lookupProvider, ext);
        gen.addProvider(true, btp);
        gen.addProvider(true, new BioItemTagsProvider(packOutput, lookupProvider, btp, ext));
        gen.addProvider(true, new BioEntityTagsProvider(packOutput, lookupProvider, ext));
        gen.addProvider(true, new BioRecipeProvider(packOutput, lookupProvider));
        gen.addProvider(true, new BioSpriteSourceProvider(packOutput, lookupProvider, ext));
	    gen.addProvider(true, new BioMultiblockProvider.Runner(packOutput, lookupProvider));
		gen.addProvider(true, new BioGenomeTemplatesProvider.Runner(packOutput, lookupProvider));
        gen.addProvider(true, BioLootTableProvider.create(
                List.of(
                        new LootTableProvider.SubProviderEntry(BioBlockLoot :: new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(BioEntityLoot :: new, LootContextParamSets.ENTITY)),
                packOutput,
                lookupProvider));
		gen.addProvider(true, new BioParticleDescriptionProvider(packOutput, ext));

		BioRegistryData.register(new BioDamageTypesProvider());
        BioRegistryData.register(new BioBookProvider());
		BioRegistryData.register(new BioPalladinOrdersProvider());
        BioRegistryData.register(new BioWorldGenProvider());
		BioRegistryData.register(new BioGenomeProvider());
		BioRegistryData.register(new BioOrganicArmorProvider());

		DatapackBuiltinEntriesProvider entries = new DatapackBuiltinEntriesProvider(
				packOutput,
				lookupProvider,
				BioRegistryData.getBuilder(),
				Set.of(Database.MOD_ID));
		
        gen.addProvider(true, entries);
	    
	    gen.addProvider(true, new EnUsProvider(packOutput, entries.getRegistryProvider()));
	    
		gen.addProvider(true, new BioBiomeTagsProvider(packOutput, entries.getRegistryProvider(), ext));
		gen.addProvider(true, new BioDamageTypeTagsProvider(packOutput, entries.getRegistryProvider(), ext));
	    
	    BioRegistryData.clear();

        gen.addProvider(true, new BioSoundsProvider(packOutput, ext));
		
		event.createProvider(BioGlobalLootModifier :: new);
    }

    private static void setupModels (final ModelEvent.ModifyBakingResult event)
    {
        event.getModels().computeIfPresent(
                BlockModelShaper.stateToModelLocation(Registration.BlockReg.FLUID_STORAGE.get().defaultBlockState()),
                (location, bakedModel) -> new BioFluidStorageBakedModel(bakedModel));
    }
	
	private static void registerCustomTextures(final IEventBus modEventBus)
	{
		// BLOCK_ENTITIES
		modEventBus.addListener(BioCatcherRenderer :: registerTextures);
		modEventBus.addListener(BioChestRenderer :: registerTextures);
		modEventBus.addListener(BioCrusherRenderer :: registerTextures);
		modEventBus.addListener(BioForgeRenderer :: registerTextures);
		modEventBus.addListener(BioSqueezerRenderer :: registerTextures);
		modEventBus.addListener(BioStomachRenderer :: registerTextures);
		modEventBus.addListener(EggsDecoRenderer :: registerTextures);
		modEventBus.addListener(LureCampfireRenderer :: registerTextures);
		modEventBus.addListener(NorphSourceRenderer ::  registerTextures);
		// MULTIBLOCKS
		modEventBus.addListener(MultiblockChamberRenderer :: registerTextures);
		modEventBus.addListener(MultiblockChrysalisRenderer :: registerTextures);
		modEventBus.addListener(MultiblockMorpherRenderer :: registerTextures);
		modEventBus.addListener(MultiblockTurretRenderer :: registerTextures);
		// ENTITIES
		modEventBus.addListener(InfestorRenderer :: registerTextures);
		modEventBus.addListener(KsiggRenderer :: registerTextures);
		modEventBus.addListener(LarvaRenderer :: registerTextures);
		modEventBus.addListener(QueenGuardRenderer :: registerTextures);
		modEventBus.addListener(QueenRenderer :: registerTextures);
		modEventBus.addListener(SwarmlingRenderer :: registerTextures);
		modEventBus.addListener(TurretProjectileRenderer :: registerTextures);
		modEventBus.addListener(WorkerRenderer :: registerTextures);
		modEventBus.addListener(ZirisRenderer :: registerTextures);
		modEventBus.addListener(MelonMawRenderer :: registerTextures);
	}
}
