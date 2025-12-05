/**
 * @author ArcAnc
 * Created at: 15.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.block_entity;


import com.arcanc.metamorphosis.content.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EggsDeco extends MetaBaseBlockEntity implements GeoBlockEntity
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public EggsDeco(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_EGGS_DECO.get(), pos, blockState);
	}

	@Override
	protected void firstTick()
	{

	}

	@Override
	public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{

	}

	@Override
	public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{

	}

	@Override
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "controller", 0, state ->
				state.setAndContinue(DefaultAnimations.IDLE)));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}
}
