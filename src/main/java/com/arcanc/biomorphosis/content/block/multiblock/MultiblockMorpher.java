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
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.content.block.multiblock.definition.PartsMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.StaticMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackSidedStorage;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiblockMorpher extends StaticMultiblockPart implements PAnimatable<MultiblockMorpher>
{
	private final PAnimationManager<MultiblockMorpher> manager = PLibHelper.createManager(this);
	private final PRawAnimation IDLE = PRawAnimation.begin().thenHold("idle").build();
	private final PRawAnimation GROW = PRawAnimation.begin().thenPlay("grow").thenLoop("pulse").build();
	//private final PRawAnimation PULSE = PRawAnimation.begin().thenLoop("pulse").build();
	
	private static final int PREPARATION_TIME_TICKS = 20 * 10;
	private static final int MORPH_TIME_TICKS = 20 * 15;
	private static final int INPUT_SLOTS = 12;
	private static final AABB INGREDIENTS_ZONE = new AABB(1 / 16f, 1 / 16f, 1 / 16f, 15 / 16f, 15 / 16f, 15 / 16f);
	
	private int morphProgress = 0;
	private final ItemStackSidedStorage itemHandler;
	private final AABB checkZone;
	private @Nullable MorphSequence morphSequence;
	private float morphDelay;
	private float accumulatedTicks;
	
	private float preparationTimer;
	
	public MultiblockMorpher(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_MULTIBLOCK_MORPHER.get(), pos, blockState);
		
		this.checkZone = INGREDIENTS_ZONE.move(pos);
		this.itemHandler = new ItemStackSidedStorage();
		for (int q = 0; q < INPUT_SLOTS; q++)
			this.itemHandler.addHolder(ItemStackHolder.newBuilder().
					setCallback(holder -> markDirty()).
					setCapacity(64).
					build(), BasicSidedStorage.FaceMode.ALL);
	}
	
	@Override
	public void tickServer()
	{
		Level level = this.level;
		if (level == null)
			return;
		
		if (!isMultiblockPart())
			absorbInputEntities(level);
		else
		{
			if (getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING)
				if (! isPreparationPhase())
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
		if (this.morphProgress >= this.morphSequence.stateMap().size())
		{
			onMorphComplete(level);
			return;
		}
		
		this.accumulatedTicks += 1f;
		while (this.accumulatedTicks >= this.morphDelay)
		{
			placeNextMorphBlock(level);
			this.accumulatedTicks -= this.morphDelay;
		}
		markDirty();
	}
	
	
	private boolean isStillValidDuringMorphing(Level level)
	{
		return isConnectedToNorph(level);
	}
	
	private void onMorphComplete(Level level)
	{
		this.setRemoved();
		
		this.morphSequence.stateMap().stream().filter(pair -> ! pair.getFirst().equals(BlockPos.ZERO)).forEach(pair ->
		{
			BlockPos offsetPos = pair.getFirst().offset(getBlockPos());
			BlockState placedState = this.morphSequence.placedBlockState();
			if (placedState.hasProperty(MultiblockPartBlock.STATE))
				placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.FORMED);
			if (placedState.hasProperty(BlockHelper.BlockProperties.HORIZONTAL_FACING))
				placedState = placedState.setValue(BlockHelper.BlockProperties.HORIZONTAL_FACING, this.getBlockState().getValue(BlockHelper.BlockProperties.HORIZONTAL_FACING));
			level.setBlockAndUpdate(offsetPos, placedState);
			BlockHelper.castTileEntity(level, offsetPos, StaticMultiblockPart.class).ifPresent(part -> part.markAsPartOfMultiblock(getBlockPos()));
		});
		
		this.morphSequence.stateMap().stream().filter(pair -> pair.getFirst().equals(BlockPos.ZERO)).
				findFirst().
				ifPresent(entry ->
		{
			BlockPos toPlacePos = getBlockPos();
			BlockState placedState = this.morphSequence.placedBlockState();
			if (placedState.hasProperty(MultiblockPartBlock.STATE))
				placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.FORMED);
			if (placedState.hasProperty(BlockHelper.BlockProperties.HORIZONTAL_FACING))
				placedState = placedState.setValue(BlockHelper.BlockProperties.HORIZONTAL_FACING, this.getBlockState().getValue(BlockHelper.BlockProperties.HORIZONTAL_FACING));
			level.setBlockAndUpdate(toPlacePos, placedState);
			BlockHelper.castTileEntity(level, toPlacePos, StaticMultiblockPart.class).ifPresent(part ->
			{
				part.setDefinition(this.definition);
				part.markAsPartOfMultiblock(toPlacePos);
			});
		});
		
		if (level instanceof ServerLevel serverLevel)
		{    PartsMap map = this.definition.getStructure(level, getBlockPos());
			
			List<EdgePart> edgeParts = collectEdgeParts(map);
			
			for (EdgePart edge : edgeParts)
			{
				BlockPos worldPos = getBlockPos().offset(edge.pos());
				
				Vec3 center = Vec3.atCenterOf(worldPos);
				
				for (Vec3 normal : edge.normals())
				{
					for (int i = 0; i < 12; i++)
					{
						double spread = 0.25;
						
						Vec3 random = new Vec3(
								(level.random.nextDouble() - 0.5) * spread,
								(level.random.nextDouble() - 0.5) * spread,
								(level.random.nextDouble() - 0.5) * spread
						);
						
						Vec3 velocity = normal.scale(
								0.15 + level.random.nextDouble() * 0.1
						).add(random);
						
						serverLevel.sendParticles(
								new BlockParticleOption(
										ParticleTypes.BLOCK,
										Blocks.WATER.defaultBlockState()
								),
								center.x + normal.x * 0.45,
								center.y + normal.y * 0.45,
								center.z + normal.z * 0.45,
								1,
								velocity.x,
								velocity.y,
								velocity.z,
								0.1f
						);
					}
				}
			}
			
			serverLevel.playSound(null, getBlockPos(), Registration.SoundReg.BLOCK_MORPH_COMPLETE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		
		this.morphProgress = 0;
		this.morphSequence = null;
		this.morphDelay = 0;
		this.accumulatedTicks = 0;
	}
	
	private void placeNextMorphBlock(Level level)
	{
		Pair<BlockPos, PartsMap.MultiblockPart> pair = this.morphSequence.stateMap().get(this.morphProgress);
		if (pair.getFirst().equals(BlockPos.ZERO))
		{
			this.morphProgress++;
			return;
		}
		BlockPos offsetPos = getBlockPos().offset(pair.getFirst());
		BlockState placedState = this.morphSequence.placedBlockState();
		if (placedState.hasProperty(MultiblockPartBlock.STATE))
			placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.MORPHING);
		if (placedState.hasProperty(BlockHelper.BlockProperties.HORIZONTAL_FACING))
			placedState = placedState.setValue(BlockHelper.BlockProperties.HORIZONTAL_FACING, this.getBlockState().getValue(BlockHelper.BlockProperties.HORIZONTAL_FACING));
		level.setBlockAndUpdate(offsetPos, placedState);
		BlockHelper.castTileEntity(level, offsetPos, StaticMultiblockPart.class).
				ifPresent(part -> part.startMorphing(getBlockPos()));
		this.morphProgress++;
	}
	
	@Override
	protected void tryFormMultiblock(Level level)
	{
		if (! isConnectedToNorph(level))
			return;
		
		List<ItemStack> stacks = gatherInputStacks();
		
		this.definition = level.registryAccess().lookup(Registration.MultiblockReg.DEFINITION_KEY).
				flatMap(registry -> registry.
						filterElements(definition -> definition.type() ==
							MultiblockType.STATIC &&
							hasAllStacks(definition.getStructure(level, getBlockPos()).getStructure(), stacks)).
						listElements().
						findFirst().
						map(Holder :: value)).
						orElse(null);
		
		if (! (this.definition instanceof StaticMultiblockDefinition staticDefinition) || ! canStartMorphing())
			return;
		
		PartsMap map = staticDefinition.getStructure();
		
		this.morphProgress = 0;
		this.preparationTimer = 0;
		this.morphSequence = new MorphSequence(map.getParts().
				entrySet().
				stream().
				sorted((o1, o2) ->
						o1.getKey().
						distManhattan(o2.getKey())).
				map(entry ->
						Pair.of(entry.getKey(), entry.getValue())).
				collect(Collectors.toList()), map.getPlacedBlock());
		if (this.morphSequence.stateMap().isEmpty())
			throw new RuntimeException("Empty morph sequence, but not empty definition");
		this.morphDelay = (float) MORPH_TIME_TICKS / this.morphSequence.stateMap().size();
		this.accumulatedTicks = 0f;
		consumeRequiredResources(map.getStructure());
		dropRemainingInput(level);
		startMorphing(getBlockPos());
	}
	
	public boolean tryStartMorphing()
	{
		Level level = getLevel();
		if (level == null || isMultiblockPart() || !canStartMorphing())
			return false;
		
		absorbInputEntities(level);
		tryFormMultiblock(level);
		return isMultiblockPart() &&
				getBlockState().hasProperty(MultiblockPartBlock.STATE) &&
				getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING;
	}
	
	private void absorbInputEntities(Level level)
	{
		if (!canStartMorphing())
			return;
		
		for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, this.checkZone))
		{
			ItemStack remaining = this.itemHandler.insert(null, entity.getItem(), false);
			if (remaining.isEmpty())
				entity.discard();
			else
				entity.setItem(remaining);
		}
	}
	
	private void consumeRequiredResources(List<IngredientWithSize> toRemove)
	{
		for (IngredientWithSize removeStack : toRemove)
		{
			int remaining = removeStack.amount();
			
			for (int q = 0; q < this.itemHandler.getSlots() && remaining > 0; q++)
			{
				ItemStack stack = this.itemHandler.getStackInSlot(q);
				
				if (removeStack.test(stack))
				{
					int toExtract = Math.min(remaining, stack.getCount());
					remaining -= this.itemHandler.extractItem(q, toExtract, false).getCount();
				}
			}
			
			if (remaining > 0)
				throw new IllegalStateException("ItemStack to remove not fully matched in ItemEntities!");
		}
	}
	
	private void dropRemainingInput(Level level)
	{
		for (int q = 0; q < this.itemHandler.getSlots(); q++)
		{
			ItemStack extracted = this.itemHandler.extractItem(q, Integer.MAX_VALUE, false);
			if (!extracted.isEmpty())
				Containers.dropItemStack(level,
						getBlockPos().getX() + 0.5d,
						getBlockPos().getY() + 0.5d,
						getBlockPos().getZ() + 0.5d,
						extracted);
		}
	}
	
	private List<ItemStack> gatherInputStacks()
	{
		List<ItemStack> stacks = new ArrayList<>();
		for (int q = 0; q < this.itemHandler.getSlots(); q++)
		{
			ItemStack stack = this.itemHandler.getStackInSlot(q);
			if (!stack.isEmpty())
				stacks.add(stack);
		}
		return stacks;
	}
	
	private boolean canStartMorphing()
	{
		BlockState state = getBlockState();
		if (state.hasProperty(MultiblockPartBlock.STATE))
			return state.getValue(MultiblockPartBlock.STATE) == MultiblockState.DISASSEMBLED;
		return false;
	}
	
	public ItemStack insertInput(ItemStack stack, boolean simulate)
	{
		if (!canStartMorphing() || !isValidMorphIngredient(stack))
			return stack;
		
		ItemStack toInsert = stack.copyWithCount(1);
		ItemStack remainder = this.itemHandler.insert(null, toInsert, simulate);
		if (!remainder.isEmpty())
			return stack;
		
		ItemStack result = stack.copy();
		result.shrink(1);
		return result;
	}
	
	public ItemStack extractInput()
	{
		return extractInput(false);
	}
	
	public ItemStack peekInput()
	{
		return extractInput(true);
	}
	
	private ItemStack extractInput(boolean simulate)
	{
		if (!canStartMorphing())
			return ItemStack.EMPTY;
		
		for (int q = this.itemHandler.getSlots() - 1; q >= 0; q--)
		{
			ItemStack extracted = this.itemHandler.extractItem(q, Integer.MAX_VALUE, simulate);
			if (!extracted.isEmpty())
				return extracted;
		}
		return ItemStack.EMPTY;
	}
	
	public ItemStackSidedStorage getInputItemHandler()
	{
		return this.itemHandler;
	}
	
	private boolean isValidMorphIngredient(ItemStack stack)
	{
		Level level = this.level;
		if (level == null || stack.isEmpty())
			return false;
		
		return level.registryAccess().lookup(Registration.MultiblockReg.DEFINITION_KEY).
				map(registry -> registry.
						listElements().
						anyMatch(definition ->
								definition.value().type() == MultiblockType.STATIC &&
								definition.value().getStructure(level, getBlockPos()).
										getStructure().
										stream().
										anyMatch(ingredient -> ingredient.test(stack)))).
				orElse(false);
	}
	
	private boolean hasAllStacks(List<IngredientWithSize> required, List<ItemStack> available)
	{
		if (available.isEmpty() || required.isEmpty())
			return false;
		for (IngredientWithSize ingredient : required)
		{
			int have = 0;
			for (ItemStack stack : available)
			{
				if (ingredient.test(stack))
					have += stack.getCount();
			}
			if (have < ingredient.amount())
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
		return this.preparationTimer;
	}
	
	public float getMorphProgress()
	{
		return this.morphProgress;
	}
	
	public float getAccumulatedTicks()
	{
		return this.accumulatedTicks;
	}
	
	public float getMorphDelay()
	{
		return this.morphDelay;
	}
	
	@Override
	public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
	{
		super.readCustomTag(tag, registries, descrPacket);
		if (tag.contains(Database.Capabilities.Items.HANDLER))
			this.itemHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Items.HANDLER));
		if (! tag.contains("morph_sequence"))
			return;
		this.morphProgress = tag.getInt("morph_progress");
		this.morphDelay = tag.getFloat("morph_delay");
		this.accumulatedTicks = tag.getFloat("accumulated_ticks");
		
		List<Pair<BlockPos, PartsMap.MultiblockPart>> morphSequence = new ArrayList<>();
		
		ListTag list = tag.getList("morph_sequence", Tag.TAG_COMPOUND);
		for (int q = 0; q < list.size(); q++)
		{
			CompoundTag pairTag = list.getCompound(q);
			BlockPos pos = TagHelper.readBlockPos(pairTag, "pos");
			RegistryOps<Tag> registryOps = registries.createSerializationContext(NbtOps.INSTANCE);
			PartsMap.MultiblockPart part = PartsMap.MultiblockPart.CODEC.parse(registryOps, pairTag.get("part")).resultOrPartial(s -> Database.LOGGER.warn("Can't read Multiblock Part Data: {}", s)).orElseThrow();
			morphSequence.add(Pair.of(pos, part));
		}
		
		BlockState placedBlock = BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("placed_state")).resultOrPartial(s -> Database.LOGGER.warn("Can't read BlockState: {}", s)).orElseThrow();
		
		this.morphSequence = new MorphSequence(morphSequence, placedBlock);
		
		this.preparationTimer = tag.getFloat("preparation");
	}
	
	@Override
	public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
	{
		super.writeCustomTag(tag, registries, descrPacket);
		tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));
		if (this.morphSequence == null)
			return;
		
		tag.putInt("morph_progress", this.morphProgress);
		tag.putFloat("morph_delay", this.morphDelay);
		tag.putFloat("accumulated_ticks", this.accumulatedTicks);
		ListTag list = new ListTag();
		this.morphSequence.stateMap().forEach(pair ->
		{
			CompoundTag pairTag = new CompoundTag();
			TagHelper.writeBlockPos(pair.getFirst(), pairTag, "pos");
			RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);
			PartsMap.MultiblockPart.CODEC.encodeStart(registryops, pair.getSecond()).resultOrPartial(s -> Database.LOGGER.warn("Can't write MultiblockPart Data into nbt: {}", s)).ifPresent(bsTag -> pairTag.put("part", bsTag));
			list.add(pairTag);
		});
		tag.put("morph_sequence", list);
		
		BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.morphSequence.placedBlockState()).resultOrPartial(s -> Database.LOGGER.warn("Can't write BlockState into nbt: {}", s)).ifPresent(bsTag -> tag.put("placed_state", bsTag));
		
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
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<MultiblockMorpher> registrar)
	{
		registrar.add(() -> state ->
		{
			MultiblockMorpher morpher = state.animatable();
			if (morpher.getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING)
				state.controller().play(GROW);
			else
				state.controller().play(IDLE);
			return ControllerState.PLAY;
		});
	}
	
	@Override
	public PAnimationManager<MultiblockMorpher> getAnimationManager(AnimManagerKey key)
	{
		return this.manager;
	}
	
	private List<EdgePart> collectEdgeParts(PartsMap map)
	{
		Set<BlockPos> positions = map.getParts().keySet();
		
		List<EdgePart> result = new ArrayList<>();
		
		for (BlockPos pos : positions)
		{
			List<Vec3> normals = new ArrayList<>();
			
			for (Direction dir : Direction.values())
			{
				BlockPos neighbour = pos.relative(dir);
				
				if (!positions.contains(neighbour))
					normals.add(Vec3.atCenterOf(dir.getNormal()));
			}
			
			if (!normals.isEmpty())
				result.add(new EdgePart(pos, normals));
		}
		
		return result;
	}
	
	private record MorphSequence(List<Pair<BlockPos, PartsMap.MultiblockPart>> stateMap, BlockState placedBlockState)
	{
	}
	
	private record EdgePart(BlockPos pos, List<Vec3> normals)
	{
	}
}
