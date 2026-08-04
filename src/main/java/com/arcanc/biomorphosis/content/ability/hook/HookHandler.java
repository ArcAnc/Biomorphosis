/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.hook;

import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CHook;
import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Direction;
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


public final class HookHandler
{
	private static final double HIT_RADIUS = 0.20d;
	private static final double BLOCK_CLEARANCE = 0.08d;
	private static final double MOB_CLEARANCE = 0.15d;
	private static final Map<ServerLevel, List<Hook>> HOOKS = new HashMap<>();

	private HookHandler()
	{
	}

	public static void register()
	{
		NeoForge.EVENT_BUS.addListener(HookHandler :: serverTick);
	}

	public static void spawn(ServerPlayer player, HookAbility ability)
	{
		spawn(player, player.getLookAngle(), ability);
	}

	public static void spawn(LivingEntity caster, Vec3 direction, HookAbility ability)
	{
		if (!(caster.level() instanceof ServerLevel level) || direction.lengthSqr() < 0.0001D)
			return;

		Vec3 normalizedDirection = direction.normalize();
		Vec3 origin = HookGeometry.handOrigin(caster, normalizedDirection);
		HOOKS.computeIfAbsent(level, ignored -> new ArrayList<>()).add(new Hook(caster.getUUID(), origin, normalizedDirection, ability));
		NetworkEngine.sendToPlayerNear(level, null, origin, ability.range() + 32.0D,
				new S2CHook(caster.getUUID(), origin, normalizedDirection, ability.speedPerTick(), ability.range()));
	}

	private static void serverTick(ServerTickEvent.Post event)
	{
		Iterator<Map.Entry<ServerLevel, List<Hook>>> levels = HOOKS.entrySet().iterator();
		while (levels.hasNext())
		{
			Map.Entry<ServerLevel, List<Hook>> entry = levels.next();
			entry.getValue().removeIf(hook -> !hook.tick(entry.getKey()));
			if (entry.getValue().isEmpty())
				levels.remove();
		}
	}

	private static final class Hook
	{
		private final UUID ownerId;
		private final Vec3 origin;
		private final Vec3 direction;
		private final HookAbility ability;
		private Vec3 position;
		private double traveled;
		private Vec3 blockAnchor;
		private UUID mobTargetId;
		private int pullTicks;

		private Hook(UUID ownerId, Vec3 position, Vec3 direction, HookAbility ability)
		{
			this.ownerId = ownerId;
			this.origin = position;
			this.position = position;
			this.direction = direction;
			this.ability = ability;
		}

