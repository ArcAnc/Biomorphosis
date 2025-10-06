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

    public static final class Integration
    {
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

        public static final class JeiInfo
        {
            public static final ResourceLocation ID = rl("plugin");
            public static final String CHAMBER_RECIPE_NAME = rl("jei.chamber.recipe_category.title").toLanguageKey();
            public static final String CRUSHER_RECIPE_NAME = rl("jei.crusher.recipe_category.title").toLanguageKey();
            public static final String STOMACH_RECIPE_NAME = rl("jei.stomach.recipe_category.title").toLanguageKey();
            public static final String FORGE_RECIPE_NAME = rl("jei.forge.recipe_category.title").toLanguageKey();


            public static final String REQUIRED = rl("jei.required.title").toLanguageKey();
            public static final String OPTIONAL = rl("jei.optional.title").toLanguageKey();
            public static final String PER_TICK = rl("jei.per_tick.title").toLanguageKey();
            public static final String WITH_ADRENALINE = rl("jei.with_boost.adrenaline.title").toLanguageKey();
            public static final String WITH_LYMPH = rl("jei.with_boost.lymph.title").toLanguageKey();
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

        public static final String HOLD_SHIFT = rl(GUI + ".tooltip.info.hold_shift").toLanguageKey();

        public static final class ChamberButton
        {
            public static final String START = rl(GUI + ".tooltip.chamber_button.start").toLanguageKey();
            public static final String PROCESS = rl(GUI + ".tooltip.chamber_button.work").toLanguageKey();
        }

        public static final class InfoArea
        {
            public static final class ProgressBar
            {
                public static class Tooltip
                {
                    public static final String PERCENT = rl(GUI + ".tooltip.info.progress_bar.percent").toLanguageKey();
                    public static final String DIRECT = rl(GUI + ".tooltip.info.progress_bar.direct").toLanguageKey();
                }
            }
            public static final class FluidArea
            {
                public static final class Tooltip
                {
                    public static final String NORMAL_SHORT_TOOLTIP = rl(GUI + ".tooltip.info.fluid.normal.short").toLanguageKey();
                    public static final String NORMAL_EXTENDED_TOOLTIP = rl(GUI + ".tooltip.info.fluid.normal.extended").toLanguageKey();
                    public static final String ADVANCED_TOOLTIP_DENSITY = rl(GUI + ".tooltip.info.fluid.advanced.density").toLanguageKey();
                    public static final String ADVANCED_TOOLTIP_TEMPERATURE = rl(GUI + ".tooltip.info.fluid.advanced.temperature").toLanguageKey();
                    public static final String ADVANCED_TOOLTIP_VISCOSITY = rl(GUI + ".tooltip.info.fluid.advanced.viscosity").toLanguageKey();
                }
            }
        }

        public static final class Textures
        {
            public static final class Tooltip
            {
                public static final ResourceLocation TOOLTIP_BACKGROUND = Database.rl("special");
                public static final ResourceLocation TOOLTIP_DECORATIONS = Database.rl("special");
            }
            public static final class JEI
            {
                public static final ResourceLocation TIME = Database.rl("textures/" + GUI + "/elements/jei/time.png");
                public static final ResourceLocation SECONDARY_OUTPUT = Database.rl("textures/" + GUI + "/elements/jei/secondary_output.png");
            }
        }

		public static final class GenomeData
		{
			public static final GeneInfo ACCURACY = new GeneInfo(Database.rl("accuracy"));
			public static final GeneInfo BALANCE = new GeneInfo(Database.rl("balance"));
			public static final GeneInfo DAMAGE = new GeneInfo(Database.rl("damage"));
			public static final GeneInfo FOOD = new GeneInfo(Database.rl("food"));
			public static final GeneInfo HEALTH = new GeneInfo(Database.rl("health"));
			public static final GeneInfo PROTECTION = new GeneInfo(Database.rl("protection"));
			public static final GeneInfo SPEED = new GeneInfo(Database.rl("speed"));
			
			public record GeneInfo(ResourceLocation id, ResourceLocation image)
			{
				public GeneInfo (ResourceLocation id)
				{
					this(id, id.withPrefix("textures/genome/"));
				}
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
                public static final PageInfo V001 = new PageInfo(rl("v001"));
                public static final PageInfo V002 = new PageInfo(rl("v002"));
                public static final PageInfo V003 = new PageInfo(rl("v003"));
                public static final PageInfo V004 = new PageInfo(rl("v004"));
                public static final PageInfo V005 = new PageInfo(rl("v005"));
                public static final PageInfo TEST_BASIC_1 = new PageInfo(rl("test_1"));
                public static final PageInfo TEST_BASIC_2 = new PageInfo(rl("test_next_lvl"));
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
                    public static final String ARROW_LEFT = rl(GUI + ".book.page.component.arrow.back").toLanguageKey();
                    public static final String ARROW_RIGHT = rl(GUI + ".book.page.component.arrow.forward").toLanguageKey();
                    public static final String ARROW_TO_TITLE = rl(GUI + ".book.page.component.arrow.to_title").toLanguageKey();
                }
            }
        }

        public static final class Sounds
        {
            public static final String BLOCK_DESTROYED = Database.rl("sound.subtitle.block.destroyed").toLanguageKey();
            public static final String BLOCK_PLACED = Database.rl("sound.subtitle.block.placed").toLanguageKey();
            public static final String BLOCK_STEP_NORMAL = Database.rl("sound.subtitle.block.step.normal").toLanguageKey();
            public static final String BLOCK_STEP_TRAMPLED = Database.rl("sound.subtitle.block.step.trampled").toLanguageKey();
            public static final String BLOCK_STEP_LEAF = Database.rl("sound.subtitle.block.step.leaf").toLanguageKey();

            public static final String BLOCK_CHEST_OPEN = Database.rl("sound.subtitle.block.chest.open").toLanguageKey();
            public static final String BLOCK_CHEST_CLOSE = Database.rl("sound.subtitle.block.chest.close").toLanguageKey();

            public static final String BLOCK_HIVE_DECO = Database.rl("sound.subtitle.block.hive_deco").toLanguageKey();

            public static final EntitySoundSubtitle QUEEN = new EntitySoundSubtitle("queen");
            public static final EntitySoundSubtitle KSIGG = new EntitySoundSubtitle("ksigg");
            public static final EntitySoundSubtitle LARVA = new EntitySoundSubtitle("larva");
            public static final EntitySoundSubtitle ZIRIS = new EntitySoundSubtitle("ziris");
            public static final EntitySoundSubtitle INFESTOR = new EntitySoundSubtitle("infestor");
            public static final EntitySoundSubtitle SWARMLING = new EntitySoundSubtitle("swarmling");
            public static final EntitySoundSubtitle GUARD = new EntitySoundSubtitle("guard");
            public static final EntitySoundSubtitle WORKER = new EntitySoundSubtitle("worker");

            public static class EntitySoundSubtitle
            {
                private final String IDLE;
                private final String HURT;
                private final String DEATH;

                public EntitySoundSubtitle(String name)
                {
                    this.IDLE = Database.rl("sound.subtitle." + name + ".idle").toLanguageKey();
                    this.HURT = Database.rl("sound.subtitle." + name + ".hurt").toLanguageKey();
                    this.DEATH = Database.rl("sound.subtitle." + name + ".death").toLanguageKey();
                }

                public String getIdle()
                {
                    return this.IDLE;
                }

                public String getHurt()
                {
                    return this.HURT;
                }

                public String getDeath()
                {
                    return this.DEATH;
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
