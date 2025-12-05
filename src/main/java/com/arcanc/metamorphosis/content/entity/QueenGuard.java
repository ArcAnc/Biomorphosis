/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity;

import com.arcanc.metamorphosis.content.entity.ai.goals.FollowQueenGoal;
import com.arcanc.metamorphosis.content.entity.ai.goals.RandomPatrolGoal;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.tags.base.MetaEntityTags;
import com.arcanc.metamorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class QueenGuard extends Monster implements GeoEntity
{

    /*FIXME: переписать ИИ на брейн работу. Добавить оружие и щит в руки гвардосу. Дописать реакцию на атаку квины*/

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID queen;
    private BlockPos patrolPos;

    public QueenGuard(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.1f, false));
        this.goalSelector.addGoal(4, new FollowQueenGoal(this, 1.25f));
        this.goalSelector.addGoal(4, new RandomPatrolGoal(this, 0.6f));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this,
                Mob.class,
                5,
                false,
                true,
                (entity, level) ->
                !entity.getType().is(MetaEntityTags.SWARM) &&
                    !(entity instanceof Creeper)));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                                  DifficultyInstance difficulty,
                                                  EntitySpawnReason spawnReason,
                                                  @Nullable SpawnGroupData spawnGroupData)
    {

        this.patrolPos = this.blockPosition();
        //this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get(), this.blockPosition());
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Nullable
    public Queen getQueen()
    {
        if (this.queen == null)
            return null;
        if (this.level() instanceof ServerLevel serverLevel)
        {
            Entity ent = serverLevel.getEntity(this.queen);
            if (ent instanceof Queen mobQueen && mobQueen.isAlive())
                return mobQueen;
        }
		return null;
	}

    public void setQueen(@NotNull Queen queen)
    {
        this.queen = queen.getUUID();
        //this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), queen.getUUID());
    }

    /*@Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> dynamic)
    {
        return GuardBrain.makeBrain(dynamic);
    }

    @Override
    public @Nullable LivingEntity getTarget()
    {
        return this.getTargetFromBrain();
    }*/

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Brain<QueenGuard> getBrain()
    {
        return (Brain<QueenGuard>) super.getBrain();
    }

    /*@Override
    protected void customServerAiStep(ServerLevel level)
    {
        this.getBrain().tick(level, this);
    }*/

    @Override
    protected @NotNull SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getDeathSound().get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getHurtSound().get();
    }

    @Override
    protected @NotNull SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getIdleSound().get();
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.35f;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);

        if (compound.hasUUID("queen"))
            this.queen = compound.getUUID("queen");

        this.patrolPos = TagHelper.readBlockPos(compound, "patrol_pos");

        /*if (compound.hasUUID("queen"))
            this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), compound.getUUID("queen"));
        else
            this.getBrain().eraseMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get());

        this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get(), TagHelper.readBlockPos(compound, "patrol"));*/
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);

        if (this.queen != null)
            compound.putUUID("queen", this.queen);
        TagHelper.writeBlockPos(this.patrolPos, compound, "patrol_pos");

        /*this.getBrain().getMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get()).
            ifPresent(uuid -> compound.putUUID("queen", uuid));

        this.getBrain().getMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get()).
                ifPresent(pos -> TagHelper.writeBlockPos(pos, compound, "patrol"))*/;
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
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }
    
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return false;
    }
}
