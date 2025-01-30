/**
 * @author ArcAnc
 * Created at: 29.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.LinkedHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BookData
{
    public static BookData INSTANCE;

    private final Map<ResourceLocation, AbstractBookPage> CONTENT = new LinkedHashMap<>();

    public static BookData getINSTANCE()
    {
        if (INSTANCE == null)
            INSTANCE = new BookData();
        return INSTANCE;
    }

}
