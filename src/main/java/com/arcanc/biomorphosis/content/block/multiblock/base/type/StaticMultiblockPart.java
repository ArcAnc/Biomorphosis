/**
 * @author ArcAnc
 * Created at: 10.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base.type;

import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.block.multiblock.base.BioMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class StaticMultiblockPart extends BioMultiblockPart implements ServerTickableBE
{
    private static final int MORPH_TIME_TICKS = 20 * 15;
    private static final AABB INGREDIENTS_ZONE = new AABB(1/16f, 1/16f, 1/16f, 15/16f, 15/16f, 15/16f);

    private int morphProgress = 0;
    private final AABB checkZone;
    private List<Pair<BlockPos, BlockState>> morphSequence;
    private float morphDelay;
    private float accumulatedTicks;

    public StaticMultiblockPart(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
        this.checkZone = INGREDIENTS_ZONE.move(pos);
    }

    @Override
    public void tickServer()
    {
        if (this.level == null)
            return;

        if (!isMultiblockPart())
            tryFormMultiblock();
        else
        {
            if (getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.MORPHING)
                multiblockMorphing();
            else if (getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED)
            {
                if (!isMultiblockStillValid())
                    disassembleMultiblock();
                else
                    multiblockServerTick();
            }
        }
    }

    public void multiblockMorphing()
    {
        if (!isMaster())
            return;
        if (!isStillValidDuringMorphing())
        {
            disassembleMultiblock();
            return;
        }
        if (this.morphProgress >= this.morphSequence.size())
        {
            onMorphComplete();
            return;
        }

        this.accumulatedTicks +=1f;
        while (this.accumulatedTicks >= this.morphDelay)
        {
            placeNextMorphBlock();
            this.accumulatedTicks -= this.morphDelay;
        }
    }

    private void onMorphComplete()
    {
        this.morphSequence.forEach(pair ->
        {
            BlockPos offsetPos = pair.getFirst().offset(getBlockPos());
            BlockState placedState = pair.getSecond();
            if (placedState.hasProperty(MultiblockPartBlock.STATE))
                placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.FORMED);
            this.level.setBlockAndUpdate(offsetPos, placedState);
            BlockHelper.castTileEntity(this.level, offsetPos, StaticMultiblockPart.class).
                    ifPresent(part -> part.markAsPartOfMultiblock(getBlockPos()));
        });
        markAsPartOfMultiblock(getBlockPos());

        this.morphProgress = 0;
        this.morphSequence = null;
        this.morphDelay = 0;
        this.accumulatedTicks = 0;
    }

    private void placeNextMorphBlock()
    {
        Pair<BlockPos, BlockState> pair = this.morphSequence.get(this.morphProgress);
        if (!consumeRequiredItems(gatherInputEntities(), pair.getSecond()))
        {
            disassembleMultiblock();
            return;
        }
        BlockPos offsetPos = getBlockPos().offset(pair.getFirst());
        BlockState placedState = pair.getSecond();
        if (placedState.hasProperty(MultiblockPartBlock.STATE))
            placedState = placedState.setValue(MultiblockPartBlock.STATE, MultiblockState.MORPHING);
        this.level.setBlockAndUpdate(offsetPos, placedState);
        BlockHelper.castTileEntity(this.level, offsetPos, StaticMultiblockPart.class).
                ifPresent(part -> part.startMorphing(getBlockPos()));
        this.morphProgress++;
        this.markDirty();
    }

    private boolean consumeRequiredItems(@NotNull List<ItemEntity> entities, @NotNull BlockState required)
    {
        ItemStack requiredStack = new ItemStack(required.getBlock());

        for (ItemEntity entity : entities)
        {
            ItemStack stack = entity.getItem();
            if (stack.isEmpty())
                continue;

            if (ItemStack.isSameItemSameComponents(stack, requiredStack))
            {
                stack.shrink(1);
                if (stack.isEmpty())
                    entity.discard();
                else
                    entity.setItem(stack);
                return true;
            }
        }
        return false;
    }

    protected abstract void multiblockServerTick();

    protected boolean isStillValidDuringMorphing()
    {
        return isConnectedToNorph();
    }

    @Override
    protected void tryFormMultiblock()
    {
        if (!isConnectedToNorph())
            return;

        List<ItemEntity> entities = gatherInputEntities();
        List<ItemStack> stacks = gatherInputStacks(entities);

        this.definition = this.level.registryAccess().
                lookup(Registration.MultiblockReg.DEFINITION_KEY).
                flatMap(registry -> registry.
                        filterElements(definition -> definition.type() == MultiblockType.STATIC &&
                                hasAllStacks(definition.getStructure(this.level, getBlockPos()).getStackedStructure(), stacks)).
                        listElements().
                        findFirst().
                        map(Holder :: value)).
                orElse(null);

        if (!(this.definition instanceof StaticMultiblockDefinition staticDefinition) || !canStartMorphing())
            return;

        BlockStateMap map = staticDefinition.getStructure();

        this.morphProgress = 0;
        this.morphSequence = map.getStates().
                entrySet().
                stream().
                filter(entry -> !entry.getKey().equals(BlockPos.ZERO)).
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
        startMorphing(getBlockPos());
        markDirty();
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

    private @NotNull List<ItemStack> gatherInputStacks(@NotNull List<ItemEntity> entities)
    {
        return entities.stream().map(ItemEntity::getItem).collect(Collectors.toList());
    }

    private @NotNull List<ItemEntity> gatherInputEntities()
    {
        return this.level.getEntitiesOfClass(ItemEntity.class, this.checkZone);
    }

    private boolean canStartMorphing()
    {
        BlockState state = getBlockState();
        if (state.hasProperty(MultiblockPartBlock.STATE))
            return state.getValue(MultiblockPartBlock.STATE) == MultiblockState.DISASSEMBLED;
        return false;
    }

    @Override
    protected boolean isMultiblockStillValid()
    {
        if (this.level == null)
            return true;
        if (!isMaster())
            return true;
        List<Pair<BlockPos, BlockState>> structure = this.definition.getStructure(getLevel(), getBlockPos()).getStates().
                entrySet().
                stream().
                filter(entry -> !entry.getKey().equals(BlockPos.ZERO)).
                map(entry ->
                {
                    Direction dir = DirectionHelper.getFace(getBlockState());
                    BlockPos rotatedPos = DirectionHelper.rotatePosition(entry.getKey(), dir);
                    return Pair.of(rotatedPos, entry.getValue().rotate(DirectionHelper.rotationFromNorth(dir)));
                }).
                toList();

        if (structure.isEmpty())
            return false;

        for (Pair<BlockPos, BlockState> pair : structure)
        {
            BlockPos realPos = pair.getFirst().offset(getBlockPos());
            if (!this.level.isLoaded(realPos))
                return false;
            BlockState toCheck = this.level.getBlockState(realPos);
            if (!BlockHelper.statesEquivalent(pair.getSecond(), toCheck))
                return false;
        }
        return true;
    }

    @Override
    protected void onDisassemble()
    {
        if (this.level == null)
            return;
        if (!isMaster())
            return;

        List<Pair<BlockPos, BlockState>> structure = this.definition.getStructure(getLevel(), getBlockPos()).getStates().
                entrySet().
                stream().
                filter(entry -> !entry.getKey().equals(BlockPos.ZERO)).
                map(entry ->
                {
                    Direction dir = DirectionHelper.getFace(getBlockState());
                    BlockPos rotatedPos = DirectionHelper.rotatePosition(entry.getKey(), dir);
                    return Pair.of(rotatedPos, entry.getValue().rotate(DirectionHelper.rotationFromNorth(dir)));
                }).
                toList();

        if (structure.isEmpty())
            return;

        for (Pair<BlockPos, BlockState> pair : structure)
        {
            BlockPos realPos = pair.getFirst().offset(getBlockPos());
            if (!this.level.isLoaded(realPos))
                continue;
            BlockState toCheck = this.level.getBlockState(realPos);
            if (!BlockHelper.statesEquivalent(pair.getSecond(), toCheck))
                continue;
            this.level.destroyBlock(realPos, true);
        }

        this.level.destroyBlock(getBlockPos(), true);
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
    }
}
