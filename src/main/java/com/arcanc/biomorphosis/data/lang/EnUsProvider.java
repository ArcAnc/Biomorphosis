/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.lang;

import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EnUsProvider extends LanguageProvider
{
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	
    public EnUsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, Database.MOD_ID, "en_us");
		this.lookupProvider = lookupProvider;
    }

    @Override
    protected void addTranslations()
    {
        this.addItem(Registration.ItemReg.FLESH_PIECE, "Piece Of Flash");
        this.addBlock(Registration.BlockReg.FLESH, "Flesh Block");
        this.addItem(Registration.ItemReg.BOOK, "Guide");
        this.addItem(Registration.ItemReg.QUEENS_BRAIN, "Queen's Brain");
        this.addItem(Registration.ItemReg.WRENCH, "Manipulator");
        this.addItem(Registration.ItemReg.FORGE_UPGRADE, "Gene Optimizer");

        this.addBlock(Registration.BlockReg.NORPH, "Norph");
        this.addBlock(Registration.BlockReg.NORPH_OVERLAY, "Norph");
        this.addBlock(Registration.BlockReg.NORPH_STAIRS, "Norph Stairs");
        this.addBlock(Registration.BlockReg.NORPH_SOURCE, "Norph Source");
        this.addBlock(Registration.BlockReg.FLUID_STORAGE, "Fluid Reservoir");
        this.addBlock(Registration.BlockReg.FLUID_TRANSMITTER, "Fluid Transmitter");
        this.addBlock(Registration.BlockReg.CRUSHER, "Crusher");
        this.addBlock(Registration.BlockReg.LURE_CAMPFIRE, "Lure Campfire");
        this.addBlock(Registration.BlockReg.STOMACH, "Organic Reprocessor");
        this.addBlock(Registration.BlockReg.CATCHER, "Biofluid Extractor");
        this.addBlock(Registration.BlockReg.FORGE, "BioForge");
        this.addBlock(Registration.BlockReg.MULTIBLOCK_FLUID_STORAGE, "Expandable Fluid Storage");
        this.addBlock(Registration.BlockReg.MULTIBLOCK_MORPHER, "Morpher");
        this.addBlock(Registration.BlockReg.MULTIBLOCK_CHAMBER, "Chamber");
        this.addBlock(Registration.BlockReg.PROP_0, "Pile of Junk");
        this.addBlock(Registration.BlockReg.PROP_1, "Pile of Junk");
        this.addBlock(Registration.BlockReg.PROP_2, "Pile of Junk");
        this.addBlock(Registration.BlockReg.GLOW_MOSS, "Glow Moss");
        this.addBlock(Registration.BlockReg.MOSS, "Moss");
        this.addBlock(Registration.BlockReg.HANGING_MOSS, "Hanging Moss");
        this.addBlock(Registration.BlockReg.NORPHED_DIRT_0, "Norphed Dirt");
        this.addBlock(Registration.BlockReg.NORPHED_DIRT_STAIR_0, "Norphed Dirt Stairs");
        this.addBlock(Registration.BlockReg.NORPHED_DIRT_SLAB_0, "Norphed Dirt Slab");
        this.addBlock(Registration.BlockReg.NORPHED_DIRT_1, "Norphed Dirt");
        this.addBlock(Registration.BlockReg.NORPHED_DIRT_STAIR_1, "Norphed Dirt Stairs");
        this.addBlock(Registration.BlockReg.NORPHED_DIRT_SLAB_1, "Norphed Dirt Slab");
        this.addBlock(Registration.BlockReg.INNER, "Inner");
        this.addBlock(Registration.BlockReg.ROOF, "Roof");
        this.addBlock(Registration.BlockReg.ROOF_STAIRS, "Roof Stairs");
        this.addBlock(Registration.BlockReg.ROOF_SLAB, "Roof Slab");
        this.addBlock(Registration.BlockReg.ROOF_DIRT, "Roof Dirt");
        this.addBlock(Registration.BlockReg.TRAMPLED_DIRT, "Trampled Dirt");
        this.addBlock(Registration.BlockReg.HIVE_DECO, "Hive");
        this.addBlock(Registration.BlockReg.EGGS_DECO, "Eggs");
        this.addBlock(Registration.BlockReg.CHEST, "Swarm Chest");

        this.addFluidDescription(Registration.FluidReg.BIOMASS, "Biomass");
        this.addFluidDescription(Registration.FluidReg.LYMPH, "Lymph");
        this.addFluidDescription(Registration.FluidReg.ADRENALINE, "Adrenaline");

        this.addEntity(Registration.EntityReg.MOB_QUEEN, "Queen", Database.GUI.Sounds.QUEEN, "stridulates");
        this.addEntity(Registration.EntityReg.MOB_QUEEN_GUARD, "Queen Guard", Database.GUI.Sounds.GUARD, "stridulates");
        this.addEntity(Registration.EntityReg.MOB_KSIGG, "Ksigg", Database.GUI.Sounds.KSIGG, "stridulates");
        this.addEntity(Registration.EntityReg.MOB_LARVA, "Larva", Database.GUI.Sounds.LARVA, "stridulates");
        this.addEntity(Registration.EntityReg.MOB_ZIRIS, "Ziris", Database.GUI.Sounds.ZIRIS, "stridulates");
        this.addEntity(Registration.EntityReg.MOB_INFESTOR, "Infestor", Database.GUI.Sounds.INFESTOR, "stridulates");
        this.addEntity(Registration.EntityReg.MOB_SWARMLING, "Swarmling", Database.GUI.Sounds.SWARMLING, "stridulates");
        this.addEntity(Registration.EntityReg.MOB_WORKER,"Worker", Database.GUI.Sounds.WORKER, "stridulates");

        //-----------------------------
        // JADE

        //this.add("config.jade.plugin_" + Database.JadeInfo.IDs.FLUID_RENDERER.toLanguageKey(), "Test Fluid Render");
        //this.add(Database.JadeInfo.Translations.FLUID_AMOUNT, "%s Amount: %s/%s");

        //-----------------------------
        // JEI

        this.add(Database.Integration.JeiInfo.CHAMBER_RECIPE_NAME, "Chamber");
        this.add(Database.Integration.JeiInfo.CRUSHER_RECIPE_NAME, "Crusher");
        this.add(Database.Integration.JeiInfo.STOMACH_RECIPE_NAME, "Organic Reprocessor");
        this.add(Database.Integration.JeiInfo.FORGE_RECIPE_NAME, "BioForge");

        this.add(Database.Integration.JeiInfo.REQUIRED, "Required");
        this.add(Database.Integration.JeiInfo.OPTIONAL, "Optional");
        this.add(Database.Integration.JeiInfo.PER_TICK, "%s mB/tick");
        this.add(Database.Integration.JeiInfo.WITH_ADRENALINE, "x%s Speed");
        this.add(Database.Integration.JeiInfo.WITH_LYMPH, "x%s Output");

        //-----------------------------
        //BOOK
        this.add(Database.GUI.GuideBook.Chapters.TITLE.langKey(), "</item;minecraft:writable_book/>Patch Notes");
        this.add(Database.GUI.GuideBook.Pages.V001.titleLangKey(), "Version: 0.0.1");
        this.add(Database.GUI.GuideBook.Pages.V001.textLangKey(), "\u2022 Initial Demo release");
        this.add(Database.GUI.GuideBook.Pages.V002.titleLangKey(), "Version: 0.0.2");
        this.add(Database.GUI.GuideBook.Pages.V002.textLangKey(), "\u2022 Added some decorative blocks");
        this.add(Database.GUI.GuideBook.Pages.V003.titleLangKey(), "Version: 0.0.3");
        this.add(Database.GUI.GuideBook.Pages.V003.textLangKey(), "\u2022 Added 2 new decorative blocks\n\u2022 Fixed wrong model on Norphed Dirt\n\u2022 Adjusted Infestor Size");
        this.add(Database.GUI.GuideBook.Pages.V004.titleLangKey(), "Version: 0.0.4");
        this.add(Database.GUI.GuideBook.Pages.V004.textLangKey(), "\u2022 Finally fixed decorative hive sound\n\u2022 Roof block now spread normally\n\u2022 Removed 2 step sounds. They was too loud\n\u2022 Changed Hive sound frequency. Early it was too rare\n\u2022 Added Queen Guard\n\u2022 Added Worker\n\u2022 Added Eggs Block\n\u2022 Start Working under Swarm Village");
        this.add(Database.GUI.GuideBook.Pages.V005.titleLangKey(), "Version: 0.0.5");
        this.add(Database.GUI.GuideBook.Pages.V005.textLangKey(), "\u2022 First version of village generation\n\u2022 A bit reworked multiblocks, now it's can have voxel shape of original block, but I still want more. Fully custom voxel shape will be top. And get rid from blockState to itemStack");
		this.add(Database.GUI.GuideBook.Pages.V006.titleLangKey(), "Version: 0.0.6");
		this.add(Database.GUI.GuideBook.Pages.V006.textLangKey(), "\u2022 Small internal changes and filled up book");

        this.add(Database.GUI.GuideBook.Chapters.BASIC.langKey(), "</block;minecraft:dirt/>Basic Info");
        this.add(Database.GUI.GuideBook.Chapters.ADVANCED.langKey(), "</block;minecraft:beacon/>Advanced Chapter");
	    //"Some text for testing </item;minecraft:bread/> Additional testing text </block;minecraft:dirt/>
	    // MORE testing text </tag;block;minecraft:planks/> Yet again testing TEXT
	    // </recipe;minecraft:crafting_shaped;minecraft:golden_leggings/>
	    // And another one testing text </entity;minecraft:creeper/> and
	    // </entity;minecraft:spider/> Last testing text");
		// recipeForBook(Registration.RecipeReg.STOMACH_RECIPE.getRecipeType().get(), Database.rl("stomach/biomass_from_meat"))
	    
	    this.add(Database.GUI.GuideBook.Pages.FLESH.titleLangKey(), "</item;biomorphosis:flesh_piece/>Flesh");
		this.add(Database.GUI.GuideBook.Pages.FLESH.textLangKey(), "The first thing you need to know is how to obtain flesh. And it’s pretty easy — just kill. Zombies, Villagers, Horses — anyone may drop </item;biomorphosis:flesh_piece/>You can even stack these pieces into a whole block, just use this recipe: </recipe;minecraft:crafting_shaped;minecraft:flesh/>" );
	    
	    this.add(Database.GUI.GuideBook.Pages.NORPH_SOURCE.titleLangKey(), "</block;biomorphosis:norph_source/>Norph Source");
        this.add(Database.GUI.GuideBook.Pages.NORPH_SOURCE.textLangKey(), "The second thing you need to know is about Norph. Norph is like a layer between us and the other world. We get resources from it and some other useful things, which you will learn about later. But the main thing you must know is that all our buildings can only be placed on Norph. And you should know that Norph can only be created by sources. It’s pretty easy to create: </recipe;minecraft:crafting_shaped;biomorphosis:norph_source/>Just place a source anywhere, and Norph will start to expand automatically");
		
		this.add(Database.GUI.GuideBook.Pages.CHAMBER.titleLangKey(), "</block;biomorphosis:chamber/>Chamber");
		this.add(Database.GUI.GuideBook.Pages.CHAMBER.textLangKey(), "The next important thing you have to know is about the Chamber. You can’t just build it — and you can’t build anything without it. What are you asking? Are we stuck? No, master, you must understand — we don’t build things, we grow everything we need.\nThe next step in your ascendance is to have a Chamber. To grow it, you must use </recipe;minecraft:crafting_shaped;biomorphosis:multiblock_morpher/>But… it will be the last thing you can obtain in the usual way. If you want anything else — use the Chamber. To get it, just drop the required materials onto the Morpher, and it will grow. Try not to interrupt the process — you know, it’s painful... What are you asking? What’s needed? Good question. Well, it seems you’ll need:" + multiblockParts(Database.rl("chamber")));
		
		this.add(Database.GUI.GuideBook.Pages.STORAGES.titleLangKey(), "</block;biomorphosis:chest/>Storages");
		this.add(Database.GUI.GuideBook.Pages.STORAGES.textLangKey(), "To grow something in the Chamber, you need to gather some resources. And these resources must be stored somewhere. We can offer you to use these structures: </recipe;biomorphosis:chamber;biomorphosis:chamber/fluid_storage_from_chamber/>and </recipe;biomorphosis:chamber;biomorphosis:chamber/chest_from_chamber/>");
		
		this.add(Database.GUI.GuideBook.Pages.CRUSHER.titleLangKey(), "</block;biomorphosis:crusher/>Crusher");
		this.add(Database.GUI.GuideBook.Pages.CRUSHER.textLangKey(), "As you may understand from its name, Crusher is for crushing! It’s one of our simpler machines for grinding materials — it allows us to get dusts from ores and to grind other things as well </recipe;biomorphosis:chamber;biomorphosis:chamber/crusher_from_chamber/>");
	    
	    this.add(Database.GUI.GuideBook.Pages.FORGE.titleLangKey(), "</block;biomorphosis:forge/>BioForge");
	    this.add(Database.GUI.GuideBook.Pages.FORGE.textLangKey(), "Everyone wants to smelt — make ingots, cook food, and do many other things. And of course, we need that too, so we have our own version: </recipe;biomorphosis:chamber;biomorphosis:chamber/forge_from_chamber/>Our Forge works like a typical furnace, but it processes everything faster and doesn’t require fuel — well, not in the usual way, at least. As fuel, it uses biomass. Using lymph and adrenaline makes the process more efficient and faster. And one more important thing — you can upgrade the Forge. Just use this item: </item;biomorphosis:forge_upgrade/>I don’t know how to get it… but maybe you’ll find a way");
	    
	    this.add(Database.GUI.GuideBook.Pages.STOMACH.titleLangKey(), "</block;biomorphosis:stomach/>Organic Reprocessor");
	    this.add(Database.GUI.GuideBook.Pages.STOMACH.textLangKey(), "This device will allow you to produce biomass — a lot of biomass. Just feed it meat. And if you feed it with lymph and adrenaline... Well, the process will become more efficient and faster </recipe;biomorphosis:chamber;biomorphosis:chamber/stomach_from_chamber/>");
		
		this.add(Database.GUI.GuideBook.Pages.CATCHER.titleLangKey(), "</block;biomorphosis:catcher/>Biofluid Extractor");
		this.add(Database.GUI.GuideBook.Pages.CATCHER.textLangKey(), "I’ve already mentioned lymph, but you still don’t know how to get it. Well, it’s pretty easy — you just need to grow a Fluid Extractor and lure someone into it... The process will be painful, but don’t worry — you, and a few other entities, can’t be captured. So you’re safe </recipe;biomorphosis:chamber;biomorphosis:chamber/catcher_from_chamber/>");
	    
	    this.add(Database.GUI.GuideBook.Pages.FLUID_TRANSMITTER.titleLangKey(), "</block;biomorphosis:fluid_transmitter/>Fluid Transmitter");
	    this.add(Database.GUI.GuideBook.Pages.FLUID_TRANSMITTER.textLangKey(), "Now you’ve found something really cool — a way to distribute all nearby fluids without any manual work. For this, you’ll need a Fluid Transmitter. Just place it, connect your storage to the required machine, and the \"magic\" will happen </recipe;biomorphosis:chamber;biomorphosis:chamber/fluid_transmitter_from_chamber/>");
		
	    this.add(Database.GUI.GuideBook.Pages.Components.SHAPED, "Shaped");
        this.add(Database.GUI.GuideBook.Pages.Components.SHAPELESS, "Shapeless");
        this.add(Database.GUI.GuideBook.Pages.Components.TICKS, "Ticks: %s");
        this.add(Database.GUI.GuideBook.Pages.Components.EXP, "Experience: %s");
        this.add(Database.GUI.GuideBook.Pages.Components.ARROW_LEFT, "Back");
        this.add(Database.GUI.GuideBook.Pages.Components.ARROW_RIGHT, "Next");
        this.add(Database.GUI.GuideBook.Pages.Components.ARROW_TO_TITLE, "To Title");

        //-----------------------------
        //TOOLTIP

        this.add(Database.GUI.HOLD_SHIFT, "Hold §4SHIFT§r for additional info");
        this.add(Database.GUI.InfoArea.FluidArea.Tooltip.NORMAL_SHORT_TOOLTIP, "%s mB");
        this.add(Database.GUI.InfoArea.FluidArea.Tooltip.NORMAL_EXTENDED_TOOLTIP, "%s/%s mB");
        this.add(Database.GUI.InfoArea.FluidArea.Tooltip.ADVANCED_TOOLTIP_DENSITY, "Density: %s kg/m³");
        this.add(Database.GUI.InfoArea.FluidArea.Tooltip.ADVANCED_TOOLTIP_TEMPERATURE, "Temperature: %s K");
        this.add(Database.GUI.InfoArea.FluidArea.Tooltip.ADVANCED_TOOLTIP_VISCOSITY, "Viscosity: %s m²/s");
        this.add(Database.GUI.InfoArea.ProgressBar.Tooltip.PERCENT, "%s / %s %%");
        this.add(Database.GUI.InfoArea.ProgressBar.Tooltip.DIRECT, "%s / %s");

        this.add(Database.GUI.ChamberButton.START, "Start Process");
        this.add(Database.GUI.ChamberButton.PROCESS, "Processing...");

        //-----------------------------
        //SOUND

        this.add(Database.GUI.Sounds.BLOCK_DESTROYED, "Block broken");
        this.add(Database.GUI.Sounds.BLOCK_PLACED, "Block placed");
        this.add(Database.GUI.Sounds.BLOCK_STEP_NORMAL, "Footsteps");
        this.add(Database.GUI.Sounds.BLOCK_STEP_TRAMPLED, "Footsteps");
        this.add(Database.GUI.Sounds.BLOCK_STEP_LEAF, "Footsteps");
        this.add(Database.GUI.Sounds.BLOCK_CHEST_OPEN, "Swarm chest opened");
        this.add(Database.GUI.Sounds.BLOCK_CHEST_CLOSE, "Swarm chest closed");
        this.add(Database.GUI.Sounds.BLOCK_HIVE_DECO, "Hive buzzes");
    }

    private void addFluidDescription(Registration.FluidReg.@NotNull FluidEntry entry, String description)
    {
        this.addItem(entry.bucket(), description + " Bucket");
        this.add(entry.still().getId().toLanguageKey(), description);
        this.add(entry.flowing().getId().toLanguageKey(), description);
        this.addBlock(entry.block(), description);
        this.add(entry.type().get().getDescriptionId(), description);
    }

    private void addEntity(Registration.EntityReg.@NotNull EntityEntry<?> entity, String description, Database.GUI.Sounds.EntitySoundSubtitle subtitle, String idleAction)
    {
        this.addEntityType(entity.getEntityHolder(), description);
        if (entity.getEggHolder() != null)
            this.addItem(entity.getEggHolder(), description + " Spawn Egg");

        this.add(subtitle.getIdle(), description + " " + idleAction);
        this.add(subtitle.getHurt(), description + " hurts");
        this.add(subtitle.getDeath(), description + " dies");
    }

	private @NotNull String multiblockParts(ResourceLocation multiblockLocation)
	{
		Preconditions.checkNotNull(multiblockLocation);
		
		HolderLookup.Provider provider = lookupProvider.join();
				
		IMultiblockDefinition definition = provider.lookupOrThrow(Registration.MultiblockReg.DEFINITION_KEY).
				get(ResourceKey.create(Registration.MultiblockReg.DEFINITION_KEY, multiblockLocation)).
				orElseThrow().
				value();
		List<ItemStack> data = definition.getStructure(null, null).getStackedStructure();
		
		StringBuilder returned = new StringBuilder();
		for (ItemStack stack : data)
		{
			returned.append("</item;").append(stack.getItemHolder().getRegisteredName()).append("/>").append(" x ").append(stack.getCount());
		}
		
		return returned.toString();
	}
	
    private @NotNull String recipeForBook(RecipeType<?> recipeType, ResourceLocation recipeId)
    {
        Preconditions.checkNotNull(recipeType);
        Preconditions.checkNotNull(recipeId);

        return "</recipe;" + recipeType.toString() + ";"+ recipeId.toString()+"/>";
    }
}
