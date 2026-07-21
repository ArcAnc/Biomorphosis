/**
 * @author ArcAnc
 * Created at: 15.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;


import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import com.arcanc.biomorphosis.content.entity.ai.brain.InfestorBrain;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Infestor extends Monster implements PAnimatable<Infestor>
{
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
	}

	@Override
	protected Brain<?> makeBrain(Dynamic<?> dynamic)
	{
		return InfestorBrain.makeBrain(dynamic);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Brain<Infestor> getBrain()
	{
		return (Brain<Infestor>)super.getBrain();
	}

	@Override
	protected void customServerAiStep()
	{
		if (this.level() instanceof ServerLevel serverLevel)
		{
			ensureHomeMemory();
			serverLevel.getProfiler().push("infestorBrain");
			this.getBrain().tick(serverLevel, this);
			serverLevel.getProfiler().pop();
		}
		super.customServerAiStep();
	}

	@Override
	public @Nullable LivingEntity getTarget()
	{
		return this.getTargetFromBrain();
	}

	@Override
	public boolean canAttack(LivingEntity target)
	{
		return super.canAttack(target) && isValidInfestationTarget(this, target);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
	                                              DifficultyInstance difficulty,
	                                              MobSpawnType spawnType,
	                                              @Nullable SpawnGroupData spawnGroupData)
	{
		this.getBrain().setMemory(Registration.AIReg.INFESTOR_HOME_POS.get(), this.blockPosition());
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	public void infest(LivingEntity target)
	{
		if (!isValidInfestationTarget(this, target))
			return;

		target.addEffect(new MobEffectInstance(Registration.EffectReg.INFESTATION, -1, 0, false, true, true));
		if (target instanceof Mob mob && mob.getTarget() == this)
			mob.setTarget(null);
	}

	public static boolean isValidInfestationTarget(Infestor infestor, @Nullable LivingEntity target)
	{
		if (!SwarmTargeting.isValidSwarmEnemy(infestor, target))
			return false;
		if (target.hasEffect(Registration.EffectReg.INFESTATION))
			return false;
		if (target instanceof Drowned)
			return false;
		if (target instanceof WaterAnimal)
			return false;
		return true;
	}

	private void ensureHomeMemory()
	{
		if (this.getBrain().getMemory(Registration.AIReg.INFESTOR_HOME_POS.get()).isEmpty())
			this.getBrain().setMemory(Registration.AIReg.INFESTOR_HOME_POS.get(), this.blockPosition());
	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<Infestor> registrar)
	{
		registrar.add("animController", () -> state ->
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
				}).
				add("death", () -> state ->
				{
					if (state.animatable().isDeadOrDying())
						state.controller().play(DEATH);
					else
						state.controller().stop();
					return  state.controller().getState();
				});
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
	public PAnimationManager<Infestor> getAnimationManager(AnimManagerKey key)
	{
		return this.manager;
	}
}
