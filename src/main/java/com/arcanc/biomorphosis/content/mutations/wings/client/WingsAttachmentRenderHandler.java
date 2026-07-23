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
import com.arcanc.biomorphosis.content.mutations.wings.client.animation.WingAnimator;
import com.arcanc.biomorphosis.content.mutations.wings.client.model.AvianWingModel;
import com.arcanc.biomorphosis.util.Database;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Wings Model was taken from {@link <a href="https://github.com/Fuzss/fantastic-wings">Fantastic Wings</a>}
 * <p>
 * Permission was obtained directly from @Fuzs
 */
public final class WingsAttachmentRenderHandler
{
	public static final ResourceLocation LAYER = Database.rl("wings");
	private static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(LAYER, "main");
	private static final ResourceLocation TEXTURE = Database.rl("textures/entity/wings/organic.png");
	private static final Map<Player, AnimationInstance> ANIMATIONS = new WeakHashMap<>();
	private static final float HOVER_ANIMATION_SPEED = 0.7F;

	private WingsAttachmentRenderHandler() { }

	public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(MODEL_LAYER, AvianWingModel :: createLayer);
	}

	public static void addLayers(EntityRenderersEvent.AddLayers event)
	{
		for (PlayerSkin.Model skin : event.getSkins())
		{
			PlayerRenderer renderer = event.getSkin(skin);
			if (renderer != null)
				renderer.addLayer(new WingsLayer(renderer, new AvianWingModel(event.getEntityModels().bakeLayer(MODEL_LAYER))));
		}
	}

	private static final class WingsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
	{
		private final AvianWingModel model;

		private WingsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, AvianWingModel model)
		{
			super(parent);
			this.model = model;
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource buffers, int light, AbstractClientPlayer player,
			                   float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
			                   float netHeadYaw, float headPitch)
		{
			if (player.isInvisible() || !WingsEffectType.hasWings(player))
				return;
			AnimationInstance animation = ANIMATIONS.computeIfAbsent(player, ignored -> new AnimationInstance());
			animation.update(player);
			poseStack.pushPose();
			poseStack.translate(0.0D, -0.0625D, 0.0D);
			this.getParentModel().body.translateAndRotate(poseStack);
			VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
			this.model.render(animation.animator, partialTick, poseStack, consumer, light, OverlayTexture.NO_OVERLAY, -1);
			poseStack.popPose();
		}
	}

	private static final class AnimationInstance
	{
		private final WingAnimator animator = new WingAnimator();
		private WingState state;
		private int lastTick = Integer.MIN_VALUE;

		private void update(Player player)
		{
			if (this.lastTick == player.tickCount)
				return;
			this.lastTick = player.tickCount;
			WingState next = WingState.forPlayer(player);
			if (next != this.state)
			{
				this.state = next;
				switch (next)
				{
					case IDLE -> this.animator.beginIdle();
					case GLIDE -> this.animator.beginGlide();
					case LIFT -> this.animator.beginLift();
					case FALL -> this.animator.beginFall();
				}
			}
			this.animator.update(WingsEffectType.isHovering(player) ? HOVER_ANIMATION_SPEED : 1.0F);
		}
	}

	private enum WingState
	{
		IDLE, GLIDE, LIFT, FALL;

		private static WingState forPlayer(Player player)
		{
			if (WingsEffectType.isFlying(player) && !player.isInWaterOrBubble())
			{
				Vec3 velocity = player.getDeltaMovement();
				if (WingsEffectType.isHovering(player) || velocity.y > 0.05D || player.getXRot() < -20.0F)
					return LIFT;
				return GLIDE;
			}
			return !player.onGround() && player.getDeltaMovement().y < -0.05D ? FALL : IDLE;
		}
	}
}
