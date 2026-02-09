/**
 * @author ArcAnc
 * Created at: 04.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.event;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioItemTags;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OverlayRenderHandler
{
	public static final ResourceLocation ADVANCEMENTS = Database.rl("advancements");
	public static final ResourceLocation ITEMS = Database.rl("items");
	public static final ResourceLocation BLOCKS = Database.rl("blocks");
	
	public static void registerGuiLayers(@NotNull RegisterGuiLayersEvent event)
	{
		event.registerAbove(VanillaGuiLayers.DEBUG_OVERLAY,
				ADVANCEMENTS,
				OverlayRenderHandler::renderAdvancementsOverlays);
		event.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY,
				ITEMS,
				OverlayRenderHandler :: renderItemOverlays);
	}
	
	private static void renderAdvancementsOverlays(GuiGraphics guiGraphics, DeltaTracker delta)
	{
		LocalPlayer player = RenderHelper.clientPlayer();
		if (player == null)
			return;
		
		int scaledWidth = RenderHelper.mc().getWindow().getGuiScaledWidth();
		int scaledHeight = RenderHelper.mc().getWindow().getGuiScaledHeight();
		
		if (AdvancementOverlays.ADVANCEMENT_INFOS.isEmpty())
			return;
		AdvancementOverlays.renderNewGeneOverlay(guiGraphics, player, scaledWidth, scaledHeight, delta.getGameTimeDeltaPartialTick(true));
	}
	
	private static void renderItemOverlays(GuiGraphics guiGraphics, DeltaTracker delta)
	{
		LocalPlayer player = RenderHelper.clientPlayer();
		if (player == null)
			return;
		
		int scaledWidth = RenderHelper.mc().getWindow().getGuiScaledWidth();
		int scaledHeight = RenderHelper.mc().getWindow().getGuiScaledHeight();
		
		for (InteractionHand hand : InteractionHand.values())
			if (!player.getItemInHand(hand).isEmpty())
			{
				ItemStack stack = player.getItemInHand(hand);
				if (stack.is(BioItemTags.WRENCH))
					ItemOverlays.renderWrenchOverlay(guiGraphics, stack, player, scaledWidth, scaledHeight);
			}
	}
	
	public static class AdvancementOverlays
	{
		private static final ResourceLocation ADVANCEMENT_BACKGROUND = Database.rl("textures/gui/overlay/advancement/background.png");
		
		private static final Vector2f ADVANCEMENT_SIZE = new Vector2f(130, 52);
		
		private static final List<AdvancementInfo<?>> ADVANCEMENT_INFOS = new ArrayList<>();
		
		private static void renderNewGeneOverlay(GuiGraphics guiGraphics, @NotNull LocalPlayer player, int scaledWidth, int scaledHeight, float partialTick)
		{
			Minecraft mc = RenderHelper.mc();
			Font font = mc.font;
			
			Vector2f anchor = ScreenAnchor.BOTTOM_MIDDLE.calculateAnchor(scaledWidth, scaledHeight);
			AdvancementInfo<?> info = ADVANCEMENT_INFOS.getFirst();
			
			info.currentTimer+= partialTick;
			if (info.currentTimer >= info.state.getTime())
			{
				info.currentTimer = 0;
				info.state = info.state.getNextState();
				
				if (info.state == null)
				{
					ADVANCEMENT_INFOS.removeFirst();
					return;
				}
			}
			
			Vector2f currentPos = calculatePos(ScreenAnchor.BOTTOM_MIDDLE, anchor, ADVANCEMENT_SIZE, info);
			
			int color = info.state.calculateColor(info);
			
			PoseStack poseStack = guiGraphics.pose();
			poseStack.pushPose();
			poseStack.translate(currentPos.x(), currentPos.y(), 0);
			
			RenderHelper.blit(
					guiGraphics,
					ADVANCEMENT_BACKGROUND,
					0, 0,
					0, 0,
					(int) ADVANCEMENT_SIZE.x(), (int)ADVANCEMENT_SIZE.y(),
					130, 52,
					256, 64,
					color);
			
			RenderHelper.blit(
					guiGraphics,
					info.object.getImageLocation(),
					17, 18,
					0, 0,
					16, 16,
					16, 16,
					16,16,
					color);
			
			poseStack.pushPose();
			poseStack.translate(37, 20, 0);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			guiGraphics.drawString(font, info.object.getName(), 0, 0, color, false);
			poseStack.popPose();
			
			poseStack.pushPose();
			poseStack.translate(37, 28, 0);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			//х2 because of scale
			guiGraphics.drawWordWrap(font, info.object.getAdditionalInfo(), 0, 0, (int)(ADVANCEMENT_SIZE.x() - 21) * 2, color);
			poseStack.popPose();
			
			poseStack.popPose();
			
			if (!info.soundPlayed)
			{
				info.soundPlayed = true;
				mc.getSoundManager().play(SimpleSoundInstance.forUI(Registration.SoundReg.ADVANCEMENT.get(), 1.0f, 0.85f));
			}
		}
		
		private static class AdvancementInfo<T extends AdvancementRenderable>
		{
			private final T object;
			private AdvancementRenderState state;
			private float currentTimer;
			private boolean soundPlayed;
			
			private AdvancementInfo(T object)
			{
				this.object = object;
				this.state = AdvancementRenderState.EMERGING;
				this.soundPlayed = false;
			}
		}
		
		public interface AdvancementRenderable
		{
			ResourceLocation getImageLocation();
			
			Component getName();
			
			Component getAdditionalInfo();
		}
		
		public static <T extends AdvancementRenderable> void addAdvancement(T object)
		{
			ADVANCEMENT_INFOS.add(new AdvancementInfo<>(object));
		}
		
		private enum AdvancementRenderState
		{
			EMERGING (10, info -> MathHelper.ColorHelper.colorFromFloat(info.currentTimer / info.state.getTime(), 1f, 1f, 1f)),
			SHOWING (5 * 20, info -> -1),
			HIDING(4 * 20, info -> MathHelper.ColorHelper.colorFromFloat(1 - (info.currentTimer / info.state.getTime()), 1f, 1f, 1f));
			
			private final int time;
			private final Function<AdvancementInfo<?>, Integer> color;
			
			AdvancementRenderState(int time, Function<AdvancementInfo<?>, Integer> color)
			{
				this.time = time;
				this.color = color;
			}
			
			private int getTime()
			{
				return time;
			}
			
			private int calculateColor(AdvancementInfo<?> info)
			{
				return this.color.apply(info);
			}
			
			private @Nullable AdvancementRenderState getNextState()
			{
				return switch (this)
				{
					case EMERGING -> SHOWING;
					case SHOWING -> HIDING;
					case HIDING -> null;
				};
			}
		}
		
		private enum ScreenAnchor
		{
			TOP_LEFT((screenWidth, screenHeight) -> new Vector2f(0, -ADVANCEMENT_SIZE.y())),
			TOP_RIGHT((screenWidth, screenHeight) -> new Vector2f(screenWidth - ADVANCEMENT_SIZE.x(), -ADVANCEMENT_SIZE.y())),
			BOTTOM_LEFT((screenWidth, screenHeight) -> new Vector2f(0, screenHeight)),
			BOTTOM_RIGHT((screenWidth, screenHeight) -> new Vector2f(screenWidth - ADVANCEMENT_SIZE.x(), screenHeight)),
			BOTTOM_MIDDLE((screenWidth, screenHeight) -> new Vector2f((screenWidth - ADVANCEMENT_SIZE.x()) / 2, screenHeight - 50));
			
			private final BiFunction<Integer, Integer, Vector2f> anchorGetter;
			
			ScreenAnchor(BiFunction<Integer, Integer, Vector2f> anchorGetter)
			{
				this.anchorGetter = anchorGetter;
			}
			
			private Vector2f calculateAnchor(int screenWidth, int screenHeight)
			{
				return this.anchorGetter.apply(screenWidth, screenHeight);
			}
		}
		
		private static @NotNull Vector2f calculatePos(ScreenAnchor anchor, Vector2f anchorPos, Vector2f size, @NotNull AdvancementInfo<?> info)
		{
			return switch (info.state)
			{
				case EMERGING, HIDING, SHOWING ->
				{
					Vector2f result = new Vector2f(anchorPos);
					if (anchor == ScreenAnchor.TOP_LEFT || anchor == ScreenAnchor.TOP_RIGHT)
						result = result.add(0, size.y());
					else
						result = result.sub(0, size.y());
					yield result;
				}
				/*case EMERGING ->
				{
					Vector2f result = new Vector2f(anchorPos);
					float percent = info.currentTimer / info.state.time;
					int offsetY = Math.round((percent) * size.y());
					if (anchor == ScreenAnchor.TOP_LEFT || anchor == ScreenAnchor.TOP_RIGHT)
						result = result.add(0, offsetY);
					else
						result = result.sub(0, offsetY);
					yield result;
				}
				case HIDING ->
				{
					Vector2f result = new Vector2f(anchorPos);
					float percent = (info.state.time - info.currentTimer) / info.state.time;
					int offsetY = Math.round((percent) * size.y());
					if (anchor == ScreenAnchor.TOP_LEFT || anchor == ScreenAnchor.TOP_RIGHT)
						result = result.add(0, offsetY);
					else
						result = result.sub(0, offsetY);
					yield result;
				}*/
			};
		}
	}
	
	private static class ItemOverlays
	{
		
		private static void renderWrenchOverlay(GuiGraphics guiGraphics, @NotNull ItemStack stack, @NotNull LocalPlayer player, int scaledWidth, int scaledHeight)
		{
			Component str;
			Minecraft mc = RenderHelper.mc();
			ClientLevel level = player.clientLevel;
			HitResult ray = RenderHelper.mc().hitResult;
			Component shift = mc.options.keyShift.getKey().getDisplayName();
			Component rmb = mc.options.keyUse.getKey().getDisplayName();
			if (!stack.has(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA))
			{
				BlockPos target;
				if (ray instanceof BlockHitResult blockRay)
					target = blockRay.getBlockPos();
				else
					return;
				if (!FluidHelper.isFluidHandler(level, target))
					return;
				else
				{
					BlockState state = level.getBlockState(target);
					Component name = state.getBlock().getName();
					str = Component.translatable(Database.GUI.Overlays.Tooltip.START_FLUID_HANDLER,
							shift.copy().
									append(" + ").
									append(rmb).
									withStyle(ChatFormatting.GOLD),
							name);
					
					int textHeight = mc.font.split(str, scaledWidth).size() * mc.font.lineHeight;
					
					guiGraphics.drawWordWrap(mc.font, str, scaledWidth / 4, scaledHeight - textHeight - 20 - leftHeight(), scaledWidth / 2, -1);
				}
			}
			else
			{
				List<BlockPos> targets = stack.get(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA).
						stream().map(BlockPos :: containing).toList();
				BlockPos target;
				if (ray instanceof BlockHitResult blockRay)
					target = blockRay.getBlockPos();
				else
					return;
				if (targets.contains(target))
					guiGraphics.drawCenteredString(mc.font,
							Component.translatable(Database.GUI.Overlays.Tooltip.WRONG_FLUID_HANDLER).
									withStyle(ChatFormatting.RED),
							scaledWidth / 2,
							scaledHeight - 20 - leftHeight(),
							-1);
				else
				{
					if (targets.get(1).equals(BlockPos.ZERO))
					{
						str = Component.translatable(Database.GUI.Overlays.Tooltip.CHOOSE_SECOND_FLUID_HANDLER,
								shift.copy().
										append(" + ").
										append(rmb).
										withStyle(ChatFormatting.GOLD));
					}
					else
					{
						str = Component.translatable(Database.GUI.Overlays.Tooltip.CHOOSE_TRANSMITTER,
								rmb.copy().withStyle(ChatFormatting.GOLD));
					}
					
					int textHeight = mc.font.split(str, scaledWidth).size() * mc.font.lineHeight;
					guiGraphics.drawWordWrap(mc.font, str, scaledWidth / 4, scaledHeight - textHeight - 20 - leftHeight(), scaledWidth / 2, -1);
					
				}
			}
		}
	}
	
	private static int leftHeight()
	{
		return RenderHelper.mc().gui.leftHeight;
	}
	
	private static int rightHeight()
	{
		return RenderHelper.mc().gui.rightHeight;
	}
}
