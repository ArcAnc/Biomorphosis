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

    public static final class DataComponents
    {
        public static final String FLUID_TRANSMIT = "fluid_transmit_data";
    }

    public static final class JadeInfo
    {
        public static final class IDs
        {
            public static final ResourceLocation FLUID_RENDERER = rl("jade_fluid_renderer");
        }
        public static final class Translations
        {
            public static final String FLUID_AMOUNT = rl("jade.fluid").toLanguageKey();
        }
    }
    public static final class FluidInfo
    {
        public static @NotNull ResourceLocation getStillLoc(String name)
        {
            return rl("fluids/" + name +"/still");
        }

        public static @NotNull ResourceLocation getFlowLoc(String name)
        {
            return rl("fluids/" + name +"/flow");
        }

        public static @NotNull ResourceLocation getOverlayLoc(String name)
        {
            return rl("fluids/" + name + "/overlay");
        }

        public static @NotNull ResourceLocation getBlockLocation(String name)
        {
            return rl("fluids/" + name + "/block");
        }

        public static @NotNull ResourceLocation getBucketLocation(String name)
        {
            return rl("fluids/" + name + "/bucket");
        }
    }
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
                public static final ChapterInfo TITLE = new ChapterInfo(rl("title"));
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
                public static final PageInfo PATCH_NOTES = new PageInfo(rl("patch_notes"));
                public static final PageInfo TEST_BASIC_1 = new PageInfo(rl("test_1"));
                public static final PageInfo TEST_BASIC_2 = new PageInfo(rl("test_next_lvl"));
                public static final PageInfo TEST_BASIC_3 = new PageInfo(rl("test_basic_3"));
                public static final PageInfo TEST_BASIC_4 = new PageInfo(rl("test_basic_4"));
                public static final PageInfo TEST_BASIC_5 = new PageInfo(rl("test_basic_5"));
                public static final PageInfo TEST_BASIC_6 = new PageInfo(rl("test_basic_6"));
                public static final PageInfo TEST_BASIC_7 = new PageInfo(rl("test_basic_7"));
                public static final PageInfo TEST_BASIC_8 = new PageInfo(rl("test_basic_8"));
                public static final PageInfo TEST_BASIC_9 = new PageInfo(rl("test_basic_9"));
                public static final PageInfo TEST_BASIC_10 = new PageInfo(rl("test_basic_10"));
                public static final PageInfo TEST_BASIC_11 = new PageInfo(rl("test_basic_11"));
                public static final PageInfo TEST_BASIC_12 = new PageInfo(rl("test_basic_12"));
                public static final PageInfo TEST_BASIC_13 = new PageInfo(rl("test_basic_13"));
                public static final PageInfo TEST_BASIC_14 = new PageInfo(rl("test_basic_14"));
                public static final PageInfo TEST_BASIC_15 = new PageInfo(rl("test_basic_15"));
                public static final PageInfo TEST_BASIC_16 = new PageInfo(rl("test_basic_16"));
                public static final PageInfo TEST_BASIC_17 = new PageInfo(rl("test_basic_17"));
                public static final PageInfo TEST_BASIC_18 = new PageInfo(rl("test_basic_18"));
                public static final PageInfo TEST_BASIC_19 = new PageInfo(rl("test_basic_19"));
                public static final PageInfo TEST_BASIC_20 = new PageInfo(rl("test_basic_20"));
                public static final PageInfo TEST_BASIC_21 = new PageInfo(rl("test_basic_21"));
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
                    public static final String TICKS = rl(GUI + ".book.page.component.recipe.ticks").toLanguageKey();
                    public static final String EXP = rl(GUI + ".book.page.component.recipe.experience").toLanguageKey();
                    public static final String ARROW_LEFT = rl(GUI + "book.page.component.arrow.back").toLanguageKey();
                    public static final String ARROW_RIGHT = rl(GUI + "book.page.component.arrow.forward").toLanguageKey();
                    public static final String ARROW_TO_TITLE = rl(GUI + "book.page.component.arrow.to_title").toLanguageKey();
                }
            }
        }

        public static @NotNull ResourceLocation getTexturePath(String str)
        {
            return rl("textures/" + str + ".png");
        }
    }

    public static final class Capabilities
    {
        public static final class Fluids
        {
            public static final String HANDLER = "fluid_handler";
            public static final String TANK = "tank";
            public static final String TANKS = "tanks";
            public static final String MODE = "mode";
            public static final String MODES = "modes";
            public static final String INTS = "ints";
            public static final String INDEX_TO_MODE = "index_to_mode";
            public static final class Holder
            {
                public static final String FLUID = "fluid";
                public static final String CAPACITY = "capacity";
            }
        }

        public static final class Items
        {
            public static final String HANDLER = "item_handler";
            public static final String HOLDER = "holder";
            public static final String HOLDERS = "holders";
            public static final String MODE = "mode";
            public static final String MODES = "modes";
            public static final String INTS = "ints";
            public static final String INDEX_TO_MODE = "index_to_mode";
            public static final class Holder
            {
                public static final String ITEM = "item";
                public static final String CAPACITY = "capacity";
            }
        }
    }

    public static @NotNull ResourceLocation rl(String name)
    {
        Preconditions.checkNotNull(name);
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static @NotNull ResourceLocation mineRl(String name)
    {
        Preconditions.checkNotNull(name);
        return ResourceLocation.withDefaultNamespace(name);
    }

    public static @NotNull ResourceLocation neoRl(String name)
    {
        Preconditions.checkNotNull(name);
        return ResourceLocation.fromNamespaceAndPath("neoforge", name);
    }

    public static @NotNull String rlStr(String name)
    {
        return rl(name).toString();
    }
}
