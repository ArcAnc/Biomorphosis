/**
 * @author ArcAnc
 * Created at: 15.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class EggsDeco extends BioBaseBlockEntity implements PAnimatable<EggsDeco>
{
	private final PAnimationManager<EggsDeco> manager = PLibHelper.createManager(this);
	private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
	
	public EggsDeco(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_EGGS_DECO.get(), pos, blockState);
	}

	@Override
	protected void firstTick()
	{

	}

	@Override
	public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
	{

	}

	@Override
	public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
	{

	}
	
	@Override
	public PAnimationManager<EggsDeco> getAnimationManager(AnimManagerKey key)
	{
		return this.manager;
	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<EggsDeco> registrar)
	{
		registrar.add(() -> state ->
		{
			state.controller().play(IDLE);
			return ControllerState.PLAY;
		});
	}
}
