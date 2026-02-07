/**
 * @author ArcAnc
 * Created at: 09.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util;


import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public record SerializableColor(int color, boolean isRaw)
{
	private static final Codec<SerializableColor> RGB_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.intRange(0, 255).fieldOf("red").forGetter(SerializableColor :: red), Codec.intRange(0, 255).fieldOf("green").forGetter(SerializableColor :: green), Codec.intRange(0, 255).fieldOf("blue").forGetter(SerializableColor :: blue)).apply(instance, SerializableColor :: new));
	
	private static final Codec<SerializableColor> INT_CODEC = Codec.INT.xmap(SerializableColor :: new, SerializableColor :: color);
	
	public static final Codec<SerializableColor> CODEC = Codec.xor(RGB_CODEC, INT_CODEC).xmap(either -> either.map(Function.identity(), Function.identity()), serializableColor ->
	{
		if (! serializableColor.isRaw())
			return Either.left(serializableColor);
		else
			return Either.right(serializableColor);
	});
	
	public static final StreamCodec<ByteBuf, SerializableColor> STREAM_CODEC = ByteBufCodecs.INT.map(SerializableColor :: new, SerializableColor :: color);
	
	public SerializableColor(int color)
	{
		this(color, true);
	}
	
	public SerializableColor(int red, int green, int blue)
	{
		this(MathHelper.ColorHelper.color(red, green, blue), false);
	}
	
	public int red()
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
