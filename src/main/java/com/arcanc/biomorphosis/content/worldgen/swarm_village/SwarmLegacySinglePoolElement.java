/**
 * @author ArcAnc
 * Created at: 19.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.swarm_village;


import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SwarmLegacySinglePoolElement extends LegacySinglePoolElement
{
	private static final Holder<StructureProcessorList> PROCESSORS = Holder.direct(new StructureProcessorList(List.of()));
	
	public SwarmLegacySinglePoolElement(ResourceLocation id, StructureTemplatePool.Projection projection)
	{
		super(Either.left(id), PROCESSORS, projection, Optional.empty());
	}
	
	public static @NotNull SwarmLegacySinglePoolElement terrainMatching(ResourceLocation id)
	{
		return new SwarmLegacySinglePoolElement(id, StructureTemplatePool.Projection.TERRAIN_MATCHING);
	}
	
	public static @NotNull SwarmLegacySinglePoolElement rigid(ResourceLocation id)
	{
		return new SwarmLegacySinglePoolElement(id, StructureTemplatePool.Projection.RIGID);
	}
}
