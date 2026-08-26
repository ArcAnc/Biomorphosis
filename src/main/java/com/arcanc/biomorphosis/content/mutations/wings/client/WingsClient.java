/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings.client;

import com.arcanc.biomorphosis.content.mutations.types.WingsEffectType;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.C2SWings;
import com.arcanc.biomorphosis.content.network.packets.C2SWingsFlightInput;
import com.arcanc.biomorphosis.mixin.client.ItemInHandRendererAccessor;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.content.player.animation.PPlayerAnimationBlendMode;
import com.arcanc.pulselib.content.player.animation.PPlayerAnimationDefinition;
import com.arcanc.pulselib.content.player.animation.PPlayerPart;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.WeakHashMap;

public final class WingsClient
{
	private static final String FLIGHT_CONTROLLER = "wings_flight_controller";
	private static final float FLIGHT_POSE_STEP = 0.12F;
	private static final float HOVER_BOB_AMPLITUDE = 0.025F;
	private static final float HOVER_BOB_SPEED = 0.12F;
	private static final PModelData FLIGHT_MODEL = new PModelData.Builder(
			Database.rl("glmodels/player/wings_flight.gltf"), "").build();
	private static final PRawAnimation FLIGHT_ANIMATION = PRawAnimation.begin().thenLoop("flight").build();
	private static final Map<Player, FlightPose> FLIGHT_POSES = new WeakHashMap<>();
	private static WingsFlightSound flightSound;
	public static final KeyMapping TOGGLE_FLIGHT = new KeyMapping(Database.HotKeys.WINGS,
			KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, Database.HotKeys.CATEGORY);

	private WingsClient() {}

	public static void init (final IEventBus modEventBus)
	{
		modEventBus.addListener(WingsClient :: registerKeyMapping);
		modEventBus.addListener(WingsClient :: registerFlightAnimation);
		NeoForge.EVENT_BUS.addListener(WingsClient :: clientPlayerTick);
		NeoForge.EVENT_BUS.addListener(WingsClient :: computeCameraAngles);
		NeoForge.EVENT_BUS.addListener(WingsClient :: renderFlyingOffHand);
		
		modEventBus.addListener(WingsAttachmentRenderHandler :: registerLayerDefinition);
		modEventBus.addListener(WingsAttachmentRenderHandler :: addLayers);
	}
	
	private static void registerKeyMapping(RegisterKeyMappingsEvent event)
	{
		event.register(TOGGLE_FLIGHT);
	}

	private static void registerFlightAnimation(PulseLibEvents.PlayerAnimationRegistrationEvent event)
	{
		event.registration().register(Database.rl("wings_flight"),
				PPlayerAnimationDefinition.builder(FLIGHT_MODEL).
						when(WingsClient :: shouldApplyFlightAnimation).
						weight(WingsClient :: getFlightPoseAmount).
						bind(PPlayerPart.LEFT_ARM, "left_arm").
						bind(PPlayerPart.RIGHT_ARM, "right_arm").
						bind(PPlayerPart.LEFT_LEG, "left_leg").
						bind(PPlayerPart.RIGHT_LEG, "right_leg").
						mask(PPlayerPart.LEFT_ARM, PPlayerPart.RIGHT_ARM, PPlayerPart.LEFT_LEG, PPlayerPart.RIGHT_LEG).
						blendMode(PPlayerAnimationBlendMode.OVERRIDE).
						priority(-100).
						controllers(registrar -> registrar.add(FLIGHT_CONTROLLER, () -> state ->
						{
							if (getFlightPoseAmount(state.animatable().player(), 1.0F) <= 0.0F)
								return ControllerState.STOP;
							if (state.controller().isStopped())
								state.controller().play(FLIGHT_ANIMATION);
							return ControllerState.PLAY;
						})).
						build());
	}

	private static boolean shouldApplyFlightAnimation(Player player)
	{
		return getFlightPoseAmount(player, 1.0F) > 0.0F &&
				(!player.isLocalPlayer() || !RenderHelper.mc().options.getCameraType().isFirstPerson());
	}

	private static void clientPlayerTick(PlayerTickEvent.Post event)
	{
		Minecraft minecraft = RenderHelper.mc();
		if (event.getEntity() != minecraft.player)
			return;
		while (TOGGLE_FLIGHT.consumeClick())
		{
			boolean flying = ! WingsEffectType.isFlying(event.getEntity());
			if (WingsEffectType.setFlying(event.getEntity(), flying))
				NetworkEngine.sendToServer(new C2SWings(flying));
		}
		WingsEffectType.tickClient(event.getEntity());
		if (WingsEffectType.isFlying(event.getEntity()))
		{
			var input = WingsEffectType.getLocalFlightInput(event.getEntity());
			NetworkEngine.sendToServer(new C2SWingsFlightInput(input.strafe(), input.forward(), input.jumping(), input.descending()));
		}
		updateFlightSound(minecraft.player);
	}

