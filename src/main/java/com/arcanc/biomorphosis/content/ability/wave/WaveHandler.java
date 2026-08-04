/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.wave;

import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CWave;
import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class WaveHandler
{
	private static final double WAVE_THICKNESS = 0.20;
	private static final double KNOCKBACK = 1.6;
	private static final float DAMAGE = 4.0f;
	private static final Map<ServerLevel, List<Wave>> WAVES = new HashMap<>();

	private WaveHandler()
	{
	}

	public static void register()
	{
		NeoForge.EVENT_BUS.addListener(WaveHandler :: serverTick);
	}

	public static void spawn(ServerPlayer player, WaveAbility ability)
	{
		spawn(player, player.getLookAngle(), ability);
	}

	public static void spawn(LivingEntity caster, Vec3 direction, WaveAbility ability)
	{
		if (!(caster.level() instanceof ServerLevel level) || direction.lengthSqr() < 0.0001d)
			return;

		Vec3 normalizedDirection = direction.normalize();
		Vec3 origin = caster.getBoundingBox().getCenter().
				add(0.0, WaveAbility.CENTER_OFFSET_Y, 0.0).
				add(normalizedDirection.scale(0.75));
		double height = WaveAbility.HEIGHT;
		WAVES.computeIfAbsent(level, ignored -> new ArrayList<>()).add(new Wave(caster.getUUID(), origin, normalizedDirection, height, ability));
		NetworkEngine.sendToPlayerNear(level, null, origin, ability.range() + 32.0,
				new S2CWave(origin, normalizedDirection, height, ability.speedPerTick(), ability.range()));
	}

	private static void serverTick(ServerTickEvent.Post event)
	{
		Iterator<Map.Entry<ServerLevel, List<Wave>>> levels = WAVES.entrySet().iterator();
		while (levels.hasNext())
		{
			Map.Entry<ServerLevel, List<Wave>> entry = levels.next();
			List<Wave> waves = entry.getValue();
			waves.removeIf(wave -> !wave.tick(entry.getKey()));
			if (waves.isEmpty())
				levels.remove();
		}
	}

	private static final class Wave
	{
		private final UUID ownerId;
		private final Vec3 direction;
		private final Vec3 right;
		private final Vec3 up;
		private final double height;
		private final WaveAbility ability;
		private final Set<UUID> damagedEntities = new HashSet<>();
		private Vec3 position;
		private double traveled;

		private Wave(UUID ownerId, Vec3 position, Vec3 direction, double height, WaveAbility ability)
		{
			this.ownerId = ownerId;
			this.position = position;
			this.direction = direction;
			this.right = WaveGeometry.right(direction);
			this.up = WaveGeometry.up(direction);
			this.height = height;
			this.ability = ability;
		}

		private boolean tick(ServerLevel level)
		{
			LivingEntity owner = level.getEntity(this.ownerId) instanceof LivingEntity entity ? entity : null;
			if (owner == null || owner.level() != level || owner.isDeadOrDying())
				return false;

			double stepLength = Math.min(this.ability.speedPerTick(), this.ability.range() - this.traveled);
			if (stepLength <= 0.0)
				return false;

			Vec3 nextPosition = this.position.add(this.direction.scale(stepLength));
			this.knockBackEntities(level, owner, stepLength);
			this.position = nextPosition;
			this.traveled += stepLength;
			return this.traveled < this.ability.range();
		}

		private void knockBackEntities(ServerLevel level, LivingEntity owner, double stepLength)
		{
			double boundRadius = WAVE_THICKNESS + WaveAbility.ARC_DEPTH + WaveAbility.HALF_WIDTH + this.height / 2.0;
			Vec3 nextPosition = this.position.add(this.direction.scale(stepLength));
			AABB hitBox = new AABB(this.position, nextPosition).inflate(boundRadius);
			for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, hitBox,
					entity -> entity.isAlive() && !entity.getUUID().equals(owner.getUUID()) &&
							(!SwarmTargeting.isSwarmMember(owner) || !SwarmTargeting.isSwarmMember(entity))))
			{
				if (!this.isInsideWavePath(entity.getBoundingBox().getCenter(), stepLength))
					continue;
				entity.push(this.direction.x * KNOCKBACK, this.direction.y * KNOCKBACK + 0.25, this.direction.z * KNOCKBACK);
				entity.hurtMarked = true;
				if (this.damagedEntities.add(entity.getUUID()))
					entity.hurt(owner instanceof Player player ? owner.damageSources().playerAttack(player) :
							owner.damageSources().mobAttack(owner), DAMAGE);
			}
		}

		private boolean isInsideWavePath(Vec3 point, double stepLength)
		{
			Vec3 offset = point.subtract(this.position);
			double lateral = offset.dot(this.right) / WaveAbility.HALF_WIDTH;
			double vertical = offset.dot(this.up) / (this.height / 2.0);
			if (lateral * lateral + vertical * vertical > 1.0)
				return false;

			double relativeDepth = offset.dot(this.direction) - WaveGeometry.forwardArcOffset(lateral);
			return relativeDepth >= -WAVE_THICKNESS && relativeDepth <= stepLength + WAVE_THICKNESS;
		}
	}
}
