/**
 * @author ArcAnc
 * Created at: 12.02.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.model;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BioItemModelProvider extends ItemModelProvider
{
	public BioItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, Database.MOD_ID, existingFileHelper);
	}
	
	@Override
	protected void registerModels()
	{
		basicItem(Registration.ItemReg.CREATIVE_TAB_ICON.get());
		basicItem(Registration.ItemReg.FLESH_PIECE.get());
		basicItem(Registration.ItemReg.QUEENS_BRAIN.get());
		basicItem(Registration.ItemReg.ANTENNAS.get());
		basicItem(Registration.ItemReg.GUARD_ARMOR_PIECE.get());
		basicItem(Registration.ItemReg.INFESTOR_STING.get());
		basicItem(Registration.ItemReg.SWARMLING_HEAD.get());
		basicItem(Registration.ItemReg.ZIRIS_WING.get());
		basicItem(Registration.ItemReg.WRENCH.get());
		basicItem(Registration.ItemReg.BOOK.get());
		basicItem(Registration.ItemReg.FORGE_UPGRADE.get());
		handheldItem(Registration.ItemReg.INJECTOR.get());
		
		createBucket(Registration.FluidReg.BIOMASS);
		createBucket(Registration.FluidReg.ACID);
		createBucket(Registration.FluidReg.ADRENALINE);
		
		/*createMultiblockItemModel(Registration.BlockReg.MULTIBLOCK_CHAMBER.asItem());
		createMultiblockItemModel(Registration.BlockReg.MULTIBLOCK_CHRYSALIS.asItem());
		createMultiblockItemModel(Registration.BlockReg.MULTIBLOCK_TURRET.asItem());
		createMultiblockItemModel(Registration.BlockReg.MULTIBLOCK_MORPHER.asItem());*/
		
		spawnEggItem(Registration.EntityReg.MOB_QUEEN.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_KSIGG.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_LARVA.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_ZIRIS.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_INFESTOR.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_SWARMLING.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_QUEEN_GUARD.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_WORKER.getEggHolder().get());
		
		spawnEggItem(Registration.EntityReg.MOB_BASE_SOLDIER.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_BASE_SERGEANT.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_BASE_CAPTAIN.getEggHolder().get());
		spawnEggItem(Registration.EntityReg.MOB_BASE_BLACKSMITH.getEggHolder().get());
	}
	
	@Override
	public String getName()
	{
		return Database.MOD_NAME + " Item Models";
	}
	
	private void createBucket(Registration.FluidReg.FluidEntry entry)
	{
		withExistingParent(itemPrefix(entry.bucket()), Database.neoRl(itemPrefix("bucket_drip"))).
				customLoader(DynamicFluidContainerModelBuilder :: begin).
				fluid(entry.still().get()).
				applyFluidLuminosity(true).
				applyTint(true);
	}
	
	private void createMultiblockItemModel(Item itemHolder)
	{
		//Shulker box used coz this is block model with right rotations, but still have parent builtin/entity
		ModelFile model = getExistingFile(mcLoc("template_shulker_box"));
		getBuilder(itemPrefix(itemHolder)).parent(model);
	}
	
	private String itemPrefix(Item itemHolder)
	{
		return itemPrefix(BuiltInRegistries.ITEM.getKey(itemHolder).getPath());
	}
	
	private String itemPrefix(DeferredHolder<Item, ? extends Item> itemHolder)
	{
		return itemPrefix(itemHolder.get());
	}
	
	private String itemPrefix(String name)
	{
		return ModelProvider.ITEM_FOLDER + "/" + name;
	}
}
