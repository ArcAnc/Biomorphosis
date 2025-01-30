/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        itemModels.generateFlatItem(Registration.ItemReg.CREATIVE_TAB_ICON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.WRENCH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ItemReg.BOOK.get(), ModelTemplates.FLAT_ITEM);
    }

    private void registerBlockModels(@NotNull BlockModelGenerators blockModels)
    {
        blockModels.createTrivialCube(Registration.BlockReg.FLESH.get());
        createLureCampfireModel(blockModels);
    }

    private void createLureCampfireModel(@NotNull BlockModelGenerators blockModels)
    {
        Block block = Registration.BlockReg.LURE_CAMPFIRE.get();
        TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block));

        ExtendedModelTemplateBuilder template = BLOCK.extend().
                parent(ResourceLocation.withDefaultNamespace("block/block")).
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
