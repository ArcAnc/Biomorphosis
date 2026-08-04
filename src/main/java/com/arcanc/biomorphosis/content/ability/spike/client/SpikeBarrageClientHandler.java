/**
 * @author ArcAnc
 * Created at: 02.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.spike.client;

import com.arcanc.biomorphosis.content.ability.AbilityCastAnimations;
import com.arcanc.biomorphosis.content.ability.client.AbilityCastClientHandler;
import com.arcanc.biomorphosis.content.ability.spike.SpikeGeometry;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.content.player.animation.PPlayerAnimationBlendMode;
import com.arcanc.pulselib.content.player.animation.PPlayerAnimationDefinition;
import com.arcanc.pulselib.content.player.animation.PPlayerPart;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SpikeBarrageClientHandler
{
	private static final List<ClientSpike> SPIKES = new ArrayList<>();
	private static final Map<UUID, Long> HANDS_FORWARD_UNTIL = new HashMap<>();
	private static final String CAST_CONTROLLER = "spike_barrage_controller";
	private static final PModelData CAST_MODEL = new PModelData.Builder(
			Database.rl("glmodels/player/abilities/spike_barrage.gltf"), "").build();
	private static final PRawAnimation CAST_ANIMATION = PRawAnimation.begin().thenPlay("hands_forward").build();

	private SpikeBarrageClientHandler()
	{
	}

	public static void init(IEventBus modEventBus)
	{
		NeoForge.EVENT_BUS.addListener(SpikeBarrageClientHandler :: clientTick);
		NeoForge.EVENT_BUS.addListener(SpikeBarrageClientHandler :: renderSpikes);
		modEventBus.addListener(SpikeBarrageClientHandler :: registerAnimation);
	}

	public static void spawn(Vec3 origin, Vec3 targetOffset, double lateralCurve, double aimLift, double speedPerTick,
			double range)
	{
		if (targetOffset.lengthSqr() < 0.0001d)
			return;
		SPIKES.add(new ClientSpike(origin, targetOffset.normalize(), lateralCurve, aimLift, targetOffset.length(), speedPerTick, range));
	}

	public static void impact(Vec3 origin, double lateralCurve, Vec3 impactPosition)
	{
		ClientSpike closest = null;
		for (ClientSpike spike : SPIKES)
		{
			if (spike.origin.distanceToSqr(origin) > 0.0001D || Double.compare(spike.lateralCurve, lateralCurve) != 0)
				continue;
			if (closest == null || spike.position.distanceToSqr(impactPosition) < closest.position.distanceToSqr(impactPosition))
				closest = spike;
		}
		if (closest != null)
			SPIKES.remove(closest);
	}

	public static void keepHandsForward(UUID playerId, int durationTicks)
	{
		ClientLevel level = RenderHelper.mc().level;
		if (level != null)
			HANDS_FORWARD_UNTIL.put(playerId, level.getGameTime() + durationTicks);
	}

	public static void registerAnimation(PulseLibEvents.PlayerAnimationRegistrationEvent event)
	{
		event.registration().register(AbilityCastAnimations.SPIKE_BARRAGE,
				PPlayerAnimationDefinition.builder(CAST_MODEL).
						when(SpikeBarrageClientHandler :: isHandsForward).
						bind(PPlayerPart.LEFT_ARM, "left_arm").
						bind(PPlayerPart.RIGHT_ARM, "right_arm").
						mask(PPlayerPart.LEFT_ARM, PPlayerPart.RIGHT_ARM).
						blendMode(PPlayerAnimationBlendMode.REPLACE).
						controllers(registrar -> registrar.add(CAST_CONTROLLER, () -> state ->
						{
							if (!isHandsForward(state.animatable().player()))
								return ControllerState.STOP;
							if (state.controller().isStopped())
								state.controller().play(CAST_ANIMATION);
							return ControllerState.PLAY;
						})).
						build());
	}

	private static void clientTick(ClientTickEvent.Post event)
	{
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null)
		{
			SPIKES.clear();
			HANDS_FORWARD_UNTIL.clear();
			return;
		}
		
		HANDS_FORWARD_UNTIL.values().removeIf(endTime -> endTime <= level.getGameTime());
		SPIKES.removeIf(clientSpike -> ! clientSpike.tick(level));
	}

	private static boolean isHandsForward(net.minecraft.world.entity.player.Player player)
	{
		if (AbilityCastClientHandler.isCasting(player, AbilityCastAnimations.SPIKE_BARRAGE))
			return true;
		return HANDS_FORWARD_UNTIL.getOrDefault(player.getUUID(), 0L) > player.level().getGameTime();
	}

	private static void renderSpikes(RenderLevelStageEvent event)
	{
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || SPIKES.isEmpty())
			return;

		PoseStack poseStack = event.getPoseStack();
		Vec3 cameraPosition = event.getCamera().getPosition();
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		poseStack.pushPose();
		poseStack.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		for (ClientSpike spike : SPIKES)
			spike.render(buffer, poseStack.last(), partialTick);
		RenderType.lightning().draw(buffer.buildOrThrow());
		poseStack.popPose();
	}

	private static final class ClientSpike
	{
		private static final Vector3f BROWN = new Vector3f(0.30f, 0.13f, 0.035f);
		private static final Vector3f DARK_BROWN = new Vector3f(0.16f, 0.065f, 0.015f);
		private static final Vector3f GREEN = new Vector3f(0.38f, 0.72f, 0.12f);
		private static final Vector3f DARK_GREEN = new Vector3f(0.12f, 0.34f, 0.035f);

		private final Vec3 origin;
		private final Vec3 direction;
		private final Vec3 right;
		private final Vec3 up;
		private final double lateralCurve;
		private final double aimLift;
		private final double aimDistance;
		private final double speedPerTick;
		private final double range;
		private Vec3 position;
		private Vec3 previousPosition;
		private double traveled;
		private int age;

		private ClientSpike(Vec3 origin, Vec3 direction, double lateralCurve, double aimLift, double aimDistance,
				double speedPerTick, double range)
		{
			this.origin = origin;
			this.direction = direction;
			this.right = SpikeGeometry.right(direction);
			this.up = SpikeGeometry.up(direction);
			this.lateralCurve = lateralCurve;
			this.aimLift = aimLift;
			this.aimDistance = aimDistance;
			this.speedPerTick = speedPerTick;
			this.range = range;
			this.position = origin;
			this.previousPosition = origin;
		}

		private boolean tick(ClientLevel level)
		{
			double step = Math.min(this.speedPerTick, this.range - this.traveled);
			if (step <= 0)
				return false;

			this.previousPosition = this.position;
			this.traveled += step;
			this.position = SpikeGeometry.position(this.origin, this.direction, this.lateralCurve, this.aimLift,
					this.traveled, this.aimDistance);
			if ((this.age++ & 1) == 0)
				this.spawnTrail(level);
			return this.traveled < this.range;
		}

		private void render(VertexConsumer consumer, PoseStack.Pose pose, float partialTick)
		{
			Vec3 tip = this.previousPosition.lerp(this.position, partialTick);
			Vec3 tail = tip.subtract(this.direction.scale(0.7d));
			Vec3 baseRight = this.right.scale(0.14d);
			Vec3 baseUp = this.up.scale(0.14d);
			Vec3 baseA = tail.add(baseRight);
			Vec3 baseB = tail.add(baseUp);
			Vec3 baseC = tail.subtract(baseRight);
			Vec3 baseD = tail.subtract(baseUp);

			addFace(consumer, pose, baseA, baseB, tip, BROWN, DARK_BROWN, GREEN);
			addFace(consumer, pose, baseB, baseC, tip, BROWN, DARK_BROWN, GREEN);
			addFace(consumer, pose, baseC, baseD, tip, DARK_BROWN, BROWN, DARK_GREEN);
			addFace(consumer, pose, baseD, baseA, tip, DARK_BROWN, BROWN, DARK_GREEN);
		}

		private void spawnTrail(ClientLevel level)
		{
			Vec3 trailPosition = this.position.subtract(this.direction.scale(0.28d));
			level.addParticle(new DustParticleOptions(BROWN, 0.65f), trailPosition.x, trailPosition.y, trailPosition.z,
					0, 0, 0);
			level.addParticle(new DustParticleOptions(GREEN, 0.42f), this.position.x, this.position.y, this.position.z,
					0, 0, 0);
		}

		private static void addFace(VertexConsumer consumer, PoseStack.Pose pose, Vec3 first, Vec3 second, Vec3 tip,
				Vector3f firstColor, Vector3f secondColor, Vector3f tipColor)
		{
			addVertex(consumer, pose, first, firstColor);
			addVertex(consumer, pose, second, secondColor);
			addVertex(consumer, pose, tip, tipColor);
		}

		private static void addVertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 point, Vector3f color)
		{
			consumer.addVertex(pose, (float)point.x, (float)point.y, (float)point.z).
					setColor(color.x(), color.y(), color.z(), 1f);
		}
	}
}
