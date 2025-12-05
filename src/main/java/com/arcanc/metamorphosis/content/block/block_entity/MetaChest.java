/**
 * @author ArcAnc
 * Created at: 20.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.block_entity;


import com.arcanc.metamorphosis.content.block.BlockInterfaces;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.metamorphosis.util.inventory.item.ItemStackHolder;
import com.arcanc.metamorphosis.util.inventory.item.ItemStackSidedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MetaChest extends MetaSidedAccessBlockEntity implements GeoBlockEntity, BlockInterfaces.IInteractionObject<MetaChest>
{
	private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("open");
	private static final RawAnimation CLOSE = RawAnimation.begin().thenPlayAndHold("close");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public static final int MAX_SLOT_AMOUNT = 27;

	private final ItemStackSidedStorage itemHandler;
	private boolean open = false;

	public MetaChest(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_CHEST.get(), pos, blockState);
		setSideMode(BasicSidedStorage.RelativeFace.UP, BasicSidedStorage.FaceMode.ALL);

		this.itemHandler = new ItemStackSidedStorage();

		for (int q = 0; q < MAX_SLOT_AMOUNT; q++)
				this.itemHandler.addHolder(ItemStackHolder.newBuilder().
						setCallback(holder -> this.markDirty()).
						setValidator(stack -> true).
						setCapacity(64).
						build(),
				BasicSidedStorage.FaceMode.ALL);
	}

	public void open()
	{
		if (this.level == null)
			return;
		this.open = true;
		this.markDirty();
		this.level.playSound(null, this.getBlockPos(), Registration.SoundReg.BLOCK_CHEST_OPEN.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
	}

	public void close()
	{
		if (this.level == null)
			return;
		this.open = false;
		this.markDirty();
		this.level.playSound(null, this.getBlockPos(), Registration.SoundReg.BLOCK_CHEST_CLOSE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
	}

	@Override
	public InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx)
	{
		return InteractionResult.PASS;
	}

	@Override
	protected void firstTick()
	{

	}

	@Override
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "controller", 0,
				state ->
				this.open ?
						state.setAndContinue(OPEN) :
						state.setAndContinue(CLOSE)));
	}

	public static @Nullable ItemStackSidedStorage getItemHandler(@NotNull MetaChest be, Direction ctx)
	{
		return ctx == null ? be.itemHandler : be.isAccessible(ctx) ? be.itemHandler : null;
	}

	@Override
	public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.readCustomTag(tag, registries, descrPacket);
		this.itemHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Items.HANDLER));
		this.open = tag.getBoolean("open");
	}

	@Override
	public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.writeCustomTag(tag, registries, descrPacket);
		tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));
		tag.putBoolean("open", this.open);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}

	@Override
	public @Nullable MetaChest getGuiMaster()
	{
		return this;
	}

	@Override
	public Registration.MenuTypeReg.ArgContainer<? super MetaChest, ?> getContainerType()
	{
		return Registration.MenuTypeReg.CHEST;
	}

	@Override
	public boolean canUseGui(@NotNull Player player)
	{
		BlockPos pos = this.getBlockPos();
		return player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= 64;
	}
}
