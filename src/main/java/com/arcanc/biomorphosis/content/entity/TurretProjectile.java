/**
 * @author ArcAnc
 * Created at: 19.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockTurret;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TurretProjectile extends ThrowableProjectile implements GeoEntity
{
	private static final EntityDataAccessor<Integer> EFFECT_ID =
			SynchedEntityData.defineId(TurretProjectile.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<BlockPos> TURRET_POS =
			SynchedEntityData.defineId(TurretProjectile.class, EntityDataSerializers.BLOCK_POS);
	
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	
	public TurretProjectile(EntityType<? extends ThrowableProjectile> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
	{
		builder.define(EFFECT_ID, 0);
		builder.define(TURRET_POS, BlockPos.ZERO);
	}
	
	@Override
	protected void onHitEntity(@NotNull EntityHitResult result)
	{
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		
		Entity hit = result.getEntity();
		
		if (!(hit instanceof LivingEntity living))
			return;
		
		BlockHelper.castTileEntity(this.level(), getTurretPos(), MultiblockTurret.class).
				ifPresent(turret ->
		{
			this.getEffect().
					getEffect().
					applyEffect(turret, living);
			
			spawnImpactParticles(serverLevel, result.getLocation());
		});
		this.discard();
	}
	
	@Override
	protected void onHitBlock(@NotNull BlockHitResult result)
	{
		super.onHitBlock(result);
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		
		spawnImpactParticles(serverLevel, result.getLocation());
		discard();
	}
	
	private void spawnImpactParticles(@NotNull ServerLevel serverLevel, @NotNull Vec3 location)
	{
		//FIXME: change particle types and speed
		ParticleOptions particle = ParticleTypes.CRIT;
		
		serverLevel.sendParticles(
				particle,
				location.x(), location.y(), location.z(),
				12,
				0, 0, 0,
				0.15f);
	}
	
	public void setTurretPos (BlockPos pos)
	{
		if (pos == null)
			return;
		this.getEntityData().set(TURRET_POS, pos);
	}
	
	public BlockPos getTurretPos()
	{
		return this.getEntityData().get(TURRET_POS);
	}
	
	public void setEffect(MultiblockTurret.@NotNull TurretEffect shootEffect)
	{
		this.getEntityData().set(EFFECT_ID, shootEffect.ordinal());
	}
	
	public MultiblockTurret.TurretEffect getEffect()
	{
		int effectId = this.getEntityData().get(EFFECT_ID);
		return MultiblockTurret.TurretEffect.values()[effectId];
	}
	
	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		compound.putInt("effect_id", this.getEntityData().get(EFFECT_ID));
		TagHelper.writeBlockPos(this.getEntityData().get(TURRET_POS), compound, "turret_pos");
	}
	
	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		this.getEntityData().set(EFFECT_ID, compound.getInt("effect_id"));
		this.getEntityData().set(TURRET_POS,
				TagHelper.readBlockPos(compound, "turret_pos"));
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
