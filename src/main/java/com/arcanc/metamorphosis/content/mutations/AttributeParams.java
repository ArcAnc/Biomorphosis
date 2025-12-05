/**
 * @author ArcAnc
 * Created at: 06.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.mutations;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record AttributeParams(Dynamic<?> rawData)
{
	public static final Codec<AttributeParams> CODEC = Codec.PASSTHROUGH.
			xmap(AttributeParams::new, AttributeParams::rawData);
	
	public static final StreamCodec<ByteBuf, AttributeParams> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
	
	public double getDouble(String key, double defaultValue)
	{
		Optional<? extends Dynamic<?>> child = rawData.get(key).result();
		if (child.isEmpty())
			return defaultValue;
		
		DataResult<Number> num = child.get().asNumber();
		return num.result().map(Number :: doubleValue).orElse(defaultValue);
	}
	
	public int getInt(String key, int defaultValue)
	{
		Optional<? extends Dynamic<?>> child = rawData.get(key).result();
		if (child.isEmpty())
			return defaultValue;
		
		DataResult<Number> num = child.get().asNumber();
		return num.result().map(Number :: intValue).orElse(defaultValue);
	}
	
	public boolean getBool(String key, boolean defaultValue)
	{
		Optional<? extends Dynamic<?>> val = rawData.get(key).result();
		if (val.isEmpty())
			return defaultValue;
		
		DataResult<Boolean> bool = val.get().asBoolean();
		return bool.result().orElse(defaultValue);
	}
	
	public String getString(String key, String defaultData)
	{
		Optional<? extends Dynamic<?>> val = rawData.get(key).result();
		if (val.isEmpty())
			return defaultData;
		
		DataResult<String> str = val.get().asString();
		return str.result().orElse(defaultData);
	}
	
	public boolean has(String key)
	{
		return rawData.get(key).result().isPresent();
	}
}
