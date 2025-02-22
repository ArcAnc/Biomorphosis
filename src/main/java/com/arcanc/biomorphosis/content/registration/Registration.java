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
import com.arcanc.biomorphosis.content.block.LureCampfireBlock;
import com.arcanc.biomorphosis.content.block.block_entity.LureCampfireBE;
import com.arcanc.biomorphosis.content.block.norph.NorphBlock;
import com.arcanc.biomorphosis.content.block.norph.NorphOverlay;
import com.arcanc.biomorphosis.content.block.norph.NorphSource;
import com.arcanc.biomorphosis.content.block.norph.NorphStairs;
import com.arcanc.biomorphosis.content.book_data.BookChapterData;
import com.arcanc.biomorphosis.content.book_data.BookPageData;
import com.arcanc.biomorphosis.content.fluid.BioBaseFluid;
import com.arcanc.biomorphosis.content.fluid.BioFluidType;
import com.arcanc.biomorphosis.content.gui.container_menu.BioContainerMenu;
import com.arcanc.biomorphosis.content.item.*;
import com.arcanc.biomorphosis.mixin.FluidTypeRarityAccessor;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.enumextensions.RarityExtension;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Registration
{
    public static class BlockReg
    {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Database.MOD_ID);

        public static final DeferredBlock<BioBaseBlock> FLESH = register("flesh", BioBaseBlock :: new,
                properties -> properties.
                        mapColor(MapColor.COLOR_PURPLE).
                        strength(3, 3).
                        sound(SoundType.HONEY_BLOCK).
                        isValidSpawn((state, level, pos, value) -> false),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<LureCampfireBlock> LURE_CAMPFIRE = register("lure_campfire", LureCampfireBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BASS).
                        strength(2, 2).
                        sound(SoundType.WOOD).
                        lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0).
                        noOcclusion().
                        ignitedByLava(),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<NorphSource> NORPH_SOURCE = register("norph_source", NorphSource :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(3,3).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion().
                        randomTicks().
                        isValidSpawn((state, level, pos, value) -> false),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<NorphBlock> NORPH = register("norph", NorphBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(0.3f).
                        sound(SoundType.HONEY_BLOCK).
                        isValidSpawn((state, level, pos, value) -> false),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<NorphStairs> NORPH_STAIRS = register("norph_stairs", properties -> new NorphStairs(NORPH.get().defaultBlockState(), properties),
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(0.3f).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion().
                        isValidSpawn((state, level, pos, value) -> false),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<NorphOverlay> NORPH_OVERLAY = register("norph_overlay", NorphOverlay :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(0.3f).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion().
                        noCollission().
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

    public static class BETypeReg
    {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
                BuiltInRegistries.BLOCK_ENTITY_TYPE, Database.MOD_ID);

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LureCampfireBE>> BE_LURE_CAMPFIRE = BLOCK_ENTITIES.register(
                "lure_campfire",
                makeType(LureCampfireBE :: new, BlockReg.LURE_CAMPFIRE));

        public static <T extends BlockEntity> @NotNull Supplier<BlockEntityType<T>> makeType(BlockEntityType.BlockEntitySupplier<T> create, Supplier<? extends Block> valid)
        {
            return makeTypeMultipleBlocks(create, ImmutableSet.of(valid));
        }

        public static <T extends BlockEntity> @NotNull Supplier<BlockEntityType<T>> makeTypeMultipleBlocks(
                BlockEntityType.BlockEntitySupplier<T> create, Collection<? extends Supplier<? extends Block>> valid
        )
        {
            return () -> new BlockEntityType<>(
                    create, valid.stream().map(Supplier :: get).collect(Collectors.toUnmodifiableSet()));
        }

        public static void init (final IEventBus modEventBus)
        {
            BLOCK_ENTITIES.register(modEventBus);
        }
    }

    public static class ItemReg
    {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Database.MOD_ID);

        public static final DeferredItem<BioIconItem> CREATIVE_TAB_ICON = registerIcon("creative_tab_icon");
        public static final DeferredItem<BioBaseItem> QUEENS_BRAIN = register("queens_brain", BioBaseItem :: new, properties -> properties.rarity(RarityExtension.BIO_RARE.getValue()));
        public static final DeferredItem<WrenchItem> WRENCH = register("wrench", WrenchItem :: new, properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));
        public static final DeferredItem<BioBaseItem> FLESH_PIECE = register("flesh_piece", BioBaseItem::new, properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));
        public static final DeferredItem<BioBook> BOOK = register("book", BioBook :: new, properties -> properties.stacksTo(1).rarity(RarityExtension.BIO_COMMON.getValue()));

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

    public static class FluidReg
    {
        public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Database.MOD_ID);
        public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Database.MOD_ID);

        public static final FluidEntry BIOMASS = FluidEntry.make("biomass",
                new BioFluidType.ColorParams(new Vector4f(130, 33, 16, 255), new Vector4f(178, 78, 53, 255), 80, (minColor, maxColor, maxTime) ->
                {
                    Vector4f minimumColor = minColor.div(255f, new Vector4f());
                    Vector4f maximumColor = maxColor.div(255f, new Vector4f());
                    Minecraft mc = RenderHelper.mc();
                    Level level = mc.level;
                    if (level == null)
                        return -1;
                    long levelTime = level.getGameTime();
                    float partialTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                    float halfTime = maxTime / 2f;

                    float time = (levelTime + partialTicks) % maxTime;
                    if (time < halfTime)
                        return MathHelper.ColorHelper.lerp(time / halfTime, MathHelper.ColorHelper.color(maximumColor), MathHelper.ColorHelper.color(minimumColor));
                    else
                        return MathHelper.ColorHelper.lerp((time - halfTime) / halfTime, MathHelper.ColorHelper.color(minimumColor), MathHelper.ColorHelper.color(maximumColor));
                }),
                (camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor, colorParams) ->
                        MathHelper.ColorHelper.vector4fFromARGB(colorParams.getColor()),
                (camera, mode, renderDistance, partialTick, fogParameters, colorParams) ->
                    {
                        Vector4f color = MathHelper.ColorHelper.vector4fFromARGB(colorParams.getColor());
                        return new FogParameters(0.00f, 0.5f, FogShape.CYLINDER, color.x(), color.y(), color.z(), color.w());
                    },
                props -> props.slopeFindDistance(2).levelDecreasePerBlock(2).explosionResistance(100),
                typeProps -> typeProps.
                        lightLevel(0).
                        density(3000).
                        viscosity(6000).
                        rarity(RarityExtension.BIO_COMMON.getValue()).
                        canSwim(true).
                        canExtinguish(true).
                        canHydrate(true).
                        fallDistanceModifier(0f));

        public record FluidEntry(DeferredHolder<Fluid, BioBaseFluid> still,
                                  DeferredHolder<Fluid, BioBaseFluid> flowing,
                                  DeferredBlock<LiquidBlock> block,
                                  DeferredItem<BucketItem> bucket,
                                  DeferredHolder<FluidType, BioFluidType> type,
                                  List<Property<?>> properties)
        {
            private static @NotNull FluidEntry make(String name, BioFluidType.FogGetter fogColor, BioFluidType.FogOptionsGetter fogOptions, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, new BioFluidType.ColorParams(new Vector4f(0,0,0,0), new Vector4f(0,0,0,0), 1, (vector4f, vector4f2, integer) -> 0xffffffff), fogColor, fogOptions, props);
            }

            private static @NotNull FluidEntry make(String name, BioFluidType.ColorParams colorParams, BioFluidType.FogGetter fogColor, BioFluidType.FogOptionsGetter fogOptions, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, 0, Database.FluidInfo.getStillLoc(name), Database.FluidInfo.getFlowLoc(name), Database.FluidInfo.getOverlayLoc(name), colorParams, fogColor, fogOptions, props);
            }

            private static @NotNull FluidEntry make(String name, int burnTime, BioFluidType.ColorParams colorParams, BioFluidType.FogGetter fogColor, BioFluidType.FogOptionsGetter fogOptions, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, burnTime, Database.FluidInfo.getStillLoc(name), Database.FluidInfo.getFlowLoc(name), Database.FluidInfo.getOverlayLoc(name), colorParams, fogColor, fogOptions, props);
            }

            private static @NotNull FluidEntry make(String name, ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, BioFluidType.ColorParams colorParams, BioFluidType.FogGetter fogColor, BioFluidType.FogOptionsGetter fogOptions, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, 0, stillTex, flowingTex, overlayTex, colorParams, fogColor, fogOptions, props);
            }

            private static @NotNull FluidEntry make(
                    String name, BioFluidType.ColorParams colorParams, BioFluidType.FogGetter fogColor, BioFluidType.FogOptionsGetter fogOptions, Consumer<BaseFlowingFluid.Properties> props, Consumer<FluidType.Properties> buildAttributes
            )
            {
                return make(name, 0, Database.FluidInfo.getStillLoc(name), Database.FluidInfo.getFlowLoc(name), Database.FluidInfo.getOverlayLoc(name), colorParams, fogColor, fogOptions, props, buildAttributes);
            }

            private static @NotNull FluidEntry make(String name, int burnTime, ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, BioFluidType.ColorParams colorParams, BioFluidType.FogGetter fogColor, BioFluidType.FogOptionsGetter fogOptions, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, burnTime, stillTex, flowingTex, overlayTex, colorParams, fogColor, fogOptions, props, null);
            }

            private static @NotNull FluidEntry make(
                    String name, int burnTime,
                    ResourceLocation stillTex,
                    ResourceLocation flowingTex,
                    ResourceLocation overlayTex,
                    BioFluidType.ColorParams colorParams,
                    BioFluidType.FogGetter fogColor,
                    BioFluidType.FogOptionsGetter fogOptions,
                    @Nullable Consumer<BaseFlowingFluid.Properties> props,
                    @Nullable Consumer<FluidType.Properties> buildAttributes
            )
            {
                return make(
                        name, burnTime, stillTex, flowingTex, overlayTex, colorParams, fogColor, fogOptions, BioBaseFluid.BioFluidSource :: new, BioBaseFluid.BioFluidFlowing :: new, props, buildAttributes,
                        ImmutableList.of()
                );
            }

            private static @NotNull FluidEntry make(
                    String name,
                    int burnTime,
                    ResourceLocation stillTex,
                    ResourceLocation flowingTex,
                    ResourceLocation overlayTex,
                    BioFluidType.ColorParams colorParams,
                    BioFluidType.FogGetter fogColor,
                    BioFluidType.FogOptionsGetter fogOptions,
                    Function<BaseFlowingFluid.Properties, ? extends BioBaseFluid> makeStill,
                    Function<BaseFlowingFluid.Properties, ? extends BioBaseFluid> makeFlowing,
                    @Nullable Consumer<BaseFlowingFluid.Properties> props,
                    @Nullable Consumer<FluidType.Properties> buildAttributes,
                    List<Property<?>> properties)
            {
                FluidType.Properties builder = FluidType.Properties.create().
                        sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).
                        sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY).
                        descriptionId(Database.rl("fluid_type" + "." + name).toLanguageKey());
                if(buildAttributes!=null)
                    buildAttributes.accept(builder);
                DeferredHolder<FluidType, BioFluidType> type = FLUID_TYPES.register(
                        name, () -> makeTypeWithTextures(builder, stillTex, flowingTex, overlayTex, colorParams, fogColor, fogOptions)
                );
                Mutable<FluidEntry> thisMutable = new MutableObject<>();
                BioBaseFluid.FluidPropsGetter fluidProps = BaseFlowingFluid.Properties :: new;
                DeferredHolder<Fluid, BioBaseFluid> still = FLUIDS.register(Database.FluidInfo.getStillLoc(name).getPath(), () -> BioBaseFluid.makeFluid(makeStill,
                        fluidProps.get(
                                        thisMutable.getValue().type(),
                                        thisMutable.getValue().still(),
                                        thisMutable.getValue().flowing()).
                                block(thisMutable.getValue().block()).
                                bucket(thisMutable.getValue().bucket()),
                        props));
                DeferredHolder<Fluid, BioBaseFluid> flowing = FLUIDS.register(Database.FluidInfo.getFlowLoc(name).getPath(), () -> BioBaseFluid.makeFluid(makeFlowing,
                        fluidProps.get(
                                        thisMutable.getValue().type(),
                                        thisMutable.getValue().still(),
                                        thisMutable.getValue().flowing()).
                                block(thisMutable.getValue().block()).
                                bucket(thisMutable.getValue().bucket()),
                        props));
                ResourceLocation blockId = Database.FluidInfo.getBlockLocation(name);
                DeferredBlock<LiquidBlock> block = BlockReg.BLOCKS.register(blockId.getPath(),
                        () -> new LiquidBlock(thisMutable.getValue().still().get(), BlockReg.setId(blockId.getPath(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).
                                noLootTable())));
                ResourceLocation bucketId = Database.FluidInfo.getBucketLocation(name);
                DeferredItem<BucketItem> bucket = ItemReg.ITEMS.register(bucketId.getPath(), () -> makeBucket(bucketId, still, builder, burnTime));
                FluidEntry entry = new FluidEntry(still, flowing, block, bucket, type, properties);
                thisMutable.setValue(entry);
                return entry;
            }

            private static @NotNull BioFluidType makeTypeWithTextures(FluidType.Properties props, ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, BioFluidType.ColorParams colorParams, BioFluidType.FogGetter fogColor, BioFluidType.FogOptionsGetter fogOptions)
            {
                return new BioFluidType(stillTex, flowingTex, overlayTex, colorParams, fogColor, fogOptions, props);
            }

            private static @NotNull BucketItem makeBucket (@NotNull ResourceLocation id, @NotNull DeferredHolder<Fluid, BioBaseFluid> still, FluidType.@NotNull Properties props, int burnTime)
            {
                return new BioBucketItem(still.get(), ItemReg.setId(id.getPath(), new Item.Properties().
                        stacksTo(1).
                        rarity(((FluidTypeRarityAccessor) (Object)props).getRarity()).
                        craftRemainder(Items.BUCKET),
                        false))
                {
                    @Override
                    public int getBurnTime(@NotNull ItemStack itemStack,
                                           @Nullable RecipeType<?> recipeType,
                                           @NotNull FuelValues fuelValues)
                    {
                        return burnTime;
                    }
                };
            }
        }

        private static void init (@NotNull final IEventBus bus)
        {
            FLUIDS.register(bus);
            FLUID_TYPES.register(bus);
        }
    }

    public static class MenuTypeReg
    {
        public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Database.MOD_ID);

        public static <T, C extends BioContainerMenu>
        @NotNull ArgContainer<T, C> registerArg(
                String name, ArgContainerConstructor<T, C> container, ClientContainerConstructor<C> client
        )
        {
            DeferredHolder<MenuType<?>, MenuType<C>> typeRef = registerType(name, client);
            return new ArgContainer<>(typeRef, container);
        }

        private static <C extends BioContainerMenu>
        @NotNull DeferredHolder<MenuType<?>, MenuType<C>> registerType(String name, ClientContainerConstructor<C> client)
        {
            return MENU_TYPES.register(
                    name, () -> {
                        Mutable<MenuType<C>> typeBox = new MutableObject<>();
                        MenuType<C> type = IMenuTypeExtension.create((id, inv, data) -> client.construct(typeBox.getValue(), id, inv, data.readBlockPos()));
                        typeBox.setValue(type);
                        return type;
                    }
            );
        }

        public static class ArgContainer<T, C extends BioContainerMenu>
        {
            private final DeferredHolder<MenuType<?>, MenuType<C>> type;
            private final ArgContainerConstructor<T, C> factory;

            private ArgContainer(DeferredHolder<MenuType<?>, MenuType<C>> type, ArgContainerConstructor<T, C> factory)
            {
                this.type = type;
                this.factory = factory;
            }

            public C create(int windowId, Inventory playerInv, T tile)
            {
                return factory.construct(getType(), windowId, playerInv, tile);
            }

            public MenuProvider provide(T arg)
            {
                return new MenuProvider()
                {
                    @Override
                    public @NotNull Component getDisplayName()
                    {
                        return Component.empty();
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(
                            int containerId, @NotNull Inventory inventory, @NotNull Player player
                    )
                    {
                        return create(containerId, inventory, arg);
                    }
                };
            }

            public MenuType<C> getType()
            {
                return type.get();
            }
        }

        public interface ArgContainerConstructor<T, C extends BioContainerMenu>
        {
            C construct(MenuType<C> type, int windowId, Inventory inventoryPlayer, T te);
        }

        public interface ClientContainerConstructor<C extends BioContainerMenu>
        {
            C construct(MenuType<C> type, int windowId, Inventory inventoryPlayer, BlockPos pos);
        }

        private static void init (@NotNull final IEventBus bus)
        {
            MENU_TYPES.register(bus);
        }
    }

    public static class CreativeTabReg
    {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Database.MOD_ID);

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () ->
                CreativeModeTab.builder().
                icon(() -> new ItemStack(ItemReg.CREATIVE_TAB_ICON.get())).
                title(Component.literal(Database.MOD_NAME)).
                displayItems(CreativeTabReg:: fill).
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

    public static class BookDataReg
    {
        public static final ResourceKey<Registry<BookPageData>> PAGE_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("book/page"));
        public static final ResourceKey<Registry<BookChapterData>> CHAPTER_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("book/chapter"));

        private static void registerDataPackRegister(final DataPackRegistryEvent.@NotNull NewRegistry event)
        {
            event.dataPackRegistry(PAGE_KEY, BookPageData.CODEC, BookPageData.CODEC, regBuilder -> makeRegistry(regBuilder, PAGE_KEY));
            event.dataPackRegistry(CHAPTER_KEY, BookChapterData.CODEC, BookChapterData.CODEC, regBuilder -> makeRegistry(regBuilder, CHAPTER_KEY));
        }

        public static void init (@NotNull final IEventBus modEventBus)
        {
            modEventBus.addListener(BookDataReg :: registerDataPackRegister);
        }
    }

    public static void init(@NotNull final IEventBus bus)
    {
        BookDataReg.init(bus);
        BlockReg.init(bus);
        ItemReg.init(bus);
        FluidReg.init(bus);
        BETypeReg.init(bus);
        MenuTypeReg.init(bus);
        CreativeTabReg.init(bus);
    }

    private static <T> void makeRegistry(@NotNull RegistryBuilder<T> registryBuilder, @NotNull ResourceKey<? extends Registry<T>> key)
    {
        registryBuilder.defaultKey(key.location()).maxId(Integer.MAX_VALUE - 1).sync(true);
    }
}
