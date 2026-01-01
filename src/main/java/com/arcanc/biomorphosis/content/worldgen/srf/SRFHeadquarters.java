/**
 * @author ArcAnc
 * Created at: 27.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.srf;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SRFHeadquarters extends Structure
{
	public static final MapCodec<SRFHeadquarters> CODEC = simpleCodec(SRFHeadquarters :: new);
	
	public SRFHeadquarters(StructureSettings settings)
	{
		super(settings);
	}
	
	@Override
	protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context)
	{
		//FIXME: прописать адекватный плейсмент, когда начну юзать
		return Structure.onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, piecesBuilder ->
		{});
	}
	
	@Override
	public @NotNull StructureType<SRFHeadquarters> type()
	{
		return Registration.StructureTypeReg.SRF_HEADQUARTERS.get();
	}
}
