/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.spike;

import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CSpike;
import com.arcanc.biomorphosis.content.network.packets.S2CSpikeBarrageCast;
import com.arcanc.biomorphosis.content.network.packets.S2CSpikeImpact;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SpikeBarrageHandler
{
	private static final double HIT_RADIUS = 0.12d;
	private static final Map<ServerLevel, List<Barrage>> BARRAGES = new HashMap<>();
	private static final Map<ServerLevel, List<Spike>> SPIKES = new HashMap<>();

	private SpikeBarrageHandler()
	{
	}

	public static void register()
	{
		NeoForge.EVENT_BUS.addListener(SpikeBarrageHandler :: serverTick);
	}

	public static void spawn(LivingEntity caster, Vec3 targetPoint, SpikeBarrageAbility ability)
	{
		if (!(caster.level() instanceof ServerLevel level) || targetPoint.subtract(caster.getEyePosition()).lengthSqr() < 0.0001d)
			return;

		BARRAGES.computeIfAbsent(level, ignored -> new ArrayList<>()).add(
				new Barrage(caster.getUUID(), targetPoint, ability));
		NetworkEngine.sendToPlayerNear(level, null, caster.position(), 64d,
				new S2CSpikeBarrageCast(caster.getUUID(), SpikeBarrageAbility.BARRAGE_ANIMATION_TICKS));
	}
	
	public static Vec3 findAimPoint(ServerPlayer player, double range)
	{
		ServerLevel level = player.serverLevel();
		Vec3 start = player.getEyePosition();
		Vec3 end = start.add(player.getLookAngle().scale(range));
		BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		Vec3 closestEntityHit = null;
		for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(HIT_RADIUS),
				candidate -> candidate != player && candidate.isAlive() && candidate.isPickable()))
		{
			Vec3 hitLocation = entity.getBoundingBox().inflate(HIT_RADIUS).clip(start, end).orElse(null);
			if (hitLocation != null && (closestEntityHit == null ||
					hitLocation.distanceToSqr(start) < closestEntityHit.distanceToSqr(start)))
				closestEntityHit = hitLocation;
		}

		double blockDistance = blockHit.getType() == HitResult.Type.BLOCK ?
				blockHit.getLocation().distanceToSqr(start) : Double.MAX_VALUE;
		if (closestEntityHit != null && closestEntityHit.distanceToSqr(start) <= blockDistance)
			return closestEntityHit;
		return blockDistance == Double.MAX_VALUE ? end : blockHit.getLocation();
	}

	private static void serverTick(ServerTickEvent.Post event)
	{
		forEachLevel(SPIKES, (level, spikes) -> spikes.removeIf(spike -> !spike.tick(level)));
		forEachLevel(BARRAGES, (level, barrages) -> barrages.removeIf(barrage -> !barrage.tick(level)));
	}

	private static <T> void forEachLevel(Map<ServerLevel, List<T>> entries, LevelTicker<T> ticker)
	{
		Iterator<Map.Entry<ServerLevel, List<T>>> iterator = entries.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<ServerLevel, List<T>> entry = iterator.next();
			ticker.tick(entry.getKey(), entry.getValue());
			if (entry.getValue().isEmpty())
				iterator.remove();
		}
	}

	private static void spawnSpike(ServerLevel level, LivingEntity owner, Vec3 origin, Vec3 direction,
				double lateralCurve, double aimLift, double aimDistance, SpikeBarrageAbility ability)
	{
		SPIKES.computeIfAbsent(level, ignored -> new ArrayList<>()).add(
				new Spike(owner.getUUID(), origin, direction, lateralCurve, aimLift, aimDistance, ability));
		NetworkEngine.sendToPlayerNear(level, null, origin, ability.range() + 32d,
				new S2CSpike(origin, direction.scale(aimDistance), lateralCurve, aimLift, ability.speedPerTick(), ability.range()));
	}

	private static void sendImpact(ServerLevel level, Vec3 origin, double lateralCurve, Vec3 impactPosition)
	{
		NetworkEngine.sendToPlayerNear(level, null, impactPosition, 32.0D,
				new S2CSpikeImpact(origin, lateralCurve, impactPosition));
	}

	private static final class Barrage
	{
		private final UUID ownerId;
		private final Vec3 targetPoint;
		private final SpikeBarrageAbility ability;
		private int volley;
		private int ticksUntilNextVolley;

		private Barrage(UUID ownerId, Vec3 targetPoint, SpikeBarrageAbility ability)
		{
			this.ownerId = ownerId;
			this.targetPoint = targetPoint;
			this.ability = ability;
		}

		private boolean tick(ServerLevel level)
		{
			LivingEntity owner = level.getEntity(this.ownerId) instanceof LivingEntity entity && entity.isAlive() ? entity : null;
			if (owner == null)
				return false;
			if (this.ticksUntilNextVolley-- > 0)
				return true;

			Vec3 initialDirection = this.targetPoint.subtract(owner.getEyePosition());
			if (initialDirection.lengthSqr() < 0.0001d)
				return false;
			Vec3 origin = SpikeGeometry.volleyOrigin(owner, initialDirection.normalize());
			Vec3 targetOffset = this.targetPoint.subtract(origin);
			if (targetOffset.lengthSqr() < 0.0001d)
				return false;
			Vec3 direction = targetOffset.normalize();
			double aimLift = owner.getEyePosition().subtract(origin).dot(SpikeGeometry.up(direction));
			double aimDistance = Math.min(this.ability.range(), targetOffset.length());
			level.playSound(null, origin.x, origin.y, origin.z, Registration.SoundReg.SPIKE_START.get(),
					owner instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 0.85F, 1.0F);
			for (int spikeIndex = 0; spikeIndex < SpikeBarrageAbility.SPIKES_PER_VOLLEY; spikeIndex++)
			{
				double lateralCurve = (spikeIndex - (SpikeBarrageAbility.SPIKES_PER_VOLLEY - 1) / 2d) *
						SpikeBarrageAbility.SIDE_CURVE;
				spawnSpike(level, owner, origin, direction, lateralCurve, aimLift, aimDistance, this.ability);
			}
			this.volley++;
			this.ticksUntilNextVolley = SpikeBarrageAbility.VOLLEY_INTERVAL_TICKS - 1;
			return this.volley < SpikeBarrageAbility.VOLLEY_COUNT;
		}
	}

	private static final class Spike
	{
		private final UUID ownerId;
		private final Vec3 origin;
		private final Vec3 direction;
		private final double lateralCurve;
		private final double aimLift;
		private final double aimDistance;
		private final SpikeBarrageAbility ability;
		private Vec3 position;
		private double traveled;

		private Spike(UUID ownerId, Vec3 origin, Vec3 direction, double lateralCurve, double aimLift, double aimDistance,
				SpikeBarrageAbility ability)
		{
			this.ownerId = ownerId;
			this.origin = origin;
			this.position = origin;
			this.direction = direction;
			this.lateralCurve = lateralCurve;
			this.aimLift = aimLift;
			this.aimDistance = aimDistance;
			this.ability = ability;
		}

		private boolean tick(ServerLevel level)
		{
			LivingEntity owner = level.getEntity(this.ownerId) instanceof LivingEntity entity && entity.isAlive() ? entity : null;
			if (owner == null)
				return false;

			double step = Math.min(this.ability.speedPerTick(), this.ability.range() - this.traveled);
			if (step <= 0.0d)
				return false;

			double nextTraveled = this.traveled + step;
			Vec3 nextPosition = SpikeGeometry.position(this.origin, this.direction, this.lateralCurve, this.aimLift,
				nextTraveled, this.aimDistance);
			BlockHitResult blockHit = level.clip(new ClipContext(this.position, nextPosition,
					ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
			EntityHit entityHit = this.findClosestEntity(level, owner, this.position, nextPosition);
			double blockDistance = blockHit.getType() == HitResult.Type.BLOCK ?
					blockHit.getLocation().distanceToSqr(this.position) : Double.MAX_VALUE;
			double entityDistance = entityHit == null ? Double.MAX_VALUE : entityHit.location().distanceToSqr(this.position);
			if (entityDistance < blockDistance)
			{
				if (entityHit.entity() instanceof LivingEntity target)
					target.hurt(new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).
							getOrThrow(Registration.DamageTypeReg.SPIKE_BARRAGE), owner, owner), SpikeBarrageAbility.DAMAGE);
				sendImpact(level, this.origin, this.lateralCurve, entityHit.location());
				return false;
			}
			if (blockDistance != Double.MAX_VALUE)
			{
				sendImpact(level, this.origin, this.lateralCurve, blockHit.getLocation());
				return false;
			}

			this.position = nextPosition;
			this.traveled = nextTraveled;
			return this.traveled < this.ability.range();
		}

		private EntityHit findClosestEntity(ServerLevel level, LivingEntity owner, Vec3 start, Vec3 end)
		{
			EntityHit closest = null;
			for (Entity entity : level.getEntities(owner, new AABB(start, end).inflate(HIT_RADIUS), candidate ->
					candidate.isAlive() && candidate.isPickable() &&
							(!(candidate instanceof LivingEntity living) || !SwarmTargeting.isSwarmMember(owner) ||
									!SwarmTargeting.isSwarmMember(living))))
			{
				Vec3 hitLocation = entity.getBoundingBox().inflate(HIT_RADIUS).clip(start, end).orElse(null);
				if (hitLocation == null || closest != null && hitLocation.distanceToSqr(start) >= closest.location().distanceToSqr(start))
					continue;
				closest = new EntityHit(entity, hitLocation);
			}
			return closest;
		}
	}

	@FunctionalInterface
	private interface LevelTicker<T>
	{
		void tick(ServerLevel level, List<T> values);
	}

	private record EntityHit(Entity entity, Vec3 location)
	{
	}
}
