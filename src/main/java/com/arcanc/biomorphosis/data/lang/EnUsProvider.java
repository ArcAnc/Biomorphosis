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
        this.addItem(Registration.ItemReg.test, "Basic Common Item");
        this.addItem(Registration.ItemReg.test_rare, "Basic Rare Item");
        this.addItem(Registration.ItemReg.test_ultra_rare, "Basic Epic Item");
    }
}
