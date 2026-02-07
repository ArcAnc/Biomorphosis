/**
 * @author ArcAnc
 * Created at: 28.09.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations;


import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum GeneRarity implements StringRepresentable
{
	COMMON("slyth", MathHelper.ColorHelper.color(46, 204, 64)),
	UNCOMMON("grath", MathHelper.ColorHelper.color(255, 220, 0)),
	ULTRA_RARE("xyrr", MathHelper.ColorHelper.color(255, 65, 54)),
	EPIC("thrynn", MathHelper.ColorHelper.color(0, 116, 217)),
	LEGENDARY("vorrath", MathHelper.ColorHelper.color(177, 13, 201));
	
	public static final Codec<GeneRarity> CODEC = StringRepresentable.fromValues(GeneRarity :: values);
	public static final StreamCodec<ByteBuf, GeneRarity> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
	
	private final String name;
	private final int color;
	
	GeneRarity(String name, int color)
	{
		this.name = name;
		this.color = color;
	}
	
	@Override
	public @NotNull String getSerializedName()
	{
		return this.name;
	}
	
	public int getColor()
	{
		return this.color;
	}
}
