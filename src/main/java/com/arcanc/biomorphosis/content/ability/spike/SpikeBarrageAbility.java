/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.spike;

import com.arcanc.biomorphosis.content.ability.Ability;
import com.arcanc.biomorphosis.content.ability.AbilityCastAnimations;
import com.arcanc.biomorphosis.content.ability.IAbilityType;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class SpikeBarrageAbility extends Ability
{
	public static final int COOLDOWN_TICKS = 20 * 10;
	public static final int CAST_TIME_TICKS = 12;
	public static final int VOLLEY_COUNT = 3;
	public static final int SPIKES_PER_VOLLEY = 3;
	public static final int VOLLEY_INTERVAL_TICKS = 4;
	public static final int BARRAGE_ANIMATION_TICKS = VOLLEY_INTERVAL_TICKS * (VOLLEY_COUNT - 1) + 4;
	public static final double RANGE = 40.0d;
	public static final double SPEED_PER_TICK = 1.20d;
	public static final double SIDE_CURVE = 4.00d;
	public static final float DAMAGE = 1.0f;
	public static final ResourceLocation ICON = Database.rl("textures/gui/ability/spike.png");
	
	public SpikeBarrageAbility(Holder<? extends IAbilityType<?>> type)
	{
		super(type, COOLDOWN_TICKS, CAST_TIME_TICKS, AbilityCastAnimations.SPIKE_BARRAGE);
	}
	
	public double range()
	{
		return RANGE;
	}
	
	public double speedPerTick()
	{
		return SPEED_PER_TICK;
	}
	
	@Override
	public Optional<ResourceLocation> getIcon()
	{
		return Optional.of(ICON);
	}
}
