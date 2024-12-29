/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.registration;

import com.arcanc.biomorphosis.content.item.BioBaseItem;
import com.arcanc.biomorphosis.content.item.BioIconItem;
import com.arcanc.biomorphosis.content.item.ItemInterfaces;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.enumextensions.RarityExtension;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Registration
{
    public static class BlockReg
    {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Database.MOD_ID);

        private static void init (@NotNull final IEventBus bus)
        {
            BLOCKS.register(bus);
        }
    }

    public static class ItemReg
    {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Database.MOD_ID);

        public static final DeferredItem<BioBaseItem> test = register("common_item", BioBaseItem :: new, properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));
        public static final DeferredItem<BioBaseItem> test_rare = register("rare_item", BioBaseItem :: new, properties -> properties.rarity(RarityExtension.BIO_RARE.getValue()));
        public static final DeferredItem<BioBaseItem> test_ultra_rare = register("ultra_rare_item", BioBaseItem :: new, properties -> properties.rarity(RarityExtension.BIO_ULTRA_RARE.getValue()));
        public static final DeferredItem<BioIconItem> CREATIVE_TAB_ICON = registerIcon("creative_tab_icon");

        private static DeferredItem<BioIconItem> registerIcon(String name)
        {
            Item.Properties props = setId(name, new Item.Properties().stacksTo(1));
            return ITEMS.register(name, ()-> new BioIconItem(props));
        }

        private static <I extends Item> DeferredItem<I> register(String name, Function<Item.Properties, I> item, Consumer<Item.Properties> additionalProps)
        {
            Item.Properties props = setId(name, props(additionalProps));
            return ITEMS.register(name, ()-> item.apply(props));
        }

        private static Item.Properties setId(String id, Item.Properties props)
        {
            ResourceKey<Item> resourceKey = ResourceKey.create(Registries.ITEM, Database.rl(id));
            return props.setId(resourceKey).overrideDescription(resourceKey.location().withPrefix("item.").toLanguageKey().replace(':', '.').replace('/', '.'));
        }

        private static Item.Properties props (Consumer<Item.Properties> additionalProps)
        {
            return Util.make(new Item.Properties(), additionalProps);
        }

        private static void init (@NotNull final IEventBus bus)
        {
            ITEMS.register(bus);
        }
    }

    public static class CreativeTabsReg
    {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Database.MOD_ID);

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () ->
                CreativeModeTab.builder().
                icon(() -> new ItemStack(ItemReg.CREATIVE_TAB_ICON.get())).
                title(Component.literal(Database.MOD_NAME)).
                displayItems(CreativeTabsReg :: fill).
                build());

        private static void fill(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output)
        {
            for(DeferredHolder<Item, ? extends Item> holder : ItemReg.ITEMS.getEntries())
            {
                Item item = holder.value();

                if(item instanceof ItemInterfaces.IMustAddToCreativeTab i && i.addSelfToCreativeTab())
                    output.accept(item);
            }
        }

        private static void init (@NotNull final IEventBus bus)
        {
            CREATIVE_MODE_TABS.register(bus);
        }
    }

    public static void init(@NotNull final IEventBus bus)
    {
        BlockReg.init(bus);
        ItemReg.init(bus);
        CreativeTabsReg.init(bus);
    }
}
