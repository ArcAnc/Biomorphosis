/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.entity.ai.brain.QueenBrain;
import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Queen extends Monster implements PAnimatable<Queen>
{
    private static final double GUARD_SIGNAL_RADIUS = 128;
    private static final float MIN_BURROW_HEIGHT_SCALE = 0.0f;
    private static final EntityDataAccessor<Integer> BURROW_STATE = SynchedEntityData.defineId(Queen.class, EntityDataSerializers.INT);

    private final PAnimationManager<Queen> manager = PLibHelper.createManager(this);

    private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
    private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    private static final PRawAnimation UNBURROW = PRawAnimation.begin().thenHold("unburrow").build();
    private static final PRawAnimation BURROW = PRawAnimation.begin().thenHold("burrow").build();
    private static final PRawAnimation DEATH = PRawAnimation.begin().thenHold("death").build();

    private BlockPos lurePos;
    private BlockPos spawnPos;
    private boolean findLure = false;
    private int unburrowTimer = 0;
    private int burrowTimer = 0;

    public Queen(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
        if (this.spawnPos == null)
            this.spawnPos = BlockPos.ZERO;
        if (this.lurePos == null)
            this.lurePos = BlockPos.ZERO;
    }

    public Queen (Level level, Vec3 position)
    {
        this(Registration.EntityReg.MOB_QUEEN.getEntityHolder().get(), level);
        this.setPos(position);
        this.spawnPos = BlockPos.containing(position);
    }

    public Queen(Level level, Vec3 position, BlockPos lurePos)
    {
        this(level, position);
        this.lurePos = lurePos;
    }

    public boolean isFindLure()
    {
        return this.findLure;
    }

    public @Nullable BlockPos getLurePos()
    {
        return this.lurePos;
    }

    public @Nullable BlockPos getSpawnPos()
    {
        return this.spawnPos;
    }

    public void setLurePos(BlockPos pos)
    {
        this.lurePos = pos;
    }

    @Override
    protected void registerGoals()
    {
    }

    public boolean isUnderGround()
    {
        return this.getBurrowState() == BurrowState.UNDER_GROUND;
    }

    public boolean isUnburrowing()
    {
        return this.getBurrowState() == BurrowState.UNBURROWING;
    }

    public boolean isOnGround()
    {
        return this.getBurrowState() == BurrowState.ON_GROUND;
    }

    public boolean isBurrowing()
    {
        return this.getBurrowState() == BurrowState.BURROWING;
    }

    public void tickUnburrow()
    {
        if (!this.isUnburrowing())
            return;
        this.unburrowTimer++;
        this.refreshDimensions();
        if (this.unburrowTimer >= QueenBrain.UNBURROW_TICKS)
        {
            this.unburrowTimer = 0;
            this.setBurrowState(BurrowState.ON_GROUND);
        }
    }

    public void tickBurrow()
    {
        if (!this.isBurrowing())
            return;
        this.burrowTimer++;
        this.refreshDimensions();
        if (this.burrowTimer >= QueenBrain.BURROW_TICKS)
        {
            this.burrowTimer = 0;
            this.setBurrowState(BurrowState.UNDER_GROUND);
            this.discard();
        }
    }

    public void markLureFound()
    {
        this.findLure = true;
    }

    public void startBurrowing()
    {
        if (this.isOnGround())
        {
            this.burrowTimer = 0;
            this.setBurrowState(BurrowState.BURROWING);
        }
    }

    public boolean isValidCombatTarget(@Nullable LivingEntity target)
    {
        return this.isOnGround() && SwarmTargeting.isValidSwarmEnemy(this, target);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.level().isClientSide())
            tickClientBurrowDimensions();
    }

    private void tickClientBurrowDimensions()
    {
        if (this.isUnburrowing() && this.unburrowTimer < QueenBrain.UNBURROW_TICKS)
        {
            this.unburrowTimer++;
            this.refreshDimensions();
        }
        else if (this.isBurrowing() && this.burrowTimer < QueenBrain.BURROW_TICKS)
        {
            this.burrowTimer++;
            this.refreshDimensions();
        }
    }

    private BurrowState getBurrowState()
    {
        return BurrowState.values()[this.entityData.get(BURROW_STATE)];
    }

    private void setBurrowState(BurrowState state)
    {
        this.entityData.set(BURROW_STATE, state.ordinal());
        this.refreshDimensions();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return QueenBrain.makeBrain(dynamic);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Queen> getBrain()
    {
        return (Brain<Queen>) super.getBrain();
    }

    @Override
    public @Nullable LivingEntity getTarget()
    {
        return this.getTargetFromBrain();
    }

    @Override
    public boolean canAttack(LivingEntity target)
    {
        return super.canAttack(target) && isValidCombatTarget(target);
    }

    @Override
    protected void customServerAiStep()
    {
        if (this.level() instanceof ServerLevel serverLevel)
        {
            serverLevel.getProfiler().push("queenBrain");
            this.getBrain().tick(serverLevel, this);
            serverLevel.getProfiler().pop();
        }
        super.customServerAiStep();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(BURROW_STATE, BurrowState.UNBURROWING.ordinal());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        super.onSyncedDataUpdated(key);
        if (BURROW_STATE.equals(key))
        {
            if (this.isUnburrowing())
                this.unburrowTimer = 0;
            else if (this.isBurrowing())
                this.burrowTimer = 0;
            this.refreshDimensions();
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose)
    {
        EntityDimensions dimensions = super.getDefaultDimensions(pose);
        return dimensions.scale(1.0f, getBurrowHeightScale());
    }

    private float getBurrowHeightScale()
    {
        return switch (this.getBurrowState())
        {
            case UNDER_GROUND -> MIN_BURROW_HEIGHT_SCALE;
            case UNBURROWING -> Math.max(MIN_BURROW_HEIGHT_SCALE, Math.min(1.0f, this.unburrowTimer / (float) QueenBrain.UNBURROW_TICKS));
            case ON_GROUND -> 1.0f;
            case BURROWING -> Math.max(MIN_BURROW_HEIGHT_SCALE, 1.0f - Math.min(1.0f, this.burrowTimer / (float) QueenBrain.BURROW_TICKS));
        };
    }

    @Override
    public void die(DamageSource damageSource)
    {
        super.die(damageSource);
        if (!this.level().isClientSide())
            signalGuardQueenDeath();
    }

    @Override
    public void remove(RemovalReason reason)
    {
        if (!this.level().isClientSide() && reason == RemovalReason.DISCARDED)
            discardGuards();
        super.remove(reason);
    }

    private void signalGuardQueenDeath()
    {
        this.level().getEntitiesOfClass(
                QueenGuard.class,
                this.getBoundingBox().inflate(GUARD_SIGNAL_RADIUS),
                guard -> guard.isGuarding(this)).
                forEach(guard -> guard.queenDied(this));
    }

    private void discardGuards()
    {
        this.level().getEntitiesOfClass(
                QueenGuard.class,
                this.getBoundingBox().inflate(GUARD_SIGNAL_RADIUS),
                guard -> guard.isGuarding(this)).
                forEach(Entity :: discard);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.lurePos = TagHelper.readBlockPos(compound, "lure_pos");
        this.spawnPos = TagHelper.readBlockPos(compound, "spawn_pos");
        this.findLure = compound.getBoolean("find_lure");
        this.burrowTimer = compound.getInt("burrow_timer");
        this.unburrowTimer = compound.getInt("unburrow_timer");
        this.setBurrowState(BurrowState.values()[compound.getInt("burrow_state")]);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        TagHelper.writeBlockPos(this.lurePos, compound, "lure_pos");
        TagHelper.writeBlockPos(this.spawnPos, compound, "spawn_pos");
        compound.putBoolean("find_lure", this.findLure);
        compound.putInt("burrow_state", this.getBurrowState().ordinal());
        compound.putInt("burrow_timer", this.burrowTimer);
        compound.putInt("unburrow_timer", this.unburrowTimer);
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<Queen> registrar)
    {
        registrar.add("animControl", () ->state ->
        {
            Queen animatable = state.animatable();
            return switch (animatable.getBurrowState())
            {
                case BURROWING -> {
                    state.controller().play(BURROW);
                    yield ControllerState.PLAY;
                }
                case ON_GROUND ->
                {
                    if (animatable.swinging)
                    {
                        state.controller().play(ATTACK);
                    }
                    else
                    {
                        if (animatable.walkAnimation.isMoving())
                            state.controller().play(WALK);
                        else
                            state.controller().play(IDLE);
                    }
	                yield ControllerState.PLAY;
                }
                case UNBURROWING -> {
                    state.controller().play(UNBURROW);
                    yield ControllerState.PLAY;
                }
                case UNDER_GROUND ->
                {
                    state.controller().pause();
                    yield ControllerState.STOP;
                }
            };
        }).
        add("death", () -> state ->
        {
            if (!state.animatable().isDeadOrDying())
                return ControllerState.STOP;
            state.controller().play(DEATH);
            return ControllerState.PLAY;
        });
    }
    @Override
    public int getCurrentSwingDuration()
    {
        return 30;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getIdleSound().get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getDeathSound().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getHurtSound().get();
    }
    
    @Override
    public PAnimationManager<Queen> getAnimationManager(AnimManagerKey key)
    {
        return this.manager;
    }
    
    private enum BurrowState
    {
        UNDER_GROUND, UNBURROWING, ON_GROUND, BURROWING;
    }
    
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return false;
    }
	
	@Override
	public boolean isPersistenceRequired()
	{
		return true;
	}
}
