/**
 * @author ArcAnc
 * Created at: 20.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome.wastes;


import com.arcanc.biomorphosis.content.worldgen.BioBiomes;
import com.arcanc.biomorphosis.content.worldgen.biome.TagRandomStateRuleSource;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class WastesSurfaceRuleData
{
	private static final int MAX_WASTES_SURFACE_DEPTH = 10;
	private static final SurfaceRules.RuleSource NORPHED_SURFACE = new TagRandomStateRuleSource(BioBlockTags.NORPHED_BLOCKS);
	private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	
	public static SurfaceRules.RuleSource makeRules()
	{
		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(
						SurfaceRules.isBiome(BioBiomes.WASTES),
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, NORPHED_SURFACE),
								SurfaceRules.ifTrue(
										SurfaceRules.stoneDepthCheck(MAX_WASTES_SURFACE_DEPTH - 1, false, CaveSurface.FLOOR),
										SANDSTONE
								)
						)
				)
		);
	}
	
	private static SurfaceRules.RuleSource makeStateRule(Block block)
	{
		return SurfaceRules.state(block.defaultBlockState());
	}
}
