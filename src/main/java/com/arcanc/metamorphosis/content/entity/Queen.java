/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity;

import com.arcanc.metamorphosis.content.entity.ai.goals.MoveToLureGoal;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.tags.base.MetaEntityTags;
import com.arcanc.metamorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Queen extends Monster implements GeoEntity
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation UNBURROW = RawAnimation.begin().thenLoop("unburrow");
    public static final RawAnimation BURROW = RawAnimation.begin().thenLoop("burrow");

    private BlockPos lurePos;
    private BlockPos spawnPos;
    private boolean findLure = false;
    private BurrowState burrowState;
    private int unburrowTimer = 0;
    private int burrowTimer = 0;

    public Queen(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
        if (spawnPos == null)
            this.spawnPos = BlockPos.ZERO;
        if (this.lurePos == null)
            this.lurePos = BlockPos.ZERO;
        this.burrowState = BurrowState.UNBURROWING;
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
        return findLure;
    }

    public BlockPos getLurePos()
    {
        return lurePos;
    }

    public BlockPos getSpawnPos()
    {
        return spawnPos;
    }

    public void setLurePos(BlockPos pos)
    {
        this.lurePos = pos;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, (entity, level) -> !entity.getType().is(MetaEntityTags.SWARM)));

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(2, new MoveToLureGoal(this, 1.0));

        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean isUnderGround()
    {
        return this.burrowState == BurrowState.UNDER_GROUND;
    }

    public boolean isUnburrowing()
    {
        return this.burrowState == BurrowState.UNBURROWING;
    }

    public boolean isOnGround()
    {
        return this.burrowState == BurrowState.ON_GROUND;
    }

    public boolean isBurrowing()
    {
        return this.burrowState == BurrowState.BURROWING;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.isUnburrowing())
            this.unburrowTimer++;
        if (this.isBurrowing())
            this.burrowTimer++;

        if (this.unburrowTimer >= 1.5f * 20 && this.isUnburrowing())
        {
            this.unburrowTimer = 0;
            this.burrowState = BurrowState.ON_GROUND;
        }

        if (this.burrowTimer >= 5f * 20 && this.isBurrowing())
        {
            this.burrowTimer = 0;
            this.burrowState = BurrowState.UNDER_GROUND;
        }

        if (this.lurePos != null && !this.findLure && this.isOnGround())
        {
            if (this.blockPosition().closerToCenterThan(Vec3.atCenterOf(this.lurePos), 2.0))
                sniffBait();
        }

        if (this.findLure && this.lurePos != null)
        {
            double distance = this.blockPosition().distManhattan(this.lurePos);
            if (distance > 32)
            {
                if (this.isOnGround())
                    this.burrowState = BurrowState.BURROWING;
                if (this.isUnderGround())
                {
                    this.discard();
                    this.level().getEntitiesOfClass(QueenGuard.class, this.getBoundingBox().inflate(16))
                            .forEach(Entity :: discard);
				}
            }
        }
    }

    private void sniffBait()
    {
        this.findLure = true;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.lurePos = TagHelper.readBlockPos(compound, "lure_pos");
        this.spawnPos = TagHelper.readBlockPos(compound, "spawn_pos");
        this.findLure = compound.getBoolean("find_lure");
        this.burrowState = BurrowState.values()[compound.getInt("burrow_state")];
        this.burrowTimer = compound.getInt("burrow_timer");
        this.unburrowTimer = compound.getInt("unburrow_timer");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        TagHelper.writeBlockPos(this.lurePos, compound, "lure_pos");
        TagHelper.writeBlockPos(this.spawnPos, compound, "spawn_pos");
        compound.putBoolean("find_lure", this.findLure);
        compound.putInt("burrow_state", this.burrowState.ordinal());
        compound.putInt("burrow_timer", this.burrowTimer);
        compound.putInt("unburrow_timer", this.unburrowTimer);
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "animControl", 5, state ->
                switch (this.burrowState)
                {
                    case BURROWING -> state.setAndContinue(BURROW);
                    case ON_GROUND ->
                    {
                        if (this.swinging)
                            yield  state.setAndContinue(DefaultAnimations.ATTACK_STRIKE);
                        yield state.setAndContinue(this.walkAnimation.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
                    }
                    case UNBURROWING -> state.setAndContinue(UNBURROW);
                    case UNDER_GROUND ->
                    {
                        state.setAndContinue(UNBURROW);
                        yield PlayState.STOP;
                    }
                }));
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
    protected @NotNull SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getDeathSound().get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getHurtSound().get();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
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
}
