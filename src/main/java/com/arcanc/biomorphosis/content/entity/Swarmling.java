/**
 * @author ArcAnc
 * Created at: 16.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.arcanc.biomorphosis.data.tags.base.BioItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Swarmling extends Animal implements GeoEntity
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public Swarmling(EntityType<? extends Animal> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.1f, false));
		
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
		
		this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
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
	public boolean isFood(@NotNull ItemStack stack)
	{
		return stack.is(BioItemTags.SWARMLING_FOOD);
	}
	
	@Override
	public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent)
	{
		return Registration.EntityReg.MOB_SWARMLING.getEntityHolder().get().create(level);
	}
	
	@Override
	protected @Nullable SoundEvent getAmbientSound()
	{
		return Registration.EntityReg.MOB_SWARMLING.getSounds().getIdleSound().get();
	}

	@Override
	protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource)
	{
		return Registration.EntityReg.MOB_SWARMLING.getSounds().getHurtSound().get();
	}

	@Override
	protected @NotNull SoundEvent getDeathSound()
	{
		return Registration.EntityReg.MOB_SWARMLING.getSounds().getDeathSound().get();
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}
}
