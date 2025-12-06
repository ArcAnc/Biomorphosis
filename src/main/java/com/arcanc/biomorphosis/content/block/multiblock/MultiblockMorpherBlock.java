/**
 * @author ArcAnc
 * Created at: 14.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;


import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPartBlock;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class MultiblockMorpherBlock extends StaticMultiblockPartBlock<MultiblockMorpher>
{
	private static final VoxelShape BASIC_SHAPE = Shapes.or(
			box(0,0,0, 16, 1, 16),
			box(0,0,0, 16, 8, 1),
			box(0,0,0, 1, 8, 16),
			box(15, 0, 0, 16, 8, 16),
			box(0, 0, 15, 16, 8, 16));

	public MultiblockMorpherBlock(Properties props)
	{
		super(Registration.BETypeReg.BE_MULTIBLOCK_MORPHER, props);
	}

	@Override
	protected void onRemove(@NotNull BlockState state,
							@NotNull Level level,
							@NotNull BlockPos pos,
							@NotNull BlockState newState,
							boolean movedByPiston)
	{
		if (state.hasBlockEntity() && !state.is(newState.getBlock()))
			level.removeBlockEntity(pos);
	}

	/*FIXME: add custom logic for shape, which must be received from multiblock definition, which means json*/
	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state,
										   @NotNull BlockGetter level,
										   @NotNull BlockPos pos,
										   @NotNull CollisionContext context)
	{
		return state.getValue(STATE) == MultiblockState.FORMED ? super.getShape(state, level, pos, context) : BASIC_SHAPE;
	}

	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
	{
		return RenderShape.INVISIBLE;
	}
}
