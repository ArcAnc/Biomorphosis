/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.block.*;
import com.arcanc.biomorphosis.content.block.block_entity.HiveDeco;
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockFluidStorageBlock;
import com.arcanc.biomorphosis.content.block.norph.source.NorphSourceBlock;
import com.arcanc.biomorphosis.content.item.renderer.MultiblockMorpherSpecialRenderer;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.generators.template.FaceRotation;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeckolibSpecialRenderer;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class SummaryModelProvider extends ModelProvider
{
    private static final ModelTemplate BLOCK = ModelTemplates.create("block", TextureSlot.PARTICLE);
    private static final TextureSlot PORT = TextureSlot.create("port");

    public SummaryModelProvider(PackOutput output)
    {
        super(output, Database.MOD_ID);
    }

    private void registerItemModels(@NotNull ItemModelGenerators itemModels)
    {
        itemModels.generateFlatItem(Registration.ItemReg.FLESH_PIECE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.QUEENS_BRAIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.CREATIVE_TAB_ICON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.WRENCH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.BOOK.get(), ModelTemplates.FLAT_ITEM);

        createBucketModel(Registration.FluidReg.BIOMASS, itemModels);
        createBucketModel(Registration.FluidReg.LYMPH, itemModels);
        createBucketModel(Registration.FluidReg.ADRENALINE, itemModels);

        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_QUEEN.getEggHolder().get(), 12654873, 7475473);
        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_KSIGG.getEggHolder().get(), MathHelper.ColorHelper.color(42, 34, 23), MathHelper.ColorHelper.color(14, 71, 20));
        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_LARVA.getEggHolder().get(), MathHelper.ColorHelper.color(81, 39, 6), MathHelper.ColorHelper.color(237, 159, 1));
        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_ZIRIS.getEggHolder().get(), MathHelper.ColorHelper.color(38, 29, 12), MathHelper.ColorHelper.color(76, 89, 34));
        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_INFESTOR.getEggHolder().get(), MathHelper.ColorHelper.color(19, 54, 34), MathHelper.ColorHelper.color(64, 94, 14));
        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_SWARMLING.getEggHolder().get(), MathHelper.ColorHelper.color(17, 91, 23), MathHelper.ColorHelper.color(42, 92, 77));
        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_QUEEN_GUARD.getEggHolder().get(), MathHelper.ColorHelper.color(0, 40, 2), MathHelper.ColorHelper.color(43, 3, 99));
        itemModels.generateSpawnEgg(Registration.EntityReg.MOB_WORKER.getEggHolder().get(), MathHelper.ColorHelper.color(34, 97, 13), MathHelper.ColorHelper.color(33, 26, 93));

        itemModels.generateFlatItem(Registration.ItemReg.FORGE_UPGRADE.get(), ModelTemplates.FLAT_ITEM);

        itemModels.itemModelOutput.accept(Registration.BlockReg.MULTIBLOCK_CHAMBER.asItem(),
                new SpecialModelWrapper.Unbaked(Database.rl("item/multiblock_chamber"),
                        new GeckolibSpecialRenderer.Unbaked()));

        itemModels.itemModelOutput.accept(Registration.BlockReg.MULTIBLOCK_MORPHER.asItem(),
                new SpecialModelWrapper.Unbaked(Database.rl( "item/multiblock_morpher"),
                        new MultiblockMorpherSpecialRenderer.Unbaked()));
    }

    private void registerBlockModels(@NotNull BlockModelGenerators blockModels)
    {
        blockModels.createTrivialCube(Registration.BlockReg.FLESH.get());
        createLureCampfireModel(blockModels);

        createNorphSource(blockModels);
        createStairs(Registration.BlockReg.NORPH_STAIRS.get(), blockModels);
        blockModels.createTrivialCube(Registration.BlockReg.NORPH.get());

        createMultifaceModel(Registration.BlockReg.NORPH_OVERLAY.get(), blockModels);

        createFluidModel(Registration.FluidReg.BIOMASS, blockModels);
        createFluidModel(Registration.FluidReg.LYMPH, blockModels);
        createFluidModel(Registration.FluidReg.ADRENALINE, blockModels);

        createFluidStorage(blockModels);
        createFluidTransmitter(blockModels);
        createCrusherModel(blockModels);
        createStomachModel(blockModels);
        createCatcherModel(blockModels);
        createForgeModel(blockModels);

        createMultiblockFluidStorage(blockModels);
        createMultiblockChamberModel(blockModels);

        blockModels.createTrivialCube(Registration.BlockReg.MULTIBLOCK_MORPHER.get());

        /*FIXME: проверить модель третьего пропса и светящегося мха. Там где-то ошибка в координатах*/
        createProps(blockModels);
        createGlowMoss(blockModels);

        createNorphedDirt(blockModels);
        blockModels.createTrivialCube(Registration.BlockReg.INNER.get());
        createRoofModel(blockModels);

        blockModels.createTrivialCube(Registration.BlockReg.TRAMPLED_DIRT.get());
        createDecoHiveModel(blockModels);
        createChestModel(blockModels);
        createHangingMoss(blockModels);
    }

    //------------------------------------------------------------------------------
    // ITEM MODELS
    //------------------------------------------------------------------------------
    private void createBucketModel(@NotNull Registration.FluidReg.FluidEntry fluidEntry, @NotNull ItemModelGenerators itemModels)
    {
        DynamicFluidContainerModel.Textures textures = new DynamicFluidContainerModel.Textures(
                Optional.of(fluidEntry.bucket().getId().withPrefix("block/")),
                Optional.of(Database.mineRl("item/bucket")),
                Optional.of(Database.neoRl("item/mask/bucket_fluid_drip")),
                Optional.of(Database.neoRl("item/mask/bucket_fluid_cover_drip")));
        itemModels.itemModelOutput.accept(fluidEntry.bucket().get(), new DynamicFluidContainerModel.Unbaked(
                textures,
                fluidEntry.still().get(),
                false,
                true,
                true));
    }
    //------------------------------------------------------------------------------
    // BLOCK MODELS
    //------------------------------------------------------------------------------

    private void createHangingMoss(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<HangingMossBlock> block = Registration.BlockReg.HANGING_MOSS;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("cutout").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                element(elementBuilder -> elementBuilder.
                        from(0.8f, 0, 8).
                        to(15.2f, 16, 8).
                        rotation(rotationBuilder -> rotationBuilder.
                            origin(8, 8, 8).
                            angle(45).
                            axis(Direction.Axis.Y).
                            rescale(true)).
                        shade(false).
                        allFacesExcept((direction, faceBuilder) -> faceBuilder.
                                uvs(0, 0, 16, 16).
                                texture(TextureSlot.ALL),
                                EnumSet.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST))).
                element(elementBuilder -> elementBuilder.
                        from(8, 0, 0.8f).
                        to(8, 16, 15.2f).
                        rotation(rotationBuilder -> rotationBuilder.
                            origin(8, 8, 8).
                            angle(45).
                            axis(Direction.Axis.Y).
                            rescale(true)).
                        shade(false).
                        allFacesExcept((direction, faceBuilder) -> faceBuilder.
                                uvs(0, 0, 16, 16).
                                texture(TextureSlot.ALL),
                                EnumSet.of(Direction.UP, Direction.DOWN, Direction.NORTH,Direction.SOUTH)));

        PropertyDispatch propertydispatch = PropertyDispatch.property(HangingMossBlock.TIP)
                .generate(
                        bool -> {
                            String s = bool ? "_tip" : "";
                            TextureMapping mapping = new TextureMapping().
                                    put(TextureSlot.ALL, blockLoc.withSuffix(s)).
                                    put(TextureSlot.PARTICLE, blockLoc.withSuffix(s));

                            ResourceLocation resourcelocation = new TexturedModel(mapping, template.build()).
                                    createWithSuffix(block.get(), s, blockModels.modelOutput);

                            return Variant.variant().with(VariantProperties.MODEL, resourcelocation);
                        }
                );
        blockModels.registerSimpleFlatItemModel(block.get());
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get()).with(propertydispatch));
    }

    private void createChestModel(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioChestBlock> block = Registration.BlockReg.CHEST;
        ResourceLocation blockLoc = BuiltInRegistries.BLOCK.getKey(block.get()).withPrefix("swarm_").withPrefix("block/");

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                        element(elementBuilder -> elementBuilder.
                                from(3, 2, 3).
                                to(13, 3, 13).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(7.25f, 8.75f, 8.5f, 8.875f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(6, 8.75f, 7.25f, 8.875f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(9.75f, 8.75f, 11, 8.875f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8.5f, 8.75f, 9.75f, 8.875f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(8.5f, 8.75f, 7.25f, 7.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(9.75f, 7.5f, 8.5f, 8.75f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2.5f, 3, 2.5f).
                                to(13.5f, 4, 13.5f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(8.875f, 1.375f, 10.25f, 1.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(7.5f, 1.375f, 8.875f, 1.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(11.625f, 1.375f, 13, 1.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10.25f, 1.375f, 11.625f, 1.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10.25f, 1.375f, 8.875f, 0)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(11.625f, 0, 10.25f, 1.375f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(3.5f, 1, 3.5f).
                                to(12.5f, 2, 12.5f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(7.125f, 10, 8.25f, 10.125f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(6, 10, 7.125f, 10.125f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(9.375f, 10, 10.5f, 10.125f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8.25f, 10, 9.375f, 10.125f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(8.25f, 10, 7.125f, 8.875f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(9.375f, 8.875f, 8.25f, 10)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2.5f, 11, 12.5f).
                                to(13.5f, 12, 13.5f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(7.625f, 1.625f, 9, 1.75f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(7.5f, 1.625f, 7.625f, 1.75f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(9.125f, 1.625f, 10.5f, 1.75f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(9, 1.625f, 9.125f, 1.75f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(9, 1.625f, 7.625f, 1.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10.375f, 1.5f, 9, 1.625f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2.5f, 11, 2.5f).
                                to(13.5f, 12, 3.5f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(7.625f, 1.875f, 9, 2)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(7.5f, 1.875f, 7.625f, 2)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(9.125f, 1.875f, 10.5f, 2)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(9, 1.875f, 9.125f, 2)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(9, 1.875f, 7.625f, 1.75f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10.375f, 1.75f, 9, 1.875f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2.5f, 11, 3.45f).
                                to(3.5f, 12, 12.45f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(9, 10.25f, 9.125f, 10.375f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10.375f, 10.25f, 11.5f, 10.375f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(10.25f, 10.25f, 10.375f, 10.375f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(9.125f, 10.25f, 10.25f, 10.375f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10.25f, 10.25f, 9.125f, 10.125f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(11.375f, 10.125f, 10.25f, 10.25f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(12.5f, 11, 3.5f).
                                to(13.5f, 12, 12.5f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(10.25f, 5.375f, 10.375f, 5.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(11.625f, 5.375f, 12.75f, 5.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(11.5f, 5.375f, 11.625f, 5.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10.375f, 5.375f, 11.5f, 5.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(11.5f, 5.375f, 10.375f, 5.25f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(12.625f, 5.25f, 11.5f, 5.375f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2, 4, 2).
                                to(14, 5, 14).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(8.5f, 3.5f, 10, 3.625f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(7, 3.5f, 8.5f, 3.625f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(11.5f, 3.5f, 13, 3.625f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10, 3.5f, 11.5f, 3.625f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10, 3.5f, 8.5f, 2)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(11.5f, 2, 10, 3.5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2, 0, 2).
                                to(14, 1, 14).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(8.5f, 5.125f, 10, 5.25f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(7, 5.125f, 8.5f, 5.25f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(11.5f, 5.125f, 13, 5.25f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10, 5.125f, 11.5f, 5.25f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10, 5.125f, 8.5f, 3.625f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(11.5f, 3.625f, 10, 5.125f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2, 10, 13).
                                to(14, 11, 14).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(7.125f, 5.375f, 8.625f, 5.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(7, 5.375f, 7.125f, 5.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(8.75f, 5.375f, 10.25f, 5.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8.625f, 5.375f, 8.75f, 5.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(8.625f, 5.375f, 7.125f, 5.25f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10.125f, 5.25f, 8.625f, 5.375f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2, 10, 2).
                                to(14, 11, 3).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(7.125f, 5.625f, 8.625f, 5.75f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(7, 5.625f, 7.125f, 5.75f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(8.75f, 5.625f, 10.25f, 5.75f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8.625f, 5.625f, 8.75f, 5.75f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(8.625f, 5.625f, 7.125f, 5.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10.125f, 5.5f, 8.625f, 5.625f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(13, 10, 3).
                                to(14, 11, 13).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(0, 9.125f, 0.125f, 9.25f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(1.5f, 9.125f, 2.75f, 9.25f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(1.375f, 9.125f, 1.5f, 9.25f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(0.125f, 9.125f, 1.375f, 9.25f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(1.375f, 9.125f, 0.125f, 9).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(2.625f, 9, 1.375f, 9.125f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2, 10, 2.95f).
                                to(3, 11, 12.95f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(2.75f, 9.125f, 2.875f, 9.25f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(4.25f, 9.125f, 5.5f, 9.25f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(4.125f, 9.125f, 4.25f, 9.25f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(2.875f, 9.125f, 4.125f, 9.25f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(4.125f, 9.125f, 2.875f, 9).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(5.375f, 9, 4.125f, 9.125f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(1.5f, 5, 1.5f).
                                to(14.5f, 6, 14.5f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(1.625f, 7.375f, 3.25f, 7.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(0, 7.375f, 1.625f, 7.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(4.875f, 7.375f, 6.5f, 7.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(3.25f, 7.375f, 4.875f, 7.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(3.25f, 7.375f, 1.625f, 5.75f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(4.875f, 5.75f, 3.25f, 7.375f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(1.5f, 9, 1.5f).
                                to(14.5f, 10, 14.5f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(8.125f, 7.375f, 9.75f, 7.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(6.5f, 7.375f, 8.125f, 7.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(11.375f, 7.375f, 13, 7.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(9.75f, 7.375f, 11.375f, 7.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(9.75f, 7.375f, 8.125f, 5.75f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(11.375f, 5.75f, 9.75f, 7.375f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(1, 8, 1).
                                to(15, 9, 15).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(1.75f, 3.75f, 3.5f, 3.875f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(0, 3.75f, 1.75f, 3.875f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(5.25f, 3.75f, 7, 3.875f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(3.5f, 3.75f, 5.25f, 3.875f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(3.5f, 3.75f, 1.75f, 2)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(5.25f, 2, 3.5f, 3.75f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(1, 6, 1).
                                to(15, 7, 15).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(1.75f, 5.625f, 3.5f, 5.75f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(0, 5.625f, 1.75f, 5.75f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(5.25f, 5.625f, 7, 5.75f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(3.5f, 5.625f, 5.25f, 5.75f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(3.5f, 5.625f, 1.75f, 3.875f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(5.25f, 3.875f, 3.5f, 5.625f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(0.5f, 7, 0.75f).
                                to(15.5f, 8, 15.75f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(1.875f, 1.875f, 3.75f, 2)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(0, 1.875f, 1.875f, 2)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(5.625f, 1.875f, 7.5f, 2)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(3.75f, 1.875f, 5.625f, 2)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(3.75f, 1.875f, 1.875f, 0)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(5.625f, 0, 3.75f, 1.875f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(4, 11.5f, 3).
                                to(12, 14.5f, 4).
                                rotation(rotationBuilder -> rotationBuilder.
                                    angle(22.5f).
                                    axis(Direction.Axis.X).
                                    origin(8, 12.5f, 3.5f)).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(0.125f, 10, 1.125f, 10.375f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(0, 10, 0.125f, 10.375f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(1.25f, 10, 2.25f, 10.375f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(1.125f, 10, 1.25f, 10.375f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(1.125f, 10, 0.125f, 9.875f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(2.125f, 9.875f, 1.125f, 10)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(4.14767f, 13.88793f, 4).
                                to(8.14767f, 14.88793f, 12).
                                rotation(rotationBuilder -> rotationBuilder.
                                    angle(22.5f).
                                    axis(Direction.Axis.Z).
                                    origin(5.64767f, 14.38793f, 8)).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(0, 9.375f, 0.125f, 9.875f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(1.125f, 9.375f, 0.125f, 9.25f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(1.125f, 9.375f, 1.25f, 9.875f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(2.125f, 9.25f, 1.125f, 9.375f).
                                        rotation(FaceRotation.UPSIDE_DOWN)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(0.125f, 9.375f, 1.125f, 9.875f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(1.25f, 9.375f, 2.25f, 9.875f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(3, 11.5f, 4).
                                to(4, 14.5f, 12).
                                rotation(rotationBuilder -> rotationBuilder.
                                    angle(-22.5f).
                                    axis(Direction.Axis.Z).
                                    origin(3.5f, 12.5f, 8)).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(2.25f, 10, 2.375f, 10.375f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(3.5f, 10, 4.5f, 10.375f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(3.375f, 10, 3.5f, 10.375f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(2.375f, 10, 3.375f, 10.375f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(3.375f, 10, 2.375f, 9.875f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(4.375f, 9.875f, 3.375f, 10).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(4, 11.5f, 12).
                                to(12, 14.5f, 13).
                                rotation(rotationBuilder -> rotationBuilder.
                                    angle(-22.5f).
                                    axis(Direction.Axis.X).
                                    origin(8, 12.5f, 12.5f)).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(4.625f, 10.25f, 5.625f, 10.625f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(4.5f, 10.25f, 4.625f, 10.625f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(5.75f, 10.25f, 6.75f, 10.625f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(5.625f, 10.25f, 5.75f, 10.625f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(5.625f, 10.25f, 4.625f, 10.125f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(6.625f, 10.125f, 5.625f, 10.25f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(8.14767f, 13.87793f, 4.01f).
                                to(12.14767f, 14.87793f, 12.01f).
                                rotation(rotationBuilder -> rotationBuilder.
                                    angle(-22.5f).
                                    axis(Direction.Axis.Z).
                                    origin(10.64767f, 14.38793f, 8.01f)).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(2.25f, 9.375f, 2.375f, 9.875f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(4.375f, 9.25f, 3.375f, 9.375f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(3.375f, 9.375f, 3.5f, 9.875f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(3.375f, 9.375f, 2.375f, 9.25f).
                                        rotation(FaceRotation.UPSIDE_DOWN)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(3.5f, 9.375f, 4.5f, 9.875f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(2.375f, 9.375f, 3.375f, 9.875f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(12, 11.5f, 4).
                                to(13, 14.5f, 12).
                                rotation(rotationBuilder -> rotationBuilder.
                                    angle(22.5f).
                                    axis(Direction.Axis.Z).
                                    origin(12.5f, 12.5f, 8)).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(7.875f, 10.25f, 8, 10.625f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(6.875f, 10.25f, 7.875f, 10.625f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(6.75f, 10.25f, 6.875f, 10.625f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8, 10.25f, 9, 10.625f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(7.875f, 10.25f, 6.875f, 10.125f).
                                        rotation(FaceRotation.CLOCKWISE_90)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(8.875f, 10.125f, 7.875f, 10.25f).
                                        rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2, 10.25f, 2).
                                to(14, 10.25f, 14).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(1.5f, 9, 3, 9)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(0, 9, 1.5f, 9)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(4.5f, 9, 6, 9)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(3, 9, 4.5f, 9)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(3, 9, 1.5f, 7.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(4.5f, 7.5f, 3, 9)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(11.75f, 12, 11.75f).
                                to(12.75f, 13, 12.75f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(5.625f, 9.375f, 5.75f, 9.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(5.5f, 9.375f, 5.625f, 9.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(5.875f, 9.375f, 6, 9.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(5.75f, 9.375f, 5.875f, 9.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(5.75f, 9.375f, 5.625f, 9.25f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(5.875f, 9.25f, 5.75f, 9.375f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(11.75f, 12, 3.25f).
                                to(12.75f, 13, 4.25f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(5.625f, 9.125f, 5.75f, 9.25f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(5.5f, 9.125f, 5.625f, 9.25f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(5.875f, 9.125f, 6, 9.25f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(5.75f, 9.125f, 5.875f, 9.25f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(5.75f, 9.125f, 5.625f, 9)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(5.875f, 9, 5.75f, 9.125f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(3.25f, 12, 3.25f).
                                to(4.25f, 13, 4.25f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(4.625f, 9.375f, 4.75f, 9.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(4.5f, 9.375f, 4.625f, 9.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(4.875f, 9.375f, 5, 9.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(4.75f, 9.375f, 4.875f, 9.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(4.75f, 9.375f, 4.625f, 9.25f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(4.875f, 9.25f, 4.75f, 9.375f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(3.25f, 12, 11.75f).
                                to(4.25f, 13, 12.75f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(5.125f, 9.375f, 5.25f, 9.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(5, 9.375f, 5.125f, 9.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(5.375f, 9.375f, 5.5f, 9.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(5.25f, 9.375f, 5.375f, 9.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(5.25f, 9.375f, 5.125f, 9.25f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(5.375f, 9.25f, 5.25f, 9.375f)).
                                texture(TextureSlot.ALL));

        ResourceLocation model = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, model)));
    }

    private void createDecoHiveModel(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioBaseEntityBlock<HiveDeco>> block = Registration.BlockReg.HIVE_DECO;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                element(elementBuilder -> elementBuilder.
                    from(0,0,0).
                    to(16, 16, 16).
                    face(Direction.NORTH, faceBuilder -> faceBuilder.
                            uvs(4, 4, 8, 8)).
                    face(Direction.EAST, faceBuilder -> faceBuilder.
                            uvs(0, 4, 4, 8)).
                    face(Direction.SOUTH, faceBuilder -> faceBuilder.
                            uvs(12, 4, 16, 8)).
                    face(Direction.WEST, faceBuilder -> faceBuilder.
                            uvs(8, 4, 12, 8)).
                    face(Direction.UP, faceBuilder -> faceBuilder.
                            uvs(8, 4, 4, 0)).
                    face(Direction.DOWN, faceBuilder -> faceBuilder.
                            uvs(12, 0, 8, 4)).
                    texture(TextureSlot.ALL));

        ResourceLocation model = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, model)));
    }

    private void createRoofModel(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioBaseBlock> block = Registration.BlockReg.ROOF;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.TOP, blockLoc.withSuffix("_top")).
                put(TextureSlot.SIDE, blockLoc.withSuffix("_middle")).
                put(TextureSlot.BOTTOM, blockLoc.withSuffix("_bot")).
                put(TextureSlot.PARTICLE, blockLoc.withSuffix("_middle"));

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.TOP).
                requiredTextureSlot(TextureSlot.SIDE).
                requiredTextureSlot(TextureSlot.BOTTOM).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                    from(0,0,0).
                    to(16, 16, 16).
                    allFaces((direction, faceBuilder) ->
                    {
                        faceBuilder.uvs(0, 0, 16, 16);
                        if (direction.getAxis().isHorizontal())
                            faceBuilder.texture(TextureSlot.SIDE);
                        else
                            if (direction == Direction.UP)
                                faceBuilder.texture(TextureSlot.TOP);
                            else
                                faceBuilder.texture(TextureSlot.BOTTOM);
                    }));

        ResourceLocation model = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, model)));

        mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc.withSuffix("_bot")).
                put(TextureSlot.PARTICLE, blockLoc.withSuffix("_bot"));

        template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                    from(0, 0, 0).
                    to(16, 16, 16).
                    allFaces((direction, faceBuilder) -> faceBuilder.
                            uvs(0, 0, 16, 16).
                            texture(TextureSlot.ALL)));

        block = Registration.BlockReg.ROOF_DIRT;
        ResourceLocation dirtModel = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, dirtModel)));

        DeferredBlock<StairBlock> stair = Registration.BlockReg.ROOF_STAIRS;
        blockLoc = TextureMapping.getBlockTexture(stair.get().base);

        mapping = new TextureMapping().
                put(TextureSlot.BOTTOM, blockLoc.withSuffix("_bot")).
                put(TextureSlot.TOP, blockLoc.withSuffix("_top")).
                put(TextureSlot.SIDE, blockLoc.withSuffix("_middle")).
                put(TextureSlot.PARTICLE, blockLoc.withSuffix("_middle"));

        ResourceLocation modelInner = ModelTemplates.STAIRS_INNER.create(stair.get(), mapping, blockModels.modelOutput);
        ResourceLocation modelStraight = ModelTemplates.STAIRS_STRAIGHT.create(stair.get(), mapping, blockModels.modelOutput);
        ResourceLocation modelOuter = ModelTemplates.STAIRS_OUTER.create(stair.get(), mapping, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createStairs(stair.get(), modelInner, modelStraight, modelOuter));
        blockModels.registerSimpleItemModel(stair.get(), modelStraight);

        DeferredBlock<SlabBlock> slab = Registration.BlockReg.ROOF_SLAB;

        mapping = new TextureMapping().
                put(TextureSlot.BOTTOM, blockLoc.withSuffix("_bot")).
                put(TextureSlot.TOP, blockLoc.withSuffix("_top")).
                put(TextureSlot.SIDE, blockLoc.withSuffix("_middle")).
                put(TextureSlot.PARTICLE, blockLoc.withSuffix("_middle"));

        template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.TOP).
                requiredTextureSlot(TextureSlot.SIDE).
                requiredTextureSlot(TextureSlot.BOTTOM).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                    from(0, 0, 0).
                    to(16, 8, 16).
                    allFaces((direction, faceBuilder) ->
                    {
                        if (direction.getAxis().isHorizontal())
                            faceBuilder.uvs(0, 0, 16, 8).
                                    texture(TextureSlot.SIDE);
                        else
                        {
                            faceBuilder.uvs(0, 0, 16, 16);
                            if (direction == Direction.UP)
                                faceBuilder.texture(TextureSlot.TOP);
                            else
                                faceBuilder.texture(TextureSlot.BOTTOM);
                        }
                    }));

        ResourceLocation bottom = new TexturedModel(mapping, template.build()).create(slab.get(), blockModels.modelOutput);
        ResourceLocation top = ModelTemplates.SLAB_TOP.create(slab.get(), mapping, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSlab(slab.get(), bottom, top, model));
        blockModels.registerSimpleItemModel(slab.get(), bottom);
    }

    private void createNorphedDirt(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioBaseBlock> block_0 = Registration.BlockReg.NORPHED_DIRT_0;
        DeferredBlock<BioBaseBlock> block_1 = Registration.BlockReg.NORPHED_DIRT_1;
        ResourceLocation blockLoc0 = TextureMapping.getBlockTexture(block_0.get());
        ResourceLocation blockLoc1 = TextureMapping.getBlockTexture(block_1.get());

        TextureMapping mapping0 = new TextureMapping().
                put(TextureSlot.ALL, blockLoc0).
                put(TextureSlot.PARTICLE, blockLoc0);

        TextureMapping mapping1 = new TextureMapping().
                put(TextureSlot.ALL, blockLoc1).
                put(TextureSlot.PARTICLE, blockLoc1);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                    from(0,0,0).
                    to(16, 16, 16).
                    allFaces((direction, faceBuilder) ->
                            faceBuilder.uvs(0, 0, 16, 16)).
                    texture(TextureSlot.ALL));

        ResourceLocation model0 = new TexturedModel(mapping0, template.build()).create(block_0.get(), blockModels.modelOutput);
        ResourceLocation model1 = new TexturedModel(mapping1, template.build()).create(block_1.get(), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.
                multiVariant(block_0.get(),
                Variant.variant().with(VariantProperties.MODEL, model0)));
        blockModels.itemModelOutput.accept(block_0.get().asItem(), ItemModelUtils.plainModel(model0));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.
                multiVariant(block_1.get(),
                Variant.variant().with(VariantProperties.MODEL, model1)));
        blockModels.itemModelOutput.accept(block_1.get().asItem(), ItemModelUtils.plainModel(model1));


        DeferredBlock<StairBlock> stairBlock0 = Registration.BlockReg.NORPHED_DIRT_STAIR_0;
        DeferredBlock<StairBlock> stairBlock1 = Registration.BlockReg.NORPHED_DIRT_STAIR_1;
        blockLoc0 = TextureMapping.getBlockTexture(stairBlock0.get().base);
        blockLoc1 = TextureMapping.getBlockTexture(stairBlock1.get().base);

        mapping0 = new TextureMapping().
                put(TextureSlot.BOTTOM, blockLoc0).
                put(TextureSlot.TOP, blockLoc0).
                put(TextureSlot.SIDE, blockLoc0).
                put(TextureSlot.PARTICLE, blockLoc0);

        mapping1 = new TextureMapping().
                put(TextureSlot.BOTTOM, blockLoc1).
                put(TextureSlot.TOP, blockLoc1).
                put(TextureSlot.SIDE, blockLoc1).
                put(TextureSlot.PARTICLE, blockLoc1);

        ResourceLocation modelInner0 = ModelTemplates.STAIRS_INNER.create(stairBlock0.get(), mapping0, blockModels.modelOutput);
        ResourceLocation modelStraight0 = ModelTemplates.STAIRS_STRAIGHT.create(stairBlock0.get(), mapping0, blockModels.modelOutput);
        ResourceLocation modelOuter0 = ModelTemplates.STAIRS_OUTER.create(stairBlock0.get(), mapping0, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createStairs(stairBlock0.get(), modelInner0, modelStraight0, modelOuter0));
        blockModels.registerSimpleItemModel(stairBlock0.get(), modelStraight0);

        ResourceLocation modelInner1 = ModelTemplates.STAIRS_INNER.create(stairBlock1.get(), mapping1, blockModels.modelOutput);
        ResourceLocation modelStraight1 = ModelTemplates.STAIRS_STRAIGHT.create(stairBlock1.get(), mapping1, blockModels.modelOutput);
        ResourceLocation modelOuter1 = ModelTemplates.STAIRS_OUTER.create(stairBlock1.get(), mapping1, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createStairs(stairBlock1.get(), modelInner1, modelStraight1, modelOuter1));
        blockModels.registerSimpleItemModel(stairBlock1.get(), modelStraight1);

        DeferredBlock<SlabBlock> slabBlock0 = Registration.BlockReg.NORPHED_DIRT_SLAB_0;
        DeferredBlock<SlabBlock> slabBlock1 = Registration.BlockReg.NORPHED_DIRT_SLAB_1;
        blockLoc0 = TextureMapping.getBlockTexture(block_0.get());
        blockLoc1 = TextureMapping.getBlockTexture(block_1.get());

        mapping0 = new TextureMapping().
                put(TextureSlot.BOTTOM, blockLoc0).
                put(TextureSlot.TOP, blockLoc0).
                put(TextureSlot.SIDE, blockLoc0).
                put(TextureSlot.PARTICLE, blockLoc0);

        mapping1 = new TextureMapping().
                put(TextureSlot.BOTTOM, blockLoc1).
                put(TextureSlot.TOP, blockLoc1).
                put(TextureSlot.SIDE, blockLoc1).
                put(TextureSlot.PARTICLE, blockLoc1);

        ResourceLocation bottom0 = ModelTemplates.SLAB_BOTTOM.create(slabBlock0.get(), mapping0, blockModels.modelOutput);
        ResourceLocation top0 = ModelTemplates.SLAB_TOP.create(slabBlock0.get(), mapping0, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSlab(slabBlock0.get(),bottom0, top0, model0));
        blockModels.registerSimpleItemModel(slabBlock0.get(), bottom0);

        ResourceLocation bottom1 = ModelTemplates.SLAB_BOTTOM.create(slabBlock1.get(), mapping1, blockModels.modelOutput);
        ResourceLocation top1 = ModelTemplates.SLAB_TOP.create(slabBlock1.get(), mapping1, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSlab(slabBlock1.get(),bottom1, top1, model1));
        blockModels.registerSimpleItemModel(slabBlock1.get(), bottom1);
    }

    private void createGlowMoss(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<GlowMossBlock> block = Registration.BlockReg.GLOW_MOSS;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                        renderType("translucent").
                        requiredTextureSlot(TextureSlot.ALL).
                        requiredTextureSlot(TextureSlot.PARTICLE).
                        guiLight(UnbakedModel.GuiLight.SIDE).
                        element(elementBuilder -> elementBuilder.
                                from(0f, 0f, 0.1f).
                                to(16f, 16f, 0.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(0f, 0f, 8f, 8f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(0f, 0f, 8f, 8f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(10f, 9f, 0.1f).
                                to(12f, 11f, 2.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(8f, 0f, 9f, 1f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(8f, 1f, 9f, 2f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(8f, 2f, 9f, 3f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8f, 3f, 9f, 4f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(9f, 5f, 8f, 4f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(9f, 5f, 8f, 6f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(4f, 12f, 0.1f).
                                to(6f, 14f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(8f, 6f, 9f, 7f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(9f, 7f, 9.5f, 8f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(8f, 7f, 9f, 8f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8f, 9f, 8.5f, 10f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10f, 8.5f, 9f, 8f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(9.5f, 9f, 8.5f, 9.5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(4f, 4f, 0.1f).
                                to(6f, 6f, 2.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(8f, 8f, 9f, 9f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(9f, 0f, 10f, 1f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(9f, 1f, 10f, 2f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(9f, 2f, 10f, 3f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10f, 4f, 9f, 3f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10f, 4f, 9f, 5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(12f, 2f, 0.1f).
                                to(14f, 4f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(9f, 5f, 10f, 6f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(9.5f, 7f, 10f, 8f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(9f, 6f, 10f, 7f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(8.5f, 9.5f, 9f, 10.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10f, 9f, 9f, 8.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10f, 9.5f, 9f, 10f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(11f, 5f, 0.1f).
                                to(12f, 6f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(9.5f, 9f, 10f, 9.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10f, 0f, 10.5f, 0.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(10f, 0.5f, 10.5f, 1f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10f, 1f, 10.5f, 1.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 2f, 10f, 1.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 2f, 10f, 2.5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(6f, 8f, 0.1f).
                                to(7f, 9f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(10f, 2.5f, 10.5f, 3f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10f, 3f, 10.5f, 3.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(10f, 3.5f, 10.5f, 4f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10f, 4f, 10.5f, 4.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 5f, 10f, 4.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 5f, 10f, 5.5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(13f, 13f, 0.1f).
                                to(14f, 14f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(10f, 5.5f, 10.5f, 6f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10f, 6f, 10.5f, 6.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(10f, 6.5f, 10.5f, 7f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10f, 7f, 10.5f, 7.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 8f, 10f, 7.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(8.5f, 10f, 8f, 10.5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(8f, 2f, 0.1f).
                                to(9f, 3f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(10f, 8f, 10.5f, 8.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10f, 8.5f, 10.5f, 9f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(9f, 10f, 9.5f, 10.5f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10f, 9f, 10.5f, 9.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(10f, 10.5f, 9.5f, 10f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 9.5f, 10f, 10f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2f, 10f, 0.1f).
                                to(3f, 11f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(10f, 10f, 10.5f, 10.5f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 0f, 11f, 0.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 0.5f, 11f, 1f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 1f, 11f, 1.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(11f, 2f, 10.5f, 1.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(11f, 2f, 10.5f, 2.5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(9f, 12f, 0.1f).
                                to(10f, 13f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 2.5f, 11f, 3f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 3f, 11f, 3.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 3.5f, 11f, 4f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 4f, 11f, 4.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(11f, 5f, 10.5f, 4.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(11f, 5f, 10.5f, 5.5f)).
                                texture(TextureSlot.ALL)).
                        element(elementBuilder -> elementBuilder.
                                from(2f, 2f, 0.1f).
                                to(3f, 3f, 1.1f).
                                face(Direction.NORTH, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 5.5f, 11f, 6f)).
                                face(Direction.EAST, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 6f, 11f, 6.5f)).
                                face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 6.5f, 11f, 7f)).
                                face(Direction.WEST, faceBuilder -> faceBuilder.
                                        uvs(10.5f, 7f, 11f, 7.5f)).
                                face(Direction.UP, faceBuilder -> faceBuilder.
                                        uvs(11f, 8f, 10.5f, 7.5f)).
                                face(Direction.DOWN, faceBuilder -> faceBuilder.
                                        uvs(8.5f, 10.5f, 8f, 11f)).
                                texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);

        MultiPartGenerator multipartgenerator = MultiPartGenerator.multiPart(block.get());
        Condition.TerminalCondition condition$terminalcondition = Util.make(
                Condition.condition(), condition -> BlockModelGenerators.MULTIFACE_GENERATOR.stream().
                        map(Pair :: getFirst).
                        map(MultifaceBlock :: getFaceProperty).
                        forEach(property ->
                        {
                            if (block.get().defaultBlockState().hasProperty(property))
                                condition.term(property, false);
                        }));

        for (Pair<Direction, Function<ResourceLocation, Variant>> pair : BlockModelGenerators.MULTIFACE_GENERATOR)
        {
            BooleanProperty booleanproperty = MultifaceBlock.getFaceProperty(pair.getFirst());
            Function<ResourceLocation, Variant> function = pair.getSecond();
            if (block.get().defaultBlockState().hasProperty(booleanproperty))
            {
                multipartgenerator.with(Condition.condition().term(booleanproperty, true), function.apply(modelLocation));
                multipartgenerator.with(condition$terminalcondition, function.apply(modelLocation));
            }
        }

        blockModels.blockStateOutput.accept(multipartgenerator);

        DeferredBlock<VineBlock> moss = Registration.BlockReg.MOSS;

        template = BLOCK.extend().
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                    from(0f, 0f, 0.1f).
                    to(16f, 16f, 0.1f).
                    face(Direction.NORTH, faceBuilder -> faceBuilder.
                        uvs(0f, 0f, 8f, 8f)).
                    face(Direction.SOUTH, faceBuilder -> faceBuilder.
                        uvs(0f, 0f, 8f, 8f)).
                        texture(TextureSlot.ALL));

        modelLocation = new TexturedModel(mapping, template.build()).create(moss.get(), blockModels.modelOutput);

        multipartgenerator = MultiPartGenerator.multiPart(moss.get());
        condition$terminalcondition = Util.make(
                Condition.condition(), condition -> BlockModelGenerators.MULTIFACE_GENERATOR.stream().
                        map(Pair :: getFirst).
                        map(MultifaceBlock :: getFaceProperty).
                        forEach(property ->
                {
                    if (moss.get().defaultBlockState().hasProperty(property))
                        condition.term(property, false);
                }));

        for (Pair<Direction, Function<ResourceLocation, Variant>> pair : BlockModelGenerators.MULTIFACE_GENERATOR)
        {
            BooleanProperty booleanproperty = MultifaceBlock.getFaceProperty(pair.getFirst());
            Function<ResourceLocation, Variant> function = pair.getSecond();
            if (moss.get().defaultBlockState().hasProperty(booleanproperty))
            {
                multipartgenerator.with(Condition.condition().term(booleanproperty, true), function.apply(modelLocation));
                multipartgenerator.with(condition$terminalcondition, function.apply(modelLocation));
            }
        }

        blockModels.blockStateOutput.accept(multipartgenerator);
    }

    private void createProps(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioBaseBlock> prop_0 = Registration.BlockReg.PROP_0;
        DeferredBlock<BioBaseBlock> prop_1 = Registration.BlockReg.PROP_1;
        DeferredBlock<BioBaseBlock> prop_2 = Registration.BlockReg.PROP_2;

        ResourceLocation textureLoc = Database.rl("props").withPrefix("block/");

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, textureLoc).
                put(TextureSlot.PARTICLE, textureLoc);

        ExtendedModelTemplateBuilder template0 = BLOCK.extend().
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(2f, 0f, 2f).
                        to(14f, 2f, 14f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(10f, 0f, 16f, 1f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(10f, 1f, 16f, 2f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(10f, 2f, 16f, 3f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(10f, 3f, 16f, 4f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(6f, 6f, 0f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(6f, 6f, 0f, 12f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4f, 2f, 4f).
                        to(12f, 4f, 12f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(10f, 4f, 14f, 5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(10f, 5f, 14f, 6f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(10f, 6f, 14f, 7f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(10f, 7f, 14f, 8f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(10f, 4f, 6f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(10f, 4f, 6f, 8f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(11f, 3.25f, 5f).
                        to(12f, 8.25f, 6f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-22.5f).
                                axis(Direction.Axis.Z).
                                origin(11.5f, 4.25f, 5.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11f, 12f, 11.5f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12f, 12f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(12f, 12f, 12.5f, 14.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(6f, 12.5f, 6.5f, 15f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4f, 14f, 3.5f, 13.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7f, 13.5f, 6.5f, 14f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7f, 1.5f, 4.75f).
                        to(8f, 6.5f, 5.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-45f).
                                axis(Direction.Axis.X).
                                origin(7.5f, 2.5f, 5.25f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12f, 12f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12f, 12f, 12.5f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(6f, 12.5f, 6.5f, 15f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(11f, 12f, 11.5f, 14.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4f, 14f, 3.5f, 13.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7f, 13.5f, 6.5f, 14f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(10f, 2f, 10.5f).
                        to(11f, 7f, 11.5f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45f).
                                axis(Direction.Axis.X).
                                origin(10.5f, 3f, 11f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12f, 12f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12f, 12f, 12.5f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(6f, 12.5f, 6.5f, 15f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(11f, 12f, 11.5f, 14.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4f, 14f, 3.5f, 13.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7f, 13.5f, 6.5f, 14f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL))
                .element(elementBuilder -> elementBuilder.
                        from(4f, 3.25f, 9f).
                        to(5f, 8.25f, 10f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(4.5f, 4.25f, 9.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11f, 12f, 11.5f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12f, 12f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(12f, 12f, 12.5f, 14.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(6f, 12.5f, 6.5f, 15f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4f, 14f, 3.5f, 13.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7f, 13.5f, 6.5f, 14f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5.75f, 5.75f, 5.5f).
                        to(7.75f, 7.75f, 7.5f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6.5f, 12.5f, 7.5f, 13.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(7.5f, 12.5f, 8.5f, 13.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8.5f, 12.5f, 9.5f, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 12f, 13.5f, 13f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 14f, 0f, 13f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(2f, 13f, 1f, 14f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(11.25f, 1.5f, 4.75f).
                        to(13.25f, 3.5f, 6.75f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6.5f, 12.5f, 7.5f, 13.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(7.5f, 12.5f, 8.5f, 13.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8.5f, 12.5f, 9.5f, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 12f, 13.5f, 13f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 14f, 0f, 13f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(2f, 13f, 1f, 14f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5.5f, 3.7f, 10.1f).
                        to(9.5f, 6.7f, 11.1f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45f).
                                axis(Direction.Axis.X).
                                origin(5.5f, 3.7f, 10.1f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6f, 11f, 8f, 12.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(9.5f, 12.5f, 10f, 14f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8f, 11f, 10f, 12.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 13f, 13f, 14.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(14f, 11f, 12f, 10.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 2f, 13.5f))
                        .texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2.67178f, 1.7f, 3.22045f).
                        to(6.67178f, 2.7f, 6.22045f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45f).
                                axis(Direction.Axis.Y).
                                origin(4.67178f, 2.2f, 4.72045f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 2f, 13.5f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(9.5f, 12.5f, 10f, 14f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(14f, 11f, 12f, 10.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 13f, 13f, 14.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(6f, 11f, 8f, 12.5f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(8f, 11f, 10f, 12.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13.25f, 1.75f, 9f).
                        to(13.25f, 4.75f, 11f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-22.5f).
                                axis(Direction.Axis.Z).
                                origin(13.25f, 3.25f, 10f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12f, 9f, 13f, 10.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(10f, 12f, 11f, 13.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.25f, 1.5f, 2.75f).
                        to(11.25f, 3.5f, 2.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-22.5f).
                                axis(Direction.Axis.X).
                                origin(10.25f, 2.5f, 2.75f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 5f, 14f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(5f, 13f, 6f, 14f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.25f, 1.75f, 12.75f).
                        to(9.25f, 3.75f, 12.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.X).
                                origin(8.25f, 2.75f, 12.75f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 5f, 14f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(5f, 13f, 6f, 14f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2.5f, 1.75f, 10f).
                        to(3.5f, 3.75f, 12f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(3f, 2.75f, 11f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(14f, 10.5f, 13f, 10f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(13f, 11f, 14f, 12f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(3.5f, 13.5f, 2.5f, 14f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(13f, 9f, 14f, 10f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(2f, 13.5f, 2.5f, 14.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(13f, 13f, 13.5f, 14f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8f, 5.5f, 8f).
                        to(10f, 6.5f, 10f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(9f, 6f, 9f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(14f, 10.5f, 13f, 10f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(2f, 13.5f, 2.5f, 14.5f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(3.5f, 13.5f, 2.5f, 14f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(13f, 13f, 13.5f, 14f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(13f, 9f, 14f, 10f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(13f, 11f, 14f, 12f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5f, 4f, 5f).
                        to(11f, 6f, 11f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(10f, 11f, 13f, 12f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(0f, 12f, 3f, 13f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(3f, 12f, 6f, 13f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12f, 8f, 15f, 9f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(9f, 11f, 6f, 8f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(12f, 8f, 9f, 11f)).
                        texture(TextureSlot.ALL));

        ExtendedModelTemplateBuilder template1 = BLOCK.extend().
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(2f, 0f, 2f).
                        to(14f, 2f, 14f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(10f, 0f, 16f, 1f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(10f, 1f, 16f, 2f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(10f, 2f, 16f, 3f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(10f, 3f, 16f, 4f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(6f, 6f, 0f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(6f, 6f, 0f, 12f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4f, 2f, 4f).
                        to(12f, 4f, 12f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(10f, 4f, 14f, 5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(10f, 5f, 14f, 6f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(10f, 6f, 14f, 7f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(10f, 7f, 14f, 8f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(10f, 4f, 6f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(10f, 4f, 6f, 8f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.75f, 0.75f, 2.25f).
                        to(9.75f, 5.75f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-45f).
                                axis(Direction.Axis.Z).
                                origin(9.25f, 1.75f, 2.75f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12f, 12f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12f, 12f, 12.5f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(6f, 12.5f, 6.5f, 15f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(11f, 12f, 11.5f, 14.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4f, 14f, 3.5f, 13.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7f, 13.5f, 6.5f, 14f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7f, 1f, 12.5f).
                        to(8f, 6f, 13.5f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(7.5f, 2f, 13f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12f, 12f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12f, 12f, 12.5f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(6f, 12.5f, 6.5f, 15f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(11f, 12f, 11.5f, 14.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4f, 14f, 3.5f, 13.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7f, 13.5f, 6.5f, 14f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5f, 1.75f, 7f).
                        to(6f, 6.75f, 8f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45f).
                                axis(Direction.Axis.Z).
                                origin(5.5f, 2.75f, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11f, 12f, 11.5f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12f, 12f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(12f, 12f, 12.5f, 14.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(6f, 12.5f, 6.5f, 15f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4f, 14f, 3.5f, 13.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7f, 13.5f, 6.5f, 14f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5f, 3.75f, 5.25f).
                        to(7f, 5.75f, 7.25f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45f).
                                axis(Direction.Axis.Y).
                                origin(6f, 4.75f, 6.25f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6.5f, 12.5f, 7.5f, 13.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(7.5f, 12.5f, 8.5f, 13.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8.5f, 12.5f, 9.5f, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 12f, 13.5f, 13f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 14f, 0f, 13f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(2f, 13f, 1f, 14f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.75f, 3.75f, 9.5f).
                        to(10.75f, 5.75f, 11.5f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6.5f, 12.5f, 7.5f, 13.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(7.5f, 12.5f, 8.5f, 13.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8.5f, 12.5f, 9.5f, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 12f, 13.5f, 13f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 14f, 0f, 13f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(2f, 13f, 1f, 14f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.26777f, 3.7f, 4.78934f).
                        to(11.26777f, 4.7f, 8.78934f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(9.76777f, 4.2f, 6.78934f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(9.5f, 12.5f, 10f, 14f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(14f, 11f, 12f, 10.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(12.5f, 13f, 13f, 14.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 2f, 13.5f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(6f, 11f, 8f, 12.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(8f, 11f, 10f, 12.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3.25f, 1f, 9f).
                        to(3.25f, 4f, 11f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(3.25f, 2.5f, 10f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12f, 9f, 13f, 10.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(10f, 12f, 11f, 13.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3.25f, 1.5f, 2.75f).
                        to(5.25f, 3.5f, 2.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-22.5f).
                                axis(Direction.Axis.X).
                                origin(4.25f, 2.5f, 2.75f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 5f, 14f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(5f, 13f, 6f, 14f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(10.25f, 1.5f, 12.75f).
                        to(12.25f, 3.5f, 12.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.X).
                                origin(11.25f, 2.5f, 12.75f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 5f, 14f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(5f, 13f, 6f, 14f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13.25f, 1.75f, 3.25f).
                        to(13.25f, 3.75f, 5.25f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-22.5f).
                                axis(Direction.Axis.Z).
                                origin(13.25f, 2.75f, 4.25f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(5f, 13f, 6f, 14f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(0f, 0f, 0f, 1f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(4f, 13f, 5f, 14f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1f, 0f, 0f, 0f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12.5f, 1.75f, 8.25f).
                        to(13.5f, 3.75f, 10.25f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-22.5f).
                                axis(Direction.Axis.Z).
                                origin(13f, 2.75f, 9.25f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(14f, 10.5f, 13f, 10f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(13f, 11f, 14f, 12f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(3.5f, 13.5f, 2.5f, 14f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(13f, 9f, 14f, 10f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(2f, 13.5f, 2.5f, 14.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(13f, 13f, 13.5f, 14f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5f, 3.5f, 9f).
                        to(7f, 4.5f, 11f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(6f, 4f, 10f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(14f, 10.5f, 13f, 10f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(2f, 13.5f, 2.5f, 14.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(3.5f, 13.5f, 2.5f, 14f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(13f, 13f, 13.5f, 14f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(13f, 9f, 14f, 10f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(13f, 11f, 14f, 12f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL));

        ExtendedModelTemplateBuilder template2 = BLOCK.extend().
                guiLight(UnbakedModel.GuiLight.SIDE).
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                element(elementBuilder -> elementBuilder.
                        from(2, 0, 2).
                        to(14, 2, 14).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(10, 0, 16, 1)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(10, 1, 16, 2)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(10, 2, 16, 3)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(10, 3, 16, 4)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(6, 6, 0, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(6, 6, 0, 12)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12.5f, 2, 6.75f).
                        to(13.5f, 3, 11.75f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(4, 14, 3.5f, 13.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12, 12, 12.5f, 14.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(7, 13.5f, 6.5f, 14).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(11, 12, 11.5f, 14.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(6, 12.5f, 6.5f, 15)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12, 12, 14.5f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7, 2, 3.75f).
                        to(12, 3, 4.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Y).
                                origin(8, 2.5f, 4.25f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11, 12, 11.5f, 14.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(4, 14, 3.5f, 13.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(12, 12, 12.5f, 14.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(7, 13.5f, 6.5f, 14).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(6, 12.5f, 6.5f, 15).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12, 12, 14.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.5f, 1, 9.5f).
                        to(8.5f, 6, 10.5f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45).
                                axis(Direction.Axis.X).
                                origin(8, 2, 10)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12, 12, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(12, 12, 12.5f, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(6, 12.5f, 6.5f, 15)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(11, 12, 11.5f, 14.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4, 14, 3.5f, 13.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7, 13.5f, 6.5f, 14).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5, 1.75f, 7).
                        to(6, 6.75f, 8).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(5.5f, 2.75f, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(11, 12, 11.5f, 14.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(11.5f, 12, 12, 14.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(12, 12, 12.5f, 14.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(6, 12.5f, 6.5f, 15)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(4, 14, 3.5f, 13.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(7, 13.5f, 6.5f, 14)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4, 1.75f, 3.25f).
                        to(6, 3.75f, 5.25f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45f).
                                axis(Direction.Axis.Y).
                                origin(5, 2.75f, 4.25f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6.5f, 12.5f, 7.5f, 13.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(7.5f, 12.5f, 8.5f, 13.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8.5f, 12.5f, 9.5f, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 12, 13.5f, 13)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1, 14, 0, 13)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(2, 13, 1, 14)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4.75f, 1.75f, 9.5f).
                        to(6.75f, 3.75f, 11.5f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6.5f, 12.5f, 7.5f, 13.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(7.5f, 12.5f, 8.5f, 13.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8.5f, 12.5f, 9.5f, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 12, 13.5f, 13)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1, 14, 0, 13)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(2, 13, 1, 14)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(10.75f, 1.75f, 4.5f).
                        to(12.75f, 3.75f, 6.5f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Y).
                                origin(10.75f, 1.75f, 4.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6.5f, 12.5f, 7.5f, 13.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(7.5f, 12.5f, 8.5f, 13.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8.5f, 12.5f, 9.5f, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 12, 13.5f, 13)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1, 14, 0, 13)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(2, 13, 1, 14)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.76777f, 2.45f, 8.28934f).
                        to(12.76777f, 3.45f, 11.28934f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45).
                                axis(Direction.Axis.Z).
                                origin(10.76777f, 2.95f, 9.78934f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(14, 11, 12, 10.5f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(9.5f, 12.5f, 10, 14).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(4, 13, 2, 13.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 13, 13, 14.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(8, 11, 10, 12.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(6, 11, 8, 12.5f).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3.017f, 0.45f, 12.0393f).
                        to(7.017f, 3.45f, 13.039f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45f).
                                axis(Direction.Axis.X).
                                origin(5.017f, 1.95f, 12.539f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(6, 11, 8, 12.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(9.5f, 12.5f, 10, 14)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(8, 11, 10, 12.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(12.5f, 13, 13, 14.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(14, 11, 12, 10.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(4, 13, 2, 13.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(10.25f, 1.5f, 12.75f).
                        to(12.25f, 3.5f, 12.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.X).
                                origin(11.25f, 2.5f, 12.75f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(4, 13, 5, 14)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(0, 0, 0, 1)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(5, 13, 6, 14)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(0, 0, 0, 1)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1, 0, 0, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1, 0, 0, 0)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13, 1.5f, 2).
                        to(13, 3.5f, 2).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(45).
                                axis(Direction.Axis.Y).
                                origin(13, 2.5f, 3)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(0, 0, 0, 1)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(5, 13, 6, 14)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(0, 0, 0, 1)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(4, 13, 5, 14)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(1, 0, 0, 0).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(1, 0, 0, 0).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7f, 1.75f, 5.75f).
                        to(9f, 3.75f, 6.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(-22.5f).
                                axis(Direction.Axis.X).
                                origin(8f, 2.75f, 6.25f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(13, 11, 14, 12).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(3.5f, 13.5f, 2.5f, 14).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(13, 9, 14, 10).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(14, 10.5f, 13, 10).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(2, 13.5f, 2.5f, 14.5f).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(13, 13, 13.5f, 14).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2.5f, 1.5f, 7.75f).
                        to(4.5f, 2.5f, 9.75f).
                        rotation(rotationBuilder -> rotationBuilder.
                                angle(22.5f).
                                axis(Direction.Axis.Z).
                                origin(3.5f, 2, 8.75f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.
                                uvs(14, 10.5f, 13, 10)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.
                                uvs(2, 13.5f, 2.5f, 14.5f).
                                rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.
                                uvs(3.5f, 13.5f, 2.5f, 14).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.
                                uvs(13, 13, 13.5f, 14).
                                rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.
                                uvs(13f, 9, 14, 10)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.
                                uvs(13, 11, 14, 12).
                                rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL));

        ResourceLocation model0Loc = new TexturedModel(mapping, template0.build()).create(prop_0.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(prop_0.get(), model0Loc));
        ResourceLocation model1Loc = new TexturedModel(mapping, template1.build()).create(prop_1.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(prop_1.get(), model1Loc));
        ResourceLocation model2Loc = new TexturedModel(mapping, template2.build()).create(prop_2.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(prop_2.get(), model2Loc));
    }

    private void createMultiblockChamberModel(@NotNull BlockModelGenerators blockModels)
    {
        blockModels.createTrivialCube(Registration.BlockReg.MULTIBLOCK_CHAMBER.get());
    }

    private void createMultiblockFluidStorage(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<MultiblockFluidStorageBlock> block = Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.SIDE, blockLoc.withSuffix("/side_transparent")).
                put(TextureSlot.UP, blockLoc.withSuffix("/top")).
                put(TextureSlot.DOWN, blockLoc.withSuffix("/top")).
                put(TextureSlot.PARTICLE, blockLoc.withSuffix("/side_transparent")).
                put(TextureSlot.CONTENT, blockLoc.withSuffix("/side"));

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("cutout").
                guiLight(UnbakedModel.GuiLight.SIDE).
                requiredTextureSlot(TextureSlot.SIDE).
                requiredTextureSlot(TextureSlot.UP).
                requiredTextureSlot(TextureSlot.DOWN).
                requiredTextureSlot(TextureSlot.PARTICLE).
                requiredTextureSlot(TextureSlot.CONTENT).
                element(elementBuilder -> elementBuilder.
                        from(0, 0, 0).
                        to(16, 16, 16).
                        allFaces((direction, faceBuilder) ->
                        {
                            faceBuilder.uvs(0, 0, 16, 16);
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.texture(TextureSlot.SIDE);
                            else if (direction == Direction.UP)
                                faceBuilder.texture(TextureSlot.UP);
                            else
                                faceBuilder.texture(TextureSlot.DOWN);
                        }));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block.get(), modelLocation));
    }

    private void createForgeModel(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioForgeBlock> block = Registration.BlockReg.FORGE;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        /*FIXME: Добавить вторую модель*/

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder singleModel = BLOCK.extend().
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(6,6,6).
                        to(10, 9, 7).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.5f, 8.125f, 8, 8.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 8.125f, 7.5f, 8.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.5f, 8.125f, 9, 8.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8, 8.125f, 8.5f, 8.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8, 8.125f, 7.5f, 7.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.5f, 7.625f, 8, 8.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(6, 12, 5).
                        to(10, 15, 8).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.375f, 1.125f, 8.875f, 1.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8, 1.125f, 8.375f, 1.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.25f, 1.125f, 9.75f, 1.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 1.125f, 9.25f, 1.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 1.125f, 8.375f, 0.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.375f, 0.75f, 8.875f, 1.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7, 12, 8).
                        to(9, 16, 10).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.X).origin(5.5f, 13.5f, 9)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(9.25f, 7, 9.5f, 7.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(9, 7, 9.25f, 7.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.75f, 7, 10, 7.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9.5f, 7, 9.75f, 7.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9.5f, 7, 9.25f, 6.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.75f, 6.75f, 9.5f, 7)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(6.9f, 9.5f, 12).
                        to(8.9f, 14.5f, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.X).origin(5.4f, 12, 13)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 8.875f, 3.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3, 8.875f, 3.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 8.875f, 4, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 8.875f, 3.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 8.875f, 3.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.625f, 3.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7, 5.5f, 13.5f).
                        to(9, 10.5f, 15.5f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.25f, 8.875f, 4.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4, 8.875f, 4.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.75f, 8.875f, 5, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 8.875f, 4.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.5f, 8.875f, 4.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.75f, 8.625f, 4.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7, 1.5f, 12).
                        to(9, 6.5f, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.X).origin(5.5f, 4, 13)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.25f, 8.875f, 5.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5, 8.875f, 5.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.75f, 8.875f, 6, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.5f, 8.875f, 5.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.5f, 8.875f, 5.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.75f, 8.625f, 5.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(-0.5f, 11.5f, 0.9f).
                        to(0.5f, 15.5f, 13.9f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-22.5f).axis(Direction.Axis.Z).origin(0, 12.5f, 7.4f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.625f, 3.75f, 8.75f, 4.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 3.75f, 8.625f, 4.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10.375f, 3.75f, 10.5f, 4.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.75f, 3.75f, 10.375f, 4.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.75f, 3.75f, 8.625f, 2.125f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.875f, 2.125f, 8.75f, 3.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 2, 1).
                        to(2, 12, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(22.5f).axis(Direction.Axis.Z).origin(1.5f, 7, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.125f, 7.375f, 5.25f, 8.625f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 7.375f, 5.125f, 8.625f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6.875f, 7.375f, 7, 8.625f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.25f, 7.375f, 6.875f, 8.625f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.25f, 7.375f, 5.125f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.375f, 5.75f, 5.25f, 7.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(15.5f, 11.5f, 0.9f).
                        to(16.5f, 15.5f, 13.9f).
                        rotation(rotationBuilder -> rotationBuilder.angle(22.5f).axis(Direction.Axis.Z).origin(16, 12.5f, 7.4f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.625f, 5.875f, 8.75f, 6.375f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 5.875f, 8.625f, 6.375f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10.375f, 5.875f, 10.5f, 6.375f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.75f, 5.875f, 10.375f, 6.375f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.75f, 5.875f, 8.625f, 4.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.875f, 4.25f, 8.75f, 5.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14, 2, 1).
                        to(15, 12, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-22.5f).axis(Direction.Axis.Z).origin(14.5f, 7, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.625f, 7.375f, 1.75f, 8.625f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 7.375f, 1.625f, 8.625f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.375f, 7.375f, 3.5f, 8.625f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.75f, 7.375f, 3.375f, 8.625f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.75f, 7.375f, 1.625f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.875f, 5.75f, 1.75f, 7.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 10, 1).
                        to(15, 10, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.5f, 5.75f, 3.25f, 5.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 5.75f, 1.5f, 5.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.75f, 5.75f, 6.5f, 5.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.25f, 5.75f, 4.75f, 5.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.25f, 5.75f, 1.5f, 4.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5, 4.25f, 3.25f, 5.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7, 1.75f, 4).
                        to(9, 9.75f, 6).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.25f, 8.75f, 8.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8, 8.75f, 8.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.75f, 8.75f, 9, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.5f, 8.75f, 8.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.5f, 8.75f, 8.25f, 8.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.75f, 8.5f, 8.5f, 8.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2, 1, 0).
                        to(14, 2, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2, 4.125f, 3.5f, 4.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 4.125f, 2, 4.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.5f, 4.125f, 7, 4.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 4.125f, 5.5f, 4.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 4.125f, 2, 2.125f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5, 2.125f, 3.5f, 4.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 0, 0).
                        to(16, 1, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2, 2, 4, 2.125f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 2, 2, 2.125f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6, 2, 8, 2.125f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4, 2, 6, 2.125f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4, 2, 2, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6, 0, 4, 2)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 9, 0).
                        to(15, 10.5f, 2).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.25f, 6.625f, 9, 6.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 6.625f, 7.25f, 6.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.25f, 6.625f, 11, 6.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9, 6.625f, 9.25f, 6.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9, 6.625f, 7.25f, 6.375f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(10.75f, 6.375f, 9, 6.625f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 9, 12).
                        to(15, 10.5f, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.25f, 6.625f, 9, 6.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 6.625f, 7.25f, 6.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.25f, 6.625f, 11, 6.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9, 6.625f, 9.25f, 6.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9, 6.625f, 7.25f, 6.375f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(10.75f, 6.375f, 9, 6.625f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 1.5f, 7).
                        to(3, 2.5f, 8).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 1.5f, 3).
                        to(3, 2.5f, 4).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 3.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 1.5f, 11).
                        to(3, 2.5f, 12).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 11.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13, 1.5f, 7).
                        to(16, 2.5f, 8).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13, 1.5f, 3).
                        to(16, 2.5f, 4).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 3.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13, 1.5f, 11).
                        to(16, 2.5f, 12).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 11.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL));

        ExtendedModelTemplateBuilder doubleModel = BLOCK.extend().
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(1.5f, 0.5f, 13.5f).
                        to(14.5f, 10.5f, 14.5f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 7.375f, 1.625f, 8.625f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.375f, 7.375f, 3.5f, 8.625f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.75f, 7.375f, 3.375f, 8.625f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.625f, 7.375f, 1.75f, 8.625f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.75f, 7.375f, 1.625f, 5.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.875f, 5.75f, 1.75f, 7.375f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3.5f, 6, 3).
                        to(7.5f, 9, 7).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.5f, 8.125f, 8, 8.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 8.125f, 7.5f, 8.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.5f, 8.125f, 9, 8.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8, 8.125f, 8.5f, 8.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8, 8.125f, 7.5f, 7.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.5f, 7.625f, 8, 8.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.5f, 2, 3).
                        to(12.5f, 5, 7).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.5f, 7.25f, 8, 7.625f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 7.25f, 7.5f, 7.625f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.5f, 7.25f, 9, 7.625f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8, 7.25f, 8.5f, 7.625f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8, 7.25f, 7.5f, 6.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.5f, 6.75f, 8, 7.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3.5f, 12, 5).
                        to(7.5f, 15, 8).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.375f, 1.125f, 8.875f, 1.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8, 1.125f, 8.375f, 1.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.25f, 1.125f, 9.75f, 1.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 1.125f, 9.25f, 1.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 1.125f, 8.375f, 0.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.375f, 0.75f, 8.875f, 1.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4.5f, 12, 8).
                        to(6.5f, 16, 10).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.X).origin(5.5f, 13.5f, 9)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(9.25f, 7, 9.5f, 7.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(9, 7, 9.25f, 7.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.75f, 7, 10, 7.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9.5f, 7, 9.75f, 7.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9.5f, 7, 9.25f, 6.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.75f, 6.75f, 9.5f, 7)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4.4f, 9.5f, 12).
                        to(6.4f, 14.5f, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.X).origin(5.4f, 12, 13)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 8.875f, 3.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3, 8.875f, 3.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 8.875f, 4, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 8.875f, 3.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 8.875f, 3.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.625f, 3.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4.5f, 5.5f, 13.5f).
                        to(6.5f, 10.5f, 15.5f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.25f, 8.875f, 4.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4, 8.875f, 4.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.75f, 8.875f, 5, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 8.875f, 4.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.5f, 8.875f, 4.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.75f, 8.625f, 4.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4.5f, 1.5f, 12).
                        to(6.5f, 6.5f, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.X).origin(5.5f, 4, 13)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.25f, 8.875f, 5.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5, 8.875f, 5.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.75f, 8.875f, 6, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.5f, 8.875f, 5.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.5f, 8.875f, 5.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.75f, 8.625f, 5.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.5f, 12, 5).
                        to(12.5f, 15, 8).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.875f, 0.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8, 0.375f, 8.375f, 0.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.25f, 0.375f, 9.75f, 0.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9.25f, 0.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.375f, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.375f, 0, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.5f, 12, 8).
                        to(11.5f, 16, 10).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.X).origin(10.5f, 13.5f, 9)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.25f, 8.875f, 6.5f, 9.375f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6, 8.875f, 6.25f, 9.375f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6.75f, 8.875f, 7, 9.375f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(6.5f, 8.875f, 6.75f, 9.375f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(6.5f, 8.875f, 6.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6.75f, 8.625f, 6.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.4f, 9.5f, 12).
                        to(11.4f, 14.5f, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.X).origin(10.4f, 12, 13)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2.25f, 8.875f, 2.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(2, 8.875f, 2.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(2.75f, 8.875f, 3, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(2.5f, 8.875f, 2.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2.5f, 8.875f, 2.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(2.75f, 8.625f, 2.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.5f, 5.5f, 13.5f).
                        to(11.5f, 10.5f, 15.5f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.25f, 8.875f, 1.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1, 8.875f, 1.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.75f, 8.875f, 2, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.5f, 8.875f, 1.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.5f, 8.875f, 1.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.75f, 8.625f, 1.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.4f, 1.5f, 12).
                        to(11.4f, 6.5f, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.X).origin(10.4f, 4, 13)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.25f, 8.875f, 0.5f, 9.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 8.875f, 0.25f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0.75f, 8.875f, 1, 9.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0.5f, 8.875f, 0.75f, 9.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.5f, 8.875f, 0.25f, 8.625f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(0.75f, 8.625f, 0.5f, 8.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(-0.5f, 11.5f, 0.9f).
                        to(0.5f, 15.5f, 13.9f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-22.5f).axis(Direction.Axis.Z).origin(0, 12.5f, 7.4f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.625f, 3.75f, 8.75f, 4.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 3.75f, 8.625f, 4.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10.375f, 3.75f, 10.5f, 4.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.75f, 3.75f, 10.375f, 4.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.75f, 3.75f, 8.625f, 2.125f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.875f, 2.125f, 8.75f, 3.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 2, 1).
                        to(2, 12, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(22.5f).axis(Direction.Axis.Z).origin(1.5f, 7, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.125f, 7.375f, 5.25f, 8.625f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 7.375f, 5.125f, 8.625f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6.875f, 7.375f, 7, 8.625f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.25f, 7.375f, 6.875f, 8.625f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.25f, 7.375f, 5.125f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.25f, 7.375f, 5.125f, 5.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(15.5f, 11.5f, 0.9f).
                        to(16.5f, 15.5f, 13.9f).
                        rotation(rotationBuilder -> rotationBuilder.angle(22.5f).axis(Direction.Axis.Z).origin(16, 12.5f, 7.4f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.625f, 5.875f, 8.75f, 6.375f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 5.875f, 8.625f, 6.375f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10.375f, 5.875f, 10.5f, 6.375f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.75f, 5.875f, 10.375f, 6.375f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.75f, 5.875f, 8.625f, 4.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.875f, 4.25f, 8.75f, 5.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14, 2, 1).
                        to(15, 12, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-22.5f).axis(Direction.Axis.Z).origin(14.5f, 7, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.625f, 7.375f, 1.75f, 8.625f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 7.375f, 1.625f, 8.625f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.375f, 7.375f, 3.5f, 8.625f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.75f, 7.375f, 3.375f, 8.625f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.75f, 7.375f, 1.625f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.875f, 5.75f, 1.75f, 7.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 10, 1).
                        to(15, 10, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.5f, 5.75f, 3.25f, 5.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 5.75f, 1.5f, 5.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.75f, 5.75f, 6.5f, 5.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.25f, 5.75f, 4.75f, 5.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.25f, 5.75f, 1.5f, 4.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5, 4.25f, 3.25f, 5.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4.5f, 1.75f, 4).
                        to(6.5f, 9.75f, 6).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.25f, 8.75f, 8.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8, 8.75f, 8.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.75f, 8.75f, 9, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.5f, 8.75f, 8.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.5f, 8.75f, 8.25f, 8.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.75f, 8.5f, 8.5f, 8.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.5f, 1.75f, 4).
                        to(11.5f, 9.75f, 6).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.25f, 8.75f, 7.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 8.75f, 7.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7.75f, 8.75f, 8, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7.75f, 8.75f, 8, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(7.5f, 8.75f, 7.25f, 8.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(7.75f, 8.5f, 7.5f, 8.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2, 1, 0).
                        to(14, 2, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2, 4.125f, 3.5f, 4.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 4.125f, 2, 4.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.5f, 4.125f, 7, 4.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 4.125f, 5.5f, 4.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 4.125f, 2, 2.125f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5, 2.125f, 3.5f, 4.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 0, 0).
                        to(16, 1, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2, 2, 4, 2.125f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 2, 2, 2.125f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6, 2, 8, 2.125f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4, 2, 6, 2.125f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4, 2, 2, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6, 0, 4, 2)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 0, 0).
                        to(16, 1, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2, 2, 4, 2.125f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 2, 2, 2.125f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6, 2, 8, 2.125f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4, 2, 6, 2.125f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4, 2, 2, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6, 0, 4, 2)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 9, 0).
                        to(15, 10.5f, 2).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.25f, 6.625f, 9, 6.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 6.625f, 7.25f, 6.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.25f, 6.625f, 11, 6.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9, 6.625f, 9.25f, 6.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9, 6.625f, 7.25f, 6.375f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(10.75f, 6.375f, 9, 6.625f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 9, 12).
                        to(15, 10.5f, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.25f, 6.625f, 9, 6.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7.125f, 6.625f, 7.25f, 6.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.125f, 6.625f, 10.875f, 6.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9, 6.625f, 9.125f, 6.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9, 6.625f, 7.25f, 6.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9, 6.625f, 7.25f, 6.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 1.5f, 7).
                        to(3, 2.5f, 8).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 1.5f, 3).
                        to(3, 2.5f, 4).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 3.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 1.5f, 11).
                        to(3, 2.5f, 12).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 11.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13, 1.5f, 7).
                        to(16, 2.5f, 8).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 7.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13, 1.5f, 3).
                        to(16, 2.5f, 4).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 3.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13, 1.5f, 11).
                        to(16, 2.5f, 12).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 11.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 0.375f, 8.875f, 0.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.375f, 0.375f, 8.5f, 0.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9, 0.375f, 9.375f, 0.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 9, 0.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.875f, 0.375f, 8.5f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9.25f, 0.25f, 8.875f, 0.375f)).
                        texture(TextureSlot.ALL));

        ResourceLocation singleLocation = new TexturedModel(mapping, singleModel.build()).create(block.get(), blockModels.modelOutput);
        ResourceLocation doubleLocation = new TexturedModel(mapping, doubleModel.build()).createWithSuffix(block.get(), "_double", blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get()).
                with(BlockModelGenerators.createHorizontalFacingDispatchAlt()).
                with(PropertyDispatch.property(BioForgeBlock.DOUBLE).
                        select(true, Variant.variant().with(VariantProperties.MODEL, doubleLocation)).
                        select(false, Variant.variant().with(VariantProperties.MODEL, singleLocation))));
    }

    private void createCatcherModel(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioCatcherBlock> block = Registration.BlockReg.CATCHER;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(3f, 1f, 13f).
                        to(13f, 3f, 15f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0f, 8.25f, 2.5f, 8.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5.5f, 8.25f, 6f, 8.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 5.5f, 8.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(2.5f, 8.25f, 3f, 8.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 2.5f, 5.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.5f, 5.75f, 3f, 8.25f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13f, 1f, 3f).
                        to(15f, 3f, 13f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2.5f, 8.25f, 3f, 8.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 8.25f, 2.5f, 8.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.5f, 8.25f, 6f, 8.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 5.5f, 8.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 2.5f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.5f, 5.75f, 3f, 8.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1.29289f, 1f, 2.70711f).
                        to(3.29289f, 3f, 12.70711f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2.5f, 8.25f, 3f, 8.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 8.25f, 2.5f, 8.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.5f, 8.25f, 6f, 8.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 5.5f, 8.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 2.5f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.5f, 5.75f, 3f, 8.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3f, 1f, 1f).
                        to(13f, 3f, 3f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 5.5f, 8.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(2.5f, 8.25f, 3f, 8.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0f, 8.25f, 2.5f, 8.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.5f, 8.25f, 6f, 8.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3f, 8.25f, 2.5f, 5.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.5f, 5.75f, 3f, 8.25f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0f, 1f, 10f).
                        to(1f, 4f, 11f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5f, 1f, 15f).
                        to(6f, 4f, 16f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(10f, 1f, 15f).
                        to(11f, 4f, 16f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(10f, 1f, 0f).
                        to(11f, 4f, 1f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5f, 1f, 0f).
                        to(6f, 4f, 1f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(15f, 1f, 10f).
                        to(16f, 4f, 11f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(15f, 1f, 5f).
                        to(16f, 4f, 6f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0f, 1f, 5f).
                        to(1f, 4f, 6f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 9f, 3.5f, 9.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3f, 9f, 3.25f, 9.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.75f, 9f, 4f, 9.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.75f, 9.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3.5f, 9f, 3.25f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.75f, 8.75f, 3.5f, 9f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2f, 0f, 2f).
                        to(14f, 1f, 14f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3f, 3f, 6f, 3.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 3f, 3f, 3.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9f, 3f, 12f, 3.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(6f, 3f, 9f, 3.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(6f, 3f, 3f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(9f, 0f, 6f, 3f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0f, 0f, 2f).
                        to(2f, 1f, 14f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 10f, 6.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6.5f, 6.25f, 9.5f, 6.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6f, 6.25f, 6.5f, 6.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(10f, 6.25f, 13f, 6.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 6.5f, 5.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12.5f, 5.75f, 9.5f, 6.25f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2f, 0f, 0f).
                        to(14f, 1f, 2f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.5f, 6.25f, 9.5f, 6.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6f, 6.25f, 6.5f, 6.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10f, 6.25f, 13f, 6.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 10f, 6.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 6.5f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12.5f, 5.75f, 9.5f, 6.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14f, 0f, 2f).
                        to(16f, 1f, 14f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6f, 6.25f, 6.5f, 6.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(10f, 6.25f, 13f, 6.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 10f, 6.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(6.5f, 6.25f, 9.5f, 6.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 6.5f, 5.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12.5f, 5.75f, 9.5f, 6.25f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2f, 0f, 14f).
                        to(14f, 1f, 16f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.5f, 6.25f, 9.5f, 6.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6f, 6.25f, 6.5f, 6.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10f, 6.25f, 13f, 6.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 10f, 6.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(9.5f, 6.25f, 6.5f, 5.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12.5f, 5.75f, 9.5f, 6.25f)).
                        texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block.get(), modelLocation));
    }

    private void createStomachModel(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioStomachBlock> block = Registration.BlockReg.STOMACH;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(12.5f, 13.5f, 3.25f).
                        to(13.5f, 14.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.375f, 3.25f, 4.5f, 3.375f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.25f, 3.25f, 4.375f, 3.375f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.125f, 3.25f, 4.25f, 3.375f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4f, 3.25f, 4.125f, 3.375f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.25f, 3.25f, 4.125f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.375f, 3.125f, 4.25f, 3.25f).rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12.5f, 11.5f, 3.25f).
                        to(13.5f, 12.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.375f, 3f, 4.5f, 3.125f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.25f, 3f, 4.375f, 3.125f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.125f, 3f, 4.25f, 3.125f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4f, 3f, 4.125f, 3.125f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.25f, 3f, 4.125f, 2.875f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.375f, 2.875f, 4.25f, 3f).rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13.5f, 9.5f, 3.25f).
                        to(14.5f, 16.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.375f, 6.875f, 0.5f, 7.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.25f, 6.875f, 0.375f, 7.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0.125f, 6.875f, 0.25f, 7.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0f, 6.875f, 0.125f, 7.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.25f, 6.875f, 0.125f, 6.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(0.375f, 6.75f, 0.25f, 6.875f).rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.5f, 11.5f, 3.25f).
                        to(9.5f, 12.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.375f, 3f, 5.5f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5f, 3f, 5.125f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.125f, 3f, 5.25f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.25f, 3f, 5.375f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.375f, 2.875f, 5.25f, 3f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.25f, 3f, 5.125f, 2.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.5f, 13.5f, 3.25f).
                        to(9.5f, 14.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.375f, 3.25f, 5.5f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5f, 3.25f, 5.125f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.125f, 3.25f, 5.25f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.25f, 3.25f, 5.375f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.375f, 3.125f, 5.25f, 3.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.25f, 3.25f, 5.125f, 3.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.5f, 9.5f, 3.25f).
                        to(8.5f, 16.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.375f, 6.875f, 1.5f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1f, 6.875f, 1.125f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.125f, 6.875f, 1.25f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.25f, 6.875f, 1.375f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.375f, 6.75f, 1.25f, 6.875f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 6.875f, 1.125f, 6.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.5f, 14.5f, 3.25f).
                        to(10.5f, 15.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.875f, 3f, 5f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.875f, 2.875f, 4.75f, 3f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.625f, 3f, 4.75f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.75f, 3f, 4.625f, 2.875f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.75f, 3f, 4.875f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.5f, 3f, 4.625f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(11.5f, 14.5f, 3.25f).
                        to(12.5f, 15.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.875f, 3.25f, 5f, 3.375f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.875f, 3.125f, 4.75f, 3.25f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.625f, 3.25f, 4.75f, 3.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.75f, 3.25f, 4.625f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.75f, 3.25f, 4.875f, 3.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.5f, 3.25f, 4.625f, 3.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.5f, 15.5f, 3f).
                        to(14.5f, 16.5f, 4f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.875f, 6.875f, 1f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.875f, 6.75f, 0.75f, 6.875f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0.625f, 6.875f, 0.75f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0.75f, 6.875f, 0.625f, 6.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.75f, 6.875f, 0.875f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(0.5f, 6.875f, 0.625f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(11.5f, 10.5f, 3.25f).
                        to(12.5f, 11.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.375f, 5.375f, 3.5f, 5.5f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.25f, 5.375f, 3.125f, 5.25f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.125f, 5.375f, 3.25f, 5.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.375f, 5.25f, 3.25f, 5.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3f, 5.375f, 3.125f, 5.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.25f, 5.375f, 3.375f, 5.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.5f, 10.5f, 3.25f).
                        to(10.5f, 11.5f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.875f, 3f, 6f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5.75f, 3f, 5.625f, 2.875f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.625f, 3f, 5.75f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.875f, 2.875f, 5.75f, 3f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.5f, 3f, 5.625f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.75f, 3f, 5.875f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.5f, 9.5f, 3f).
                        to(14.5f, 10.5f, 4f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.875f, 6.875f, 2f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.75f, 6.875f, 1.625f, 6.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.625f, 6.875f, 1.75f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.875f, 6.75f, 1.75f, 6.875f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.5f, 6.875f, 1.625f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.75f, 6.875f, 1.875f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12.5f, 13.5f, 2.25f).
                        to(13.5f, 14.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.375f, 3.25f, 4.5f, 3.375f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.25f, 3.25f, 4.375f, 3.375f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.125f, 3.25f, 4.25f, 3.375f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4f, 3.25f, 4.125f, 3.375f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.25f, 3.25f, 4.125f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.375f, 3.125f, 4.25f, 3.25f).rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12.5f, 11.5f, 2.25f).
                        to(13.5f, 12.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.375f, 3f, 4.5f, 3.125f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.25f, 3f, 4.375f, 3.125f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.125f, 3f, 4.25f, 3.125f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4f, 3f, 4.125f, 3.125f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.25f, 3f, 4.125f, 2.875f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.375f, 2.875f, 4.25f, 3f).rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(13.5f, 9.5f, 2.25f).
                        to(14.5f, 16.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.375f, 6.875f, 0.5f, 7.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.25f, 6.875f, 0.375f, 7.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0.125f, 6.875f, 0.25f, 7.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0f, 6.875f, 0.125f, 7.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.25f, 6.875f, 0.125f, 6.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(0.375f, 6.75f, 0.25f, 6.875f).rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.5f, 11.5f, 2.25f).
                        to(9.5f, 12.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.375f, 3f, 5.5f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5f, 3f, 5.125f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.125f, 3f, 5.25f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.25f, 3f, 5.375f, 3.125f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.375f, 2.875f, 5.25f, 3f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.25f, 3f, 5.125f, 2.875f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8.5f, 13.5f, 2.25f).
                        to(9.5f, 14.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.375f, 3.25f, 5.5f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5f, 3.25f, 5.125f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.125f, 3.25f, 5.25f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.25f, 3.25f, 5.375f, 3.375f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.375f, 3.125f, 5.25f, 3.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.25f, 3.25f, 5.125f, 3.125f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.5f, 9.5f, 2.25f).
                        to(8.5f, 16.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.375f, 6.875f, 1.5f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1f, 6.875f, 1.125f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.125f, 6.875f, 1.25f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.25f, 6.875f, 1.375f, 7.75f).rotation(FaceRotation.UPSIDE_DOWN)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.375f, 6.75f, 1.25f, 6.875f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 6.875f, 1.125f, 6.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.5f, 14.5f, 2.25f).
                        to(10.5f, 15.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.875f, 3f, 5f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.875f, 2.875f, 4.75f, 3f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.625f, 3f, 4.75f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.75f, 3f, 4.625f, 2.875f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.75f, 3f, 4.875f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.5f, 3f, 4.625f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(11.5f, 14.5f, 2.25f).
                        to(12.5f, 15.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.875f, 3.25f, 5f, 3.375f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.875f, 3.125f, 4.75f, 3.25f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.625f, 3.25f, 4.75f, 3.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.75f, 3.25f, 4.625f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.75f, 3.25f, 4.875f, 3.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4.5f, 3.25f, 4.625f, 3.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.5f, 15.5f, 2f).
                        to(14.5f, 16.5f, 3f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.875f, 6.875f, 1f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.875f, 6.75f, 0.75f, 6.875f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0.625f, 6.875f, 0.75f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0.75f, 6.875f, 0.625f, 6.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.75f, 6.875f, 0.875f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(0.5f, 6.875f, 0.625f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(11.5f, 10.5f, 2.25f).
                        to(12.5f, 11.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.375f, 5.375f, 3.5f, 5.5f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.25f, 5.375f, 3.125f, 5.25f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.125f, 5.375f, 3.25f, 5.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.375f, 5.25f, 3.25f, 5.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(3f, 5.375f, 3.125f, 5.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(3.25f, 5.375f, 3.375f, 5.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9.5f, 10.5f, 2.25f).
                        to(10.5f, 11.5f, 3.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.875f, 3f, 6f, 3.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5.75f, 3f, 5.625f, 2.875f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(5.625f, 3f, 5.75f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5.875f, 2.875f, 5.75f, 3f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.5f, 3f, 5.625f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.75f, 3f, 5.875f, 3.125f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.5f, 9.5f, 2f).
                        to(14.5f, 10.5f, 3f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.875f, 6.875f, 2f, 7.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.75f, 6.875f, 1.625f, 6.75f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.625f, 6.875f, 1.75f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.875f, 6.75f, 1.75f, 6.875f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.5f, 6.875f, 1.625f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.75f, 6.875f, 1.875f, 7.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(15f, 1f, 3.25f).
                        to(16f, 7f, 13.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.75f, 4.75f, 4.875f, 5.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 4.75f, 4.75f, 5.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6.125f, 4.75f, 6.25f, 5.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.875f, 4.75f, 6.125f, 5.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.875f, 4.75f, 4.75f, 3.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5f, 3.5f, 4.875f, 4.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8f, 1f, 3.25f).
                        to(9f, 7f, 13.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4.75f, 4.75f, 4.875f, 5.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 4.75f, 4.75f, 5.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6.125f, 4.75f, 6.25f, 5.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.875f, 4.75f, 6.125f, 5.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.875f, 4.75f, 4.75f, 3.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5f, 3.5f, 4.875f, 4.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8f, 1f, 2.25f).
                        to(16f, 7f, 3.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.375f, 3f, 7.375f, 3.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6.25f, 3f, 6.375f, 3.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7.5f, 3f, 8.5f, 3.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7.375f, 3f, 7.5f, 3.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(7.375f, 3f, 6.375f, 2.875f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.375f, 2.875f, 7.375f, 3f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1f, 1f, 3.25f).
                        to(8f, 3f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.375f, 3f, 7.25f, 3.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6.25f, 3f, 6.375f, 3.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7.375f, 3f, 8.25f, 3.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7.25f, 3f, 7.375f, 3.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(7.25f, 3f, 6.375f, 2.875f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.125f, 2.875f, 7.25f, 3f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1f, 1f, 12.25f).
                        to(8f, 3f, 13.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.375f, 3f, 7.25f, 3.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6.25f, 3f, 6.375f, 3.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7.375f, 3f, 8.25f, 3.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7.25f, 3f, 7.375f, 3.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(7.25f, 3f, 6.375f, 2.875f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.125f, 2.875f, 7.25f, 3f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8f, 1f, 13.25f).
                        to(16f, 7f, 14.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.375f, 3f, 7.375f, 3.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6.25f, 3f, 6.375f, 3.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7.5f, 3f, 8.5f, 3.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7.375f, 3f, 7.5f, 3.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(7.375f, 3f, 6.375f, 2.875f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.375f, 2.875f, 7.375f, 3f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1f, 3f, 3.25f).
                        to(7f, 4f, 13.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1.25f, 3.375f, 2f, 3.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 3.375f, 1.25f, 3.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.25f, 3.375f, 4f, 3.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(2f, 3.375f, 3.25f, 3.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2f, 3.375f, 1.25f, 2.125f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(2.75f, 2.125f, 2f, 3.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0f, 1f, 3.25f).
                        to(1f, 4f, 13.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.25f, 3.375f, 3.625f, 3.5f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(2f, 3.375f, 1.625f, 2.125f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.625f, 3.375f, 2f, 3.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(2.375f, 2.125f, 2f, 3.375f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.375f, 3.375f, 1.625f, 3.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(2f, 3.375f, 3.25f, 3.5f).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7f, 1f, 12.75f).
                        to(9f, 9f, 14.75f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.5f, 4f, 6.75f, 5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6.25f, 4f, 6.5f, 5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7f, 4f, 7.25f, 5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(6.75f, 4f, 7f, 5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(6.75f, 4f, 6.5f, 3.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(7f, 3.75f, 6.75f, 4f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3.5f, 1f, 2.25f).
                        to(5.5f, 7f, 4.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.75f, 4f, 7f, 4.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6.5f, 4f, 6.75f, 4.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6.25f, 4f, 6.5f, 4.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7f, 4f, 7.25f, 4.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(6.75f, 4f, 6.5f, 3.75f).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(7f, 3.75f, 6.75f, 4f).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2f, 3f, 0.25f).
                        to(8f, 9f, 6.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.75f, 6.25f, 7.5f, 7f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6f, 6.25f, 6.75f, 7f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.25f, 6.25f, 9f, 7f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7.5f, 6.25f, 8.25f, 7f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(7.5f, 6.25f, 6.75f, 5.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.25f, 5.5f, 7.5f, 6.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0f, 4.25f, 6.25f).
                        to(7f, 11.25f, 13.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.875f, 4.375f, 1.75f, 5.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 4.375f, 0.875f, 5.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(2.625f, 4.375f, 3.5f, 5.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.75f, 4.375f, 2.625f, 5.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.75f, 4.375f, 0.875f, 3.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(2.625f, 3.5f, 1.75f, 4.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5f, 7f, 9.25f).
                        to(12f, 14f, 16.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.875f, 4.375f, 1.75f, 5.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 4.375f, 0.875f, 5.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(2.625f, 4.375f, 3.5f, 5.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.75f, 4.375f, 2.625f, 5.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.75f, 4.375f, 0.875f, 3.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(2.625f, 3.5f, 1.75f, 4.375f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0f, 0f, 0.25f).
                        to(16f, 1f, 16.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2f, 2f, 4f, 2.125f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 2f, 2f, 2.125f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6f, 2f, 8f, 2.125f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4f, 2f, 6f, 2.125f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4f, 2f, 2f, 0f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6f, 0f, 4f, 2f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8f, 10f, 4.25f).
                        to(14f, 16f, 10.25f).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.75f, 6f, 1.5f, 6.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0f, 6f, 0.75f, 6.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(2.25f, 6f, 3f, 6.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.5f, 6f, 2.25f, 6.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.5f, 6f, 0.75f, 5.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(2.25f, 5.25f, 1.5f, 6f)).
                        texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, modelLocation)).
                with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private void createCrusherModel(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioCrusherBlock> block = Registration.BlockReg.CRUSHER;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("solid").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(3, 2, 3).
                        to(13, 9, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(2.5f, 7, 5, 8.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 7, 2.5f, 8.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7.5f, 7, 10, 8.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5, 7, 7.5f, 8.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5, 7, 2.5f, 4.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(7.5f, 4.5f, 5, 7)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 9, 0).
                        to(16, 11, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4, 4, 8, 4.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 4, 4, 4.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(12, 4, 16, 4.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8, 4, 12, 4.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8, 4, 4, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12, 0, 8, 4)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0,0, 0).
                        to(16, 2, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4, 4, 8, 4.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 4, 4, 4.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(12, 4, 16, 4.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8, 4, 12, 4.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8, 4, 4, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12, 0, 8, 4)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9, 12, 1).
                        to(13, 16, 15).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.5f, 12.25f, 4.5f, 13.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 12.25f, 3.5f, 13.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8, 12.25f, 9, 13.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 12.25f, 8, 13.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.5f, 12.25f, 3.5f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.5f, 8.75f, 4.5f, 12.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9, 12, 2).
                        to(13, 16, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(11, 14,8)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(13, 7.5f, 14, 8.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(10, 7.5f, 13, 8.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(14, 7.5f, 15, 8.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(14, 7.5f, 15, 8.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(14, 7.5f, 13, 4.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(15, 4.5f, 14, 7.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3, 12, 1).
                        to(7, 16, 15).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.5f, 12.25f, 4.5f, 13.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 12.25f, 3.5f, 13.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8, 12.25f, 9, 13.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 12.25f, 8, 13.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.5f, 12.25f, 3.5f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.5f, 8.75f, 4.5f, 12.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3, 12, 2).
                        to(7, 16, 14).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Z).origin(5, 14, 8)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(13, 7.5f, 14, 8.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(10, 7.5f, 13, 8.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(14, 7.5f, 15, 8.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(10, 6.5f, 13, 7.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(14, 7.5f, 13, 4.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(15, 4.5f, 14, 7.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14, 11,1).
                        to(16, 17, 15).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(12.5f, 12.25f, 13, 13.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(9, 12.25f, 12.5f, 13.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(13, 12.25f, 13.5f, 13.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9, 10.75f, 12.5f, 12.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(13, 12.25f, 12.5f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(13.5f, 8.75f, 13, 12.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 11,1).
                        to(2, 17, 15).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(12.5f, 12.25f, 13, 13.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(9, 12.25f, 12.5f, 13.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(13, 12.25f, 13.5f, 13.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9, 10.75f, 12.5f, 12.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(13, 12.25f, 12.5f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(13.5f, 8.75f, 13, 12.25f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 11,15).
                        to(16, 14, 16).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.25f, 13.5f, 4.25f, 14.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 13.5f, 0.25f, 14.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.5f, 13.5f, 8.5f, 14.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.25f, 13.5f, 4.5f, 14.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.25f, 13.5f, 0.25f, 13.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.25f, 13.25f, 4.25f, 13.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(0, 11,0).
                        to(16, 14, 1).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.25f, 13.5f, 4.25f, 14.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0, 13.5f, 0.25f, 14.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4.5f, 13.5f, 8.5f, 14.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.25f, 13.5f, 4.5f, 14.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4.25f, 13.5f, 0.25f, 13.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.25f, 13.25f, 4.25f, 13.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5, 2.86603f, 0.5f).
                        to(11, 5.86603f, 5.5f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.X).origin(8, 4.36603f, 3)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(9.75f, 15, 11.25f, 15.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.5f, 15, 9.75f, 15.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(12.5f, 15, 14, 15.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(11.25f, 15, 12.5f, 15.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(12.75f, 15, 11.25f, 13.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12.75f, 15, 11.25f, 13.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 16, 3).
                        to(2, 20, 4).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 1.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 0.5f, 0.75f, 1.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1.5f, 1.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1, 0.5f, 1.25f, 1.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 0.25f, 1, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 16, 9).
                        to(2, 20, 10).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 1.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 0.5f, 0.75f, 1.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1.5f, 1.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1, 0.5f, 1.25f, 1.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 0.25f, 1, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 17, 3).
                        to(2, 20, 4).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 0.5f, 0.75f, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1.5f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1, 0.5f, 1.25f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 0.25f, 1, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 17, 9).
                        to(2, 20, 10).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 0.5f, 0.75f, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1.5f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1, 0.5f, 1.25f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 0.25f, 1, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 17, 12).
                        to(2, 20, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 0.5f, 0.75f, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1.5f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1, 0.5f, 1.25f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 0.25f, 1, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 17, 6).
                        to(2, 20, 7).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 0.5f, 0.75f, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1.5f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1, 0.5f, 1.25f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 0.25f, 1, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14, 17, 12).
                        to(15, 20, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.5f, 0.5f, 1.25f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 0.5f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 0.25f, 1.25f, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14, 17, 6).
                        to(15, 20, 7).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.5f, 0.5f, 1.25f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 0.5f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 0.25f, 1.25f, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14, 17, 3).
                        to(15, 20, 4).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.5f, 0.5f, 1.25f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 0.5f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 0.25f, 1.25f, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(14, 17, 9).
                        to(15, 20, 10).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(1, 0.5f, 0.75f, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.25f, 0.5f, 1, 1.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.5f, 0.5f, 1.25f, 1.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 0.5f, 1.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(0.75f, 0.5f, 1, 0.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 0.25f, 1.25f, 0.5f)).
                        texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, modelLocation)).
                with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private void createFluidTransmitter(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioFluidTransmitterBlock> block = Registration.BlockReg.FLUID_TRANSMITTER;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(4,0, 4).
                        to(12, 2, 12).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(4, 0, 8, 2)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4, 2, 8, 4)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4, 4, 8, 6)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4, 8, 0, 0)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4, 8, 0, 0)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(4, 0, 0, 8).rotation(FaceRotation.UPSIDE_DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12, 0, 4).
                        to(14, 1, 12).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(14.5f, 0, 15.5f, 1)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.5f, 6, 12.5f, 7)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(13, 0, 14, 1)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.5f, 8, 12.5f, 9)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(12.5f, 4.75f, 8.5f, 3).rotation(FaceRotation.COUNTERCLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.5f, 2, 12.5f, 0).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2, 0, 4).
                        to(4, 1, 12).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(13, 0, 14, 1)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.5f, 6, 12.5f, 7)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(14.5f, 0, 15.5f, 1)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.5f, 8, 12.5f, 9)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(12.5f, 5, 8.5f, 3).rotation(FaceRotation.CLOCKWISE_90)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.5f, 2, 12.5f, 0).rotation(FaceRotation.CLOCKWISE_90)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4, 0, 2).
                        to(12, 1, 4).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 8, 12.5f, 9)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(13, 0, 14, 1)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.5f, 6, 12.5f, 7)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(14.5f, 0, 15.5f, 1)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(12.5f, 5, 8.5f, 3)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12.5f, 0, 8.5f, 2)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4, 0, 12).
                        to(12, 1, 14).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(8.5f, 6, 12.5f, 7)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(13, 0, 14, 1)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.5f, 8, 12.5f, 9)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(14.5f, 0, 15.5f, 1)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(12.5f, 5, 8.5f, 3)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(12.5f, 0, 8.5f, 2)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5, 2, 10).
                        to(6, 3, 11).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3, 8, 3.5f,9)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 8, 4, 9)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4, 8, 4.5f, 9)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 8, 5, 9)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.5f, 9, 5, 8)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6, 8, 5.5f, 9)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(11, 2, 8).
                        to(12, 3, 9).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3, 8, 3.5f, 9)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 8, 4, 9)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4, 8, 4.5f, 9)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 8, 5, 9)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.5f, 9, 5, 8)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6, 8, 5.5f, 9)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(6, 2, 4).
                        to(7, 3, 5).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3, 8, 3.5f, 9)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 8, 4, 9)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(4, 8, 4.5f, 9)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 8, 5, 9)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5.5f, 9, 5, 8)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(6, 8, 5.5f, 9)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(4, 2, 6).
                        to(6, 4, 8).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3, 10, 4, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.5f, 12, 5.5f, 10)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3, 12, 4, 14)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(4.5f, 14, 5.5f, 12)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(4, 16, 3, 14)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(5.5f, 16, 4.5f, 14)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8, 2, 10).
                        to(10, 4, 12).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6, 10, 7, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(6, 12, 7, 14)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(6, 14, 7, 16)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7.5f, 10, 8.5f, 12)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.5f, 14, 7.5f, 12)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.5f, 14, 7.5f, 16)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(8, 2, 4).
                        to(11, 5, 7).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(12.5f, 2, 14, 5)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(14.5f, 2, 16, 5)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(12.5f, 5, 14, 8)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(14.5f, 5, 16, 8)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(14, 11, 12.5f, 8)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(16, 8, 14.5f, 11)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3, 1, 9).
                        to(4, 4, 10).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 9, 0.5f, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 9, 1, 12)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1, 9, 1.5f, 12)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0, 12, 0.5f, 15)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2, 10, 1.5f, 9)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 12, 0.5f, 13)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(9, 1, 3).
                        to(10, 4, 4).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 9, 0.5f, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 9, 1, 12)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1, 9, 1.5f, 12)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0, 12, 0.5f, 15)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2, 10, 1.5f, 9)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 12, 0.5f, 13)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(5, 1, 3).
                        to(6, 4, 4).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 9, 0.5f, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 9, 1, 12)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1, 9, 1.5f, 12)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0, 12, 0.5f, 15)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2, 10, 1.5f, 9)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 12, 0.5f, 13)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12, 1, 5).
                        to(13, 4, 6).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 9, 0.5f, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 9, 1, 12)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1, 9, 1.5f, 12)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0, 12, 0.5f, 15)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2, 10, 1.5f, 9)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 12, 0.5f, 13)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(12, 1, 10).
                        to(13, 4, 11).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 9, 0.5f, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 9, 1, 12)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1, 9, 1.5f, 12)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0, 12, 0.5f, 15)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2, 10, 1.5f, 9)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 12, 0.5f, 13)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(6, 1, 12).
                        to(7, 4, 13).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 9, 0.5f, 12)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(0.5f, 9, 1, 12)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1, 9, 1.5f, 12)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0, 12, 0.5f, 15)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(2, 10, 1.5f, 9)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1, 12, 0.5f, 13)).
                        texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, modelLocation)).
                with(PropertyDispatch.property(BioFluidTransmitterBlock.FACING).
                        select(Direction.DOWN, Variant.variant()).
                        select(Direction.UP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0)).
                        select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).
                        select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).
                        select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))));
    }

    private void createFluidStorage(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<BioFluidStorageBlock> block = Registration.BlockReg.FLUID_STORAGE;
        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());


        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(PORT, Database.rl(blockPrefix("port"))).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                renderType("translucent").
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(PORT).
                requiredTextureSlot(TextureSlot.PARTICLE).
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(3, 2, 3).
                        to(13, 14, 13).
                        allFacesExcept((direction, faceBuilder) ->
                                faceBuilder.uvs(7.5f, 0, 10, 6).texture(TextureSlot.ALL), EnumSet.of(Direction.UP, Direction.DOWN))).
                element(elementBuilder -> elementBuilder.
                        from(1, 14, 1).
                        to(15, 15.999f, 15).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(7.5f, 6.5f + direction.get2DDataValue() * 1.5f, 11, 7.5f + direction.get2DDataValue() * 1.5f);
                            else
                            {
                                faceBuilder.uvs(3.5f, 14.5f * direction.get3DDataValue(), 0, 7 + direction.get3DDataValue() * 0.5f);
                                if (direction == Direction.UP)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(elementBuilder -> elementBuilder.
                        from(1, 0.001f, 1).
                        to(15, 2, 15).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(7.5f, 6.5f + direction.get2DDataValue() * 1.5f, 11, 7.5f + direction.get2DDataValue() * 1.5f);
                            else
                            {
                                faceBuilder.uvs(7.25f, 14.5f * direction.get3DDataValue(), 3.75f, 7 + direction.get3DDataValue() * 0.5f);
                                if (direction == Direction.DOWN)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(elementBuilder -> elementBuilder.
                        from(2,2,2).
                        to(4,5, 4).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 3, 10.25f, 2);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.DOWN))).
                element(elementBuilder -> elementBuilder.
                        from(2,11,2).
                        to(4,14, 4).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 2, 10.25f, 3);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.UP))).
                element(elementBuilder -> elementBuilder.
                        from(2,11,12).
                        to(4,14, 14).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 2, 10.25f, 3);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.UP))).
                element(elementBuilder -> elementBuilder.
                        from(2,2,12).
                        to(4,5, 14).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 3, 10.25f, 2);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.DOWN))).
                element(elementBuilder -> elementBuilder.
                        from(12,11,12).
                        to(14,14, 14).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 2, 10.25f, 3);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.UP))).
                element(elementBuilder -> elementBuilder.
                        from(12,2,12).
                        to(14,5, 14).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 3, 10.25f, 2);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.DOWN))).
                element(elementBuilder -> elementBuilder.
                        from(12,11,2).
                        to(14,14, 4).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 2, 10.25f, 3);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.UP))).
                element(elementBuilder -> elementBuilder.
                        from(12,2,2).
                        to(14,5, 4).
                        allFacesExcept((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
                            else
                                faceBuilder.uvs(10.75f, 3, 10.25f, 2);
                            faceBuilder.texture(TextureSlot.ALL);
                        }, EnumSet.of(Direction.DOWN))).
                element(elementBuilder -> elementBuilder.
                        from(1,0f,1).
                        to(15, 0f, 15).
                        allFacesExcept((direction, faceBuilder) ->
                                    faceBuilder.uvs(1,1,15,15).texture(PORT).tintindex(1).cullface(direction),
                                EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP))).
                element(elementBuilder -> elementBuilder.
                        from(1,16,1).
                        to(15, 16, 15).
                        allFacesExcept((direction, faceBuilder) ->
                                    faceBuilder.uvs(1,1,15,15).texture(PORT).tintindex(1).cullface(direction),
                                EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN)));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block.get(), modelLocation));
    }

    private void createFluidModel(@NotNull Registration.FluidReg.FluidEntry fluid, @NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<LiquidBlock> block = fluid.block();

        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.PARTICLE, fluid.still().getId());

        ExtendedModelTemplateBuilder template = ModelTemplates.PARTICLE_ONLY.
                extend().
                renderType("translucent").
                guiLight(UnbakedModel.GuiLight.SIDE);

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block.get(), modelLocation));
    }

    private void createNorphSource(@NotNull BlockModelGenerators blockModels)
    {
        DeferredBlock<NorphSourceBlock> block = Registration.BlockReg.NORPH_SOURCE;

        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());
        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.ALL, blockLoc).
                put(TextureSlot.PARTICLE, blockLoc);

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                parent(ResourceLocation.withDefaultNamespace(blockPrefix("block"))).
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                renderType("translucent").
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(elementBuilder -> elementBuilder.
                        from(4, 7.005f, 5).
                        to(11, 14.005f, 11).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(6.5f, 6.75f, 8.25f, 8.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(5, 7.75f, 6.5f, 9.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(7, 2, 8.75f, 3.75f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(8.25f, 5.5f, 9.75f, 7.25f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(10, 8.75f, 8.25f, 7.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(10.25f, 0, 8.5f, 1.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2, -1.995f, 6).
                        to(9, 8.005f, 10).
                        rotation(rotationBuilder -> rotationBuilder.angle(-22.5f).axis(Direction.Axis.Z).origin(5.5f, 3.005f, 8)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(3.5f, 0, 5.25f, 2.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.5f, 8.5f, 2.5f, 11)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(3.5f, 2.5f, 5.25f, 5)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(6.5f, 8.5f, 7.5f, 11)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(11.5f, 3.5f, 9.75f, 2.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(11.5f, 5.5f, 9.75f, 6.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(6, -1.995f, 7).
                        to(13, 2.005f, 15).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.X).origin(10.5f, 0.005f, 12.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(10, 3.5f, 11.75f, 4.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.25f, 9.5f, 6.25f, 10.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10, 4.5f, 11.75f, 5.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(9.75f, 1.5f, 11.75f, 2.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.25f, 6.75f, 6.5f, 4.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(8.5f, 0, 6.75f, 2)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(6, -2.93434f, 2.43934f).
                        to(13, 5.06566f, 6.43934f).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.X).origin(10, 1.06566f, 4.43934f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.25f, 2.75f, 7, 4.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(8.75f, 1.5f, 9.75f, 3.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(1.5f, 6.5f, 3.25f, 8.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(3.25f, 9.5f, 4.25f, 11.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(11.5f, 9.75f, 9.75f, 8.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(11.5f, 9.75f, 9.75f, 10.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(7.64645f, -1.995f, 4.49645f).
                        to(14.64645f, 11.005f, 11.49645f).
                        rotation(rotationBuilder -> rotationBuilder.angle(22.5f).axis(Direction.Axis.Z).origin(11.14645f, 2.505f, 7.99645f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(0, 0, 1.75f, 3.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(1.75f, 0, 3.5f, 3.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0, 3.25f, 1.75f, 6.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(1.75f, 3.25f, 3.5f, 6.5f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(5, 9.5f, 3.25f, 7.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(10, 3.75f, 8.25f, 5.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(3.25f, 0.005f, 8.5f).
                        to(6.25f, 11.005f, 14.25f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Y).origin(5.25f, -2, 11.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(7.5f, 8.5f, 8.25f, 11.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(3.5f, 5, 5, 7.75f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(8.25f, 8.75f, 9, 11.5f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5, 5, 6.5f, 7.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(1.5f, 10.75f, 0.75f, 9.25f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(10.75f, 6.5f, 10, 8)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1.75f, 0.005f, 3.5f).
                        to(7.5f, 11.005f, 6.5f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Y).origin(4.5f, -2, 5.5f)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(5.25f, 0, 6.75f, 2.75f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(9, 8.75f, 9.75f, 11.5f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(0, 6.5f, 1.5f, 9.25f)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(0, 9.25f, 0.75f, 12)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(11.5f, 8.75f, 10, 8)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(11.75f, 0, 10.25f, 0.75f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2, 8, 12).
                        to(4, 10, 15).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Y).origin(3, 8, 14)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(10.75f, 7, 11.25f, 7.5f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(4.25f, 10.5f, 5, 11)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10.75f, 7.5f, 11.25f, 8)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(5, 10.5f, 5.75f, 11)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(6.25f, 11.25f, 5.75f, 10.5f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(1.25f, 10.75f, 0.75f, 11.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1, 8, 1).
                        to(3, 10, 4).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Y).origin(2, 8, 3)).
                        face(Direction.NORTH, faceBuilder -> faceBuilder.uvs(10.75f, 0.75f, 11.25f, 1.25f)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(7, 3.75f, 7.75f, 4.25f)).
                        face(Direction.SOUTH, faceBuilder -> faceBuilder.uvs(10.75f, 6.5f, 11.25f, 7)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(7, 4.25f, 7.75f, 4.75f)).
                        face(Direction.UP, faceBuilder -> faceBuilder.uvs(8.25f, 4.5f, 7.75f, 3.75f)).
                        face(Direction.DOWN, faceBuilder -> faceBuilder.uvs(10.75f, 0.75f, 10.25f, 1.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2.9f, -4f, 13).
                        to(2.9f, 9, 16).
                        rotation(rotationBuilder -> rotationBuilder.angle(45).axis(Direction.Axis.Y).origin(1.9f, 7, 15)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(15.25f, 0, 16, 3)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(15.25f, 0, 16, 3)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(1.9f, -4f, 0).
                        to(1.9f, 9, 3).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Y).origin(0.9f, 7, 2)).
                        face(Direction.EAST, faceBuilder -> faceBuilder.uvs(15.25f, 0, 16, 3)).
                        face(Direction.WEST, faceBuilder -> faceBuilder.uvs(15.25f, 0, 16, 3)).
                        texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, modelLocation)).
                with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private void createStairs(@NotNull StairBlock block, @NotNull BlockModelGenerators blockModels)
    {
        TextureMapping mapping = new TextureMapping().
                put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block.base)).
                put(TextureSlot.TOP, TextureMapping.getBlockTexture(block.base)).
                put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block.base));

        ResourceLocation resourcelocation = ModelTemplates.STAIRS_INNER.create(block, mapping, blockModels.modelOutput);
        ResourceLocation resourcelocation1 = ModelTemplates.STAIRS_STRAIGHT.create(block, mapping, blockModels.modelOutput);
        ResourceLocation resourcelocation2 = ModelTemplates.STAIRS_OUTER.create(block, mapping, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createStairs(block, resourcelocation, resourcelocation1, resourcelocation2));
        blockModels.registerSimpleItemModel(block, resourcelocation1);
    }

    private void createMultifaceModel(@NotNull MultifaceBlock block, @NotNull BlockModelGenerators blockModels)
    {
        TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block));

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                parent(ResourceLocation.withDefaultNamespace(blockPrefix("block"))).
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                renderType("translucent").
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(builder -> builder.
                        from(0f,0f,0.1f).
                        to(16f,16f,0.1f).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(16f, 0f, 0f, 16f)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(0f,0f, 16f, 16f)).
                        texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block, blockModels.modelOutput);

        MultiPartGenerator multipartgenerator = MultiPartGenerator.multiPart(block);
        Condition.TerminalCondition condition$terminalcondition = Util.make(
                Condition.condition(), condition -> BlockModelGenerators.MULTIFACE_GENERATOR.stream().map(Pair::getFirst).map(MultifaceBlock::getFaceProperty).forEach(property ->
                {
                    if (block.defaultBlockState().hasProperty(property))
                        condition.term(property, false);
                }));

        for (Pair<Direction, Function<ResourceLocation, Variant>> pair : BlockModelGenerators.MULTIFACE_GENERATOR)
        {
            BooleanProperty booleanproperty = MultifaceBlock.getFaceProperty(pair.getFirst());
            Function<ResourceLocation, Variant> function = pair.getSecond();
            if (block.defaultBlockState().hasProperty(booleanproperty))
            {
                multipartgenerator.with(Condition.condition().term(booleanproperty, true), function.apply(modelLocation));
                multipartgenerator.with(condition$terminalcondition, function.apply(modelLocation));
            }
        }

        blockModels.blockStateOutput.accept(multipartgenerator);

        blockModels.registerSimpleFlatItemModel(block);
    }

    private void createLureCampfireModel(@NotNull BlockModelGenerators blockModels)
    {
        Block block = Registration.BlockReg.LURE_CAMPFIRE.get();
        TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block));

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                parent(ResourceLocation.withDefaultNamespace(blockPrefix("block"))).
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(TextureSlot.PARTICLE).
                renderType("solid").
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(builder -> builder.
                        from(2, 0, 3).
                        to(14, 2, 6).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(0.75f, 13.5f, 3.75f, 14.5f)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(0, 13.5f, 0.75f, 14.5f)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(4.5f, 13.5f, 7.5f, 14.5f)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(3.75f, 13.5f, 4.5f, 14.5f)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(3.75f, 13.5f, 0.75f, 12)).
                        face(Direction.DOWN,
                                 faceBuilder -> faceBuilder.uvs(6.75f, 12, 3.75f, 13.5f).cullface(Direction.DOWN)).
                        texture(TextureSlot.ALL)).
                element(builder -> builder.
                        from(2, 0,10).
                        to(14, 2, 13).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(0.75f, 13.5f, 3.75f, 14.5f)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(0, 1.35f, 0.75f, 14.5f)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(4.5f, 13.5f, 7.5f, 14.5f)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(3.75f, 13.5f, 4.5f, 14.5f)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(3.75f, 13.5f, 0.75f, 12)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(6.75f, 12, 3.75f, 13.5f).cullface(Direction.DOWN)).
                        texture(TextureSlot.ALL)).
                element(builder -> builder.
                        from(4,2,2).
                        to(7, 4, 14).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(3, 6, 3.75f, 7)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(0, 6, 3, 7)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(6.75f, 6, 7.5f, 7)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(3.75f, 6, 6.75f, 7)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(3.75f, 6, 3, 0)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(4.5f, 0, 3.75f, 6)).
                        texture(TextureSlot.ALL)).
                element(builder -> builder.
                        from(9,2,2).
                        to(12, 4, 14).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(3, 6, 3.75f, 7)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(0, 6, 3, 7)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(6.75f, 6, 7.5f, 7)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(3.75f, 6, 6.75f, 7)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(3.75f, 6, 3, 0)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(4.5f, 0, 3.75f, 6)).
                        texture(TextureSlot.ALL)).
                element(builder -> builder.
                        from(14, 0, 7).
                        to(16, 16, 9).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(14.5f, 1, 15, 10)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(14, 1, 14.5f, 10).cullface(Direction.EAST)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(15.5f, 1, 16, 10)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(15, 1, 15.5f, 10)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(15, 1, 14.5f, 0).cullface(Direction.UP)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(15.5f, 0, 15, 1).cullface(Direction.DOWN)).
                        texture(TextureSlot.ALL)).
                element(builder -> builder.
                        from(0, 0, 7).
                        to(2, 16, 9).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(14.5f, 1, 15, 10)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(14, 1, 14.5f, 10).cullface(Direction.EAST)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(15.5f, 1, 16, 10)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(15, 1, 15.5f, 10)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(15, 1, 14.5f, 0).cullface(Direction.UP)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(15.5f, 0, 15, 1).cullface(Direction.DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(2, 0,6).
                        to(14, 1, 10).
                        face(Direction.NORTH,
                                 faceBuilder -> faceBuilder.uvs(2.25f, 9.5f, 4, 10)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(0, 9.5f, 4, 10)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(6.25f,9.5f, 8, 10)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(4, 9.5f, 1, 7.5f)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(4, 9.5f, 1, 7.5f)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(7, 7.5f, 4, 9.5f).cullface(Direction.DOWN)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(-1, 14, 7.5f).
                        to(14, 15, 8.5f).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(0.25f, 11, 4, 11.5f)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(0, 11, 0.25f, 11.5f)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(4.25f, 11, 8, 11.5f)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(4, 11, 4.25f, 11.5f).cullface(Direction.WEST)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(4, 11, 0.25f, 10.5f)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(7.75f, 10.5f, 4, 11)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(-2, 10, 7.5f).
                        to(-1, 15, 8.5f).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(5, 0.5f, 5.25f, 3)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(4.75f, 0.5f, 5, 3)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(5.5f, 0.5f, 5.75f, 3)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(5.25f, 0.5f, 5.5f, 3).cullface(Direction.WEST)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(5.25f, 0.5f, 5, 0)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(5.5f, 0, 5.25f, 0.5f)).
                        texture(TextureSlot.ALL)).
                element(elementBuilder -> elementBuilder.
                        from(-4, 10, 7.5f).
                        to(-2, 11, 8.5f).
                        face(Direction.NORTH,
                                faceBuilder -> faceBuilder.uvs(6.25f, 0.5f, 6.75f, 1)).
                        face(Direction.EAST,
                                faceBuilder -> faceBuilder.uvs(6, 0.5f, 6.25f, 1)).
                        face(Direction.SOUTH,
                                faceBuilder -> faceBuilder.uvs(7, 0.5f, 7.5f, 1)).
                        face(Direction.WEST,
                                faceBuilder -> faceBuilder.uvs(6.75f, 0.5f, 7, 1).cullface(Direction.WEST)).
                        face(Direction.UP,
                                faceBuilder -> faceBuilder.uvs(6.75f, 0.5f, 6.25f, 0)).
                        face(Direction.DOWN,
                                faceBuilder -> faceBuilder.uvs(7.25f, 0, 6.25f, 0.5f)).
                        texture(TextureSlot.ALL));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block, blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation)).
                with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels)
    {
        registerItemModels(itemModels);
        registerBlockModels(blockModels);
    }

    @Contract(pure = true)
    private @NotNull String blockPrefix(String str)
    {
        return "block/" + str;
    }

    @Contract(pure = true)
    private @NotNull String itemPrefix(String str)
    {
        return "item/" + str;
    }

    @Override
    protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks()
    {
        return Registration.BlockReg.BLOCKS.getEntries().
                stream();
    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems()
    {
        return Registration.ItemReg.ITEMS.getEntries().
                stream();
    }

    @Override
    public @NotNull String getName()
    {
        return Database.MOD_NAME + ": Models";
    }
}
