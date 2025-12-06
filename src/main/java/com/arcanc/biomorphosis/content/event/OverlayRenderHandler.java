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
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
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

import java.util.List;

public class OverlayRenderHandler
{
	public static final ResourceLocation ITEMS = Database.rl("items");
	public static final ResourceLocation BLOCKS = Database.rl("blocks");
	
	public static void registerGuiLayers(@NotNull RegisterGuiLayersEvent event)
	{
		event.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY,
				ITEMS,
				OverlayRenderHandler :: renderItemOverlays);
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
					renderWrenchOverlay(guiGraphics, stack, player, scaledWidth, scaledHeight);
			}
	}
	
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
				
				guiGraphics.drawWordWrap(mc.font, str, scaledWidth / 4, scaledHeight - textHeight - 20 - leftHeight(), scaledWidth / 2, -1, false);
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
				guiGraphics.drawWordWrap(mc.font, str, scaledWidth / 4, scaledHeight - textHeight - 20 - leftHeight(), scaledWidth / 2, -1, false);
				
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
