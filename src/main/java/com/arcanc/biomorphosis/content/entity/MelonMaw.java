/**
 * @author ArcAnc
 * Created at: 09.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;


import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MelonMaw extends Monster implements PAnimatable<MelonMaw>
{
	private final PAnimationManager<MelonMaw> manager = PLibHelper.createManager(this);
	
	private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
	private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").withSpeed(2.0f).build();
	private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
	private static final PRawAnimation DEATH = PRawAnimation.begin().thenHold("death").build();
	
	public MelonMaw(EntityType<? extends Monster> entityType, Level level)
	{
		super(entityType, level);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
				this,
				LivingEntity.class,
				true,
				entity -> !entity.getType().is(BioEntityTags.MELON_MAW)));
		
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
		
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}
	
	@Override
	public PAnimationManager<MelonMaw> getAnimationManager(AnimManagerKey key)
	{
		return this.manager;
	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<MelonMaw> registrar)
	{
		registrar.add("animController", () -> state ->
		{
			MelonMaw animatable = state.animatable();
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
}
