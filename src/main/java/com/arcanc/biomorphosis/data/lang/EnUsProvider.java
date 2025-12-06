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
		this.addBlock(Registration.BlockReg.SQUEEZER, "Squeezer");
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
        this.addFluidDescription(Registration.FluidReg.ACID, "Acid");
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
	    this.add(Database.Integration.JeiInfo.SQUEEZER_RECIPE_NAME, "Squeezer");
        this.add(Database.Integration.JeiInfo.STOMACH_RECIPE_NAME, "Organic Reprocessor");
        this.add(Database.Integration.JeiInfo.FORGE_RECIPE_NAME, "BioForge");

        this.add(Database.Integration.JeiInfo.REQUIRED, "Required");
        this.add(Database.Integration.JeiInfo.OPTIONAL, "Optional");
        this.add(Database.Integration.JeiInfo.PER_TICK, "%s mB/tick");
        this.add(Database.Integration.JeiInfo.WITH_ADRENALINE, "x%s Speed");
        this.add(Database.Integration.JeiInfo.WITH_ACID, "x%s Output");

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
		this.add(Database.GUI.GuideBook.Pages.V006.textLangKey(), "\u2022 Small internal changes and filled up book\n\u2022 Fixed entities not attacked and crash by missing sound.\n\u2022 A bit changed descriptions in book\n\u2022 A bit nerfed Queen Guard. He was have too much armor");
	    this.add(Database.GUI.GuideBook.Pages.V007.titleLangKey(), "Version: 0.0.7");
	    this.add(Database.GUI.GuideBook.Pages.V007.textLangKey(), "\u2022 Renamed lymph into acid \n\u2022 Changed recipes to work with float values of fluids\n\u2022 Added Squeezer");
	    this.add(Database.GUI.GuideBook.Pages.V0071.titleLangKey(), "Version: 0.0.7.1");
	    this.add(Database.GUI.GuideBook.Pages.V0071.textLangKey(), "\u2022 Added an overlay to the Manipulator to explain how it works");

        this.add(Database.GUI.GuideBook.Chapters.BASIC.langKey(), "</block;minecraft:dirt/>Basic Info");
        this.add(Database.GUI.GuideBook.Chapters.ADVANCED.langKey(), "</block;minecraft:beacon/>Advanced Chapter");
	    
	    this.add(Database.GUI.GuideBook.Pages.FLESH.titleLangKey(), "</item;biomorphosis:flesh_piece/>Flesh");
		this.add(Database.GUI.GuideBook.Pages.FLESH.textLangKey(), "The first thing you must understand, little one, is flesh. Everything begins with flesh. It is the seed of life — and of us. Harvest it from the living. Tear it from Zombies, Villagers, Horses… all bodies serve. Each will give you </item;biomorphosis:flesh_piece/>Gather enough, and you may compress the pieces into a single, pulsating mass. Use this method: </recipe;minecraft:crafting_shaped;minecraft:flesh/>The Swarm is built from flesh. Remember that" );
	    
	    this.add(Database.GUI.GuideBook.Pages.NORPH_SOURCE.titleLangKey(), "</block;biomorphosis:norph_source/>Norph Source");
        this.add(Database.GUI.GuideBook.Pages.NORPH_SOURCE.textLangKey(), "The next thing you must learn is Norph — the living ground beneath us. Norph is the membrane between our world and theirs. Through it, we draw resources, power, and life itself. But remember this well — our structures live only upon Norph. Without it, they cannot breathe, cannot grow. Norph can be born only from a Source. Craft one like this: </recipe;minecraft:crafting_shaped;biomorphosis:norph_source/>Place it, and the Norph will spread on its own... slowly, hungrily. Watch as the world turns alive");
		
		this.add(Database.GUI.GuideBook.Pages.CHAMBER.titleLangKey(), "</block;biomorphosis:chamber/>Chamber");
		this.add(Database.GUI.GuideBook.Pages.CHAMBER.textLangKey(), "Now... the Chamber. You cannot simply build it. Nothing of ours is built — it is grown. The Chamber is the heart of creation. Through it, the Swarm expands. Without it, you are nothing. To grow one, you’ll need this: </recipe;minecraft:crafting_shaped;biomorphosis:multiblock_morpher/>But remember, this will be the last thing you can obtain the human way. From now on — everything must be grown. Drop the required matter onto the Morpher, and it will take shape. Do not interrupt the process... It screams when disturbed. Curious what’s needed? Good. You’ll need: " + multiblockParts(Database.rl("chamber")));
		
		this.add(Database.GUI.GuideBook.Pages.STORAGES.titleLangKey(), "</block;biomorphosis:chest/>Storages");
		this.add(Database.GUI.GuideBook.Pages.STORAGES.textLangKey(), "To grow within the Chamber, you’ll need resources — flesh, fluids, essence. They must be stored, contained, like organs within a body. Use these to shape our vessels: </recipe;biomorphosis:chamber;biomorphosis:chamber/fluid_storage_from_chamber/>and</recipe;biomorphosis:chamber;biomorphosis:chamber/chest_from_chamber/>Each will serve as part of the Swarm’s anatomy — one holds fluids, the other, matter");
		
		this.add(Database.GUI.GuideBook.Pages.CRUSHER.titleLangKey(), "</block;biomorphosis:crusher/>Crusher");
		this.add(Database.GUI.GuideBook.Pages.CRUSHER.textLangKey(), "You’ve learned to feed. Now, learn to break. The Crusher — it grinds, tears, reduces. From cold ore, it makes dust. From hard matter, it makes food for the Swarm </recipe;biomorphosis:chamber;biomorphosis:chamber/crusher_from_chamber/>Simple, but vital. Every scream of stone becomes a whisper of progress");

	    this.add(Database.GUI.GuideBook.Pages.SQUEEZER.titleLangKey(), "</block;biomorphosis:squeezer/>Squeezer");
	    this.add(Database.GUI.GuideBook.Pages.SQUEEZER.textLangKey(), "Hunger gnaws at all of us, young one. If you wish to feed — you must learn to harvest. The only way to nourish the Swarm is to grow a Squeezer. It accepts any organic matter... and crushes it, crushes it, until nothing remains but rich, flowing biomass. As long as it drips — we live. As long as you feed it — the Swarm stays strong</recipe;biomorphosis:chamber;biomorphosis:chamber/squeezer_from_chamber/>");
	    
	    this.add(Database.GUI.GuideBook.Pages.FORGE.titleLangKey(), "</block;biomorphosis:forge/>BioForge");
	    this.add(Database.GUI.GuideBook.Pages.FORGE.textLangKey(), "Even the Swarm needs heat. Our Forge burns without fire, consumes without flame. It melts ores, cooks flesh, and does so faster than your crude furnaces</recipe;biomorphosis:chamber;biomorphosis:chamber/forge_from_chamber/>It needs no fuel — only biomass. Add acid or adrenaline, and the process quickens, pulsing like a heart in rage. And should you desire more — evolution awaits. Feed it this: </item;biomorphosis:forge_upgrade/>How to find it? Heh... The Swarm rewards those who dig deep enough");
	    
	    this.add(Database.GUI.GuideBook.Pages.STOMACH.titleLangKey(), "</block;biomorphosis:stomach/>Organic Reprocessor");
	    this.add(Database.GUI.GuideBook.Pages.STOMACH.textLangKey(), "To feed the Swarm, you must grow a Organic Reprocessor. It consumes meat, breaks it down, and births acid — pure life matter. Feed it flesh, and it will pulse. Feed it acid and adrenaline, and it will roar, devouring faster, stronger</recipe;biomorphosis:chamber;biomorphosis:chamber/stomach_from_chamber/>Everything that enters becomes part of us");
		
		this.add(Database.GUI.GuideBook.Pages.CATCHER.titleLangKey(), "</block;biomorphosis:catcher/>Biofluid Extractor");
		this.add(Database.GUI.GuideBook.Pages.CATCHER.textLangKey(), "You’ve heard of acid, haven’t you? Now you shall take it. Grow a Fluid Extractor, and lure the living close. When it bites, it drinks — drawing life from their veins. The process is... Exquisite. Painful for them, perhaps. But necessary. Be warned: some creatures are too strong to be captured by our devices. Those beasts resist the pull — you will not take them easily</recipe;biomorphosis:chamber;biomorphosis:chamber/catcher_from_chamber/>");

	    this.add(Database.GUI.GuideBook.Pages.MANIPULATOR.titleLangKey(), "</item;biomorphosis:wrench/>Manipulator");
	    this.add(Database.GUI.GuideBook.Pages.MANIPULATOR.textLangKey(), "Remember this, young one: without a proper tool, you are nothing but soft flesh. To shape, to repair, to open and to bind — you need the Manipulator. It is simple... yet irreplaceable. Fine tendrils, hardened claws, stiff cartilage — everything in it is crafted for precise, delicate work. Humans use a ‘screwdriver’ for such tasks. We possess something far more refined. The Manipulator is an extension of your will. Through it you build the Swarm... and keep it alive. But be aware: every block responds to touch in its own unique way. If you wish to learn how the Manipulator interacts with a specific structure — consult the guide page of that block</recipe;biomorphosis:chamber;biomorphosis:chamber/wrench_from_chamber/>");
	    
	    this.add(Database.GUI.GuideBook.Pages.FLUID_TRANSMITTER.titleLangKey(), "</block;biomorphosis:fluid_transmitter/>Fluid Transmitter");
	    this.add(Database.GUI.GuideBook.Pages.FLUID_TRANSMITTER.textLangKey(), "Ah... So you’ve finally reached this stage. Good. Now you will learn how to let our essence flow — how to spread fluids through the veins of the Swarm without touching them yourself.For this, you must grow a Fluid Transmitter. Place it... Connect the storage to the desired organ — and then, watch. The flow will begin on its own, like blood through flesh. No pipes. No hands. Only life moving life </recipe;biomorphosis:chamber;biomorphosis:chamber/fluid_transmitter_from_chamber/>To link two different blocks, use the Manipulator while sneaking. First select the block from which you want to transfer fluids, then select the block to which the fluid should be transferred. After that, stop sneaking and use the Manipulator on the transmitter");
		
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
	    //OVERLAY
	    this.add(Database.GUI.Overlays.Tooltip.START_FLUID_HANDLER, "Using %s on %s will allow you to begin linking two fluid containers");
		this.add(Database.GUI.Overlays.Tooltip.WRONG_FLUID_HANDLER, "ANOTHER ONE!");
		this.add(Database.GUI.Overlays.Tooltip.CHOOSE_SECOND_FLUID_HANDLER, "Choose the second fluid container and use %s on it");
		this.add(Database.GUI.Overlays.Tooltip.CHOOSE_TRANSMITTER, "Choose the Fluid Transmitter and use %s on it");
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
