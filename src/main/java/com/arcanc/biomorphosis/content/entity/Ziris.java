/**
 * @author ArcAnc
 * Created at: 12.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class Ziris extends FlyingMob implements GeoEntity, Enemy
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private Ziris.AttackPhase attackPhase = Ziris.AttackPhase.CIRCLE;
    private BlockPos anchorPoint = BlockPos.ZERO;
    private Vec3 moveTargetPoint = Vec3.ZERO;

    public Ziris(EntityType<? extends FlyingMob> type, Level level)
    {
        super(type, level);
        this.moveControl = new Ziris.ZirisMoveControl(this);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new Ziris.ZirisAttackStrategyGoal());
        this.goalSelector.addGoal(2, new Ziris.ZirisSweepAttackGoal());
        this.goalSelector.addGoal(3, new Ziris.ZirisCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new Ziris.ZirisAttackPlayerTargetGoal());
    }

    //Копипаста из ванилы
    private enum AttackPhase
    {
        CIRCLE,
        SWOOP;
    }

    private class ZirisAttackStrategyGoal extends Goal
    {
        private int nextSweepTick;

        @Override
        public boolean canUse()
        {
            LivingEntity livingentity = Ziris.this.getTarget();
            return livingentity != null && Ziris.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }

        @Override
        public void start()
        {
            this.nextSweepTick = this.adjustedTickDelay(10);
            Ziris.this.attackPhase = Ziris.AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        @Override
        public void stop()
        {
            Ziris.this.anchorPoint = Ziris.this.level()
                    .getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, Ziris.this.anchorPoint)
                    .above(10 + Ziris.this.random.nextInt(20));
        }

        @Override
        public void tick()
        {
            if (Ziris.this.attackPhase == Ziris.AttackPhase.CIRCLE)
            {
                this.nextSweepTick--;
                if (this.nextSweepTick <= 0)
                {
                    Ziris.this.attackPhase = Ziris.AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + Ziris.this.random.nextInt(4)) * 20);
                    Ziris.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + Ziris.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget()
        {
            Ziris.this.anchorPoint = Ziris.this.getTarget().blockPosition().above(20 + Ziris.this.random.nextInt(20));
            if (Ziris.this.anchorPoint.getY() < Ziris.this.level().getSeaLevel())
            {
                Ziris.this.anchorPoint = new BlockPos(
                        Ziris.this.anchorPoint.getX(), Ziris.this.level().getSeaLevel() + 1, Ziris.this.anchorPoint.getZ()
                );
            }
        }
    }

    private abstract class ZirisMoveTargetGoal extends Goal
    {
        public ZirisMoveTargetGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget()
        {
            return Ziris.this.moveTargetPoint.distanceToSqr(Ziris.this.getX(), Ziris.this.getY(), Ziris.this.getZ()) < 4.0;
        }
    }

    private class ZirisSweepAttackGoal extends Ziris.ZirisMoveTargetGoal
    {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        @Override
        public boolean canUse()
        {
            return Ziris.this.getTarget() != null && Ziris.this.attackPhase == Ziris.AttackPhase.SWOOP;
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity livingentity = Ziris.this.getTarget();
            if (livingentity == null)
                return false;
            else if (!livingentity.isAlive())
                return false;
            else
            {
                if (livingentity instanceof Player player && (livingentity.isSpectator() || player.isCreative()))
                    return false;

                if (!this.canUse())
                    return false;
                else
                {
                    if (Ziris.this.tickCount > this.catSearchTick)
                    {
                        this.catSearchTick = Ziris.this.tickCount + 20;
                        List<Cat> list = Ziris.this.level()
                                .getEntitiesOfClass(Cat.class, Ziris.this.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);

                        for (Cat cat : list)
                        {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }
	    
	    @Override
        public void stop()
        {
            Ziris.this.setTarget(null);
            Ziris.this.attackPhase = Ziris.AttackPhase.CIRCLE;
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = Ziris.this.getTarget();
            if (livingentity != null)
            {
                Ziris.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5), livingentity.getZ());
                if (Ziris.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox()))
                {
                    Ziris.this.doHurtTarget(livingentity);
                    Ziris.this.attackPhase = Ziris.AttackPhase.CIRCLE;
                    if (!Ziris.this.isSilent())
                        Ziris.this.level().levelEvent(1039, Ziris.this.blockPosition(), 0);
                }
                else if (Ziris.this.horizontalCollision || Ziris.this.hurtTime > 0)
                {
                    Ziris.this.attackPhase = Ziris.AttackPhase.CIRCLE;
                }
            }
        }
    }

    class ZirisCircleAroundAnchorGoal extends Ziris.ZirisMoveTargetGoal
    {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        @Override
        public boolean canUse()
        {
            return Ziris.this.getTarget() == null || Ziris.this.attackPhase == Ziris.AttackPhase.CIRCLE;
        }

        @Override
        public void start()
        {
            this.distance = 5.0F + Ziris.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + Ziris.this.random.nextFloat() * 9.0F;
            this.clockwise = Ziris.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        @Override
        public void tick()
        {
            if (Ziris.this.random.nextInt(this.adjustedTickDelay(350)) == 0)
                this.height = -4.0F + Ziris.this.random.nextFloat() * 9.0F;

            if (Ziris.this.random.nextInt(this.adjustedTickDelay(250)) == 0)
            {
                this.distance++;
                if (this.distance > 15.0F)
                {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (Ziris.this.random.nextInt(this.adjustedTickDelay(450)) == 0)
            {
                this.angle = Ziris.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget())
                this.selectNext();

            if (Ziris.this.moveTargetPoint.y < Ziris.this.getY() && !Ziris.this.level().isEmptyBlock(Ziris.this.blockPosition().below(1)))
            {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (Ziris.this.moveTargetPoint.y > Ziris.this.getY() && !Ziris.this.level().isEmptyBlock(Ziris.this.blockPosition().above(1)))
            {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }
        }

        private void selectNext()
        {
            if (BlockPos.ZERO.equals(Ziris.this.anchorPoint))
                Ziris.this.anchorPoint = Ziris.this.blockPosition();

            this.angle = this.angle + this.clockwise * 15.0F * (float) (Math.PI / 180.0);
            Ziris.this.moveTargetPoint = Vec3.atLowerCornerOf(Ziris.this.anchorPoint)
                    .add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    private class ZirisAttackPlayerTargetGoal extends Goal
    {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);
        private int nextScanTick = reducedTickDelay(20);

        @Override
        public boolean canUse()
        {
            if (this.nextScanTick > 0)
            {
                this.nextScanTick--;
                return false;
            }
            else
            {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = level().getNearbyPlayers(this.attackTargeting, Ziris.this, Ziris.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
                if (!list.isEmpty())
                {
                    list.sort(Comparator.<Player, Double>comparing(Entity::getY).reversed());

                    for (Player player : list) {
                        if (Ziris.this.canAttack(player, TargetingConditions.DEFAULT))
                        {
                            Ziris.this.setTarget(player);
                            return true;
                        }
                    }
                }
                return false;
            }
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity livingentity = Ziris.this.getTarget();
            return livingentity != null && Ziris.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }
    }

    private class ZirisMoveControl extends MoveControl
    {
        private float speed = 0.4F;

        public ZirisMoveControl(Mob mob)
        {
            super(mob);
        }

        @Override
        public void tick()
        {
            if (Ziris.this.horizontalCollision)
            {
                Ziris.this.setYRot(Ziris.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = Ziris.this.moveTargetPoint.x - Ziris.this.getX();
            double d1 = Ziris.this.moveTargetPoint.y - Ziris.this.getY();
            double d2 = Ziris.this.moveTargetPoint.z - Ziris.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > 1.0E-5F) {
                double d4 = 1.0 - Math.abs(d1 * 0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = Ziris.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(Ziris.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180.0F / (float)Math.PI));
                Ziris.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                Ziris.this.yBodyRot = Ziris.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, Ziris.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * 180.0F / (float)Math.PI));
                Ziris.this.setXRot(f4);
                float f5 = Ziris.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * (float) (Math.PI / 180.0))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * (float) (Math.PI / 180.0))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * (float) (Math.PI / 180.0))) * Math.abs(d1 / d5);
                Vec3 vec3 = Ziris.this.getDeltaMovement();
                Ziris.this.setDeltaMovement(vec3.add(new Vec3(d6, d8, d7).subtract(vec3).scale(0.2)));
            }
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AX"))
            this.anchorPoint = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("AX", this.anchorPoint.getX());
        compound.putInt("AY", this.anchorPoint.getY());
        compound.putInt("AZ", this.anchorPoint.getZ());
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level,
                                                  @NotNull DifficultyInstance difficulty,
                                                  @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData)
    {
        this.anchorPoint = this.blockPosition().above(5);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return true;
    }

    @Override
    public boolean canAttackType(@NotNull EntityType<?> type)
    {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "animController", 0, state ->
                {
                    if (this.swinging)
                        return state.setAndContinue(DefaultAnimations.ATTACK_SWING);
                    return state.setAndContinue(this.walkAnimation.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
                }),
                DefaultAnimations.genericDeathController(this));
    }

    @Override
    protected @Nullable SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_ZIRIS.getSounds().getDeathSound().get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_ZIRIS.getSounds().getHurtSound().get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_ZIRIS.getSounds().getIdleSound().get();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }
}
