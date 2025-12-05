/**
 * @author ArcAnc
 * Created at: 13.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity;


import com.arcanc.metamorphosis.content.entity.ai.goals.WorkingRandomGoal;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.tags.base.MetaEntityTags;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Worker extends Monster implements GeoEntity
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final RawAnimation WORK = RawAnimation.begin().thenPlay("misc.work");

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
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
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
				(entity, level) ->
						!entity.getType().is(MetaEntityTags.SWARM) &&
								!(entity instanceof Creeper)));
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);

		//this.working = compound.getBoolean("working");
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);

		//compound.putBoolean("working", this.working);
	}

	@Override
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "animController", 0, state ->
				{
					if (this.swinging)
						return state.setAndContinue(DefaultAnimations.ATTACK_SWING);
					else if (this.isWorking())
						return state.setAndContinue(WORK);
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
	protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource)
	{
		return Registration.EntityReg.MOB_WORKER.getSounds().getHurtSound().get();
	}

	@Override
	protected @NotNull SoundEvent getDeathSound()
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
}
