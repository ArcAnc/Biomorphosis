/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.block.norph.NorphSource;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.generators.template.FaceRotation;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class SummaryModelProvider extends ModelProvider
{
    private static final ModelTemplate BLOCK = ModelTemplates.create("block", TextureSlot.ALL);

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
        DeferredBlock<NorphSource> block = Registration.BlockReg.NORPH_SOURCE;

        TextureSlot membrane = TextureSlot.create("membrane");
        TextureSlot fluid = TextureSlot.create("fluid");

        ResourceLocation blockLoc = TextureMapping.getBlockTexture(block.get());
        TextureMapping mapping = new TextureMapping().
                put(membrane, blockLoc.withSuffix("/membrane")).
                put(TextureSlot.ALL, blockLoc.withSuffix("/main")).
                put(fluid, blockLoc.withSuffix("/fluid")).
                put(TextureSlot.PARTICLE, blockLoc.withSuffix("/main"));

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                parent(ResourceLocation.withDefaultNamespace(blockPrefix("block"))).
                requiredTextureSlot(membrane).
                requiredTextureSlot(TextureSlot.ALL).
                requiredTextureSlot(fluid).
                requiredTextureSlot(TextureSlot.PARTICLE).
                renderType("cutout").
                guiLight(UnbakedModel.GuiLight.SIDE).
                element(builder -> builder.
                        from(7, 0.001f, 4).
                        to(9, 16.001f, 11).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(14, 0, 16, 7);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(14, 0, 16, 16);
                            else
                                faceBuilder.uvs(9, 0, 16, 16);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(5, 12, 6).
                        to(7, 14, 9).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(14, 0, 16, 3);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(14, 0, 16, 2);
                            else
                                faceBuilder.uvs(13, 0, 16, 2);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(2, 0.001f, 4).
                        to(7, 1.001f, 11).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(11,0, 16, 7);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(11, 0, 16, 1);
                            else
                                faceBuilder.uvs(9, 0, 16, 1);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(2, 1, 4).
                        to(7, 4, 5).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(11,0, 16, 1);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(11, 0, 16, 3);
                            else
                                faceBuilder.uvs(15, 0, 16, 3);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(2, 1, 5).
                        to(3, 4, 10).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(15,0, 16, 5);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(15, 0, 16, 3);
                            else
                                faceBuilder.uvs(11, 0, 16, 3);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(2, 1, 10).
                        to(7, 4, 11).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(11,0, 16, 1);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(11, 0, 16, 3);
                            else
                                faceBuilder.uvs(15, 0, 16, 3);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(4.999f, 1, 6).
                        to(4.999f, 14, 9).
                        allFacesExcept((direction, faceBuilder) -> faceBuilder.uvs(0, 3, 3, 16).texture(fluid),
                                EnumSet.of(Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH))).
                element(builder -> builder.
                        from(5, 14.001f, 6).
                        to(7, 14.001f, 9).
                        allFacesExcept((direction, faceBuilder) -> faceBuilder.uvs(0,0,3,2).texture(fluid),
                                EnumSet.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH))).
                element(builder -> builder.
                        from(0, 0, 0).
                        to(16, 0.001f, 16).
                        allFacesExcept((direction, faceBuilder) -> faceBuilder.uvs(0, 0, 16, 16).texture(membrane),
                                EnumSet.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH))).
                element(builder -> builder.
                        from(14.8f, 0.001f, -0.3f).
                        to(15.8f, 1.001f, 5.7f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Y).origin(15.3f, 0.5f, 0.7f)).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(10,15, 16, 16).rotation(FaceRotation.CLOCKWISE_90);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(15, 14, 16, 15);
                            else
                                faceBuilder.uvs(10, 15, 16, 16);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(10.2f, 0.001f, 14.7f).
                        to(16.2f, 1.001f, 15.7f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Y).origin(15.2f, 1f, 15.2f)).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(10,15, 16, 16);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(10, 15, 16, 16);
                            else
                                faceBuilder.uvs(15, 14, 16, 15);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(-0.2f, 0.001f, 0.3f).
                        to(5.8f, 1.001f, 1.3f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45f).axis(Direction.Axis.Y).origin(0.8f, 0.5f, 0.8f)).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(10, 15, 16, 16);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(10, 15, 16, 16);
                            else
                                faceBuilder.uvs(15, 14, 16, 15);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(0.3f, 0.001f, 10.2f).
                        to(1.3f, 1.001f, 16.2f).
                        rotation(rotationBuilder -> rotationBuilder.angle(-45).axis(Direction.Axis.Y).origin(0.8f, 0.5f, 15.2f)).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(10,15, 16, 16).rotation(FaceRotation.CLOCKWISE_90);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(15, 14, 16, 15);
                            else
                                faceBuilder.uvs(10, 15, 16, 16);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(4, 0.001f, 1).
                        to(12, 5.001f, 4).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(8,11, 16, 14);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(8, 9, 16, 14);
                            else
                                faceBuilder.uvs(13, 3, 16, 8);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(3, 0.001f, 11).
                        to(13, 10.001f, 15).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(6,0, 16, 14);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(6, 0, 10, 16);
                            else
                                faceBuilder.uvs(0, 6, 4, 16);
                            faceBuilder.texture(TextureSlot.ALL);
                        })).
                element(builder -> builder.
                        from(9, 0.001f, 4).
                        to(12, 8.001f, 11).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                faceBuilder.uvs(13,7, 16, 4);
                            else if (direction.getAxis() == Direction.Axis.Z)
                                faceBuilder.uvs(13, 6, 16, 4);
                            else
                                faceBuilder.uvs(9, 6, 16, 4);
                            faceBuilder.texture(TextureSlot.ALL);
                        }));

        ResourceLocation modelLocation = new TexturedModel(mapping, template.build()).create(block.get(), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block.get(), Variant.variant().with(VariantProperties.MODEL, modelLocation)).
                with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private void createStairs(@NotNull StairBlock block, @NotNull BlockModelGenerators blockModels)
    {
        TextureMapping mapping = new TextureMapping().put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block.base)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block.base)).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block.base));

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
                renderType("cutout").
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
