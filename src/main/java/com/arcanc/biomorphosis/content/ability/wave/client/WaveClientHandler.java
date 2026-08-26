/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.wave.client;

import com.arcanc.biomorphosis.content.ability.AbilityCastAnimations;
import com.arcanc.biomorphosis.content.ability.client.AbilityCastClientHandler;
import com.arcanc.biomorphosis.content.ability.wave.WaveAbility;
import com.arcanc.biomorphosis.content.ability.wave.WaveGeometry;
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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class WaveClientHandler
{
	private static final List<ClientWave> WAVES = new ArrayList<>();
	
	private static final String CAST_CONTROLLER = "wave_controller";
	private static final PModelData CAST_MODEL = new PModelData.Builder(
			Database.rl("glmodels/player/abilities/wave.gltf"), "").build();
	private static final PRawAnimation CAST_ANIMATION = PRawAnimation.begin().thenPlay("cast").build();
	
	private WaveClientHandler()
	{
	}

	public static void init(final IEventBus modEventBus)
	{
		NeoForge.EVENT_BUS.addListener(WaveClientHandler :: playerTick);
		NeoForge.EVENT_BUS.addListener(WaveClientHandler :: renderWaves);
		modEventBus.addListener(WaveClientHandler :: registerAnimation);
		WaveRenderTypes.register(modEventBus);
	}

	public static void spawn(Vec3 origin, Vec3 direction, double height, double speedPerTick, double range)
	{
		ClientWave wave = new ClientWave(origin, direction, height, speedPerTick, range);
		WAVES.add(wave);
		wave.startSound();
	}

	private static void playerTick(PlayerTickEvent.Post event)
	{
		Minecraft minecraft = RenderHelper.mc();
		if (event.getEntity() != minecraft.player)
			return;

		ClientLevel level = minecraft.level;
		if (level == null)
		{
			WAVES.forEach(ClientWave :: stopSound);
			WAVES.clear();
			return;
		}
		
		Iterator<ClientWave> iterator = WAVES.iterator();
		while (iterator.hasNext())
		{
			ClientWave wave = iterator.next();
			if (wave.tick(level))
				continue;

			wave.stopSound();
			iterator.remove();
		}
	}

	private static void renderWaves(RenderLevelStageEvent event)
	{
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || WAVES.isEmpty())
			return;

		PoseStack poseStack = event.getPoseStack();
		Vec3 cameraPosition = event.getCamera().getPosition();
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		poseStack.pushPose();
		poseStack.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
		for (ClientWave wave : WAVES)
			wave.render(buffer, poseStack.last(), partialTick);
		RenderType renderType = WaveRenderTypes.wave();
		renderType.draw(buffer.buildOrThrow());
		poseStack.popPose();
	}
	
	public static void registerAnimation(PulseLibEvents.PlayerAnimationRegistrationEvent event)
	{
		event.registration().register(AbilityCastAnimations.WAVE,
				PPlayerAnimationDefinition.builder(CAST_MODEL).
						when(player -> AbilityCastClientHandler.isCasting(player, AbilityCastAnimations.WAVE)).
						bind(PPlayerPart.LEFT_ARM, "left_arm").
						bind(PPlayerPart.RIGHT_ARM, "right_arm").
						mask(PPlayerPart.LEFT_ARM, PPlayerPart.RIGHT_ARM).
						blendMode(PPlayerAnimationBlendMode.OVERRIDE).
						controllers(registrar -> registrar.
						add(CAST_CONTROLLER, () -> state ->
						{
							if (!AbilityCastClientHandler.isCasting(state.animatable().player(), AbilityCastAnimations.WAVE))
								return ControllerState.STOP;
					
							if (state.controller().isStopped())
								state.controller().play(CAST_ANIMATION);
					
							return ControllerState.PLAY;
						})).
						build());
	}

	private static final class ClientWave
	{
		private final Vec3 direction;
		private final Vec3 right;
		private final Vec3 up;
		private final double height;
		private final double speedPerTick;
		private final double range;
		private Vec3 position;
		private Vec3 previousPosition;
		private double traveled;
		private int age;
		private final WaveTravelSound sound;

		private ClientWave(Vec3 position, Vec3 direction, double height, double speedPerTick, double range)
		{
			this.position = position;
			this.previousPosition = position;
			this.direction = direction;
			this.right = WaveGeometry.right(direction);
			this.up = WaveGeometry.up(direction);
			this.height = height;
			this.speedPerTick = speedPerTick;
			this.range = range;
			Minecraft mc = RenderHelper.mc();
			ClientLevel level = mc.level;
			this.sound = new WaveTravelSound(this, level);
		}

		private void startSound()
		{
			RenderHelper.mc().getSoundManager().play(this.sound);
		}

		private void stopSound()
		{
			this.sound.stopTravel();
		}

		private boolean tick(ClientLevel level)
		{
			double step = Math.min(this.speedPerTick, this.range - this.traveled);
			if (step <= 0.0)
				return false;

			this.previousPosition = this.position;
			this.position = this.position.add(this.direction.scale(step));
			this.traveled += step;
			this.spawnParticles(level);
			this.age++;
			return this.traveled < this.range;
		}

		private void render(VertexConsumer consumer, PoseStack.Pose pose, float partialTick)
		{
			final int horizontalSegments = 28;
			final int verticalSegments = 20;
			for (int horizontalIndex = 0; horizontalIndex < horizontalSegments; horizontalIndex++)
			{
				float u0 = (float)horizontalIndex / horizontalSegments;
				float u1 = (float)(horizontalIndex + 1) / horizontalSegments;
				for (int verticalIndex = 0; verticalIndex < verticalSegments; verticalIndex++)
				{
					float v0 = (float)verticalIndex / verticalSegments;
					float v1 = (float)(verticalIndex + 1) / verticalSegments;
					addSurfaceQuad(consumer, pose, partialTick, u0, v0, u1, v1, false);
					addSurfaceQuad(consumer, pose, partialTick, u0, v0, u1, v1, true);

					if (horizontalIndex == 0)
						addEdgeQuad(consumer, pose, partialTick, u0, v0, u0, v1);
					if (horizontalIndex == horizontalSegments - 1)
						addEdgeQuad(consumer, pose, partialTick, u1, v1, u1, v0);
					if (verticalIndex == 0)
						addEdgeQuad(consumer, pose, partialTick, u1, v0, u0, v0);
					if (verticalIndex == verticalSegments - 1)
						addEdgeQuad(consumer, pose, partialTick, u0, v1, u1, v1);
				}
			}
		}

		private void addSurfaceQuad(VertexConsumer consumer, PoseStack.Pose pose, float partialTick, float u0, float v0, float u1, float v1, boolean back)
		{
			float depthSign = back ? -0.5F : 0.5F;
			if (back)
			{
				addVertex(consumer, pose, partialTick, u0, v0, depthSign);
				addVertex(consumer, pose, partialTick, u1, v1, depthSign);
				addVertex(consumer, pose, partialTick, u0, v1, depthSign);
				addVertex(consumer, pose, partialTick, u0, v0, depthSign);
				addVertex(consumer, pose, partialTick, u1, v0, depthSign);
				addVertex(consumer, pose, partialTick, u1, v1, depthSign);
				return;
			}

			addVertex(consumer, pose, partialTick, u0, v0, depthSign);
			addVertex(consumer, pose, partialTick, u0, v1, depthSign);
			addVertex(consumer, pose, partialTick, u1, v1, depthSign);
			addVertex(consumer, pose, partialTick, u0, v0, depthSign);
			addVertex(consumer, pose, partialTick, u1, v1, depthSign);
			addVertex(consumer, pose, partialTick, u1, v0, depthSign);
		}

		private void addEdgeQuad(VertexConsumer consumer, PoseStack.Pose pose, float partialTick, float u0, float v0, float u1, float v1)
		{
			addVertex(consumer, pose, partialTick, u0, v0, 0.5F);
			addVertex(consumer, pose, partialTick, u1, v1, 0.5F);
			addVertex(consumer, pose, partialTick, u1, v1, -0.5F);
			addVertex(consumer, pose, partialTick, u0, v0, 0.5F);
			addVertex(consumer, pose, partialTick, u1, v1, -0.5F);
			addVertex(consumer, pose, partialTick, u0, v0, -0.5F);
		}

		private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float partialTick, float u, float v, float depthSign)
		{
			Vec3 center = this.previousPosition.lerp(this.position, partialTick);
			double time = this.age + partialTick;
			double horizontal = u * 2.0 - 1.0;
			double heightFactor = WaveGeometry.heightFactor(horizontal);
			double vertical = (v - 0.5) * this.height * heightFactor;
			double forwardCurve = WaveGeometry.forwardArcOffset(horizontal);
			double ripple = Math.sin((u * 2.0 + v * 3.0) * Math.PI + time * 0.72) * 0.06 * heightFactor;
			double sway = Math.sin(v * Math.PI * 2.0 + time * 0.48) * 0.04 * heightFactor;
			double thickness = 0.12 + heightFactor * 0.10;
			Vec3 vertex = center.
					add(this.right.scale(horizontal * WaveAbility.HALF_WIDTH + sway)).
					add(this.up.scale(vertical)).
					add(this.direction.scale(0.10 + forwardCurve + ripple + thickness * depthSign));
			consumer.addVertex(pose, (float)vertex.x, (float)vertex.y, (float)vertex.z).
					setColor(1.0F, 1.0F, 1.0F, 0.60F).
					setUv(u, v);
		}

		private void spawnParticles(ClientLevel level)
		{
			float hue = Mth.frac(this.age * 0.08F);
			float red = Mth.lerp(hue, 0.46F, 0.94F);
			float green = Mth.lerp(hue, 0.08F, 0.28F);
			float blue = Mth.lerp(hue, 0.94F, 1.0F);
			DustParticleOptions particle = new DustParticleOptions(new Vector3f(red, green, blue), 1.45F);
			for (int horizontalIndex = -4; horizontalIndex <= 4; horizontalIndex++)
			{
				double horizontal = horizontalIndex / 4.0;
				double heightFactor = WaveGeometry.heightFactor(horizontal);
				for (int verticalIndex = 0; verticalIndex < 2; verticalIndex++)
				{
					double verticalOffset = (verticalIndex - 0.5) * this.height * heightFactor;
					double ripple = Math.sin((this.age + horizontalIndex + verticalIndex) * 0.45) * 0.08;
					Vec3 particlePosition = this.position.
									add(this.right.scale(horizontal * WaveAbility.HALF_WIDTH)).
									add(this.up.scale(verticalOffset)).
									add(this.direction.scale(WaveGeometry.forwardArcOffset(horizontal) + ripple));
					level.addParticle(particle, particlePosition.x, particlePosition.y, particlePosition.z, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	private static final class WaveTravelSound extends AbstractTickableSoundInstance
	{
		private final ClientWave wave;

		private WaveTravelSound(ClientWave wave, ClientLevel level)
		{
			super(Registration.SoundReg.WAVE_TRAVEL.get(), SoundSource.PLAYERS, level.random);
			this.wave = wave;
			this.looping = true;
			this.delay = 0;
			this.volume = 2.0f;
			this.pitch = 1.0f;
			this.x = wave.position.x;
			this.y = wave.position.y;
			this.z = wave.position.z;
		}

		@Override
		public void tick()
		{
			this.x = this.wave.position.x;
			this.y = this.wave.position.y;
			this.z = this.wave.position.z;
		}

		private void stopTravel()
		{
			this.stop();
		}
	}
}
