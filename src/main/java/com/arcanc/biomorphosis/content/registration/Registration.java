/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.registration;

import com.arcanc.biomorphosis.content.block.BioBaseBlock;
import com.arcanc.biomorphosis.content.item.*;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.enumextensions.RarityExtension;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Registration
{
    public static class BlockReg
    {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Database.MOD_ID);

        public static final DeferredBlock<BioBaseBlock> FLESH = register("flesh", BioBaseBlock::new,
                properties -> properties.
                        mapColor(MapColor.COLOR_PURPLE).
                        strength(3, 3).
                        sound(SoundType.HONEY_BLOCK).
                        isValidSpawn((state, level, pos, value) -> false),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        private static <B extends Block> @NotNull DeferredBlock<B> register (String name, Function<BlockBehaviour.Properties, B> block, Consumer<BlockBehaviour.Properties> additionalProps, Consumer<Item.Properties> itemAddProps)
        {
            BlockBehaviour.Properties props = setId(name, props(additionalProps));
            Item.Properties itemProps = ItemReg.setId(name, ItemReg.props(itemAddProps), true);
            DeferredBlock<B> blockGetter = BLOCKS.register(name, ()-> block.apply(props));
            ItemReg.ITEMS.register(name, () -> new BioBaseBlockItem(blockGetter.get(), itemProps));
            return blockGetter;
        }

        private static BlockBehaviour.@NotNull Properties setId(String id, BlockBehaviour.@NotNull Properties props)
        {
            ResourceKey<Block> resourceKey = ResourceKey.create(Registries.BLOCK, Database.rl(id));
            return props.setId(resourceKey).overrideDescription(resourceKey.location().withPrefix("block.").toLanguageKey().replace(':', '.').replace('/', '.'));
        }

        private static BlockBehaviour.@NotNull Properties props (Consumer<BlockBehaviour.Properties> additionalProps)
        {
            return Util.make(BlockBehaviour.Properties.of(), additionalProps);
        }

        private static void init (@NotNull final IEventBus bus)
        {
            BLOCKS.register(bus);
        }
    }

    public static class ItemReg
    {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Database.MOD_ID);

        public static final DeferredItem<BioIconItem> CREATIVE_TAB_ICON = registerIcon("creative_tab_icon");
        public static final DeferredItem<WrenchItem> WRENCH = register("wrench", WrenchItem :: new, properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));
        public static final DeferredItem<BioBaseItem> FLESH_PIECE = register("flesh_piece", BioBaseItem::new, properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        private static @NotNull DeferredItem<BioIconItem> registerIcon(String name)
        {
            Item.Properties props = setId(name, new Item.Properties().stacksTo(1), false);
            return ITEMS.register(name, ()-> new BioIconItem(props));
        }

        private static <I extends Item> @NotNull DeferredItem<I> register(String name, Function<Item.Properties, I> item, Consumer<Item.Properties> additionalProps)
        {
            Item.Properties props = setId(name, props(additionalProps), false);
            return ITEMS.register(name, ()-> item.apply(props));
        }

        private static Item.@NotNull Properties setId(String id, Item.@NotNull Properties props, boolean blockItem)
        {
            ResourceKey<Item> resourceKey = ResourceKey.create(Registries.ITEM, Database.rl(id));
            return props.setId(resourceKey).overrideDescription(resourceKey.location().withPrefix(blockItem ? "block." : "item." ).toLanguageKey().replace(':', '.').replace('/', '.'));
        }

        @Contract("_ -> new")
        private static Item.@NotNull Properties props (Consumer<Item.Properties> additionalProps)
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
