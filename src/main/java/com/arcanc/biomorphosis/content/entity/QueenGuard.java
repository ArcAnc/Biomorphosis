/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.entity.ai.goals.FollowQueenGoal;
import com.arcanc.biomorphosis.content.entity.ai.goals.RandomPatrolGoal;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.instance.ControllerState;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class QueenGuard extends Monster implements PAnimatable<QueenGuard>
{

    /*FIXME: переписать ИИ на брейн работу. Добавить оружие и щит в руки гвардосу. Дописать реакцию на атаку квины*/

    private final PAnimationManager<QueenGuard> manager = PLibHelper.createManager(this);
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
    private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
    private static final PRawAnimation DEATH = PRawAnimation.begin().thenPlay("death").build();
    
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
                entity ->
                !entity.getType().is(BioEntityTags.SWARM) &&
                    !(entity instanceof Creeper)));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                                  DifficultyInstance difficulty,
                                                  MobSpawnType spawnType,
                                                  @Nullable SpawnGroupData spawnGroupData)
    {
        this.patrolPos = this.blockPosition();
        //this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get(), this.blockPosition());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
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

    public void setQueen(Queen queen)
    {
        this.queen = queen.getUUID();
        //this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), queen.getUUID());
    }

    /*@Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
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
    public Brain<QueenGuard> getBrain()
    {
        return (Brain<QueenGuard>) super.getBrain();
    }

    /*@Override
    protected void customServerAiStep(ServerLevel level)
    {
        this.getBrain().tick(level, this);
    }*/

    @Override
    protected SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getDeathSound().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getHurtSound().get();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getIdleSound().get();
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.35f;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
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
    public void addAdditionalSaveData(CompoundTag compound)
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
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<QueenGuard> registrar)
    {
        registrar.add(new PAnimationController<>("walk/idle/attack", state ->
                {
                    QueenGuard animatable = state.animatable();
                    if (animatable.swinging)
                    {
                        state.controller().play(ATTACK);
                        return ControllerState.PLAY;
                    }
                    if (animatable.walkAnimation.isMoving())
                        state.controller().play(WALK);
                    else
                        state.controller().play(IDLE);
                    return ControllerState.PLAY;
                })).
        add(new PAnimationController<>("death", state ->
        {
            if (!state.animatable().isDeadOrDying())
                return ControllerState.STOP;
            state.controller().play(DEATH);
            return ControllerState.PLAY;
        }));
    }
    
    @Override
    public PAnimationManager<QueenGuard> getAnimationManager()
    {
        return this.manager;
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
