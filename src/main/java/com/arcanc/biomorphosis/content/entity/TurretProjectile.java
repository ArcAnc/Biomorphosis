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
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class TurretProjectile extends ThrowableProjectile implements PAnimatable<TurretProjectile>
{
	private static final EntityDataAccessor<Integer> EFFECT_ID =
			SynchedEntityData.defineId(TurretProjectile.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<BlockPos> TURRET_POS =
			SynchedEntityData.defineId(TurretProjectile.class, EntityDataSerializers.BLOCK_POS);
	
	private final PAnimationManager<TurretProjectile> manager = PLibHelper.createManager(this);
	private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
	
	public TurretProjectile(EntityType<? extends ThrowableProjectile> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		builder.define(EFFECT_ID, 0);
		builder.define(TURRET_POS, BlockPos.ZERO);
	}
	
	@Override
	public void tick()
	{
		if (this.firstTick)
			this.setNoGravity(true);
		super.tick();
		RandomSource random = this.level().random;
		for (int q = 0; q < 4; q++)
			this.level().addParticle(ParticleTypes.CRIT,
					this.getX() + random.nextFloat() * 0.75f - 0.75f,
					this.getY() + random.nextFloat() * 0.75f - 0.75f,
					this.getZ() + random.nextFloat() * 0.75f - 0.75f, 0, 0.005f, 0);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result)
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
	protected void onHitBlock(BlockHitResult result)
	{
		super.onHitBlock(result);
		if (!(this.level() instanceof ServerLevel serverLevel))
			return;
		
		spawnImpactParticles(serverLevel, result.getLocation());
		discard();
	}
	
	private void spawnImpactParticles(ServerLevel serverLevel, Vec3 location)
	{
		//FIXME: change particle types and speed
		ParticleOptions particle = ParticleTypes.EXPLOSION;
		
		serverLevel.sendParticles(
				particle,
				location.x(), location.y(), location.z(),
				1,
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
	
	public void setEffect(MultiblockTurret.TurretEffect shootEffect)
	{
		this.getEntityData().set(EFFECT_ID, shootEffect.ordinal());
	}
	
	public MultiblockTurret.TurretEffect getEffect()
	{
		int effectId = this.getEntityData().get(EFFECT_ID);
		return MultiblockTurret.TurretEffect.values()[effectId];
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		compound.putInt("effect_id", this.getEntityData().get(EFFECT_ID));
		TagHelper.writeBlockPos(this.getEntityData().get(TURRET_POS), compound, "turret_pos");
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		this.getEntityData().set(EFFECT_ID, compound.getInt("effect_id"));
		this.getEntityData().set(TURRET_POS,
				TagHelper.readBlockPos(compound, "turret_pos"));
	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<TurretProjectile> registrar)
	{
		registrar.add(new PAnimationController<>(state ->
		{
			state.controller().play(IDLE);
			return state.controller().getState();
		}));
	}
	
	@Override
	public PAnimationManager<TurretProjectile> getAnimationManager()
	{
		return this.manager;
	}
}
