/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.hook.client;

import com.arcanc.biomorphosis.content.ability.AbilityCastAnimations;
import com.arcanc.biomorphosis.content.ability.client.AbilityCastClientHandler;
import com.arcanc.biomorphosis.content.ability.hook.HookAbility;
import com.arcanc.biomorphosis.content.ability.hook.HookGeometry;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.content.player.animation.PPlayerAnimationBlendMode;
import com.arcanc.pulselib.content.player.animation.PPlayerAnimationDefinition;
import com.arcanc.pulselib.content.player.animation.PPlayerPart;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class HookClientHandler
{
	private static final List<ClientHook> HOOKS = new ArrayList<>();
	private static final String CAST_CONTROLLER = "hook_controller";
	private static final PModelData CAST_MODEL = new PModelData.Builder(
			Database.rl("glmodels/player/abilities/hook.gltf"), "").build();
	private static final PRawAnimation CAST_ANIMATION = PRawAnimation.begin().thenPlay("extend").build();
	private static final RenderType TETHER_RENDER_TYPE = RenderType.create("biomorphosis_hook_tether",
			DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 4096, false, true,
			RenderType.CompositeState.builder().
					setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer :: getPositionColorShader)).
					setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).
					setCullState(RenderType.NO_CULL).
					setDepthTestState(RenderType.LEQUAL_DEPTH_TEST).
					setWriteMaskState(RenderType.COLOR_WRITE).
					createCompositeState(false));
	
	private static final float GET_OVER_HERE_SOUND_CHANCE = 0.05f;

	private HookClientHandler()
	{
	}

	public static void init(final IEventBus modEventBus)
	{
		NeoForge.EVENT_BUS.addListener(HookClientHandler :: playerTick);
		NeoForge.EVENT_BUS.addListener(HookClientHandler :: renderHooks);
		modEventBus.addListener(HookClientHandler :: registerAnimation);
	}

	public static void spawn(UUID ownerId, Vec3 origin, Vec3 direction, double speedPerTick, double range)
	{
		ClientHook hook = new ClientHook(ownerId, origin, direction, speedPerTick, range);
		HOOKS.add(hook);
		hook.startFlightSounds();
	}

	private static void playerTick(PlayerTickEvent.Post event)
	{
		Minecraft minecraft = RenderHelper.mc();
		if (event.getEntity() != minecraft.player)
			return;

		ClientLevel level = minecraft.level;
		if (level == null)
		{
			HOOKS.forEach(ClientHook :: stopFlightSounds);
			HOOKS.clear();
			return;
		}

		HOOKS.removeIf(clientHook -> !clientHook.tick(level));
	}

	private static void renderHooks(RenderLevelStageEvent event)
	{
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || HOOKS.isEmpty())
			return;

		PoseStack poseStack = event.getPoseStack();
		Vec3 cameraPosition = event.getCamera().getPosition();
		poseStack.pushPose();
		poseStack.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		for (ClientHook hook : HOOKS)
			hook.render(buffer, poseStack.last(), cameraPosition);
		TETHER_RENDER_TYPE.draw(buffer.buildOrThrow());
		poseStack.popPose();
	}

	public static void registerAnimation(PulseLibEvents.PlayerAnimationRegistrationEvent event)
	{
		event.registration().register(AbilityCastAnimations.HOOK,
				PPlayerAnimationDefinition.builder(CAST_MODEL).
						when(player -> AbilityCastClientHandler.isCasting(player, AbilityCastAnimations.HOOK)).
						bind(PPlayerPart.RIGHT_ARM, "right_arm").
						mask(PPlayerPart.RIGHT_ARM).
						blendMode(PPlayerAnimationBlendMode.OVERRIDE).
						controllers(registrar -> registrar.add(CAST_CONTROLLER, () -> state ->
						{
							if (!AbilityCastClientHandler.isCasting(state.animatable().player(), AbilityCastAnimations.HOOK))
								return ControllerState.STOP;
							if (state.controller().isStopped())
								state.controller().play(CAST_ANIMATION);
							return ControllerState.PLAY;
						})).
						build());
	}

	private static final class ClientHook
	{
		private static final int SECOND_FLY_DELAY_TICKS = 6;
		private static final double HIT_RADIUS = 0.20D;
		private final UUID ownerId;
		private final Vec3 origin;
		private final Vec3 direction;
		private final double speedPerTick;
		private final double range;
		private Vec3 position;
		private Vec3 previousPosition;
		private double traveled;
		private UUID targetMobId;
		private int tetherTicks;
		private final HookFlySound primaryFlySound;
		private final HookFlySound secondaryFlySound;
		private boolean flightSoundsStopped;

		private ClientHook(UUID ownerId, Vec3 position, Vec3 direction, double speedPerTick, double range)
		{
			this.ownerId = ownerId;
			this.origin = position;
			this.position = position;
			this.previousPosition = position;
			this.direction = direction;
			this.speedPerTick = speedPerTick;
			this.range = range;
			ClientLevel level = RenderHelper.mc().level;
			this.primaryFlySound = new HookFlySound(this, level, 0);
			this.secondaryFlySound = new HookFlySound(this, level, SECOND_FLY_DELAY_TICKS);
		}

		private void startFlightSounds()
		{
			RenderHelper.mc().getSoundManager().play(this.primaryFlySound);
			RenderHelper.mc().getSoundManager().play(this.secondaryFlySound);
		}

		private void stopFlightSounds()
		{
			if (this.flightSoundsStopped)
				return;
			this.primaryFlySound.stopFlight();
			this.secondaryFlySound.stopFlight();
			this.flightSoundsStopped = true;
		}

		private boolean tick(ClientLevel level)
		{
			if (this.tetherTicks > 0)
			{
				this.previousPosition = this.position;
				LivingEntity target = this.findTarget(level);
				if (target != null)
					this.position = target.getBoundingBox().getCenter();
				boolean isAttached = --this.tetherTicks > 0;
				if (!isAttached)
					this.stopFlightSounds();
				return isAttached;
			}

			double step = Math.min(this.speedPerTick, this.range - this.traveled);
			if (step <= 0.0D)
			{
				this.stopFlightSounds();
				return false;
			}

			double nextTraveled = this.traveled + step;
			Vec3 nextPosition = HookGeometry.spiralPosition(this.origin, this.direction, nextTraveled);
			BlockHitResult blockHit = level.clip(new ClipContext(this.position, nextPosition,
					ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
			HitCandidate mobHit = this.findClosestMob(level, this.position, nextPosition);
			double blockDistance = blockHit.getType() == HitResult.Type.BLOCK ?
					blockHit.getLocation().distanceToSqr(this.position) : Double.MAX_VALUE;
			double mobDistance = mobHit == null ? Double.MAX_VALUE : mobHit.location().distanceToSqr(this.position);

			this.previousPosition = this.position;
			if (mobDistance < blockDistance)
			{
				this.position = mobHit.location();
				this.targetMobId = mobHit.mob().getUUID();
				this.tetherTicks = HookAbility.PULL_TICKS;
				if (level.random.nextFloat() < GET_OVER_HERE_SOUND_CHANCE)
					level.playLocalSound(this.position.x, this.position.y, this.position.z,
							Registration.SoundReg.HOOK_GET_OVER_HERE.get(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
				return true;
			}
			if (blockDistance != Double.MAX_VALUE)
			{
				this.position = blockHit.getLocation();
				this.tetherTicks = HookAbility.PULL_TICKS;
				return true;
			}

			this.position = nextPosition;
			this.traveled = nextTraveled;
			boolean isFlying = this.traveled < this.range;
			if (!isFlying)
				this.stopFlightSounds();
			return isFlying;
		}

		private HitCandidate findClosestMob(ClientLevel level, Vec3 start, Vec3 end)
		{
			HitCandidate closest = null;
			for (LivingEntity mob : level.getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(HIT_RADIUS),
					mob -> mob.isAlive() && mob.isPickable() && !mob.getUUID().equals(this.ownerId)))
			{
				Vec3 hit = mob.getBoundingBox().inflate(HIT_RADIUS).clip(start, end).orElse(null);
				if (hit == null || closest != null && hit.distanceToSqr(start) >= closest.location().distanceToSqr(start))
					continue;
				closest = new HitCandidate(mob, hit);
			}
			return closest;
		}

		private LivingEntity findTarget(ClientLevel level)
		{
			if (this.targetMobId == null)
				return null;
			return level.getEntitiesOfClass(LivingEntity.class, new AABB(this.position, this.position).inflate(this.range),
					mob -> mob.getUUID().equals(this.targetMobId)).stream().findFirst().orElse(null);
		}

		private void render(VertexConsumer consumer, PoseStack.Pose pose, Vec3 cameraPosition)
		{
			Player owner = RenderHelper.mc().level == null ? null : RenderHelper.mc().level.getPlayerByUUID(this.ownerId);
			Vec3 hand = owner == null ? this.origin : HookGeometry.handOrigin(owner, this.direction);
			Vec3 tip = this.previousPosition;
			if (this.tetherTicks > 0)
			{
				addRibbon(consumer, pose, cameraPosition, hand, tip, 0.115D, 0.66F, 0.14F, 0.01F, 0.82F);
				addRibbon(consumer, pose, cameraPosition, hand, tip, 0.045D, 1.0F, 0.48F, 0.05F, 1.0F);
			}
			else
			{
				addSpiralTether(consumer, pose, cameraPosition, hand);
			}
			addHookHead(consumer, pose, cameraPosition, tip);
		}

		private void addSpiralTether(VertexConsumer consumer, PoseStack.Pose pose, Vec3 cameraPosition, Vec3 hand)
		{
			final int segments = Math.max(4, (int)Math.ceil(this.traveled * 2.0D));
			Vec3 previous = hand;
			for (int index = 1; index <= segments; index++)
			{
				double distance = this.traveled * index / segments;
				Vec3 point = HookGeometry.spiralPosition(this.origin, this.direction, distance);
				addRibbon(consumer, pose, cameraPosition, previous, point, 0.115D, 0.66F, 0.14F, 0.01F, 0.82F);
				addRibbon(consumer, pose, cameraPosition, previous, point, 0.045D, 1.0F, 0.48F, 0.05F, 1.0F);
				previous = point;
			}
		}

		private void addHookHead(VertexConsumer consumer, PoseStack.Pose pose, Vec3 cameraPosition, Vec3 tip)
		{
			Vec3 right = this.direction.cross(new Vec3(0.0D, 1.0D, 0.0D));
			if (right.lengthSqr() < 0.001D)
				right = new Vec3(1.0D, 0.0D, 0.0D);
			right = right.normalize().scale(0.30D);
			Vec3 up = right.cross(this.direction).normalize().scale(0.30D);
			Vec3 stem = tip.subtract(this.direction.scale(0.46D));
			addHookProng(consumer, pose, cameraPosition, tip, stem.add(right));
			addHookProng(consumer, pose, cameraPosition, tip, stem.subtract(right));
			addHookProng(consumer, pose, cameraPosition, tip, stem.add(up));
		}

		private static void addHookProng(VertexConsumer consumer, PoseStack.Pose pose, Vec3 cameraPosition, Vec3 start, Vec3 end)
		{
			addRibbon(consumer, pose, cameraPosition, start, end, 0.105D, 0.78F, 0.20F, 0.01F, 0.96F);
			addRibbon(consumer, pose, cameraPosition, start, end, 0.042D, 1.0F, 0.68F, 0.16F, 1.0F);
		}

		private static void addRibbon(VertexConsumer consumer, PoseStack.Pose pose, Vec3 cameraPosition, Vec3 start, Vec3 end,
				double halfWidth, float red, float green, float blue, float alpha)
		{
			Vec3 direction = end.subtract(start);
			if (direction.lengthSqr() < 0.0001D)
				return;

			Vec3 midpoint = start.add(end).scale(0.5D);
			Vec3 side = direction.cross(cameraPosition.subtract(midpoint));
			if (side.lengthSqr() < 0.0001D)
				side = direction.cross(new Vec3(0.0D, 1.0D, 0.0D));
			if (side.lengthSqr() < 0.0001D)
				side = new Vec3(1.0D, 0.0D, 0.0D);
			side = side.normalize().scale(halfWidth);

			Vec3 startLeft = start.subtract(side);
			Vec3 startRight = start.add(side);
			Vec3 endLeft = end.subtract(side);
			Vec3 endRight = end.add(side);
			addVertex(consumer, pose, startLeft, red, green, blue, alpha);
			addVertex(consumer, pose, endLeft, red, green, blue, alpha);
			addVertex(consumer, pose, endRight, red, green, blue, alpha);
			addVertex(consumer, pose, startLeft, red, green, blue, alpha);
			addVertex(consumer, pose, endRight, red, green, blue, alpha);
			addVertex(consumer, pose, startRight, red, green, blue, alpha);
		}

		private static void addVertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 position,
				float red, float green, float blue, float alpha)
		{
			consumer.addVertex(pose, (float)position.x, (float)position.y, (float)position.z).setColor(red, green, blue, alpha);
		}

		private static final class HookFlySound extends AbstractTickableSoundInstance
		{
			private final ClientHook hook;

			private HookFlySound(ClientHook hook, ClientLevel level, int delay)
			{
				super(Registration.SoundReg.HOOK_FLY.get(), SoundSource.PLAYERS, level.random);
				this.hook = hook;
				this.looping = true;
				this.delay = delay;
				this.volume = 0.75F;
				this.pitch = 1.0F;
				this.x = hook.position.x;
				this.y = hook.position.y;
				this.z = hook.position.z;
			}

			@Override
			public void tick()
			{
				this.x = this.hook.position.x;
				this.y = this.hook.position.y;
				this.z = this.hook.position.z;
			}

			private void stopFlight()
			{
				this.stop();
			}
		}
	}

	private record HitCandidate(LivingEntity mob, Vec3 location)
	{
	}
}
