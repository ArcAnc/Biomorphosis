/**
 * @author ArcAnc
 * Created at: 20.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;


import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BioChest extends BioSidedAccessBlockEntity implements PAnimatable<BioChest>, BlockInterfaces.IInteractionObject<BioChest>
{
	private static final PRawAnimation OPEN = PRawAnimation.begin().thenHold("open").build();
	private static final PRawAnimation CLOSE = PRawAnimation.begin().thenHold("close").build();

	private final PAnimationManager<BioChest> manager = PLibHelper.createManager(this);

	public static final int MAX_SLOT_AMOUNT = 27;

	private final ItemStackSidedStorage itemHandler;
	private boolean open = false;

	public BioChest(BlockPos pos, BlockState blockState)
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
	public InteractionResult onUsed(ItemStack stack, UseOnContext ctx)
	{
		return InteractionResult.PASS;
	}

	@Override
	protected void firstTick()
	{

	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<BioChest> animationRegistrar)
	{
		animationRegistrar.add(() -> state ->
		{
			if (this.open)
				state.controller().play(OPEN);
			else
				state.controller().play(CLOSE);
			return ControllerState.PLAY;
		});
	}
	
	public static @Nullable ItemStackSidedStorage getItemHandler(BioChest be, @Nullable Direction ctx)
	{
		return ctx == null ? be.itemHandler : be.isAccessible(ctx) ? be.itemHandler : null;
	}

	@Override
	public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
	{
		super.readCustomTag(tag, registries, descrPacket);
		this.itemHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Items.HANDLER));
		this.open = tag.getBoolean("open");
	}

	@Override
	public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
	{
		super.writeCustomTag(tag, registries, descrPacket);
		tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));
		tag.putBoolean("open", this.open);
	}
	
	@Override
	public PAnimationManager<BioChest> getAnimationManager(AnimManagerKey key)
	{
		return this.manager;
	}
	
	@Override
	public @Nullable BioChest getGuiMaster()
	{
		return this;
	}

	@Override
	public Registration.MenuTypeReg.ArgContainer<? super BioChest, ?> getContainerType()
	{
		return Registration.MenuTypeReg.CHEST;
	}

	@Override
	public boolean canUseGui(Player player)
	{
		BlockPos pos = this.getBlockPos();
		return player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= 64;
	}
}
