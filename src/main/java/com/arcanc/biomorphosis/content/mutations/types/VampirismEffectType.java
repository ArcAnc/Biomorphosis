/**
 * @author ArcAnc
 * Created at: 26.01.2026
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
import org.jetbrains.annotations.NotNull;

public class VampirismEffectType implements IGeneEffectType<VampirismEffectType>
{
	public static final MapCodec<VampirismEffectType> CODEC = MapCodec.unit(VampirismEffectType :: new);
	
	@Override
	public MapCodec<VampirismEffectType> mapCodec()
	{
		return CODEC;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return Database.GUI.Genome.VAMPIRISM.id();
	}
	
	@Override
	public void apply(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
	}
	
	@Override
	public void remove(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
	}
	
	@Override
	public void tick(@NotNull LivingEntity entity, @NotNull AttributeParams params)
	{
	}
	
	public void handle(LivingEntity damager, float newDamage, @NotNull AttributeParams params)
	{
		int healthPerDamage = params.getInt("modifier", 0);
		if (healthPerDamage == 0)
			return;
		
		damager.heal(newDamage * (healthPerDamage / 100f));
	}
}
