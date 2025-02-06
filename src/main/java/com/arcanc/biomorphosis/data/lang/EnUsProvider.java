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
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

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


        //-----------------------------
        //BOOK
        this.add(Database.GUI.GuideBook.Chapters.BASIC.langKey(), "</block;minecraft:dirt/>Basic Chapter");
        this.add(Database.GUI.GuideBook.Chapters.ADVANCED.langKey(), "</block;minecraft:beacon/>Advanced Chapter");
        this.add(Database.GUI.GuideBook.Pages.TEST_PAGE_1.textLangKey(), "Some text for testing </item;minecraft:bread/> Additional testing text </block;minecraft:dirt/> MORE testing text </tag;block;minecraft:planks/> Yet again testing TEXT </recipe;minecraft:crafting_shaped;minecraft:fishing_rod/> And another one testing text </entity;minecraft:creeper/> and </entity;minecraft:spider/> Last testing text");

        this.add(Database.GUI.GuideBook.Pages.Components.SHAPED, "Shaped");
        this.add(Database.GUI.GuideBook.Pages.Components.SHAPELESS, "Shapeless");
    }
}
