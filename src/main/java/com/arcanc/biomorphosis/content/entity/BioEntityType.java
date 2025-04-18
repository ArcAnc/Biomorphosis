/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class BioEntityType<T extends Entity> extends EntityType<T>
{
    private final EntityAttributeProvider entityAttributeProvider;
    private final EntityRendererProvider<T> rendererProvider;

    public BioEntityType(EntityFactory<T> factory,
                         MobCategory category,
                         boolean serialize,
                         boolean summon,
                         boolean fireImmune,
                         boolean canSpawnFarFromPlayer,
                         ImmutableSet<Block> immuneTo,
                         EntityDimensions dimensions,
                         float spawnDimensionsScale,
                         int clientTrackingRange,
                         int updateInterval,
                         String descriptionId,
                         Optional<ResourceKey<LootTable>> lootTable,
                         FeatureFlagSet requiredFeatures,
                         Predicate<EntityType<?>> trackDeltasSupplier,
                         ToIntFunction<EntityType<?>> trackingRangeSupplier,
                         ToIntFunction<EntityType<?>> updateIntervalSupplier,
                         boolean onlyOpCanSetNbt,
                         EntityAttributeProvider attributeProvider,
                         EntityRendererProvider<T> rendererProvider)
    {
        super(factory, category, serialize, summon, fireImmune, canSpawnFarFromPlayer, immuneTo, dimensions, spawnDimensionsScale, clientTrackingRange, updateInterval, descriptionId, lootTable, requiredFeatures, trackDeltasSupplier, trackingRangeSupplier, updateIntervalSupplier, onlyOpCanSetNbt);
        this.entityAttributeProvider = attributeProvider;
        this.rendererProvider = rendererProvider;
    }

    public EntityAttributeProvider getEntityAttributeProvider()
    {
        return entityAttributeProvider;
    }

    public EntityRendererProvider<T> getRendererProvider()
    {
        return rendererProvider;
    }

    public static class BioTypeBuilder<T extends Entity> extends Builder<T>
    {

        private EntityAttributeProvider attributeProvider;
        private EntityRendererProvider<T> rendererProvider;

        protected BioTypeBuilder(EntityFactory<T> factory, MobCategory category)
        {
            super(factory, category);
        }

        public static <T extends Entity> BioEntityType.@NotNull BioTypeBuilder<T> of(EntityType.@NotNull EntityFactory<T> factory, @NotNull MobCategory category)
        {
            return new BioEntityType.BioTypeBuilder<>(factory, category);
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> sized(float width, float height)
        {
            super.sized(width, height);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> spawnDimensionsScale(float spawnDimensionsScale)
        {
            super.spawnDimensionsScale(spawnDimensionsScale);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> eyeHeight(float eyeHeight)
        {
            super.eyeHeight(eyeHeight);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> passengerAttachments(float @NotNull ... attachPoints)
        {
            super.passengerAttachments(attachPoints);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> passengerAttachments(Vec3 @NotNull ... attachPoints)
        {
            super.passengerAttachments(attachPoints);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> vehicleAttachment(@NotNull Vec3 attachPoint)
        {
            super.attach(EntityAttachment.VEHICLE, attachPoint);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> ridingOffset(float ridingOffset)
        {
            super.attach(EntityAttachment.VEHICLE, 0.0F, -ridingOffset, 0.0F);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> nameTagOffset(float nameTagOffset)
        {
            super.attach(EntityAttachment.NAME_TAG, 0.0F, nameTagOffset, 0.0F);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> attach(@NotNull EntityAttachment attachment, float x, float y, float z)
        {
            super.attach(attachment, x, y, z);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> attach(@NotNull EntityAttachment attachment, @NotNull Vec3 pos)
        {
            super.attach(attachment, pos);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> noSummon()
        {
            super.noSummon();
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> noSave()
        {
            super.noSave();
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> fireImmune()
        {
            super.fireImmune();
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> immuneTo(Block @NotNull ... blocks)
        {
            super.immuneTo(blocks);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> canSpawnFarFromPlayer()
        {
            super.canSpawnFarFromPlayer();
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> clientTrackingRange(int clientTrackingRange)
        {
            super.clientTrackingRange(clientTrackingRange);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> updateInterval(int updateInterval)
        {
            super.updateInterval(updateInterval);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> requiredFeatures(FeatureFlag @NotNull ... requiredFeatures)
        {
            super.requiredFeatures(requiredFeatures);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> noLootTable()
        {
            super.noLootTable();
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> setUpdateInterval(int interval)
        {
            super.setUpdateInterval(interval);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> setTrackingRange(int range)
        {
            super.setTrackingRange(range);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> setShouldReceiveVelocityUpdates(boolean value)
        {
            super.setShouldReceiveVelocityUpdates(value);
            return this;
        }

        @Override
        public BioEntityType.@NotNull BioTypeBuilder<T> setOnlyOpCanSetNbt(boolean onlyOpCanSetNbt)
        {
            super.setOnlyOpCanSetNbt(onlyOpCanSetNbt);
            return this;
        }

        public @NotNull BioEntityType.BioTypeBuilder<T> attributeProvider(@NotNull EntityAttributeProvider attributeProvider)
        {
            this.attributeProvider = attributeProvider;
            return this;
        }

        public @NotNull BioEntityType.BioTypeBuilder<T> rendererProvider(@NotNull EntityRendererProvider<T> rendererProvider)
        {
            this.rendererProvider = rendererProvider;
            return this;
        }

        @Override
        public @NotNull BioEntityType<T> build(@NotNull ResourceKey<EntityType<?>> entityType)
        {
            if (this.serialize)
                Util.fetchChoiceType(References.ENTITY_TREE, entityType.location().toString());

            this.descriptionId = resourceKey -> resourceKey.location().getNamespace() + "." + "entity" + "." +  resourceKey.location().getPath().replace('/', '.');

            return new BioEntityType<>(
                    this.factory,
                    this.category,
                    this.serialize,
                    this.summon,
                    this.fireImmune,
                    this.canSpawnFarFromPlayer,
                    this.immuneTo,
                    this.dimensions.withAttachments(this.attachments),
                    this.spawnDimensionsScale,
                    this.clientTrackingRange,
                    this.updateInterval,
                    this.descriptionId.get(entityType),
                    this.lootTable.get(entityType),
                    this.requiredFeatures,
                    this.velocityUpdateSupplier,
                    this.trackingRangeSupplier,
                    this.updateIntervalSupplier,
                    this.onlyOpCanSetNbt,
                    this.attributeProvider,
                    this.rendererProvider
            );
        }
    }
}
