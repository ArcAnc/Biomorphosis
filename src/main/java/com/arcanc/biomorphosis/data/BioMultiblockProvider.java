/**
 * @author ArcAnc
 * Created at: 09.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.block.multiblock.definition.DynamicMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.definition.StaticMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.multiblock.DynamicMultiblockBuilder;
import com.arcanc.biomorphosis.data.multiblock.StaticMultiblockBuilder;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BioMultiblockProvider
{
	private final Map<ResourceLocation, IMultiblockDefinition> definitions = new LinkedHashMap<>();
	private final HolderLookup.Provider registries;
	
    public BioMultiblockProvider(HolderLookup.Provider registries)
    {
		this.registries = registries;
    }
	
    protected void addMultiblocks()
    {
        addMultiblock(staticBuilder(Database.rl("chamber")).
                addPart(new BlockPos(0, 0, 0), Shapes.block(),
		                IngredientWithSize.of(Registration.BlockReg.FLESH),
		                IngredientWithSize.of(Items.EGG),
		                IngredientWithSize.of(new ItemStack(Items.CLAY_BALL, 8))).
                addPart(new BlockPos(1, 0, -1), Shapes.or(Shapes.box(0, 0, 0.5d, 0.5d, 0.5d, 1),
				                                                   Shapes.box(0, 0.5d, 0, 0.5d, 1, 1),
				                                                   Shapes.box(0.5d, 0.5d, 0.5d, 1, 1, 1),
				                                                   Shapes.box(0.75d, 0.5d, 0.375d, 0.875d, 1, 0.5d),
				                                                   Shapes.box(0.5d, 0.5d, 0.125d, 0.625d, 1, 0.25d),
				                                                   Shapes.box(0.5d, 0.5d, 0.25d, 0.75d, 1, 0.5d)),
		        IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
                addPart(new BlockPos(-1, 0, -1), Shapes.or(Shapes.box(0.5d, 0, 0.5d, 1, 0.5d, 1),
				                                                    Shapes.box(0, 0.5d, 0.5d, 1, 1, 1),
				                                                    Shapes.box(0.5d, 0.5d, 0, 1, 1, 0.5d),
				                                                    Shapes.box(0.375d, 0.5d, 0.125d, 0.5d, 1, 0.25d),
				                                                    Shapes.box(0.125d, 0.5d, 0.375d, 0.25d, 1, 0.5d),
				                                                    Shapes.box(0.25d, 0.5d, 0.25d, 0.5d, 1, 0.5d)),
		        IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
                addPart(new BlockPos(1, 0, 1), Shapes.or(Shapes.box(0, 0, 0, 0.5d, 0.5d, 0.5d),
				                                                  Shapes.box(0, 0.5d, 0, 1, 1, 0.5d),
				                                                  Shapes.box(0, 0.5d, 0.5d, 0.5d, 1, 1),
				                                                  Shapes.box(0.5d, 0.5d, 0.75d, 0.625d, 1, 0.875d),
				                                                  Shapes.box(0.75d, 0.5d, 0.5d, 0.875d, 1, 0.625d),
				                                                  Shapes.box(0.5d, 0.5d, 0.5d, 0.75d, 1, 0.75d)),
		        IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
                addPart(new BlockPos(-1, 0, 1), Shapes.or(Shapes.box(0.5d, 0, 0, 1, 0.5d, 0.5d),
				                                                   Shapes.box(0.5d, 0.5d, 0, 1, 1, 1),
				                                                   Shapes.box(0, 0.5d, 0, 0.5d, 1, 0.5d),
				                                                   Shapes.box(0.125d, 0.5d, 0.5d, 0.25d, 1, 0.625d),
				                                                   Shapes.box(0.375d, 0.5d, 0.75d, 0.5d, 1, 0.875d),
				                                                   Shapes.box(0.25d, 0.5d, 0.5d, 0.5d, 1, 0.75d)),
		        IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
                addPart(new BlockPos(1, 1, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 1, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(1, 1, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 1, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(0, 1, 0), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                
                addPart(new BlockPos(1, 2, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 2, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(1, 2, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 2, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(0, 2, 0), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                
                addPart(new BlockPos(1, 3, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 3, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(1, 3, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 3, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(0, 3, 0), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                
                addPart(new BlockPos(1, 4, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 4, -1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(1, 4, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(-1, 4, 1), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                addPart(new BlockPos(0, 4, 0), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
                
                setPlacedBlock(Registration.BlockReg.MULTIBLOCK_CHAMBER.get().defaultBlockState()).
                end());
		
		addMultiblock(staticBuilder(Database.rl("turret")).
				addPart(new BlockPos(0,0,0), Shapes.block(), IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(0, 1, 0), Shapes.box(0.375d, 0, 0.375d, 0.625d, 1, 0.625d),
						IngredientWithSize.of(Ingredient.of(tag(ItemTags.FENCES)))).
				setPlacedBlock(Registration.BlockReg.MULTIBLOCK_TURRET.get().defaultBlockState()).
				end());
		
		addMultiblock(staticBuilder(Database.rl("chrysalis")).
				addPart(new BlockPos(0, 0, 0), Shapes.box(0, 0, 0, 1, 0.1d, 1),
						IngredientWithSize.of(Registration.BlockReg.CATCHER),
						IngredientWithSize.of(Registration.ItemReg.WRENCH)).
				addPart(new BlockPos(0, 0, -1), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																   Shapes.box(0, 0.1d, 0, 1, 1, 0.125d)),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(0, 0, 1), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																  Shapes.box(0, 0.1d, 0.875d, 1, 1, 1)),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(-1, 0, 0), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																   Shapes.box(0, 0.1d, 0, 0.125d, 1, 1)),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(1, 0, 0), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																  Shapes.box(0.875d, 0.1d, 0, 1, 1, 1)),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(-1, 0, -1), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																	Shapes.box(0, 0.1d, 0, 0.125d, 1, 1),
																	Shapes.box(0.125d, 0.1, 0, 1, 1, 0.125d)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(-1, 0, 1), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																   Shapes.box(0, 0.1d, 0, 0.125d, 1, 1),
																   Shapes.box(0.125d, 0.1, 0.875d, 1, 1, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(1, 0, -1), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																   Shapes.box(0.875d, 0.1d, 0, 1, 1, 1),
																   Shapes.box(0, 0.1, 0, 0.875d, 1, 0.125d)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(1, 0, 1), Shapes.or(Shapes.box(0, 0, 0, 1, 0.1d, 1),
																  Shapes.box(0.875d, 0.1d, 0, 1, 1, 1),
																  Shapes.box(0, 0.1, 0.875d, 0.875d, 1, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(0, 1, -1), Shapes.box(0, 0, 0, 1, 1, 0.125d),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(0, 1, 1), Shapes.box(0, 0, 0.875d, 1, 1, 1),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(-1, 1, 0), Shapes.box(0, 0, 0, 0.125d, 1, 1),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(1, 1, 0), Shapes.box(0.875d, 0, 0, 1, 1, 1),
						IngredientWithSize.of(Registration.BlockReg.FLESH)).
				addPart(new BlockPos(-1, 1, -1), Shapes.or(Shapes.box(0, 0, 0, 0.125d, 1, 1),
																	Shapes.box(0.125d, 0, 0, 1, 1, 0.125d)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(-1, 1, 1), Shapes.or(Shapes.box(0, 0, 0, 0.125d, 1, 1),
																   Shapes.box(0.125d, 0, 0.875d, 1, 1, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(1, 1, -1), Shapes.or(Shapes.box(0.875d, 0, 0, 1, 1, 1),
																   Shapes.box(0, 0, 0, 0.875d, 1, 0.125d)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(1, 1, 1), Shapes.or(Shapes.box(0.875d, 0, 0, 1, 1, 1),
																  Shapes.box(0, 0, 0.875d, 0.875d, 1, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(0, 2, -1), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																   Shapes.box(0, 0, 0, 1, 0.875d, 0.125d)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(0, 2, 1), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																   Shapes.box(0, 0, 0.875d, 1, 0.875d, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(-1, 2, 0), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																   Shapes.box(0, 0, 0, 0.125d, 0.875d, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(1, 2, 0), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																  Shapes.box(0.875d, 0, 0, 1, 0.875d, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(-1, 2, -1), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																	Shapes.box(0, 0, 0, 0.125d, 0.875d, 1),
																	Shapes.box(0.125d, 0, 0, 1, 0.875d, 0.125d)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(-1, 2, 1), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																   Shapes.box(0, 0, 0, 0.125d, 0.875d, 1),
																   Shapes.box(0.125d, 0, 0.875d, 1, 0.875d, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(1, 2, -1), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																   Shapes.box(0, 0, 0, 1, 0.875d, 0.125d),
																   Shapes.box(0.875d, 0, 0.125d, 1, 0.875d, 1)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(1, 2, 1), Shapes.or(Shapes.box(0, 0.875d, 0, 1, 1, 1),
																  Shapes.box(0, 0, 0.875d, 1, 0.875d, 1),
																  Shapes.box(0.875d, 0, 0, 1, 0.875d, 0.875d)),
						IngredientWithSize.of(Registration.BlockReg.NORPHED_DIRT_STAIR_0)).
				addPart(new BlockPos(0, 2, 0), Shapes.box(0, 0.875d, 0,1, 1, 1),
						IngredientWithSize.of(Registration.BlockReg.FLESH),
						IngredientWithSize.of(Registration.ItemReg.WRENCH)).
				setPlacedBlock(Registration.BlockReg.MULTIBLOCK_CHRYSALIS.get().defaultBlockState()).
				end());
		
        addMultiblock(dynamicBuilder(Database.rl("fluid_storage")).
                setBehavior(DynamicMultiblockDefinition.ScanBehavior.BFS).
                setMaxSize(2, 3, 2).
                setAllowedBlockType(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE.get().defaultBlockState()).
                end());
    }

	private @NotNull HolderSet<Item> tag(@NotNull TagKey<Item> tag)
	{
		return this.registries.lookupOrThrow(Registries.ITEM).getOrThrow(tag);
	}
	
    private @NotNull StaticMultiblockBuilder staticBuilder(ResourceLocation location)
    {
        return new StaticMultiblockBuilder(location);
    }

    private @NotNull DynamicMultiblockBuilder dynamicBuilder(ResourceLocation location)
    {
        return new DynamicMultiblockBuilder(location);
    }
	
	private <T extends IMultiblockDefinition>void addMultiblock(T definition)
	{
		if (this.definitions.putIfAbsent(definition.getId(), definition) != null)
			throw new IllegalStateException("Duplicate Multiblock Definitions " + definition.getId());
	}

	public record Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) implements DataProvider
	{
		@Override
		public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output)
		{
			return this.registries.thenCompose(provider ->
			{
				final PackOutput.PathProvider pathProvider = this.packOutput.createRegistryElementsPathProvider(Registration.MultiblockReg.DEFINITION_KEY);
				List<CompletableFuture<?>> list = new ArrayList<>();
				BioMultiblockProvider definitions = new BioMultiblockProvider(provider);
				definitions.addMultiblocks();
				
				for (IMultiblockDefinition definition : definitions.definitions.values())
				{
					validateMultiblock(definition);
					list.add(DataProvider.saveStable(output, provider, IMultiblockDefinition.CODEC, definition, pathProvider.json(definition.getId())));
				}
				
				return CompletableFuture.allOf(list.toArray(CompletableFuture[] :: new));
			});
		}
		
		private void validateMultiblock(IMultiblockDefinition definition)
		{
			if (definition instanceof StaticMultiblockDefinition staticMultiblock)
			{
				if (staticMultiblock.getStructure().getParts().isEmpty())
					throw new IllegalStateException("Empty multiblock parts " + definition.getId());
				if (staticMultiblock.getStructure().getPlacedBlock() == null)
					throw new IllegalStateException("Definition " + definition.getId() + " haven't placed block");
			}
			else if (definition instanceof DynamicMultiblockDefinition dynamicMultiblock)
				if (dynamicMultiblock.getAllowedBlockType() == null)
					throw new IllegalStateException("Dynamic definition " + definition.getId() +"  haven't allowed block type");
		}
		
		@Override
		public @NotNull String getName()
		{
			return Database.MOD_ID + ": Multiblocks";
		}
	}
}
