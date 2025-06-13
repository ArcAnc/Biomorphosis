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
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LureCampfireBE extends BioBaseBlockEntity implements ServerTickableBE, GeoBlockEntity
{
    private static final RawAnimation SHAFT_ROTATION = RawAnimation.begin().thenLoop("shaft_rotation");
    private static final RawAnimation FIRE_ENABLE = RawAnimation.begin().thenPlayAndHold("fire_enable");
    private static final RawAnimation FIRE_DISABLE = RawAnimation.begin().thenPlayAndHold("fire_disable");

    private static final int BASE_SUMMON_TIME = 2 * 60 * 20;
    private static final int REDUCTION_PER_MEAT = 20 * 15;
    private int timer;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
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
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        timer = tag.getInt("timer");
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
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
        timer = 0;
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
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "shaft_controller", 10, state ->
        {
            if (state.getAnimatable().getBlockState().getValue(BlockHelper.BlockProperties.LIT) && !ItemHelper.isEmpty(state.getAnimatable().getInventory()))
                return state.setAndContinue(SHAFT_ROTATION);
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "fire_controller", state ->
        {
            if (state.getAnimatable().getBlockState().getValue(BlockHelper.BlockProperties.LIT))
                state.getController().setAnimation(FIRE_ENABLE);
            else
                state.getController().setAnimation(FIRE_DISABLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public @NotNull AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
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
        protected int getStackLimit(int slot, @NotNull ItemStack stack)
        {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
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
