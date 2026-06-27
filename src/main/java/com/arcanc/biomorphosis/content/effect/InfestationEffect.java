/**
 * @author ArcAnc
 * Created at: 22.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.effect;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.DamageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class InfestationEffect extends MobEffect
{
	private static final float DAMAGE_AMOUNT = 2.0f;
	private static final int DAMAGE_PERIOD = 10; // IN TICKS
	
	public InfestationEffect()
	{
		super(MobEffectCategory.HARMFUL, 0x405e1a);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier)
	{
		return duration % DAMAGE_PERIOD == 0;
	}

	@Override
	public boolean applyEffectTick(LivingEntity livingEntity, int amplifier)
	{
		if (!livingEntity.level().isClientSide())
			DamageHelper.infestationDamage(DAMAGE_AMOUNT, livingEntity);
		return true;
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance)
	{
	}

	@Override
	public void onMobRemoved(LivingEntity livingEntity, int amplifier, Entity.RemovalReason reason)
	{
		if (reason != Entity.RemovalReason.KILLED || !(livingEntity.level() instanceof ServerLevel level))
			return;

		BlockPos pos = livingEntity.blockPosition();
		Registration.EntityReg.MOB_LARVA.getEntityHolder().get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
	}
}
