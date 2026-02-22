/**
 * @author ArcAnc
 * Created at: 11.02.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;


import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BioHangingMossBlock extends BioBaseBlock implements BonemealableBlock
{
	public static final MapCodec<BioHangingMossBlock> CODEC = simpleCodec(BioHangingMossBlock::new);
	private static final VoxelShape TIP_SHAPE = Block.box(1.0, 2.0, 1.0, 15.0, 16.0, 15.0);
	private static final VoxelShape BASE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
	public static final BooleanProperty TIP = BlockHelper.BlockProperties.TIP;
	
	public BioHangingMossBlock(BlockBehaviour.Properties props)
	{
		super(props);
		this.registerDefaultState(this.stateDefinition.any().
				setValue(TIP, true));
	}
	
	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state,
	                                       @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos,
	                                       @NotNull CollisionContext context)
	{
		return state.getValue(TIP) ? TIP_SHAPE : BASE_SHAPE;
	}
	
	@Override
	protected boolean propagatesSkylightDown(@NotNull BlockState state,
	                                         @NotNull BlockGetter level,
	                                         @NotNull BlockPos pos)
	{
		return true;
	}
	
	@Override
	protected boolean canSurvive(@NotNull BlockState state,
	                             @NotNull LevelReader level,
	                             @NotNull BlockPos pos)
	{
		return this.canStayAtPosition(level, pos);
	}
	
	private boolean canStayAtPosition(@NotNull BlockGetter level, @NotNull BlockPos pos)
	{
		BlockPos blockpos = pos.relative(Direction.UP);
		BlockState blockstate = level.getBlockState(blockpos);
		return MultifaceBlock.canAttachTo(level, Direction.UP, blockpos, blockstate) || blockstate.is(this);
	}
	
	@Override
	protected @NotNull BlockState updateShape(@NotNull BlockState state,
	                                          @NotNull Direction direction,
	                                          @NotNull BlockState neighborState,
	                                          @NotNull LevelAccessor level,
	                                          @NotNull BlockPos pos,
	                                          @NotNull BlockPos neighborPos)
	{
		if (!this.canStayAtPosition(level, pos))
			level.scheduleTick(pos, this, 1);
		
		return state.setValue(TIP, !level.getBlockState(pos.below()).is(this));
	}
	
	@Override
	protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random)
	{
		super.randomTick(state, level, pos, random);
	}
	
	@Override
	protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random)
	{
		if (!this.canStayAtPosition(level, pos))
			level.destroyBlock(pos, true);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
	{
		builder.add(TIP);
	}
	
	@Override
	public boolean isValidBonemealTarget(@NotNull LevelReader level,
	                                     @NotNull BlockPos pos,
	                                     @NotNull BlockState state)
	{
		return this.canGrowInto(level.getBlockState(this.getTip(level, pos).below()));
	}
	
	private boolean canGrowInto(@NotNull BlockState state)
	{
		return state.isAir();
	}
	
	public BlockPos getTip(@NotNull BlockGetter level, @NotNull BlockPos pos)
	{
		BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();
		
		BlockState blockstate;
		do {
			blockpos$mutableblockpos.move(Direction.DOWN);
			blockstate = level.getBlockState(blockpos$mutableblockpos);
		} while (blockstate.is(this));
		
		return blockpos$mutableblockpos.relative(Direction.UP).immutable();
	}
	
	@Override
	public boolean isBonemealSuccess(@NotNull Level level,
	                                 @NotNull RandomSource random,
	                                 @NotNull BlockPos pos,
	                                 @NotNull BlockState state)
	{
		return true;
	}
	
	@Override
	public void performBonemeal(@NotNull ServerLevel level,
	                            @NotNull RandomSource random,
	                            @NotNull BlockPos pos,
	                            @NotNull BlockState state)
	{
		BlockPos blockpos = this.getTip(level, pos).below();
		if (this.canGrowInto(level.getBlockState(blockpos)))
			level.setBlockAndUpdate(blockpos, state.setValue(TIP, true));
	}
	
	@Override
	public @NotNull MapCodec<BioHangingMossBlock> codec()
	{
		return CODEC;
	}
}
