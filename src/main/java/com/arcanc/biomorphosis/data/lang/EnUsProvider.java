/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.lang;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class EnUsProvider extends LanguageProvider
{
    public EnUsProvider(PackOutput output)
    {
        super(output, Database.MOD_ID, "en_us");
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
        this.add(Database.GUI.GuideBook.Pages.V004.textLangKey(), "\u2022 Finally fixed decorative hive sound\n\u2022 Roof block now spread normally\n\u2022 Removed 2 step sounds. They was too loud\n\u2022 Changed Hive sound frequency. Early it was too rare\n\u2022 Added Queen Guard\n\u2022 Added Worker");

        this.add(Database.GUI.GuideBook.Chapters.BASIC.langKey(), "</block;minecraft:dirt/>Basic Chapter");
        this.add(Database.GUI.GuideBook.Chapters.ADVANCED.langKey(), "</block;minecraft:beacon/>Advanced Chapter");
        this.add(Database.GUI.GuideBook.Pages.TEST_BASIC_1.textLangKey(), "Some text for testing </item;minecraft:bread/> Additional testing text </block;minecraft:dirt/> MORE testing text </tag;block;minecraft:planks/> Yet again testing TEXT </recipe;minecraft:crafting_shaped;minecraft:golden_leggings/> And another one testing text </entity;minecraft:creeper/> and </entity;minecraft:spider/> Last testing text");
        this.add(Database.GUI.GuideBook.Pages.TEST_BASIC_2.textLangKey(), recipeForBook(Registration.RecipeReg.STOMACH_RECIPE.getRecipeType().get(), Database.rl("stomach/biomass_from_meat")));

        this.add(Database.GUI.GuideBook.Pages.TEST_BASIC_1.titleLangKey(), "</item;minecraft:apple/>First Page Title");
        this.add(Database.GUI.GuideBook.Pages.TEST_BASIC_2.titleLangKey(), "</item;minecraft:iron_ingot/>Second Page Title");

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

    private @NotNull String recipeForBook(RecipeType<?> recipeType, ResourceLocation recipeId)
    {
        Preconditions.checkNotNull(recipeType);
        Preconditions.checkNotNull(recipeId);

        return "</recipe;" + recipeType.toString() + ";"+ recipeId.toString()+"/>";
    }
}
