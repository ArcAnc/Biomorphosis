/**
 * @author ArcAnc
 * Created at: 31.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.LureCampfireBlock;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.content.entity.QueenGuard;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class LureCampfireBE extends BioBaseBlockEntity implements ServerTickableBE, PAnimatable<LureCampfireBE>
{
    private static final PRawAnimation SHAFT_ROTATION = PRawAnimation.begin().thenLoop("shaft_rotation").build();
    private static final PRawAnimation FIRE_ENABLE = PRawAnimation.begin().thenHold("fire_enable").build();
    private static final PRawAnimation FIRE_DISABLE = PRawAnimation.begin().thenHold("fire_disable").build();

    private static final int BASE_SUMMON_TIME = 2 * 60 * 20;
    private static final int REDUCTION_PER_MEAT = 20 * 15;
    private int timer;

    private final PAnimationManager<LureCampfireBE> manager = PLibHelper.createManager(this);
    private final LureCampfireStackHandler itemHandler = new LureCampfireStackHandler(5);

    public LureCampfireBE(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_LURE_CAMPFIRE.get(), pos, blockState);
    }

    public UsingResult addMeat(ItemStack stack, Player player)
    {
        ItemStack returnedStack = ItemHandlerHelper.insertItem(itemHandler, stack, false);

        return new UsingResult(returnedStack, !ItemStack.matches(stack, returnedStack));
    }

    public LureCampfireStackHandler getInventory()
    {
        return itemHandler;
    }

    @Override
    protected void firstTick()
    {}

    @Override
    public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        timer = tag.getInt("timer");
    }

    @Override
    public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("timer", timer);
    }

    @Override
    public void tickServer()
    {
        if (ItemHelper.isEmpty(itemHandler))
            return;
        BlockState state = getBlockState();
        if (state.getValue(LureCampfireBlock.LIT))
            timer++;
        else
            timer = 0;
        if (timer >= getSummonTime())
            summonMobs();
    }

    private void summonMobs()
    {
        Level level = this.getLevel();
        RandomSource rnd = level.getRandom();
        double angle = rnd.nextDouble() * 2 * Math.PI;
        double distance = 32 + rnd.nextDouble() * (48 - 32);
        double dx = Math.cos(angle) * distance;
        double dz = Math.sin(angle) * distance;

        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(this.getBlockPos().getBottomCenter().add(dx, 0, dz)));

        Queen queen = new Queen(level, surfacePos.getBottomCenter(), this.getBlockPos());
        level.addFreshEntity(queen);

        for (int q = 0; q < 4; q++)
        {
            QueenGuard guard = new QueenGuard(Registration.EntityReg.MOB_QUEEN_GUARD.getEntityHolder().get(), level);
            guard.moveTo(surfacePos.getBottomCenter());
            guard.setQueen(queen);
            level.addFreshEntity(guard);
        }
        this.timer = 0;
        level.removeBlock(this.getBlockPos(), false);
    }

    private int getSummonTime()
    {
        int meatAmount = 0;
        for (int q = 0; q < itemHandler.getSlots(); q++)
        {
            ItemStack stack = itemHandler.getStackInSlot(q);
            if (!stack.isEmpty())
                meatAmount ++;
        }
        return meatAmount > 1 ? BASE_SUMMON_TIME - ((meatAmount - 1) * REDUCTION_PER_MEAT) : BASE_SUMMON_TIME;
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<LureCampfireBE> registrar)
    {
        registrar.add("shaft_controller", () -> state ->
        {
            LureCampfireBE animatable = state.animatable();
            if (animatable.getBlockState().getValue(BlockHelper.BlockProperties.LIT) && !ItemHelper.isEmpty(animatable.getInventory()))
            {
                state.controller().play(SHAFT_ROTATION);
                return ControllerState.PLAY;
            }
            return ControllerState.STOP;
        }).add("fire_controller", () -> state ->
        {
            LureCampfireBE animatable = state.animatable();
            if (animatable.getBlockState().getValue(BlockHelper.BlockProperties.LIT))
                state.controller().play(FIRE_ENABLE);
            else
                state.controller().play(FIRE_DISABLE);
            return ControllerState.PLAY;
        });
    }

    @Override
    public PAnimationManager<LureCampfireBE> getAnimationManager(AnimManagerKey key)
    {
        return this.manager;
    }
    
    public record UsingResult(ItemStack stack, boolean added)
    {
    }

    public final class LureCampfireStackHandler extends ItemStackHandler
    {
        private LureCampfireStackHandler(int size)
        {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return stack.is(Registration.BlockReg.FLESH.asItem());
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            LureCampfireBE.this.markDirty();
        }
    }
}
