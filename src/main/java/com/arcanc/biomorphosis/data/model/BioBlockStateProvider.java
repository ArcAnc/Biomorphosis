/**
 * @author ArcAnc
 * Created at: 12.02.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.model;


import com.arcanc.biomorphosis.content.block.*;
import com.arcanc.biomorphosis.content.block.block_entity.EggsDeco;
import com.arcanc.biomorphosis.content.block.multiblock.*;
import com.arcanc.biomorphosis.content.block.norph.NorphBlock;
import com.arcanc.biomorphosis.content.block.norph.NorphOverlay;
import com.arcanc.biomorphosis.content.block.norph.NorphStairs;
import com.arcanc.biomorphosis.content.block.norph.source.NorphSourceBlock;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.EnumSet;

public class BioBlockStateProvider extends BlockStateProvider
{
	public BioBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, Database.MOD_ID, existingFileHelper);
	}
	
	@Override
	protected void registerStatesAndModels()
	{
		registerSimpleBlock(Registration.BlockReg.FLESH.get());
		
		createLureCampfireModel();
		
		createNorphSource();
		createNorphBlock();
		createNorphOverlay();
		createNorphStairs();
		
		createFluidModel(Registration.FluidReg.BIOMASS);
		createFluidModel(Registration.FluidReg.ACID);
		createFluidModel(Registration.FluidReg.ADRENALINE);
		
		createFluidStorage();
		createFluidTransmitter();
		createCrusherModel();
		createStomachModel();
		createCatcherModel();
		createForgeModel();
		createSqueezerModel();
		
		createMultiblockFluidStorage();
		createMultiblockChamberModel();
		createMultiblockTurretModel();
		createMultiblockChrysalisModel();
		createMultiblockMorpherModel();
		
		/*FIXME: проверить модель третьего пропса и светящегося мха. Там где-то ошибка в координатах*/
		createProps();
		createGlowMoss();
		
		createNorphedDirt();
		registerSimpleBlock(Registration.BlockReg.INNER.get());
		createRoofModel();
		
		registerSimpleBlock(Registration.BlockReg.TRAMPLED_DIRT.get());
		createDecoHiveModel();
		createChestModel();
		createHangingMoss();
		createEggsModel();
		
		createBioFarmLand();
		createMeatMelonStem();
		createMeatMelonBlock();
		
		createShitBlock();
		createBushModel();
		
		createNorphedStone();
	}
	
	private void createNorphedStone()
	{
		BioBaseBlock block = Registration.BlockReg.NORPHED_STONE.get();
		
		ResourceLocation baseText = blockTexture(block);
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("top", baseText.withSuffix("_top")).
				texture("side", baseText.withSuffix("_side")).
				texture("bottom", baseText.withSuffix("_bottom")).
				texture("particle", baseText.withSuffix("_side")).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
					from(0, 0, 0).
					to(16, 16, 16).
					allFaces((direction, faceBuilder) ->
					{
						faceBuilder.uvs(0, 0, 16, 16).cullface(direction);
						if (direction.getAxis().isHorizontal())
							faceBuilder.texture("#side");
						else
						{
							if (direction == Direction.UP)
								faceBuilder.texture("#top");
							else
								faceBuilder.texture("#bottom");
						}
					}).
				end();
		
		registerModels(block, model);
	}
	
	private void createBushModel()
	{
		BioBushBlock block = Registration.BlockReg.BIO_BUSH.get();
		
		int variations = 3;
		
		ModelFile[] models = new ModelFile[variations];
		
		for (int q = 0; q < variations; q++)
		{
			ResourceLocation texture = blockTexture(block).withSuffix("_" + q);
			
			models[q] = models().withExistingParent(blockPrefix(name(block)) + "_" + q, mcLoc(blockPrefix("cross"))).
					renderType(RenderType.cutout().name).
					texture("cross", texture).
					texture("particle", texture).
					guiLight(BlockModel.GuiLight.SIDE);
		}
		
		ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
		
		for (int q = 0; q < variations; q++)
		{
			builder = builder.modelFile(models[q]);
			if (q != variations - 1)
				builder = builder.nextModel();
		}
		
		getVariantBuilder(block).partialState().addModels(builder.build());
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(models[0]);
	}
	
	private void createShitBlock()
	{
		BioShitBlock block = Registration.BlockReg.BIO_SHIT.get();
		
		int variations = 3;
		
		ModelFile[] models = new ModelFile[variations];
		
		for (int q = 0; q < variations; q++)
		{
			ResourceLocation texture = blockTexture(block).withSuffix("_" + q);
			models[q] = models().withExistingParent(blockPrefix(name(block)) + "_" + q, mcLoc(blockPrefix("block"))).
					renderType(RenderType.translucent().name).
					texture("all", texture).
					texture("particle", texture).
					guiLight(BlockModel.GuiLight.SIDE).
					element().
							from(0, 0, 0).
							to(16, 1, 16).
							allFaces((direction, faceBuilder) ->
							{
								faceBuilder.texture("#all");
								if (direction.getAxis().isHorizontal())
									faceBuilder.cullface(direction).uvs(0, 0, 16, 1);
								else
								{
									faceBuilder.uvs(0, 0, 16, 16);
									if (direction == Direction.DOWN)
										faceBuilder.cullface(Direction.DOWN);
								}
							}).
					end();
		}
		
		ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
		
		for (int q = 0; q < variations; q++)
		{
			builder = builder.modelFile(models[q]);
			if (q != variations - 1)
				builder = builder.nextModel();
		}
		
		getVariantBuilder(block).partialState().addModels(builder.build());
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(models[0]);
	}
	
	private void createMeatMelonBlock()
	{
		BioBaseBlock block = Registration.BlockReg.MEAT_MELON_BLOCK.get();
		
		ResourceLocation texture = blockTexture(block);
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.SOLID.name).
				texture("all", texture).
				texture("particle", texture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(2, 0, 2).
						to(14, 16, 14).
						face(Direction.NORTH).uvs(3, 10, 6, 16).end().
						face(Direction.EAST).uvs(0, 10, 3, 14).end().
						face(Direction.SOUTH).uvs(9, 10, 12, 14).end().
						face(Direction.WEST).uvs(6, 10, 9, 14).end().
						face(Direction.UP).uvs(6, 10, 3, 7).cullface(Direction.UP).end().
						face(Direction.DOWN).uvs(9, 7, 6, 10).cullface(Direction.DOWN).end().
						texture("#all").
				end().
				element().
						from(0, 2, 0).
						to(16, 14, 16).
						face(Direction.NORTH).uvs(4, 4, 8, 7).cullface(Direction.NORTH).end().
						face(Direction.EAST).uvs(0, 4, 4, 7).cullface(Direction.EAST).end().
						face(Direction.SOUTH).uvs(12, 4, 16, 7).cullface(Direction.SOUTH).end().
						face(Direction.WEST).uvs(8, 4, 12, 7).cullface(Direction.WEST).end().
						face(Direction.UP).uvs(8, 4, 4, 0).end().
						face(Direction.DOWN).uvs(12, 0, 8, 4).end().
						texture("#all").
				end();
		
		registerModels(block, model);
	}
	
	private void createMeatMelonStem()
	{
		BioStemBlock block = Registration.BlockReg.MEAT_MELON_STEM.get();
		
		int ageCount = BioStemBlock.MAX_AGE + 1;
		int variantCount = 2;
		
		ResourceLocation texture = blockTexture(block);
		
		ModelFile[] models = new ModelFile[ageCount * 2];
		
		for (int variant = 0; variant < variantCount; variant++)
		{
			for (int q = 0; q < ageCount; q++)
				models[variant * ageCount + q] = models().withExistingParent(blockPrefix(name(block)) + "_" + variant + "_" + q, mcLoc(blockPrefix("block"))).
						renderType(RenderType.cutout().name).
						texture("stem", texture + "_" + variant).
						texture("particle", texture + "_" + variant).
						guiLight(BlockModel.GuiLight.SIDE).
						element().
								from(0, 17 - 2 * (q + 1), 8).
								to(16, 17, 8).
								rotation().
										origin(8, 8, 8).
										axis(Direction.Axis.Y).
										angle(45f).
										rescale(true).
								end().
								face(Direction.NORTH).
										uvs(0, 16, 16, 16 - 2 * (q + 1)).
										texture("#stem").
										tintindex(0).
								end().
								face(Direction.SOUTH).
										uvs(16, 16, 0, 16 - 2 * (q + 1)).
										texture("#stem").
										tintindex(0).
								end().
						end().
						element().
								from(8, 17 - 2 * (q + 1), 0).
								to(8, 17, 16).
								rotation().
										origin(8, 8, 8).
										axis(Direction.Axis.Y).
										angle(45f).
										rescale(true).
								end().
								face(Direction.WEST).
										uvs(0, 16, 16, 16 - 2 * (q + 1)).
										texture("#stem").
										tintindex(0).
								end().
								face(Direction.EAST).
										uvs(16, 16, 0, 16 - 2 * (q + 1)).
										texture("#stem").
										tintindex(0).
								end().
						end();
		}
		
		getVariantBuilder(block).forAllStates(state ->
		{
			int age = state.getValue(BioStemBlock.AGE);
			return ConfiguredModel.builder().
					modelFile(models[age]).
					nextModel().
					modelFile(models[age + ageCount]).
					build();
		});
	}
	
	private void createBioFarmLand()
	{
		BioFarmland block = Registration.BlockReg.BIO_FARMLAND.get();
		
		ResourceLocation blockTexture = blockTexture(block);
		ResourceLocation dirtTexture = blockTexture(Registration.BlockReg.NORPHED_DIRT_0.get());
		
		ModelFile modelMoist = models().withExistingParent(blockPrefix(name(block)) + "_moist", mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("down", blockTexture.withSuffix("_moist")).
				texture("all", dirtTexture).
				texture("particle", dirtTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(0, 1, 0).
						to(16, 16, 16).
						allFaces((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(0, 0, 16, 15).
										texture("#all");
							else
							{
								if (direction.getAxis().isVertical())
								{
									faceBuilder.uvs(0, 0, 16, 16);
									if (direction == Direction.UP)
										faceBuilder.texture("#all");
									else
										faceBuilder.texture("#down");
								}
							}
						}).
				end();
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("down", blockTexture).
				texture("all", dirtTexture).
				texture("particle", dirtTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(0, 1, 0).
						to(16, 16, 16).
						allFaces((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(0, 0, 16, 15).
										texture("#all");
							else
							{
								if (direction.getAxis().isVertical())
								{
									faceBuilder.uvs(0, 0, 16, 16);
									if (direction == Direction.UP)
										faceBuilder.texture("#all");
									else
										faceBuilder.texture("#down");
								}
							}
						}).
				end();
		
		getVariantBuilder(block).forAllStates(state ->
		{
			int moisure = state.getValue(BioFarmland.MOISTURE);
			return ConfiguredModel.builder().
					modelFile(moisure < BioFarmland.MAX_MOISTURE ? model : modelMoist).
					build();
		});
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(modelMoist);
	}
	
	private void createEggsModel()
	{
		BioBaseEntityBlock<EggsDeco> block = Registration.BlockReg.EGGS_DECO.get();
		
		ResourceLocation blockTexture = blockTexture(block).withSuffix("/0");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(10f, 5f, 10f).
						to(13f, 8f, 13f).
						face(Direction.NORTH).
								uvs(6.375f, 5.125f, 6.75f, 5.5f).
						end().
						face(Direction.EAST).
								uvs(6f, 5.125f, 6.375f, 5.5f).
						end().
						face(Direction.SOUTH).
								uvs(7.125f, 5.125f, 7.5f, 5.5f).
						end().
						face(Direction.WEST).
								uvs(6.75f, 5.125f, 7.125f, 5.5f).
						end().
						face(Direction.UP).
								uvs(6.75f, 5.125f, 6.375f, 4.75f).
						end().
						face(Direction.DOWN).
								uvs(7.125f, 4.75f, 6.75f, 5.125f).
						end().
						texture("#all").
				end().
				element().
				from(7f, 5f, 11f).
				to(10f, 8f, 14f).
				face(Direction.NORTH).uvs(6.375f, 5.875f, 6.75f, 6.25f).end().
				face(Direction.EAST).uvs(6f, 5.875f, 6.375f, 6.25f).end().
				face(Direction.SOUTH).uvs(7.125f, 5.875f, 7.5f, 6.25f).end().
				face(Direction.WEST).uvs(6.75f, 5.875f, 7.125f, 6.25f).end().
				face(Direction.UP).uvs(6.75f, 5.875f, 6.375f, 5.5f).end().
				face(Direction.DOWN).uvs(7.125f, 5.5f, 6.75f, 5.875f).end().
				texture("#all").end().
				element().
				from(3f, 5f, 10f).
				to(6f, 8f, 13f).
				face(Direction.NORTH).uvs(0.375f, 6.625f, 0.75f, 7f).end().
				face(Direction.EAST).uvs(0f, 6.625f, 0.375f, 7f).end().
				face(Direction.SOUTH).uvs(1.125f, 6.625f, 1.5f, 7f).end().
				face(Direction.WEST).uvs(0.75f, 6.625f, 1.125f, 7f).end().
				face(Direction.UP).uvs(0.75f, 6.625f, 0.375f, 6.25f).end().
				face(Direction.DOWN).uvs(1.125f, 6.25f, 0.75f, 6.625f).end().
				texture("#all").end().
				element().
				from(2f, 5f, 6f).
				to(5f, 8f, 9f).
				face(Direction.NORTH).uvs(1.875f, 6.625f, 2.25f, 7f).end().
				face(Direction.EAST).uvs(1.5f, 6.625f, 1.875f, 7f).end().
				face(Direction.SOUTH).uvs(2.625f, 6.625f, 3f, 7f).end().
				face(Direction.WEST).uvs(2.25f, 6.625f, 2.625f, 7f).end().
				face(Direction.UP).uvs(2.25f, 6.625f, 1.875f, 6.25f).end().
				face(Direction.DOWN).uvs(2.625f, 6.25f, 2.25f, 6.625f).end().
				texture("#all").end().
				element().
				from(5f, 5f, 3f).
				to(8f, 8f, 6f).
				face(Direction.NORTH).uvs(3.375f, 6.625f, 3.75f, 7f).end().
				face(Direction.EAST).uvs(3f, 6.625f, 3.375f, 7f).end().
				face(Direction.SOUTH).uvs(4.125f, 6.625f, 4.5f, 7f).end().
				face(Direction.WEST).uvs(3.75f, 6.625f, 4.125f, 7f).end().
				face(Direction.UP).uvs(3.75f, 6.625f, 3.375f, 6.25f).end().
				face(Direction.DOWN).uvs(4.125f, 6.25f, 3.75f, 6.625f).end().
				texture("#all").end().
				element().
				from(10f, 5f, 3f).
				to(13f, 8f, 6f).
				face(Direction.NORTH).uvs(4.875f, 6.625f, 5.25f, 7f).end().
				face(Direction.EAST).uvs(4.5f, 6.625f, 4.875f, 7f).end().
				face(Direction.SOUTH).uvs(5.625f, 6.625f, 6f, 7f).end().
				face(Direction.WEST).uvs(5.25f, 6.625f, 5.625f, 7f).end().
				face(Direction.UP).uvs(5.25f, 6.625f, 4.875f, 6.25f).end().
				face(Direction.DOWN).uvs(5.625f, 6.25f, 5.25f, 6.625f).end().
				texture("#all").end().
				element().
				from(5f, 5f, 7f).
				to(8f, 8f, 10f).
				face(Direction.NORTH).uvs(6.375f, 6.625f, 6.75f, 7f).end().
				face(Direction.EAST).uvs(6f, 6.625f, 6.375f, 7f).end().
				face(Direction.SOUTH).uvs(7.125f, 6.625f, 7.5f, 7f).end().
				face(Direction.WEST).uvs(6.75f, 6.625f, 7.125f, 7f).end().
				face(Direction.UP).uvs(6.75f, 6.625f, 6.375f, 6.25f).end().
				face(Direction.DOWN).uvs(7.125f, 6.25f, 6.75f, 6.625f).end().
				texture("#all").end().
				element().
				from(9f, 5f, 7f).
				to(12f, 8f, 10f).
				face(Direction.NORTH).uvs(0.375f, 7.375f, 0.75f, 7.75f).end().
				face(Direction.EAST).uvs(0f, 7.375f, 0.375f, 7.75f).end().
				face(Direction.SOUTH).uvs(1.125f, 7.375f, 1.5f, 7.75f).end().
				face(Direction.WEST).uvs(0.75f, 7.375f, 1.125f, 7.75f).end().
				face(Direction.UP).uvs(0.75f, 7.375f, 0.375f, 7f).end().
				face(Direction.DOWN).uvs(1.125f, 7f, 0.75f, 7.375f).end().
				texture("#all").end().
				element().
				from(4f, 8f, 4f).
				to(7f, 11f, 7f).
				face(Direction.NORTH).uvs(7.375f, 3.25f, 7.75f, 3.625f).end().
				face(Direction.EAST).uvs(7f, 3.25f, 7.375f, 3.625f).end().
				face(Direction.SOUTH).uvs(8.125f, 3.25f, 8.5f, 3.625f).end().
				face(Direction.WEST).uvs(7.75f, 3.25f, 8.125f, 3.625f).end().
				face(Direction.UP).uvs(7.75f, 3.25f, 7.375f, 2.875f).end().
				face(Direction.DOWN).uvs(8.125f, 2.875f, 7.75f, 3.25f).end().
				texture("#all").end().
				element().
				from(4f, 8f, 8f).
				to(7f, 11f, 11f).
				face(Direction.NORTH).uvs(7.375f, 2.5f, 7.75f, 2.875f).end().
				face(Direction.EAST).uvs(7f, 2.5f, 7.375f, 2.875f).end().
				face(Direction.SOUTH).uvs(8.125f, 2.5f, 8.5f, 2.875f).end().
				face(Direction.WEST).uvs(7.75f, 2.5f, 8.125f, 2.875f).end().
				face(Direction.UP).uvs(7.75f, 2.5f, 7.375f, 2.125f).end().
				face(Direction.DOWN).uvs(8.125f, 2.125f, 7.75f, 2.5f).end().
				texture("#all").end().
				element().
				from(8f, 8f, 10f).
				to(11f, 11f, 13f).
				face(Direction.NORTH).uvs(1.875f, 7.375f, 2.25f, 7.75f).end().
				face(Direction.EAST).uvs(1.5f, 7.375f, 1.875f, 7.75f).end().
				face(Direction.SOUTH).uvs(2.625f, 7.375f, 3f, 7.75f).end().
				face(Direction.WEST).uvs(2.25f, 7.375f, 2.625f, 7.75f).end().
				face(Direction.UP).uvs(2.25f, 7.375f, 1.875f, 7f).end().
				face(Direction.DOWN).uvs(2.625f, 7f, 2.25f, 7.375f).end().
				texture("#all").end().
				element().
				from(7f, 8f, 7f).
				to(10f, 11f, 10f).
				face(Direction.NORTH).uvs(3.375f, 7.375f, 3.75f, 7.75f).end().
				face(Direction.EAST).uvs(3f, 7.375f, 3.375f, 7.75f).end().
				face(Direction.SOUTH).uvs(4.125f, 7.375f, 4.5f, 7.75f).end().
				face(Direction.WEST).uvs(3.75f, 7.375f, 4.125f, 7.75f).end().
				face(Direction.UP).uvs(3.75f, 7.375f, 3.375f, 7f).end().
				face(Direction.DOWN).uvs(4.125f, 7f, 3.75f, 7.375f).end().
				texture("#all").end().
				element().
				from(9f, 8f, 4f).
				to(12f, 11f, 7f).
				face(Direction.NORTH).uvs(4.875f, 7.375f, 5.25f, 7.75f).end().
				face(Direction.EAST).uvs(4.5f, 7.375f, 4.875f, 7.75f).end().
				face(Direction.SOUTH).uvs(5.625f, 7.375f, 6f, 7.75f).end().
				face(Direction.WEST).uvs(5.25f, 7.375f, 5.625f, 7.75f).end().
				face(Direction.UP).uvs(5.25f, 7.375f, 4.875f, 7f).end().
				face(Direction.DOWN).uvs(5.625f, 7f, 5.25f, 7.375f).end().
				texture("#all").end().
				element().
				from(8f, 11f, 5f).
				to(11f, 14f, 8f).
				face(Direction.NORTH).uvs(6.375f, 7.375f, 6.75f, 7.75f).end().
				face(Direction.EAST).uvs(6f, 7.375f, 6.375f, 7.75f).end().
				face(Direction.SOUTH).uvs(7.125f, 7.375f, 7.5f, 7.75f).end().
				face(Direction.WEST).uvs(6.75f, 7.375f, 7.125f, 7.75f).end().
				face(Direction.UP).uvs(6.75f, 7.375f, 6.375f, 7f).end().
				face(Direction.DOWN).uvs(7.125f, 7f, 6.75f, 7.375f).end().
				texture("#all").end().
				element().
				from(5f, 11f, 6f).
				to(8f, 14f, 9f).
				face(Direction.NORTH).uvs(7.875f, 4f, 8.25f, 4.375f).end().
				face(Direction.EAST).uvs(7.5f, 4f, 7.875f, 4.375f).end().
				face(Direction.SOUTH).uvs(8.625f, 4f, 9f, 4.375f).end().
				face(Direction.WEST).uvs(8.25f, 4f, 8.625f, 4.375f).end().
				face(Direction.UP).uvs(8.25f, 4f, 7.875f, 3.625f).end().
				face(Direction.DOWN).uvs(8.625f, 3.625f, 8.25f, 4f).end().
				texture("#all").end().
				element().
				from(7f, 11f, 9f).
				to(10f, 14f, 12f).
				face(Direction.NORTH).uvs(7.875f, 4.75f, 8.25f, 5.125f).end().
				face(Direction.EAST).uvs(7.5f, 4.75f, 7.875f, 5.125f).end().
				face(Direction.SOUTH).uvs(8.625f, 4.75f, 9f, 5.125f).end().
				face(Direction.WEST).uvs(8.25f, 4.75f, 8.625f, 5.125f).end().
				face(Direction.UP).uvs(8.25f, 4.75f, 7.875f, 4.375f).end().
				face(Direction.DOWN).uvs(8.625f, 4.375f, 8.25f, 4.75f).end().
				texture("#all").end().
				element().
				from(7f, 2f, 1f).
				to(10f, 5f, 4f).
				face(Direction.NORTH).uvs(0.375f, 5.875f, 0.75f, 6.25f).end().
				face(Direction.EAST).uvs(0f, 5.875f, 0.375f, 6.25f).end().
				face(Direction.SOUTH).uvs(1.125f, 5.875f, 1.5f, 6.25f).end().
				face(Direction.WEST).uvs(0.75f, 5.875f, 1.125f, 6.25f).end().
				face(Direction.UP).uvs(0.75f, 5.875f, 0.375f, 5.5f).end().
				face(Direction.DOWN).uvs(1.125f, 5.5f, 0.75f, 5.875f).end().
				texture("#all").end().
				element().
				from(2f, 2f, 2f).
				to(5f, 5f, 5f).
				face(Direction.NORTH).uvs(6.375f, 4.375f, 6.75f, 4.75f).end().
				face(Direction.EAST).uvs(6f, 4.375f, 6.375f, 4.75f).end().
				face(Direction.SOUTH).uvs(7.125f, 4.375f, 7.5f, 4.75f).end().
				face(Direction.WEST).uvs(6.75f, 4.375f, 7.125f, 4.75f).end().
				face(Direction.UP).uvs(6.75f, 4.375f, 6.375f, 4f).end().
				face(Direction.DOWN).uvs(7.125f, 4f, 6.75f, 4.375f).end().
				texture("#all").end().
				element().
				from(7f, 2f, 4f).
				to(10f, 5f, 7f).
				face(Direction.NORTH).uvs(4.875f, 5.125f, 5.25f, 5.5f).end().
				face(Direction.EAST).uvs(4.5f, 5.125f, 4.875f, 5.5f).end().
				face(Direction.SOUTH).uvs(5.625f, 5.125f, 6f, 5.5f).end().
				face(Direction.WEST).uvs(5.25f, 5.125f, 5.625f, 5.5f).end().
				face(Direction.UP).uvs(5.25f, 5.125f, 4.875f, 4.75f).end().
				face(Direction.DOWN).uvs(5.625f, 4.75f, 5.25f, 5.125f).end().
				texture("#all").end().
				element().
				from(4f, 2f, 5f).
				to(7f, 5f, 8f).
				face(Direction.NORTH).uvs(4.875f, 4.375f, 5.25f, 4.75f).end().
				face(Direction.EAST).uvs(4.5f, 4.375f, 4.875f, 4.75f).end().
				face(Direction.SOUTH).uvs(5.625f, 4.375f, 6f, 4.75f).end().
				face(Direction.WEST).uvs(5.25f, 4.375f, 5.625f, 4.75f).end().
				face(Direction.UP).uvs(5.25f, 4.375f, 4.875f, 4f).end().
				face(Direction.DOWN).uvs(5.625f, 4f, 5.25f, 4.375f).end().
				texture("#all").end().
				element().
				from(1f, 2f, 6f).
				to(4f, 5f, 9f).
				face(Direction.NORTH).uvs(0.375f, 5.125f, 0.75f, 5.5f).end().
				face(Direction.EAST).uvs(0f, 5.125f, 0.375f, 5.5f).end().
				face(Direction.SOUTH).uvs(1.125f, 5.125f, 1.5f, 5.5f).end().
				face(Direction.WEST).uvs(0.75f, 5.125f, 1.125f, 5.5f).end().
				face(Direction.UP).uvs(0.75f, 5.125f, 0.375f, 4.75f).end().
				face(Direction.DOWN).uvs(1.125f, 4.75f, 0.75f, 5.125f).end().
				texture("#all").end().
				element().
				from(1f, 2f, 10f).
				to(4f, 5f, 13f).
				face(Direction.NORTH).uvs(1.875f, 4.375f, 2.25f, 4.75f).end().
				face(Direction.EAST).uvs(1.5f, 4.375f, 1.875f, 4.75f).end().
				face(Direction.SOUTH).uvs(2.625f, 4.375f, 3f, 4.75f).end().
				face(Direction.WEST).uvs(2.25f, 4.375f, 2.625f, 4.75f).end().
				face(Direction.UP).uvs(2.25f, 4.375f, 1.875f, 4f).end().
				face(Direction.DOWN).uvs(2.625f, 4f, 2.25f, 4.375f).end().
				texture("#all").end().
				element().
				from(4f, 2f, 9f).
				to(7f, 5f, 12f).
				face(Direction.NORTH).uvs(3.375f, 4.375f, 3.75f, 4.75f).end().
				face(Direction.EAST).uvs(3f, 4.375f, 3.375f, 4.75f).end().
				face(Direction.SOUTH).uvs(4.125f, 4.375f, 4.5f, 4.75f).end().
				face(Direction.WEST).uvs(3.75f, 4.375f, 4.125f, 4.75f).end().
				face(Direction.UP).uvs(3.75f, 4.375f, 3.375f, 4f).end().
				face(Direction.DOWN).uvs(4.125f, 4f, 3.75f, 4.375f).end().
				texture("#all").end().
				element().
				from(7f, 2f, 9f).
				to(10f, 5f, 12f).
				face(Direction.NORTH).uvs(1.875f, 5.125f, 2.25f, 5.5f).end().
				face(Direction.EAST).uvs(1.5f, 5.125f, 1.875f, 5.5f).end().
				face(Direction.SOUTH).uvs(2.625f, 5.125f, 3f, 5.5f).end().
				face(Direction.WEST).uvs(2.25f, 5.125f, 2.625f, 5.5f).end().
				face(Direction.UP).uvs(2.25f, 5.125f, 1.875f, 4.75f).end().
				face(Direction.DOWN).uvs(2.625f, 4.75f, 2.25f, 5.125f).end().
				texture("#all").end().
				element().
				from(6f, 2f, 12f).
				to(9f, 5f, 15f).
				face(Direction.NORTH).uvs(0.375f, 4.375f, 0.75f, 4.75f).end().
				face(Direction.EAST).uvs(0f, 4.375f, 0.375f, 4.75f).end().
				face(Direction.SOUTH).uvs(1.125f, 4.375f, 1.5f, 4.75f).end().
				face(Direction.WEST).uvs(0.75f, 4.375f, 1.125f, 4.75f).end().
				face(Direction.UP).uvs(0.75f, 4.375f, 0.375f, 4f).end().
				face(Direction.DOWN).uvs(1.125f, 4f, 0.75f, 4.375f).end().
				texture("#all").end().
				element().
				from(10f, 2f, 12f).
				to(13f, 5f, 15f).
				face(Direction.NORTH).uvs(3.375f, 5.875f, 3.75f, 6.25f).end().
				face(Direction.EAST).uvs(3f, 5.875f, 3.375f, 6.25f).end().
				face(Direction.SOUTH).uvs(4.125f, 5.875f, 4.5f, 6.25f).end().
				face(Direction.WEST).uvs(3.75f, 5.875f, 4.125f, 6.25f).end().
				face(Direction.UP).uvs(3.75f, 5.875f, 3.375f, 5.5f).end().
				face(Direction.DOWN).uvs(4.125f, 5.5f, 3.75f, 5.875f).end().
				texture("#all").end().
				element().
				from(12f, 2f, 9f).
				to(15f, 5f, 12f).
				face(Direction.NORTH).uvs(1.875f, 5.875f, 2.25f, 6.25f).end().
				face(Direction.EAST).uvs(1.5f, 5.875f, 1.875f, 6.25f).end().
				face(Direction.SOUTH).uvs(2.625f, 5.875f, 3f, 6.25f).end().
				face(Direction.WEST).uvs(2.25f, 5.875f, 2.625f, 6.25f).end().
				face(Direction.UP).uvs(2.25f, 5.875f, 1.875f, 5.5f).end().
				face(Direction.DOWN).uvs(2.625f, 5.5f, 2.25f, 5.875f).end().
				texture("#all").end().
				element().
				from(10f, 2f, 6f).
				to(13f, 5f, 9f).
				face(Direction.NORTH).uvs(3.375f, 5.125f, 3.75f, 5.5f).end().
				face(Direction.EAST).uvs(3f, 5.125f, 3.375f, 5.5f).end().
				face(Direction.SOUTH).uvs(4.125f, 5.125f, 4.5f, 5.5f).end().
				face(Direction.WEST).uvs(3.75f, 5.125f, 4.125f, 5.5f).end().
				face(Direction.UP).uvs(3.75f, 5.125f, 3.375f, 4.75f).end().
				face(Direction.DOWN).uvs(4.125f, 4.75f, 3.75f, 5.125f).end().
				texture("#all").end().
				element().
				from(11f, 2f, 3f).
				to(14f, 5f, 6f).
				face(Direction.NORTH).uvs(4.875f, 5.875f, 5.25f, 6.25f).end().
				face(Direction.EAST).uvs(4.5f, 5.875f, 4.875f, 6.25f).end().
				face(Direction.SOUTH).uvs(5.625f, 5.875f, 6f, 6.25f).end().
				face(Direction.WEST).uvs(5.25f, 5.875f, 5.625f, 6.25f).end().
				face(Direction.UP).uvs(5.25f, 5.875f, 4.875f, 5.5f).end().
				face(Direction.DOWN).uvs(5.625f, 5.5f, 5.25f, 5.875f).end().
				texture("#all").end().
				element().
				from(0f, 0f, 0f).
				to(16f, 1f, 16f).
				face(Direction.NORTH).uvs(2f, 2f, 4f, 2.125f).end().
				face(Direction.EAST).uvs(0f, 2f, 2f, 2.125f).end().
				face(Direction.SOUTH).uvs(6f, 2f, 8f, 2.125f).end().
				face(Direction.WEST).uvs(4f, 2f, 6f, 2.125f).end().
				face(Direction.UP).uvs(4f, 2f, 2f, 0f).end().
				face(Direction.DOWN).uvs(6f, 0f, 4f, 2f).end().
				texture("#all").end().
				element().
				from(1f, 1f, 1f).
				to(15f, 2f, 15f).
				face(Direction.NORTH).uvs(1.75f, 3.875f, 3.5f, 4f).end().
				face(Direction.EAST).uvs(0f, 3.875f, 1.75f, 4f).end().
				face(Direction.SOUTH).uvs(5.25f, 3.875f, 7f, 4f).end().
				face(Direction.WEST).uvs(3.5f, 3.875f, 5.25f, 4f).end().
				face(Direction.UP).uvs(3.5f, 3.875f, 1.75f, 2.125f).end().
				face(Direction.DOWN).uvs(5.25f, 2.125f, 3.5f, 3.875f).end().
				texture("#all").end().
				element().
				from(12f, 3f, 4f).
				to(13f, 4f, 5f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(6f, 6f, 4f).
				to(7f, 7f, 5f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(10f, 6f, 8f).
				to(11f, 7f, 9f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(7f, 3f, 13f).
				to(8f, 4f, 14f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(11f, 6f, 11f).
				to(12f, 7f, 12f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(5f, 9f, 9f).
				to(6f, 10f, 10f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(9f, 12f, 6f).
				to(10f, 13f, 7f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(2f, 3f, 7f).
				to(3f, 4f, 8f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(3f, 3f, 3f).
				to(4f, 4f, 4f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(11f, 3f, 7f).
				to(12f, 4f, 8f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(5f, 9f, 5f).
				to(6f, 10f, 6f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(8f, 6f, 12f).
				to(9f, 7f, 13f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(6f, 12f, 7f).
				to(7f, 13f, 8f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(2f, 3f, 11f).
				to(3f, 4f, 12f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(11f, 3f, 13f).
				to(12f, 4f, 14f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(8f, 9f, 8f).
				to(9f, 10f, 9f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(11f, 6f, 4f).
				to(12f, 7f, 5f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(8f, 3f, 2f).
				to(9f, 4f, 3f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(13f, 3f, 10f).
				to(14f, 4f, 11f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(10f, 9f, 5f).
				to(11f, 10f, 6f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(9f, 9f, 11f).
				to(10f, 10f, 12f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(8f, 12f, 10f).
				to(9f, 13f, 11f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end().
				element().
				from(3f, 6f, 7f).
				to(4f, 7f, 8f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				texture("#all").end().
				element().
				from(4f, 6f, 11f).
				to(5f, 7f, 12f).
				face(Direction.NORTH).uvs(7.125f, 3.75f, 7.25f, 3.875f).end().
				face(Direction.EAST).uvs(7f, 3.75f, 7.125f, 3.875f).end().
				face(Direction.SOUTH).uvs(7.375f, 3.75f, 7.5f, 3.875f).end().
				face(Direction.WEST).uvs(7.25f, 3.75f, 7.375f, 3.875f).end().
				face(Direction.UP).uvs(7.25f, 3.75f, 7.125f, 3.625f).end().
				face(Direction.DOWN).uvs(7.375f, 3.625f, 7.25f, 3.75f).end().
				texture("#all").end();
		
		registerModels(block, model);
	}
	
	private void createHangingMoss()
	{
		BioHangingMossBlock block = Registration.BlockReg.HANGING_MOSS.get();
		ResourceLocation blockTexture = blockTexture(block);
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.cutout().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				element().
				from(0.8f, 0, 8).
				to(15.2f, 16, 8).
				rotation().
				origin(8, 8, 8).
				angle(45).
				axis(Direction.Axis.Y).
				rescale(true).end().
				shade(false).
				allFacesExcept((direction, faceBuilder) -> faceBuilder.
						uvs(0, 0, 16, 16).
						texture("#all"),
						EnumSet.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST)).end().
				element().
				from(8, 0, 0.8f).
				to(8, 16, 15.2f).
				rotation().
				origin(8, 8, 8).
				angle(45).
				axis(Direction.Axis.Y).
				rescale(true).end().
				shade(false).
				allFacesExcept((direction, faceBuilder) -> faceBuilder.
						uvs(0, 0, 16, 16).
						texture("#all"),
				EnumSet.of(Direction.UP, Direction.DOWN, Direction.NORTH,Direction.SOUTH)).end();
		
		
		getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().
				modelFile(model).
				build());
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
	}
	
	private void createChestModel()
	{
		BioChestBlock block = Registration.BlockReg.CHEST.get();
		ResourceLocation blockTexture = BuiltInRegistries.BLOCK.getKey(block).withPrefix("swarm_").withPrefix("block/").withSuffix("/0");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				element().
				from(3, 2, 3).
				to(13, 3, 13).
				face(Direction.NORTH).
				uvs(7.25f, 8.75f, 8.5f, 8.875f).end().
				face(Direction.EAST).
				uvs(6, 8.75f, 7.25f, 8.875f).end().
				face(Direction.SOUTH).
				uvs(9.75f, 8.75f, 11, 8.875f).end().
				face(Direction.WEST).
				uvs(8.5f, 8.75f, 9.75f, 8.875f).end().
				face(Direction.UP).
				uvs(8.5f, 8.75f, 7.25f, 7.5f).end().
				face(Direction.DOWN).
				uvs(9.75f, 7.5f, 8.5f, 8.75f).end().
				texture("#all").end().
				element().
				from(2.5f, 3, 2.5f).
				to(13.5f, 4, 13.5f).
				face(Direction.NORTH).
				uvs(8.875f, 1.375f, 10.25f, 1.5f).end().
				face(Direction.EAST).
				uvs(7.5f, 1.375f, 8.875f, 1.5f).end().
				face(Direction.SOUTH).
				uvs(11.625f, 1.375f, 13, 1.5f).end().
				face(Direction.WEST).
				uvs(10.25f, 1.375f, 11.625f, 1.5f).end().
				face(Direction.UP).
				uvs(10.25f, 1.375f, 8.875f, 0).end().
				face(Direction.DOWN).
				uvs(11.625f, 0, 10.25f, 1.375f).end().
				texture("#all").end().
				element().
				from(3.5f, 1, 3.5f).
				to(12.5f, 2, 12.5f).
				face(Direction.NORTH).
				uvs(7.125f, 10, 8.25f, 10.125f).end().
				face(Direction.EAST).
				uvs(6, 10, 7.125f, 10.125f).end().
				face(Direction.SOUTH).
				uvs(9.375f, 10, 10.5f, 10.125f).end().
				face(Direction.WEST).
				uvs(8.25f, 10, 9.375f, 10.125f).end().
				face(Direction.UP).
				uvs(8.25f, 10, 7.125f, 8.875f).end().
				face(Direction.DOWN).
				uvs(9.375f, 8.875f, 8.25f, 10).end().
				texture("#all").end().
				element().
				from(2.5f, 11, 12.5f).
				to(13.5f, 12, 13.5f).
				face(Direction.NORTH).
				uvs(7.625f, 1.625f, 9, 1.75f).end().
				face(Direction.EAST).
				uvs(7.5f, 1.625f, 7.625f, 1.75f).end().
				face(Direction.SOUTH).
				uvs(9.125f, 1.625f, 10.5f, 1.75f).end().
				face(Direction.WEST).
				uvs(9, 1.625f, 9.125f, 1.75f).end().
				face(Direction.UP).
				uvs(9, 1.625f, 7.625f, 1.5f).end().
				face(Direction.DOWN).
				uvs(10.375f, 1.5f, 9, 1.625f).end().
				texture("#all").end().
				element().
				from(2.5f, 11, 2.5f).
				to(13.5f, 12, 3.5f).
				face(Direction.NORTH).
				uvs(7.625f, 1.875f, 9, 2).end().
				face(Direction.EAST).
				uvs(7.5f, 1.875f, 7.625f, 2).end().
				face(Direction.SOUTH).
				uvs(9.125f, 1.875f, 10.5f, 2).end().
				face(Direction.WEST).
				uvs(9, 1.875f, 9.125f, 2).end().
				face(Direction.UP).
				uvs(9, 1.875f, 7.625f, 1.75f).end().
				face(Direction.DOWN).
				uvs(10.375f, 1.75f, 9, 1.875f).end().
				texture("#all").end().
				element().
				from(2.5f, 11, 3.45f).
				to(3.5f, 12, 12.45f).
				face(Direction.NORTH).
				uvs(9, 10.25f, 9.125f, 10.375f).end().
				face(Direction.EAST).
				uvs(10.375f, 10.25f, 11.5f, 10.375f).end().
				face(Direction.SOUTH).
				uvs(10.25f, 10.25f, 10.375f, 10.375f).end().
				face(Direction.WEST).
				uvs(9.125f, 10.25f, 10.25f, 10.375f).end().
				face(Direction.UP).
				uvs(10.25f, 10.25f, 9.125f, 10.125f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(11.375f, 10.125f, 10.25f, 10.25f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(12.5f, 11, 3.5f).
				to(13.5f, 12, 12.5f).
				face(Direction.NORTH).
				uvs(10.25f, 5.375f, 10.375f, 5.5f).end().
				face(Direction.EAST).
				uvs(11.625f, 5.375f, 12.75f, 5.5f).end().
				face(Direction.SOUTH).
				uvs(11.5f, 5.375f, 11.625f, 5.5f).end().
				face(Direction.WEST).
				uvs(10.375f, 5.375f, 11.5f, 5.5f).end().
				face(Direction.UP).
				uvs(11.5f, 5.375f, 10.375f, 5.25f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(12.625f, 5.25f, 11.5f, 5.375f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(2, 4, 2).
				to(14, 5, 14).
				face(Direction.NORTH).
				uvs(8.5f, 3.5f, 10, 3.625f).end().
				face(Direction.EAST).
				uvs(7, 3.5f, 8.5f, 3.625f).end().
				face(Direction.SOUTH).
				uvs(11.5f, 3.5f, 13, 3.625f).end().
				face(Direction.WEST).
				uvs(10, 3.5f, 11.5f, 3.625f).end().
				face(Direction.UP).
				uvs(10, 3.5f, 8.5f, 2).end().
				face(Direction.DOWN).
				uvs(11.5f, 2, 10, 3.5f).end().
				texture("#all").end().
				element().
				from(2, 0, 2).
				to(14, 1, 14).
				face(Direction.NORTH).
				uvs(8.5f, 5.125f, 10, 5.25f).end().
				face(Direction.EAST).
				uvs(7, 5.125f, 8.5f, 5.25f).end().
				face(Direction.SOUTH).
				uvs(11.5f, 5.125f, 13, 5.25f).end().
				face(Direction.WEST).
				uvs(10, 5.125f, 11.5f, 5.25f).end().
				face(Direction.UP).
				uvs(10, 5.125f, 8.5f, 3.625f).end().
				face(Direction.DOWN).
				uvs(11.5f, 3.625f, 10, 5.125f).end().
				texture("#all").end().
				element().
				from(2, 10, 13).
				to(14, 11, 14).
				face(Direction.NORTH).
				uvs(7.125f, 5.375f, 8.625f, 5.5f).end().
				face(Direction.EAST).
				uvs(7, 5.375f, 7.125f, 5.5f).end().
				face(Direction.SOUTH).
				uvs(8.75f, 5.375f, 10.25f, 5.5f).end().
				face(Direction.WEST).
				uvs(8.625f, 5.375f, 8.75f, 5.5f).end().
				face(Direction.UP).
				uvs(8.625f, 5.375f, 7.125f, 5.25f).end().
				face(Direction.DOWN).
				uvs(10.125f, 5.25f, 8.625f, 5.375f).end().
				texture("#all").end().
				element().
				from(2, 10, 2).
				to(14, 11, 3).
				face(Direction.NORTH).
				uvs(7.125f, 5.625f, 8.625f, 5.75f).end().
				face(Direction.EAST).
				uvs(7, 5.625f, 7.125f, 5.75f).end().
				face(Direction.SOUTH).
				uvs(8.75f, 5.625f, 10.25f, 5.75f).end().
				face(Direction.WEST).
				uvs(8.625f, 5.625f, 8.75f, 5.75f).end().
				face(Direction.UP).
				uvs(8.625f, 5.625f, 7.125f, 5.5f).end().
				face(Direction.DOWN).
				uvs(10.125f, 5.5f, 8.625f, 5.625f).end().
				texture("#all").end().
				element().
				from(13, 10, 3).
				to(14, 11, 13).
				face(Direction.NORTH).
				uvs(0, 9.125f, 0.125f, 9.25f).end().
				face(Direction.EAST).
				uvs(1.5f, 9.125f, 2.75f, 9.25f).end().
				face(Direction.SOUTH).
				uvs(1.375f, 9.125f, 1.5f, 9.25f).end().
				face(Direction.WEST).
				uvs(0.125f, 9.125f, 1.375f, 9.25f).end().
				face(Direction.UP).
				uvs(1.375f, 9.125f, 0.125f, 9).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(2.625f, 9, 1.375f, 9.125f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(2, 10, 2.95f).
				to(3, 11, 12.95f).
				face(Direction.NORTH).
				uvs(2.75f, 9.125f, 2.875f, 9.25f).end().
				face(Direction.EAST).
				uvs(4.25f, 9.125f, 5.5f, 9.25f).end().
				face(Direction.SOUTH).
				uvs(4.125f, 9.125f, 4.25f, 9.25f).end().
				face(Direction.WEST).
				uvs(2.875f, 9.125f, 4.125f, 9.25f).end().
				face(Direction.UP).
				uvs(4.125f, 9.125f, 2.875f, 9).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(5.375f, 9, 4.125f, 9.125f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(1.5f, 5, 1.5f).
				to(14.5f, 6, 14.5f).
				face(Direction.NORTH).
				uvs(1.625f, 7.375f, 3.25f, 7.5f).end().
				face(Direction.EAST).
				uvs(0, 7.375f, 1.625f, 7.5f).end().
				face(Direction.SOUTH).
				uvs(4.875f, 7.375f, 6.5f, 7.5f).end().
				face(Direction.WEST).
				uvs(3.25f, 7.375f, 4.875f, 7.5f).end().
				face(Direction.UP).
				uvs(3.25f, 7.375f, 1.625f, 5.75f).end().
				face(Direction.DOWN).
				uvs(4.875f, 5.75f, 3.25f, 7.375f).end().
				texture("#all").end().
				element().
				from(1.5f, 9, 1.5f).
				to(14.5f, 10, 14.5f).
				face(Direction.NORTH).
				uvs(8.125f, 7.375f, 9.75f, 7.5f).end().
				face(Direction.EAST).
				uvs(6.5f, 7.375f, 8.125f, 7.5f).end().
				face(Direction.SOUTH).
				uvs(11.375f, 7.375f, 13, 7.5f).end().
				face(Direction.WEST).
				uvs(9.75f, 7.375f, 11.375f, 7.5f).end().
				face(Direction.UP).
				uvs(9.75f, 7.375f, 8.125f, 5.75f).end().
				face(Direction.DOWN).
				uvs(11.375f, 5.75f, 9.75f, 7.375f).end().
				texture("#all").end().
				element().
				from(1, 8, 1).
				to(15, 9, 15).
				face(Direction.NORTH).
				uvs(1.75f, 3.75f, 3.5f, 3.875f).end().
				face(Direction.EAST).
				uvs(0, 3.75f, 1.75f, 3.875f).end().
				face(Direction.SOUTH).
				uvs(5.25f, 3.75f, 7, 3.875f).end().
				face(Direction.WEST).
				uvs(3.5f, 3.75f, 5.25f, 3.875f).end().
				face(Direction.UP).
				uvs(3.5f, 3.75f, 1.75f, 2).end().
				face(Direction.DOWN).
				uvs(5.25f, 2, 3.5f, 3.75f).end().
				texture("#all").end().
				element().
				from(1, 6, 1).
				to(15, 7, 15).
				face(Direction.NORTH).
				uvs(1.75f, 5.625f, 3.5f, 5.75f).end().
				face(Direction.EAST).
				uvs(0, 5.625f, 1.75f, 5.75f).end().
				face(Direction.SOUTH).
				uvs(5.25f, 5.625f, 7, 5.75f).end().
				face(Direction.WEST).
				uvs(3.5f, 5.625f, 5.25f, 5.75f).end().
				face(Direction.UP).
				uvs(3.5f, 5.625f, 1.75f, 3.875f).end().
				face(Direction.DOWN).
				uvs(5.25f, 3.875f, 3.5f, 5.625f).end().
				texture("#all").end().
				element().
				from(0.5f, 7, 0.75f).
				to(15.5f, 8, 15.75f).
				face(Direction.NORTH).
				uvs(1.875f, 1.875f, 3.75f, 2).end().
				face(Direction.EAST).
				uvs(0, 1.875f, 1.875f, 2).end().
				face(Direction.SOUTH).
				uvs(5.625f, 1.875f, 7.5f, 2).end().
				face(Direction.WEST).
				uvs(3.75f, 1.875f, 5.625f, 2).end().
				face(Direction.UP).
				uvs(3.75f, 1.875f, 1.875f, 0).end().
				face(Direction.DOWN).
				uvs(5.625f, 0, 3.75f, 1.875f).end().
				texture("#all").end().
				element().
				from(4, 11.5f, 3).
				to(12, 14.5f, 4).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.X).
				origin(8, 12.5f, 3.5f).end().
				face(Direction.NORTH).
				uvs(0.125f, 10, 1.125f, 10.375f).end().
				face(Direction.EAST).
				uvs(0, 10, 0.125f, 10.375f).end().
				face(Direction.SOUTH).
				uvs(1.25f, 10, 2.25f, 10.375f).end().
				face(Direction.WEST).
				uvs(1.125f, 10, 1.25f, 10.375f).end().
				face(Direction.UP).
				uvs(1.125f, 10, 0.125f, 9.875f).end().
				face(Direction.DOWN).
				uvs(2.125f, 9.875f, 1.125f, 10).end().
				texture("#all").end().
				element().
				from(4.14767f, 13.88793f, 4).
				to(8.14767f, 14.88793f, 12).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(5.64767f, 14.38793f, 8).end().
				face(Direction.NORTH).
				uvs(0, 9.375f, 0.125f, 9.875f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.EAST).
				uvs(1.125f, 9.375f, 0.125f, 9.25f).end().
				face(Direction.SOUTH).
				uvs(1.125f, 9.375f, 1.25f, 9.875f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.WEST).
				uvs(2.125f, 9.25f, 1.125f, 9.375f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				face(Direction.UP).
				uvs(0.125f, 9.375f, 1.125f, 9.875f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(1.25f, 9.375f, 2.25f, 9.875f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(3, 11.5f, 4).
				to(4, 14.5f, 12).
				rotation().
				angle(-22.5f).
				axis(Direction.Axis.Z).
				origin(3.5f, 12.5f, 8).end().
				face(Direction.NORTH).
				uvs(2.25f, 10, 2.375f, 10.375f).end().
				face(Direction.EAST).
				uvs(3.5f, 10, 4.5f, 10.375f).end().
				face(Direction.SOUTH).
				uvs(3.375f, 10, 3.5f, 10.375f).end().
				face(Direction.WEST).
				uvs(2.375f, 10, 3.375f, 10.375f).end().
				face(Direction.UP).
				uvs(3.375f, 10, 2.375f, 9.875f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(4.375f, 9.875f, 3.375f, 10).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(4, 11.5f, 12).
				to(12, 14.5f, 13).
				rotation().
				angle(-22.5f).
				axis(Direction.Axis.X).
				origin(8, 12.5f, 12.5f).end().
				face(Direction.NORTH).
				uvs(4.625f, 10.25f, 5.625f, 10.625f).end().
				face(Direction.EAST).
				uvs(4.5f, 10.25f, 4.625f, 10.625f).end().
				face(Direction.SOUTH).
				uvs(5.75f, 10.25f, 6.75f, 10.625f).end().
				face(Direction.WEST).
				uvs(5.625f, 10.25f, 5.75f, 10.625f).end().
				face(Direction.UP).
				uvs(5.625f, 10.25f, 4.625f, 10.125f).end().
				face(Direction.DOWN).
				uvs(6.625f, 10.125f, 5.625f, 10.25f).end().
				texture("#all").end().
				element().
				from(8.14767f, 13.87793f, 4.01f).
				to(12.14767f, 14.87793f, 12.01f).
				rotation().
				angle(-22.5f).
				axis(Direction.Axis.Z).
				origin(10.64767f, 14.38793f, 8.01f).end().
				face(Direction.NORTH).
				uvs(2.25f, 9.375f, 2.375f, 9.875f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.EAST).
				uvs(4.375f, 9.25f, 3.375f, 9.375f).end().
				face(Direction.SOUTH).
				uvs(3.375f, 9.375f, 3.5f, 9.875f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.WEST).
				uvs(3.375f, 9.375f, 2.375f, 9.25f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				face(Direction.UP).
				uvs(3.5f, 9.375f, 4.5f, 9.875f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(2.375f, 9.375f, 3.375f, 9.875f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(12, 11.5f, 4).
				to(13, 14.5f, 12).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(12.5f, 12.5f, 8).end().
				face(Direction.NORTH).
				uvs(7.875f, 10.25f, 8, 10.625f).end().
				face(Direction.EAST).
				uvs(6.875f, 10.25f, 7.875f, 10.625f).end().
				face(Direction.SOUTH).
				uvs(6.75f, 10.25f, 6.875f, 10.625f).end().
				face(Direction.WEST).
				uvs(8, 10.25f, 9, 10.625f).end().
				face(Direction.UP).
				uvs(7.875f, 10.25f, 6.875f, 10.125f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(8.875f, 10.125f, 7.875f, 10.25f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(2, 10.25f, 2).
				to(14, 10.25f, 14).
				face(Direction.NORTH).
				uvs(1.5f, 9, 3, 9).end().
				face(Direction.EAST).
				uvs(0, 9, 1.5f, 9).end().
				face(Direction.SOUTH).
				uvs(4.5f, 9, 6, 9).end().
				face(Direction.WEST).
				uvs(3, 9, 4.5f, 9).end().
				face(Direction.UP).
				uvs(3, 9, 1.5f, 7.5f).end().
				face(Direction.DOWN).
				uvs(4.5f, 7.5f, 3, 9).end().
				texture("#all").end().
				element().
				from(11.75f, 12, 11.75f).
				to(12.75f, 13, 12.75f).
				face(Direction.NORTH).
				uvs(5.625f, 9.375f, 5.75f, 9.5f).end().
				face(Direction.EAST).
				uvs(5.5f, 9.375f, 5.625f, 9.5f).end().
				face(Direction.SOUTH).
				uvs(5.875f, 9.375f, 6, 9.5f).end().
				face(Direction.WEST).
				uvs(5.75f, 9.375f, 5.875f, 9.5f).end().
				face(Direction.UP).
				uvs(5.75f, 9.375f, 5.625f, 9.25f).end().
				face(Direction.DOWN).
				uvs(5.875f, 9.25f, 5.75f, 9.375f).end().
				texture("#all").end().
				element().
				from(11.75f, 12, 3.25f).
				to(12.75f, 13, 4.25f).
				face(Direction.NORTH).
				uvs(5.625f, 9.125f, 5.75f, 9.25f).end().
				face(Direction.EAST).
				uvs(5.5f, 9.125f, 5.625f, 9.25f).end().
				face(Direction.SOUTH).
				uvs(5.875f, 9.125f, 6, 9.25f).end().
				face(Direction.WEST).
				uvs(5.75f, 9.125f, 5.875f, 9.25f).end().
				face(Direction.UP).
				uvs(5.75f, 9.125f, 5.625f, 9).end().
				face(Direction.DOWN).
				uvs(5.875f, 9, 5.75f, 9.125f).end().
				texture("#all").end().
				element().
				from(3.25f, 12, 3.25f).
				to(4.25f, 13, 4.25f).
				face(Direction.NORTH).
				uvs(4.625f, 9.375f, 4.75f, 9.5f).end().
				face(Direction.EAST).
				uvs(4.5f, 9.375f, 4.625f, 9.5f).end().
				face(Direction.SOUTH).
				uvs(4.875f, 9.375f, 5, 9.5f).end().
				face(Direction.WEST).
				uvs(4.75f, 9.375f, 4.875f, 9.5f).end().
				face(Direction.UP).
				uvs(4.75f, 9.375f, 4.625f, 9.25f).end().
				face(Direction.DOWN).
				uvs(4.875f, 9.25f, 4.75f, 9.375f).end().
				texture("#all").end().
				element().
				from(3.25f, 12, 11.75f).
				to(4.25f, 13, 12.75f).
				face(Direction.NORTH).
				uvs(5.125f, 9.375f, 5.25f, 9.5f).end().
				face(Direction.EAST).
				uvs(5, 9.375f, 5.125f, 9.5f).end().
				face(Direction.SOUTH).
				uvs(5.375f, 9.375f, 5.5f, 9.5f).end().
				face(Direction.WEST).
				uvs(5.25f, 9.375f, 5.375f, 9.5f).end().
				face(Direction.UP).
				uvs(5.25f, 9.375f, 5.125f, 9.25f).end().
				face(Direction.DOWN).
				uvs(5.375f, 9.25f, 5.25f, 9.375f).end().
				texture("#all").end();
		
		registerModels(block, model);
	}
	
	private void createDecoHiveModel()
	{
		BioBaseBlock block = Registration.BlockReg.HIVE_DECO.get();
		ResourceLocation blockTexture = blockTexture(block).withSuffix("/0");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				element().
				from(0, 0, 0).
				to(16, 16, 16).
				allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 0, 16, 16).
						texture("#all").
						cullface(direction)).end();
		
		registerModels(block, model);
	}
	
	private void createRoofModel()
	{
		BioBaseBlock block = Registration.BlockReg.ROOF.get();
		ResourceLocation blockTexture = blockTexture(block);
		
		ModelFile blockModel = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("top", blockTexture.withSuffix("_top")).
				texture("side", blockTexture.withSuffix("_middle")).
				texture("bottom", blockTexture.withSuffix("_bot")).
				texture("particle", blockTexture.withSuffix("_middle")).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(0,0,0).
				to(16, 16, 16).
				allFaces((direction, faceBuilder) ->
				{
					faceBuilder.uvs(0, 0, 16, 16);
					if (direction.getAxis().isHorizontal())
						faceBuilder.texture("#side");
					else if (direction == Direction.UP)
						faceBuilder.texture("#top");
					else
						faceBuilder.texture("#bottom");
				}).
				end();
		
		registerModels(block, blockModel);
		
		block = Registration.BlockReg.ROOF_DIRT.get();
		ModelFile dirtModel = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture.withSuffix("_bot")).
				texture("particle", blockTexture.withSuffix("_bot")).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(0, 0, 0).
				to(16, 16, 16).
				allFaces((direction, faceBuilder) -> faceBuilder.
				uvs(0, 0, 16, 16).
				texture("#all")).
				end();
		
		registerModels(block, dirtModel);
		
		StairBlock stair = Registration.BlockReg.ROOF_STAIRS.get();
		blockTexture = blockTexture(stair.base);
		
		ModelFile modelInner = models().stairsInner(blockPrefix(name(stair)) + "_inner", blockTexture.withSuffix("_middle"), blockTexture.withSuffix("_bot"), blockTexture.withSuffix("_top"));
		ModelFile modelStraight = models().stairs(blockPrefix(name(stair)), blockTexture.withSuffix("_middle"), blockTexture.withSuffix("_bot"), blockTexture.withSuffix("_top"));
		ModelFile modelOuter = models().stairsOuter(blockPrefix(name(stair)) + "_outer", blockTexture.withSuffix("_middle"), blockTexture.withSuffix("_bot"), blockTexture.withSuffix("_top"));

		stairsBlock(stair, modelStraight, modelInner, modelOuter);
		itemModels().getBuilder(itemPrefix(name(stair))).
				parent(modelStraight);
		
		SlabBlock slab = Registration.BlockReg.ROOF_SLAB.get();
		
		ModelFile bottom = models().slab(blockPrefix(name(slab)), blockTexture.withSuffix("_middle"), blockTexture.withSuffix("_bot"), blockTexture.withSuffix("_top"));
		ModelFile top = models().slabTop(blockPrefix(name(slab)) + "_top", blockTexture.withSuffix("_middle"), blockTexture.withSuffix("_bot"), blockTexture.withSuffix("_top"));
		
		slabBlock(slab, bottom, top, blockModel);
		itemModels().getBuilder(itemPrefix(name(slab))).
				parent(bottom);
	}
	
	private void createNorphedDirt()
	{
		BioBaseBlock[] blocks = new BioBaseBlock[]
				{
						Registration.BlockReg.NORPHED_DIRT_0.get(),
						Registration.BlockReg.NORPHED_DIRT_1.get()
				};
		
		ResourceLocation[] blockTexture = new ResourceLocation[]
				{
						blockTexture(blocks[0]),
						blockTexture(blocks[1])
				};
		ModelFile[] blockModels = new ModelFile[2];
		for (int q = 0; q < 2; q++)
		{
			blockModels[q] = models().withExistingParent(blockPrefix(name(blocks[q])), mcLoc(blockPrefix("block"))).
					renderType(RenderType.solid().name).
					texture("all", blockTexture[q]).
					texture("particle", blockTexture[q]).
					guiLight(BlockModel.GuiLight.SIDE).
					element().
					from(0,0,0).
					to(16, 16, 16).
					allFaces((direction, faceBuilder) ->
					faceBuilder.uvs(0, 0, 16, 16)).
					texture("#all").end();
			
			registerModels(blocks[q], blockModels[q]);
		}
		
		StairBlock[] stairBlocks = new StairBlock[]
				{
						Registration.BlockReg.NORPHED_DIRT_STAIR_0.get(),
						Registration.BlockReg.NORPHED_DIRT_STAIR_1.get()
				};
		
		for (int q = 0; q < 2; q++)
		{
			ModelFile innerModel = models().stairsInner(blockPrefix(name(stairBlocks[q])) + "_inner_" + q, blockTexture[q], blockTexture[q], blockTexture[q]);
			ModelFile straightModel = models().stairs(blockPrefix(name(stairBlocks[q])) + "_" + q, blockTexture[q], blockTexture[q], blockTexture[q]);
			ModelFile outerModel = models().stairsOuter(blockPrefix(name(stairBlocks[q])) + "_outer_" + q, blockTexture[q], blockTexture[q], blockTexture[q]);
			
			stairsBlock(stairBlocks[q], straightModel, innerModel, outerModel);
			
			itemModels().getBuilder(itemPrefix(name(stairBlocks[q]))).
					parent(straightModel);
		}
		
		SlabBlock[] slabBlocks = new SlabBlock[]
				{
						Registration.BlockReg.NORPHED_DIRT_SLAB_0.get(),
						Registration.BlockReg.NORPHED_DIRT_SLAB_1.get()
				};
		
		for (int q = 0; q < 2; q++)
		{
			ModelFile bottom = models().slab(blockPrefix(name(slabBlocks[q])), blockTexture[q], blockTexture[q], blockTexture[q]);
			ModelFile top = models().slabTop(blockPrefix(name(slabBlocks[q]) + "_top"), blockTexture[q], blockTexture[q], blockTexture[q]);
			
			slabBlock(slabBlocks[q], bottom, top, blockModels[q]);
			
			itemModels().getBuilder(itemPrefix(name(slabBlocks[q]))).
					parent(bottom);
		}
		
	}
	
	private void createGlowMoss()
	{
		GlowMossBlock block = Registration.BlockReg.GLOW_MOSS.get();
		ResourceLocation blockTexture = blockTexture(block);
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(0f, 0f, 0.1f).
				to(16f, 16f, 0.1f).
				face(Direction.NORTH).
				uvs(0f, 0f, 8f, 8f).end().
				face(Direction.SOUTH).
				uvs(0f, 0f, 8f, 8f).end().
				texture("#all").end().
				element().
				from(10f, 9f, 0.1f).
				to(12f, 11f, 2.1f).
				face(Direction.NORTH).
				uvs(8f, 0f, 9f, 1f).end().
				face(Direction.EAST).
				uvs(8f, 1f, 9f, 2f).end().
				face(Direction.SOUTH).
				uvs(8f, 2f, 9f, 3f).end().
				face(Direction.WEST).
				uvs(8f, 3f, 9f, 4f).end().
				face(Direction.UP).
				uvs(9f, 5f, 8f, 4f).end().
				face(Direction.DOWN).
				uvs(9f, 5f, 8f, 6f).end().
				texture("#all").end().
				element().
				from(4f, 12f, 0.1f).
				to(6f, 14f, 1.1f).
				face(Direction.NORTH).
				uvs(8f, 6f, 9f, 7f).end().
				face(Direction.EAST).
				uvs(9f, 7f, 9.5f, 8f).end().
				face(Direction.SOUTH).
				uvs(8f, 7f, 9f, 8f).end().
				face(Direction.WEST).
				uvs(8f, 9f, 8.5f, 10f).end().
				face(Direction.UP).
				uvs(10f, 8.5f, 9f, 8f).end().
				face(Direction.DOWN).
				uvs(9.5f, 9f, 8.5f, 9.5f).end().
				texture("#all").end().
				element().
				from(4f, 4f, 0.1f).
				to(6f, 6f, 2.1f).
				face(Direction.NORTH).
				uvs(8f, 8f, 9f, 9f).end().
				face(Direction.EAST).
				uvs(9f, 0f, 10f, 1f).end().
				face(Direction.SOUTH).
				uvs(9f, 1f, 10f, 2f).end().
				face(Direction.WEST).
				uvs(9f, 2f, 10f, 3f).end().
				face(Direction.UP).
				uvs(10f, 4f, 9f, 3f).end().
				face(Direction.DOWN).
				uvs(10f, 4f, 9f, 5f).end().
				texture("#all").end().
				element().
				from(12f, 2f, 0.1f).
				to(14f, 4f, 1.1f).
				face(Direction.NORTH).
				uvs(9f, 5f, 10f, 6f).end().
				face(Direction.EAST).
				uvs(9.5f, 7f, 10f, 8f).end().
				face(Direction.SOUTH).
				uvs(9f, 6f, 10f, 7f).end().
				face(Direction.WEST).
				uvs(8.5f, 9.5f, 9f, 10.5f).end().
				face(Direction.UP).
				uvs(10f, 9f, 9f, 8.5f).end().
				face(Direction.DOWN).
				uvs(10f, 9.5f, 9f, 10f).end().
				texture("#all").end().
				element().
				from(11f, 5f, 0.1f).
				to(12f, 6f, 1.1f).
				face(Direction.NORTH).
				uvs(9.5f, 9f, 10f, 9.5f).end().
				face(Direction.EAST).
				uvs(10f, 0f, 10.5f, 0.5f).end().
				face(Direction.SOUTH).
				uvs(10f, 0.5f, 10.5f, 1f).end().
				face(Direction.WEST).
				uvs(10f, 1f, 10.5f, 1.5f).end().
				face(Direction.UP).
				uvs(10.5f, 2f, 10f, 1.5f).end().
				face(Direction.DOWN).
				uvs(10.5f, 2f, 10f, 2.5f).end().
				texture("#all").end().
				element().
				from(6f, 8f, 0.1f).
				to(7f, 9f, 1.1f).
				face(Direction.NORTH).
				uvs(10f, 2.5f, 10.5f, 3f).end().
				face(Direction.EAST).
				uvs(10f, 3f, 10.5f, 3.5f).end().
				face(Direction.SOUTH).
				uvs(10f, 3.5f, 10.5f, 4f).end().
				face(Direction.WEST).
				uvs(10f, 4f, 10.5f, 4.5f).end().
				face(Direction.UP).
				uvs(10.5f, 5f, 10f, 4.5f).end().
				face(Direction.DOWN).
				uvs(10.5f, 5f, 10f, 5.5f).end().
				texture("#all").end().
				element().
				from(13f, 13f, 0.1f).
				to(14f, 14f, 1.1f).
				face(Direction.NORTH).
				uvs(10f, 5.5f, 10.5f, 6f).end().
				face(Direction.EAST).
				uvs(10f, 6f, 10.5f, 6.5f).end().
				face(Direction.SOUTH).
				uvs(10f, 6.5f, 10.5f, 7f).end().
				face(Direction.WEST).
				uvs(10f, 7f, 10.5f, 7.5f).end().
				face(Direction.UP).
				uvs(10.5f, 8f, 10f, 7.5f).end().
				face(Direction.DOWN).
				uvs(8.5f, 10f, 8f, 10.5f).end().
				texture("#all").end().
				element().
				from(8f, 2f, 0.1f).
				to(9f, 3f, 1.1f).
				face(Direction.NORTH).
				uvs(10f, 8f, 10.5f, 8.5f).end().
				face(Direction.EAST).
				uvs(10f, 8.5f, 10.5f, 9f).end().
				face(Direction.SOUTH).
				uvs(9f, 10f, 9.5f, 10.5f).end().
				face(Direction.WEST).
				uvs(10f, 9f, 10.5f, 9.5f).end().
				face(Direction.UP).
				uvs(10f, 10.5f, 9.5f, 10f).end().
				face(Direction.DOWN).
				uvs(10.5f, 9.5f, 10f, 10f).end().
				texture("#all").end().
				element().
				from(2f, 10f, 0.1f).
				to(3f, 11f, 1.1f).
				face(Direction.NORTH).
				uvs(10f, 10f, 10.5f, 10.5f).end().
				face(Direction.EAST).
				uvs(10.5f, 0f, 11f, 0.5f).end().
				face(Direction.SOUTH).
				uvs(10.5f, 0.5f, 11f, 1f).end().
				face(Direction.WEST).
				uvs(10.5f, 1f, 11f, 1.5f).end().
				face(Direction.UP).
				uvs(11f, 2f, 10.5f, 1.5f).end().
				face(Direction.DOWN).
				uvs(11f, 2f, 10.5f, 2.5f).end().
				texture("#all").end().
				element().
				from(9f, 12f, 0.1f).
				to(10f, 13f, 1.1f).
				face(Direction.NORTH).
				uvs(10.5f, 2.5f, 11f, 3f).end().
				face(Direction.EAST).
				uvs(10.5f, 3f, 11f, 3.5f).end().
				face(Direction.SOUTH).
				uvs(10.5f, 3.5f, 11f, 4f).end().
				face(Direction.WEST).
				uvs(10.5f, 4f, 11f, 4.5f).end().
				face(Direction.UP).
				uvs(11f, 5f, 10.5f, 4.5f).end().
				face(Direction.DOWN).
				uvs(11f, 5f, 10.5f, 5.5f).end().
				texture("#all").end().
				element().
				from(2f, 2f, 0.1f).
				to(3f, 3f, 1.1f).
				face(Direction.NORTH).
				uvs(10.5f, 5.5f, 11f, 6f).end().
				face(Direction.EAST).
				uvs(10.5f, 6f, 11f, 6.5f).end().
				face(Direction.SOUTH).
				uvs(10.5f, 6.5f, 11f, 7f).end().
				face(Direction.WEST).
				uvs(10.5f, 7f, 11f, 7.5f).end().
				face(Direction.UP).
				uvs(11f, 8f, 10.5f, 7.5f).end().
				face(Direction.DOWN).
				uvs(8.5f, 10.5f, 8f, 11f).end().
				texture("#all").end();
		
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
		
		PipeBlock.PROPERTY_BY_DIRECTION.
				forEach((dir, value) -> builder.part().
						modelFile(model).
						rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? 270 : 0).
						rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot() + 180) % 360).
						uvLock(dir != Direction.NORTH).
						addModel().
						condition(value, true));
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
		
		VineBlock moss = Registration.BlockReg.MOSS.get();
		
		ModelFile mossModel = models().withExistingParent(blockPrefix(name(moss)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(0f, 0f, 0.1f).
				to(16f, 16f, 0.1f).
				face(Direction.NORTH).
				uvs(0f, 0f, 8f, 8f).end().
				face(Direction.SOUTH).
				uvs(0f, 0f, 8f, 8f).end().
				texture("#all").end();
		
		MultiPartBlockStateBuilder mossBuilder = getMultipartBuilder(moss);
		
		VineBlock.PROPERTY_BY_DIRECTION.
				forEach((dir, value) -> mossBuilder.part().
						modelFile(mossModel).
						rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? 270 : 0).
						rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot() + 180) % 360).
						uvLock(dir != Direction.NORTH).
						addModel().
						condition(value, true));
		
		itemModels().getBuilder(itemPrefix(name(moss))).
				parent(mossModel);
	}
	
	private void createProps()
	{
		BioBaseBlock prop_0 = Registration.BlockReg.PROP_0.get();
		BioBaseBlock prop_1 = Registration.BlockReg.PROP_1.get();
		BioBaseBlock prop_2 = Registration.BlockReg.PROP_2.get();
		
		ResourceLocation textureLoc = Database.rl("props").withPrefix("block/");
		
		ModelFile template0 = models().withExistingParent(blockPrefix(name(prop_0)), mcLoc(blockPrefix("block"))).
						renderType(RenderType.translucent().name).
						texture("all", textureLoc).
						texture("particle", textureLoc).
						guiLight(BlockModel.GuiLight.SIDE).
						element().
						from(2f, 0f, 2f).
						to(14f, 2f, 14f).
						face(Direction.NORTH).
						uvs(10f, 0f, 16f, 1f).end().
						face(Direction.EAST).
						uvs(10f, 1f, 16f, 2f).end().
						face(Direction.SOUTH).
						uvs(10f, 2f, 16f, 3f).end().
						face(Direction.WEST).
						uvs(10f, 3f, 16f, 4f).end().
						face(Direction.UP).
						uvs(6f, 6f, 0f, 0f).end().
						face(Direction.DOWN).
						uvs(6f, 6f, 0f, 12f).end().
						texture("#all").end().
						element().
						from(4f, 2f, 4f).
						to(12f, 4f, 12f).
						face(Direction.NORTH).
						uvs(10f, 4f, 14f, 5f).end().
						face(Direction.EAST).
						uvs(10f, 5f, 14f, 6f).end().
						face(Direction.SOUTH).
						uvs(10f, 6f, 14f, 7f).end().
						face(Direction.WEST).
						uvs(10f, 7f, 14f, 8f).end().
						face(Direction.UP).
						uvs(10f, 4f, 6f, 0f).end().
						face(Direction.DOWN).
						uvs(10f, 4f, 6f, 8f).end().
						texture("#all").end().
						element().
						from(11f, 3.25f, 5f).
						to(12f, 8.25f, 6f).
						rotation().
						angle(-22.5f).
						axis(Direction.Axis.Z).
						origin(11.5f, 4.25f, 5.5f).end().
						face(Direction.NORTH).
						uvs(11f, 12f, 11.5f, 14.5f).end().
						face(Direction.EAST).
						uvs(11.5f, 12f, 12f, 14.5f).end().
						face(Direction.SOUTH).
						uvs(12f, 12f, 12.5f, 14.5f).end().
						face(Direction.WEST).
						uvs(6f, 12.5f, 6.5f, 15f).end().
						face(Direction.UP).
						uvs(4f, 14f, 3.5f, 13.5f).end().
						face(Direction.DOWN).
						uvs(7f, 13.5f, 6.5f, 14f).end().
						texture("#all").end().
						element().
						from(7f, 1.5f, 4.75f).
						to(8f, 6.5f, 5.75f).
						rotation().
						angle(-45f).
						axis(Direction.Axis.X).
						origin(7.5f, 2.5f, 5.25f).end().
						face(Direction.NORTH).
						uvs(11.5f, 12f, 12f, 14.5f).end().
						face(Direction.EAST).
						uvs(12f, 12f, 12.5f, 14.5f).end().
						face(Direction.SOUTH).
						uvs(6f, 12.5f, 6.5f, 15f).end().
						face(Direction.WEST).
						uvs(11f, 12f, 11.5f, 14.5f).end().
						face(Direction.UP).
						uvs(4f, 14f, 3.5f, 13.5f).
						rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).
						uvs(7f, 13.5f, 6.5f, 14f).
						rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").end().
						element().
						from(10f, 2f, 10.5f).
						to(11f, 7f, 11.5f).
						rotation().
						angle(45f).
						axis(Direction.Axis.X).
						origin(10.5f, 3f, 11f).end().
						face(Direction.NORTH).
						uvs(11.5f, 12f, 12f, 14.5f).end().
						face(Direction.EAST).
						uvs(12f, 12f, 12.5f, 14.5f).end().
						face(Direction.SOUTH).
						uvs(6f, 12.5f, 6.5f, 15f).end().
						face(Direction.WEST).
						uvs(11f, 12f, 11.5f, 14.5f).end().
						face(Direction.UP).
						uvs(4f, 14f, 3.5f, 13.5f).
						rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).
						uvs(7f, 13.5f, 6.5f, 14f).
						rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
						end().
						element().
						from(4f, 3.25f, 9f).
						to(5f, 8.25f, 10f).
						rotation().
						angle(22.5f).
						axis(Direction.Axis.Z).
						origin(4.5f, 4.25f, 9.5f).end().
						face(Direction.NORTH).
						uvs(11f, 12f, 11.5f, 14.5f).end().
						face(Direction.EAST).
						uvs(11.5f, 12f, 12f, 14.5f).end().
						face(Direction.SOUTH).
						uvs(12f, 12f, 12.5f, 14.5f).end().
						face(Direction.WEST).
						uvs(6f, 12.5f, 6.5f, 15f).end().
						face(Direction.UP).
						uvs(4f, 14f, 3.5f, 13.5f).end().
						face(Direction.DOWN).
						uvs(7f, 13.5f, 6.5f, 14f).end().
						texture("#all").end().
						element().
						from(5.75f, 5.75f, 5.5f).
						to(7.75f, 7.75f, 7.5f).
						face(Direction.NORTH).
						uvs(6.5f, 12.5f, 7.5f, 13.5f).end().
						face(Direction.EAST).
						uvs(7.5f, 12.5f, 8.5f, 13.5f).end().
						face(Direction.SOUTH).
						uvs(8.5f, 12.5f, 9.5f, 13.5f).end().
						face(Direction.WEST).
						uvs(12.5f, 12f, 13.5f, 13f).end().
						face(Direction.UP).
						uvs(1f, 14f, 0f, 13f).end().
						face(Direction.DOWN).
						uvs(2f, 13f, 1f, 14f).end().
						texture("#all").end().
						element().
						from(11.25f, 1.5f, 4.75f).
						to(13.25f, 3.5f, 6.75f).
						face(Direction.NORTH).
						uvs(6.5f, 12.5f, 7.5f, 13.5f).end().
						face(Direction.EAST).
						uvs(7.5f, 12.5f, 8.5f, 13.5f).end().
						face(Direction.SOUTH).
						uvs(8.5f, 12.5f, 9.5f, 13.5f).end().
						face(Direction.WEST).
						uvs(12.5f, 12f, 13.5f, 13f).end().
						face(Direction.UP).
						uvs(1f, 14f, 0f, 13f).end().
						face(Direction.DOWN).
						uvs(2f, 13f, 1f, 14f).end().
						texture("#all").end().
						element().
								from(5.5f, 3.7f, 10.1f).
								to(9.5f, 6.7f, 11.1f).
								rotation().
								angle(45f).
								axis(Direction.Axis.X).
								origin(5.5f, 3.7f, 10.1f).end().
								face(Direction.NORTH).
								uvs(6f, 11f, 8f, 12.5f).end().
								face(Direction.EAST).
								uvs(9.5f, 12.5f, 10f, 14f).end().
								face(Direction.SOUTH).
								uvs(8f, 11f, 10f, 12.5f).end().
								face(Direction.WEST).
								uvs(12.5f, 13f, 13f, 14.5f).end().
								face(Direction.UP).
								uvs(14f, 11f, 12f, 10.5f).end().
								face(Direction.DOWN).
								uvs(4f, 13f, 2f, 13.5f).end().
						texture("#all").end().
						element().
						from(2.67178f, 1.7f, 3.22045f).
						to(6.67178f, 2.7f, 6.22045f).
						rotation().
						angle(45f).
						axis(Direction.Axis.Y).
						origin(4.67178f, 2.2f, 4.72045f).end().
						face(Direction.NORTH).
						uvs(4f, 13f, 2f, 13.5f).
						rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.EAST).
						uvs(9.5f, 12.5f, 10f, 14f).
						rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).
						uvs(14f, 11f, 12f, 10.5f).end().
						face(Direction.WEST).
						uvs(12.5f, 13f, 13f, 14.5f).
						rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.UP).
						uvs(6f, 11f, 8f, 12.5f).
						rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.DOWN).
						uvs(8f, 11f, 10f, 12.5f).end().
						texture("#all").end().
						element().
						from(13.25f, 1.75f, 9f).
						to(13.25f, 4.75f, 11f).
						rotation().
						angle(-22.5f).
						axis(Direction.Axis.Z).
						origin(13.25f, 3.25f, 10f).end().
						face(Direction.NORTH).
						uvs(0f, 0f, 0f, 1.5f).end().
						face(Direction.EAST).
						uvs(12f, 9f, 13f, 10.5f).end().
						face(Direction.SOUTH).
						uvs(0f, 0f, 0f, 1.5f).end().
						face(Direction.WEST).
						uvs(10f, 12f, 11f, 13.5f).end().
						face(Direction.UP).
						uvs(1f, 0f, 0f, 0f).
						rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).
						uvs(1f, 0f, 0f, 0f).
						rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").end().
						element().
						from(9.25f, 1.5f, 2.75f).
						to(11.25f, 3.5f, 2.75f).
						rotation().
						angle(-22.5f).
						axis(Direction.Axis.X).
						origin(10.25f, 2.5f, 2.75f).end().
						face(Direction.NORTH).
						uvs(4f, 13f, 5f, 14f).end().
						face(Direction.EAST).
						uvs(0f, 0f, 0f, 1f).end().
						face(Direction.SOUTH).
						uvs(5f, 13f, 6f, 14f).end().
						face(Direction.WEST).
						uvs(0f, 0f, 0f, 1f).end().
						face(Direction.UP).
						uvs(1f, 0f, 0f, 0f).end().
						face(Direction.DOWN).
						uvs(1f, 0f, 0f, 0f).end().
						texture("#all").end().
						element().
						from(7.25f, 1.75f, 12.75f).
						to(9.25f, 3.75f, 12.75f).
						rotation().
						angle(22.5f).
						axis(Direction.Axis.X).
						origin(8.25f, 2.75f, 12.75f).end().
						face(Direction.NORTH).
						uvs(4f, 13f, 5f, 14f).end().
						face(Direction.EAST).
						uvs(0f, 0f, 0f, 1f).end().
						face(Direction.SOUTH).
						uvs(5f, 13f, 6f, 14f).end().
						face(Direction.WEST).
						uvs(0f, 0f, 0f, 1f).end().
						face(Direction.UP).
						uvs(1f, 0f, 0f, 0f).end().
						face(Direction.DOWN).
						uvs(1f, 0f, 0f, 0f).end().
						texture("#all").end().
						element().
						from(2.5f, 1.75f, 10f).
						to(3.5f, 3.75f, 12f).
						rotation().
						angle(22.5f).
						axis(Direction.Axis.Z).
						origin(3f, 2.75f, 11f).end().
						face(Direction.NORTH).
						uvs(14f, 10.5f, 13f, 10f).
						rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.EAST).
						uvs(13f, 11f, 14f, 12f).
						rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.SOUTH).
						uvs(3.5f, 13.5f, 2.5f, 14f).
						rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).
						uvs(13f, 9f, 14f, 10f).
						rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).
						uvs(2f, 13.5f, 2.5f, 14.5f).end().
						face(Direction.DOWN).
						uvs(13f, 13f, 13.5f, 14f).
						rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").end().
						element().
						from(8f, 5.5f, 8f).
						to(10f, 6.5f, 10f).
						rotation().
						angle(22.5f).
						axis(Direction.Axis.Z).
						origin(9f, 6f, 9f).end().
						face(Direction.NORTH).
						uvs(14f, 10.5f, 13f, 10f).end().
						face(Direction.EAST).
						uvs(2f, 13.5f, 2.5f, 14.5f).
						rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.SOUTH).
						uvs(3.5f, 13.5f, 2.5f, 14f).
						rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.WEST).
						uvs(13f, 13f, 13.5f, 14f).
						rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).
						uvs(13f, 9f, 14f, 10f).end().
						face(Direction.DOWN).
						uvs(13f, 11f, 14f, 12f).
						rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").end().
						element().
						from(5f, 4f, 5f).
						to(11f, 6f, 11f).
						face(Direction.NORTH).
						uvs(10f, 11f, 13f, 12f).end().
						face(Direction.EAST).
						uvs(0f, 12f, 3f, 13f).end().
						face(Direction.SOUTH).
						uvs(3f, 12f, 6f, 13f).end().
						face(Direction.WEST).
						uvs(12f, 8f, 15f, 9f).end().
						face(Direction.UP).
						uvs(9f, 11f, 6f, 8f).end().
						face(Direction.DOWN).
						uvs(12f, 8f, 9f, 11f).end().
						texture("#all").end();
		
		ModelFile template1 = models().withExistingParent(blockPrefix(name(prop_1)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", textureLoc).
				texture("particle", textureLoc).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(2f, 0f, 2f).
				to(14f, 2f, 14f).
				face(Direction.NORTH).
				uvs(10f, 0f, 16f, 1f).end().
				face(Direction.EAST).
				uvs(10f, 1f, 16f, 2f).end().
				face(Direction.SOUTH).
				uvs(10f, 2f, 16f, 3f).end().
				face(Direction.WEST).
				uvs(10f, 3f, 16f, 4f).end().
				face(Direction.UP).
				uvs(6f, 6f, 0f, 0f).end().
				face(Direction.DOWN).
				uvs(6f, 6f, 0f, 12f).end().
				texture("#all").end().
				element().
				from(4f, 2f, 4f).
				to(12f, 4f, 12f).
				face(Direction.NORTH).
				uvs(10f, 4f, 14f, 5f).end().
				face(Direction.EAST).
				uvs(10f, 5f, 14f, 6f).end().
				face(Direction.SOUTH).
				uvs(10f, 6f, 14f, 7f).end().
				face(Direction.WEST).
				uvs(10f, 7f, 14f, 8f).end().
				face(Direction.UP).
				uvs(10f, 4f, 6f, 0f).end().
				face(Direction.DOWN).
				uvs(10f, 4f, 6f, 8f).end().
				texture("#all").end().
				element().
				from(8.75f, 0.75f, 2.25f).
				to(9.75f, 5.75f, 3.25f).
				rotation().
				angle(-45f).
				axis(Direction.Axis.Z).
				origin(9.25f, 1.75f, 2.75f).end().
				face(Direction.NORTH).
				uvs(11.5f, 12f, 12f, 14.5f).end().
				face(Direction.EAST).
				uvs(12f, 12f, 12.5f, 14.5f).end().
				face(Direction.SOUTH).
				uvs(6f, 12.5f, 6.5f, 15f).end().
				face(Direction.WEST).
				uvs(11f, 12f, 11.5f, 14.5f).end().
				face(Direction.UP).
				uvs(4f, 14f, 3.5f, 13.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(7f, 13.5f, 6.5f, 14f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(7f, 1f, 12.5f).
				to(8f, 6f, 13.5f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(7.5f, 2f, 13f).end().
				face(Direction.NORTH).
				uvs(11.5f, 12f, 12f, 14.5f).end().
				face(Direction.EAST).
				uvs(12f, 12f, 12.5f, 14.5f).end().
				face(Direction.SOUTH).
				uvs(6f, 12.5f, 6.5f, 15f).end().
				face(Direction.WEST).
				uvs(11f, 12f, 11.5f, 14.5f).end().
				face(Direction.UP).
				uvs(4f, 14f, 3.5f, 13.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(7f, 13.5f, 6.5f, 14f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(5f, 1.75f, 7f).
				to(6f, 6.75f, 8f).
				rotation().
				angle(45f).
				axis(Direction.Axis.Z).
				origin(5.5f, 2.75f, 7.5f).end().
				face(Direction.NORTH).
				uvs(11f, 12f, 11.5f, 14.5f).end().
				face(Direction.EAST).
				uvs(11.5f, 12f, 12f, 14.5f).end().
				face(Direction.SOUTH).
				uvs(12f, 12f, 12.5f, 14.5f).end().
				face(Direction.WEST).
				uvs(6f, 12.5f, 6.5f, 15f).end().
				face(Direction.UP).
				uvs(4f, 14f, 3.5f, 13.5f).end().
				face(Direction.DOWN).
				uvs(7f, 13.5f, 6.5f, 14f).end().
				texture("#all").end().
				element().
				from(5f, 3.75f, 5.25f).
				to(7f, 5.75f, 7.25f).
				rotation().
				angle(45f).
				axis(Direction.Axis.Y).
				origin(6f, 4.75f, 6.25f).end().
				face(Direction.NORTH).
				uvs(6.5f, 12.5f, 7.5f, 13.5f).end().
				face(Direction.EAST).
				uvs(7.5f, 12.5f, 8.5f, 13.5f).end().
				face(Direction.SOUTH).
				uvs(8.5f, 12.5f, 9.5f, 13.5f).end().
				face(Direction.WEST).
				uvs(12.5f, 12f, 13.5f, 13f).end().
				face(Direction.UP).
				uvs(1f, 14f, 0f, 13f).end().
				face(Direction.DOWN).
				uvs(2f, 13f, 1f, 14f).end().
				texture("#all").end().
				element().
				from(8.75f, 3.75f, 9.5f).
				to(10.75f, 5.75f, 11.5f).
				face(Direction.NORTH).
				uvs(6.5f, 12.5f, 7.5f, 13.5f).end().
				face(Direction.EAST).
				uvs(7.5f, 12.5f, 8.5f, 13.5f).end().
				face(Direction.SOUTH).
				uvs(8.5f, 12.5f, 9.5f, 13.5f).end().
				face(Direction.WEST).
				uvs(12.5f, 12f, 13.5f, 13f).end().
				face(Direction.UP).
				uvs(1f, 14f, 0f, 13f).end().
				face(Direction.DOWN).
				uvs(2f, 13f, 1f, 14f).end().
				texture("#all").end().
				element().
				from(8.26777f, 3.7f, 4.78934f).
				to(11.26777f, 4.7f, 8.78934f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(9.76777f, 4.2f, 6.78934f).end().
				face(Direction.NORTH).
				uvs(9.5f, 12.5f, 10f, 14f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.EAST).
				uvs(14f, 11f, 12f, 10.5f).end().
				face(Direction.SOUTH).
				uvs(12.5f, 13f, 13f, 14.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.WEST).
				uvs(4f, 13f, 2f, 13.5f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				face(Direction.UP).
				uvs(6f, 11f, 8f, 12.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(8f, 11f, 10f, 12.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(3.25f, 1f, 9f).
				to(3.25f, 4f, 11f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(3.25f, 2.5f, 10f).end().
				face(Direction.NORTH).
				uvs(0f, 0f, 0f, 1.5f).end().
				face(Direction.EAST).
				uvs(12f, 9f, 13f, 10.5f).end().
				face(Direction.SOUTH).
				uvs(0f, 0f, 0f, 1.5f).end().
				face(Direction.WEST).
				uvs(10f, 12f, 11f, 13.5f).end().
				face(Direction.UP).
				uvs(1f, 0f, 0f, 0f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(1f, 0f, 0f, 0f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(3.25f, 1.5f, 2.75f).
				to(5.25f, 3.5f, 2.75f).
				rotation().
				angle(-22.5f).
				axis(Direction.Axis.X).
				origin(4.25f, 2.5f, 2.75f).end().
				face(Direction.NORTH).
				uvs(4f, 13f, 5f, 14f).end().
				face(Direction.EAST).
				uvs(0f, 0f, 0f, 1f).end().
				face(Direction.SOUTH).
				uvs(5f, 13f, 6f, 14f).end().
				face(Direction.WEST).
				uvs(0f, 0f, 0f, 1f).end().
				face(Direction.UP).
				uvs(1f, 0f, 0f, 0f).end().
				face(Direction.DOWN).
				uvs(1f, 0f, 0f, 0f).end().
				texture("#all").end().
				element().
				from(10.25f, 1.5f, 12.75f).
				to(12.25f, 3.5f, 12.75f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.X).
				origin(11.25f, 2.5f, 12.75f).end().
				face(Direction.NORTH).
				uvs(4f, 13f, 5f, 14f).end().
				face(Direction.EAST).
				uvs(0f, 0f, 0f, 1f).end().
				face(Direction.SOUTH).
				uvs(5f, 13f, 6f, 14f).end().
				face(Direction.WEST).
				uvs(0f, 0f, 0f, 1f).end().
				face(Direction.UP).
				uvs(1f, 0f, 0f, 0f).end().
				face(Direction.DOWN).
				uvs(1f, 0f, 0f, 0f).end().
				texture("#all").end().
				element().
				from(13.25f, 1.75f, 3.25f).
				to(13.25f, 3.75f, 5.25f).
				rotation().
				angle(-22.5f).
				axis(Direction.Axis.Z).
				origin(13.25f, 2.75f, 4.25f).end().
				face(Direction.NORTH).
				uvs(0f, 0f, 0f, 1f).end().
				face(Direction.EAST).
				uvs(5f, 13f, 6f, 14f).end().
				face(Direction.SOUTH).
				uvs(0f, 0f, 0f, 1f).end().
				face(Direction.WEST).
				uvs(4f, 13f, 5f, 14f).end().
				face(Direction.UP).
				uvs(1f, 0f, 0f, 0f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(1f, 0f, 0f, 0f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(12.5f, 1.75f, 8.25f).
				to(13.5f, 3.75f, 10.25f).
				rotation().
				angle(-22.5f).
				axis(Direction.Axis.Z).
				origin(13f, 2.75f, 9.25f).end().
				face(Direction.NORTH).
				uvs(14f, 10.5f, 13f, 10f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.EAST).
				uvs(13f, 11f, 14f, 12f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.SOUTH).
				uvs(3.5f, 13.5f, 2.5f, 14f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.WEST).
				uvs(13f, 9f, 14f, 10f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.UP).
				uvs(2f, 13.5f, 2.5f, 14.5f).end().
				face(Direction.DOWN).
				uvs(13f, 13f, 13.5f, 14f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				texture("#all").end().
				element().
				from(5f, 3.5f, 9f).
				to(7f, 4.5f, 11f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(6f, 4f, 10f).end().
				face(Direction.NORTH).
				uvs(14f, 10.5f, 13f, 10f).end().
				face(Direction.EAST).
				uvs(2f, 13.5f, 2.5f, 14.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.SOUTH).
				uvs(3.5f, 13.5f, 2.5f, 14f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				face(Direction.WEST).
				uvs(13f, 13f, 13.5f, 14f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.UP).
				uvs(13f, 9f, 14f, 10f).end().
				face(Direction.DOWN).
				uvs(13f, 11f, 14f, 12f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				texture("#all").end();
		
		ModelFile template2 = models().withExistingParent(blockPrefix(name(prop_2)), mcLoc(blockPrefix("block"))).
				guiLight(BlockModel.GuiLight.SIDE).
				renderType(RenderType.translucent().name).
				texture("all", textureLoc).
				texture("particle", textureLoc).
				element().
				from(2, 0, 2).
				to(14, 2, 14).
				face(Direction.NORTH).
				uvs(10, 0, 16, 1).end().
				face(Direction.EAST).
				uvs(10, 1, 16, 2).end().
				face(Direction.SOUTH).
				uvs(10, 2, 16, 3).end().
				face(Direction.WEST).
				uvs(10, 3, 16, 4).end().
				face(Direction.UP).
				uvs(6, 6, 0, 0).end().
				face(Direction.DOWN).
				uvs(6, 6, 0, 12).end().
				texture("#all").end().
				element().
				from(12.5f, 2, 6.75f).
				to(13.5f, 3, 11.75f).
				face(Direction.NORTH).
				uvs(4, 14, 3.5f, 13.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.EAST).
				uvs(12, 12, 12.5f, 14.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.SOUTH).
				uvs(7, 13.5f, 6.5f, 14).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.WEST).
				uvs(11, 12, 11.5f, 14.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.UP).
				uvs(6, 12.5f, 6.5f, 15).end().
				face(Direction.DOWN).
				uvs(11.5f, 12, 12, 14.5f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				texture("#all").end().
				element().
				from(7, 2, 3.75f).
				to(12, 3, 4.75f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Y).
				origin(8, 2.5f, 4.25f).end().
				face(Direction.NORTH).
				uvs(11, 12, 11.5f, 14.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.EAST).
				uvs(4, 14, 3.5f, 13.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.SOUTH).
				uvs(12, 12, 12.5f, 14.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.WEST).
				uvs(7, 13.5f, 6.5f, 14).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.UP).
				uvs(6, 12.5f, 6.5f, 15).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(11.5f, 12, 12, 14.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(7.5f, 1, 9.5f).
				to(8.5f, 6, 10.5f).
				rotation().
				angle(45).
				axis(Direction.Axis.X).
				origin(8, 2, 10).end().
				face(Direction.NORTH).
				uvs(11.5f, 12, 12, 14.5f).end().
				face(Direction.EAST).
				uvs(12, 12, 12.5f, 14.5f).end().
				face(Direction.SOUTH).
				uvs(6, 12.5f, 6.5f, 15).end().
				face(Direction.WEST).
				uvs(11, 12, 11.5f, 14.5f).end().
				face(Direction.UP).
				uvs(4, 14, 3.5f, 13.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(7, 13.5f, 6.5f, 14).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(5, 1.75f, 7).
				to(6, 6.75f, 8).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(5.5f, 2.75f, 7.5f).end().
				face(Direction.NORTH).
				uvs(11, 12, 11.5f, 14.5f).end().
				face(Direction.EAST).
				uvs(11.5f, 12, 12, 14.5f).end().
				face(Direction.SOUTH).
				uvs(12, 12, 12.5f, 14.5f).end().
				face(Direction.WEST).
				uvs(6, 12.5f, 6.5f, 15).end().
				face(Direction.UP).
				uvs(4, 14, 3.5f, 13.5f).end().
				face(Direction.DOWN).
				uvs(7, 13.5f, 6.5f, 14).end().
				texture("#all").end().
				element().
				from(4, 1.75f, 3.25f).
				to(6, 3.75f, 5.25f).
				rotation().
				angle(45f).
				axis(Direction.Axis.Y).
				origin(5, 2.75f, 4.25f).end().
				face(Direction.NORTH).
				uvs(6.5f, 12.5f, 7.5f, 13.5f).end().
				face(Direction.EAST).
				uvs(7.5f, 12.5f, 8.5f, 13.5f).end().
				face(Direction.SOUTH).
				uvs(8.5f, 12.5f, 9.5f, 13.5f).end().
				face(Direction.WEST).
				uvs(12.5f, 12, 13.5f, 13).end().
				face(Direction.UP).
				uvs(1, 14, 0, 13).end().
				face(Direction.DOWN).
				uvs(2, 13, 1, 14).end().
				texture("#all").end().
				element().
				from(4.75f, 1.75f, 9.5f).
				to(6.75f, 3.75f, 11.5f).
				face(Direction.NORTH).
				uvs(6.5f, 12.5f, 7.5f, 13.5f).end().
				face(Direction.EAST).
				uvs(7.5f, 12.5f, 8.5f, 13.5f).end().
				face(Direction.SOUTH).
				uvs(8.5f, 12.5f, 9.5f, 13.5f).end().
				face(Direction.WEST).
				uvs(12.5f, 12, 13.5f, 13).end().
				face(Direction.UP).
				uvs(1, 14, 0, 13).end().
				face(Direction.DOWN).
				uvs(2, 13, 1, 14).end().
				texture("#all").end().
				element().
				from(10.75f, 1.75f, 4.5f).
				to(12.75f, 3.75f, 6.5f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Y).
				origin(10.75f, 1.75f, 4.5f).end().
				face(Direction.NORTH).
				uvs(6.5f, 12.5f, 7.5f, 13.5f).end().
				face(Direction.EAST).
				uvs(7.5f, 12.5f, 8.5f, 13.5f).end().
				face(Direction.SOUTH).
				uvs(8.5f, 12.5f, 9.5f, 13.5f).end().
				face(Direction.WEST).
				uvs(12.5f, 12, 13.5f, 13).end().
				face(Direction.UP).
				uvs(1, 14, 0, 13).end().
				face(Direction.DOWN).
				uvs(2, 13, 1, 14).end().
				texture("#all").end().
				element().
				from(8.76777f, 2.45f, 8.28934f).
				to(12.76777f, 3.45f, 11.28934f).
				rotation().
				angle(45).
				axis(Direction.Axis.Z).
				origin(10.76777f, 2.95f, 9.78934f).end().
				face(Direction.NORTH).
				uvs(14, 11, 12, 10.5f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				face(Direction.EAST).
				uvs(9.5f, 12.5f, 10, 14).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.SOUTH).
				uvs(4, 13, 2, 13.5f).end().
				face(Direction.WEST).
				uvs(12.5f, 13, 13, 14.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.UP).
				uvs(8, 11, 10, 12.5f).end().
				face(Direction.SOUTH).
				uvs(6, 11, 8, 12.5f).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				texture("#all").end().
				element().
				from(3.017f, 0.45f, 12.0393f).
				to(7.017f, 3.45f, 13.039f).
				rotation().
				angle(45f).
				axis(Direction.Axis.X).
				origin(5.017f, 1.95f, 12.539f).end().
				face(Direction.NORTH).
				uvs(6, 11, 8, 12.5f).end().
				face(Direction.EAST).
				uvs(9.5f, 12.5f, 10, 14).end().
				face(Direction.SOUTH).
				uvs(8, 11, 10, 12.5f).end().
				face(Direction.WEST).
				uvs(12.5f, 13, 13, 14.5f).end().
				face(Direction.UP).
				uvs(14, 11, 12, 10.5f).end().
				face(Direction.DOWN).
				uvs(4, 13, 2, 13.5f).end().
				texture("#all").end().
				element().
				from(10.25f, 1.5f, 12.75f).
				to(12.25f, 3.5f, 12.75f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.X).
				origin(11.25f, 2.5f, 12.75f).end().
				face(Direction.NORTH).
				uvs(4, 13, 5, 14).end().
				face(Direction.EAST).
				uvs(0, 0, 0, 1).end().
				face(Direction.SOUTH).
				uvs(5, 13, 6, 14).end().
				face(Direction.WEST).
				uvs(0, 0, 0, 1).end().
				face(Direction.UP).
				uvs(1, 0, 0, 0).end().
				face(Direction.DOWN).
				uvs(1, 0, 0, 0).end().
				texture("#all").end().
				element().
				from(13, 1.5f, 2).
				to(13, 3.5f, 2).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(13, 2.5f, 3).end().
				face(Direction.NORTH).
				uvs(0, 0, 0, 1).end().
				face(Direction.EAST).
				uvs(5, 13, 6, 14).end().
				face(Direction.SOUTH).
				uvs(0, 0, 0, 1).end().
				face(Direction.WEST).
				uvs(4, 13, 5, 14).end().
				face(Direction.UP).
				uvs(1, 0, 0, 0).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(1, 0, 0, 0).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(7f, 1.75f, 5.75f).
				to(9f, 3.75f, 6.75f).
				rotation().
				angle(-22.5f).
				axis(Direction.Axis.X).
				origin(8f, 2.75f, 6.25f).end().
				face(Direction.NORTH).
				uvs(13, 11, 14, 12).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.EAST).
				uvs(3.5f, 13.5f, 2.5f, 14).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.SOUTH).
				uvs(13, 9, 14, 10).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.WEST).
				uvs(14, 10.5f, 13, 10).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.UP).
				uvs(2, 13.5f, 2.5f, 14.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(13, 13, 13.5f, 14).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(2.5f, 1.5f, 7.75f).
				to(4.5f, 2.5f, 9.75f).
				rotation().
				angle(22.5f).
				axis(Direction.Axis.Z).
				origin(3.5f, 2, 8.75f).end().
				face(Direction.NORTH).
				uvs(14, 10.5f, 13, 10).end().
				face(Direction.EAST).
				uvs(2, 13.5f, 2.5f, 14.5f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				face(Direction.SOUTH).
				uvs(3.5f, 13.5f, 2.5f, 14).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				face(Direction.WEST).
				uvs(13, 13, 13.5f, 14).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.UP).
				uvs(13f, 9, 14, 10).end().
				face(Direction.DOWN).
				uvs(13, 11, 14, 12).
				rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
				texture("#all").end();
		
		registerModels(prop_0, template0);
		registerModels(prop_1, template1);
		registerModels(prop_2, template2);
	}
	
	private void createMultiblockMorpherModel()
	{
		MultiblockMorpherBlock block = Registration.BlockReg.MULTIBLOCK_MORPHER.get();
		
		//Water used coz there is no particle only block in 1.21.1
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("water"))).
				texture("particle", blockPrefix("morpher") + "/0");
		
		//Item model is missing here, coz using custom renderer
		getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));
	}
	
	private void createMultiblockChrysalisModel()
	{
		MultiblockChrysalisBlock block = Registration.BlockReg.MULTIBLOCK_CHRYSALIS.get();
		
		//Water used coz there is no particle only block in 1.21.1
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("water"))).
				texture("particle", blockPrefix("chrysalis") + "/0");
		
		//Item model is missing here, coz using custom renderer
		getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));
	}
	
	private void createMultiblockTurretModel()
	{
		MultiblockTurretBlock block = Registration.BlockReg.MULTIBLOCK_TURRET.get();
		
		//Water used coz there is no particle only block in 1.21.1
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("water"))).
				texture("particle", blockPrefix("turret") + "/0");
		
		//Item model is missing here, coz using custom renderer
		getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));
	}
	
	private void createMultiblockChamberModel()
	{
		MultiblockChamberBlock block = Registration.BlockReg.MULTIBLOCK_CHAMBER.get();
		
		//Water used coz there is no particle only block in 1.21.1
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("water"))).
				texture("particle", blockPrefix("chamber") + "/0");
		
		//Item model is missing here, coz using custom renderer
		getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));
	}
	
	private void createMultiblockFluidStorage()
	{
		MultiblockFluidStorageBlock block = Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.get();
		ResourceLocation blockTexture = blockTexture(block);
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.cutout().name).
				guiLight(BlockModel.GuiLight.SIDE).
				texture("side", blockTexture.withSuffix("/side_transparent")).
				texture("up", blockTexture.withSuffix("/top")).
				texture("down", blockTexture.withSuffix("/top")).
				texture("particle", blockTexture.withSuffix("/side_transparent")).
				texture("content", blockTexture.withSuffix("/side")).
				element().
				from(0, 0, 0).
				to(16, 16, 16).
				allFaces((direction, faceBuilder) ->
				{
					faceBuilder.uvs(0, 0, 16, 16);
					if (direction.getAxis().isHorizontal())
						faceBuilder.texture("#side");
					else if (direction == Direction.UP)
						faceBuilder.texture("#up");
					else
						faceBuilder.texture("#down");
				}).
				end();
		
		registerModels(block, model);
	}
	
	private void createSqueezerModel()
	{
		BioSqueezerBlock block = Registration.BlockReg.SQUEEZER.get();
		
		ResourceLocation blockTexture = blockTexture(block).withSuffix("/0");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(0, 0, 0).
				to(16, 1, 16).
				face(Direction.NORTH).
				uvs(2, 2, 4, 2.125f).end().
				face(Direction.EAST).
				uvs(0, 2, 2, 2.125f).end().
				face(Direction.SOUTH).
				uvs(6, 2, 8, 2.125f).end().
				face(Direction.WEST).
				uvs(4, 2, 6, 2.125f).end().
				face(Direction.UP).
				uvs(4, 2, 2, 0).end().
				face(Direction.DOWN).
				uvs(6, 0, 4, 2).end().
				texture("#all").end().
				element().
				from(2, 1, 2).
				to(14, 6, 14).
				rotation().
				angle(-45).
				axis(Direction.Axis.Y).
				origin(8, 1, 8).end().
				face(Direction.NORTH).
				uvs(1.5f, 3.625f, 3, 4.25f).end().
				face(Direction.EAST).
				uvs(0, 3.625f, 1.5f, 4.25f).end().
				face(Direction.SOUTH).
				uvs(4.5f, 3.625f, 6, 4.25f).end().
				face(Direction.WEST).
				uvs(3, 3.625f, 4.5f, 4.25f).end().
				face(Direction.UP).
				uvs(3, 3.625f, 1.5f, 2.125f).end().
				face(Direction.DOWN).
				uvs(4.5f, 2.125f, 3, 3.625f).end().
				texture("#all").end().
				element().
				from(1, 1, 1).
				to(3, 5, 3).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(2, 1, 2).end().
				face(Direction.NORTH).
				uvs(6.25f, 3.5f, 6.5f, 4).end().
				face(Direction.EAST).
				uvs(6, 3.5f, 6.25f, 4).end().
				face(Direction.SOUTH).
				uvs(6.75f, 3.5f, 7, 4).end().
				face(Direction.WEST).
				uvs(6.5f, 3.5f, 6.75f, 4).end().
				face(Direction.UP).
				uvs(6.5f, 3.5f, 6.25f, 3.25f).end().
				face(Direction.DOWN).
				uvs(6.75f, 3.25f, 6.5f, 3.5f).end().
				texture("#all").end().
				element().
				from(1.5f, 4, 1.5f).
				to(2.5f, 11, 2.5f).
				face(Direction.NORTH).
				uvs(5.125f, 6.25f, 5.25f, 7.125f).end().
				face(Direction.EAST).
				uvs(5, 6.25f, 5.125f, 7.125f).end().
				face(Direction.SOUTH).
				uvs(5.375f, 6.25f, 5.5f, 7.125f).end().
				face(Direction.WEST).
				uvs(5.25f, 6.25f, 5.375f, 7.125f).end().
				face(Direction.UP).
				uvs(5.25f, 6.25f, 5.125f, 6.125f).end().
				face(Direction.DOWN).
				uvs(5.375f, 6.125f, 5.25f, 6.25f).end().
				texture("#all").end().
				element().
				from(1.05619f, 10.14395f, 1.03723f).
				to(3.05619f, 12.14395f, 3.03723f).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(2.05619f, 11.14395f, 2.03723f).end().
				face(Direction.NORTH).
				uvs(5.75f, 6.375f, 6, 6.625f).end().
				face(Direction.EAST).
				uvs(5.5f, 6.375f, 5.75f, 6.625f).end().
				face(Direction.SOUTH).
				uvs(6.25f, 6.375f, 6.5f, 6.625f).end().
				face(Direction.WEST).
				uvs(6, 6.375f, 6.25f, 6.625f).end().
				face(Direction.UP).
				uvs(6, 6.375f, 5.75f, 6.125f).end().
				face(Direction.DOWN).
				uvs(6.25f, 6.125f, 6, 6.375f).end().
				texture("#all").end().
				element().
				from(1.55619f, 12.14395f, 1.53723f).
				to(2.55619f, 16.14395f, 2.53723f).
				face(Direction.NORTH).
				uvs(2.125f, 6.875f, 2.25f, 7.375f).end().
				face(Direction.EAST).
				uvs(2, 6.875f, 2.125f, 7.375f).end().
				face(Direction.SOUTH).
				uvs(2.375f, 6.875f, 2.5f, 7.375f).end().
				face(Direction.WEST).
				uvs(2.25f, 6.875f, 2.375f, 7.375f).end().
				face(Direction.UP).
				uvs(2.25f, 6.875f, 2.125f, 6.75f).end().
				face(Direction.DOWN).
				uvs(2.375f, 6.75f, 2.25f, 6.875f).end().
				texture("#all").end().
				element().
				from(12.75762f, 0.93372f, 12.76052f).
				to(14.75762f, 4.93372f, 14.76052f).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(13.75762f, 0.93372f, 13.76052f).end().
				face(Direction.NORTH).
				uvs(6.25f, 4.25f, 6.5f, 4.75f).end().
				face(Direction.EAST).
				uvs(6, 4.25f, 6.25f, 4.75f).end().
				face(Direction.SOUTH).
				uvs(6.75f, 4.25f, 7, 4.75f).end().
				face(Direction.WEST).
				uvs(6.5f, 4.25f, 6.75f, 4.75f).end().
				face(Direction.UP).
				uvs(6.5f, 4.25f, 6.25f, 4).end().
				face(Direction.DOWN).
				uvs(6.75f, 4, 6.5f, 4.25f).end().
				texture("#all").end().
				element().
				from(13.25762f, 3.93372f, 13.26052f).
				to(14.25762f, 10.93372f, 14.26052f).
				face(Direction.NORTH).
				uvs(6.625f, 5.625f, 6.75f, 6.5f).end().
				face(Direction.EAST).
				uvs(6.5f, 5.625f, 6.625f, 6.5f).end().
				face(Direction.SOUTH).
				uvs(6.875f, 5.625f, 7, 6.5f).end().
				face(Direction.WEST).
				uvs(6.75f, 5.625f, 6.875f, 6.5f).end().
				face(Direction.UP).
				uvs(6.75f, 5.625f, 6.625f, 5.5f).end().
				face(Direction.DOWN).
				uvs(6.875f, 5.5f, 6.75f, 5.625f).end().
				texture("#all").end().
				element().
				from(12.81381f, 10.07767f, 12.79775f).
				to(14.81381f, 12.07767f, 14.79775f).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(13.81381f, 10.07767f, 13.79775f).end().
				face(Direction.NORTH).
				uvs(6.75f, 6.75f, 7, 7).end().
				face(Direction.EAST).
				uvs(6.5f, 6.75f, 6.75f, 7).end().
				face(Direction.SOUTH).
				uvs(7.25f, 6.75f, 7.5f, 7).end().
				face(Direction.WEST).
				uvs(7, 6.75f, 7.25f, 7).end().
				face(Direction.UP).
				uvs(7, 6.75f, 6.5f, 6.5f).end().
				face(Direction.DOWN).
				uvs(7.25f, 6.5f, 7, 6.75f).end().
				texture("#all").end().
				element().
				from(13.31381f, 12.07767f, 13.29775f).
				to(14.31381f, 16.07767f, 14.29775f).
				face(Direction.NORTH).
				uvs(2.625f, 6.875f, 2.75f, 7.375f).end().
				face(Direction.EAST).
				uvs(2.5f, 6.875f, 2.625f, 7.375f).end().
				face(Direction.SOUTH).
				uvs(2.875f, 6.875f, 3, 7.375f).end().
				face(Direction.WEST).
				uvs(2.75f, 6.875f, 2.875f, 7.375f).end().
				face(Direction.UP).
				uvs(2.75f, 6.875f, 2.625f, 6.75f).end().
				face(Direction.DOWN).
				uvs(2.875f, 6.75f, 2.75f, 6.875f).end().
				texture("#all").end().
				element().
				from(1, 1, 12).
				to(3, 5, 14).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(2, 1, 13).end().
				face(Direction.NORTH).
				uvs(6.25f, 5, 6.5f, 5.5f).end().
				face(Direction.EAST).
				uvs(6, 5, 6.25f, 5.5f).end().
				face(Direction.SOUTH).
				uvs(6.75f, 5, 7, 5.5f).end().
				face(Direction.WEST).
				uvs(6.5f, 5, 6.75f, 5.5f).end().
				face(Direction.UP).
				uvs(6.5f, 5, 6.25f, 4.75f).end().
				face(Direction.DOWN).
				uvs(6.75f, 4.75f, 6.5f, 5).end().
				texture("#all").end().
				element().
				from(1.5f, 4, 12.5f).
				to(2.5f, 11, 13.5f).
				face(Direction.NORTH).
				uvs(5.625f, 6.75f, 5.75f, 7.625f).end().
				face(Direction.EAST).
				uvs(5.5f, 6.75f, 5.625f, 7.625f).end().
				face(Direction.SOUTH).
				uvs(5.875f, 6.75f, 6, 7.625f).end().
				face(Direction.WEST).
				uvs(5.75f, 6.75f, 5.875f, 7.625f).end().
				face(Direction.UP).
				uvs(5.75f, 6.75f, 5.625f, 6.625f).end().
				face(Direction.DOWN).
				uvs(5.875f, 6.625f, 5.75f, 6.75f).end().
				texture("#all").end().
				element().
				from(1.05619f, 10.14395f, 12.03723f).
				to(3.05619f, 12.14395f, 14.03723f).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(2.05619f, 10.14395f, 13.03723f).end().
				face(Direction.NORTH).
				uvs(0.25f, 7, 0.5f, 7.25f).end().
				face(Direction.EAST).
				uvs(0, 7, 0.25f, 7.25f).end().
				face(Direction.SOUTH).
				uvs(0.75f, 7, 1, 7.25f).end().
				face(Direction.WEST).
				uvs(0.5f, 7, 0.75f, 7.25f).end().
				face(Direction.UP).
				uvs(0.5f, 7, 0.25f, 6.75f).end().
				face(Direction.DOWN).
				uvs(0.75f, 6.75f, 0.5f, 7).end().
				texture("#all").end().
				element().
				from(1.55619f, 12.14395f, 12.53723f).
				to(2.55619f, 16.14395f, 13.53723f).
				face(Direction.NORTH).
				uvs(3.125f, 6.875f, 3.25f, 7.375f).end().
				face(Direction.EAST).
				uvs(3, 6.875f, 3.125f, 7.375f).end().
				face(Direction.SOUTH).
				uvs(3.375f, 6.875f, 3.5f, 7.375f).end().
				face(Direction.WEST).
				uvs(3.25f, 6.875f, 3.375f, 7.375f).end().
				face(Direction.UP).
				uvs(3.25f, 6.875f, 3.125f, 6.75f).end().
				face(Direction.DOWN).
				uvs(3.375f, 6.75f, 3.25f, 6.875f).end().
				texture("#all").end().
				element().
				from(12, 1, 1).
				to(14, 5, 3).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(13, 1, 2).end().
				face(Direction.NORTH).
				uvs(4.25f, 6.375f, 4.5f, 6.875f).end().
				face(Direction.EAST).
				uvs(4, 6.375f, 4.25f, 6.875f).end().
				face(Direction.SOUTH).
				uvs(4.75f, 6.375f, 5, 6.875f).end().
				face(Direction.WEST).
				uvs(4.5f, 6.375f, 4.75f, 6.875f).end().
				face(Direction.UP).
				uvs(4.5f, 6.375f, 4.25f, 6.125f).end().
				face(Direction.DOWN).
				uvs(4.75f, 6.125f, 4.5f, 6.375f).end().
				texture("#all").end().
				element().
				from(12.5f, 4, 1.5f).
				to(13.5f, 11, 2.5f).
				face(Direction.NORTH).
				uvs(6.125f, 6.75f, 6.25f, 7.625f).end().
				face(Direction.EAST).
				uvs(6, 6.75f, 6.125f, 7.625f).end().
				face(Direction.SOUTH).
				uvs(6.375f, 6.75f, 6.5f, 7.625f).end().
				face(Direction.WEST).
				uvs(6.25f, 6.75f, 6.375f, 7.625f).end().
				face(Direction.UP).
				uvs(6.25f, 6.75f, 6.125f, 6.625f).end().
				face(Direction.DOWN).
				uvs(6.375f, 6.625f, 6.25f, 6.75f).end().
				texture("#all").end().
				element().
				from(12.05619f, 10.14395f, 1.03723f).
				to(14.05619f, 12.14395f, 3.03723f).
				rotation().
				angle(45).
				axis(Direction.Axis.Y).
				origin(13.05619f, 10.14395f, 2.03723f).end().
				face(Direction.NORTH).
				uvs(1.25f, 7, 1.5f, 7.25f).end().
				face(Direction.EAST).
				uvs(1, 7, 1.25f, 7.25f).end().
				face(Direction.SOUTH).
				uvs(1.75f, 7, 2, 7.25f).end().
				face(Direction.WEST).
				uvs(1.5f, 7, 1.75f, 7.25f).end().
				face(Direction.UP).
				uvs(1.5f, 7, 1.25f, 6.75f).end().
				face(Direction.DOWN).
				uvs(1.75f, 6.75f, 1.5f, 7).end().
				texture("#all").end().
				element().
				from(12.55619f, 12.14395f, 1.53723f).
				to(13.55619f, 16.14395f, 2.53723f).
				face(Direction.NORTH).
				uvs(3.625f, 6.875f, 3.75f, 7.375f).end().
				face(Direction.EAST).
				uvs(3.5f, 6.875f, 3.625f, 7.375f).end().
				face(Direction.SOUTH).
				uvs(3.875f, 6.875f, 4, 7.375f).end().
				face(Direction.WEST).
				uvs(3.75f, 6.875f, 3.875f, 7.375f).end().
				face(Direction.UP).
				uvs(3.75f, 6.875f, 3.625f, 6.75f).end().
				face(Direction.DOWN).
				uvs(3.875f, 6.75f, 3.75f, 6.875f).end().
				texture("#all").end().
				element().
				from(3, 7, 3).
				to(13, 9, 5).
				face(Direction.NORTH).
				uvs(0, 5.5f, 1.25f, 5.75f).end().
				face(Direction.EAST).
				uvs(2.75f, 5.5f, 3, 5.75f).end().
				face(Direction.SOUTH).
				uvs(1.5f, 5.5f, 2.75f, 5.75f).end().
				face(Direction.WEST).
				uvs(1.25f, 5.5f, 1.5f, 5.75f).end().
				face(Direction.UP).
				uvs(1.5f, 5.5f, 1.25f, 4.25f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(1.75f, 4.25f, 1.5f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(3, 7, 5).
				to(5, 9, 11).
				face(Direction.NORTH).
				uvs(0.75f, 6.5f, 1, 6.75f).end().
				face(Direction.EAST).
				uvs(0, 6.5f, 0.75f, 6.75f).end().
				face(Direction.SOUTH).
				uvs(1.75f, 6.5f, 2, 6.75f).end().
				face(Direction.WEST).
				uvs(1, 6.5f, 1.75f, 6.75f).end().
				face(Direction.UP).
				uvs(1, 6.5f, 0.75f, 5.75f).end().
				face(Direction.DOWN).
				uvs(1.25f, 5.75f, 1, 6.5f).end().
				texture("#all").end().
				element().
				from(11, 7, 5).
				to(13, 9, 11).
				face(Direction.NORTH).
				uvs(2.75f, 6.5f, 3, 6.75f).end().
				face(Direction.EAST).
				uvs(2, 6.5f, 2.75f, 6.75f).end().
				face(Direction.SOUTH).
				uvs(3.75f, 6.5f, 4, 6.75f).end().
				face(Direction.WEST).
				uvs(3, 6.5f, 3.75f, 6.75f).end().
				face(Direction.UP).
				uvs(3, 6.5f, 2.75f, 5.75f).end().
				face(Direction.DOWN).
				uvs(3.25f, 5.75f, 3, 6.5f).end().
				texture("#all").end().
				element().
				from(3, 7, 11).
				to(13, 9, 13).
				face(Direction.NORTH).
				uvs(3, 5.5f, 4.25f, 5.75f).end().
				face(Direction.EAST).
				uvs(5.75f, 5.5f, 6, 5.75f).end().
				face(Direction.SOUTH).
				uvs(4.5f, 5.5f, 5.75f, 5.75f).end().
				face(Direction.WEST).
				uvs(4.25f, 5.5f, 4.5f, 5.75f).end().
				face(Direction.UP).
				uvs(4.5f, 5.5f, 4.25f, 4.25f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(4.75f, 4.25f, 4.5f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(4, 6, 10).
				to(12, 7, 12).
				face(Direction.NORTH).
				uvs(4.25f, 6, 5.25f, 6.125f).end().
				face(Direction.EAST).
				uvs(4, 6, 4.25f, 6.125f).end().
				face(Direction.SOUTH).
				uvs(5.5f, 6, 6.5f, 6.125f).end().
				face(Direction.WEST).
				uvs(5.25f, 6, 5.5f, 6.125f).end().
				face(Direction.UP).
				uvs(5.25f, 6, 4.25f, 5.75f).end().
				face(Direction.DOWN).
				uvs(6.25f, 5.75f, 5.25f, 6).end().
				texture("#all").end().
				element().
				from(4, 6, 4).
				to(12, 7, 6).
				face(Direction.NORTH).
				uvs(6.25f, 2.375f, 7.25f, 2.5f).end().
				face(Direction.EAST).
				uvs(6, 2.375f, 6.25f, 2.5f).end().
				face(Direction.SOUTH).
				uvs(7.5f, 2.375f, 8.5f, 2.5f).end().
				face(Direction.WEST).
				uvs(7.25f, 2.375f, 7.5f, 2.5f).end().
				face(Direction.UP).
				uvs(7.25f, 2.375f, 6.25f, 2.125f).end().
				face(Direction.DOWN).
				uvs(8.25f, 2.125f, 7.25f, 2.375f).end().
				texture("#all").end().
				element().
				from(10, 6, 6).
				to(12, 7, 10).
				face(Direction.NORTH).
				uvs(6, 2.75f, 6.25f, 2.875f).end().
				face(Direction.EAST).
				uvs(7, 2.75f, 7.5f, 2.875f).end().
				face(Direction.SOUTH).
				uvs(6.75f, 2.75f, 7, 2.875f).end().
				face(Direction.WEST).
				uvs(6.25f, 2.75f, 6.75f, 2.875f).end().
				face(Direction.UP).
				uvs(6.75f, 2.75f, 6.25f, 2.5f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(7.25f, 2.5f, 6.75f, 2.75f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(4, 6, 6).
				to(6, 7, 10).
				face(Direction.NORTH).
				uvs(6, 3.125f, 6.25f, 3.25f).end().
				face(Direction.EAST).
				uvs(7, 3.125f, 7.5f, 3.25f).end().
				face(Direction.SOUTH).
				uvs(6.75f, 3.125f, 7, 3.25f).end().
				face(Direction.WEST).
				uvs(6.25f, 3.125f, 6.75f, 3.25f).end().
				face(Direction.UP).
				uvs(6.75f, 3.125f, 6.25f, 2.875f).
				rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).
				uvs(7.25f, 2.875f, 6.75f, 3.125f).
				rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end();

		horizontalBlock(block, model);
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
	}
	
	private void createForgeModel()
	{
		BioForgeBlock block = Registration.BlockReg.FORGE.get();
		ResourceLocation blockTexture = blockTexture(block).withSuffix("/0");
		
		/*FIXME: Добавить вторую модель*/
		
		ModelFile singleModel = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(6,6,6).
				to(10, 9, 7).
				face(Direction.NORTH).uvs(7.5f, 8.125f, 8, 8.5f).end().
				face(Direction.EAST).uvs(7, 8.125f, 7.5f, 8.5f).end().
				face(Direction.SOUTH).uvs(8.5f, 8.125f, 9, 8.5f).end().
				face(Direction.WEST).uvs(8, 8.125f, 8.5f, 8.5f).end().
				face(Direction.UP).uvs(8, 8.125f, 7.5f, 7.625f).end().
				face(Direction.DOWN).uvs(8.5f, 7.625f, 8, 8.125f).end().
				texture("#all").end().
				element().
				from(6, 12, 5).
				to(10, 15, 8).
				face(Direction.NORTH).uvs(8.375f, 1.125f, 8.875f, 1.5f).end().
				face(Direction.EAST).uvs(8, 1.125f, 8.375f, 1.5f).end().
				face(Direction.SOUTH).uvs(9.25f, 1.125f, 9.75f, 1.5f).end().
				face(Direction.WEST).uvs(8.875f, 1.125f, 9.25f, 1.5f).end().
				face(Direction.UP).uvs(8.875f, 1.125f, 8.375f, 0.75f).end().
				face(Direction.DOWN).uvs(9.375f, 0.75f, 8.875f, 1.125f).end().
				texture("#all").end().
				element().
				from(7, 12, 8).
				to(9, 16, 10).
				rotation().angle(45).axis(Direction.Axis.X).origin(5.5f, 13.5f, 9).end().
				face(Direction.NORTH).uvs(9.25f, 7, 9.5f, 7.5f).end().
				face(Direction.EAST).uvs(9, 7, 9.25f, 7.5f).end().
				face(Direction.SOUTH).uvs(9.75f, 7, 10, 7.5f).end().
				face(Direction.WEST).uvs(9.5f, 7, 9.75f, 7.5f).end().
				face(Direction.UP).uvs(9.5f, 7, 9.25f, 6.75f).end().
				face(Direction.DOWN).uvs(9.75f, 6.75f, 9.5f, 7).end().
				texture("#all").end().
				element().
				from(6.9f, 9.5f, 12).
				to(8.9f, 14.5f, 14).
				rotation().angle(-45).axis(Direction.Axis.X).origin(5.4f, 12, 13).end().
				face(Direction.NORTH).uvs(3.25f, 8.875f, 3.5f, 9.5f).end().
				face(Direction.EAST).uvs(3, 8.875f, 3.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(3.75f, 8.875f, 4, 9.5f).end().
				face(Direction.WEST).uvs(3.5f, 8.875f, 3.75f, 9.5f).end().
				face(Direction.UP).uvs(3.5f, 8.875f, 3.25f, 8.625f).end().
				face(Direction.DOWN).uvs(3.75f, 8.625f, 3.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(7, 5.5f, 13.5f).
				to(9, 10.5f, 15.5f).
				face(Direction.NORTH).uvs(4.25f, 8.875f, 4.5f, 9.5f).end().
				face(Direction.EAST).uvs(4, 8.875f, 4.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(4.75f, 8.875f, 5, 9.5f).end().
				face(Direction.WEST).uvs(4.5f, 8.875f, 4.75f, 9.5f).end().
				face(Direction.UP).uvs(4.5f, 8.875f, 4.25f, 8.625f).end().
				face(Direction.DOWN).uvs(4.75f, 8.625f, 4.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(7, 1.5f, 12).
				to(9, 6.5f, 14).
				rotation().angle(45).axis(Direction.Axis.X).origin(5.5f, 4, 13).end().
				face(Direction.NORTH).uvs(5.25f, 8.875f, 5.5f, 9.5f).end().
				face(Direction.EAST).uvs(5, 8.875f, 5.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(5.75f, 8.875f, 6, 9.5f).end().
				face(Direction.WEST).uvs(5.5f, 8.875f, 5.75f, 9.5f).end().
				face(Direction.UP).uvs(5.5f, 8.875f, 5.25f, 8.625f).end().
				face(Direction.DOWN).uvs(5.75f, 8.625f, 5.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(-0.5f, 11.5f, 0.9f).
				to(0.5f, 15.5f, 13.9f).
				rotation().angle(-22.5f).axis(Direction.Axis.Z).origin(0, 12.5f, 7.4f).end().
				face(Direction.NORTH).uvs(8.625f, 3.75f, 8.75f, 4.25f).end().
				face(Direction.EAST).uvs(7, 3.75f, 8.625f, 4.25f).end().
				face(Direction.SOUTH).uvs(10.375f, 3.75f, 10.5f, 4.25f).end().
				face(Direction.WEST).uvs(8.75f, 3.75f, 10.375f, 4.25f).end().
				face(Direction.UP).uvs(8.75f, 3.75f, 8.625f, 2.125f).end().
				face(Direction.DOWN).uvs(8.875f, 2.125f, 8.75f, 3.75f).end().
				texture("#all").end().
				element().
				from(1, 2, 1).
				to(2, 12, 14).
				rotation().angle(22.5f).axis(Direction.Axis.Z).origin(1.5f, 7, 7.5f).end().
				face(Direction.NORTH).uvs(5.125f, 7.375f, 5.25f, 8.625f).end().
				face(Direction.EAST).uvs(3.5f, 7.375f, 5.125f, 8.625f).end().
				face(Direction.SOUTH).uvs(6.875f, 7.375f, 7, 8.625f).end().
				face(Direction.WEST).uvs(5.25f, 7.375f, 6.875f, 8.625f).end().
				face(Direction.UP).uvs(5.25f, 7.375f, 5.125f, 5.75f).end().
				face(Direction.DOWN).uvs(5.375f, 5.75f, 5.25f, 7.375f).end().
				texture("#all").end().
				element().
				from(15.5f, 11.5f, 0.9f).
				to(16.5f, 15.5f, 13.9f).
				rotation().angle(22.5f).axis(Direction.Axis.Z).origin(16, 12.5f, 7.4f).end().
				face(Direction.NORTH).uvs(8.625f, 5.875f, 8.75f, 6.375f).end().
				face(Direction.EAST).uvs(7, 5.875f, 8.625f, 6.375f).end().
				face(Direction.SOUTH).uvs(10.375f, 5.875f, 10.5f, 6.375f).end().
				face(Direction.WEST).uvs(8.75f, 5.875f, 10.375f, 6.375f).end().
				face(Direction.UP).uvs(8.75f, 5.875f, 8.625f, 4.25f).end().
				face(Direction.DOWN).uvs(8.875f, 4.25f, 8.75f, 5.875f).end().
				texture("#all").end().
				element().
				from(14, 2, 1).
				to(15, 12, 14).
				rotation().angle(-22.5f).axis(Direction.Axis.Z).origin(14.5f, 7, 7.5f).end().
				face(Direction.NORTH).uvs(1.625f, 7.375f, 1.75f, 8.625f).end().
				face(Direction.EAST).uvs(0, 7.375f, 1.625f, 8.625f).end().
				face(Direction.SOUTH).uvs(3.375f, 7.375f, 3.5f, 8.625f).end().
				face(Direction.WEST).uvs(1.75f, 7.375f, 3.375f, 8.625f).end().
				face(Direction.UP).uvs(1.75f, 7.375f, 1.625f, 5.75f).end().
				face(Direction.DOWN).uvs(1.875f, 5.75f, 1.75f, 7.375f).end().
				texture("#all").end().
				element().
				from(1, 10, 1).
				to(15, 10, 13).
				face(Direction.NORTH).uvs(1.5f, 5.75f, 3.25f, 5.75f).end().
				face(Direction.EAST).uvs(0, 5.75f, 1.5f, 5.75f).end().
				face(Direction.SOUTH).uvs(4.75f, 5.75f, 6.5f, 5.75f).end().
				face(Direction.WEST).uvs(3.25f, 5.75f, 4.75f, 5.75f).end().
				face(Direction.UP).uvs(3.25f, 5.75f, 1.5f, 4.25f).end().
				face(Direction.DOWN).uvs(5, 4.25f, 3.25f, 5.75f).end().
				texture("#all").end().
				element().
				from(7, 1.75f, 4).
				to(9, 9.75f, 6).
				face(Direction.NORTH).uvs(8.25f, 8.75f, 8.5f, 9.75f).end().
				face(Direction.EAST).uvs(8, 8.75f, 8.25f, 9.75f).end().
				face(Direction.SOUTH).uvs(8.75f, 8.75f, 9, 9.75f).end().
				face(Direction.WEST).uvs(8.5f, 8.75f, 8.75f, 9.75f).end().
				face(Direction.UP).uvs(8.5f, 8.75f, 8.25f, 8.5f).end().
				face(Direction.DOWN).uvs(8.75f, 8.5f, 8.5f, 8.75f).end().
				texture("#all").end().
				element().
				from(2, 1, 0).
				to(14, 2, 16).
				face(Direction.NORTH).uvs(2, 4.125f, 3.5f, 4.25f).end().
				face(Direction.EAST).uvs(0, 4.125f, 2, 4.25f).end().
				face(Direction.SOUTH).uvs(5.5f, 4.125f, 7, 4.25f).end().
				face(Direction.WEST).uvs(3.5f, 4.125f, 5.5f, 4.25f).end().
				face(Direction.UP).uvs(3.5f, 4.125f, 2, 2.125f).end().
				face(Direction.DOWN).uvs(5, 2.125f, 3.5f, 4.125f).end().
				texture("#all").end().
				element().
				from(0, 0, 0).
				to(16, 1, 16).
				face(Direction.NORTH).uvs(2, 2, 4, 2.125f).end().
				face(Direction.EAST).uvs(0, 2, 2, 2.125f).end().
				face(Direction.SOUTH).uvs(6, 2, 8, 2.125f).end().
				face(Direction.WEST).uvs(4, 2, 6, 2.125f).end().
				face(Direction.UP).uvs(4, 2, 2, 0).end().
				face(Direction.DOWN).uvs(6, 0, 4, 2).end().
				texture("#all").end().
				element().
				from(1, 9, 0).
				to(15, 10.5f, 2).
				face(Direction.NORTH).uvs(7.25f, 6.625f, 9, 6.75f).end().
				face(Direction.EAST).uvs(7, 6.625f, 7.25f, 6.75f).end().
				face(Direction.SOUTH).uvs(9.25f, 6.625f, 11, 6.75f).end().
				face(Direction.WEST).uvs(9, 6.625f, 9.25f, 6.75f).end().
				face(Direction.UP).uvs(9, 6.625f, 7.25f, 6.375f).end().
				face(Direction.DOWN).uvs(10.75f, 6.375f, 9, 6.625f).end().
				texture("#all").end().
				element().
				from(1, 9, 12).
				to(15, 10.5f, 13).
				face(Direction.NORTH).uvs(7.25f, 6.625f, 9, 6.75f).end().
				face(Direction.EAST).uvs(7, 6.625f, 7.25f, 6.75f).end().
				face(Direction.SOUTH).uvs(9.25f, 6.625f, 11, 6.75f).end().
				face(Direction.WEST).uvs(9, 6.625f, 9.25f, 6.75f).end().
				face(Direction.UP).uvs(9, 6.625f, 7.25f, 6.375f).end().
				face(Direction.DOWN).uvs(10.75f, 6.375f, 9, 6.625f).end().
				texture("#all").end().
				element().
				from(0, 1.5f, 7).
				to(3, 2.5f, 8).
				rotation().angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 7.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(0, 1.5f, 3).
				to(3, 2.5f, 4).
				rotation().angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 3.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(0, 1.5f, 11).
				to(3, 2.5f, 12).
				rotation().angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 11.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(13, 1.5f, 7).
				to(16, 2.5f, 8).
				rotation().angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 7.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(13, 1.5f, 3).
				to(16, 2.5f, 4).
				rotation().angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 3.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(13, 1.5f, 11).
				to(16, 2.5f, 12).
				rotation().angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 11.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end();
		
		ModelFile doubleModel = models().withExistingParent(blockPrefix(name(block)) + "_double", mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
				from(1.5f, 0.5f, 13.5f).
				to(14.5f, 10.5f, 14.5f).
				face(Direction.NORTH).uvs(0, 7.375f, 1.625f, 8.625f).end().
				face(Direction.EAST).uvs(3.375f, 7.375f, 3.5f, 8.625f).end().
				face(Direction.SOUTH).uvs(1.75f, 7.375f, 3.375f, 8.625f).end().
				face(Direction.WEST).uvs(1.625f, 7.375f, 1.75f, 8.625f).end().
				face(Direction.UP).uvs(1.75f, 7.375f, 1.625f, 5.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
				face(Direction.DOWN).uvs(1.875f, 5.75f, 1.75f, 7.375f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
				texture("#all").end().
				element().
				from(3.5f, 6, 3).
				to(7.5f, 9, 7).
				face(Direction.NORTH).uvs(7.5f, 8.125f, 8, 8.5f).end().
				face(Direction.EAST).uvs(7, 8.125f, 7.5f, 8.5f).end().
				face(Direction.SOUTH).uvs(8.5f, 8.125f, 9, 8.5f).end().
				face(Direction.WEST).uvs(8, 8.125f, 8.5f, 8.5f).end().
				face(Direction.UP).uvs(8, 8.125f, 7.5f, 7.625f).end().
				face(Direction.DOWN).uvs(8.5f, 7.625f, 8, 8.125f).end().
				texture("#all").end().
				element().
				from(8.5f, 2, 3).
				to(12.5f, 5, 7).
				face(Direction.NORTH).uvs(7.5f, 7.25f, 8, 7.625f).end().
				face(Direction.EAST).uvs(7, 7.25f, 7.5f, 7.625f).end().
				face(Direction.SOUTH).uvs(8.5f, 7.25f, 9, 7.625f).end().
				face(Direction.WEST).uvs(8, 7.25f, 8.5f, 7.625f).end().
				face(Direction.UP).uvs(8, 7.25f, 7.5f, 6.75f).end().
				face(Direction.DOWN).uvs(8.5f, 6.75f, 8, 7.25f).end().
				texture("#all").end().
				element().
				from(3.5f, 12, 5).
				to(7.5f, 15, 8).
				face(Direction.NORTH).uvs(8.375f, 1.125f, 8.875f, 1.5f).end().
				face(Direction.EAST).uvs(8, 1.125f, 8.375f, 1.5f).end().
				face(Direction.SOUTH).uvs(9.25f, 1.125f, 9.75f, 1.5f).end().
				face(Direction.WEST).uvs(8.875f, 1.125f, 9.25f, 1.5f).end().
				face(Direction.UP).uvs(8.875f, 1.125f, 8.375f, 0.75f).end().
				face(Direction.DOWN).uvs(9.375f, 0.75f, 8.875f, 1.125f).end().
				texture("#all").end().
				element().
				from(4.5f, 12, 8).
				to(6.5f, 16, 10).
				rotation().angle(45).axis(Direction.Axis.X).origin(5.5f, 13.5f, 9).end().
				face(Direction.NORTH).uvs(9.25f, 7, 9.5f, 7.5f).end().
				face(Direction.EAST).uvs(9, 7, 9.25f, 7.5f).end().
				face(Direction.SOUTH).uvs(9.75f, 7, 10, 7.5f).end().
				face(Direction.WEST).uvs(9.5f, 7, 9.75f, 7.5f).end().
				face(Direction.UP).uvs(9.5f, 7, 9.25f, 6.75f).end().
				face(Direction.DOWN).uvs(9.75f, 6.75f, 9.5f, 7).end().
				texture("#all").end().
				element().
				from(4.4f, 9.5f, 12).
				to(6.4f, 14.5f, 14).
				rotation().angle(-45).axis(Direction.Axis.X).origin(5.4f, 12, 13).end().
				face(Direction.NORTH).uvs(3.25f, 8.875f, 3.5f, 9.5f).end().
				face(Direction.EAST).uvs(3, 8.875f, 3.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(3.75f, 8.875f, 4, 9.5f).end().
				face(Direction.WEST).uvs(3.5f, 8.875f, 3.75f, 9.5f).end().
				face(Direction.UP).uvs(3.5f, 8.875f, 3.25f, 8.625f).end().
				face(Direction.DOWN).uvs(3.75f, 8.625f, 3.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(4.5f, 5.5f, 13.5f).
				to(6.5f, 10.5f, 15.5f).
				face(Direction.NORTH).uvs(4.25f, 8.875f, 4.5f, 9.5f).end().
				face(Direction.EAST).uvs(4, 8.875f, 4.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(4.75f, 8.875f, 5, 9.5f).end().
				face(Direction.WEST).uvs(4.5f, 8.875f, 4.75f, 9.5f).end().
				face(Direction.UP).uvs(4.5f, 8.875f, 4.25f, 8.625f).end().
				face(Direction.DOWN).uvs(4.75f, 8.625f, 4.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(4.5f, 1.5f, 12).
				to(6.5f, 6.5f, 14).
				rotation().angle(45).axis(Direction.Axis.X).origin(5.5f, 4, 13).end().
				face(Direction.NORTH).uvs(5.25f, 8.875f, 5.5f, 9.5f).end().
				face(Direction.EAST).uvs(5, 8.875f, 5.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(5.75f, 8.875f, 6, 9.5f).end().
				face(Direction.WEST).uvs(5.5f, 8.875f, 5.75f, 9.5f).end().
				face(Direction.UP).uvs(5.5f, 8.875f, 5.25f, 8.625f).end().
				face(Direction.DOWN).uvs(5.75f, 8.625f, 5.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(8.5f, 12, 5).
				to(12.5f, 15, 8).
				face(Direction.NORTH).uvs(8.375f, 0.375f, 8.875f, 0.75f).end().
				face(Direction.EAST).uvs(8, 0.375f, 8.375f, 0.75f).end().
				face(Direction.SOUTH).uvs(9.25f, 0.375f, 9.75f, 0.75f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9.25f, 0.75f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.375f, 0).end().
				face(Direction.DOWN).uvs(9.375f, 0, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(9.5f, 12, 8).
				to(11.5f, 16, 10).
				rotation().angle(45).axis(Direction.Axis.X).origin(10.5f, 13.5f, 9).end().
				face(Direction.NORTH).uvs(6.25f, 8.875f, 6.5f, 9.375f).end().
				face(Direction.EAST).uvs(6, 8.875f, 6.25f, 9.375f).end().
				face(Direction.SOUTH).uvs(6.75f, 8.875f, 7, 9.375f).end().
				face(Direction.WEST).uvs(6.5f, 8.875f, 6.75f, 9.375f).end().
				face(Direction.UP).uvs(6.5f, 8.875f, 6.25f, 8.625f).end().
				face(Direction.DOWN).uvs(6.75f, 8.625f, 6.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(9.4f, 9.5f, 12).
				to(11.4f, 14.5f, 14).
				rotation().angle(-45).axis(Direction.Axis.X).origin(10.4f, 12, 13).end().
				face(Direction.NORTH).uvs(2.25f, 8.875f, 2.5f, 9.5f).end().
				face(Direction.EAST).uvs(2, 8.875f, 2.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(2.75f, 8.875f, 3, 9.5f).end().
				face(Direction.WEST).uvs(2.5f, 8.875f, 2.75f, 9.5f).end().
				face(Direction.UP).uvs(2.5f, 8.875f, 2.25f, 8.625f).end().
				face(Direction.DOWN).uvs(2.75f, 8.625f, 2.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(9.5f, 5.5f, 13.5f).
				to(11.5f, 10.5f, 15.5f).
				face(Direction.NORTH).uvs(1.25f, 8.875f, 1.5f, 9.5f).end().
				face(Direction.EAST).uvs(1, 8.875f, 1.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(1.75f, 8.875f, 2, 9.5f).end().
				face(Direction.WEST).uvs(1.5f, 8.875f, 1.75f, 9.5f).end().
				face(Direction.UP).uvs(1.5f, 8.875f, 1.25f, 8.625f).end().
				face(Direction.DOWN).uvs(1.75f, 8.625f, 1.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(9.4f, 1.5f, 12).
				to(11.4f, 6.5f, 14).
				rotation().angle(-45).axis(Direction.Axis.X).origin(10.4f, 4, 13).end().
				face(Direction.NORTH).uvs(0.25f, 8.875f, 0.5f, 9.5f).end().
				face(Direction.EAST).uvs(0, 8.875f, 0.25f, 9.5f).end().
				face(Direction.SOUTH).uvs(0.75f, 8.875f, 1, 9.5f).end().
				face(Direction.WEST).uvs(0.5f, 8.875f, 0.75f, 9.5f).end().
				face(Direction.UP).uvs(0.5f, 8.875f, 0.25f, 8.625f).end().
				face(Direction.DOWN).uvs(0.75f, 8.625f, 0.5f, 8.875f).end().
				texture("#all").end().
				element().
				from(-0.5f, 11.5f, 0.9f).
				to(0.5f, 15.5f, 13.9f).
				rotation().angle(-22.5f).axis(Direction.Axis.Z).origin(0, 12.5f, 7.4f).end().
				face(Direction.NORTH).uvs(8.625f, 3.75f, 8.75f, 4.25f).end().
				face(Direction.EAST).uvs(7, 3.75f, 8.625f, 4.25f).end().
				face(Direction.SOUTH).uvs(10.375f, 3.75f, 10.5f, 4.25f).end().
				face(Direction.WEST).uvs(8.75f, 3.75f, 10.375f, 4.25f).end().
				face(Direction.UP).uvs(8.75f, 3.75f, 8.625f, 2.125f).end().
				face(Direction.DOWN).uvs(8.875f, 2.125f, 8.75f, 3.75f).end().
				texture("#all").end().
				element().
				from(1, 2, 1).
				to(2, 12, 14).
				rotation().angle(22.5f).axis(Direction.Axis.Z).origin(1.5f, 7, 7.5f).end().
				face(Direction.NORTH).uvs(5.125f, 7.375f, 5.25f, 8.625f).end().
				face(Direction.EAST).uvs(3.5f, 7.375f, 5.125f, 8.625f).end().
				face(Direction.SOUTH).uvs(6.875f, 7.375f, 7, 8.625f).end().
				face(Direction.WEST).uvs(5.25f, 7.375f, 6.875f, 8.625f).end().
				face(Direction.UP).uvs(5.25f, 7.375f, 5.125f, 5.75f).end().
				face(Direction.DOWN).uvs(5.25f, 7.375f, 5.125f, 5.75f).end().
				texture("#all").end().
				element().
				from(15.5f, 11.5f, 0.9f).
				to(16.5f, 15.5f, 13.9f).
				rotation().angle(22.5f).axis(Direction.Axis.Z).origin(16, 12.5f, 7.4f).end().
				face(Direction.NORTH).uvs(8.625f, 5.875f, 8.75f, 6.375f).end().
				face(Direction.EAST).uvs(7, 5.875f, 8.625f, 6.375f).end().
				face(Direction.SOUTH).uvs(10.375f, 5.875f, 10.5f, 6.375f).end().
				face(Direction.WEST).uvs(8.75f, 5.875f, 10.375f, 6.375f).end().
				face(Direction.UP).uvs(8.75f, 5.875f, 8.625f, 4.25f).end().
				face(Direction.DOWN).uvs(8.875f, 4.25f, 8.75f, 5.875f).end().
				texture("#all").end().
				element().
				from(14, 2, 1).
				to(15, 12, 14).
				rotation().angle(-22.5f).axis(Direction.Axis.Z).origin(14.5f, 7, 7.5f).end().
				face(Direction.NORTH).uvs(1.625f, 7.375f, 1.75f, 8.625f).end().
				face(Direction.EAST).uvs(0, 7.375f, 1.625f, 8.625f).end().
				face(Direction.SOUTH).uvs(3.375f, 7.375f, 3.5f, 8.625f).end().
				face(Direction.WEST).uvs(1.75f, 7.375f, 3.375f, 8.625f).end().
				face(Direction.UP).uvs(1.75f, 7.375f, 1.625f, 5.75f).end().
				face(Direction.DOWN).uvs(1.875f, 5.75f, 1.75f, 7.375f).end().
				texture("#all").end().
				element().
				from(1, 10, 1).
				to(15, 10, 13).
				face(Direction.NORTH).uvs(1.5f, 5.75f, 3.25f, 5.75f).end().
				face(Direction.EAST).uvs(0, 5.75f, 1.5f, 5.75f).end().
				face(Direction.SOUTH).uvs(4.75f, 5.75f, 6.5f, 5.75f).end().
				face(Direction.WEST).uvs(3.25f, 5.75f, 4.75f, 5.75f).end().
				face(Direction.UP).uvs(3.25f, 5.75f, 1.5f, 4.25f).end().
				face(Direction.DOWN).uvs(5, 4.25f, 3.25f, 5.75f).end().
				texture("#all").end().
				element().
				from(4.5f, 1.75f, 4).
				to(6.5f, 9.75f, 6).
				face(Direction.NORTH).uvs(8.25f, 8.75f, 8.5f, 9.75f).end().
				face(Direction.EAST).uvs(8, 8.75f, 8.25f, 9.75f).end().
				face(Direction.SOUTH).uvs(8.75f, 8.75f, 9, 9.75f).end().
				face(Direction.WEST).uvs(8.5f, 8.75f, 8.75f, 9.75f).end().
				face(Direction.UP).uvs(8.5f, 8.75f, 8.25f, 8.5f).end().
				face(Direction.DOWN).uvs(8.75f, 8.5f, 8.5f, 8.75f).end().
				texture("#all").end().
				element().
				from(9.5f, 1.75f, 4).
				to(11.5f, 9.75f, 6).
				face(Direction.NORTH).uvs(7.25f, 8.75f, 7.5f, 9.75f).end().
				face(Direction.EAST).uvs(7, 8.75f, 7.25f, 9.75f).end().
				face(Direction.SOUTH).uvs(7.75f, 8.75f, 8, 9.75f).end().
				face(Direction.WEST).uvs(7.75f, 8.75f, 8, 9.75f).end().
				face(Direction.UP).uvs(7.5f, 8.75f, 7.25f, 8.5f).end().
				face(Direction.DOWN).uvs(7.75f, 8.5f, 7.5f, 8.75f).end().
				texture("#all").end().
				element().
				from(2, 1, 0).
				to(14, 2, 16).
				face(Direction.NORTH).uvs(2, 4.125f, 3.5f, 4.25f).end().
				face(Direction.EAST).uvs(0, 4.125f, 2, 4.25f).end().
				face(Direction.SOUTH).uvs(5.5f, 4.125f, 7, 4.25f).end().
				face(Direction.WEST).uvs(3.5f, 4.125f, 5.5f, 4.25f).end().
				face(Direction.UP).uvs(3.5f, 4.125f, 2, 2.125f).end().
				face(Direction.DOWN).uvs(5, 2.125f, 3.5f, 4.125f).end().
				texture("#all").end().
				element().
				from(0, 0, 0).
				to(16, 1, 16).
				face(Direction.NORTH).uvs(2, 2, 4, 2.125f).end().
				face(Direction.EAST).uvs(0, 2, 2, 2.125f).end().
				face(Direction.SOUTH).uvs(6, 2, 8, 2.125f).end().
				face(Direction.WEST).uvs(4, 2, 6, 2.125f).end().
				face(Direction.UP).uvs(4, 2, 2, 0).end().
				face(Direction.DOWN).uvs(6, 0, 4, 2).end().
				texture("#all").end().
				element().
				from(0, 0, 0).
				to(16, 1, 16).
				face(Direction.NORTH).uvs(2, 2, 4, 2.125f).end().
				face(Direction.EAST).uvs(0, 2, 2, 2.125f).end().
				face(Direction.SOUTH).uvs(6, 2, 8, 2.125f).end().
				face(Direction.WEST).uvs(4, 2, 6, 2.125f).end().
				face(Direction.UP).uvs(4, 2, 2, 0).end().
				face(Direction.DOWN).uvs(6, 0, 4, 2).end().
				texture("#all").end().
				element().
				from(1, 9, 0).
				to(15, 10.5f, 2).
				face(Direction.NORTH).uvs(7.25f, 6.625f, 9, 6.75f).end().
				face(Direction.EAST).uvs(7, 6.625f, 7.25f, 6.75f).end().
				face(Direction.SOUTH).uvs(9.25f, 6.625f, 11, 6.75f).end().
				face(Direction.WEST).uvs(9, 6.625f, 9.25f, 6.75f).end().
				face(Direction.UP).uvs(9, 6.625f, 7.25f, 6.375f).end().
				face(Direction.DOWN).uvs(10.75f, 6.375f, 9, 6.625f).end().
				texture("#all").end().
				element().
				from(1, 9, 12).
				to(15, 10.5f, 13).
				face(Direction.NORTH).uvs(7.25f, 6.625f, 9, 6.75f).end().
				face(Direction.EAST).uvs(7.125f, 6.625f, 7.25f, 6.75f).end().
				face(Direction.SOUTH).uvs(9.125f, 6.625f, 10.875f, 6.75f).end().
				face(Direction.WEST).uvs(9, 6.625f, 9.125f, 6.75f).end().
				face(Direction.UP).uvs(9, 6.625f, 7.25f, 6.5f).end().
				face(Direction.DOWN).uvs(9, 6.625f, 7.25f, 6.5f).end().
				texture("#all").end().
				element().
				from(0, 1.5f, 7).
				to(3, 2.5f, 8).
				rotation().angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 7.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(0, 1.5f, 3).
				to(3, 2.5f, 4).
				rotation().angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 3.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(0, 1.5f, 11).
				to(3, 2.5f, 12).
				rotation().angle(-45).axis(Direction.Axis.Z).origin(1.5f, 2, 11.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(13, 1.5f, 7).
				to(16, 2.5f, 8).
				rotation().angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 7.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(13, 1.5f, 3).
				to(16, 2.5f, 4).
				rotation().angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 3.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end().
				element().
				from(13, 1.5f, 11).
				to(16, 2.5f, 12).
				rotation().angle(45).axis(Direction.Axis.Z).origin(14.5f, 2, 11.5f).end().
				face(Direction.NORTH).uvs(8.5f, 0.375f, 8.875f, 0.5f).end().
				face(Direction.EAST).uvs(8.375f, 0.375f, 8.5f, 0.5f).end().
				face(Direction.SOUTH).uvs(9, 0.375f, 9.375f, 0.5f).end().
				face(Direction.WEST).uvs(8.875f, 0.375f, 9, 0.5f).end().
				face(Direction.UP).uvs(8.875f, 0.375f, 8.5f, 0.25f).end().
				face(Direction.DOWN).uvs(9.25f, 0.25f, 8.875f, 0.375f).end().
				texture("#all").end();
		
		horizontalBlock(block, state -> 
		{
			boolean isDouble = state.getValue(BioForgeBlock.DOUBLE);
			return isDouble ? doubleModel : singleModel;
		});
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(singleModel);
	}
	
	private void createCatcherModel()
	{
		BioCatcherBlock block = Registration.BlockReg.CATCHER.get();
		ResourceLocation blockTexture = blockTexture(block).withSuffix("/0");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(3f, 1f, 13f).
						to(13f, 3f, 15f).
						face(Direction.NORTH).uvs(0f, 8.25f, 2.5f, 8.75f).end().
						face(Direction.EAST).uvs(5.5f, 8.25f, 6f, 8.75f).end().
						face(Direction.SOUTH).uvs(3f, 8.25f, 5.5f, 8.75f).end().
						face(Direction.WEST).uvs(2.5f, 8.25f, 3f, 8.75f).end().
						face(Direction.UP).uvs(3f, 8.25f, 2.5f, 5.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(3.5f, 5.75f, 3f, 8.25f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(13f, 1f, 3f).
						to(15f, 3f, 13f).
						face(Direction.NORTH).uvs(2.5f, 8.25f, 3f, 8.75f).end().
						face(Direction.EAST).uvs(0f, 8.25f, 2.5f, 8.75f).end().
						face(Direction.SOUTH).uvs(5.5f, 8.25f, 6f, 8.75f).end().
						face(Direction.WEST).uvs(3f, 8.25f, 5.5f, 8.75f).end().
						face(Direction.UP).uvs(3f, 8.25f, 2.5f, 5.75f).end().
						face(Direction.DOWN).uvs(3.5f, 5.75f, 3f, 8.25f).end().
						texture("#all").
				end().
				element().
						from(1.29289f, 1f, 2.70711f).
						to(3.29289f, 3f, 12.70711f).
						face(Direction.NORTH).uvs(2.5f, 8.25f, 3f, 8.75f).end().
						face(Direction.EAST).uvs(0f, 8.25f, 2.5f, 8.75f).end().
						face(Direction.SOUTH).uvs(5.5f, 8.25f, 6f, 8.75f).end().
						face(Direction.WEST).uvs(3f, 8.25f, 5.5f, 8.75f).end().
						face(Direction.UP).uvs(3f, 8.25f, 2.5f, 5.75f).end().
						face(Direction.DOWN).uvs(3.5f, 5.75f, 3f, 8.25f).end().
						texture("#all").
				end().
				element().
						from(3f, 1f, 1f).
						to(13f, 3f, 3f).
						face(Direction.NORTH).uvs(3f, 8.25f, 5.5f, 8.75f).end().
						face(Direction.EAST).uvs(2.5f, 8.25f, 3f, 8.75f).end().
						face(Direction.SOUTH).uvs(0f, 8.25f, 2.5f, 8.75f).end().
						face(Direction.WEST).uvs(5.5f, 8.25f, 6f, 8.75f).end().
						face(Direction.UP).uvs(3f, 8.25f, 2.5f, 5.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(3.5f, 5.75f, 3f, 8.25f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(0f, 1f, 10f).
						to(1f, 4f, 11f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(5f, 1f, 15f).
						to(6f, 4f, 16f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(10f, 1f, 15f).
						to(11f, 4f, 16f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(10f, 1f, 0f).
						to(11f, 4f, 1f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(5f, 1f, 0f).
						to(6f, 4f, 1f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(15f, 1f, 10f).
						to(16f, 4f, 11f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(15f, 1f, 5f).
						to(16f, 4f, 6f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(0f, 1f, 5f).
						to(1f, 4f, 6f).
						face(Direction.NORTH).uvs(3.25f, 9f, 3.5f, 9.75f).end().
						face(Direction.EAST).uvs(3f, 9f, 3.25f, 9.75f).end().
						face(Direction.SOUTH).uvs(3.75f, 9f, 4f, 9.75f).end().
						face(Direction.WEST).uvs(3.5f, 9f, 3.75f, 9.75f).end().
						face(Direction.UP).uvs(3.5f, 9f, 3.25f, 8.75f).end().
						face(Direction.DOWN).uvs(3.75f, 8.75f, 3.5f, 9f).end().
						texture("#all").
				end().
				element().
						from(2f, 0f, 2f).
						to(14f, 1f, 14f).
						face(Direction.NORTH).uvs(3f, 3f, 6f, 3.25f).end().
						face(Direction.EAST).uvs(0f, 3f, 3f, 3.25f).end().
						face(Direction.SOUTH).uvs(9f, 3f, 12f, 3.25f).end().
						face(Direction.WEST).uvs(6f, 3f, 9f, 3.25f).end().
						face(Direction.UP).uvs(6f, 3f, 3f, 0f).end().
						face(Direction.DOWN).uvs(9f, 0f, 6f, 3f).end().
						texture("#all").
				end().
				element().
						from(0f, 0f, 2f).
						to(2f, 1f, 14f).
						face(Direction.NORTH).uvs(9.5f, 6.25f, 10f, 6.5f).end().
						face(Direction.EAST).uvs(6.5f, 6.25f, 9.5f, 6.5f).end().
						face(Direction.SOUTH).uvs(6f, 6.25f, 6.5f, 6.5f).end().
						face(Direction.WEST).uvs(10f, 6.25f, 13f, 6.5f).end().
						face(Direction.UP).uvs(9.5f, 6.25f, 6.5f, 5.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(12.5f, 5.75f, 9.5f, 6.25f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(2f, 0f, 0f).
						to(14f, 1f, 2f).
						face(Direction.NORTH).uvs(6.5f, 6.25f, 9.5f, 6.5f).end().
						face(Direction.EAST).uvs(6f, 6.25f, 6.5f, 6.5f).end().
						face(Direction.SOUTH).uvs(10f, 6.25f, 13f, 6.5f).end().
						face(Direction.WEST).uvs(9.5f, 6.25f, 10f, 6.5f).end().
						face(Direction.UP).uvs(9.5f, 6.25f, 6.5f, 5.75f).end().
						face(Direction.DOWN).uvs(12.5f, 5.75f, 9.5f, 6.25f).end().
						texture("#all").
				end().
				element().
						from(14f, 0f, 2f).
						to(16f, 1f, 14f).
						face(Direction.NORTH).uvs(6f, 6.25f, 6.5f, 6.5f).end().
						face(Direction.EAST).uvs(10f, 6.25f, 13f, 6.5f).end().
						face(Direction.SOUTH).uvs(9.5f, 6.25f, 10f, 6.5f).end().
						face(Direction.WEST).uvs(6.5f, 6.25f, 9.5f, 6.5f).end().
						face(Direction.UP).uvs(9.5f, 6.25f, 6.5f, 5.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(12.5f, 5.75f, 9.5f, 6.25f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(2f, 0f, 14f).
						to(14f, 1f, 16f).
						face(Direction.NORTH).uvs(6.5f, 6.25f, 9.5f, 6.5f).end().
						face(Direction.EAST).uvs(6f, 6.25f, 6.5f, 6.5f).end().
						face(Direction.SOUTH).uvs(10f, 6.25f, 13f, 6.5f).end().
						face(Direction.WEST).uvs(9.5f, 6.25f, 10f, 6.5f).end().
						face(Direction.UP).uvs(9.5f, 6.25f, 6.5f, 5.75f).end().
						face(Direction.DOWN).uvs(12.5f, 5.75f, 9.5f, 6.25f).end().
						texture("#all").
				end();
		
		registerModels(block, model);
	}
	
	private void createStomachModel()
	{
		BioStomachBlock block = Registration.BlockReg.STOMACH.get();
		ResourceLocation blockTexture = blockTexture(block).withSuffix("/0");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(12.5f, 13.5f, 3.25f).
						to(13.5f, 14.5f, 4.25f).
						face(Direction.NORTH).uvs(4.375f, 3.25f, 4.5f, 3.375f).end().
						face(Direction.EAST).uvs(4.25f, 3.25f, 4.375f, 3.375f).end().
						face(Direction.SOUTH).uvs(4.125f, 3.25f, 4.25f, 3.375f).end().
						face(Direction.WEST).uvs(4f, 3.25f, 4.125f, 3.375f).end().
						face(Direction.UP).uvs(4.25f, 3.25f, 4.125f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.DOWN).uvs(4.375f, 3.125f, 4.25f, 3.25f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").
				end().
				element().
						from(12.5f, 11.5f, 3.25f).
						to(13.5f, 12.5f, 4.25f).
						face(Direction.NORTH).uvs(4.375f, 3f, 4.5f, 3.125f).end().
						face(Direction.EAST).uvs(4.25f, 3f, 4.375f, 3.125f).end().
						face(Direction.SOUTH).uvs(4.125f, 3f, 4.25f, 3.125f).end().
						face(Direction.WEST).uvs(4f, 3f, 4.125f, 3.125f).end().
						face(Direction.UP).uvs(4.25f, 3f, 4.125f, 2.875f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.DOWN).uvs(4.375f, 2.875f, 4.25f, 3f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").
				end().
				element().
						from(13.5f, 9.5f, 3.25f).
						to(14.5f, 16.5f, 4.25f).
						face(Direction.NORTH).uvs(0.375f, 6.875f, 0.5f, 7.75f).end().
						face(Direction.EAST).uvs(0.25f, 6.875f, 0.375f, 7.75f).end().
						face(Direction.SOUTH).uvs(0.125f, 6.875f, 0.25f, 7.75f).end().
						face(Direction.WEST).uvs(0f, 6.875f, 0.125f, 7.75f).end().
						face(Direction.UP).uvs(0.25f, 6.875f, 0.125f, 6.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.DOWN).uvs(0.375f, 6.75f, 0.25f, 6.875f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").
				end().
				element().
						from(8.5f, 11.5f, 3.25f).
						to(9.5f, 12.5f, 4.25f).
						face(Direction.NORTH).uvs(5.375f, 3f, 5.5f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.EAST).uvs(5f, 3f, 5.125f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.SOUTH).uvs(5.125f, 3f, 5.25f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.WEST).uvs(5.25f, 3f, 5.375f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.UP).uvs(5.375f, 2.875f, 5.25f, 3f).end().
						face(Direction.DOWN).uvs(5.25f, 3f, 5.125f, 2.875f).end().
						texture("#all").
				end().
				element().
						from(8.5f, 13.5f, 3.25f).
						to(9.5f, 14.5f, 4.25f).
						face(Direction.NORTH).uvs(5.375f, 3.25f, 5.5f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.EAST).uvs(5f, 3.25f, 5.125f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.SOUTH).uvs(5.125f, 3.25f, 5.25f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.WEST).uvs(5.25f, 3.25f, 5.375f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.UP).uvs(5.375f, 3.125f, 5.25f, 3.25f).end().
						face(Direction.DOWN).uvs(5.25f, 3.25f, 5.125f, 3.125f).end().
						texture("#all").
				end().
				element().
						from(7.5f, 9.5f, 3.25f).
						to(8.5f, 16.5f, 4.25f).
						face(Direction.NORTH).uvs(1.375f, 6.875f, 1.5f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.EAST).uvs(1f, 6.875f, 1.125f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.SOUTH).uvs(1.125f, 6.875f, 1.25f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.WEST).uvs(1.25f, 6.875f, 1.375f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.UP).uvs(1.375f, 6.75f, 1.25f, 6.875f).end().
						face(Direction.DOWN).uvs(1.25f, 6.875f, 1.125f, 6.75f).end().
						texture("#all").
				end().
				element().
						from(9.5f, 14.5f, 3.25f).
						to(10.5f, 15.5f, 4.25f).
						face(Direction.NORTH).uvs(4.875f, 3f, 5f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.EAST).uvs(4.875f, 2.875f, 4.75f, 3f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(4.625f, 3f, 4.75f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.WEST).uvs(4.75f, 3f, 4.625f, 2.875f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.UP).uvs(4.75f, 3f, 4.875f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(4.5f, 3f, 4.625f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(11.5f, 14.5f, 3.25f).
						to(12.5f, 15.5f, 4.25f).
						face(Direction.NORTH).uvs(4.875f, 3.25f, 5f, 3.375f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.EAST).uvs(4.875f, 3.125f, 4.75f, 3.25f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(4.625f, 3.25f, 4.75f, 3.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.WEST).uvs(4.75f, 3.25f, 4.625f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.UP).uvs(4.75f, 3.25f, 4.875f, 3.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(4.5f, 3.25f, 4.625f, 3.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(7.5f, 15.5f, 3f).
						to(14.5f, 16.5f, 4f).
						face(Direction.NORTH).uvs(0.875f, 6.875f, 1f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.EAST).uvs(0.875f, 6.75f, 0.75f, 6.875f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(0.625f, 6.875f, 0.75f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.WEST).uvs(0.75f, 6.875f, 0.625f, 6.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.UP).uvs(0.75f, 6.875f, 0.875f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(0.5f, 6.875f, 0.625f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(11.5f, 10.5f, 3.25f).
						to(12.5f, 11.5f, 4.25f).
						face(Direction.NORTH).uvs(3.375f, 5.375f, 3.5f, 5.5f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.EAST).uvs(3.25f, 5.375f, 3.125f, 5.25f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(3.125f, 5.375f, 3.25f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).uvs(3.375f, 5.25f, 3.25f, 5.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).uvs(3f, 5.375f, 3.125f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(3.25f, 5.375f, 3.375f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(9.5f, 10.5f, 3.25f).
						to(10.5f, 11.5f, 4.25f).
						face(Direction.NORTH).uvs(5.875f, 3f, 6f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.EAST).uvs(5.75f, 3f, 5.625f, 2.875f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(5.625f, 3f, 5.75f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).uvs(5.875f, 2.875f, 5.75f, 3f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).uvs(5.5f, 3f, 5.625f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(5.75f, 3f, 5.875f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(7.5f, 9.5f, 3f).
						to(14.5f, 10.5f, 4f).
						face(Direction.NORTH).uvs(1.875f, 6.875f, 2f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.EAST).uvs(1.75f, 6.875f, 1.625f, 6.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(1.625f, 6.875f, 1.75f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).uvs(1.875f, 6.75f, 1.75f, 6.875f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).uvs(1.5f, 6.875f, 1.625f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(1.75f, 6.875f, 1.875f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(12.5f, 13.5f, 2.25f).
						to(13.5f, 14.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(4.375f, 3.25f, 4.5f, 3.375f).end().
						face(Direction.EAST).uvs(4.25f, 3.25f, 4.375f, 3.375f).end().
						face(Direction.SOUTH).uvs(4.125f, 3.25f, 4.25f, 3.375f).end().
						face(Direction.WEST).uvs(4f, 3.25f, 4.125f, 3.375f).end().
						face(Direction.UP).uvs(4.25f, 3.25f, 4.125f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.DOWN).uvs(4.375f, 3.125f, 4.25f, 3.25f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").
				end().
				element().
						from(12.5f, 11.5f, 2.25f).
						to(13.5f, 12.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(4.375f, 3f, 4.5f, 3.125f).end().
						face(Direction.EAST).uvs(4.25f, 3f, 4.375f, 3.125f).end().
						face(Direction.SOUTH).uvs(4.125f, 3f, 4.25f, 3.125f).end().
						face(Direction.WEST).uvs(4f, 3f, 4.125f, 3.125f).end().
						face(Direction.UP).uvs(4.25f, 3f, 4.125f, 2.875f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.DOWN).uvs(4.375f, 2.875f, 4.25f, 3f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").
				end().
				element().
						from(13.5f, 9.5f, 2.25f).
						to(14.5f, 16.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(0.375f, 6.875f, 0.5f, 7.75f).end().
						face(Direction.EAST).uvs(0.25f, 6.875f, 0.375f, 7.75f).end().
						face(Direction.SOUTH).uvs(0.125f, 6.875f, 0.25f, 7.75f).end().
						face(Direction.WEST).uvs(0f, 6.875f, 0.125f, 7.75f).end().
						face(Direction.UP).uvs(0.25f, 6.875f, 0.125f, 6.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.DOWN).uvs(0.375f, 6.75f, 0.25f, 6.875f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						texture("#all").
				end().
				element().
						from(8.5f, 11.5f, 2.25f).
						to(9.5f, 12.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(5.375f, 3f, 5.5f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.EAST).uvs(5f, 3f, 5.125f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.SOUTH).uvs(5.125f, 3f, 5.25f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.WEST).uvs(5.25f, 3f, 5.375f, 3.125f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.UP).uvs(5.375f, 2.875f, 5.25f, 3f).end().
						face(Direction.DOWN).uvs(5.25f, 3f, 5.125f, 2.875f).end().
						texture("#all").
				end().
				element().
						from(8.5f, 13.5f, 2.25f).
						to(9.5f, 14.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(5.375f, 3.25f, 5.5f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.EAST).uvs(5f, 3.25f, 5.125f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.SOUTH).uvs(5.125f, 3.25f, 5.25f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.WEST).uvs(5.25f, 3.25f, 5.375f, 3.375f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.UP).uvs(5.375f, 3.125f, 5.25f, 3.25f).end().
						face(Direction.DOWN).uvs(5.25f, 3.25f, 5.125f, 3.125f).end().
						texture("#all").
				end().
				element().
						from(7.5f, 9.5f, 2.25f).
						to(8.5f, 16.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(1.375f, 6.875f, 1.5f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.EAST).uvs(1f, 6.875f, 1.125f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.SOUTH).uvs(1.125f, 6.875f, 1.25f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.WEST).uvs(1.25f, 6.875f, 1.375f, 7.75f).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
						face(Direction.UP).uvs(1.375f, 6.75f, 1.25f, 6.875f).end().
						face(Direction.DOWN).uvs(1.25f, 6.875f, 1.125f, 6.75f).end().
						texture("#all").
				end().
				element().
						from(9.5f, 14.5f, 2.25f).
						to(10.5f, 15.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(4.875f, 3f, 5f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.EAST).uvs(4.875f, 2.875f, 4.75f, 3f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(4.625f, 3f, 4.75f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.WEST).uvs(4.75f, 3f, 4.625f, 2.875f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.UP).uvs(4.75f, 3f, 4.875f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(4.5f, 3f, 4.625f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(11.5f, 14.5f, 2.25f).
						to(12.5f, 15.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(4.875f, 3.25f, 5f, 3.375f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.EAST).uvs(4.875f, 3.125f, 4.75f, 3.25f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(4.625f, 3.25f, 4.75f, 3.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.WEST).uvs(4.75f, 3.25f, 4.625f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.UP).uvs(4.75f, 3.25f, 4.875f, 3.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(4.5f, 3.25f, 4.625f, 3.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(7.5f, 15.5f, 2f).
						to(14.5f, 16.5f, 3f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(0.875f, 6.875f, 1f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.EAST).uvs(0.875f, 6.75f, 0.75f, 6.875f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(0.625f, 6.875f, 0.75f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.WEST).uvs(0.75f, 6.875f, 0.625f, 6.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.UP).uvs(0.75f, 6.875f, 0.875f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.DOWN).uvs(0.5f, 6.875f, 0.625f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(11.5f, 10.5f, 2.25f).
						to(12.5f, 11.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(3.375f, 5.375f, 3.5f, 5.5f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.EAST).uvs(3.25f, 5.375f, 3.125f, 5.25f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(3.125f, 5.375f, 3.25f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).uvs(3.375f, 5.25f, 3.25f, 5.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).uvs(3f, 5.375f, 3.125f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(3.25f, 5.375f, 3.375f, 5.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(9.5f, 10.5f, 2.25f).
						to(10.5f, 11.5f, 3.25f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(5.875f, 3f, 6f, 3.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.EAST).uvs(5.75f, 3f, 5.625f, 2.875f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(5.625f, 3f, 5.75f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).uvs(5.875f, 2.875f, 5.75f, 3f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).uvs(5.5f, 3f, 5.625f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(5.75f, 3f, 5.875f, 3.125f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(7.5f, 9.5f, 2f).
						to(14.5f, 10.5f, 3f).
						rotation().angle(45).axis(Direction.Axis.Z).origin(11f, 13f, 2.625f).end().
						face(Direction.NORTH).uvs(1.875f, 6.875f, 2f, 7.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.EAST).uvs(1.75f, 6.875f, 1.625f, 6.75f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(1.625f, 6.875f, 1.75f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).uvs(1.875f, 6.75f, 1.75f, 6.875f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).uvs(1.5f, 6.875f, 1.625f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(1.75f, 6.875f, 1.875f, 7.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(15f, 1f, 3.25f).
						to(16f, 7f, 13.25f).
						face(Direction.NORTH).uvs(4.75f, 4.75f, 4.875f, 5.5f).end().
						face(Direction.EAST).uvs(3.5f, 4.75f, 4.75f, 5.5f).end().
						face(Direction.SOUTH).uvs(6.125f, 4.75f, 6.25f, 5.5f).end().
						face(Direction.WEST).uvs(4.875f, 4.75f, 6.125f, 5.5f).end().
						face(Direction.UP).uvs(4.875f, 4.75f, 4.75f, 3.5f).end().
						face(Direction.DOWN).uvs(5f, 3.5f, 4.875f, 4.75f).end().
						texture("#all").
				end().
				element().
						from(8f, 1f, 3.25f).
						to(9f, 7f, 13.25f).
						face(Direction.NORTH).uvs(4.75f, 4.75f, 4.875f, 5.5f).end().
						face(Direction.EAST).uvs(3.5f, 4.75f, 4.75f, 5.5f).end().
						face(Direction.SOUTH).uvs(6.125f, 4.75f, 6.25f, 5.5f).end().
						face(Direction.WEST).uvs(4.875f, 4.75f, 6.125f, 5.5f).end().
						face(Direction.UP).uvs(4.875f, 4.75f, 4.75f, 3.5f).end().
						face(Direction.DOWN).uvs(5f, 3.5f, 4.875f, 4.75f).end().
						texture("#all").
				end().
				element().
						from(8f, 1f, 2.25f).
						to(16f, 7f, 3.25f).
						face(Direction.NORTH).uvs(6.375f, 3f, 7.375f, 3.75f).end().
						face(Direction.EAST).uvs(6.25f, 3f, 6.375f, 3.75f).end().
						face(Direction.SOUTH).uvs(7.5f, 3f, 8.5f, 3.75f).end().
						face(Direction.WEST).uvs(7.375f, 3f, 7.5f, 3.75f).end().
						face(Direction.UP).uvs(7.375f, 3f, 6.375f, 2.875f).end().
						face(Direction.DOWN).uvs(8.375f, 2.875f, 7.375f, 3f).end().
						texture("#all").
				end().
				element().
						from(1f, 1f, 3.25f).
						to(8f, 3f, 4.25f).
						face(Direction.NORTH).uvs(6.375f, 3f, 7.25f, 3.25f).end().
						face(Direction.EAST).uvs(6.25f, 3f, 6.375f, 3.25f).end().
						face(Direction.SOUTH).uvs(7.375f, 3f, 8.25f, 3.25f).end().
						face(Direction.WEST).uvs(7.25f, 3f, 7.375f, 3.25f).end().
						face(Direction.UP).uvs(7.25f, 3f, 6.375f, 2.875f).end().
						face(Direction.DOWN).uvs(8.125f, 2.875f, 7.25f, 3f).end().
						texture("#all").
				end().
				element().
						from(1f, 1f, 12.25f).
						to(8f, 3f, 13.25f).
						face(Direction.NORTH).uvs(6.375f, 3f, 7.25f, 3.25f).end().
						face(Direction.EAST).uvs(6.25f, 3f, 6.375f, 3.25f).end().
						face(Direction.SOUTH).uvs(7.375f, 3f, 8.25f, 3.25f).end().
						face(Direction.WEST).uvs(7.25f, 3f, 7.375f, 3.25f).end().
						face(Direction.UP).uvs(7.25f, 3f, 6.375f, 2.875f).end().
						face(Direction.DOWN).uvs(8.125f, 2.875f, 7.25f, 3f).end().
						texture("#all").
				end().
				element().
						from(8f, 1f, 13.25f).
						to(16f, 7f, 14.25f).
						face(Direction.NORTH).uvs(6.375f, 3f, 7.375f, 3.75f).end().
						face(Direction.EAST).uvs(6.25f, 3f, 6.375f, 3.75f).end().
						face(Direction.SOUTH).uvs(7.5f, 3f, 8.5f, 3.75f).end().
						face(Direction.WEST).uvs(7.375f, 3f, 7.5f, 3.75f).end().
						face(Direction.UP).uvs(7.375f, 3f, 6.375f, 2.875f).end().
						face(Direction.DOWN).uvs(8.375f, 2.875f, 7.375f, 3f).end().
						texture("#all").
				end().
				element().
						from(1f, 3f, 3.25f).
						to(7f, 4f, 13.25f).
						face(Direction.NORTH).uvs(1.25f, 3.375f, 2f, 3.5f).end().
						face(Direction.EAST).uvs(0f, 3.375f, 1.25f, 3.5f).end().
						face(Direction.SOUTH).uvs(3.25f, 3.375f, 4f, 3.5f).end().
						face(Direction.WEST).uvs(2f, 3.375f, 3.25f, 3.5f).end().
						face(Direction.UP).uvs(2f, 3.375f, 1.25f, 2.125f).end().
						face(Direction.DOWN).uvs(2.75f, 2.125f, 2f, 3.375f).end().
						texture("#all").
				end().
				element().
						from(0f, 1f, 3.25f).
						to(1f, 4f, 13.25f).
						face(Direction.NORTH).uvs(3.25f, 3.375f, 3.625f, 3.5f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.EAST).uvs(2f, 3.375f, 1.625f, 2.125f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.SOUTH).uvs(1.625f, 3.375f, 2f, 3.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.WEST).uvs(2.375f, 2.125f, 2f, 3.375f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						face(Direction.UP).uvs(0.375f, 3.375f, 1.625f, 3.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(2f, 3.375f, 3.25f, 3.5f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(7f, 1f, 12.75f).
						to(9f, 9f, 14.75f).
						face(Direction.NORTH).uvs(6.5f, 4f, 6.75f, 5f).end().
						face(Direction.EAST).uvs(6.25f, 4f, 6.5f, 5f).end().
						face(Direction.SOUTH).uvs(7f, 4f, 7.25f, 5f).end().
						face(Direction.WEST).uvs(6.75f, 4f, 7f, 5f).end().
						face(Direction.UP).uvs(6.75f, 4f, 6.5f, 3.75f).end().
						face(Direction.DOWN).uvs(7f, 3.75f, 6.75f, 4f).end().
						texture("#all").
				end().
				element().
						from(3.5f, 1f, 2.25f).
						to(5.5f, 7f, 4.25f).
						face(Direction.NORTH).uvs(6.75f, 4f, 7f, 4.75f).end().
						face(Direction.EAST).uvs(6.5f, 4f, 6.75f, 4.75f).end().
						face(Direction.SOUTH).uvs(6.25f, 4f, 6.5f, 4.75f).end().
						face(Direction.WEST).uvs(7f, 4f, 7.25f, 4.75f).end().
						face(Direction.UP).uvs(6.75f, 4f, 6.5f, 3.75f).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(7f, 3.75f, 6.75f, 4f).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(2f, 3f, 0.25f).
						to(8f, 9f, 6.25f).
						face(Direction.NORTH).uvs(6.75f, 6.25f, 7.5f, 7f).end().
						face(Direction.EAST).uvs(6f, 6.25f, 6.75f, 7f).end().
						face(Direction.SOUTH).uvs(8.25f, 6.25f, 9f, 7f).end().
						face(Direction.WEST).uvs(7.5f, 6.25f, 8.25f, 7f).end().
						face(Direction.UP).uvs(7.5f, 6.25f, 6.75f, 5.5f).end().
						face(Direction.DOWN).uvs(8.25f, 5.5f, 7.5f, 6.25f).end().
						texture("#all").
				end().
				element().
						from(0f, 4.25f, 6.25f).
						to(7f, 11.25f, 13.25f).
						face(Direction.NORTH).uvs(0.875f, 4.375f, 1.75f, 5.25f).end().
						face(Direction.EAST).uvs(0f, 4.375f, 0.875f, 5.25f).end().
						face(Direction.SOUTH).uvs(2.625f, 4.375f, 3.5f, 5.25f).end().
						face(Direction.WEST).uvs(1.75f, 4.375f, 2.625f, 5.25f).end().
						face(Direction.UP).uvs(1.75f, 4.375f, 0.875f, 3.5f).end().
						face(Direction.DOWN).uvs(2.625f, 3.5f, 1.75f, 4.375f).end().
						texture("#all").
				end().
				element().
						from(5f, 7f, 9.25f).
						to(12f, 14f, 16.25f).
						face(Direction.NORTH).uvs(0.875f, 4.375f, 1.75f, 5.25f).end().
						face(Direction.EAST).uvs(0f, 4.375f, 0.875f, 5.25f).end().
						face(Direction.SOUTH).uvs(2.625f, 4.375f, 3.5f, 5.25f).end().
						face(Direction.WEST).uvs(1.75f, 4.375f, 2.625f, 5.25f).end().
						face(Direction.UP).uvs(1.75f, 4.375f, 0.875f, 3.5f).end().
						face(Direction.DOWN).uvs(2.625f, 3.5f, 1.75f, 4.375f).end().
						texture("#all").
				end().
				element().
						from(0f, 0f, 0.25f).
						to(16f, 1f, 16.25f).
						face(Direction.NORTH).uvs(2f, 2f, 4f, 2.125f).end().
						face(Direction.EAST).uvs(0f, 2f, 2f, 2.125f).end().
						face(Direction.SOUTH).uvs(6f, 2f, 8f, 2.125f).end().
						face(Direction.WEST).uvs(4f, 2f, 6f, 2.125f).end().
						face(Direction.UP).uvs(4f, 2f, 2f, 0f).end().
						face(Direction.DOWN).uvs(6f, 0f, 4f, 2f).end().
						texture("#all").
				end().
				element().
						from(8f, 10f, 4.25f).
						to(14f, 16f, 10.25f).
						face(Direction.NORTH).uvs(0.75f, 6f, 1.5f, 6.75f).end().
						face(Direction.EAST).uvs(0f, 6f, 0.75f, 6.75f).end().
						face(Direction.SOUTH).uvs(2.25f, 6f, 3f, 6.75f).end().
						face(Direction.WEST).uvs(1.5f, 6f, 2.25f, 6.75f).end().
						face(Direction.UP).uvs(1.5f, 6f, 0.75f, 5.25f).end().
						face(Direction.DOWN).uvs(2.25f, 5.25f, 1.5f, 6f).end().
						texture("#all").
				end();
		
		horizontalBlock(block, model);
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
	}
	
	private void createCrusherModel()
	{
		BioCrusherBlock block = Registration.BlockReg.CRUSHER.get();
		ResourceLocation blockTexture = blockTexture(block).withSuffix("/0");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(3, 2, 3).
						to(13, 9, 13).
						face(Direction.NORTH).uvs(2.5f, 7, 5, 8.75f).end().
						face(Direction.EAST).uvs(0, 7, 2.5f, 8.75f).end().
						face(Direction.SOUTH).uvs(7.5f, 7, 10, 8.75f).end().
						face(Direction.WEST).uvs(5, 7, 7.5f, 8.75f).end().
						face(Direction.UP).uvs(5, 7, 2.5f, 4.5f).end().
						face(Direction.DOWN).uvs(7.5f, 4.5f, 5, 7).end().
						texture("#all").
				end().
				element().
						from(0, 9, 0).
						to(16, 11, 16).
						face(Direction.NORTH).uvs(4, 4, 8, 4.5f).end().
						face(Direction.EAST).uvs(0, 4, 4, 4.5f).end().
						face(Direction.SOUTH).uvs(12, 4, 16, 4.5f).end().
						face(Direction.WEST).uvs(8, 4, 12, 4.5f).end().
						face(Direction.UP).uvs(8, 4, 4, 0).end().
						face(Direction.DOWN).uvs(12, 0, 8, 4).end().
						texture("#all").
				end().
				element().
						from(0,0, 0).
						to(16, 2, 16).
						face(Direction.NORTH).uvs(4, 4, 8, 4.5f).end().
						face(Direction.EAST).uvs(0, 4, 4, 4.5f).end().
						face(Direction.SOUTH).uvs(12, 4, 16, 4.5f).end().
						face(Direction.WEST).uvs(8, 4, 12, 4.5f).end().
						face(Direction.UP).uvs(8, 4, 4, 0).end().
						face(Direction.DOWN).uvs(12, 0, 8, 4).end().
						texture("#all").
				end().
				element().
						from(9, 12, 1).
						to(13, 16, 15).
						face(Direction.NORTH).uvs(3.5f, 12.25f, 4.5f, 13.25f).end().
						face(Direction.EAST).uvs(0, 12.25f, 3.5f, 13.25f).end().
						face(Direction.SOUTH).uvs(8, 12.25f, 9, 13.25f).end().
						face(Direction.WEST).uvs(4.5f, 12.25f, 8, 13.25f).end().
						face(Direction.UP).uvs(4.5f, 12.25f, 3.5f, 8.75f).end().
						face(Direction.DOWN).uvs(5.5f, 8.75f, 4.5f, 12.25f).end().
						texture("#all").
				end().
				element().
						from(9, 12, 2).
						to(13, 16, 14).
						rotation().
								angle(-45).
								axis(Direction.Axis.Z).
								origin(11, 14,8).end().
						face(Direction.NORTH).uvs(13, 7.5f, 14, 8.5f).end().
						face(Direction.EAST).uvs(10, 7.5f, 13, 8.5f).end().
						face(Direction.SOUTH).uvs(14, 7.5f, 15, 8.5f).end().
						face(Direction.WEST).uvs(14, 7.5f, 15, 8.5f).end().
						face(Direction.UP).uvs(14, 7.5f, 13, 4.5f).end().
						face(Direction.DOWN).uvs(15, 4.5f, 14, 7.5f).end().
						texture("#all").
				end().
				element().
						from(3, 12, 1).
						to(7, 16, 15).
						face(Direction.NORTH).uvs(3.5f, 12.25f, 4.5f, 13.25f).end().
						face(Direction.EAST).uvs(0, 12.25f, 3.5f, 13.25f).end().
						face(Direction.SOUTH).uvs(8, 12.25f, 9, 13.25f).end().
						face(Direction.WEST).uvs(4.5f, 12.25f, 8, 13.25f).end().
						face(Direction.UP).uvs(4.5f, 12.25f, 3.5f, 8.75f).end().
						face(Direction.DOWN).uvs(5.5f, 8.75f, 4.5f, 12.25f).end().
						texture("#all").
				end().
				element().
						from(3, 12, 2).
						to(7, 16, 14).
						rotation().angle(-45).axis(Direction.Axis.Z).origin(5, 14, 8).end().
						face(Direction.NORTH).uvs(13, 7.5f, 14, 8.5f).end().
						face(Direction.EAST).uvs(10, 7.5f, 13, 8.5f).end().
						face(Direction.SOUTH).uvs(14, 7.5f, 15, 8.5f).end().
						face(Direction.WEST).uvs(10, 6.5f, 13, 7.5f).end().
						face(Direction.UP).uvs(14, 7.5f, 13, 4.5f).end().
						face(Direction.DOWN).uvs(15, 4.5f, 14, 7.5f).end().
						texture("#all").
				end().
				element().
						from(14, 11,1).
						to(16, 17, 15).
						face(Direction.NORTH).uvs(12.5f, 12.25f, 13, 13.75f).end().
						face(Direction.EAST).uvs(9, 12.25f, 12.5f, 13.75f).end().
						face(Direction.SOUTH).uvs(13, 12.25f, 13.5f, 13.75f).end().
						face(Direction.WEST).uvs(9, 10.75f, 12.5f, 12.25f).end().
						face(Direction.UP).uvs(13, 12.25f, 12.5f, 8.75f).end().
						face(Direction.DOWN).uvs(13.5f, 8.75f, 13, 12.25f).end().
						texture("#all").
				end().
				element().
						from(0, 11,1).
						to(2, 17, 15).
						face(Direction.NORTH).uvs(12.5f, 12.25f, 13, 13.75f).end().
						face(Direction.EAST).uvs(9, 12.25f, 12.5f, 13.75f).end().
						face(Direction.SOUTH).uvs(13, 12.25f, 13.5f, 13.75f).end().
						face(Direction.WEST).uvs(9, 10.75f, 12.5f, 12.25f).end().
						face(Direction.UP).uvs(13, 12.25f, 12.5f, 8.75f).end().
						face(Direction.DOWN).uvs(13.5f, 8.75f, 13, 12.25f).end().
						texture("#all").
				end().
				element().
						from(0, 11,15).
						to(16, 14, 16).
						face(Direction.NORTH).uvs(0.25f, 13.5f, 4.25f, 14.25f).end().
						face(Direction.EAST).uvs(0, 13.5f, 0.25f, 14.25f).end().
						face(Direction.SOUTH).uvs(4.5f, 13.5f, 8.5f, 14.25f).end().
						face(Direction.WEST).uvs(4.25f, 13.5f, 4.5f, 14.25f).end().
						face(Direction.UP).uvs(4.25f, 13.5f, 0.25f, 13.25f).end().
						face(Direction.DOWN).uvs(8.25f, 13.25f, 4.25f, 13.5f).end().
						texture("#all").
				end().
				element().
						from(0, 11,0).
						to(16, 14, 1).
						face(Direction.NORTH).uvs(0.25f, 13.5f, 4.25f, 14.25f).end().
						face(Direction.EAST).uvs(0, 13.5f, 0.25f, 14.25f).end().
						face(Direction.SOUTH).uvs(4.5f, 13.5f, 8.5f, 14.25f).end().
						face(Direction.WEST).uvs(4.25f, 13.5f, 4.5f, 14.25f).end().
						face(Direction.UP).uvs(4.25f, 13.5f, 0.25f, 13.25f).end().
						face(Direction.DOWN).uvs(8.25f, 13.25f, 4.25f, 13.5f).end().
						texture("#all").
				end().
				element().
						from(5, 2.86603f, 0.5f).
						to(11, 5.86603f, 5.5f).
						rotation().angle(-45).axis(Direction.Axis.X).origin(8, 4.36603f, 3).end().
						face(Direction.NORTH).uvs(9.75f, 15, 11.25f, 15.75f).end().
						face(Direction.EAST).uvs(8.5f, 15, 9.75f, 15.75f).end().
						face(Direction.SOUTH).uvs(12.5f, 15, 14, 15.75f).end().
						face(Direction.WEST).uvs(11.25f, 15, 12.5f, 15.75f).end().
						face(Direction.UP).uvs(12.75f, 15, 11.25f, 13.75f).end().
						face(Direction.DOWN).uvs(12.75f, 15, 11.25f, 13.75f).end().
						texture("#all").
				end().
				element().
						from(1, 16, 3).
						to(2, 20, 4).
						face(Direction.NORTH).uvs(0.75f, 0.5f, 1, 1.5f).end().
						face(Direction.EAST).uvs(0.5f, 0.5f, 0.75f, 1.5f).end().
						face(Direction.SOUTH).uvs(1.25f, 0.5f, 1.5f, 1.5f).end().
						face(Direction.WEST).uvs(1, 0.5f, 1.25f, 1.5f).end().
						face(Direction.UP).uvs(1, 0.5f, 0.75f, 0.25f).end().
						face(Direction.DOWN).uvs(1.25f, 0.25f, 1, 0.5f).end().
						texture("#all").
				end().
				element().
						from(1, 16, 9).
						to(2, 20, 10).
						face(Direction.NORTH).uvs(0.75f, 0.5f, 1, 1.5f).end().
						face(Direction.EAST).uvs(0.5f, 0.5f, 0.75f, 1.5f).end().
						face(Direction.SOUTH).uvs(1.25f, 0.5f, 1.5f, 1.5f).end().
						face(Direction.WEST).uvs(1, 0.5f, 1.25f, 1.5f).end().
						face(Direction.UP).uvs(1, 0.5f, 0.75f, 0.25f).end().
						face(Direction.DOWN).uvs(1.25f, 0.25f, 1, 0.5f).end().
						texture("#all").
				end().
				element().
						from(1, 17, 3).
						to(2, 20, 4).
						face(Direction.NORTH).uvs(0.75f, 0.5f, 1, 1.25f).end().
						face(Direction.EAST).uvs(0.5f, 0.5f, 0.75f, 1.25f).end().
						face(Direction.SOUTH).uvs(1.25f, 0.5f, 1.5f, 1.25f).end().
						face(Direction.WEST).uvs(1, 0.5f, 1.25f, 1.25f).end().
						face(Direction.UP).uvs(1, 0.5f, 0.75f, 0.25f).end().
						face(Direction.DOWN).uvs(1.25f, 0.25f, 1, 0.5f).end().
						texture("#all").
				end().
				element().
						from(1, 17, 9).
						to(2, 20, 10).
						face(Direction.NORTH).uvs(0.75f, 0.5f, 1, 1.25f).end().
						face(Direction.EAST).uvs(0.5f, 0.5f, 0.75f, 1.25f).end().
						face(Direction.SOUTH).uvs(1.25f, 0.5f, 1.5f, 1.25f).end().
						face(Direction.WEST).uvs(1, 0.5f, 1.25f, 1.25f).end().
						face(Direction.UP).uvs(1, 0.5f, 0.75f, 0.25f).end().
						face(Direction.DOWN).uvs(1.25f, 0.25f, 1, 0.5f).end().
						texture("#all").
				end().
				element().
						from(1, 17, 12).
						to(2, 20, 13).
						face(Direction.NORTH).uvs(0.75f, 0.5f, 1, 1.25f).end().
						face(Direction.EAST).uvs(0.5f, 0.5f, 0.75f, 1.25f).end().
						face(Direction.SOUTH).uvs(1.25f, 0.5f, 1.5f, 1.25f).end().
						face(Direction.WEST).uvs(1, 0.5f, 1.25f, 1.25f).end().
						face(Direction.UP).uvs(1, 0.5f, 0.75f, 0.25f).end().
						face(Direction.DOWN).uvs(1.25f, 0.25f, 1, 0.5f).end().
						texture("#all").
				end().
				element().
						from(1, 17, 6).
						to(2, 20, 7).
						face(Direction.NORTH).uvs(0.75f, 0.5f, 1, 1.25f).end().
						face(Direction.EAST).uvs(0.5f, 0.5f, 0.75f, 1.25f).end().
						face(Direction.SOUTH).uvs(1.25f, 0.5f, 1.5f, 1.25f).end().
						face(Direction.WEST).uvs(1, 0.5f, 1.25f, 1.25f).end().
						face(Direction.UP).uvs(1, 0.5f, 0.75f, 0.25f).end().
						face(Direction.DOWN).uvs(1.25f, 0.25f, 1, 0.5f).end().
						texture("#all").
				end().
				element().
						from(14, 17, 12).
						to(15, 20, 13).
						face(Direction.NORTH).uvs(1, 0.5f, 0.75f, 1.25f).end().
						face(Direction.EAST).uvs(1.25f, 0.5f, 1, 1.25f).end().
						face(Direction.SOUTH).uvs(1.5f, 0.5f, 1.25f, 1.25f).end().
						face(Direction.WEST).uvs(0.75f, 0.5f, 0.5f, 1.25f).end().
						face(Direction.UP).uvs(0.75f, 0.5f, 1, 0.25f).end().
						face(Direction.DOWN).uvs(1, 0.25f, 1.25f, 0.5f).end().
						texture("#all").
				end().
				element().
						from(14, 17, 6).
						to(15, 20, 7).
						face(Direction.NORTH).uvs(1, 0.5f, 0.75f, 1.25f).end().
						face(Direction.EAST).uvs(1.25f, 0.5f, 1, 1.25f).end().
						face(Direction.SOUTH).uvs(1.5f, 0.5f, 1.25f, 1.25f).end().
						face(Direction.WEST).uvs(0.75f, 0.5f, 0.5f, 1.25f).end().
						face(Direction.UP).uvs(0.75f, 0.5f, 1, 0.25f).end().
						face(Direction.DOWN).uvs(1, 0.25f, 1.25f, 0.5f).end().
						texture("#all").
				end().
				element().
						from(14, 17, 3).
						to(15, 20, 4).
						face(Direction.NORTH).uvs(1, 0.5f, 0.75f, 1.25f).end().
						face(Direction.EAST).uvs(1.25f, 0.5f, 1, 1.25f).end().
						face(Direction.SOUTH).uvs(1.5f, 0.5f, 1.25f, 1.25f).end().
						face(Direction.WEST).uvs(0.75f, 0.5f, 0.5f, 1.25f).end().
						face(Direction.UP).uvs(0.75f, 0.5f, 1, 0.25f).end().
						face(Direction.DOWN).uvs(1, 0.25f, 1.25f, 0.5f).end().
						texture("#all").
				end().
				element().
						from(14, 17, 9).
						to(15, 20, 10).
						face(Direction.NORTH).uvs(1, 0.5f, 0.75f, 1.25f).end().
						face(Direction.EAST).uvs(1.25f, 0.5f, 1, 1.25f).end().
						face(Direction.SOUTH).uvs(1.5f, 0.5f, 1.25f, 1.25f).end().
						face(Direction.WEST).uvs(0.75f, 0.5f, 0.5f, 1.25f).end().
						face(Direction.UP).uvs(0.75f, 0.5f, 1, 0.25f).end().
						face(Direction.DOWN).uvs(1, 0.25f, 1.25f, 0.5f).end().
						texture("#all").
				end();
		
		horizontalBlock(block, model);
		
		itemModels().getBuilder(itemPrefix(name(block))).
						parent(model);
	}
	
	private void createFluidTransmitter()
	{
		BioFluidTransmitterBlock block = Registration.BlockReg.FLUID_TRANSMITTER.get();
		ResourceLocation blockTexture = blockTexture(block);
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", blockTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(4,0, 4).
						to(12, 2, 12).
						face(Direction.NORTH).
								uvs(4, 0, 8, 2).
						end().
						face(Direction.EAST).
								uvs(4, 2, 8, 4).
						end().
						face(Direction.SOUTH).
								uvs(4, 4, 8, 6).
						end().
						face(Direction.WEST).
								uvs(4, 8, 0, 0).
						end().
						face(Direction.UP).
								uvs(4, 8, 0, 0).
						end().
						face(Direction.DOWN).
								uvs(4, 0, 0, 8).
								rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).
						end().
						texture("#all").
				end().
				element().
						from(12, 0, 4).
						to(14, 1, 12).
						face(Direction.NORTH).
								uvs(14.5f, 0, 15.5f, 1).
						end().
						face(Direction.EAST).
								uvs(8.5f, 6, 12.5f, 7).
						end().
						face(Direction.SOUTH).
								uvs(13, 0, 14, 1).
						end().
						face(Direction.WEST).
								uvs(8.5f, 8, 12.5f, 9).
						end().
						face(Direction.UP).
								uvs(12.5f, 4.75f, 8.5f, 3).
								rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).
						end().
						face(Direction.DOWN).
								uvs(8.5f, 2, 12.5f, 0).
								rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).
						end().
						texture("#all").
				end().
				element().
						from(2, 0, 4).
						to(4, 1, 12).
						face(Direction.NORTH).uvs(13, 0, 14, 1).end().
						face(Direction.EAST).uvs(8.5f, 6, 12.5f, 7).end().
						face(Direction.SOUTH).uvs(14.5f, 0, 15.5f, 1).end().
						face(Direction.WEST).uvs(8.5f, 8, 12.5f, 9).end().
						face(Direction.UP).uvs(12.5f, 5, 8.5f, 3).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						face(Direction.DOWN).uvs(8.5f, 2, 12.5f, 0).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
						texture("#all").
				end().
				element().
						from(4, 0, 2).
						to(12, 1, 4).
						face(Direction.NORTH).uvs(8.5f, 8, 12.5f, 9).end().
						face(Direction.EAST).uvs(13, 0, 14, 1).end().
						face(Direction.SOUTH).uvs(8.5f, 6, 12.5f, 7).end().
						face(Direction.WEST).uvs(14.5f, 0, 15.5f, 1).end().
						face(Direction.UP).uvs(12.5f, 5, 8.5f, 3).end().
						face(Direction.DOWN).uvs(12.5f, 0, 8.5f, 2).end().
						texture("#all").
				end().
				element().
						from(4, 0, 12).
						to(12, 1, 14).
						face(Direction.NORTH).uvs(8.5f, 6, 12.5f, 7).end().
						face(Direction.EAST).uvs(13, 0, 14, 1).end().
						face(Direction.SOUTH).uvs(8.5f, 8, 12.5f, 9).end().
						face(Direction.WEST).uvs(14.5f, 0, 15.5f, 1).end().
						face(Direction.UP).uvs(12.5f, 5, 8.5f, 3).end().
						face(Direction.DOWN).uvs(12.5f, 0, 8.5f, 2).end().
						texture("#all").
				end().
				element().
						from(5, 2, 10).
						to(6, 3, 11).
						face(Direction.NORTH).uvs(3, 8, 3.5f,9).end().
						face(Direction.EAST).uvs(3.5f, 8, 4, 9).end().
						face(Direction.SOUTH).uvs(4, 8, 4.5f, 9).end().
						face(Direction.WEST).uvs(4.5f, 8, 5, 9).end().
						face(Direction.UP).uvs(5.5f, 9, 5, 8).end().
						face(Direction.DOWN).uvs(6, 8, 5.5f, 9).end().
						texture("#all").
				end().
				element().
						from(11, 2, 8).
						to(12, 3, 9).
						face(Direction.NORTH).uvs(3, 8, 3.5f, 9).end().
						face(Direction.EAST).uvs(3.5f, 8, 4, 9).end().
						face(Direction.SOUTH).uvs(4, 8, 4.5f, 9).end().
						face(Direction.WEST).uvs(4.5f, 8, 5, 9).end().
						face(Direction.UP).uvs(5.5f, 9, 5, 8).end().
						face(Direction.DOWN).uvs(6, 8, 5.5f, 9).end().
						texture("#all").
				end().
				element().
						from(6, 2, 4).
						to(7, 3, 5).
						face(Direction.NORTH).uvs(3, 8, 3.5f, 9).end().
						face(Direction.EAST).uvs(3.5f, 8, 4, 9).end().
						face(Direction.SOUTH).uvs(4, 8, 4.5f, 9).end().
						face(Direction.WEST).uvs(4.5f, 8, 5, 9).end().
						face(Direction.UP).uvs(5.5f, 9, 5, 8).end().
						face(Direction.DOWN).uvs(6, 8, 5.5f, 9).end().
						texture("#all").
				end().
				element().
						from(4, 2, 6).
						to(6, 4, 8).
						face(Direction.NORTH).uvs(3, 10, 4, 12).end().
						face(Direction.EAST).uvs(4.5f, 12, 5.5f, 10).end().
						face(Direction.SOUTH).uvs(3, 12, 4, 14).end().
						face(Direction.WEST).uvs(4.5f, 14, 5.5f, 12).end().
						face(Direction.UP).uvs(4, 16, 3, 14).end().
						face(Direction.DOWN).uvs(5.5f, 16, 4.5f, 14).end().
						texture("#all").
				end().
				element().
						from(8, 2, 10).
						to(10, 4, 12).
						face(Direction.NORTH).uvs(6, 10, 7, 12).end().
						face(Direction.EAST).uvs(6, 12, 7, 14).end().
						face(Direction.SOUTH).uvs(6, 14, 7, 16).end().
						face(Direction.WEST).uvs(7.5f, 10, 8.5f, 12).end().
						face(Direction.UP).uvs(8.5f, 14, 7.5f, 12).end().
						face(Direction.DOWN).uvs(8.5f, 14, 7.5f, 16).end().
						texture("#all").
				end().
				element().
						from(8, 2, 4).
						to(11, 5, 7).
						face(Direction.NORTH).uvs(12.5f, 2, 14, 5).end().
						face(Direction.EAST).uvs(14.5f, 2, 16, 5).end().
						face(Direction.SOUTH).uvs(12.5f, 5, 14, 8).end().
						face(Direction.WEST).uvs(14.5f, 5, 16, 8).end().
						face(Direction.UP).uvs(14, 11, 12.5f, 8).end().
						face(Direction.DOWN).uvs(16, 8, 14.5f, 11).end().
						texture("#all").
				end().
				element().
						from(3, 1, 9).
						to(4, 4, 10).
						face(Direction.NORTH).uvs(0, 9, 0.5f, 12).end().
						face(Direction.EAST).uvs(0.5f, 9, 1, 12).end().
						face(Direction.SOUTH).uvs(1, 9, 1.5f, 12).end().
						face(Direction.WEST).uvs(0, 12, 0.5f, 15).end().
						face(Direction.UP).uvs(2, 10, 1.5f, 9).end().
						face(Direction.DOWN).uvs(1, 12, 0.5f, 13).end().
						texture("#all").
				end().
				element().
						from(9, 1, 3).
						to(10, 4, 4).
						face(Direction.NORTH).uvs(0, 9, 0.5f, 12).end().
						face(Direction.EAST).uvs(0.5f, 9, 1, 12).end().
						face(Direction.SOUTH).uvs(1, 9, 1.5f, 12).end().
						face(Direction.WEST).uvs(0, 12, 0.5f, 15).end().
						face(Direction.UP).uvs(2, 10, 1.5f, 9).end().
						face(Direction.DOWN).uvs(1, 12, 0.5f, 13).end().
						texture("#all").
				end().
				element().
						from(5, 1, 3).
						to(6, 4, 4).
						face(Direction.NORTH).uvs(0, 9, 0.5f, 12).end().
						face(Direction.EAST).uvs(0.5f, 9, 1, 12).end().
						face(Direction.SOUTH).uvs(1, 9, 1.5f, 12).end().
						face(Direction.WEST).uvs(0, 12, 0.5f, 15).end().
						face(Direction.UP).uvs(2, 10, 1.5f, 9).end().
						face(Direction.DOWN).uvs(1, 12, 0.5f, 13).end().
						texture("#all").
				end().
				element().
						from(12, 1, 5).
						to(13, 4, 6).
						face(Direction.NORTH).uvs(0, 9, 0.5f, 12).end().
						face(Direction.EAST).uvs(0.5f, 9, 1, 12).end().
						face(Direction.SOUTH).uvs(1, 9, 1.5f, 12).end().
						face(Direction.WEST).uvs(0, 12, 0.5f, 15).end().
						face(Direction.UP).uvs(2, 10, 1.5f, 9).end().
						face(Direction.DOWN).uvs(1, 12, 0.5f, 13).end().
						texture("#all").
				end().
				element().
						from(12, 1, 10).
						to(13, 4, 11).
						face(Direction.NORTH).uvs(0, 9, 0.5f, 12).end().
						face(Direction.EAST).uvs(0.5f, 9, 1, 12).end().
						face(Direction.SOUTH).uvs(1, 9, 1.5f, 12).end().
						face(Direction.WEST).uvs(0, 12, 0.5f, 15).end().
						face(Direction.UP).uvs(2, 10, 1.5f, 9).end().
						face(Direction.DOWN).uvs(1, 12, 0.5f, 13).end().
						texture("#all").
				end().
				element().
						from(6, 1, 12).
						to(7, 4, 13).
						face(Direction.NORTH).uvs(0, 9, 0.5f, 12).end().
						face(Direction.EAST).uvs(0.5f, 9, 1, 12).end().
						face(Direction.SOUTH).uvs(1, 9, 1.5f, 12).end().
						face(Direction.WEST).uvs(0, 12, 0.5f, 15).end().
						face(Direction.UP).uvs(2, 10, 1.5f, 9).end().
						face(Direction.DOWN).uvs(1, 12, 0.5f, 13).end().
						texture("#all").
				end();
		
		getVariantBuilder(block).forAllStates(state ->
		{
			Direction facing = state.getValue(BioFluidTransmitterBlock.FACING);
			
			return ConfiguredModel.builder().
					modelFile(model).
					rotationY(facing.getAxis().isVertical() ? 0 : (facing.get2DDataValue() * 90 + 180) % 360).
					rotationX(facing == Direction.DOWN ? 0 : facing == Direction.UP ? 180 : 270).
					build();
		});
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
	}
	
	private void createFluidStorage()
	{
		BioFluidStorageBlock block = Registration.BlockReg.FLUID_STORAGE.get();
		ResourceLocation blockTexture = blockTexture(block);
		ResourceLocation portTexture = Database.rl(blockPrefix("port"));
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", blockTexture).
				texture("port", portTexture).
				texture("particle", blockTexture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(3, 2, 3).
						to(13, 14, 13).
				allFacesExcept((direction, faceBuilder) ->
						faceBuilder.uvs(7.5f, 0, 10, 6).
						texture("#all"),
						EnumSet.of(Direction.UP, Direction.DOWN)).
				end().
				element().
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
							faceBuilder.texture("#all");
						}).
				end().
				element().
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
							faceBuilder.texture("#all");
						}).
				end().
				element().
						from(2,2,2).
						to(4,5, 4).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 3, 10.25f, 2);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.DOWN)).
				end().
				element().
						from(2,11,2).
						to(4,14, 4).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 2, 10.25f, 3);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.UP)).
				end().
				element().
						from(2,11,12).
						to(4,14, 14).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 2, 10.25f, 3);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.UP)).
				end().
				element().
						from(2,2,12).
						to(4,5, 14).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 3, 10.25f, 2);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.DOWN)).
				end().
				element().
						from(12,11,12).
						to(14,14, 14).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 2, 10.25f, 3);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.UP)).
				end().
				element().
						from(12,2,12).
						to(14,5, 14).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 3, 10.25f, 2);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.DOWN)).
				end().
				element().
						from(12,11,2).
						to(14,14, 4).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 2, 10.25f, 3);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.UP)).
				end().
				element().
						from(12,2,2).
						to(14,5, 4).
						allFacesExcept((direction, faceBuilder) ->
						{
							if (direction.getAxis().isHorizontal())
								faceBuilder.uvs(10.25f, 0, 10.75f, 1.5f);
							else
								faceBuilder.uvs(10.75f, 3, 10.25f, 2);
							faceBuilder.texture("#all");
						}, EnumSet.of(Direction.DOWN)).
				end().
				element().
						from(1,0f,1).
						to(15, 0f, 15).
						allFacesExcept((direction, faceBuilder) ->
								faceBuilder.
										uvs(1,1,15,15).
										texture("#port").
										tintindex(1).
										cullface(direction),
								EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP)).
				end().
				element().
						from(1,16,1).
						to(15, 16, 15).
						allFacesExcept((direction, faceBuilder) ->
								faceBuilder.
										uvs(1,1,15,15).
										texture("#port").
										tintindex(1).
										cullface(direction),
										EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN)).
				end();
		
		registerModels(block, model);
	}
	
	private void createFluidModel(Registration.FluidReg.FluidEntry fluid)
	{
		LiquidBlock block = fluid.block().get();
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("water"))).
				renderType(RenderType.translucent().name).
				texture("particle", fluid.still().getId().withPrefix("block/")).
				guiLight(BlockModel.GuiLight.SIDE);
		
		registerModels(block, model);
	}
	
	private void createNorphStairs()
	{
		NorphStairs block = Registration.BlockReg.NORPH_STAIRS.get();
		
		ModelFile[] innerModels = new ModelFile[8];
		ModelFile[] straightModels = new ModelFile[8];
		ModelFile[] outerModels = new ModelFile[8];
		
		ResourceLocation texture = blockTexture(block.base);
		for (int q = 0; q < 8; q++)
		{
			ResourceLocation localTexture = texture.withSuffix("_" + q);
			
			innerModels[q] = models().stairsInner(blockPrefix(name(block)) + "_inner_" + q, localTexture, localTexture, localTexture);
			straightModels[q] = models().stairs(blockPrefix(name(block)) + "_" + q, localTexture, localTexture, localTexture);
			outerModels[q] = models().stairsOuter(blockPrefix(name(block)) + "_outer_" + q, localTexture, localTexture, localTexture);
		}
		
		getVariantBuilder(block).
				forAllStatesExcept(state ->
				{
					Direction facing = state.getValue(StairBlock.FACING);
					Half half = state.getValue(StairBlock.HALF);
					StairsShape shape = state.getValue(StairBlock.SHAPE);
					int yRot = (int) facing.getClockWise().toYRot();
					if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
						yRot += 270;
					if (shape != StairsShape.STRAIGHT && half == Half.TOP)
						yRot += 90;
					yRot %= 360;
					boolean uvlock = yRot != 0 || half == Half.TOP;
					
					ConfiguredModel[] models = new ConfiguredModel[8];
					for (int q = 0; q < 8; q++)
					{
						models[q] = ConfiguredModel.builder().
								modelFile(shape == StairsShape.STRAIGHT ? straightModels[q] :
								shape == StairsShape.INNER_LEFT ||
										shape == StairsShape.INNER_RIGHT ? innerModels[q]
										: outerModels[q]).
								rotationX(half == Half.BOTTOM ? 0 : 180).
								rotationY(yRot).
								uvLock(uvlock).
								buildLast();
					}
					
					return models;
				}, StairBlock.WATERLOGGED);
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(straightModels[0]);
	}
	
	private void createNorphOverlay()
	{
		NorphOverlay block = Registration.BlockReg.NORPH_OVERLAY.get();
		
		ModelFile[] models = new ModelFile[8];
		ResourceLocation texture = blockTexture(block);
		
		for (int q = 0; q < 8; q++)
		{
			ResourceLocation localTexture = texture.withSuffix("_" + q);
			
			ModelFile model = models().withExistingParent(blockPrefix(name(block)) + "_" + q, mcLoc(blockPrefix("block"))).
					texture("all", localTexture).
					texture("particle", localTexture).
					renderType(RenderType.translucent().name).
					guiLight(BlockModel.GuiLight.SIDE).
					element().
							from(0f,0f,0.1f).
							to(16f,16f,0.1f).
							face(Direction.NORTH).
									uvs(16f, 0f, 0f, 16f).
							end().
							face(Direction.SOUTH).
									uvs(0f,0f, 16f, 16f).
							end().
							texture("#all").
					end();
			
			models[q] = model;
		}
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
		
		PipeBlock.PROPERTY_BY_DIRECTION.
				forEach((dir, value) ->
				{
					ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> modelBuilder = builder.part();
					for (int q = 0; q < 8; q++)
					{
						modelBuilder.
								modelFile(models[q]).
								rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? 270 : 0).
								rotationY(dir.getAxis().isVertical() ? 0 : ((int)dir.toYRot() + 180)  % 360).
								uvLock(dir != Direction.NORTH);
						if (q != 7)
							modelBuilder = modelBuilder.nextModel();
					}
					modelBuilder.addModel().condition(value, true);
				});
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(models[0]);
	}
	
	private void createNorphBlock()
	{
		NorphBlock block = Registration.BlockReg.NORPH.get();
		
		ModelFile[] models = new ModelFile[8];
		ResourceLocation texture = blockTexture(block);
		for (int q = 0; q < 8; q++)
		{
			ResourceLocation localTexture = texture.withSuffix("_" + q);
			
			ModelFile model = models().withExistingParent(blockPrefix(name(block)) + "_" + q, mcLoc(blockPrefix("block"))).
					texture("all", localTexture).
					texture("particle", localTexture).
					renderType(RenderType.solid().name).
					guiLight(BlockModel.GuiLight.SIDE).
					element().
							from(0, 0, 0).
							to(16, 16, 16).
					allFaces((direction, faceBuilder) ->
							faceBuilder.
									uvs(0, 0, 16, 16).
									cullface(direction)).
					texture("#all").
					end();
			
			models[q] = model;
		}
		
		getVariantBuilder(block).partialState().
				addModels(Arrays.stream(models).map(ConfiguredModel :: new).toArray(ConfiguredModel[] :: new));
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(models[0]);
	}
	
	private void createNorphSource()
	{
		NorphSourceBlock block = Registration.BlockReg.NORPH_SOURCE.get();
		
		ResourceLocation blockText = blockTexture(block).withSuffix("/main");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				texture("all", blockText).
				texture("particle", blockText).
				renderType(RenderType.translucent().name).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(4, 7.005f, 5).
						to(11, 14.005f, 11).
						face(Direction.NORTH).
								uvs(6.5f, 6.75f, 8.25f, 8.5f).
						end().
						face(Direction.EAST).
								uvs(5, 7.75f, 6.5f, 9.5f).
						end().
						face(Direction.SOUTH).
								uvs(7, 2, 8.75f, 3.75f).
						end().
						face(Direction.WEST).
								uvs(8.25f, 5.5f, 9.75f, 7.25f).
						end().
						face(Direction.UP).
								uvs(10, 8.75f, 8.25f, 7.25f).
						end().
						face(Direction.DOWN).
								uvs(10.25f, 0, 8.5f, 1.5f).
						end().
						texture("#all").
				end().
				element().
						from(2, -1.995f, 6).
						to(9, 8.005f, 10).
						rotation().
								angle(-22.5f).
								axis(Direction.Axis.Z).
								origin(5.5f, 3.005f, 8).
						end().
						face(Direction.NORTH).
								uvs(3.5f, 0, 5.25f, 2.5f).
						end().
						face(Direction.EAST).
								uvs(1.5f, 8.5f, 2.5f, 11).
						end().
						face(Direction.SOUTH).
								uvs(3.5f, 2.5f, 5.25f, 5).
						end().
						face(Direction.WEST).
								uvs(6.5f, 8.5f, 7.5f, 11).
						end().
						face(Direction.UP).
								uvs(11.5f, 3.5f, 9.75f, 2.5f).
						end().
						face(Direction.DOWN).
								uvs(11.5f, 5.5f, 9.75f, 6.5f).
						end().
						texture("#all").
				end().
				element().
						from(6, -1.995f, 7).
						to(13, 2.005f, 15).
						rotation().
								angle(45).
								axis(Direction.Axis.X).
								origin(10.5f, 0.005f, 12.5f).
						end().
						face(Direction.NORTH).
								uvs(10, 3.5f, 11.75f, 4.5f).
						end().
						face(Direction.EAST).
								uvs(4.25f, 9.5f, 6.25f, 10.5f).
						end().
						face(Direction.SOUTH).
								uvs(10, 4.5f, 11.75f, 5.5f).
						end().
						face(Direction.WEST).
								uvs(9.75f, 1.5f, 11.75f, 2.5f).
						end().
						face(Direction.UP).
								uvs(8.25f, 6.75f, 6.5f, 4.75f).
						end().
						face(Direction.DOWN).
								uvs(8.5f, 0, 6.75f, 2).
						end().
						texture("#all").
				end().
				element().
						from(6, -2.93434f, 2.43934f).
						to(13, 5.06566f, 6.43934f).
						rotation().
								angle(45).
								axis(Direction.Axis.X).
								origin(10, 1.06566f, 4.43934f).
						end().
						face(Direction.NORTH).
								uvs(5.25f, 2.75f, 7, 4.75f).
						end().
						face(Direction.EAST).
								uvs(8.75f, 1.5f, 9.75f, 3.5f).
						end().
						face(Direction.SOUTH).
								uvs(1.5f, 6.5f, 3.25f, 8.5f).
						end().
						face(Direction.WEST).
								uvs(3.25f, 9.5f, 4.25f, 11.5f).
						end().
						face(Direction.UP).
								uvs(11.5f, 9.75f, 9.75f, 8.75f).
						end().
						face(Direction.DOWN).
								uvs(11.5f, 9.75f, 9.75f, 10.75f).
						end().
						texture("#all").
				end().
				element().
						from(7.64645f, -1.995f, 4.49645f).
						to(14.64645f, 11.005f, 11.49645f).
						rotation().
								angle(22.5f).
								axis(Direction.Axis.Z).
								origin(11.14645f, 2.505f, 7.99645f).
						end().
						face(Direction.NORTH).
								uvs(0, 0, 1.75f, 3.25f).
						end().
						face(Direction.EAST).
								uvs(1.75f, 0, 3.5f, 3.25f).
						end().
						face(Direction.SOUTH).
								uvs(0, 3.25f, 1.75f, 6.5f).
						end().
						face(Direction.WEST).
								uvs(1.75f, 3.25f, 3.5f, 6.5f).
						end().
						face(Direction.UP).
								uvs(5, 9.5f, 3.25f, 7.75f).
						end().
						face(Direction.DOWN).
								uvs(10, 3.75f, 8.25f, 5.5f).
						end().
						texture("#all").
				end().
				element().
						from(3.25f, 0.005f, 8.5f).
						to(6.25f, 11.005f, 14.25f).
						rotation().
								angle(-45).
								axis(Direction.Axis.Y).
								origin(5.25f, -2, 11.5f).
						end().
						face(Direction.NORTH).
								uvs(7.5f, 8.5f, 8.25f, 11.25f).
						end().
						face(Direction.EAST).
								uvs(3.5f, 5, 5, 7.75f).
						end().
						face(Direction.SOUTH).
								uvs(8.25f, 8.75f, 9, 11.5f).
						end().
						face(Direction.WEST).
								uvs(5, 5, 6.5f, 7.75f).
						end().
						face(Direction.UP).
								uvs(1.5f, 10.75f, 0.75f, 9.25f).
						end().
						face(Direction.DOWN).
								uvs(10.75f, 6.5f, 10, 8).
						end().
						texture("#all").
				end().
				element().
						from(1.75f, 0.005f, 3.5f).
						to(7.5f, 11.005f, 6.5f).
						rotation().
								angle(-45).
								axis(Direction.Axis.Y).
								origin(4.5f, -2, 5.5f).
						end().
						face(Direction.NORTH).
								uvs(5.25f, 0, 6.75f, 2.75f).
						end().
						face(Direction.EAST).
								uvs(9, 8.75f, 9.75f, 11.5f).
						end().
						face(Direction.SOUTH).
								uvs(0, 6.5f, 1.5f, 9.25f).
						end().
						face(Direction.WEST).
								uvs(0, 9.25f, 0.75f, 12).
						end().
						face(Direction.UP).
								uvs(11.5f, 8.75f, 10, 8).
						end().
						face(Direction.DOWN).
								uvs(11.75f, 0, 10.25f, 0.75f).
						end().
						texture("#all").
				end().
				element().
						from(2, 8, 12).
						to(4, 10, 15).
						rotation().
								angle(45).
								axis(Direction.Axis.Y).
								origin(3, 8, 14).
						end().
						face(Direction.NORTH).
								uvs(10.75f, 7, 11.25f, 7.5f).
						end().
						face(Direction.EAST).
								uvs(4.25f, 10.5f, 5, 11).
						end().
						face(Direction.SOUTH).
								uvs(10.75f, 7.5f, 11.25f, 8).
						end().
						face(Direction.WEST).
								uvs(5, 10.5f, 5.75f, 11).
						end().
						face(Direction.UP).
								uvs(6.25f, 11.25f, 5.75f, 10.5f).
						end().
						face(Direction.DOWN).
								uvs(1.25f, 10.75f, 0.75f, 11.5f).
						end().
						texture("#all").
				end().
				element().
						from(1, 8, 1).
						to(3, 10, 4).
						rotation().
								angle(-45).
								axis(Direction.Axis.Y).
								origin(2, 8, 3).
						end().
						face(Direction.NORTH).
								uvs(10.75f, 0.75f, 11.25f, 1.25f).
						end().
						face(Direction.EAST).
								uvs(7, 3.75f, 7.75f, 4.25f).
						end().
						face(Direction.SOUTH).
								uvs(10.75f, 6.5f, 11.25f, 7).
						end().
						face(Direction.WEST).
								uvs(7, 4.25f, 7.75f, 4.75f).
						end().
						face(Direction.UP).
								uvs(8.25f, 4.5f, 7.75f, 3.75f).
						end().
						face(Direction.DOWN).
								uvs(10.75f, 0.75f, 10.25f, 1.5f).
						end().
						texture("#all").
				end().
				element().
						from(2.9f, -4f, 13).
						to(2.9f, 9, 16).
						rotation().
								angle(45).
								axis(Direction.Axis.Y).
								origin(1.9f, 7, 15).
						end().
						face(Direction.EAST).
								uvs(15.25f, 0, 16, 3).
						end().
						face(Direction.WEST).
								uvs(15.25f, 0, 16, 3).
						end().
						texture("#all").
				end().
				element().
						from(1.9f, -4f, 0).
						to(1.9f, 9, 3).
						rotation().
								angle(-45).
								axis(Direction.Axis.Y).
								origin(0.9f, 7, 2).
						end().
						face(Direction.EAST).
								uvs(15.25f, 0, 16, 3).
						end().
						face(Direction.WEST).
								uvs(15.25f, 0, 16, 3).
						end().
						texture("#all").
				end();
		
		horizontalBlock(block, model);
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
	}
	
	private void createLureCampfireModel()
	{
		Block block = Registration.BlockReg.LURE_CAMPFIRE.get();
		ResourceLocation texture = blockTexture(block).withSuffix("/main");
		
		ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
				renderType(RenderType.translucent().name).
				texture("all", texture).
				texture("particle", texture).
				guiLight(BlockModel.GuiLight.SIDE).
				element().
						from(2, 0, 3).
						to(14, 2, 6).
							face(Direction.NORTH).
								uvs(0.75f, 13.5f, 3.75f, 14.5f).
							end().
						face(Direction.EAST).
								uvs(0, 13.5f, 0.75f, 14.5f).
						end().
						face(Direction.SOUTH).
								uvs(4.5f, 13.5f, 7.5f, 14.5f).
						end().
						face(Direction.WEST).
								uvs(3.75f, 13.5f, 4.5f, 14.5f).
						end().
						face(Direction.UP).
								uvs(3.75f, 13.5f, 0.75f, 12).
						end().
						face(Direction.DOWN).
								uvs(6.75f, 12, 3.75f, 13.5f).
								cullface(Direction.DOWN).
						end().
						texture("#all").
				end().
				element().
						from(2, 0,10).
						to(14, 2, 13).
						face(Direction.NORTH).
								uvs(0.75f, 13.5f, 3.75f, 14.5f).
						end().
						face(Direction.EAST).
								uvs(0, 1.35f, 0.75f, 14.5f).
						end().
						face(Direction.SOUTH).
								uvs(4.5f, 13.5f, 7.5f, 14.5f).
						end().
						face(Direction.WEST).
								uvs(3.75f, 13.5f, 4.5f, 14.5f).
						end().
						face(Direction.UP).
								uvs(3.75f, 13.5f, 0.75f, 12).
						end().
						face(Direction.DOWN).
								uvs(6.75f, 12, 3.75f, 13.5f).
								cullface(Direction.DOWN).
							end().
						texture("#all").
				end().
				element().
						from(4,2,2).
						to(7, 4, 14).
						face(Direction.NORTH).
								uvs(3, 6, 3.75f, 7).
						end().
						face(Direction.EAST).
								uvs(0, 6, 3, 7).
						end().
						face(Direction.SOUTH).
								uvs(6.75f, 6, 7.5f, 7).
						end().
						face(Direction.WEST).
								uvs(3.75f, 6, 6.75f, 7).
						end().
						face(Direction.UP).
								uvs(3.75f, 6, 3, 0).
						end().
						face(Direction.DOWN).
								uvs(4.5f, 0, 3.75f, 6).
						end().
						texture("#all").
				end().
				element().
						from(9, 2, 2).
						to(12, 4, 14).
						face(Direction.NORTH).
								uvs(3, 6, 3.75f, 7).
						end().
						face(Direction.EAST).
								uvs(0, 6, 3, 7).
						end().
						face(Direction.SOUTH).
								uvs(6.75f, 6, 7.5f, 7).
						end().
						face(Direction.WEST).
								uvs(3.75f, 6, 6.75f, 7).
						end().
						face(Direction.UP).
								uvs(3.75f, 6, 3, 0).
						end().
						face(Direction.DOWN).
								uvs(4.5f, 0, 3.75f, 6).
						end().
						texture("#all").
				end().
				element().
						from(14, 0, 7).
						to(16, 16, 9).
						face(Direction.NORTH).
								uvs(14.5f, 1, 15, 10).
						end().
						face(Direction.EAST).
								uvs(14, 1, 14.5f, 10).
								cullface(Direction.EAST).
						end().
						face(Direction.SOUTH).
								uvs(15.5f, 1, 16, 10).
						end().
						face(Direction.WEST).
								uvs(15, 1, 15.5f, 10).
						end().
						face(Direction.UP).
								uvs(15, 1, 14.5f, 0).
								cullface(Direction.UP).
						end().
						face(Direction.DOWN).
								uvs(15.5f, 0, 15, 1).
								cullface(Direction.DOWN).
						end().
						texture("#all").
				end().
				element().
						from(0, 0, 7).
						to(2, 16, 9).
						face(Direction.NORTH).
								uvs(14.5f, 1, 15, 10).
						end().
						face(Direction.EAST).
								uvs(14, 1, 14.5f, 10).
								cullface(Direction.EAST).
						end().
						face(Direction.SOUTH).
								uvs(15.5f, 1, 16, 10).
						end().
						face(Direction.WEST).
								uvs(15, 1, 15.5f, 10).
						end().
						face(Direction.UP).
								uvs(15, 1, 14.5f, 0).
								cullface(Direction.UP).
						end().
						face(Direction.DOWN).
								uvs(15.5f, 0, 15, 1).
								cullface(Direction.DOWN).
						end().
						texture("#all").
				end().
				element().
						from(2, 0, 6).
						to(14, 1, 10).
						face(Direction.NORTH).
								uvs(2.25f, 9.5f, 4, 10).
						end().
						face(Direction.EAST).
								uvs(0, 9.5f, 4, 10).
						end().
						face(Direction.SOUTH).
								uvs(6.25f, 9.5f, 8, 10).
						end().
						face(Direction.WEST).
								uvs(4, 9.5f, 1, 7.5f).
						end().
						face(Direction.UP).
								uvs(4, 9.5f, 1, 7.5f).
						end().
						face(Direction.DOWN).
								uvs(7, 7.5f, 4, 9.5f).
								cullface(Direction.DOWN).
						end().
						texture("#all").
				end().
				element().
						from(-1, 14, 7.5f).
						to(14, 15, 8.5f).
						face(Direction.NORTH).
								uvs(0.25f, 11, 4, 11.5f).
						end().
						face(Direction.EAST).
								uvs(0, 11, 0.25f, 11.5f).
						end().
						face(Direction.SOUTH).
								uvs(4.25f, 11, 8, 11.5f).
						end().
						face(Direction.WEST).
								uvs(4, 11, 4.25f, 11.5f).
								cullface(Direction.WEST).
						end().
						face(Direction.UP).
								uvs(4, 11, 0.25f, 10.5f).
						end().
						face(Direction.DOWN).
								uvs(7.75f, 10.5f, 4, 11).
						end().
						texture("#all").
				end().
				element().
						from(-2, 10, 7.5f).
						to(-1, 15, 8.5f).
						face(Direction.NORTH).
								uvs(5, 0.5f, 5.25f, 3).
						end().
						face(Direction.EAST).
								uvs(4.75f, 0.5f, 5, 3).
						end().
						face(Direction.SOUTH).
								uvs(5.5f, 0.5f, 5.75f, 3).
						end().
						face(Direction.WEST).
								uvs(5.25f, 0.5f, 5.5f, 3).
								cullface(Direction.WEST).
						end().
						face(Direction.UP).
								uvs(5.25f, 0.5f, 5, 0).
						end().
						face(Direction.DOWN).
								uvs(5.5f, 0, 5.25f, 0.5f).
						end().
						texture("#all").
				end().
				element().
						from(-4, 10, 7.5f).
						to(-2, 11, 8.5f).
						face(Direction.NORTH).
								uvs(6.25f, 0.5f, 6.75f, 1).
						end().
						face(Direction.EAST).
						uvs(6, 0.5f, 6.25f, 1).
						end().
						face(Direction.SOUTH).
								uvs(7, 0.5f, 7.5f, 1).
						end().
						face(Direction.WEST).
								uvs(6.75f, 0.5f, 7, 1).
								cullface(Direction.WEST).
						end().
						face(Direction.UP).
								uvs(6.75f, 0.5f, 6.25f, 0).
						end().
						face(Direction.DOWN).
								uvs(7.25f, 0, 6.25f, 0.5f).
						end().
						texture("#all").
				end();

		horizontalBlock(block, model);
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
	}
	
	private void registerSimpleBlock(Block block)
	{
		ModelFile model = models().
				withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("cube_all"))).
				renderType(RenderType.solid().name).
				texture("all", blockTexture(block)).
				texture("particle", blockTexture(block));
		
		registerModels(block, model);
	}
	
	private void registerModels(Block block, ModelFile model)
	{
		getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));
		
		itemModels().getBuilder(itemPrefix(name(block))).
				parent(model);
	}
	
	private String itemPrefix(String str)
	{
		return ModelProvider.ITEM_FOLDER + "/" + str;
	}
	
	private String blockPrefix(String str)
	{
		return ModelProvider.BLOCK_FOLDER + "/" + str;
	}
	
	private String name(Block block)
	{
		return BlockHelper.getRegistryName(block).getPath();
	}
	
	@Override
	public String getName()
	{
		return Database.MOD_NAME + " Block Models";
	}
}