	private static void updateFlightSound(Player player)
	{
		boolean shouldPlay = WingsEffectType.isFlying(player) && !player.isInWaterOrBubble();
		if (!shouldPlay)
		{
			if (flightSound != null)
				flightSound.stopFlight();
			flightSound = null;
			return;
		}
		if (flightSound == null || flightSound.isStopped())
		{
			flightSound = new WingsFlightSound(player);
			RenderHelper.mc().getSoundManager().play(flightSound);
		}
	}

	private static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event)
	{
		Entity entity = event.getCamera().getEntity();
		if (!(entity instanceof Player player))
			return;
		float partialTick = (float) event.getPartialTick();
		float flightPoseAmount = getFlightPoseAmount(player, partialTick);
		if (flightPoseAmount <= 0.0F)
			return;
		float bodyRoll = Mth.rotLerp(partialTick, player.yBodyRotO - player.yRotO, player.yBodyRot - player.getYRot());
		event.setRoll(event.getRoll() - bodyRoll * 0.25F * flightPoseAmount);
	}

	public static void applyFlightModelRotation(AbstractClientPlayer player, PoseStack poseStack, float partialTick)
	{
		if (WingsEffectType.isHovering(player) && !player.isInWaterOrBubble())
		{
			float bobOffset = Mth.sin((player.tickCount + partialTick) * HOVER_BOB_SPEED) * HOVER_BOB_AMPLITUDE;
			poseStack.translate(0.0D, bobOffset, 0.0D);
		}

		float flightPoseAmount = getFlightPoseAmount(player, partialTick);
		if (flightPoseAmount <= 0.0F)
			return;
		float roll = Mth.rotLerp(partialTick, player.yBodyRotO - player.yRotO, player.yBodyRot - player.getYRot());
		float pitch = -90.0F - Mth.rotLerp(partialTick, player.xRotO, player.getXRot());
		poseStack.mulPose(Axis.ZP.rotationDegrees(roll * flightPoseAmount));
		poseStack.mulPose(Axis.XP.rotationDegrees(pitch * flightPoseAmount));
		poseStack.translate(0.0D, -1.2D * flightPoseAmount, 0.0D);
	}

	public static void applyFlightHeadPose(Player player, PlayerModel<?> model, float ageInTicks, float headPitch)
	{
		float flightPoseAmount = getFlightPoseAmount(player, Mth.frac(ageInTicks));
		if (flightPoseAmount <= 0.0F)
			return;

		float flightHeadPitch = (headPitch / 4.0F - 90.0F) * Mth.DEG_TO_RAD;
		model.head.xRot = Mth.lerp(flightPoseAmount, model.head.xRot, flightHeadPitch);
		model.hat.copyFrom(model.head);
	}

	private static float getFlightPoseAmount(Player player, float partialTick)
	{
		FlightPose pose = FLIGHT_POSES.computeIfAbsent(player, ignored -> new FlightPose());
		pose.update(player);
		return Mth.lerp(partialTick, pose.previousAmount, pose.amount);
	}

	private static boolean isFlightPoseTarget(Player player)
	{
		return WingsEffectType.isFlying(player) && !WingsEffectType.isHovering(player) && !player.isInWaterOrBubble();
	}

	private static final class FlightPose
	{
		private int lastTick = Integer.MIN_VALUE;
		private float previousAmount;
		private float amount;

		private void update(Player player)
		{
			if (this.lastTick == player.tickCount)
				return;
			this.lastTick = player.tickCount;
			this.previousAmount = this.amount;
			this.amount = Mth.approach(this.amount, isFlightPoseTarget(player) ? 1.0F : 0.0F, FLIGHT_POSE_STEP);
		}
	}

	private static void renderFlyingOffHand(RenderHandEvent event)
	{
		Minecraft minecraft = RenderHelper.mc();
		if (!(minecraft.player instanceof AbstractClientPlayer player) ||
				event.getHand() != InteractionHand.OFF_HAND ||
				!event.getItemStack().isEmpty() ||
				player.isScoping() || player.isInvisible() ||
				player.getMainHandItem().is(Items.FILLED_MAP) || !WingsEffectType.isFlying(player) || WingsEffectType.isHovering(player))
			return;

		ItemInHandRenderer renderer = minecraft.gameRenderer.itemInHandRenderer;
		((ItemInHandRendererAccessor) renderer).biomorphosis$renderPlayerArm(
				event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(),
				event.getEquipProgress(), event.getSwingProgress(), player.getMainArm().getOpposite());
		event.setCanceled(true);
	}
}