		private boolean tick(ServerLevel level)
		{
			LivingEntity owner = level.getEntity(this.ownerId) instanceof LivingEntity entity ? entity : null;
			if (owner == null || owner.level() != level || owner.isDeadOrDying())
				return false;

			if (this.blockAnchor != null)
				return this.pullOwnerTo(owner, this.blockAnchor);
			if (this.mobTargetId != null)
				return this.pullTogether(level, owner);

			double step = Math.min(this.ability.speedPerTick(), this.ability.range() - this.traveled);
			if (step <= 0.0D)
				return false;

			double nextTraveled = this.traveled + step;
			Vec3 nextPosition = HookGeometry.spiralPosition(this.origin, this.direction, nextTraveled);
			BlockHitResult blockHit = level.clip(new ClipContext(this.position, nextPosition,
					ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
			HitCandidate mobHit = this.findClosestMob(level, owner, this.position, nextPosition);
			double blockDistance = blockHit.getType() == HitResult.Type.BLOCK ?
					blockHit.getLocation().distanceToSqr(this.position) : Double.MAX_VALUE;
			double mobDistance = mobHit == null ? Double.MAX_VALUE : mobHit.location().distanceToSqr(this.position);

			if (mobDistance < blockDistance)
			{
				this.position = mobHit.location();
				this.mobTargetId = mobHit.mob().getUUID();
				this.pullTicks = HookAbility.PULL_TICKS;
				return true;
			}

			if (blockDistance != Double.MAX_VALUE)
			{
				this.position = blockHit.getLocation();
				this.blockAnchor = getBlockAnchor(owner, blockHit);
				this.pullTicks = HookAbility.PULL_TICKS;
				return true;
			}

			this.position = nextPosition;
			this.traveled = nextTraveled;
			return this.traveled < this.ability.range();
		}

		private HitCandidate findClosestMob(ServerLevel level, LivingEntity owner, Vec3 start, Vec3 end)
		{
			HitCandidate closest = null;
			for (LivingEntity mob : level.getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(HIT_RADIUS),
					mob -> mob.isAlive() && mob.isPickable() && !mob.getUUID().equals(this.ownerId) &&
							(!SwarmTargeting.isSwarmMember(owner) ||
									!SwarmTargeting.isSwarmMember(mob))))
			{
				Vec3 hit = mob.getBoundingBox().inflate(HIT_RADIUS).clip(start, end).orElse(null);
				if (hit == null || closest != null && hit.distanceToSqr(start) >= closest.location().distanceToSqr(start))
					continue;
				closest = new HitCandidate(mob, hit);
			}
			return closest;
		}

		private boolean pullOwnerTo(LivingEntity owner, Vec3 anchor)
		{
			if (--this.pullTicks < 0 || !pull(owner, anchor, HookAbility.PULL_SPEED, 0.05D))
				return false;
			return true;
		}

		private boolean pullTogether(ServerLevel level, LivingEntity owner)
		{
			LivingEntity target = level.getEntity(this.mobTargetId) instanceof LivingEntity entity && entity.isAlive() ? entity : null;
			if (target == null || --this.pullTicks < 0)
				return false;

			Vec3 ownerCenter = owner.getBoundingBox().getCenter();
			Vec3 targetCenter = target.getBoundingBox().getCenter();
			double distance = ownerCenter.distanceTo(targetCenter);
			double contactDistance = (owner.getBbWidth() + target.getBbWidth()) / 2.0D + MOB_CLEARANCE;
			if (distance <= contactDistance)
			{
				removeApproachingVelocity(owner, targetCenter);
				removeApproachingVelocity(target, ownerCenter);
				return false;
			}

			double sharedSpeed = Math.min(HookAbility.PULL_SPEED, (distance - contactDistance) / 2.0D);
			pull(owner, targetCenter, sharedSpeed, 0.0D);
			pull(target, ownerCenter, sharedSpeed, 0.0D);
			return true;
		}

		private static Vec3 getBlockAnchor(LivingEntity owner, BlockHitResult hit)
		{
			Vec3 eyeToCenter = owner.getEyePosition().subtract(owner.getBoundingBox().getCenter());
			Vec3 anchor = hit.getLocation().subtract(eyeToCenter);
			Direction face = hit.getDirection();
			if (face.getAxis().isVertical())
			{
				double centerY = hit.getLocation().y + face.getStepY() * (owner.getBbHeight() / 2.0D + BLOCK_CLEARANCE);
				return new Vec3(anchor.x, centerY, anchor.z);
			}
			return anchor.add(face.getStepX() * (owner.getBbWidth() / 2.0D + BLOCK_CLEARANCE), 0.0D,
					face.getStepZ() * (owner.getBbWidth() / 2.0D + BLOCK_CLEARANCE));
		}

		private static boolean pull(net.minecraft.world.entity.Entity entity, Vec3 target, double speed, double stoppingDistance)
		{
			Vec3 offset = target.subtract(entity.getBoundingBox().getCenter());
			double distance = offset.length();
			if (distance <= stoppingDistance)
				return false;

			Vec3 impulse = offset.scale(Math.min(speed, distance - stoppingDistance) / distance);
			entity.setDeltaMovement(impulse);
			entity.hurtMarked = true;
			if (entity instanceof ServerPlayer player)
				player.resetFallDistance();
			return true;
		}

		private static void removeApproachingVelocity(net.minecraft.world.entity.Entity entity, Vec3 target)
		{
			Vec3 offset = target.subtract(entity.getBoundingBox().getCenter());
			if (offset.lengthSqr() < 0.0001D)
				return;

			Vec3 direction = offset.normalize();
			Vec3 velocity = entity.getDeltaMovement();
			double approachingSpeed = velocity.dot(direction);
			if (approachingSpeed > 0.0D)
				entity.setDeltaMovement(velocity.subtract(direction.scale(approachingSpeed)));
		}
	}

	private record HitCandidate(LivingEntity mob, Vec3 location)
	{
	}
}
