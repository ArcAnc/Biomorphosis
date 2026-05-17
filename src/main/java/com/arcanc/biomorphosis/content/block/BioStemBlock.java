/**
 * @author ArcAnc
 * Created at: 16.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;

public class BioStemBlock extends BioBaseBlock implements BonemealableBlock
{
	public static final MapCodec<BioStemBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter(BioStemBlock :: fruit),
							ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter(BioStemBlock :: seed),
							propertiesCodec()
					)
					.apply(instance, BioStemBlock :: new)
	);
	
	public static final int MAX_AGE = 7;
	public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
	
	protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
			Block.box(7.0, 14.0, 7.0, 9.0, 16.0, 9.0),
			Block.box(7.0, 12.0, 7.0, 9.0, 16.0, 9.0),
			Block.box(7.0, 10.0, 7.0, 9.0, 16.0, 9.0),
			Block.box(7.0, 8.0, 7.0, 9.0, 16.0, 9.0),
			Block.box(7.0, 6.0, 7.0, 9.0, 16.0, 9.0),
			Block.box(7.0, 4.0, 7.0, 9.0, 16.0, 9.0),
			Block.box(7.0, 2.0, 7.0, 9.0, 16.0, 9.0),
			Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	
	private final ResourceKey<Block> fruit;
	private final ResourceKey<Item> seed;
	
	public BioStemBlock(ResourceKey<Block> fruit, ResourceKey<Item> seed, Properties props)
	{
		super(props);
		this.fruit = fruit;
		this.seed = seed;
	}
	
	@Override
	protected BlockState getInitDefaultState()
	{
		BlockState state = super.getInitDefaultState();
		if (state.hasProperty(AGE))
			state = state.setValue(AGE, 0);
		return state;
	}
	
	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return SHAPE_BY_AGE[state.getValue(AGE)];
	}
	
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		BlockState aboveState = level.getBlockState(pos.above());
		return this.mayPlaceOn(aboveState) || aboveState.is(state.getBlock());
	}
	
	protected boolean mayPlaceOn(BlockState state)
	{
		return state.getBlock() instanceof BioFarmland;
	}
	
	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
	{
		return !state.canSurvive(level, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}
	
	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
	{
		return state.getFluidState().isEmpty();
	}
	
	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
	{
		return pathComputationType == PathComputationType.AIR &&
				!this.hasCollision ||
				super.isPathfindable(state, pathComputationType);
	}
	
	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		if (!level.isAreaLoaded(pos, 1))
			return;
		float growthSpeed = getGrowthSpeed(state, level, pos);
		if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / growthSpeed) + 1) == 0))
		{
			int age = state.getValue(AGE);
			if (age < 7)
				level.setBlock(pos, state.setValue(AGE, age + 1), 2);
			else
				this.growHangingStem(level, pos, state);
			CommonHooks.fireCropGrowPost(level, pos, state);
		}
	}
	
	protected void growHangingStem(ServerLevel level, BlockPos pos, BlockState state)
	{
		BlockPos tipPos = this.getTip(level, pos);
		BlockPos targetPos = tipPos.below();
		BlockState targetState = level.getBlockState(targetPos);
		if (targetState.isEmpty())
		{
			level.setBlockAndUpdate(targetPos, state.setValue(AGE, MAX_AGE));
			return;
		}
		if (targetState.isSolid())
			level.registryAccess().registryOrThrow(Registries.BLOCK).getOptional(this.fruit()).
					ifPresent(block -> level.setBlockAndUpdate(tipPos, block.defaultBlockState()));
	}
	
	protected BlockPos getTip(BlockGetter level, BlockPos pos)
	{
		BlockPos.MutableBlockPos mutablePos = pos.mutable();
		
		while (level.getBlockState(mutablePos.below()).is(this))
			mutablePos.move(Direction.DOWN);
		
		return mutablePos.immutable();
	}
	
	protected float getGrowthSpeed(BlockState blockState, BlockGetter level, BlockPos pos)
	{
		Block block = blockState.getBlock();
		float growthSpeed = 1.0F;
		BlockPos blockpos = pos.above();
		
		for (int x = -1; x <= 1; x++)
		{
			for (int z = -1; z <= 1; z++)
			{
				float modifier = 0.0F;
				BlockState blockstate = level.getBlockState(blockpos.offset(x, 0, z));
				net.neoforged.neoforge.common.util.TriState soilDecision = blockstate.canSustainPlant(level, blockpos.offset(x, 0, z), Direction.DOWN, blockState);
				if (soilDecision.isDefault() ? this.mayPlaceOn(blockstate) : soilDecision.isTrue())
				{
					modifier = 1.0F;
					if (blockstate.isFertile(level, blockpos.offset(x, 0, z)))
						modifier = 3.0F;
				}
				
				if (x != 0 || z != 0)
					modifier /= 4.0F;
				
				growthSpeed += modifier;
			}
		}
		
		BlockPos north = pos.north();
		BlockPos south = pos.south();
		BlockPos west = pos.west();
		BlockPos east = pos.east();
		boolean flag = level.getBlockState(west).is(block) || level.getBlockState(east).is(block);
		boolean flag1 = level.getBlockState(north).is(block) || level.getBlockState(south).is(block);
		if (flag && flag1)
			growthSpeed /= 2.0F;
		else
		{
			boolean flag2 = level.getBlockState(west.north()).is(block)
					|| level.getBlockState(east.north()).is(block)
					|| level.getBlockState(east.south()).is(block)
					|| level.getBlockState(west.south()).is(block);
			if (flag2)
				growthSpeed /= 2.0F;
		}
		
		return growthSpeed;
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
	{
		return new ItemStack(level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.seed()).orElse(this.asItem()));
	}
	
	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state)
	{
		return state.getValue(AGE) != MAX_AGE || level.getBlockState(this.getTip(level, pos).below()).isAir();
	}
	
	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state)
	{
		return true;
	}
	
	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state)
	{
		if (state.getValue(AGE) == MAX_AGE)
		{
			this.growHangingStem(level, pos, state);
			return;
		}
		
		int age = Math.min(MAX_AGE, state.getValue(AGE) + Mth.nextInt(level.random, 2, 5));
		BlockState blockstate = state.setValue(AGE, age);
		level.setBlock(pos, blockstate, 2);
		if (age == MAX_AGE)
			blockstate.randomTick(level, pos, level.random);
	}
	
	public ResourceKey<Block> fruit()
	{
		return this.fruit;
	}
	
	public ResourceKey<Item> seed()
	{
		return this.seed;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}
}
