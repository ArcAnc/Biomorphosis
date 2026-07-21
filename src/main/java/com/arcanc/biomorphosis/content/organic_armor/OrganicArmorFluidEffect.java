/**
 * @author ArcAnc
 * Created at: 16.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.organic_armor;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record OrganicArmorFluidEffect(
		ResourceKey<IOrganicArmorEffect> effect,
		ResourceKey<FluidType> fluidType,
		ResourceKey<OrganicArmorType> organicArmor)
{
	public static final Codec<OrganicArmorFluidEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceKey.codec(Registration.OrganicArmorReg.EFFECT_KEY).fieldOf("effect").forGetter(OrganicArmorFluidEffect :: effect),
			ResourceKey.codec(NeoForgeRegistries.Keys.FLUID_TYPES).fieldOf("fluid_type").forGetter(OrganicArmorFluidEffect :: fluidType),
			ResourceKey.codec(Registration.OrganicArmorReg.TYPE_KEY).fieldOf("organic_armor").forGetter(OrganicArmorFluidEffect :: organicArmor)
	).apply(instance, OrganicArmorFluidEffect :: new));
}
