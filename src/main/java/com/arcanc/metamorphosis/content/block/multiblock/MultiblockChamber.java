/**
 * @author ArcAnc
 * Created at: 28.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.multiblock;

import com.arcanc.metamorphosis.content.block.BlockInterfaces;
import com.arcanc.metamorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.metamorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.metamorphosis.content.block.multiblock.base.type.StaticMultiblockPart;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.recipe.ChamberRecipe;
import com.arcanc.metamorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.metamorphosis.data.recipe.input.ChamberRecipeInput;
import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.helper.BlockHelper;
import com.arcanc.metamorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.metamorphosis.util.inventory.item.ItemStackHolder;
import com.arcanc.metamorphosis.util.inventory.item.ItemStackSidedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class MultiblockChamber extends StaticMultiblockPart implements GeoBlockEntity, BlockInterfaces.IInteractionObject<MultiblockChamber>
{
    public static final int MAX_SLOT_AMOUNT = 12;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ItemStackSidedStorage itemHandler;
    private boolean canWork = false;
    private int workedTime;
    private int maxWorkedTime = 0;

    private final RecipeManager.CachedCheck<ChamberRecipeInput, ChamberRecipe> quickCheck;

    public MultiblockChamber(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_CHAMBER.get(), pos, blockState);

        this.quickCheck = RecipeManager.createCheck(Registration.RecipeReg.CHAMBER_RECIPE.getRecipeType().get());

        this.itemHandler = new ItemStackSidedStorage().
                addHolder(ItemStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> false).
                                setCapacity(64).
                                build(),
                        BasicSidedStorage.FaceMode.OUTPUT);
        for (int q = 0; q < MAX_SLOT_AMOUNT; q++)
            this.itemHandler.addHolder(ItemStackHolder.newBuilder().
                            setCallback(holder -> this.markDirty()).
                            setValidator(stack -> true).
                            setCapacity(64).
                            build(),
                    BasicSidedStorage.FaceMode.ALL);
    }

    public void tryStart()
    {   if (this.canWork)
            return;

        this.canWork = true;
        this.workedTime = 0;
        this.maxWorkedTime = 0;
    }

    public boolean isCanWork()
    {
        return this.canWork;
    }

    public int getMaxWorkedTime()
    {
        return this.maxWorkedTime;
    }

    public int getWorkedTime()
    {
        return this.workedTime;
    }

    @Override
    protected void multiblockServerTick()
    {
        Level level = getLevel();
        if (level == null)
            return;

        if (!isMaster())
            return;
        if (!this.canWork)
            return;

        List<ItemStack> input = findInput();

        int lastWorkedTime = this.workedTime;
        this.canWork = this.quickCheck.getRecipeFor(new ChamberRecipeInput(input), (ServerLevel) level).
                map(holder ->
                {
                    this.workedTime++;
                    this.maxWorkedTime = holder.value().getResources().time();
                    return tryCraft(holder.value());
                }).orElseGet(() ->
                {
                    this.workedTime = 0;
                    this.maxWorkedTime = 0;
                    return false;
                });

        if (lastWorkedTime != this.workedTime)
            this.markDirty();
    }

    private boolean tryCraft(@NotNull ChamberRecipe recipe)
    {
        ItemStack resultStack = this.itemHandler.getStackInSlot(0);
        if (!   (resultStack.isEmpty() ||
                (ItemStack.isSameItemSameComponents(recipe.result(), resultStack) &&
                        resultStack.isStackable() &&
                        resultStack.getCount() <= recipe.result().getCount() + resultStack.getMaxStackSize())))
        {
            this.workedTime = 0;
            this.maxWorkedTime = 0;
            return false;
        }


        if (this.workedTime < recipe.getResources().time())
            return true;
        else
        {
            List<ItemStack> available = findInput();

            for (IngredientWithSize needed : recipe.input())
            {
                int remaining = needed.amount();

                for (ItemStack stack : available)
                {
                    if (needed.ingredient().test(stack) && stack.getCount() > 0)
                    {
                        int used = Math.min(remaining, stack.getCount());
                        stack.shrink(used);
                        remaining -= used;

                        if (remaining <= 0)
                            break;
                    }
                }
            }

            if (resultStack.isEmpty())
                this.itemHandler.getHolderAt(null, 0).ifPresent(holder -> holder.setStack(recipe.result().copy()));
            else
                this.itemHandler.getHolderAt(null, 0).ifPresent(holder -> holder.setStack(recipe.result().copyWithCount(recipe.result().getCount() + holder.getStackSize())));
            this.workedTime = 0;
            this.maxWorkedTime = 0;
            return false;
        }
    }

    private @NotNull List<ItemStack> findInput()
    {
        List<ItemStack> items = new ArrayList<>();

        for (int q = 0; q < MAX_SLOT_AMOUNT; q++)
            items.add(this.itemHandler.getStackInSlot(q + 1));
        return items;
    }


    @Override
    protected void firstTick()
    { }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        if (!this.isMaster())
            return;
        tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));
        tag.putBoolean("can_work", this.canWork);
        tag.putInt("worked_time", this.workedTime);
        tag.putInt("max_worked_time", this.maxWorkedTime);
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        if (!this.isMaster())
            return;
        this.itemHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Items.HANDLER));
        this.canWork = tag.getBoolean("can_work");
        this.workedTime = tag.getInt("worked_time");
        this.maxWorkedTime = tag.getInt("max_worked_time");
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, state ->
        {
            if (!isMaster())
                return PlayState.STOP;
            if (getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED)
                return state.setAndContinue(DefaultAnimations.IDLE);
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    public static @Nullable ItemStackSidedStorage getItemHandler(@NotNull MultiblockChamber be, Direction ctx)
    {
        return be.isMaster() ? be.itemHandler : be.getMasterPos().
                flatMap(pos -> BlockHelper.
                        castTileEntity(be.getLevel(), pos, MultiblockChamber.class).
                        map(master -> master.itemHandler)).
                orElse(null);
    }

    @Override
    public @Nullable MultiblockChamber getGuiMaster()
    {
        return isMaster() ? this : getMasterPos().flatMap(pos ->
                BlockHelper.castTileEntity(getLevel(), pos, MultiblockChamber.class)).
                orElse(null);
    }

    @Override
    public Registration.MenuTypeReg.ArgContainer<? super MultiblockChamber, ?> getContainerType()
    {
        return Registration.MenuTypeReg.CHAMBER;
    }

    @Override
    public boolean canUseGui(@NotNull Player player)
    {
        return getMasterPos().map(pos -> player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 64).orElse(false);
    }
}
