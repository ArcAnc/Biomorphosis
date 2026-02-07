/**
 * @author ArcAnc
 * Created at: 23.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.types;


import com.arcanc.biomorphosis.content.mutations.AttributeParams;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

public class SwimSpeedEffectType implements IGeneEffectType<SwimSpeedEffectType>
{
	public static final MapCodec<SwimSpeedEffectType> CODEC = MapCodec.unit(SwimSpeedEffectType :: new);
	
	@Override
	public MapCodec<SwimSpeedEffectType> mapCodec()
	{
		return CODEC;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return Database.GUI.Genome.SWIM_SPEED.id();
	}
	
	@Override
	public void apply(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
		//we get modifier in percent, so, have to convert to double
		int modifier = params.getInt("modifier", 0);
		if (modifier == 0)
			return;
		
		double dMod = modifier/ 100d;
		AttributeInstance instance = entity.getAttribute(NeoForgeMod.SWIM_SPEED);
		if (instance == null)
			return;
		instance.removeModifier(this.getId());
		instance.addTransientModifier(new AttributeModifier(this.getId(), dMod, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
	}
	
	@Override
	public void remove(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
		int modifier = params.getInt("modifier", 0);
		if (modifier == 0)
			return;
		AttributeInstance instance = entity.getAttribute(NeoForgeMod.SWIM_SPEED);
		if (instance == null)
			return;
		instance.removeModifier(this.getId());
	}
	
	@Override
	public void tick(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
	
	}
}
