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

        this.addFluidDescription(Registration.FluidReg.BIOMASS, "Biomass");
        this.addFluidDescription(Registration.FluidReg.LYMPH, "Lymph");
        this.addFluidDescription(Registration.FluidReg.ADRENALINE, "Adrenaline");

        this.addEntity(Registration.EntityReg.MOB_QUEEN, "Queen");
        this.addEntity(Registration.EntityReg.MOB_KSIGG, "Ksigg");
        this.addEntity(Registration.EntityReg.MOB_LARVA, "Larva");

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
    }

    private void addFluidDescription(Registration.FluidReg.@NotNull FluidEntry entry, String description)
    {
        this.addItem(entry.bucket(), description + " Bucket");
        this.add(entry.still().getId().toLanguageKey(), description);
        this.add(entry.flowing().getId().toLanguageKey(), description);
        this.addBlock(entry.block(), description);
        this.add(entry.type().get().getDescriptionId(), description);
    }

    private void addEntity(Registration.EntityReg.@NotNull EntityEntry<?> entity, String description)
    {
        this.addEntityType(entity.getEntityHolder(), description);
        if (entity.getEggHolder() != null)
            this.addItem(entity.getEggHolder(), description + " Spawn Egg");
    }

    private @NotNull String recipeForBook(RecipeType<?> recipeType, ResourceLocation recipeId)
    {
        Preconditions.checkNotNull(recipeType);
        Preconditions.checkNotNull(recipeId);

        return "</recipe;" + recipeType.toString() + ";"+ recipeId.toString()+"/>";
    }
}
