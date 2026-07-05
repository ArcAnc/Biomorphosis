/**
 * @author ArcAnc
 * Created at: 28.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

public class FluidCapsuleItem extends BioBaseItem
{
	private static final int DEFAULT_CAPACITY = FluidType.BUCKET_VOLUME;

	public FluidCapsuleItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		ItemStack stack = context.getItemInHand();
		if (!getFluid(stack).isEmpty())
			return InteractionResult.PASS;
		
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction side = context.getClickedFace();
		
		if (level.isClientSide())
			return canFillFromClientView(level, pos, side) ? InteractionResult.SUCCESS : InteractionResult.PASS;

		Player player = context.getPlayer();
		if (tryFillFromBlockHandler(stack, level, pos, side, true))
			return InteractionResult.CONSUME;

		FluidStack filled = pickupSource(level, pos, player);
		if (filled.isEmpty())
			return InteractionResult.PASS;

		setFluid(stack, filled);
		playPickupSound(level, pos, filled);
		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		ItemStack stack = player.getItemInHand(usedHand);
		if (!FluidHelper.isFluidHandler(stack))
			return InteractionResultHolder.pass(stack);
		
		FluidStack fluid = getFluid(stack);
		
		if (fluid.isEmpty())
			return tryFillFromSource(level, player, usedHand, stack);

		if (!OrganicArmorHelper.canFillFluid(player, fluid))
			return InteractionResultHolder.fail(stack);

		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity)
	{
		if (!(livingEntity instanceof Player player))
			return stack;

		FluidStack fluid = getFluid(stack);
		if (!fluid.isEmpty() && player instanceof ServerPlayer serverPlayer)
		{
			int filled = OrganicArmorHelper.fillFluid(serverPlayer, fluid);
			if (filled > 0)
			{
				FluidStack remaining = fluid.copy();
				remaining.shrink(filled);
				setFluid(stack, remaining);
				level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.8f, 1.0f);
			}
		}
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity)
	{
		return 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack)
	{
		return UseAnim.DRINK;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		FluidStack fluid = getFluid(stack);
		if (fluid.isEmpty())
		{
			tooltipComponents.add(Component.translatable("item.biomorphosis.fluid_capsule.empty").withStyle(ChatFormatting.DARK_GRAY));
			return;
		}

		tooltipComponents.add(Component.translatable(fluid.getDescriptionId()).withStyle(ChatFormatting.AQUA));
		tooltipComponents.add(Component.translatable("item.biomorphosis.fluid_capsule.amount", fluid.getAmount(), DEFAULT_CAPACITY).withStyle(ChatFormatting.GRAY));
	}

	public static FluidStack getFluid(ItemStack stack)
	{
		return getContainer(stack).fluid();
	}

	public static CapsuleContainer getContainer(ItemStack stack)
	{
		return stack.getOrDefault(Registration.DataComponentsReg.FLUID_CAPSULE.get(), CapsuleContainer.EMPTY);
	}

	public static int getCapacity(ItemStack stack)
	{
		return getContainer(stack).containerSize();
	}

	public static void setFluid(ItemStack stack, FluidStack fluid)
	{
		int capacity = getCapacity(stack);
		if (fluid.isEmpty() && capacity == DEFAULT_CAPACITY)
			stack.remove(Registration.DataComponentsReg.FLUID_CAPSULE.get());
		else
			stack.set(Registration.DataComponentsReg.FLUID_CAPSULE.get(), new CapsuleContainer(fluid, capacity));
	}

	public static boolean tryFillFromBlockHandler(ItemStack stack, Level level, BlockPos pos, Direction direction)
	{
		return tryFillFromBlockHandler(stack, level, pos, direction, false);
	}

	private static boolean tryFillFromBlockHandler(ItemStack stack, Level level, BlockPos pos, Direction direction, boolean fallbackWithoutSide)
	{
		if (!getFluid(stack).isEmpty())
			return false;

		FluidStack filled = drainFromBlock(level, pos, direction);
		if (filled.isEmpty() && fallbackWithoutSide)
			filled = drainFromBlock(level, pos, null);
		if (filled.isEmpty())
			return false;

		setFluid(stack, filled);
		playPickupSound(level, pos, filled);
		return true;
	}

	private static InteractionResultHolder<ItemStack> tryFillFromSource(Level level, Player player, InteractionHand usedHand, ItemStack stack)
	{
		BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (hitResult.getType() != HitResult.Type.BLOCK)
			return InteractionResultHolder.pass(stack);

		BlockPos pos = hitResult.getBlockPos();
		if (level.isClientSide())
			return InteractionResultHolder.success(stack);

		if (!isSourceFluid(level, pos))
			return InteractionResultHolder.pass(stack);

		FluidStack filled = pickupSource(level, pos, player);
		if (filled.isEmpty())
			return InteractionResultHolder.pass(stack);

		setFluid(stack, filled);
		playPickupSound(level, pos, filled);
		return InteractionResultHolder.consume(stack);
	}

	private static boolean canFillFromClientView(Level level, BlockPos pos, Direction direction)
	{
		if (isSourceFluid(level, pos))
			return true;
		
		return FluidHelper.isFluidHandler(level, pos, direction);
	}

	private static FluidStack drainFromBlock(Level level, BlockPos pos, Direction direction)
	{
		return FluidHelper.getFluidHandler(level, pos, direction).
				map(FluidCapsuleItem :: drainBucket).
				orElse(FluidStack.EMPTY);
	}

	private static FluidStack drainBucket(IFluidHandler handler)
	{
		FluidStack simulated = handler.drain(DEFAULT_CAPACITY, IFluidHandler.FluidAction.SIMULATE);
		if (simulated.getAmount() != DEFAULT_CAPACITY)
			return FluidStack.EMPTY;

		FluidStack drained = handler.drain(DEFAULT_CAPACITY, IFluidHandler.FluidAction.EXECUTE);
		return drained.getAmount() == DEFAULT_CAPACITY && FluidStack.isSameFluidSameComponents(simulated, drained) ? drained.copy() : FluidStack.EMPTY;
	}

	private static boolean isSourceFluid(Level level, BlockPos pos)
	{
		FluidState fluidState = level.getBlockState(pos).getFluidState();
		return !fluidState.isEmpty() && fluidState.isSource();
	}

	private static FluidStack pickupSource(Level level, BlockPos pos, Player player)
	{
		BlockState state = level.getBlockState(pos);
		FluidState fluidState = state.getFluidState();
		if (fluidState.isEmpty() || !fluidState.isSource())
			return FluidStack.EMPTY;
		if (!(state.getBlock() instanceof BucketPickup pickup))
			return FluidStack.EMPTY;

		FluidStack filled = new FluidStack(fluidState.getType(), DEFAULT_CAPACITY);
		if (pickup.pickupBlock(player, level, pos, state).isEmpty())
			return FluidStack.EMPTY;
		return filled;
	}

	private static void playPickupSound(Level level, BlockPos pos, FluidStack fluid)
	{
		SoundEvent sound = fluid.isEmpty() ? SoundEvents.BUCKET_FILL : fluid.getFluidType().getSound(fluid, SoundActions.BUCKET_FILL);
		level.playSound(null, pos, sound == null ? SoundEvents.BUCKET_FILL : sound, SoundSource.PLAYERS, 1.0f, 1.0f);
	}

	public record CapsuleContainer(FluidStack fluid, int containerSize)
	{
		public static final CapsuleContainer EMPTY = new CapsuleContainer(FluidStack.EMPTY, DEFAULT_CAPACITY);

		public CapsuleContainer
		{
			containerSize = Math.max(0, containerSize);
			fluid = normalizeFluid(fluid, containerSize);
		}

		@Override
		public FluidStack fluid()
		{
			return this.fluid.copy();
		}
		
		public static final Codec<CapsuleContainer> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(CapsuleContainer :: fluid),
						Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(CapsuleContainer :: containerSize)).
				apply(instance, CapsuleContainer :: new));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, CapsuleContainer> STREAM_CODEC = StreamCodec.composite(
				FluidStack.OPTIONAL_STREAM_CODEC,
				CapsuleContainer :: fluid,
				ByteBufCodecs.INT,
				CapsuleContainer :: containerSize,
				CapsuleContainer :: new);

		private static FluidStack normalizeFluid(FluidStack fluid, int containerSize)
		{
			if (fluid.isEmpty() || containerSize <= 0)
				return FluidStack.EMPTY;

			FluidStack copy = fluid.copy();
			copy.setAmount(Math.min(copy.getAmount(), containerSize));
			return copy;
		}
	}
	
	public static class CapsuleFluidHandler implements IFluidHandlerItem
	{
		private final ItemStack container;

		public CapsuleFluidHandler(ItemStack container)
		{
			this.container = container;
		}

		@Override
		public ItemStack getContainer()
		{
			return this.container;
		}

		@Override
		public int getTanks()
		{
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int tank)
		{
			return tank == 0 ? getFluid(this.container).copy() : FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank)
		{
			return tank == 0 ? getCapacity(this.container) : 0;
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack)
		{
			if (tank != 0 || stack.isEmpty())
				return false;

			FluidStack current = getFluid(this.container);
			return current.isEmpty() || FluidStack.isSameFluidSameComponents(current, stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action)
		{
			if (!isFluidValid(0, resource))
				return 0;

			FluidStack current = getFluid(this.container);
			int filled = Math.min(getCapacity(this.container) - current.getAmount(), resource.getAmount());
			if (filled <= 0)
				return 0;

			if (action.execute())
			{
				FluidStack result = current.isEmpty() ? resource.copy() : current.copy();
				result.setAmount(current.getAmount() + filled);
				setFluid(this.container, result);
			}
			return filled;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action)
		{
			FluidStack current = getFluid(this.container);
			if (resource.isEmpty() || current.isEmpty() || !FluidStack.isSameFluidSameComponents(current, resource))
				return FluidStack.EMPTY;
			return drain(resource.getAmount(), action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action)
		{
			FluidStack current = getFluid(this.container);
			if (current.isEmpty() || maxDrain <= 0)
				return FluidStack.EMPTY;

			int drainedAmount = Math.min(maxDrain, current.getAmount());
			FluidStack drained = current.copy();
			drained.setAmount(drainedAmount);

			if (action.execute())
			{
				FluidStack left = current.copy();
				left.shrink(drainedAmount);
				setFluid(this.container, left);
			}
			return drained;
		}
	}
}
