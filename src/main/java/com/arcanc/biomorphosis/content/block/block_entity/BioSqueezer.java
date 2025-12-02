/**
 * @author ArcAnc
 * Created at: 30.11.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;


import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.fluid.FluidLevelAnimator;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.SqueezerRecipe;
import com.arcanc.biomorphosis.data.recipe.input.SqueezerRecipeInput;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BioSqueezer extends BioSidedAccessBlockEntity implements GeoBlockEntity, ServerTickableBE
{
	private static final RawAnimation WORK = RawAnimation.begin().thenLoop("work");
	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	
	private static final AABB INPUT_ZONE = new AABB(0,0,0,1, 0.5d, 1);
	
	private final FluidSidedStorage fluidHandler;
	private final ItemStackSidedStorage itemHandler;
	
	private final AABB inputZone;
	private boolean isWorking = false;
	private boolean adrenalineUsedThisTick = false;
	private final ConsumedFluidsData consumedFluidsData = new ConsumedFluidsData();
	private int workedTime = 0;
	
	private final RecipeManager.CachedCheck<SqueezerRecipeInput, SqueezerRecipe> quickCheck;
	
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	
	public BioSqueezer(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_SQUEEZER.get(), pos, blockState);
		
		this.quickCheck = RecipeManager.createCheck(Registration.RecipeReg.SQUEEZER_RECIPE.getRecipeType().get());
		setSideMode(BasicSidedStorage.RelativeFace.UP, BasicSidedStorage.FaceMode.INPUT).
				setSideMode(BasicSidedStorage.RelativeFace.DOWN, BasicSidedStorage.FaceMode.OUTPUT);
		
		this.fluidHandler = new FluidSidedStorage().
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
						setValidator(stack -> stack.is(Registration.FluidReg.BIOMASS.type().get())).
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
		
		this.inputZone = INPUT_ZONE.move(pos.above());
	}
	
	@Override
	public InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx)
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
		
		findInput();
		
		ItemStack input = this.itemHandler.getStackInSlot(0);
		FluidStack lymph = this.fluidHandler.getFluidInTank(0);
		FluidStack adrenaline = this.fluidHandler.getFluidInTank(1);
		this.adrenalineUsedThisTick = false;
		boolean lastWorkingState = this.isWorking;
		this.isWorking = this.fluidHandler.getHolderAt(BasicSidedStorage.FaceMode.OUTPUT, 2).map(holder -> !holder.isFull()).orElse(false) &&
				this.quickCheck.getRecipeFor(new SqueezerRecipeInput(input, lymph, adrenaline), (ServerLevel)this.getLevel()).
						map(recipeHolder ->
				{
					this.workedTime++;
					consumeResources(recipeHolder.value());
					return tryCraft(recipeHolder.value());
				}).orElse(false);
		this.adrenalineUsedThisTick = false;
		if (lastWorkingState != this.isWorking)
			this.markDirty();
	}
	
	private boolean tryCraft(@NotNull SqueezerRecipe recipe)
	{
		int timeToCheck = recipe.getResources().adrenaline().
				filter(adrenaline -> !adrenaline.required() && this.adrenalineUsedThisTick).
				map(adrenaline -> Mth.ceil(recipe.getResources().time() * adrenaline.modifier())).
				orElse(recipe.getResources().time());
		
		if (this.workedTime < timeToCheck)
			return true;
		else
		{
			FluidStack[] result = new FluidStack[]{ recipe.result().copy() };
			
			result[0] = recipe.getResources().acid().map(acid ->
			{
				FluidStack returnedStack = result[0].copy();
				int totalLymphAmount = (int) (acid.perSecond() * timeToCheck);
				if (acid.required())
				{
					if (this.consumedFluidsData.acid < totalLymphAmount)
						returnedStack = FluidStack.EMPTY;
				}
				else
				if (this.consumedFluidsData.acid >= totalLymphAmount)
					returnedStack.setAmount(Mth.ceil(returnedStack.getAmount() * acid.modifier()));
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
	
	private void consumeResources(@NotNull SqueezerRecipe recipe)
	{
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
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "work_controller", 0, state ->
		{
			if (this.isWorking)
				return state.setAndContinue(WORK);
			return state.setAndContinue(IDLE);
		}));
	}
	
	public static @Nullable FluidSidedStorage getFluidHandler(@NotNull BioSqueezer be, Direction ctx)
	{
		return ctx == null ? be.fluidHandler : be.isAccessible(ctx) ? be.fluidHandler : null;
	}
	
	public static @Nullable ItemStackSidedStorage getItemHandler(@NotNull BioSqueezer be, Direction ctx)
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
}
