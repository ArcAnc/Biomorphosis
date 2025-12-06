/**
 * @author ArcAnc
 * Created at: 29.09.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.types;

import com.arcanc.biomorphosis.content.mutations.AttributeParams;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface IGeneEffectType<T extends IGeneEffectType<?>>
{
	Codec<IGeneEffectType<?>> CODEC = Registration.GenomeReg.EFFECT_TYPE_REGISTRY.byNameCodec();
	
	MapCodec<T> mapCodec();
	
	ResourceLocation getId();

	void apply (LivingEntity entity, AttributeParams params);
	
	void remove (LivingEntity entity, AttributeParams params);
	
	void tick (LivingEntity entity, AttributeParams params);
}
