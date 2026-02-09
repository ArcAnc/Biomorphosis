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
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class BioEntityType<T extends Entity> extends EntityType<T>
{
    private final EntityAttributeProvider entityAttributeProvider;
    private final EntityRendererProvider<T> rendererProvider;
	private final Class<T> clazz;
	
    public BioEntityType(Class<T> clazz,
						 EntityFactory<T> factory,
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
                         FeatureFlagSet requiredFeatures,
                         Predicate<EntityType<?>> trackDeltasSupplier,
                         ToIntFunction<EntityType<?>> trackingRangeSupplier,
                         ToIntFunction<EntityType<?>> updateIntervalSupplier,
                         EntityAttributeProvider attributeProvider,
                         EntityRendererProvider<T> rendererProvider)
    {
        super(factory, category, serialize, summon, fireImmune, canSpawnFarFromPlayer, immuneTo, dimensions, spawnDimensionsScale, clientTrackingRange, updateInterval, requiredFeatures, trackDeltasSupplier, trackingRangeSupplier, updateIntervalSupplier);
		this.clazz = clazz;
        this.entityAttributeProvider = attributeProvider;
        this.rendererProvider = rendererProvider;
    }
	
	@Override
	public @NotNull Class<T> getBaseClass()
	{
		return this.clazz;
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

		private final Class<T> clazz;
        private EntityAttributeProvider attributeProvider;
        private EntityRendererProvider<T> rendererProvider;

        protected BioTypeBuilder(Class<T> entityClass, EntityFactory<T> factory, MobCategory category)
        {
            super(factory, category);
			this.clazz = entityClass;
        }

        public static @NotNull <T extends Entity> BioEntityType.BioTypeBuilder<T> of(Class<T> entityClass, EntityType.@NotNull EntityFactory<T> factory, @NotNull MobCategory category)
        {
            return new BioTypeBuilder<>(entityClass, factory, category);
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> sized(float width, float height)
        {
            super.sized(width, height);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> spawnDimensionsScale(float spawnDimensionsScale)
        {
            super.spawnDimensionsScale(spawnDimensionsScale);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> eyeHeight(float eyeHeight)
        {
            super.eyeHeight(eyeHeight);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> passengerAttachments(float @NotNull ... attachPoints)
        {
            super.passengerAttachments(attachPoints);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> passengerAttachments(Vec3 @NotNull ... attachPoints)
        {
            super.passengerAttachments(attachPoints);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> vehicleAttachment(@NotNull Vec3 attachPoint)
        {
            super.attach(EntityAttachment.VEHICLE, attachPoint);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> ridingOffset(float ridingOffset)
        {
            super.attach(EntityAttachment.VEHICLE, 0.0F, -ridingOffset, 0.0F);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> nameTagOffset(float nameTagOffset)
        {
            super.attach(EntityAttachment.NAME_TAG, 0.0F, nameTagOffset, 0.0F);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> attach(@NotNull EntityAttachment attachment, float x, float y, float z)
        {
            super.attach(attachment, x, y, z);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> attach(@NotNull EntityAttachment attachment, @NotNull Vec3 pos)
        {
            super.attach(attachment, pos);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> noSummon()
        {
            super.noSummon();
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> noSave()
        {
            super.noSave();
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> fireImmune()
        {
            super.fireImmune();
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> immuneTo(Block @NotNull ... blocks)
        {
            super.immuneTo(blocks);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> canSpawnFarFromPlayer()
        {
            super.canSpawnFarFromPlayer();
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> clientTrackingRange(int clientTrackingRange)
        {
            super.clientTrackingRange(clientTrackingRange);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> updateInterval(int updateInterval)
        {
            super.updateInterval(updateInterval);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> requiredFeatures(FeatureFlag @NotNull ... requiredFeatures)
        {
            super.requiredFeatures(requiredFeatures);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> setUpdateInterval(int interval)
        {
            super.setUpdateInterval(interval);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> setTrackingRange(int range)
        {
            super.setTrackingRange(range);
            return this;
        }

        @Override
        public @NotNull BioEntityType.BioTypeBuilder<T> setShouldReceiveVelocityUpdates(boolean value)
        {
            super.setShouldReceiveVelocityUpdates(value);
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
        public @NotNull BioEntityType<T> build(@NotNull String key)
        {
            if (this.serialize)
                Util.fetchChoiceType(References.ENTITY_TREE, key);
            
            return new BioEntityType<>(
					this.clazz,
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
                    this.requiredFeatures,
                    this.velocityUpdateSupplier,
                    this.trackingRangeSupplier,
                    this.updateIntervalSupplier,
                    this.attributeProvider,
                    this.rendererProvider
            );
        }
    }
}
