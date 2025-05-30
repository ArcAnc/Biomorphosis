/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.registration;

import com.arcanc.biomorphosis.content.block.*;
import com.arcanc.biomorphosis.content.block.block_entity.*;
import com.arcanc.biomorphosis.content.block.block_entity.ber.*;
import com.arcanc.biomorphosis.content.block.multiblock.*;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.norph.NorphBlock;
import com.arcanc.biomorphosis.content.block.norph.NorphOverlay;
import com.arcanc.biomorphosis.content.block.norph.NorphStairs;
import com.arcanc.biomorphosis.content.block.norph.source.NorphSource;
import com.arcanc.biomorphosis.content.block.norph.source.NorphSourceBlock;
import com.arcanc.biomorphosis.content.book_data.BookChapterData;
import com.arcanc.biomorphosis.content.book_data.BookPageData;
import com.arcanc.biomorphosis.content.entity.BioEntityType;
import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.renderer.QueenRenderer;
import com.arcanc.biomorphosis.content.fluid.BioBaseFluid;
import com.arcanc.biomorphosis.content.fluid.BioFluidType;
import com.arcanc.biomorphosis.content.gui.container_menu.BioContainerMenu;
import com.arcanc.biomorphosis.content.gui.container_menu.ChamberMenu;
import com.arcanc.biomorphosis.content.gui.screen.container.BioContainerScreen;
import com.arcanc.biomorphosis.content.gui.screen.container.ChamberScreen;
import com.arcanc.biomorphosis.content.item.*;
import com.arcanc.biomorphosis.data.recipe.CrusherRecipe;
import com.arcanc.biomorphosis.data.recipe.ForgeRecipe;
import com.arcanc.biomorphosis.data.recipe.StomachRecipe;
import com.arcanc.biomorphosis.data.recipe.display.CrusherRecipeDisplay;
import com.arcanc.biomorphosis.data.recipe.display.ForgeRecipeDisplay;
import com.arcanc.biomorphosis.data.recipe.display.StomachRecipeDisplay;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.data.recipe.input.CrusherRecipeInput;
import com.arcanc.biomorphosis.data.recipe.input.ForgeRecipeInput;
import com.arcanc.biomorphosis.data.recipe.input.StomachRecipeInput;
import com.arcanc.biomorphosis.data.recipe.slot_display.ItemStackWithChanceDisplay;
import com.arcanc.biomorphosis.data.recipe.slot_display.ResourcesDisplay;
import com.arcanc.biomorphosis.mixin.FluidTypeRarityAccessor;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.enumextensions.RarityExtension;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class Registration
{
    public static class DataComponentsReg
    {
        public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Database.MOD_ID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Vec3>>> FLUID_TRANSMIT_DATA = TYPES.registerComponentType(
                Database.DataComponents.FLUID_TRANSMIT,
                builder -> builder.
                        persistent(Vec3.CODEC.
                            sizeLimitedListOf(2)).
                        networkSynchronized(ByteBufCodecs.
                                <ByteBuf, Vec3>list(2).
                                apply(Vec3.STREAM_CODEC)).
                        cacheEncoding());

        private static void init (@NotNull final IEventBus bus)
        {
            TYPES.register(bus);
        }
    }

    public static class EntityReg
    {

        public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Database.MOD_ID);

        public static final EntityEntry<Queen> MOB_QUEEN = makeEntityType(
                "queen",
                Queen.class,
                Queen :: new,
                MobCategory.MONSTER,
                builder -> builder.
                        canSpawnFarFromPlayer().
                        clientTrackingRange(6).
                        sized(1.6f, 2.3f).
                        eyeHeight(2.05f).
                        immuneTo(Blocks.POWDER_SNOW, Blocks.SWEET_BERRY_BUSH).
                        updateInterval(4).
                        attributeProvider(() -> LivingEntity.createLivingAttributes().
                                add(Attributes.MAX_HEALTH, 20).
                                add(Attributes.ATTACK_DAMAGE, 3).
                                add(Attributes.FOLLOW_RANGE, 32).
                                add(Attributes.MOVEMENT_SPEED, 0.15f).
                                add(Attributes.ARMOR, 4)).
                        rendererProvider(QueenRenderer :: new),
                itemProps -> itemProps.rarity(RarityExtension.BIO_ULTRA_RARE.getValue()));

        /*public static final EntityEntry<QueenGuard> MOB_QUEEN_GUARD = makeEntityType(
                "queen_guard",
                QueenGuard.class,
                QueenGuard :: new,
                MobCategory.MONSTER,
                builder -> builder.
                        canSpawnFarFromPlayer().
                        clientTrackingRange(6).
                        eyeHeight(2.1f).
                        sized(2.3f, 2.2f).
                        immuneTo(Blocks.POWDER_SNOW, Blocks.SWEET_BERRY_BUSH).
                        updateInterval(5).
                        attributeProvider(() -> LivingEntity.createLivingAttributes().
                                add(Attributes.MAX_HEALTH, 30).
                                add(Attributes.ATTACK_DAMAGE, 6).
                                add(Attributes.FOLLOW_RANGE, 12).
                                add(Attributes.MOVEMENT_SPEED, 0.8f).
                                add(Attributes.ARMOR, 6)),
                itemProps -> itemProps.rarity(RarityExtension.BIO_ULTRA_RARE.getValue()));*/

        private static <T extends Entity> @NotNull EntityEntry<T> makeEntityType(String name,
                                                                      Class<T> entityClass,
                                                                      EntityType.EntityFactory<T> factory,
                                                                      MobCategory category,
                                                                      UnaryOperator<BioEntityType.BioTypeBuilder<T>> typeBuilder,
                                                                      Consumer<Item.Properties> additionalProperties)
        {
            return new EntityEntry<>(name, entityClass, factory, category, typeBuilder, additionalProperties);
        }

        public static class EntityEntry<T extends Entity>
        {
            private final DeferredHolder<EntityType<?>, EntityType<T>> entityHolder;
            private final DeferredHolder<Item, SpawnEggItem> eggHolder;

            @SuppressWarnings("unchecked")
            public EntityEntry(String name,
                               Class<T> entityClass,
                               EntityType.EntityFactory<T> factory,
                               MobCategory category,
                               UnaryOperator<BioEntityType.BioTypeBuilder<T>> typeBuilder,
                               Consumer<Item.Properties> additionalProperties)
            {
                this.entityHolder = ENTITY_TYPES.register(name, key -> typeBuilder.apply(BioEntityType.BioTypeBuilder.of(factory, category)).build(ResourceKey.create(Registries.ENTITY_TYPE, key)));
                this.eggHolder = Mob.class.isAssignableFrom(entityClass) ?
                        Registration.ItemReg.register("spawning_egg_" + name, properties -> new BioSpawnEgg((EntityType<? extends Mob>) entityHolder.get(), properties), additionalProperties) :
                        null;
            }

            public DeferredHolder<EntityType<?>, EntityType<T>> getEntityHolder()
            {
                return entityHolder;
            }

            @Nullable
            public DeferredHolder<Item, SpawnEggItem> getEggHolder()
            {
                return eggHolder;
            }
        }

        public static void init(IEventBus bus)
        {
            ENTITY_TYPES.register(bus);
        }
    }

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

        public static final DeferredBlock<NorphSourceBlock> NORPH_SOURCE = register("norph_source", NorphSourceBlock:: new,
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

        public static final DeferredBlock<BioFluidStorageBlock> FLUID_STORAGE = register("fluid_storage", BioFluidStorageBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2,2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<BioFluidTransmitterBlock> FLUID_TRANSMITTER = register("fluid_transmitter", BioFluidTransmitterBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2,2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<BioCrusherBlock> CRUSHER = register("crusher", BioCrusherBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2, 2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<BioStomachBlock> STOMACH = register("stomach", BioStomachBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2, 2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<BioCatcherBlock> CATCHER = register("catcher", BioCatcherBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2, 2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<BioForgeBlock> FORGE = register("forge", BioForgeBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2, 2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_COMMON.getValue()));

        public static final DeferredBlock<MultiblockFluidStorageBlock> MULTIBLOCK_FLUID_STORAGE = register("multiblock_fluid_storage", MultiblockFluidStorageBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2, 2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_RARE.getValue()));

        public static final DeferredBlock<MultiblockChamberBlock> MULTIBLOCK_CHAMBER = register("multiblock_chamber", MultiblockChamberBlock :: new,
                properties -> properties.mapColor(MapColor.PODZOL).
                        instrument(NoteBlockInstrument.BIT).
                        strength(2, 2).
                        sound(SoundType.HONEY_BLOCK).
                        noOcclusion(),
                properties -> properties.rarity(RarityExtension.BIO_RARE.getValue()));

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
                makeType(LureCampfireBE :: new,
                         LureCampfireRenderer:: new,
                         BlockReg.LURE_CAMPFIRE));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioFluidStorage>> BE_FLUID_STORAGE = BLOCK_ENTITIES.register(
                "fluid_storage",
                makeType(BioFluidStorage :: new,
                         BioFluidStorageRenderer :: new,
                         BlockReg.FLUID_STORAGE));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioFluidTransmitter>> BE_FLUID_TRANSMITTER = BLOCK_ENTITIES.register(
                "fluid_transmitter",
                makeType(BioFluidTransmitter :: new,
                        BioFluidTransmitterRenderer :: new,
                        BlockReg.FLUID_TRANSMITTER));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NorphSource>> BE_NORPH_SOURCE = BLOCK_ENTITIES.register(
                "norph_source",
                makeType(NorphSource :: new,
                        NorphSourceRenderer :: new,
                        BlockReg.NORPH_SOURCE));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioCrusher>> BE_CRUSHER = BLOCK_ENTITIES.register(
                "crusher",
                makeType(BioCrusher :: new,
                        BioCrusherRenderer :: new,
                        BlockReg.CRUSHER));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioStomach>> BE_STOMACH = BLOCK_ENTITIES.register(
                "stomach",
                makeType(BioStomach :: new,
                        BioStomachRenderer :: new,
                        BlockReg.STOMACH));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioCatcher>> BE_CATCHER = BLOCK_ENTITIES.register(
                "catcher",
                makeType(BioCatcher :: new,
                        BioCatcherRenderer :: new,
                        BlockReg.CATCHER));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioForge>> BE_FORGE = BLOCK_ENTITIES.register(
                "forge",
                makeType(BioForge :: new,
                        BioForgeRenderer :: new,
                        BlockReg.FORGE));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiblockFluidStorage>> BE_MULTIBLOCK_FLUID_STORAGE = BLOCK_ENTITIES.register(
                "multiblock_fluid_storage",
                makeType(MultiblockFluidStorage :: new,
                        MultiblockFluidStorageRenderer :: new,
                        BlockReg.MULTIBLOCK_FLUID_STORAGE));

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiblockChamber>> BE_MULTIBLOCK_CHAMBER = BLOCK_ENTITIES.register(
                "multiblock_chamber",
                makeType(MultiblockChamber :: new,
                        MultiblockChamberRenderer :: new,
                        MenuTypeReg.CHAMBER,
                        ChamberScreen :: new,
                        BlockReg.MULTIBLOCK_CHAMBER));

        public static <T extends BlockEntity,  C extends BioContainerMenu, S extends BioContainerScreen<C>> @NotNull Supplier<BlockEntityType<T>> makeType(BlockEntityType.BlockEntitySupplier<T> create,
                                                                                             BlockEntityRendererProvider<T> provider,
                                                                                             MenuTypeReg.ArgContainer<T, C> menuProvider,
                                                                                             MenuScreens.ScreenConstructor<C, S> screenConstructor,
                                                                                             Supplier<? extends Block> valid)
        {
            return makeTypeMultipleBlocks(create, provider, menuProvider, screenConstructor, ImmutableSet.of(valid));
        }
        public static <T extends BlockEntity> @NotNull Supplier<BlockEntityType<T>> makeType(BlockEntityType.BlockEntitySupplier<T> create,
                                                                                             BlockEntityRendererProvider<T> provider,
                                                                                             Supplier<? extends Block> valid)
        {
            return makeTypeMultipleBlocks(create, provider, null, null, ImmutableSet.of(valid));
        }

        public static <T extends BlockEntity> @NotNull Supplier<BlockEntityType<T>> makeType(BlockEntityType.BlockEntitySupplier<T> create, Supplier<? extends Block> valid)
        {
            return makeTypeMultipleBlocks(create, null, null, null, ImmutableSet.of(valid));
        }

        public static <T extends BlockEntity, C extends BioContainerMenu, S extends BioContainerScreen<C>> @NotNull Supplier<BlockEntityType<T>> makeTypeMultipleBlocks(
                BlockEntityType.BlockEntitySupplier<T> create,
                BlockEntityRendererProvider<T> rendererProvider,
                MenuTypeReg.ArgContainer<T, C> menuProvider,
                MenuScreens.ScreenConstructor<C, S> screenConstructor,
                Collection<? extends Supplier<? extends Block>> valid
        )
        {
            return () -> new BioBlockEntityType<>(
                    create, rendererProvider, menuProvider, screenConstructor, valid.stream().map(Supplier :: get).collect(Collectors.toUnmodifiableSet()));
        }

        public static class BioBlockEntityType<T extends BlockEntity, C extends BioContainerMenu, S extends BioContainerScreen<C>> extends BlockEntityType<T>
        {

            private final BlockEntityRendererProvider<T> renderer;
            private final MenuTypeReg.ArgContainer<T, C> menuProvider;
            private final MenuScreens.ScreenConstructor<C, S> screenConstructor;

            public BioBlockEntityType(BlockEntitySupplier<? extends T> factory, BlockEntityRendererProvider<T> provider, MenuTypeReg.ArgContainer<T, C> menuProvider, MenuScreens.ScreenConstructor<C, S> screenConstructor, Set<Block> validBlocks)
            {
                this(factory, provider, menuProvider, screenConstructor, validBlocks, false);
            }

            public BioBlockEntityType(BlockEntitySupplier<? extends T> factory, BlockEntityRendererProvider<T> provider, MenuTypeReg.ArgContainer<T, C> menuProvider, MenuScreens.ScreenConstructor<C, S> screenConstructor, Set<Block> validBlocks, boolean onlyOpsNbtAccess)
            {
                super(factory, validBlocks, onlyOpsNbtAccess);
                this.renderer = provider;
                this.menuProvider = menuProvider;
                this.screenConstructor = screenConstructor;
            }

            public BlockEntityRendererProvider<T> getRenderer()
            {
                return renderer;
            }

            public MenuTypeReg.ArgContainer<T, C> getMenuProvider()
            {
                return menuProvider;
            }

            public MenuScreens.ScreenConstructor<C, S> getScreenConstructor()
            {
                return screenConstructor;
            }
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
        public static final DeferredItem<Item> FORGE_UPGRADE = register("forge_upgrade", BioBaseItem :: new, properties -> properties.stacksTo(1).rarity(RarityExtension.BIO_RARE.getValue()));

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
                new BioFluidType.ColorParams(new Vector4f(112, 15, 37, 255), new Vector4f(97, 21, 10, 255), 80, (minColor, maxColor, maxTime) ->
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
                props -> props.slopeFindDistance(2).
                        levelDecreasePerBlock(2).
                        explosionResistance(100),
                typeProps -> typeProps.
                        lightLevel(0).
                        density(3000).
                        viscosity(6000).
                        rarity(RarityExtension.BIO_COMMON.getValue()).
                        canSwim(true).
                        canExtinguish(true).
                        canHydrate(true).
                        fallDistanceModifier(0f));

        public static final FluidEntry LYMPH = FluidEntry.make("lymph",
                BioFluidType.ColorParams.constantColor(new Vector4f(230, 255, 200, 255)),
                (camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor, colorParams) ->
                        MathHelper.ColorHelper.vector4fFromARGB(colorParams.getColor()),
                (camera, mode, renderDistance, partialTick, fogParameters, colorParams) ->
                {
                    Vector4f color = MathHelper.ColorHelper.vector4fFromARGB(colorParams.getColor());
                    return new FogParameters(0.00f, 0.5f, FogShape.CYLINDER, color.x(), color.y(), color.z(), color.w());
                },
                props -> props.slopeFindDistance(3).
                        levelDecreasePerBlock(1).
                        explosionResistance(100),
                typeProps -> typeProps.
                        lightLevel(0).
                        density(1500).
                        viscosity(1500).
                        rarity(RarityExtension.BIO_COMMON.getValue()).
                        canHydrate(true).
                        canExtinguish(true).
                        canHydrate(false).
                        fallDistanceModifier(0f));

        public static final FluidEntry ADRENALINE = FluidEntry.make("adrenaline",
                BioFluidType.ColorParams.constantColor(new Vector4f(173, 216, 230, 255)),
                (camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor, colorParams) ->
                        MathHelper.ColorHelper.vector4fFromARGB(colorParams.getColor()),
                (camera, mode, renderDistance, partialTick, fogParameters, colorParams) ->
                {
                    Vector4f color = MathHelper.ColorHelper.vector4fFromARGB(colorParams.getColor());
                    return new FogParameters(0.00f, 0.5f, FogShape.CYLINDER, color.x(), color.y(), color.z(), color.w());
                },
                props -> props.slopeFindDistance(4).
                        levelDecreasePerBlock(1).
                        explosionResistance(100),
                typeProps -> typeProps.
                        lightLevel(0).
                        density(1000).
                        viscosity(1000).
                        rarity(RarityExtension.BIO_RARE.getValue()).
                        canHydrate(true).
                        canExtinguish(true).
                        canHydrate(false).
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
                if(buildAttributes != null)
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

        public static final ArgContainer<MultiblockChamber, ChamberMenu> CHAMBER = registerArg(
                "chamber",
                ChamberMenu :: makeServer,
                ChamberMenu :: makeClient);

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

    public static class RecipeReg
    {
        public static final DeferredRegister<RecipeBookCategory> CATEGORIES = DeferredRegister.create(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Database.MOD_ID);
        public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAY_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_DISPLAY, Database.MOD_ID);
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Database.MOD_ID);
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Database.MOD_ID);

        public static final RecipeEntry<CrusherRecipe, CrusherRecipeInput, CrusherRecipeDisplay> CRUSHER_RECIPE = new RecipeEntry<>("crusher", RecipeBookCategory :: new, CrusherRecipeDisplay.CODEC, CrusherRecipeDisplay.STREAM_CODEC, CrusherRecipe.CrusherRecipeSerializer :: new);
        public static final RecipeEntry<StomachRecipe, StomachRecipeInput, StomachRecipeDisplay> STOMACH_RECIPE = new RecipeEntry<>("stomach", RecipeBookCategory :: new, StomachRecipeDisplay.CODEC, StomachRecipeDisplay.STREAM_CODEC, StomachRecipe.StomachRecipeSerializer :: new);
        public static final RecipeEntry<ForgeRecipe, ForgeRecipeInput, ForgeRecipeDisplay> FORGE_RECIPE = new RecipeEntry<>("forge", RecipeBookCategory :: new, ForgeRecipeDisplay.CODEC, ForgeRecipeDisplay.STREAM_CODEC, ForgeRecipe.ForgeRecipeSerializer :: new);

        public static class RecipeEntry<R extends Recipe<I>, I extends RecipeInput, D extends RecipeDisplay>
        {
            private final DeferredHolder<RecipeBookCategory, RecipeBookCategory> category;
            private final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<D>> display;
            private final DeferredHolder<RecipeType<?>, RecipeType<R>> recipeType;
            private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> serializer;

            public RecipeEntry(String name, Supplier<RecipeBookCategory> categorySupplier, MapCodec<D> displayCodec, StreamCodec<RegistryFriendlyByteBuf, D> displayStreamCodec, Supplier<RecipeSerializer<R>> serializer)
            {
                this.category = CATEGORIES.register(name, categorySupplier);
                this.display = RECIPE_DISPLAY_TYPES.register(name, () -> new RecipeDisplay.Type<>(displayCodec, displayStreamCodec));
                this.recipeType = RECIPE_TYPES.register(name, () -> new RecipeType<>()
                {
                    @Override
                    public String toString()
                    {
                        return Database.rl(name).toString();
                    }
                });
                this.serializer = RECIPE_SERIALIZERS.register(name, serializer);
            }

            public DeferredHolder<RecipeBookCategory, RecipeBookCategory> getCategory()
            {
                return category;
            }

            public DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<D>> getDisplay()
            {
                return display;
            }

            public DeferredHolder<RecipeType<?>, RecipeType<R>> getRecipeType()
            {
                return recipeType;
            }

            public DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> getSerializer()
            {
                return serializer;
            }
        }

        private static void init(IEventBus bus)
        {
            CATEGORIES.register(bus);
            RECIPE_DISPLAY_TYPES.register(bus);
            RECIPE_TYPES.register(bus);
            RECIPE_SERIALIZERS.register(bus);
        }
    }

    public static class IngredientReg
    {
        public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, Database.MOD_ID);

        public static final DeferredHolder<IngredientType<?>, IngredientType<IngredientWithSize>> SIZED_INGREDIENT = INGREDIENT_TYPES.register(
                "sized_ingredient",
                () -> new IngredientType<>(IngredientWithSize.CODEC, IngredientWithSize.STREAM_CODEC));

        private static void init (IEventBus bus)
        {
            INGREDIENT_TYPES.register(bus);
        }
    }

    public static class SlotDisplayReg
    {
        public static final DeferredRegister<SlotDisplay.Type<?>> SLOT_DISPLAY_TYPES = DeferredRegister.create(Registries.SLOT_DISPLAY, Database.MOD_ID);

        public static final DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<ItemStackWithChanceDisplay>> ITEM_STACK_WITH_CHANCE = SLOT_DISPLAY_TYPES.register(
                "stack_with_chance",
                () -> new SlotDisplay.Type<>(ItemStackWithChanceDisplay.CODEC, ItemStackWithChanceDisplay.STREAM_CODEC));

        public static final DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<ResourcesDisplay>> RESOURCES = SLOT_DISPLAY_TYPES.register(
                "resources",
                () -> new SlotDisplay.Type<>(ResourcesDisplay.CODEC, ResourcesDisplay.STREAM_CODEC));

        private static void init(IEventBus bus)
        {
            SLOT_DISPLAY_TYPES.register(bus);
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

    public static class MultiblockReg
    {
        public static final ResourceKey<Registry<IMultiblockDefinition>> DEFINITION_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("multiblock"));

        private static void registerDataPackRegister(final DataPackRegistryEvent.@NotNull NewRegistry event)
        {
            event.dataPackRegistry(DEFINITION_KEY, IMultiblockDefinition.CODEC, IMultiblockDefinition.CODEC, regBuilder -> makeRegistry(regBuilder, DEFINITION_KEY));
        }

        public static void init(@NotNull final IEventBus modEventBus)
        {
            modEventBus.addListener(MultiblockReg :: registerDataPackRegister);
        }
    }

    public static void init(@NotNull final IEventBus bus)
    {
        DataComponentsReg.init(bus);
        SlotDisplayReg.init(bus);
        IngredientReg.init(bus);
        MultiblockReg.init(bus);
        BookDataReg.init(bus);
        RecipeReg.init(bus);
        BlockReg.init(bus);
        ItemReg.init(bus);
        FluidReg.init(bus);
        BETypeReg.init(bus);
        EntityReg.init(bus);
        MenuTypeReg.init(bus);
        CreativeTabReg.init(bus);
    }

    private static <T> void makeRegistry(@NotNull RegistryBuilder<T> registryBuilder, @NotNull ResourceKey<? extends Registry<T>> key)
    {
        registryBuilder.defaultKey(key.location()).maxId(Integer.MAX_VALUE - 1).sync(true);
    }
}
