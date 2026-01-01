/**
 * @author ArcAnc
 * Created at: 27.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.srf.orders;


import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record PalladinOrder(OrderColor color)
{
	public static final Codec<PalladinOrder> CODEC = RecordCodecBuilder.create(instance -> instance.
			group(
					OrderColor.CODEC.fieldOf("color").forGetter(PalladinOrder :: color)
	).apply(instance, PalladinOrder :: new));
	
	public record OrderColor(int color, boolean isRaw)
	{
		private static final Codec<OrderColor> RGB_CODEC = RecordCodecBuilder.create(instance -> instance.
				group(
						Codec.intRange(0, 255).fieldOf("red").forGetter(OrderColor :: red),
						Codec.intRange(0, 255).fieldOf("green").forGetter(OrderColor :: green),
						Codec.intRange(0, 255).fieldOf("blue").forGetter(OrderColor :: blue)
		).apply(instance, OrderColor :: new));
		
		private static final Codec<OrderColor> INT_CODEC = Codec.INT.xmap(OrderColor :: new, OrderColor :: color);
		
		public static final Codec<OrderColor> CODEC = Codec.xor(RGB_CODEC, INT_CODEC).
				xmap(either -> either.map(
						Function.identity(), Function.identity()),
				orderColor ->
				{
					if (!orderColor.isRaw())
						return Either.left(orderColor);
					else
						return Either.right(orderColor);
				});
		
		
		public OrderColor(int color)
		{
			this(color, true);
		}
		
		public OrderColor(int red, int green, int blue)
		{
			this(MathHelper.ColorHelper.color(red, green, blue), false);
		}
		
		public int red ()
		{
			return MathHelper.ColorHelper.red(this.color);
		}
		
		public int green()
		{
			return MathHelper.ColorHelper.green(this.color);
		}
		
		public int blue()
		{
			return MathHelper.ColorHelper.blue(this.color);
		}
	}
}
