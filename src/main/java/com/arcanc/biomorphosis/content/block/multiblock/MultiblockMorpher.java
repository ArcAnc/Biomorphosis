/**
 * @author ArcAnc
 * Created at: 14.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;


import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.definition.BlockStateMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.content.block.multiblock.definition.StaticMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.stream.Collectors;

public class MultiblockMorpher extends StaticMultiblockPart implements GeoBlockEntity
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final RawAnimation MORPH = RawAnimation.begin().thenPlay("morphing");
	private final RawAnimation HOLD = RawAnimation.begin().thenPlayAndHold("hold");

	private static final int PREPARATION_TIME_TICKS = 20 * 10;
	private static final int MORPH_TIME_TICKS = 20 * 15;
	private static final AABB INGREDIENTS_ZONE = new AABB(1/16f, 1/16f, 1/16f, 15/16f, 15/16f, 15/16f);

	private int morphProgress = 0;
	private final AABB checkZone;
	private List<Pair<BlockPos, BlockState>> morphSequence;
	private float morphDelay;
	private float accumulatedTicks;

	private float preparationTimer;

	public MultiblockMorpher(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_MULTIBLOCK_MORPHER.get(), pos, blockState);

		this.checkZone = INGREDIENTS_ZONE.move(pos);
	}

	@Override
	public void tickServer()
	{
		Level level = this.level;
		if (level == null)
			return;

		if (!isMultiblockPart())
			tryFormMultiblock(level);
		else
		{
			if (getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING)
				if (!isPreparationPhase())
					multiblockMorphing(level);
				else
					preparationPhase();
		}
	}

	private void preparationPhase()
	{
		this.preparationTimer++;
		this.markDirty();
	}

	private void multiblockMorphing(Level level)
	{
		if (!isMaster())
			return;
		if (!isStillValidDuringMorphing(level))
		{
			disassembleMultiblock();
			return;
		}
		if (this.morphProgress >= this.morphSequence.size())
		{
			onMorphComplete(level);
			return;
		}

		this.accumulatedTicks +=1f;
		while (this.accumulatedTicks >= this.morphDelay)
		{
			placeNextMorphBlock(level);
			this.accumulatedTicks -= this.morphDelay;
		}
	}


	private boolean isStillValidDuringMorphing(Level level)
	{
		return isConnectedToNorph(level);
	}

	private void onMorphComplete(Level level)
	{
		this.setRemoved();

		this.morphSequence.stream().
				filter(pair -> !pair.getFirst().equals(BlockPos.ZERO)).
				forEach(pair ->
				{
					BlockPos offsetPos = pair.getFirst().offset(getBlockPos());
					BlockState placedState = pair.getSecond();
					if (placedState.hasProperty(MultiblockPartBlock.STATE))
						placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.FORMED);
					level.setBlockAndUpdate(offsetPos, placedState);
					BlockHelper.castTileEntity(level, offsetPos, StaticMultiblockPart.class).
						ifPresent(part -> part.markAsPartOfMultiblock(getBlockPos()));
		});

		this.morphSequence.stream().
				filter(pair -> pair.getFirst().equals(BlockPos.ZERO)).
				findFirst().
				ifPresent(pair ->
				{
					BlockPos toPlacePos = getBlockPos();
					BlockState placedState = pair.getSecond();
					if (placedState.hasProperty(MultiblockPartBlock.STATE))
						placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.FORMED);
					level.setBlockAndUpdate(toPlacePos, placedState);
					BlockHelper.castTileEntity(level, toPlacePos, StaticMultiblockPart.class).
							ifPresent(part ->
							{
								part.setDefinition(this.definition);
								part.markAsPartOfMultiblock(getBlockPos());
							});
				});

		this.morphProgress = 0;
		this.morphSequence = null;
		this.morphDelay = 0;
		this.accumulatedTicks = 0;
	}

	private void placeNextMorphBlock(Level level)
	{
		Pair<BlockPos, BlockState> pair = this.morphSequence.get(this.morphProgress);
		if(pair.getFirst().equals(BlockPos.ZERO))
		{
			this.morphProgress++;
			this.markDirty();
			return;
		}
		BlockPos offsetPos = getBlockPos().offset(pair.getFirst());
		BlockState placedState = pair.getSecond();
		if (placedState.hasProperty(MultiblockPartBlock.STATE))
			placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.MORPHING);
		level.setBlockAndUpdate(offsetPos, placedState);
		BlockHelper.castTileEntity(level, offsetPos, StaticMultiblockPart.class).
				ifPresent(part -> part.startMorphing(getBlockPos()));
		this.morphProgress++;
		this.markDirty();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void tryFormMultiblock(Level level)
	{
		if (!isConnectedToNorph(level))
			return;

		List<ItemEntity> entities = gatherInputEntities(level);
		List<ItemStack> stacks = gatherInputStacks(entities);

		this.definition = level.registryAccess().
				lookup(Registration.MultiblockReg.DEFINITION_KEY).
				flatMap(registry -> registry.
					filterElements(definition -> definition.type() == MultiblockType.STATIC &&
						hasAllStacks(definition.getStructure(level, getBlockPos()).getStackedStructure(), stacks)).
					listElements().
					findFirst().
					map(Holder:: value)).
				orElse(null);

		if (!(this.definition instanceof StaticMultiblockDefinition staticDefinition) || !canStartMorphing())
			return;

		BlockStateMap map = staticDefinition.getStructure();

		this.morphProgress = 0;
		this.preparationTimer = 0;
		this.morphSequence = map.getStates().
				entrySet().
				stream().
				map(entry ->
				{
					Direction dir = DirectionHelper.getFace(getBlockState());
					BlockPos rotatedPos = DirectionHelper.rotatePosition(entry.getKey(), dir);
					return Pair.of(rotatedPos, entry.getValue().rotate(DirectionHelper.rotationFromNorth(dir)));
				}).
				sorted((o1, o2) -> o1.getFirst().distManhattan(o2.getFirst())).
				collect(Collectors.toList());
		if (this.morphSequence.isEmpty())
			throw new RuntimeException("Empty morph sequence, but not empty definition");
		this.morphDelay = (float)MORPH_TIME_TICKS / this.morphSequence.size();
		this.accumulatedTicks = 0f;
		consumeRequiredResources(entities, map.getStackedStructure());
		startMorphing(getBlockPos());
	}

	private void consumeRequiredResources(@NotNull List<ItemEntity> entities, @NotNull List<ItemStack> toRemove)
	{
		for (ItemStack removeStack : toRemove)
		{
			int remaining = removeStack.getCount();

			Iterator<ItemEntity> iterator = entities.iterator();
			while (iterator.hasNext() && remaining > 0)
			{
				ItemEntity entity = iterator.next();
				ItemStack entityStack = entity.getItem();

				if (ItemStack.isSameItemSameComponents(removeStack, entityStack))
				{
					int count = entityStack.getCount();

					if (count <= remaining)
					{
						remaining -= count;
						iterator.remove();
						entity.discard();
					}
					else
					{
						entityStack.shrink(remaining);
						remaining = 0;
					}
				}
			}

			if (remaining > 0)
				throw new IllegalStateException("ItemStack to remove not fully matched in ItemEntities!");
		}
	}

	private @NotNull List<ItemEntity> gatherInputEntities(@NotNull Level level)
	{
		return level.getEntitiesOfClass(ItemEntity.class, this.checkZone);
	}

	private @NotNull List<ItemStack> gatherInputStacks(@NotNull List<ItemEntity> entities)
	{
		return entities.stream().map(ItemEntity::getItem).collect(Collectors.toList());
	}

	private boolean canStartMorphing()
	{
		BlockState state = getBlockState();
		if (state.hasProperty(MultiblockPartBlock.STATE))
			return state.getValue(MultiblockPartBlock.STATE) == MultiblockState.DISASSEMBLED;
		return false;
	}

	private boolean hasAllStacks(@NotNull List<ItemStack> required, @NotNull List<ItemStack> available)
	{
		if (available.isEmpty() || required.isEmpty())
			return false;

		Map<Item, Integer> requiredMap = new HashMap<>();
		Map<Item, Integer> availableMap = new HashMap<>();

		required.forEach(stack ->
		{
			if (!stack.isEmpty())
			{
				Item key = stack.getItem();
				requiredMap.merge(key, stack.getCount(), Integer::sum);
			}
		});

		available.forEach(stack ->
		{
			if (!stack.isEmpty())
			{
				Item key = stack.getItem();
				availableMap.merge(key, stack.getCount(), Integer::sum);
			}

		});

		for (Map.Entry<Item, Integer> entry : requiredMap.entrySet())
		{
			int have = availableMap.getOrDefault(entry.getKey(), 0);
			if (have < entry.getValue())
				return false;
		}

		return true;
	}

	public boolean isPreparationPhase()
	{
		return this.preparationTimer <= PREPARATION_TIME_TICKS;
	}

	public float getPreparationTimer()
	{
		return preparationTimer;
	}

	public int getMorphProgress()
	{
		return morphProgress;
	}

	@Override
	public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.readCustomTag(tag, registries, descrPacket);
		if (!tag.contains("morph_sequence"))
			return;
		this.morphProgress = tag.getInt("morph_progress");
		this.morphDelay = tag.getFloat("morph_delay");
		this.accumulatedTicks = tag.getFloat("accumulated_ticks");

		List<Pair<BlockPos, BlockState>> morphSequence = new ArrayList<>();

		ListTag list = tag.getList("morph_sequence", Tag.TAG_COMPOUND);
		for (int q = 0; q < list.size(); q++)
		{
			CompoundTag pairTag = list.getCompound(q);
			BlockPos pos = TagHelper.readBlockPos(pairTag, "pos");
			BlockState state = BlockState.CODEC.parse(NbtOps.INSTANCE, pairTag.get("state")).
					resultOrPartial(s -> Database.LOGGER.warn("Can't read BlockState: {}", s)).
					orElseThrow();
			morphSequence.add(new Pair<>(pos, state));
		}

		this.morphSequence = morphSequence;

		this.preparationTimer = tag.getFloat("preparation");
	}

	@Override
	public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.writeCustomTag(tag, registries, descrPacket);
		if (this.morphSequence == null)
			return;

		tag.putInt("morph_progress", this.morphProgress);
		tag.putFloat("morph_delay", this.morphDelay);
		tag.putFloat("accumulated_ticks", this.accumulatedTicks);
		ListTag list = new ListTag();
		this.morphSequence.forEach(pair ->
		{
			CompoundTag pairTag = new CompoundTag();
			TagHelper.writeBlockPos(pair.getFirst(), pairTag,"pos");
			BlockState.CODEC.encodeStart(NbtOps.INSTANCE, pair.getSecond()).resultOrPartial(s -> Database.LOGGER.warn("Can't write BlockState into nbt: {}", s)).
					ifPresent(bsTag -> pairTag.put("state", bsTag));
			list.add(pairTag);
		});
		tag.put("morph_sequence", list);

		tag.putFloat("preparation", this.preparationTimer);
	}


	@Override
	protected void multiblockServerTick()
	{

	}

	@Override
	protected void firstTick()
	{

	}

	@Override
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "animation", 0, state ->
		{
			MultiblockMorpher morpher = state.getAnimatable();
			if (morpher.getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING &&
				morpher.isPreparationPhase())
				return state.setAndContinue(MORPH);
			else
				return state.setAndContinue(HOLD);
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}
}
