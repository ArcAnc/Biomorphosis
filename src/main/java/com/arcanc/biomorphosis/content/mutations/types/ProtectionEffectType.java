/**
 * @author ArcAnc
 * Created at: 09.01.2026
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

public class ProtectionEffectType implements IGeneEffectType<ProtectionEffectType>
{
	public static final MapCodec<ProtectionEffectType> CODEC = MapCodec.unit(ProtectionEffectType::new);
	
	@Override
	public MapCodec<ProtectionEffectType> mapCodec()
	{
		return CODEC;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return Database.GUI.Genome.PROTECTION.id();
	}
	
	@Override
	public void apply(LivingEntity entity, AttributeParams params)
	{
	
	}
	
	@Override
	public void remove(LivingEntity entity, AttributeParams params)
	{
	
	}
	
	@Override
	public void tick(LivingEntity entity, AttributeParams params)
	{
	
	}
}
