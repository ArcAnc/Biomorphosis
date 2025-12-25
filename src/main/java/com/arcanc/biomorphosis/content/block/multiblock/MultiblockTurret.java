/**
 * @author ArcAnc
 * Created at: 17.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;


import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPart;
import com.arcanc.biomorphosis.content.entity.TurretProjectile;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.DamageHelper;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class MultiblockTurret extends StaticMultiblockPart implements GeoBlockEntity, BlockInterfaces.IInteractionObject<MultiblockTurret>
{
	private static final int TURRET_DAMAGE = 6;
	private static final int COOLDOWN = 20 * 3;
	private static final int RESOURCE_PER_SHOOT = 100;
	private static final int MAX_RANGE = 32;
	
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	
	private final FluidSidedStorage fluidHandler;
	private boolean enabled;
	private TurretEffect shootEffect;
	private TargetMode targetMode;
	private int timer;
	private UUID target;
	
	public MultiblockTurret(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_MULTIBLOCK_TURRET.get(), pos, blockState);
		this.shootEffect = TurretEffect.KNOCKBACK;
		this.targetMode = TargetMode.AGGRESSIVE_MOBS;
		this.fluidHandler = new FluidSidedStorage().
				addHolder(FluidStackHolder.newBuilder().
						setCallback(holder -> this.markDirty()).
						setValidator(fluid -> fluid.is(Registration.FluidReg.BIOMASS.type().get())).
						setCapacity(500).
						build(),
					BasicSidedStorage.FaceMode.ALL).
				addHolder(FluidStackHolder.newBuilder().
						setCallback(holder -> this.markDirty()).
						setValidator(fluid -> fluid.is(Registration.FluidReg.ACID.type().get())).
						setCapacity(500).
						build(),
				BasicSidedStorage.FaceMode.ALL).
				addHolder(FluidStackHolder.newBuilder().
						setCallback(holder -> this.markDirty()).
						setValidator(fluid -> fluid.is(Registration.FluidReg.ADRENALINE.type().get())).
						setCapacity(500).
						build(),
				BasicSidedStorage.FaceMode.ALL);
	}
	
	@Override
	public @Nullable MultiblockTurret getGuiMaster()
	{
		return isMaster() ? this : getMasterPos().flatMap(pos ->
					BlockHelper.castTileEntity(getLevel(), pos, MultiblockTurret.class)).
				orElse(null);
	}
	
	@Override
	public boolean canUseGui(@NotNull Player player)
	{
		return getMasterPos().map(pos -> player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 64).orElse(false);
	}
	
	@Override
	protected void multiblockServerTick()
	{
		Level level = getLevel();
		if (level == null)
			return;
		
		if (!isMaster())
			return;
		if (!isEnabled())
			return;
			
		if (!haveResource())
			return;
		findTarget();
		if (this.target == null)
			return;
		if (!checkTimer())
			return;
		shootAt(this.target);
	}
	
	private void shootAt(UUID target)
	{
		Entity cachedTarget = this.level.getEntities().get(target);
		if (!(cachedTarget instanceof LivingEntity living))
			return;
		Vec3 start = this.getBlockPos().above(2).getCenter();
		Vec3 targetPos = living.getEyePosition();
		Vec3 dir = targetPos.subtract(start).normalize();
		
		TurretProjectile projectile = new TurretProjectile(Registration.EntityReg.PROJECTILE_TURRET.getEntityHolder().get(), this.level);
		
		projectile.setOwner(null);
		projectile.setPos(start);
		projectile.setEffect(this.shootEffect);
		projectile.setTurretPos(this.getBlockPos());
		projectile.shoot(dir.x(), dir.y(), dir.z(), 1.5f, 0);
		
		this.level.addFreshEntity(projectile);
		Database.LOGGER.warn("Shoot Effect: {}", this.targetMode.name());
		this.fluidHandler.getHolderAt(null, this.shootEffect.ordinal()).
				ifPresent(fluidStackHolder -> fluidStackHolder.drain(RESOURCE_PER_SHOOT, IFluidHandler.FluidAction.EXECUTE));
		this.markDirty();
	}
	
	private boolean checkTimer()
	{
		this.timer++;
		this.markDirty();
		if (this.timer < COOLDOWN)
			return false;
		this.timer = 0;
		this.markDirty();
		return true;
	}
	
	private void findTarget()
	{
		if (!(this.level instanceof ServerLevel serverLevel))
			return;
		List<? extends LivingEntity> entities = serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class),
						entity -> this.targetMode.validateTarget(entity) &&
						entity.position().closerThan(this.getBlockPos().getBottomCenter(), MAX_RANGE));
		if (entities.isEmpty())
		{
			this.target = null;
			this.timer = 0;
			this.markDirty();
			return;
		}
		entities.sort((target1, target2) ->
		{
			double dist1 = target1.position().distanceToSqr(this.getBlockPos().getBottomCenter());
			double dist2 = target2.position().distanceToSqr(this.getBlockPos().getBottomCenter());
			return Double.compare(dist1, dist2);
		});
		this.target = entities.getFirst().getUUID();
		this.markDirty();
	}
	
	private boolean haveResource()
	{
		return this.fluidHandler.getFluidInTank(this.targetMode.ordinal()).getAmount() >= RESOURCE_PER_SHOOT;
	}
	
	
	@Override
	protected void firstTick()
	{
	
	}
	
	public TurretEffect getShootEffect()
	{
		return this.shootEffect;
	}
	
	public TargetMode getTargetMode()
	{
		return targetMode;
	}
	
	public boolean isEnabled()
	{
		return this.enabled;
	}

	public void setShootEffect(TurretEffect shootEffect)
	{
		this.shootEffect = shootEffect;
		this.markDirty();
	}
	
	public void setTargetMode(TargetMode targetMode)
	{
		this.targetMode = targetMode;
		this.markDirty();
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		this.markDirty();
	}
	
	public boolean isAttacking()
	{
		return this.timer > 0;
	}
	
	@Override
	public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.writeCustomTag(tag, registries, descrPacket);
		if (!this.isMaster())
			return;
		tag.put(Database.Capabilities.Fluids.HANDLER, this.fluidHandler.serializeNBT(registries));
		tag.putBoolean("enabled", this.enabled);
		tag.putInt("shoot_effect", this.shootEffect.ordinal());
		tag.putInt("target_mode", this.targetMode.ordinal());
		tag.putInt("timer", this.timer);
		if (this.target != null)
			tag.putUUID("target", this.target);
	}
	
	@Override
	public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.readCustomTag(tag, registries, descrPacket);
		if (!this.isMaster())
			return;
		this.fluidHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
		this.enabled = tag.getBoolean("enabled");
		this.shootEffect = TurretEffect.values()[tag.getInt("shoot_effect")];
		this.targetMode = TargetMode.values()[tag.getInt("target_mode")];
		this.timer = tag.getInt("timer");
		if (tag.contains("target"))
			this.target = tag.getUUID("target");
		else
			this.target = null;
	}
	
	@Override
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "controller", 0, state ->
		{
			if (!isMaster())
				return PlayState.STOP;
			if (getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED)
				if (this.isAttacking())
					return state.setAndContinue(DefaultAnimations.ATTACK_THROW);
				else
					return state.setAndContinue(DefaultAnimations.IDLE);
			return PlayState.STOP;
		}));
	}
	
	@Override
	public Registration.MenuTypeReg.ArgContainer<? super MultiblockTurret, ?> getContainerType()
	{
		return Registration.MenuTypeReg.TURRET;
	}
	
	public static @Nullable FluidSidedStorage getFluidHandler(@NotNull MultiblockTurret be, Direction ctx)
	{
		return be.isMaster() ? be.fluidHandler : be.getMasterPos().
				flatMap(pos -> BlockHelper.
						castTileEntity(be.getLevel(), pos, MultiblockTurret.class).
						map(master -> master.fluidHandler)).
				orElse(null);
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}
	
	public enum TargetMode
	{
		EVERYONE (TargetMode :: isValidBaseTarget),
		PLAYERS(entity -> isValidBaseTarget(entity) &&
				entity instanceof Player player &&
				!player.isCreative() &&
				!player.isSpectator() &&
				!player.isInvulnerable()),
		AGGRESSIVE_MOBS(entity -> isValidBaseTarget(entity) &&
				entity instanceof Enemy);
		
		private final Predicate<LivingEntity> targetValidator;
		
		TargetMode(Predicate<LivingEntity> targetValidator)
		{
			this.targetValidator = targetValidator;
		}
		
		public boolean validateTarget(LivingEntity target)
		{
			return targetValidator.test(target);
		}
		
		private static boolean isValidBaseTarget(@NotNull LivingEntity target)
		{
			return target.isAlive() && !target.isRemoved();
		}
	}
	
	public enum TurretEffect
	{
		POWER("power",
				MathHelper.ColorHelper.color(194, 227, 58, 89),
				(shooter,target) ->
				{
					if (!target.isAlive() || target.level().isClientSide())
						return;
					DamageHelper.turretDamage(shooter, MultiblockTurret.TURRET_DAMAGE * 2, target);
				}),
		POISON("poison",
				MathHelper.ColorHelper.color(194, 95, 213, 86),
				(shooter, target) ->
				{
					if (!target.isAlive() || target.level().isClientSide())
						return;
					DamageHelper.turretDamage(shooter, MultiblockTurret.TURRET_DAMAGE, target);
					target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
				}),
		KNOCKBACK("knockback",
				MathHelper.ColorHelper.color(194, 77, 165, 191),
				(shooter,target) ->
				{
					if (!target.isAlive() || target.level().isClientSide())
						return;
					DamageHelper.turretDamage(shooter, MultiblockTurret.TURRET_DAMAGE, target);
					Vec3 shooterPos = shooter.getBlockPos().getBottomCenter();
					double x = shooterPos.x() - target.getX();
					double z = shooterPos.z() - target.getZ();
					target.knockback(1.0f, x, z);
				});
		
		private final String name;
		private final int color;
		private final EffectApplier effect;
		
		TurretEffect(String name, int color, EffectApplier effect)
		{
			this.name = name;
			this.color = color;
			this.effect = effect;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public int getColor()
		{
			return this.color;
		}
		
		public EffectApplier getEffect()
		{
			return this.effect;
		}
	}
	
	@FunctionalInterface
	public interface EffectApplier
	{
		void applyEffect(MultiblockTurret shooter, LivingEntity target);
	}
}
