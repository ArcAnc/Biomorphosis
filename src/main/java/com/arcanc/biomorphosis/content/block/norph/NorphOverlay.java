/**
 * @author ArcAnc
 * Created at: 11.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.norph;

import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioBlockTags;
import com.arcanc.biomorphosis.util.helper.ZoneHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class NorphOverlay extends MultifaceBlock implements BlockInterfaces.IWrencheable
{
    public static final MapCodec<NorphOverlay> CODEC = simpleCodec(NorphOverlay :: new);
    private final NorphSpreadConfig CONFIG = new NorphSpreadConfig(this);
    private final MultifaceSpreader SPREADER = new MultifaceSpreader(CONFIG);

    public NorphOverlay(Properties properties)
    {
        super(properties);
    }

    @Override
    public MapCodec<NorphOverlay> codec()
    {
        return CODEC;
    }

    @Override
    public MultifaceSpreader getSpreader()
    {
        return this.SPREADER;
    }

    public NorphSpreadConfig getSpreaderConfig()
    {
        return this.CONFIG;
    }

    @Override
    protected void tick(BlockState state,
                        ServerLevel level,
                        BlockPos pos,
                        RandomSource random)
    {
        trySpread(state, level, pos, random);
        level.scheduleTick(pos, this, NorphSpreadConfig.getTicksDelay(level));
    }

    private void trySpread(BlockState state,
                           ServerLevel level,
                           BlockPos pos,
                           RandomSource random)
    {
        if (!level.isAreaLoaded(pos, 1))
            return;
        if (this.CONFIG.getSourcePos(level, pos) == null)
        {
            Set<BlockState> nearbyBlocks = Arrays.stream(Direction.values()).map(direction -> level.getBlockState(pos.relative(direction))).
                    filter(checkState -> checkState.is(BioBlockTags.NORPH) || checkState.is(BioBlockTags.NORPH_SOURCE)).
                    collect(Collectors.toSet());
            if (random.nextFloat() < 1 - (nearbyBlocks.size() * .167f))
            {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.blockUpdated(pos, Blocks.AIR);
                state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
                return;
            }
        }

        if (hasAnyVacantFace(state))
            addFace(state, level, pos, random);
        else
            grow(state, level, pos);

        if (random.nextGaussian() < 0.1d)
        {
            getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
            return;
        }

        if (random.nextGaussian() < 0.05)
            addLowerLevel(state, level, pos);
    }

    private void addLowerLevel(BlockState state,
                               ServerLevel level,
                               BlockPos pos)
    {
        if (!hasFace(state, Direction.DOWN))
            return;
        BlockPos checkPos = pos.below();
        if (this.CONFIG.getSourcePos(level, checkPos) == null)
            return;
        BlockState checkState = level.getBlockState(checkPos);
        if (    !checkState.isAir() &&
                !checkState.is(BioBlockTags.NORPH_AVOID) &&
                !checkState.is(BioBlockTags.NORPH_SOURCE) &&
                !checkState.is(BioBlockTags.NORPH))
        {
            level.setBlockAndUpdate(checkPos, Registration.BlockReg.NORPH.get().defaultBlockState());
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
        }
    }

    private void grow(BlockState state,
                      ServerLevel level,
                      BlockPos pos)
    {
        Set<Direction> presentFaces = getPresentDirections(state);
        if (presentFaces.size() == 1)
            return;
        if (level.random.nextGaussian() > 0.1d * presentFaces.size())
            return;
        if (presentFaces.size() < 5)
        {
            AtomicReference<BlockState> stairsState = new AtomicReference<>(Registration.BlockReg.NORPH_STAIRS.get().defaultBlockState());
            stairsState.set(stairsState.get().setValue(StairBlock.HALF, presentFaces.contains(Direction.UP) ? Half.TOP : Half.BOTTOM));
            presentFaces.stream().filter(direction ->  direction.getAxis().isHorizontal()).findAny().
                    ifPresent(direction -> stairsState.set(stairsState.get().setValue(StairBlock.FACING, direction)));
            presentFaces.stream().filter(direction -> direction.getAxis().isHorizontal() && !direction.equals(stairsState.get().getValue(StairBlock.FACING))).
                    findAny().
                    ifPresentOrElse(direction -> stairsState.set(stairsState.get().setValue(StairBlock.SHAPE, StairBlock.getStairsShape(stairsState.get(), level, pos))),
                            () -> stairsState.set(stairsState.get().setValue(StairBlock.SHAPE, StairsShape.STRAIGHT)));
            level.setBlockAndUpdate(pos, stairsState.get());
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
        }
        else
            level.setBlockAndUpdate(pos, Registration.BlockReg.NORPH.get().defaultBlockState());
        level.blockUpdated(pos, Blocks.AIR);
        state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
    }

    private void addFace(BlockState state,
                         ServerLevel level,
                         BlockPos pos,
                         RandomSource random)
    {
        Set<Direction> possibleDirs = getPossibleDirections(state, level, pos);
        if (!possibleDirs.isEmpty())
        {
            possibleDirs.stream().
                    findAny().
                    ifPresent(direction ->
            {
                if (random.nextGaussian() > 0.3d)
                    return;
                level.setBlockAndUpdate(pos, state.setValue(MultifaceBlock.getFaceProperty(direction), true));
                level.blockUpdated(pos, Blocks.AIR);
                state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
            });
            return;
        }
        grow(state, level, pos);
    }

    private Set<Direction> getPossibleDirections(BlockState state, Level level, BlockPos pos)
    {
        //FIXME: check if this Multifaceblock.canAttachTo is used in right way
        return Arrays.stream(Direction.values()).filter(direction -> !state.getValue(MultifaceBlock.getFaceProperty(direction)) &&
                        MultifaceBlock.canAttachTo(level, direction, pos, state) &&
                        !level.getBlockState(pos.relative(direction)).
                                is(BioBlockTags.NORPH_AVOID)).
                collect(Collectors.toSet());
    }

    private Set<Direction> getPresentDirections(BlockState state)
    {
        return Arrays.stream(Direction.values()).filter(direction -> state.getValue(MultifaceBlock.getFaceProperty(direction))).
                collect(Collectors.toSet());
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventId, int eventParam)
    {
        if (world.isClientSide() && eventId == 255)
        {
            world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return true;
        }
        return super.triggerEvent(state, world, pos, eventId, eventParam);
    }

    @Override
    protected void onPlace(BlockState state,
                           Level level,
                           BlockPos pos,
                           BlockState oldState,
                           boolean movedByPiston)
    {
        if (level instanceof ServerLevel serverLevel)
            serverLevel.scheduleTick(pos, this, NorphSpreadConfig.getTicksDelay(level));
    }

    /**
     * WRENCHEABLE
     */

    @Override
    public InteractionResult onUsed(ItemStack stack, UseOnContext ctx)
    {
        return InteractionResult.PASS;
    }

    public static final class NorphSpreadConfig extends MultifaceSpreader.DefaultSpreaderConfig
    {
        public static final int HORIZONTAL_RADIUS = 10;
        public static final int VERTICAL_RADIUS = 6;

        private NorphSpreadConfig(NorphOverlay block)
        {
            super(block);
        }

        public static int getTicksDelay(Level level)
        {
            return 200 + level.getRandom().nextInt(50) - 25;
        }

        @Override
        public boolean isOtherBlockValidAsSource(BlockState otherBlock)
        {
            return otherBlock.is(BioBlockTags.NORPH_SOURCE);
        }

        @Override
        protected boolean stateCanBeReplaced(BlockGetter level,
                                             BlockPos pos,
                                             BlockPos spreadPos,
                                             Direction direction,
                                             BlockState state)
        {
            BlockState blockstate = level.getBlockState(spreadPos.relative(direction));
            if (!blockstate.is(Blocks.MOVING_PISTON))
            {
                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && !fluidState.is(FluidTags.WATER))
                    return false;

                if (state.is(BlockTags.FIRE))
                    return false;

                return state.canBeReplaced() || super.stateCanBeReplaced(level, pos, spreadPos, direction, state);
            }

            return super.stateCanBeReplaced(level, pos, spreadPos, direction, state);
        }

        @Override
        public boolean canSpreadInto(BlockGetter level, BlockPos pos, MultifaceSpreader.SpreadPos spreadPos)
        {
            BlockState state = level.getBlockState(spreadPos.pos());
            if (getSourcePos(level, spreadPos.pos()) != null)
                return stateCanBeReplaced(level, pos, spreadPos.pos(), spreadPos.face(), state) &&
                        this.block.isValidStateForPlacement(level, state, spreadPos.pos(), spreadPos.face()) &&
                        !level.getBlockState(pos.relative(spreadPos.face())).is(BioBlockTags.NORPH_AVOID);
            return false;
        }

        public @Nullable BlockPos getSourcePos(BlockGetter level, BlockPos pos)
        {
            return ZoneHelper.getPoses(pos, ZoneHelper.RadiusOptions.of(ZoneHelper.ZoneType.CIRCLE, HORIZONTAL_RADIUS, VERTICAL_RADIUS)).
                    filter(checkPos -> this.isOtherBlockValidAsSource(level.getBlockState(checkPos))).findAny().orElse(null);
        }

        @Override
        public boolean placeBlock(LevelAccessor level, MultifaceSpreader.SpreadPos pos, BlockState state, boolean markForPostprocessing)
        {
            return super.placeBlock(level, pos, state, markForPostprocessing);
        }
    }
}
