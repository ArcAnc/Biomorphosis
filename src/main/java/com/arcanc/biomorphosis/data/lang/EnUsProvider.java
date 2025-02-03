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
        //this.add(Database.GUI.GuideBook.Pages.TEST_PAGE_1.textLangKey(), "У нас сегодня шикарный завтрак из </item;minecraft:soup/>, тарелки с которым расставлены на </block;minecraft:table/>, который сделан из </tag;block;minecraft:planks/>. Со мной за столом сидят </entity;minecraft:creeper/> и </entity;minecraft:spider/>. Суп приготовлен по лучшему в мире рецепту </recipe;crafting_shaped;minecraft:soup/>. Хороший день намечается");
    }
}
