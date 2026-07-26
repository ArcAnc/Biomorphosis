/**
 * @author ArcAnc
 * Created at: 12.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.entity.ai.goals.SwarmHurtByTargetGoal;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class Ziris extends FlyingMob implements PAnimatable<Ziris>, Enemy
{
    private final PAnimationManager<Ziris> manager = PLibHelper.createManager(this);
    private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
    private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    private static final PRawAnimation DEATH = PRawAnimation.begin().thenHold("death").build();
    
    private Ziris.AttackPhase attackPhase = Ziris.AttackPhase.CIRCLE;
    private BlockPos anchorPoint = BlockPos.ZERO;
    private Vec3 flightTargetPoint = Vec3.ZERO;

    public Ziris(EntityType<? extends FlyingMob> type, Level level)
    {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.navigation = new FlyingPathNavigation(this, level);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new Ziris.ZirisAttackStrategyGoal());
        this.goalSelector.addGoal(2, new Ziris.ZirisSweepAttackGoal());
        this.goalSelector.addGoal(3, new Ziris.ZirisCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new SwarmHurtByTargetGoal(this, 32));
        this.targetSelector.addGoal(2, new Ziris.ZirisAttackPlayerTargetGoal());
    }

    //Копипаста из ванилы
    private enum AttackPhase
    {
        CIRCLE,
        SWOOP,
        RETREAT;
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
            return Ziris.this.flightTargetPoint.distanceToSqr(Ziris.this.getX(), Ziris.this.getY(), Ziris.this.getZ()) < 4.0;
        }
    }

    private class ZirisSweepAttackGoal extends Ziris.ZirisMoveTargetGoal
    {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private static final double ATTACK_DISTANCE = 4.5;
        private static final double RETREAT_DISTANCE = 10.0;
        private boolean isScaredOfCat;
        private int catSearchTick;
        private int retreatEndTick;

        @Override
        public boolean canUse()
        {
            return Ziris.this.getTarget() != null
                    && (Ziris.this.attackPhase == Ziris.AttackPhase.SWOOP || Ziris.this.attackPhase == Ziris.AttackPhase.RETREAT);
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
            Ziris.this.attackPhase = Ziris.AttackPhase.CIRCLE;
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = Ziris.this.getTarget();
            if (livingentity != null)
            {
                if (Ziris.this.attackPhase == Ziris.AttackPhase.RETREAT)
                {
                    if (Ziris.this.tickCount >= this.retreatEndTick)
                        Ziris.this.attackPhase = Ziris.AttackPhase.CIRCLE;
                    else
                        Ziris.this.setFlightTarget(this.getRetreatPoint(livingentity));
                    return;
                }

                Vec3 targetPosition = livingentity.getEyePosition();
                Vec3 approachDirection = Ziris.this.position().subtract(targetPosition);
                if (approachDirection.horizontalDistanceSqr() < 1.0E-4)
                    approachDirection = Ziris.this.getLookAngle().scale(-1.0);
                else
                    approachDirection = new Vec3(approachDirection.x, 0.0, approachDirection.z).normalize();

                Vec3 attackPoint = targetPosition.add(approachDirection.scale(ATTACK_DISTANCE));
                if (!Ziris.this.setFlightTarget(attackPoint))
                {
                    Ziris.this.attackPhase = Ziris.AttackPhase.CIRCLE;
                    return;
                }

                Ziris.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                double distanceToTarget = Ziris.this.distanceTo(livingentity);
                if (distanceToTarget >= 3.5 && distanceToTarget <= 5.5 && Ziris.this.hasLineOfSight(livingentity))
                    this.fireAndRetreat(livingentity);
            }
        }

        private void fireAndRetreat(LivingEntity target)
        {
            Vec3 shotDirection = target.getEyePosition().subtract(Ziris.this.getEyePosition());
            ZirisProjectile projectile = new ZirisProjectile(Registration.EntityReg.PROJECTILE_ZIRIS.getEntityHolder().get(), Ziris.this.level());
            projectile.setPos(Ziris.this.getX(), Ziris.this.getEyeY(), Ziris.this.getZ());
			projectile.setOwner(Ziris.this);
			projectile.shoot(shotDirection.x(), shotDirection.y(), shotDirection.z(), 1.5f, 0.0f);
            Ziris.this.level().addFreshEntity(projectile);
            Ziris.this.swing(InteractionHand.MAIN_HAND);
            Ziris.this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.9F + Ziris.this.random.nextFloat() * 0.2F);
            this.retreatEndTick = Ziris.this.tickCount + this.adjustedTickDelay(30);
            Ziris.this.attackPhase = Ziris.AttackPhase.RETREAT;
            Ziris.this.setFlightTarget(this.getRetreatPoint(target));
        }

        private Vec3 getRetreatPoint(LivingEntity target)
        {
            Vec3 retreatDirection = Ziris.this.position().subtract(target.position());
            if (retreatDirection.horizontalDistanceSqr() < 1.0E-4)
                retreatDirection = Ziris.this.getLookAngle().scale(-1.0);
            else
                retreatDirection = new Vec3(retreatDirection.x, 0.0, retreatDirection.z).normalize();
            return target.getEyePosition().add(retreatDirection.scale(RETREAT_DISTANCE)).add(0.0, 2.0, 0.0);
        }
    }

    class ZirisCircleAroundAnchorGoal extends Ziris.ZirisMoveTargetGoal
    {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;
        private int nextOrbitPointTick;

        @Override
        public boolean canUse()
        {
            return Ziris.this.getTarget() == null || Ziris.this.attackPhase == Ziris.AttackPhase.CIRCLE;
        }

        @Override
        public void start()
        {
            this.distance = 5.0F + Ziris.this.random.nextFloat() * 10.0F;
            this.height = -3.0F + Ziris.this.random.nextFloat() * 7.0F;
            this.clockwise = Ziris.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        @Override
        public void tick()
        {
            if (Ziris.this.random.nextInt(this.adjustedTickDelay(350)) == 0)
                this.height = -3.0F + Ziris.this.random.nextFloat() * 7.0F;

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

            if (this.touchingTarget() || Ziris.this.tickCount >= this.nextOrbitPointTick || Ziris.this.navigation.isDone())
                this.selectNext();

            if (Ziris.this.flightTargetPoint.y < Ziris.this.getY() && !Ziris.this.level().isEmptyBlock(Ziris.this.blockPosition().below(1)))
            {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (Ziris.this.flightTargetPoint.y > Ziris.this.getY() && !Ziris.this.level().isEmptyBlock(Ziris.this.blockPosition().above(1)))
            {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }
        }

        private void selectNext()
        {
            if (BlockPos.ZERO.equals(Ziris.this.anchorPoint))
                Ziris.this.anchorPoint = Ziris.this.blockPosition();

            Vec3 center = Ziris.this.getOrbitCenter();
            for (int attempt = 0; attempt < 8; attempt++)
            {
                this.angle += this.clockwise * 15.0F * (float) (Math.PI / 180.0);
                Vec3 orbitPoint = center.add(
                        this.distance * Mth.cos(this.angle), this.height, this.distance * Mth.sin(this.angle)
                );
                if (Ziris.this.setFlightTarget(orbitPoint))
                    break;
            }
            this.nextOrbitPointTick = Ziris.this.tickCount + this.adjustedTickDelay(20 + Ziris.this.random.nextInt(20));
        }
    }
    
    private Vec3 getOrbitCenter()
    {
        LivingEntity target = this.getTarget();
        return target != null
                ? new Vec3(target.getX(), target.getY(0.5), target.getZ())
                : Vec3.atCenterOf(this.anchorPoint);
    }

    private boolean setFlightTarget(Vec3 target)
    {
        boolean targetChanged = this.flightTargetPoint.distanceToSqr(target) > 1.0;
        this.flightTargetPoint = target;
        if (targetChanged || this.navigation.isDone() || this.tickCount % 10 == 0)
            return this.navigation.moveTo(target.x, target.y, target.z, 1.8);
        return !this.navigation.isDone();
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

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AX"))
            this.anchorPoint = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("AX", this.anchorPoint.getX());
        compound.putInt("AY", this.anchorPoint.getY());
        compound.putInt("AZ", this.anchorPoint.getZ());
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                                  DifficultyInstance difficulty,
                                                  MobSpawnType spawnType,
                                                  @Nullable SpawnGroupData spawnGroupData)
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
    public boolean canAttackType(EntityType<?> type)
    {
        return true;
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<Ziris> registrar)
    {
        registrar.add("animController", () -> state ->
        {
            Ziris animatable = state.animatable();
            if (animatable.swinging)
                state.controller().play(ATTACK);
            else
                state.controller().play(animatable.walkAnimation.isMoving() ? WALK : IDLE);
            return state.controller().getState();
        }).
        add("deathController", () -> state ->
        {
            if (!state.animatable().isDeadOrDying())
                return ControllerState.STOP;
            state.controller().play(DEATH);
            return ControllerState.PLAY;
        });
    }
    
    @Override
    protected @Nullable SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_ZIRIS.getSounds().getDeathSound().get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_ZIRIS.getSounds().getHurtSound().get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_ZIRIS.getSounds().getIdleSound().get();
    }
    
    @Override
    public PAnimationManager<Ziris> getAnimationManager(AnimManagerKey key)
    {
        return this.manager;
    }
}
