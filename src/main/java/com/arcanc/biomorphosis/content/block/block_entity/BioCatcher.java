/**
 * @author ArcAnc
 * Created at: 22.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.block_entity.tick.ClientTickableBE;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.fluid.FluidLevelAnimator;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BioCatcher extends BioSidedAccessBlockEntity implements GeoBlockEntity, ServerTickableBE, ClientTickableBE
{
    private static final RawAnimation LOCK_IN = RawAnimation.begin().thenPlayAndHold("lock-in");
    private static final RawAnimation LOCK_OUT = RawAnimation.begin().thenPlayAndHold("lock-out");

    private static final int TICK_PERIOD = 20;

    private static final AABB CATCH_ZONE = new AABB(8/16f, 1/16f, 8/16f, 9/16f, 6/16f, 9/16f);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final AABB entityCatchZone;
    private LivingEntity entity = null;
    private EntityInfo entityInfo = null;

    private final FluidSidedStorage fluidHandler;

    private LazyEntityLoader entityLoader;

    public BioCatcher(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_CATCHER.get(), pos, blockState);
        this.entityCatchZone = CATCH_ZONE.move(pos);

        this.fluidHandler = new FluidSidedStorage().
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.LYMPH.type().get())).
                                setCapacity(200).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT);
    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx)
    {
        return null;
    }

    @Override
    protected void firstTick()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.registerBlockEntity(this.level, getBlockPos());
    }

    @Override
    public void setRemoved()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.removeBlockEntity(this.level, getBlockPos());
        super.setRemoved();
    }

    @Override
    public void tickClient()
    {
        setEntity();
    }

    @Override
    public void tickServer()
    {
        setEntity();

        if (this.entity == null)
            findAndCaptureMob();
        else
        {
            if (this.entity.isDeadOrDying())
            {
                this.entity = null;
                this.entityInfo = null;
                markDirty();
                return;
            }
            lockEntity();
            suckEntityFluid();
        }
    }

    private void setEntity()
    {
        if (this.entityLoader != null)
        {
            this.entityLoader.loadCaptureEntity(this);
            this.entityLoader = null;
            if (!this.level.isClientSide())
                markDirty();
        }
    }

    private void suckEntityFluid()
    {
        if (this.level == null)
            return;
        if (this.level.getGameTime() % TICK_PERIOD != 0)
            return;
        this.entity.hurtServer((ServerLevel) this.level, this.level.damageSources().stalagmite(), 1.0f);
        this.fluidHandler.fill(new FluidStack(Registration.FluidReg.LYMPH.still(), 2), IFluidHandler.FluidAction.EXECUTE);
        markDirty();
    }

    private void lockEntity()
    {
        this.entity.setDeltaMovement(Vec3.ZERO);
        this.entity.moveTo(this.entityInfo.entityPos(), this.entityInfo.entityRotation().y, this.entityInfo.entityRotation().x);
    }

    private void findAndCaptureMob()
    {
        if (this.level == null)
            return;
        List<LivingEntity> mobs = this.level.getEntitiesOfClass(LivingEntity.class, this.entityCatchZone);
        if (mobs.isEmpty())
            return;
        for (LivingEntity ent : mobs)
        {
            if (ent.isDeadOrDying() ||
                    ent.getType().is(EntityTypeTags.UNDEAD) ||
                    ent.getType().equals(EntityType.ENDER_DRAGON) ||
                    ent.getType().equals(EntityType.IRON_GOLEM) ||
                    ent.getType().equals(EntityType.SNOW_GOLEM) ||
                    ent.getType().equals(EntityType.WARDEN) ||
                    ent instanceof Player)
                continue;
            this.entity = mobs.getFirst();
            this.entityInfo = new EntityInfo(this.entity.getPosition(1.0f), this.entity.getRotationVector());
            markDirty();
            return;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "catch_controller", 0, state ->
                state.setAndContinue(this.entity == null ? LOCK_OUT : LOCK_IN)));
    }

    public static @Nullable FluidSidedStorage getFluidHandler(@NotNull BioCatcher be, Direction ctx)
    {
        return ctx == null ? be.fluidHandler : be.isAccessible(ctx) ? be.fluidHandler : null;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        tag.put(Database.Capabilities.Fluids.HANDLER, this.fluidHandler.serializeNBT(registries));
        if (this.entity != null)
        {
            tag.putUUID("entity_id", this.entity.getUUID());
            this.entityInfo.writeToTag(tag);
        }
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        this.fluidHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
        if (tag.contains("entity_id"))
        {
            this.entityLoader = bioCatcher ->
            {
                Entity ent =  bioCatcher.level.getEntities().get(tag.getUUID("entity_id"));
                if (ent instanceof LivingEntity living)
                {
                    this.entity = living;
                    this.entityInfo = EntityInfo.readFromTag(tag);
                }
            };
        }
        else
        {
            this.entity = null;
            this.entityInfo = null;
        }
    }

    private record EntityInfo(Vec3 entityPos, Vec2 entityRotation)
    {
        private void writeToTag(@NotNull CompoundTag tag)
        {
            CompoundTag infoTag = new CompoundTag();
            TagHelper.writeVec3(entityPos(), infoTag, "entity_pos");
            TagHelper.writeVec2(entityRotation(), infoTag, "entity_rot");
            tag.put("entity_info", infoTag);
        }

        private static @NotNull EntityInfo readFromTag(@NotNull CompoundTag tag)
        {
            CompoundTag infoTag = tag.getCompound("entity_info");
            Vec3 pos = TagHelper.readVec3(infoTag, "entity_pos");
            Vec2 rot = TagHelper.readVec2(infoTag, "entity_rot");
            return new EntityInfo(pos, rot);
        }
    }

    @FunctionalInterface
    private interface LazyEntityLoader
    {
        void loadCaptureEntity(BioCatcher be);
    }
}
