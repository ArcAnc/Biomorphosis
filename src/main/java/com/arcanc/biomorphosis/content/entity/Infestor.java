/**
 * @author ArcAnc
 * Created at: 15.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
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

public class Infestor extends Monster implements PAnimatable<Infestor>
{
	/*FIXME: 1 удар и потеря интереса + заражение и выпадение личинок. Приделать эту херь*/
	/*FIXME: что-то не так с атакой мобов. Проверить, почему она не работает*/
	private final PAnimationManager<Infestor> manager = PLibHelper.createManager(this);

	private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
	private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
	private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
	
	private static final PRawAnimation DEATH = PRawAnimation.begin().thenHold("death").build();

	public Infestor(EntityType<? extends Monster> type, Level level)
	{
		super(type, level);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.1f, false));
		this.goalSelector.addGoal(0, new FloatGoal(this));
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
		
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<Infestor> registrar)
	{
		registrar.add(new PAnimationController<>("animController",state ->
				{
					Infestor animatable = state.animatable();
					if (animatable.swinging)
						state.controller().play(ATTACK);
					else
					{
						if (animatable.walkAnimation.isMoving())
							state.controller().play(WALK);
						else
							state.controller().play(IDLE);
					}
					return state.controller().getState();
				})).
				add(new PAnimationController<>("death", state ->
				{
					if (state.animatable().isDeadOrDying())
						state.controller().play(DEATH);
					else
						state.controller().stop();
					return  state.controller().getState();
				}));
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return Registration.EntityReg.MOB_INFESTOR.getSounds().getDeathSound().get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource)
	{
		return Registration.EntityReg.MOB_INFESTOR.getSounds().getHurtSound().get();
	}

	@Override
	protected @Nullable SoundEvent getAmbientSound()
	{
		return Registration.EntityReg.MOB_INFESTOR.getSounds().getIdleSound().get();
	}
	
	@Override
	public PAnimationManager<Infestor> getAnimationManager()
	{
		return this.manager;
	}
}
