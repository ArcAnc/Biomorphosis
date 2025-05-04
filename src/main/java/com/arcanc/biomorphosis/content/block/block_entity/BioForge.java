/**
 * @author ArcAnc
 * Created at: 03.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.BioCrusherBlock;
import com.arcanc.biomorphosis.content.block.BioForgeBlock;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.ForgeRecipe;
import com.arcanc.biomorphosis.data.recipe.input.ForgeRecipeInput;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackSidedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BioForge extends BioSidedAccessBlockEntity implements GeoBlockEntity, ServerTickableBE
{
    private static final RawAnimation WORK = RawAnimation.begin().thenLoop("work");
    private static final RawAnimation DOUBLE_WORK_LEFT = RawAnimation.begin().thenLoop("work_left");
    private static final RawAnimation DOUBLE_WORK_RIGHT = RawAnimation.begin().thenLoop("work_right");

    private static final RawAnimation DOUBLE_IDLE_LEFT = RawAnimation.begin().thenLoop("misc.idle_left");
    private static final RawAnimation DOUBLE_IDLE_RIGHT = RawAnimation.begin().thenLoop("misc.idle_right");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ItemStackSidedStorage itemHandler;
    private final FluidSidedStorage fluidHandler;

    private final Consumer<Integer> workConsumer;

    private final ConsumedFluidsData[] consumedFluidsData = {new ConsumedFluidsData(), new ConsumedFluidsData()};
    private final boolean[] isWorking = {false, false};
    private final boolean[] adrenalineUsedThisTick = {false, false};
    private final int[] workedTime = {0, 0};

    private final RecipeManager.CachedCheck<ForgeRecipeInput, ForgeRecipe> quickCheck;

    public BioForge(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_FORGE.get(), pos, blockState);
        this.quickCheck = RecipeManager.createCheck(Registration.RecipeReg.FORGE_RECIPE.getRecipeType().get());

        setSideMode(BasicSidedStorage.RelativeFace.UP, BasicSidedStorage.FaceMode.INPUT);

        this.fluidHandler = new FluidSidedStorage().
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.BIOMASS.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.LYMPH.type().get())).
                                setCapacity(2000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.ADRENALINE.type().get())).
                                setCapacity(2000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT);

        this.itemHandler = new ItemStackSidedStorage().
                addHolder(ItemStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setCapacity(64).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(ItemStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setCapacity(64).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT);

        this.workConsumer = inputSlot ->
        {
            ItemStack input = this.itemHandler.getStackInSlot(inputSlot);
            FluidStack biomass = this.fluidHandler.getFluidInTank(0);
            FluidStack lymph = this.fluidHandler.getFluidInTank(1);
            FluidStack adrenaline = this.fluidHandler.getFluidInTank(2);
            this.adrenalineUsedThisTick[inputSlot] = false;
            boolean lastWorkingState = this.isWorking[inputSlot];
            this.isWorking[inputSlot] = this.quickCheck.getRecipeFor(new ForgeRecipeInput(input, biomass, lymph, adrenaline), (ServerLevel)this.getLevel()).
                    map(recipeHolder ->
                    {
                        this.workedTime[inputSlot]++;
                        consumeResources(inputSlot, recipeHolder.value());
                        return tryCraft((ServerLevel)this.getLevel(), inputSlot, recipeHolder.value());
                    }).orElse(false);
            this.adrenalineUsedThisTick[inputSlot] = false;
            if (lastWorkingState != this.isWorking[inputSlot])
                this.markDirty();
        };
    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx)
    {
        return null;
    }

    @Override
    protected void firstTick()
    {

    }

    @Override
    public void tickServer()
    {
        Level level = this.getLevel();
        if (level == null)
            return;

        this.workConsumer.accept(0);
        if (isDouble(this))
            this.workConsumer.accept(1);
    }

    private void consumeResources(int slot, @NotNull ForgeRecipe recipe)
    {
        FluidStack extractedBiomass = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.BIOMASS.still(), recipe.getResources().biomass().perSecond()), true);
        if (extractedBiomass.getAmount() == recipe.getResources().biomass().perSecond())
            this.consumedFluidsData[slot].biomass += this.fluidHandler.extract(null, extractedBiomass, false).getAmount();
        recipe.getResources().lymph().ifPresent(lymph ->
        {
            FluidStack extracted = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.LYMPH.still(), lymph.perSecond()), true);
            if (extracted.getAmount() == lymph.perSecond())
                this.consumedFluidsData[slot].lymph += this.fluidHandler.extract(null, extracted, false).getAmount();
        });
        recipe.getResources().adrenaline().ifPresent(adrenaline ->
        {
            FluidStack extracted = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.ADRENALINE.still(), adrenaline.perSecond()), true);
            if (extracted.getAmount() == adrenaline.perSecond())
            {
                this.consumedFluidsData[slot].adrenaline += this.fluidHandler.extract(null, extracted, false).getAmount();
                this.adrenalineUsedThisTick[slot] = true;
            }
        });
    }

    private boolean tryCraft(ServerLevel level, int slot, @NotNull ForgeRecipe recipe)
    {
        int timeToCheck = recipe.getResources().adrenaline().
                filter(adrenaline -> !adrenaline.required() && this.adrenalineUsedThisTick[slot]).
                map(adrenaline -> Mth.ceil(recipe.getResources().time() * adrenaline.modifier())).
                orElse(recipe.getResources().time());

        if (this.workedTime[slot] < timeToCheck)
            return true;
        else
        {
            ItemStack[] result = new ItemStack[]{ recipe.result().copy()};
            BioBaseRecipe.BiomassInfo biomass = recipe.getResources().biomass();
            int totalBiomassAmount = biomass.perSecond() * timeToCheck;

            if (biomass.required())
            {
                if (this.consumedFluidsData[slot].biomass < totalBiomassAmount)
                    result[0] = ItemStack.EMPTY;
            }
            else
            if (this.consumedFluidsData[slot].biomass >= totalBiomassAmount)
                result[0] = recipe.result().copy();

            result[0] = recipe.getResources().lymph().map(lymph ->
            {
                ItemStack returnedStack = result[0].copy();
                int totalLymphAmount = lymph.perSecond() * timeToCheck;
                if (lymph.required())
                {
                    if (this.consumedFluidsData[slot].lymph < totalLymphAmount)
                        returnedStack = ItemStack.EMPTY;
                }
                else
                if (this.consumedFluidsData[slot].lymph >= totalLymphAmount)
                    returnedStack.setCount(Mth.ceil(returnedStack.getCount() * lymph.modifier()));
                return returnedStack;
            }).orElse(result[0]);

            result[0] = recipe.getResources().adrenaline().map(adrenaline ->
            {
                ItemStack returnedStack = result[0];
                int totalAdrenalineAmount = adrenaline.perSecond() * timeToCheck;
                if (adrenaline.required())
                    if (this.consumedFluidsData[slot].adrenaline < totalAdrenalineAmount)
                        returnedStack = ItemStack.EMPTY;
                return returnedStack;
            }).orElse(result[0]);

            ItemStack toSpawn = result[0];

            BlockPos pos = getBlockPos();
            BlockState state = getBlockState();
            Direction direction = state.getValue(BioCrusherBlock.HORIZONTAL_FACING);
            Vec3 spawnPos = pos.getCenter().add(0.6d * direction.getStepX(), 0, 0.6d * direction.getStepZ());
            Vec3 spawnVelocity = new Vec3(0.3d * direction.getStepX(), 0.1d, 0.3d * direction.getStepZ());
            ItemEntity entity = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), toSpawn, spawnVelocity.x(), spawnVelocity.y(), spawnVelocity.z());
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);

            this.itemHandler.extractItem(slot, recipe.input().amount(), false);

            clearData(slot);
            return false;
        }
    }

    private void clearData(int slot)
    {
        this.consumedFluidsData[slot].clearData();
        this.workedTime[slot] = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(createController(
                "single_controller",
                () -> !isDouble(this),
                () -> this.isWorking[0] ? WORK : DefaultAnimations.IDLE
        ));

        controllers.add(createController(
                "double_left_controller",
                () -> isDouble(this),
                () -> this.isWorking[0] ? DOUBLE_WORK_LEFT : DOUBLE_IDLE_LEFT
        ));

        controllers.add(createController(
                "double_right_controller",
                () -> isDouble(this),
                () -> this.isWorking[1] ? DOUBLE_WORK_RIGHT : DOUBLE_IDLE_RIGHT
        ));
    }

    private @NotNull AnimationController<?> createController(String name, BooleanSupplier condition, Supplier<RawAnimation> animationSupplier)
    {
        return new AnimationController<>(this, name, 0, state -> condition.getAsBoolean()
                ? state.setAndContinue(animationSupplier.get())
                : PlayState.STOP);
    }

    private static boolean isDouble(@NotNull BioForge forge)
    {
        return BioForgeBlock.isDouble(forge.getBlockState());
    }

    public static @Nullable ItemStackSidedStorage getItemHandler(@NotNull BioForge be, Direction ctx)
    {
        return ctx == null ? be.itemHandler : be.isAccessible(ctx) ? be.itemHandler : null;
    }

    public static @Nullable FluidSidedStorage getFluidHandler(@NotNull BioForge be, Direction ctx)
    {
        return ctx == null ? be.fluidHandler : be.isAccessible(ctx) ? be.fluidHandler : null;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        this.fluidHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
        this.itemHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Items.HANDLER));

        IntStream.of(0, 1).forEach(slot ->
        {
            this.isWorking[slot] = tag.getBoolean("is_working_" + slot);
            this.adrenalineUsedThisTick[slot] = tag.getBoolean("adrenaline_used_this_tick_" + slot);
            this.workedTime[slot] = tag.getInt("worked_time_" + slot);
            CompoundTag data = tag.getCompound("fluids_data_" + slot);
            this.consumedFluidsData[slot].readData(data);
        });
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        tag.put(Database.Capabilities.Fluids.HANDLER, this.fluidHandler.serializeNBT(registries));
        tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));

        IntStream.of(0, 1).forEach(slot ->
        {
            tag.putBoolean("is_working_" + slot, this.isWorking[slot]);
            tag.putBoolean("adrenaline_used_this_tick_" + slot, this.adrenalineUsedThisTick[slot]);
            tag.putInt("worked_time_" + slot, this.workedTime[slot]);
            CompoundTag data = new CompoundTag();
            this.consumedFluidsData[slot].writeData(data);
            tag.put("fluids_data_" + slot, data);
        });
    }
}
