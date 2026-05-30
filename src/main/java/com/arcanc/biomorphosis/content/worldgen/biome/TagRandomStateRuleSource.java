/**
 * @author ArcAnc
 * Created at: 28.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record TagRandomStateRuleSource(TagKey<Block> tag) implements SurfaceRules.RuleSource
{
	@Override
	public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec()
	{
		return KeyDispatchDataCodec.of(MapCodec.unit(this));
	}
	
	@Override
	public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context)
	{
		return (x, y, z) -> BuiltInRegistries.BLOCK.
				getRandomElementOf(this.tag, context.randomState.random.at(x, y, z)).
				map(Holder :: value).
				orElseThrow().
				defaultBlockState();
	}
}
