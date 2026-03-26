/**
 * @author ArcAnc
 * Created at: 13.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;


import com.arcanc.biomorphosis.content.entity.ai.goals.WorkingRandomGoal;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Worker extends Monster implements PAnimatable<Worker>
{
	private final PAnimationManager<Worker> manager = PLibHelper.createManager(this);
	
	private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
	private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
	private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
	private final PRawAnimation WORK = PRawAnimation.begin().thenPlay("work").build();
	private static final PRawAnimation DEATH = PRawAnimation.begin().thenHold("death").build();

	private static final EntityDataAccessor<Boolean> WORKING = SynchedEntityData.defineId(Worker.class, EntityDataSerializers.BOOLEAN);


	public Worker(EntityType<? extends Monster> type, Level level)
	{
		super(type, level);
	}

	public boolean isWorking()
	{
		return this.entityData.get(WORKING);
	}

	public void setWorking(boolean value)
	{
		this.entityData.set(WORKING, value);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		super.defineSynchedData(builder);
		builder.define(WORKING, false);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.1f, false));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(8, new WorkingRandomGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
				this,
				Mob.class,
				5,
				true,
				true,
				entity ->
						!entity.getType().is(BioEntityTags.SWARM) &&
								!(entity instanceof Creeper)));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);

		//this.working = compound.getBoolean("working");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);

		//compound.putBoolean("working", this.working);
	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<Worker> registrar)
	{
		registrar.add(new PAnimationController<>("animController", state ->
				{
					Worker animatable = state.animatable();
					if (animatable.swinging)
					{
						state.controller().play(ATTACK);
						return state.controller().getState();
					}
					else if (animatable.isWorking())
					{
						state.controller().play(WORK);
						return state.controller().getState();
					}
					if (animatable.walkAnimation.isMoving())
						state.controller().play(WALK);
					else
						state.controller().play(IDLE);
					return state.controller().getState();
				})).
				add(new PAnimationController<>("death", state ->
				{
					if (!state.animatable().isDeadOrDying())
						state.controller().stop();
					else
						state.controller().play(DEATH);
					return state.controller().getState();
				}));
				
	}
	
	@Override
	public PAnimationManager<Worker> getAnimationManager()
	{
		return this.manager;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource)
	{
		return Registration.EntityReg.MOB_WORKER.getSounds().getHurtSound().get();
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return Registration.EntityReg.MOB_WORKER.getSounds().getDeathSound().get();
	}

	@Override
	protected @Nullable SoundEvent getAmbientSound()
	{
		return Registration.EntityReg.MOB_WORKER.getSounds().getIdleSound().get();
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
