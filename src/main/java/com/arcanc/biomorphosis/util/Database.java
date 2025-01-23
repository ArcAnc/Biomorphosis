/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database
{
    public static final String MOD_ID = "biomorphosis";
    public static final String MOD_NAME = "Biomorphosis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final class Textures
    {
        public static final class GUI
        {
            public static final class Tooltip
            {
                public static final ResourceLocation TOOLTIP_BACKGROUND = Database.rl("special");
                public static final ResourceLocation TOOLTIP_DECORATIONS = Database.rl("special");
            }
        }
    }

    @Contract("_ -> new")
    public static @NotNull ResourceLocation rl(String name)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static @NotNull String rlStr(String name)
    {
        return rl(name).toString();
    }
}
