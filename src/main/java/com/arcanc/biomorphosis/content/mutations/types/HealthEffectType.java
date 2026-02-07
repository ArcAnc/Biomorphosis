/**
 * @author ArcAnc
 * Created at: 06.10.2025
 * Copyright (c) 2025
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class HealthEffectType implements IGeneEffectType<HealthEffectType>
{
	public static final MapCodec<HealthEffectType> CODEC = MapCodec.unit(HealthEffectType::new);
	
	@Override
	public MapCodec<HealthEffectType> mapCodec()
	{
		return CODEC;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return Database.GUI.Genome.HEALTH.id();
	}
	
	@Override
	public void apply(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
		int healthAmount = params.getInt("amount", 0);
		if (healthAmount == 0)
			return;
		AttributeInstance instance = entity.getAttribute(Attributes.MAX_HEALTH);
		if (instance == null)
			return;
		instance.removeModifier(this.getId());
		instance.addTransientModifier(new AttributeModifier(this.getId(), healthAmount, AttributeModifier.Operation.ADD_VALUE));
	}
	
	@Override
	public void remove(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
		int healthAmount = params.getInt("amount", 0);
		if (healthAmount == 0)
			return;
		AttributeInstance instance = entity.getAttribute(Attributes.MAX_HEALTH);
		if (instance == null)
			return;
		instance.removeModifier(this.getId());
	}
	
	@Override
	public void tick(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
	
	}
}
