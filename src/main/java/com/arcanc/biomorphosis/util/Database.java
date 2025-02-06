/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database
{
    public static final String MOD_ID = "biomorphosis";
    public static final String MOD_NAME = "Biomorphosis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final class GUI
    {
        private static final String GUI = "gui";

        public static final class Textures
        {
            public static final class Tooltip
            {
                public static final ResourceLocation TOOLTIP_BACKGROUND = Database.rl("special");
                public static final ResourceLocation TOOLTIP_DECORATIONS = Database.rl("special");
            }
        }

        public static final class GuideBook
        {
            public static final class Chapters
            {
                public static final ChapterInfo BASIC = new ChapterInfo(rl("basic"));
                public static final ChapterInfo ADVANCED = new ChapterInfo(rl("advanced"));

                public record ChapterInfo(ResourceLocation location, String langKey)
                {
                    public ChapterInfo(ResourceLocation location)
                    {
                        this(location, location.withPrefix("book.chapter.").toLanguageKey());
                    }
                }
            }

            public static final class Pages
            {
                public static final PageInfo TEST_PAGE_1 = new PageInfo(rl("test_1"));
                public static final PageInfo TEST_PAGE_2 = new PageInfo(rl("test_next_lvl"));
                public static final PageInfo TEST_PAGE_3 = new PageInfo(rl("even_not_test"));

                public record PageInfo(ResourceLocation location, String titleLangKey, String textLangKey)
                {
                    public PageInfo(ResourceLocation location)
                    {
                        this(location,
                                location.withPrefix("book.page.title.").toLanguageKey(),
                                location.withPrefix("book.page.text.").toLanguageKey());
                    }
                }

                public static final class Components
                {
                    public static final String SHAPED = rl(GUI + ".book.page.component.recipe.shaped").toLanguageKey();
                    public static final String SHAPELESS = rl(GUI + ".book.page.component.recipe.shapeless").toLanguageKey();
                }
            }
        }

        public static @NotNull ResourceLocation getTexturePath(String str)
        {
            return rl("textures/" + str + ".png");
        }
    }

    public static @NotNull ResourceLocation rl(String name)
    {
        Preconditions.checkNotNull(name);
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static @NotNull String rlStr(String name)
    {
        return rl(name).toString();
    }
}
