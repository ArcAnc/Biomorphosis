/**
 * @author ArcAnc
 * Created at: 26.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.effect;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.DamageHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AcidEffect extends MobEffect
{
	public static final int DAMAGE_PERIOD = 10;
	public static final int DURATION = 60;
	private static final float DAMAGE_PER_STACK = 1.0f;

	public AcidEffect()
	{
		super(MobEffectCategory.HARMFUL, 0xffe100);
	}

	/**
	 * Adds a complete, independent acid damage sequence. Keeping separate next-damage timestamps makes
	 * overlapping hits stack without replacing or extending one another.
	 */
	public static void applyTo(LivingEntity target)
	{
		if (target.level().isClientSide())
			return;

		long gameTime = target.level().getGameTime();
		AcidStacks current = target.getData(Registration.DataAttachmentsReg.ACID_STACKS);
		List<AcidStack> updated = new ArrayList<>(current.stacks());
		updated.add(new AcidStack(gameTime + DAMAGE_PERIOD, gameTime + DURATION));
		target.setData(Registration.DataAttachmentsReg.ACID_STACKS, new AcidStacks(updated));
		target.addEffect(new MobEffectInstance(Registration.EffectReg.ACID, DURATION + 1, 0, false, true, true));
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier)
	{
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity livingEntity, int amplifier)
	{
		if (livingEntity.level().isClientSide())
			return true;

		long gameTime = livingEntity.level().getGameTime();
		AcidStacks current = livingEntity.getData(Registration.DataAttachmentsReg.ACID_STACKS);
		if (current.stacks().isEmpty())
			return true;

		int stacksDealingDamage = 0;
		List<AcidStack> updated = new ArrayList<>(current.stacks().size());
		for (AcidStack stack : current.stacks())
		{
			if (stack.nextDamageTime() <= gameTime && stack.nextDamageTime() <= stack.expiresAt())
			{
				stacksDealingDamage++;
				long nextDamageTime = stack.nextDamageTime() + DAMAGE_PERIOD;
				if (nextDamageTime <= stack.expiresAt())
					updated.add(new AcidStack(nextDamageTime, stack.expiresAt()));
			}
			else if (stack.nextDamageTime() <= stack.expiresAt())
				updated.add(stack);
		}

		if (stacksDealingDamage > 0)
			DamageHelper.acidDamage(DAMAGE_PER_STACK * stacksDealingDamage, livingEntity);
		if (updated.size() != current.stacks().size() || stacksDealingDamage > 0)
			livingEntity.setData(Registration.DataAttachmentsReg.ACID_STACKS, new AcidStacks(updated));
		return true;
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance)
	{
	}

	public record AcidStack(long nextDamageTime, long expiresAt)
	{
		public static final Codec<AcidStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.LONG.fieldOf("next_damage_time").forGetter(AcidStack :: nextDamageTime),
				Codec.LONG.fieldOf("expires_at").forGetter(AcidStack :: expiresAt)
		).apply(instance, AcidStack :: new));
		public static final StreamCodec<RegistryFriendlyByteBuf, AcidStack> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, AcidStack :: nextDamageTime,
				ByteBufCodecs.VAR_LONG, AcidStack :: expiresAt,
				AcidStack :: new);
	}

	public record AcidStacks(List<AcidStack> stacks)
	{
		public static final AcidStacks EMPTY = new AcidStacks(List.of());
		public static final Codec<AcidStacks> CODEC = AcidStack.CODEC.listOf().xmap(AcidStacks :: new, AcidStacks :: stacks);
		public static final StreamCodec<RegistryFriendlyByteBuf, AcidStacks> STREAM_CODEC = ByteBufCodecs.
				<RegistryFriendlyByteBuf, AcidStack, List<AcidStack>>collection(ArrayList :: new, AcidStack.STREAM_CODEC).
				map(AcidStacks :: new, AcidStacks :: stacks);
	}
}
