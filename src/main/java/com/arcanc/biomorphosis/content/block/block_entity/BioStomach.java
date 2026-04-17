/**
 * @author ArcAnc
 * Created at: 19.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.fluid.FluidLevelAnimator;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.BioBaseRecipe;
import com.arcanc.biomorphosis.data.recipe.StomachRecipe;
import com.arcanc.biomorphosis.data.recipe.input.StomachRecipeInput;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackSidedStorage;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class BioStomach extends BioSidedAccessBlockEntity implements PAnimatable<BioStomach>, ServerTickableBE
{
    private static final float NO_BIOMASS_SPEED_MODIFIER = 0.5f;

    private static final PRawAnimation WORK = PRawAnimation.begin().thenLoop("work").build();
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    private final PAnimationManager<BioStomach> manager = PLibHelper.createManager(this);

    private final FluidSidedStorage fluidHandler;
    private final ItemStackSidedStorage itemHandler;

    private boolean isWorking = false;
    private boolean adrenalineUsedThisTick = false;
    private boolean biomassUsedThisTick = false;
    private final ConsumedFluidsData consumedFluidsData = new ConsumedFluidsData();
    private int workedTime = 0;

    private final RecipeManager.CachedCheck<StomachRecipeInput, StomachRecipe> quickCheck;

    public BioStomach(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_STOMACH.get(), pos, blockState);
        this.quickCheck = RecipeManager.createCheck(Registration.RecipeReg.STOMACH_RECIPE.getRecipeType().get());
        setSideMode(BasicSidedStorage.RelativeFace.FRONT, BasicSidedStorage.FaceMode.INPUT).
            setSideMode(BasicSidedStorage.RelativeFace.RIGHT, BasicSidedStorage.FaceMode.OUTPUT);

        this.fluidHandler = new FluidSidedStorage().
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.BIOMASS.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.ACID.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.ADRENALINE.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT).
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> stack.is(Registration.FluidReg.ACID.type().get())).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.OUTPUT);

        this.itemHandler = new ItemStackSidedStorage().
                addHolder(ItemStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> true).
                                setCapacity(64).
                                build(),
                        BasicSidedStorage.FaceMode.INPUT);
    }

    @Override
    public @Nullable InteractionResult onUsed(ItemStack stack, UseOnContext ctx)
    {
        return null;
    }

    @Override
    protected void firstTick()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.registerBlockEntity(this.level, getBlockPos());
    }

    @Override
    public void setRemoved()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.removeBlockEntity(this.level, getBlockPos());
        super.setRemoved();
    }

    @Override
    public void tickServer()
    {
        Level level = this.getLevel();
        if (level == null)
            return;

        ItemStack input = this.itemHandler.getStackInSlot(0);
        FluidStack biomass = this.fluidHandler.getFluidInTank(0);
        FluidStack acid = this.fluidHandler.getFluidInTank(1);
        FluidStack adrenaline = this.fluidHandler.getFluidInTank(2);
        this.biomassUsedThisTick = false;
        this.adrenalineUsedThisTick = false;
        boolean lastWorkingState = this.isWorking;
        this.isWorking = this.fluidHandler.getHolderAt(BasicSidedStorage.FaceMode.OUTPUT, 3).map(holder -> !holder.isFull()).orElse(false) &&
                this.quickCheck.getRecipeFor(new StomachRecipeInput(input, biomass, acid, adrenaline), (ServerLevel)this.getLevel()).
                map(recipeHolder ->
                {
                    this.workedTime++;
                    consumeResources(recipeHolder.value());
                    return tryCraft(recipeHolder.value());
                }).orElse(false);
        this.biomassUsedThisTick = false;
        this.adrenalineUsedThisTick = false;
        if (lastWorkingState != this.isWorking)
            this.markDirty();
    }

    private boolean tryCraft(StomachRecipe recipe)
    {
        int timeToCheck = recipe.getResources().adrenaline().
                filter(adrenaline -> !adrenaline.required() && this.adrenalineUsedThisTick).
                map(adrenaline -> Mth.ceil(recipe.getResources().time() * adrenaline.modifier())).
                orElse(recipe.getResources().time()) *
                Mth.ceil((!recipe.getResources().biomass().required() && this.biomassUsedThisTick) ? 1f : NO_BIOMASS_SPEED_MODIFIER);

        if (this.workedTime < timeToCheck)
            return true;
        else
        {
            FluidStack[] result = new FluidStack[]{ recipe.result().copy() };
            BioBaseRecipe.BiomassInfo biomass = recipe.getResources().biomass();
            int totalBiomassAmount = (int) (biomass.perSecond() * timeToCheck);

            if (biomass.required())
            {
                if (this.consumedFluidsData.biomass < totalBiomassAmount)
                    result[0] = FluidStack.EMPTY;
            }
            else
            if (this.consumedFluidsData.biomass >= totalBiomassAmount)
                result[0] = recipe.result().copy();

            result[0] = recipe.getResources().acid().map(lymph ->
            {
                FluidStack returnedStack = result[0].copy();
                int totalLymphAmount = (int) (lymph.perSecond() * timeToCheck);
                if (lymph.required())
                {
                    if (this.consumedFluidsData.acid < totalLymphAmount)
                        returnedStack = FluidStack.EMPTY;
                }
                else
                if (this.consumedFluidsData.acid >= totalLymphAmount)
                    returnedStack.setAmount(Mth.ceil(returnedStack.getAmount() * lymph.modifier()));
                return returnedStack;
            }).orElse(result[0]);

            result[0] = recipe.getResources().adrenaline().map(adrenaline ->
            {
                FluidStack returnedStack = result[0];
                int totalAdrenalineAmount = (int) (adrenaline.perSecond() * timeToCheck);
                if (adrenaline.required())
                    if (this.consumedFluidsData.adrenaline < totalAdrenalineAmount)
                        returnedStack = FluidStack.EMPTY;
                return returnedStack;
            }).orElse(result[0]);

            FluidStack toReturn = result[0];

            toReturn = this.fluidHandler.insert(null, toReturn, false);

            this.itemHandler.extractItem(0, recipe.input().amount(), false);

            clearData();
            return false;
        }
    }

    private void consumeResources(StomachRecipe recipe)
    {
        float biomassPerTick = recipe.getResources().biomass().perSecond();
		this.consumedFluidsData.biomassReminder += biomassPerTick;
		
		int biomassWhole = (int) this.consumedFluidsData.biomassReminder;
		if (biomassWhole > 0)
		{
			FluidStack extractedBiomass = this.fluidHandler.getHolderAt(BasicSidedStorage.FaceMode.INPUT, 0).orElseThrow().drain(new FluidStack(Registration.FluidReg.BIOMASS.still(), biomassWhole), IFluidHandler.FluidAction.SIMULATE);
			if (extractedBiomass.getAmount() == biomassWhole)
			{
				this.consumedFluidsData.biomass += this.fluidHandler.getHolderAt(BasicSidedStorage.FaceMode.INPUT, 0).orElseThrow().drain(extractedBiomass, IFluidHandler.FluidAction.EXECUTE).getAmount();
				this.consumedFluidsData.biomassReminder -= biomassWhole;
				this.biomassUsedThisTick = true;
			}
		}

        recipe.getResources().acid().ifPresent(acid ->
        {
            this.consumedFluidsData.acidRemainder += acid.perSecond();
			int wholeAcid = (int) this.consumedFluidsData.acidRemainder;
			
			if (wholeAcid > 0)
			{
				FluidStack extractedAcid = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.ACID.still(), wholeAcid), true);
				if (extractedAcid.getAmount() == wholeAcid)
				{
					this.consumedFluidsData.acid += this.fluidHandler.extract(null, extractedAcid, false).getAmount();
					this.consumedFluidsData.acidRemainder -= wholeAcid;
				}
			}
        });
        recipe.getResources().adrenaline().ifPresent(adrenaline ->
        {
            this.consumedFluidsData.adrenalineReminder += adrenaline.perSecond();
			int wholeAdrenaline = (int) this.consumedFluidsData.adrenalineReminder;
			
			if (wholeAdrenaline > 0)
			{
				FluidStack extracted = this.fluidHandler.extract(null, new FluidStack(Registration.FluidReg.ADRENALINE.still(), wholeAdrenaline), true);
				if (extracted.getAmount() == wholeAdrenaline)
				{
					this.consumedFluidsData.adrenaline += this.fluidHandler.extract(null, extracted, false).getAmount();
					this.consumedFluidsData.adrenalineReminder -= wholeAdrenaline;
					this.adrenalineUsedThisTick = true;
				}
				
			}
        });
    }
	
    private void clearData()
    {
        this.consumedFluidsData.clearData();
        this.workedTime = 0;
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<BioStomach> animationRegistrar)
    {
        animationRegistrar.add(() -> state ->
        {
            if (this.isWorking)
                state.controller().play(WORK);
            else
                state.controller().play(IDLE);
            return ControllerState.PLAY;
        });
    }

    public static @Nullable FluidSidedStorage getFluidHandler(BioStomach be, @Nullable Direction ctx)
    {
        return ctx == null ? be.fluidHandler : be.isAccessible(ctx) ? be.fluidHandler : null;
    }

    public static @Nullable ItemStackSidedStorage getItemHandler(BioStomach be, @Nullable Direction ctx)
    {
        return ctx == null ? be.itemHandler : be.isAccessible(ctx) ? be.itemHandler : null;
    }
    
    @Override
    public PAnimationManager<BioStomach> getAnimationManager(AnimManagerKey key)
    {
        return this.manager;
    }

    @Override
    public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        this.fluidHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
        this.itemHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Items.HANDLER));

        this.isWorking = tag.getBoolean("is_working");
        this.adrenalineUsedThisTick = tag.getBoolean("adrenaline_used_this_tick");
        this.biomassUsedThisTick = tag.getBoolean("biomass_used_this_tick");
        this.workedTime = tag.getInt("worked_time");
        this.consumedFluidsData.readData(tag);
    }

    @Override
    public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        tag.put(Database.Capabilities.Fluids.HANDLER, this.fluidHandler.serializeNBT(registries));
        tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));
        tag.putBoolean("is_working", this.isWorking);
        tag.putBoolean("adrenaline_used_this_tick", this.adrenalineUsedThisTick);
        tag.putBoolean("biomass_used_this_tick", this.biomassUsedThisTick);
        tag.putInt("worked_time", this.workedTime);
        this.consumedFluidsData.writeData(tag);
    }
}
