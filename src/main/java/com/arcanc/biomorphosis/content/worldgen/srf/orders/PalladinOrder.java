/**
 * @author ArcAnc
 * Created at: 27.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.srf.orders;


import com.arcanc.biomorphosis.util.SerializableColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PalladinOrder(SerializableColor color)
{
	public static final Codec<PalladinOrder> CODEC = RecordCodecBuilder.create(instance -> instance.
			group(
					SerializableColor.CODEC.fieldOf("color").forGetter(PalladinOrder :: color)
	).apply(instance, PalladinOrder :: new));
	
}
