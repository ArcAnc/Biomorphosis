/**
 * @author ArcAnc
 * Created at: 26.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.effect.AcidEffect;
import com.arcanc.biomorphosis.util.helper.DamageHelper;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ZirisProjectile extends ThrowableProjectile implements PAnimatable<ZirisProjectile>
{
	public static final float IMPACT_DAMAGE = 2.0f;

	private final PAnimationManager<ZirisProjectile> manager = PLibHelper.createManager(this);
	private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();

	public ZirisProjectile(EntityType<? extends ThrowableProjectile> type, Level level)
	{
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
	}

	@Override
	public void tick()
	{
		if (this.firstTick)
			this.setNoGravity(true);
		super.tick();

		RandomSource random = this.level().random;
		for (int i = 0; i < 4; i++)
			this.level().addParticle(ParticleTypes.WAX_ON,
					this.getX() + random.nextFloat() * 0.75f - 0.75f,
					this.getY() + random.nextFloat() * 0.75f - 0.75f,
					this.getZ() + random.nextFloat() * 0.75f - 0.75f,
					0, 0.005f, 0);
	}

	@Override
	protected void onHitEntity(EntityHitResult result)
	{
		if (!(this.level() instanceof ServerLevel))
			return;

		Entity hit = result.getEntity();
		if (hit instanceof LivingEntity living)
		{
			DamageHelper.acidDamage(IMPACT_DAMAGE, living);
			AcidEffect.applyTo(living);
		}
		this.discard();
	}

	@Override
	protected void onHitBlock(BlockHitResult result)
	{
		if (!this.level().isClientSide())
			this.discard();
	}

	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<ZirisProjectile> registrar)
	{
		registrar.add(() -> state ->
		{
			state.controller().play(IDLE);
			return state.controller().getState();
		});
	}

	@Override
	public PAnimationManager<ZirisProjectile> getAnimationManager(AnimManagerKey key)
	{
		return this.manager;
	}
}
