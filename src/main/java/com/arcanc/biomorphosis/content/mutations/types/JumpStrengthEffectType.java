/**
 * @author ArcAnc
 * Created at: 24.01.2026
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class JumpStrengthEffectType implements IGeneEffectType<JumpStrengthEffectType>
{
	public static final MapCodec<JumpStrengthEffectType> CODEC = MapCodec.unit(JumpStrengthEffectType :: new);
	
	@Override
	public MapCodec<JumpStrengthEffectType> mapCodec()
	{
		return CODEC;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return Database.GUI.Genome.JUMP_STRENGTH.id();
	}
	
	@Override
	public void apply(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
		int modifier = params.getInt("modifier", 0);
		if (modifier == 0)
			return;
		double dMod = modifier / 100d;
		AttributeInstance instance = entity.getAttribute(Attributes.JUMP_STRENGTH);
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
		AttributeInstance instance = entity.getAttribute(Attributes.JUMP_STRENGTH);
		if (instance == null)
			return;
		instance.removeModifier(this.getId());
	}
	
	@Override
	public void tick(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
	
	}
}
