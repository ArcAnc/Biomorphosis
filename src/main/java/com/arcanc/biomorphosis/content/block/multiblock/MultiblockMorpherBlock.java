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
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
	protected void onRemove(BlockState state,
							Level level,
							BlockPos pos,
							BlockState newState,
							boolean movedByPiston)
	{
		if (state.hasBlockEntity() && !state.is(newState.getBlock()))
		{
			if (level instanceof ServerLevel)
				BlockHelper.castTileEntity(level, pos, MultiblockMorpher.class).
						ifPresent(morpher -> ItemHelper.dropContents(level, pos, morpher.getInputItemHandler()));
			level.removeBlockEntity(pos);
		}
	}
	
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack,
											  BlockState state,
											  Level level,
											  BlockPos pos,
											  Player player,
											  InteractionHand hand,
											  BlockHitResult hitResult)
	{
		if (player.isShiftKeyDown())
			return extractInput(level, pos, player);
		if (stack.isEmpty())
			return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
		
		return BlockHelper.castTileEntity(level, pos, MultiblockMorpher.class).
				map(morpher ->
				{
					ItemStack returned = morpher.insertInput(stack, true);
					if (ItemStack.matches(stack, returned))
						return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
					
					if (level.isClientSide())
						return ItemInteractionResult.SUCCESS;
					
					player.setItemInHand(hand, morpher.insertInput(stack, false));
					return ItemInteractionResult.CONSUME;
				}).
				orElseGet(() -> super.useItemOn(stack, state, level, pos, player, hand, hitResult));
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state,
											   Level level,
											   BlockPos pos,
											   Player player,
											   BlockHitResult hitResult)
	{
		if (player.isShiftKeyDown())
			return switch (extractInput(level, pos, player))
			{
				case SUCCESS, CONSUME -> InteractionResult.sidedSuccess(level.isClientSide());
				default -> super.useWithoutItem(state, level, pos, player, hitResult);
			};
		
		return BlockHelper.castTileEntity(level, pos, MultiblockMorpher.class).
				map(morpher ->
				{
					if (level.isClientSide())
						return InteractionResult.SUCCESS;
					return morpher.tryStartMorphing() ? InteractionResult.CONSUME : InteractionResult.PASS;
				}).
				filter(result -> result != InteractionResult.PASS).
				orElseGet(() -> super.useWithoutItem(state, level, pos, player, hitResult));
		
	}
	
	private ItemInteractionResult extractInput(Level level, BlockPos pos, Player player)
	{
		return BlockHelper.castTileEntity(level, pos, MultiblockMorpher.class).
				map(morpher ->
				{
					if (morpher.peekInput().isEmpty())
						return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
					if (level.isClientSide())
						return ItemInteractionResult.SUCCESS;
					
					ItemStack extracted = morpher.extractInput();
					if (!player.addItem(extracted))
						player.drop(extracted, false);
					return ItemInteractionResult.CONSUME;
				}).
				orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
	}

	/*FIXME: add custom logic for shape, which must be received from multiblock definition, which means json*/
	@Override
	protected VoxelShape getShape(BlockState state,
										   BlockGetter level,
										   BlockPos pos,
										   CollisionContext context)
	{
		return state.getValue(STATE) == MultiblockState.FORMED ? super.getShape(state, level, pos, context) : BASIC_SHAPE;
	}
	
}
