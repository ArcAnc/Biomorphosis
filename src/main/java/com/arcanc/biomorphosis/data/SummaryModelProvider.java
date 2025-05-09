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
import com.arcanc.biomorphosis.content.block.norph.source.NorphSourceBlock;
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

        itemModels.generateFlatItem(Registration.ItemReg.FORGE_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
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
