/**
 * @author ArcAnc
 * Created at: 10.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.entity.MelonMaw;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BioMeatMelonBlock extends BioBaseBlock
{
	private static final int REQUIRED_BLOCKS = 3;
	
	public BioMeatMelonBlock(Properties props)
	{
		super(props);
	}
	
	@Override
	protected void onPlace(BlockState state,
	                       Level level,
	                       BlockPos pos,
	                       BlockState oldState,
	                       boolean movedByPiston)
	{
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!(level instanceof ServerLevel serverLevel))
			return;
		
		for (int offset = REQUIRED_BLOCKS - 1; offset >= 0; offset--)
		{
			BlockPos bottomPos = pos.below(offset);
			if (trySpawnMelonMaw(serverLevel, bottomPos))
				return;
		}
	}
	
	private boolean trySpawnMelonMaw(ServerLevel level, BlockPos bottomPos)
	{
		for (int offset = 0; offset < REQUIRED_BLOCKS; offset++)
		{
			if (!level.getBlockState(bottomPos.above(offset)).is(this))
				return false;
		}
		
		MelonMaw melonMaw = Registration.EntityReg.MOB_MELON_MAW.getEntityHolder().get().create(level);
		if (melonMaw == null)
			return false;
		
		melonMaw.moveTo(
				bottomPos.getX() + 0.5,
				bottomPos.getY(),
				bottomPos.getZ() + 0.5,
				0.0f,
				0.0f);
		if (!level.addFreshEntity(melonMaw))
			return false;
		
		for (int offset = 0; offset < REQUIRED_BLOCKS; offset++)
			level.setBlock(bottomPos.above(offset), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
		return true;
	}
}
