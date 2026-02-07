/**
 * @author ArcAnc
 * Created at: 30.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.builders.*;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.tags.base.BioItemTags;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BioRecipeProvider extends RecipeProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected BioRecipeProvider(HolderLookup.Provider registries, RecipeOutput output)
    {
        super(registries, output);
    }

    @Override
    protected void buildRecipes()
    {
        nineBlockStorageRecipes(RecipeCategory.MISC, Registration.ItemReg.FLESH_PIECE, RecipeCategory.MISC, Registration.BlockReg.FLESH);
        shapeless(RecipeCategory.MISC, Registration.ItemReg.BOOK).
                unlockedBy(getHasName(Registration.ItemReg.QUEENS_BRAIN), has(Registration.ItemReg.QUEENS_BRAIN)).
                unlockedBy(getHasName(Items.WRITABLE_BOOK), has(Items.WRITABLE_BOOK)).
                requires(Registration.ItemReg.QUEENS_BRAIN).
                requires(Items.WRITABLE_BOOK).
                save(this.output);

        shaped(RecipeCategory.MISC, Registration.BlockReg.NORPH_SOURCE).
                pattern(" R ").
                pattern("FHF").
                pattern("FSF").
                define('F', Registration.BlockReg.FLESH).
                define('H', Items.CREAKING_HEART).
                define('R', Tags.Items.BRICKS_RESIN).
                define('S', Tags.Items.STONES).
                unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
                unlockedBy(getHasName(Items.CREAKING_HEART), has(Items.CREAKING_HEART)).
                unlockedBy(getHasName(Items.RESIN_BRICK), has(Tags.Items.BRICKS_RESIN)).
                unlockedBy(getHasName(Items.STONE), has(Tags.Items.STONES)).
                save(this.output);

        StomachRecipeBuilder.newBuilder(
                new BioBaseRecipe.ResourcesInfo(
                    new BioBaseRecipe.BiomassInfo(false, 0.5f),
                    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 1.5f)),
                    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 0.5f)),
                    100)).
                setInput(new IngredientWithSize(tag(Tags.Items.FOODS))).
                setResult(new FluidStack(Registration.FluidReg.ACID.still(), 700)).
        unlockedBy("has_food", has(Tags.Items.FOODS)).
        save(this.output, Database.rl("acid_from_foods").toString());
	    
	    SqueezerRecipeBuilder.newBuilder(
			    new BioBaseRecipe.ResourcesInfo(
					    new BioBaseRecipe.BiomassInfo(false, 0.0f),
					    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(true, 0.25f, 1f)),
					    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 0.5f)),
					    100)).
			    setInput(new IngredientWithSize(tag(Tags.Items.FOODS))).
			    setResult(new FluidStack(Registration.FluidReg.BIOMASS.still(), 700)).
			    unlockedBy("has_food", has(Tags.Items.FOODS)).
			    save(this.output, Database.rl("biomass_from_foods").toString());

       

        shaped(RecipeCategory.MISC, Registration.BlockReg.MULTIBLOCK_MORPHER).
                pattern("SWS").
                pattern("FSF").
                define('W', tag(Tags.Items.BUCKETS_WATER)).
                define('S', tag(Tags.Items.STONES)).
                define('F', Registration.BlockReg.FLESH).
                unlockedBy(getHasName(Items.WATER_BUCKET), has(Tags.Items.BUCKETS_WATER)).
                unlockedBy(getHasName(Items.STONE), has(Tags.Items.STONES)).
                unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
                save(this.output);

		generateCrusherRecipes();
		generateChamberRecipes();
        generateBioForgeVanillaEnhancedRecipes();
    }
	
	private void generateChamberRecipes()
	{
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(Ingredient.of(Items.BUCKET))).
				addInput(new IngredientWithSize(tag(Tags.Items.BARRELS_WOODEN))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				setResult(new ItemStack(Registration.BlockReg.FLUID_STORAGE)).
				unlockedBy("has_" + Tags.Items.BARRELS_WOODEN.location().getPath(), has(Tags.Items.BARRELS_WOODEN)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				unlockedBy(getHasName(Items.BUCKET), has(Items.BUCKET)).
				save(this.output, Database.rl("fluid_storage_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(tag(Tags.Items.CHESTS))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				setResult(new ItemStack(Registration.BlockReg.CHEST)).
				unlockedBy(getHasName(Items.CHEST), has(Tags.Items.CHESTS)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				save(this.output, Database.rl("chest_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
				addInput(new IngredientWithSize(tag(Tags.Items.ENDER_PEARLS))).
				setResult(new ItemStack(Registration.BlockReg.FLUID_TRANSMITTER)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				unlockedBy(getHasName(Items.SLIME_BALL), has(Tags.Items.SLIME_BALLS)).
				unlockedBy(getHasName(Items.ENDER_PEARL), has(Tags.Items.ENDER_PEARLS)).
				save(this.output, Database.rl("fluid_transmitter_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Items.STONE))).
				addInput(new IngredientWithSize(Ingredient.of(Items.CHEST))).
				setResult(new ItemStack(Registration.BlockReg.STOMACH)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				unlockedBy(getHasName(Items.STONE), has(Items.STONE)).
				unlockedBy(getHasName(Items.CHEST), has(Items.CHEST)).
				save(this.output, Database.rl("stomach_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Items.GRINDSTONE))).
				addInput(new IngredientWithSize(Ingredient.of(Items.STONE))).
				addInput(new IngredientWithSize(Ingredient.of(Items.STONE))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				setResult(new ItemStack(Registration.BlockReg.CRUSHER)).
				unlockedBy(getHasName(Items.GRINDSTONE), has(Items.GRINDSTONE)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				save(this.output, Database.rl("crusher_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Items.SHEARS))).
				addInput(new IngredientWithSize(tag(Tags.Items.BARRELS_WOODEN))).
				addInput(new IngredientWithSize(Ingredient.of(Items.LEATHER))).
				addInput(new IngredientWithSize(Ingredient.of(Items.LEATHER))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				setResult(new ItemStack(Registration.BlockReg.SQUEEZER)).
				unlockedBy(getHasName(Items.SHEARS), has(Items.SHEARS)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				unlockedBy("has_" + Tags.Items.BARRELS_WOODEN.location().getPath(), has(Tags.Items.BARRELS_WOODEN)).
				unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER)).
				save(this.output, Database.rl("squeezer_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
				addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
				addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
				addInput(new IngredientWithSize(Ingredient.of(Items.IRON_BARS))).
				addInput(new IngredientWithSize(Ingredient.of(Items.COMPARATOR))).
				setResult(new ItemStack(Registration.BlockReg.CATCHER)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS)).
				unlockedBy(getHasName(Items.COMPARATOR), has(Items.COMPARATOR)).
				save(this.output, Database.rl("catcher_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(400).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLESH))).
				addInput(new IngredientWithSize(Ingredient.of(Items.BLAST_FURNACE))).
				addInput(new IngredientWithSize(Ingredient.of(Items.BLAST_FURNACE))).
				setResult(new ItemStack(Registration.BlockReg.FORGE)).
				unlockedBy(getHasName(Registration.BlockReg.FLESH), has(Registration.BlockReg.FLESH)).
				unlockedBy(getHasName(Items.BLAST_FURNACE), has(Items.BLAST_FURNACE)).
				save(this.output, Database.rl("forge_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(150).
				addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
				addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
				addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
				addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.BlockReg.FLUID_STORAGE))).
				setResult(new ItemStack(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE)).
				unlockedBy(getHasName(Items.SLIME_BALL), has(Tags.Items.SLIME_BALLS)).
				unlockedBy(getHasName(Registration.BlockReg.FLUID_STORAGE), has(Registration.BlockReg.FLUID_STORAGE)).
				save(this.output, Database.rl("multiblock_fluid_storage_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(100).
				addInput(new IngredientWithSize(tag(Tags.Items.RODS_WOODEN))).
				addInput(new IngredientWithSize(tag(Tags.Items.RODS_WOODEN))).
				addInput(new IngredientWithSize(tag(Tags.Items.RODS_WOODEN))).
				addInput(new IngredientWithSize(tag(Tags.Items.RODS_WOODEN))).
				addInput(new IngredientWithSize(tag(Tags.Items.RODS_WOODEN))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.ItemReg.FLESH_PIECE))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.ItemReg.FLESH_PIECE))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.ItemReg.FLESH_PIECE))).
				setResult(new ItemStack(Registration.ItemReg.WRENCH.get())).
				unlockedBy("has_" + Tags.Items.RODS_WOODEN.location().getPath(), has(Tags.Items.RODS_WOODEN)).
				unlockedBy(getHasName(Registration.ItemReg.FLESH_PIECE.get()), has(Registration.ItemReg.FLESH_PIECE)).
				save(this.output, Database.rl("wrench_from_chamber").toString());
		
		ChamberRecipeBuilder.newBuilder(100).
				addInput(new IngredientWithSize(tag(BioItemTags.WRENCH))).
				addInput(new IngredientWithSize(tag(Tags.Items.GLASS_PANES))).
				addInput(new IngredientWithSize(tag(Tags.Items.GLASS_PANES))).
				addInput(new IngredientWithSize(tag(Tags.Items.SLIME_BALLS))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.ItemReg.FLESH_PIECE))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.ItemReg.FLESH_PIECE))).
				addInput(new IngredientWithSize(Ingredient.of(Registration.ItemReg.FLESH_PIECE))).
				setResult(new ItemStack(Registration.ItemReg.INJECTOR.get())).
				unlockedBy("has_" + BioItemTags.WRENCH.location().getPath(), has(BioItemTags.WRENCH)).
				unlockedBy("has_" + Tags.Items.GLASS_PANES.location().getPath(), has(Tags.Items.GLASS_PANES)).
				unlockedBy("has_ " + Registration.ItemReg.FLESH_PIECE, has(Registration.ItemReg.FLESH_PIECE)).
				save(this.output, Database.rl("injector_from_chamber").toString());
	}
	
	private void generateCrusherRecipes()
	{
		//---- BLOCKS
		crusherRecipe(Tags.Items.STONES, 1, new ItemStack(Blocks.COBBLESTONE), "cobblestone_from_stone");
		crusherRecipe(Tags.Items.COBBLESTONES, 1,
				new ItemStack(Blocks.GRAVEL),
				"gravel_from_cobblestone",
				new StackWithChance(new ItemStack(Blocks.SAND), 0.15f));
		crusherRecipe(Tags.Items.NETHERRACKS, 1, new ItemStack(Blocks.GRAVEL), "gravel_from_netherrack");
		crusherRecipe(Tags.Items.GRAVELS, 1,
				new ItemStack(Blocks.SAND),
				"sand_from_gravel",
				new StackWithChance(new ItemStack(Items.FLINT), 0.15f));
		crusherRecipe(Tags.Items.SANDSTONE_UNCOLORED_BLOCKS, 1,
				new ItemStack(Blocks.SAND, 2),
				"sand_from_sandstone");
		crusherRecipe(Tags.Items.SANDSTONE_RED_BLOCKS, 1,
				new ItemStack(Blocks.RED_SAND, 2),
				"red_sand_from_red_sandstone");

		//---- ITEMS
		crusherRecipe(Tags.Items.BONES, 1,
				new ItemStack(Items.BONE_MEAL, 6),
				"bone_meal_from_bones");
		crusherRecipe(new ItemStack(Blocks.GLOWSTONE),
				new ItemStack(Items.GLOWSTONE_DUST, 4),
				"glowstone_dust_from_glowstone");
		crusherRecipe(new ItemStack(Blocks.MAGMA_BLOCK),
				new ItemStack(Items.MAGMA_CREAM, 4),
				"magma_cream_from_magma");
		crusherRecipe(new ItemStack(Items.PRISMARINE_SHARD),
				new ItemStack(Items.PRISMARINE_CRYSTALS, 2),
				"prismarine_crystals_from_prismarine_shard");
		crusherRecipe(Tags.Items.RODS_BLAZE, 1,
				new ItemStack(Items.BLAZE_POWDER, 4),
				"blaze_powder_from_blaze_rod");

		//---- ORES
		crusherRecipe(Tags.Items.ORES_COAL, 1,
				new ItemStack(Items.COAL, 3),
				"coal_from_coal_ore",
				new StackWithChance(new ItemStack(Items.COAL), 0.25f));
		crusherRecipe(Tags.Items.ORES_LAPIS, 1,
				new ItemStack(Items.LAPIS_LAZULI, 10),
				"lapis_lazuli_from_lapis_lazuli_ore");
		crusherRecipe(Tags.Items.ORES_REDSTONE,1,
				new ItemStack(Items.REDSTONE, 6),
				"redstone_from_redstone_ore");
		crusherRecipe(Tags.Items.ORES_DIAMOND, 1,
				new ItemStack(Items.DIAMOND, 2),
				"diamond_from_diamond_ore");
		crusherRecipe(Tags.Items.ORES_NETHERITE_SCRAP, 1,
				new ItemStack(Items.NETHERITE_SCRAP, 2),
				"netherite_scrap_from_ancient_debris_ore");
		crusherRecipe(Tags.Items.ORES_EMERALD, 1,
				new ItemStack(Items.EMERALD, 2),
				"emerald_from_emerald_ore");
		crusherRecipe(Tags.Items.ORES_QUARTZ, 1,
				new ItemStack(Items.QUARTZ, 3),
				"quartz_from_quartz_ore");
		//---- FLOWERS
		crusherRecipe(new ItemStack(Blocks.DANDELION), new ItemStack(Items.YELLOW_DYE, 4), "yellow_dye_from_dandelion");
		crusherRecipe(new ItemStack(Blocks.SUNFLOWER), new ItemStack(Items.YELLOW_DYE, 4), "yellow_dye_from_sunflower");
		crusherRecipe(new ItemStack(Blocks.ROSE_BUSH), new ItemStack(Items.YELLOW_DYE, 4), "yellow_dye_from_rose");
		crusherRecipe(new ItemStack(Blocks.POPPY), new ItemStack(Items.RED_DYE, 4), "red_dye_from_poppy");
		crusherRecipe(new ItemStack(Blocks.RED_TULIP), new ItemStack(Items.RED_DYE, 4), "red_dye_from_tulip");
		crusherRecipe(new ItemStack(Blocks.BLUE_ORCHID), new ItemStack(Items.LIGHT_GRAY_DYE, 4), "light_blue_dye_from_blue_orchid");
		crusherRecipe(new ItemStack(Blocks.ALLIUM), new ItemStack(Items.MAGENTA_DYE, 4), "magenta_dye_from_allium");
		crusherRecipe(new ItemStack(Blocks.LILAC), new ItemStack(Items.MAGENTA_DYE, 4), "magenta_dye_from_lilac");
		crusherRecipe(new ItemStack(Blocks.AZURE_BLUET), new ItemStack(Items.LIGHT_GRAY_DYE, 4), "light_gray_dye_from_azure_bluet");
		crusherRecipe(new ItemStack(Blocks.WHITE_TULIP), new ItemStack(Items.LIGHT_GRAY_DYE, 4), "light_gray_dye_from_tulip");
		crusherRecipe(new ItemStack(Blocks.OXEYE_DAISY), new ItemStack(Items.LIGHT_GRAY_DYE, 4), "light_gray_dye_from_daisy");
		crusherRecipe(new ItemStack(Blocks.ORANGE_TULIP), new ItemStack(Items.ORANGE_DYE, 4), "orange_dye_from_tulip");
		crusherRecipe(new ItemStack(Blocks.PINK_TULIP), new ItemStack(Items.PINK_DYE, 4), "pink_dye_from_tulip");
		crusherRecipe(new ItemStack(Blocks.PEONY), new ItemStack(Items.PINK_DYE, 4), "pink_dye_from_peony");
		
		//---- RECYCLING
		
		for (DyeColor color : DyeColor.values())
		{
			crusherRecipe(new ItemStack(BuiltInRegistries.ITEM.getValue(Database.mineRl(color.getName() + "_wool"))),
					new ItemStack(Items.STRING, 4),
					"string_from_" + color.getName() + "_wool",
					new StackWithChance(new ItemStack(BuiltInRegistries.ITEM.getValue(Database.mineRl(color.getName() + "_dye"))),
							0.15f));
			crusherRecipe(new ItemStack(BuiltInRegistries.ITEM.getValue(Database.mineRl(color.getName() +"_concrete"))),
					new ItemStack(BuiltInRegistries.ITEM.getValue(Database.mineRl(color.getName() + "_concrete_powder"))),
					color.getName() + "_concrete_powder_from_" + color.getName() + "_concrete");
			crusherRecipe(new ItemStack(BuiltInRegistries.ITEM.getValue(Database.mineRl(color.getName() + "_stained_glass"))),
					new ItemStack(Items.SAND),
					"sand_from_" + color.getName() + "_stained_glass");
		}
		
		crusherRecipe(ItemTags.TERRACOTTA, 1,
				new ItemStack(Items.CLAY_BALL, 4),
				"clay_from_terracotta");
		crusherRecipe(new ItemStack(Items.CLAY),
					new ItemStack(Items.CLAY_BALL, 4),
					"clay_from_clay_block");
		crusherRecipe(new ItemStack(Items.GLASS),
					new ItemStack(Items.SAND),
					"sand_from_glass");
		crusherRecipe(new ItemStack(Blocks.NETHER_BRICKS),
					new ItemStack(Items.NETHER_BRICK, 4),
					"nether_brick_from_nether_bricks");
		crusherRecipe(new ItemStack(Blocks.NETHER_BRICK_SLAB),
				new ItemStack(Items.NETHER_BRICK, 2),
				"nether_brick_from_nether_brick_slab");
		crusherRecipe(new ItemStack(Blocks.NETHER_BRICK_STAIRS),
				new ItemStack(Items.NETHER_BRICK, 3),
				"nether_brick_from_nether_brick_stairs");
		crusherRecipe(new ItemStack(Blocks.QUARTZ_PILLAR),
				new ItemStack(Items.QUARTZ, 4),
				"nether_quartz_from_quartz_pillar");
		crusherRecipe(new ItemStack(Blocks.CHISELED_QUARTZ_BLOCK),
				new ItemStack(Items.QUARTZ, 4),
				"nether_quartz_from_chiseled_quartz_pillar");
		crusherRecipe(new ItemStack(Blocks.QUARTZ_BRICKS),
				new ItemStack(Items.QUARTZ, 4),
				"nether_quartz_from_quartz_bricks");
		crusherRecipe(new ItemStack(Blocks.SMOOTH_QUARTZ),
				new ItemStack(Items.QUARTZ, 4),
				"nether_quartz_from_smooth_quartz");
		crusherRecipe(new ItemStack(Blocks.QUARTZ_SLAB),
				new ItemStack(Items.QUARTZ, 2),
				"nether_quartz_from_quartz_slab");
		crusherRecipe(new ItemStack(Blocks.SMOOTH_QUARTZ_SLAB),
				new ItemStack(Items.QUARTZ, 2),
				"nether_quartz_from_smooth_quartz_slab");
		crusherRecipe(new ItemStack(Blocks.QUARTZ_STAIRS),
				new ItemStack(Items.QUARTZ, 3),
				"nether_quartz_from_quartz_stairs");
		crusherRecipe(new ItemStack(Blocks.SMOOTH_QUARTZ_STAIRS),
				new ItemStack(Items.QUARTZ, 3),
				"nether_quartz_from_smooth_quartz_stairs");
		crusherRecipe(Tags.Items.SANDSTONE_UNCOLORED_SLABS, 1,
				new ItemStack(Blocks.SAND),
				"sand_from_sandstone_slab");
		crusherRecipe(Tags.Items.SANDSTONE_UNCOLORED_STAIRS, 1,
				new ItemStack(Blocks.SAND, 2),
				"sand_from_sandstone_stairs");
		crusherRecipe(Tags.Items.SANDSTONE_RED_SLABS, 1,
				new ItemStack(Blocks.RED_SAND),
				"red_sand_from_red_sandstone_slab");
		crusherRecipe(Tags.Items.SANDSTONE_RED_STAIRS, 1,
				new ItemStack(Blocks.RED_SAND, 2),
				"red_sand_from_red_sandstone_stairs");
	}
	
	private void generateBioForgeVanillaEnhancedRecipes()
    {
        Path path = FMLPaths.GAMEDIR.get().resolve("vanilla_recipes");

        try(Stream<Path> paths = Files.walk(path))
        {
            paths.filter(possiblePath -> possiblePath.toString().endsWith(".json")).
                    forEach(recipePath ->
                    {
                        try (Reader recipeReader = Files.newBufferedReader(recipePath))
                        {
                            JsonObject jsonObject = GSON.fromJson(recipeReader, JsonObject.class);
                            if (jsonObject.has("type") && !jsonObject.get("type").getAsString().equals("minecraft:smelting"))
                                return;
                            String recipeName = recipePath.getFileName().toString();
                            String resultedRecipeName = recipeName.substring(0, recipeName.length() - 5);
                            String ingr = jsonObject.get("ingredient").getAsString();
                            Ingredient input;
                            if (ingr.startsWith("#"))
                            {
                                TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(ingr.substring(1)));

                                input = this.tag(tag);
                            }
                            else
                                input = Ingredient.of(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(ingr)));
                            ItemStack result = ItemStack.OPTIONAL_CODEC.parse(JsonOps.INSTANCE, jsonObject.getAsJsonObject("result")).getOrThrow();
                            int cookingTime = jsonObject.has("cookingtime") ? jsonObject.get("cookingtime").getAsInt() : 200;

                            ForgeRecipeBuilder.newBuilder(
                                            new BioBaseRecipe.ResourcesInfo(
                                                    new BioBaseRecipe.BiomassInfo(true, 0.5f),
                                                    Optional.empty(),
                                                    Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 0.5f)),
                                                    cookingTime)).
                                    setInput(new IngredientWithSize(input)).
                                    setResult(result).
                                    unlockedBy(getHasName(input.getValues().get(0).value()), has(input.getValues().get(0).value())).
                                    save(this.output, Database.rl(resultedRecipeName).toString());
                        }
                        catch (Exception e)
                        {
                            Database.LOGGER.warn("Failed to parse vanilla recipe: {} - {}", recipePath, e);
                        }
                    });
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class Runner extends RecipeProvider.Runner
    {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(output, registries);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries, @NotNull RecipeOutput output)
        {
            return new BioRecipeProvider(registries, output);
        }

        @Override
        public @NotNull String getName()
        {
            return Database.MOD_NAME + ": Recipes";
        }
    }
	
	private void crusherRecipe(@NotNull ItemStack input, ItemStack output, String name, StackWithChance @NotNull ... additionalOutput)
	{
		CrusherRecipeBuilder builder = CrusherRecipeBuilder.newBuilder(new BioBaseRecipe.ResourcesInfo(
				new BioBaseRecipe.BiomassInfo(true, 0.5f),
				Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 2.0f)),
				Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 0.5f)),
				200)).
				setInput(new IngredientWithSize(Ingredient.of(input.getItem()), input.getCount())).
				setResult(output).
				unlockedBy(getHasName(input.getItem()), has(input.getItem()));
		for (StackWithChance stackWithChance : additionalOutput)
			builder.addSecondaryOutput(stackWithChance);
		builder.save(this.output, Database.rl(name).toString());
	}
	
	private void crusherRecipe(@NotNull TagKey<Item> input, int inputAmount, ItemStack output, String name, StackWithChance @NotNull ... additionalOutput)
	{
		CrusherRecipeBuilder builder = CrusherRecipeBuilder.newBuilder(new BioBaseRecipe.ResourcesInfo(
				new BioBaseRecipe.BiomassInfo(true, 0.5f),
				Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 2.0f)),
				Optional.of(new BioBaseRecipe.AdditionalResourceInfo(false, 0.25f, 0.5f)),
				200)).
				setInput(new IngredientWithSize(tag(input), inputAmount)).
				setResult(output).
				unlockedBy("has_" + input.location().getPath(), has(input));
		for (StackWithChance stackWithChance : additionalOutput)
			builder.addSecondaryOutput(stackWithChance);
		builder.save(this.output, Database.rl(name).toString());
	}
}
