/**
 * @author ArcAnc
 * Created at: 28.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.BioCrusherBlock;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.CrusherRecipe;
import com.arcanc.biomorphosis.data.recipe.input.CrusherRecipeInput;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackSidedStorage;
import com.arcanc.biomorphosis.util.inventory.item.StackWithChance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import software.bernie.geckolib.util.GeckoLibUtil;

public class BioCrusher extends BioSidedAccessBlockEntity implements GeoBlockEntity, ServerTickableBE
{
    private static final RawAnimation WORK = RawAnimation.begin().thenLoop("work");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final AABB INPUT_ZONE = new AABB(0,0,0,1, 0.5d, 1);

    private final FluidSidedStorage fluidHandler;
    private final ItemStackSidedStorage itemHandler;

    private final AABB inputZone;
    private boolean isWorking = false;
    private boolean adrenalineUsedThisTick = false;
    private final ConsumedFluidsData consumedFluidsData = new ConsumedFluidsData();
    private int workedTime = 0;

    private final RecipeManager.CachedCheck<CrusherRecipeInput, CrusherRecipe> quickCheck;

    public BioCrusher(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_CRUSHER.get(), pos, blockState);
        this.quickCheck = RecipeManager.createCheck(Registration.RecipeReg.CRUSHER_RECIPE.getRecipeType().get());
        setSideMode(BasicSidedStorage.RelativeFace.UP, BasicSidedStorage.FaceMode.INPUT);

        this.fluidHandler = new FluidSidedStorage().
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.BIOMASS.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.LYMPH.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.ADRENALINE.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT);

        this.itemHandler = new ItemStackSidedStorage().
                addHolder(ItemStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> true).
                                setCapacity(64).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT);

        this.inputZone = INPUT_ZONE.move(pos.above());
    }

    @Override
    public void tickServer()
    {
        Level level = this.getLevel();
        if (level == null)
            return;

        findInput();

        ItemStack input = this.itemHandler.getStackInSlot(0);
        FluidStack biomass = this.fluidHandler.getFluidInTank(0);
        FluidStack lymph = this.fluidHandler.getFluidInTank(1);
        FluidStack adrenaline = this.fluidHandler.getFluidInTank(2);
        this.adrenalineUsedThisTick = false;
        boolean lastWorkingState = this.isWorking;
        this.isWorking = this.quickCheck.getRecipeFor(new CrusherRecipeInput(input, biomass, lymph, adrenaline), (ServerLevel)this.getLevel()).
                map(recipeHolder ->
                {
                    this.workedTime++;
                    consumeResources(recipeHolder.value());
                    return tryCraft((ServerLevel)this.getLevel(), recipeHolder.value());
                }).orElse(false);
        this.adrenalineUsedThisTick = false;
        if (lastWorkingState != this.isWorking)
            this.markDirty();
    }

    private void findInput()
    {
        if (this.level == null)
            return;
        for (ItemEntity entity : this.level.getEntities(EntityType.ITEM, this.inputZone, itemEntity -> true))
        {
            ItemStack stack = this.itemHandler.insert(BasicSidedStorage.FaceMode.INPUT, entity.getItem(), true);
            if (stack.getCount() < entity.getItem().getCount())
                entity.setItem(this.itemHandler.insert(BasicSidedStorage.FaceMode.INPUT, entity.getItem(), false));
        }
    }

    private boolean tryCraft(ServerLevel level, @NotNull CrusherRecipe recipe)
    {
        int timeToCheck = recipe.getResources().adrenaline().
                filter(adrenaline -> !adrenaline.required() && this.adrenalineUsedThisTick).
                map(adrenaline -> Mth.ceil(recipe.getResources().time() * adrenaline.modifier())).
                orElse(recipe.getResources().time());

        if (this.workedTime < timeToCheck)
            return true;
        else
        {
            ItemStack result = ItemStack.EMPTY;
            if (this.consumedFluidsData.biomass >= recipe.getResources().biomass().perSecond() * recipe.getResources().time())
                result = recipe.result().copy();

            result = recipe.getResources().lymph().map(lymph ->
            {
                ItemStack returnedStack = recipe.result().copy();
                int totalLymphAmount = lymph.perSecond() * recipe.getResources().time();
                if (lymph.required())
                {
                    if (this.consumedFluidsData.lymph < totalLymphAmount)
                        returnedStack = ItemStack.EMPTY;
                }
                else
                    if (this.consumedFluidsData.lymph >= totalLymphAmount)
                        returnedStack.setCount(Mth.ceil(returnedStack.getCount() * lymph.modifier()));
                return returnedStack;
            }).orElse(result);

            ItemStack finalResult = result;
            result = recipe.getResources().adrenaline().map(adrenaline ->
            {
               ItemStack returnedStack = finalResult;
               int totalAdrenalineAmount = adrenaline.perSecond() * recipe.getResources().time();
               if (adrenaline.required())
                   if (this.consumedFluidsData.adrenaline < totalAdrenalineAmount)
                       returnedStack = ItemStack.EMPTY;
               return returnedStack;
            }).orElse(result);

            ItemStack toSpawn = result;

            BlockPos pos = getBlockPos();
            BlockState state = getBlockState();
            Direction direction = state.getValue(BioCrusherBlock.HORIZONTAL_FACING);
            Vec3 spawnPos = pos.getCenter().add(0.5d * direction.getStepX(), 0, 0.5d * direction.getStepZ());
            Vec3 spawnVelocity = new Vec3(0.4d * direction.getStepX(), 0.2d, 0.4d * direction.getStepZ());
            ItemEntity entity = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), toSpawn, spawnVelocity.x(), spawnVelocity.y(), spawnVelocity.z());
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);

            if (!recipe.secondaryResults().isEmpty())
            {
                RandomSource rand = level.getRandom();
                for (StackWithChance stack : recipe.secondaryResults())
                {
                    if (stack.chance() <= rand.nextFloat())
                    {
                        entity = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), stack.stack(), spawnVelocity.x(), spawnVelocity.y(), spawnVelocity.z());
                        entity.setDefaultPickUpDelay();
                        level.addFreshEntity(entity);
                    }
                }
            }

            this.itemHandler.extractItem(0, recipe.input().amount(), false);

            clearData();
            return false;
        }
    }

    private void consumeResources(@NotNull CrusherRecipe recipe)
    {
        FluidStack extractedBiomass = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.BIOMASS.still(), recipe.getResources().biomass().perSecond()), false);
        this.consumedFluidsData.biomass += extractedBiomass.getAmount();
        recipe.getResources().lymph().ifPresent(lymph ->
        {
            FluidStack extracted = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.LYMPH.still(), lymph.perSecond()), true);
            if (extracted.getAmount() == lymph.perSecond())
                this.consumedFluidsData.lymph += this.fluidHandler.extract(null, extracted, false).getAmount();
        });
        recipe.getResources().adrenaline().ifPresent(adrenaline ->
        {
            FluidStack extracted = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.ADRENALINE.still(), adrenaline.perSecond()), true);
            if (extracted.getAmount() == adrenaline.perSecond())
            {
                this.consumedFluidsData.adrenaline += this.fluidHandler.extract(null, extracted, false).getAmount();
                this.adrenalineUsedThisTick = true;
            }
        });
    }

    private void clearData()
    {
        this.consumedFluidsData.clearData();
        this.workedTime = 0;
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
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "work_controller", 10, state ->
        {
            if (isWorking)
                return state.setAndContinue(WORK);
            return PlayState.STOP;
        }));
    }

    public static @Nullable FluidSidedStorage getFluidHandler(@NotNull BioCrusher be, Direction ctx)
    {
        return ctx == null ? be.fluidHandler : be.isAccessible(ctx) ? be.fluidHandler : null;
    }

    public static @Nullable ItemStackSidedStorage getItemHandler(@NotNull BioCrusher be, Direction ctx)
    {
        return ctx == null ? be.itemHandler : be.isAccessible(ctx) ? be.itemHandler : null;
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

        this.isWorking = tag.getBoolean("is_working");
        this.adrenalineUsedThisTick = tag.getBoolean("adrenaline_used_this_tick");
        this.workedTime = tag.getInt("worked_time");
        this.consumedFluidsData.readData(tag);

    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        tag.put(Database.Capabilities.Fluids.HANDLER, this.fluidHandler.serializeNBT(registries));
        tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));
        tag.putBoolean("is_working", this.isWorking);
        tag.putBoolean("adrenaline_used_this_tick", this.adrenalineUsedThisTick);
        tag.putInt("worked_time", this.workedTime);
        this.consumedFluidsData.writeData(tag);
    }

    private static final class ConsumedFluidsData
    {
        private int biomass = 0;
        private int lymph = 0;
        private int adrenaline = 0;

        private void clearData()
        {
            this.biomass = 0;
            this.lymph = 0;
            this.adrenaline = 0;
        }

        public void writeData(@NotNull CompoundTag tag)
        {
            tag.putInt("biomass", this.biomass);
            tag.putInt("lymph", this.lymph);
            tag.putInt("adrenaline", this.adrenaline);
        }

        public void readData(@NotNull CompoundTag tag)
        {
            this.biomass = tag.getInt("biomass");
            this.lymph = tag.getInt("lymph");
            this.adrenaline = tag.getInt("adrenaline");
        }
    }
}
